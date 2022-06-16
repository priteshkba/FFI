package com.ffi.category

import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.ffi.App
import com.ffi.HomeActivity
import com.ffi.R
import com.ffi.Utils.*
import com.ffi.productlist.ProductListFragment
import com.ffi.api.ApiClient
import com.ffi.api.WebService
import io.realm.Realm
import io.realm.RealmResults
import io.realm.Sort
import kotlinx.android.synthetic.main.custom_toolbar.*
import kotlinx.android.synthetic.main.fragment_category.*
import kotlinx.android.synthetic.main.fragment_category.as_tvClearAll
import kotlinx.android.synthetic.main.fragment_category.edt_search
import kotlinx.android.synthetic.main.fragment_category.fi_search_data
import kotlinx.android.synthetic.main.fragment_category.ivSearch
import kotlinx.android.synthetic.main.fragment_category.liNoproduct
import kotlinx.android.synthetic.main.fragment_category.li_search
import kotlinx.android.synthetic.main.fragment_category.li_serachistory
import kotlinx.android.synthetic.main.fragment_category.rvCategory
import kotlinx.android.synthetic.main.fragment_category.rv_search
import kotlinx.android.synthetic.main.fragment_home.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CategoryFragment() : Fragment(), View.OnClickListener {

    var showTab: Boolean? = false
    var id: String? = null
    var type:String?=""
    var title:String?=""


    fun getInstance(id: String, isshowtab: Boolean, type: String, title: String): CategoryFragment {
        val mCategoryFragment = CategoryFragment()
        val args = Bundle()
        args.putString(Const.ID, id)
        args.putString("type",type)
        args.putString("title",title)
        args.putBoolean(Const.IS_SHOW_TAB, isshowtab)
        mCategoryFragment.setArguments(args)
        return mCategoryFragment
    }

    private fun getArgumentData() {
        if(arguments != null){
            if(arguments?.containsKey(Const.ID) != null &&
                arguments?.containsKey(Const.ID)!!){
                id = arguments?.getString(Const.ID)!!
                Log.e("////////","////id"+id.toString())
            }
            if(arguments?.containsKey(Const.IS_SHOW_TAB) != null &&
                arguments?.containsKey(Const.IS_SHOW_TAB)!!){
                showTab = arguments?.getBoolean(Const.IS_SHOW_TAB)!!
                Log.e("////////","////showTab//"+showTab.toString())

            }
            if(arguments?.containsKey("type") != null && arguments?.containsKey("type")!!){
                type = arguments?.getString("type")!!
                Log.e("////////","////type//"+type.toString())

            }
            if(arguments?.containsKey("title") != null && arguments?.containsKey("title")!!){
                title = arguments?.getString("title")!!
                Log.e("////////","title//"+title.toString())

            }
        }
    }

    var homeActivity: HomeActivity? = null
    lateinit var mprogressbar: ProgressBarHandler
    private var currentpage = 1
    private val isshowTab = showTab

    var realm: Realm? = null
    var arrdata: ArrayList<Record> = ArrayList()

    var currentPageNo = 1
    var currentPagedatasize = ""
    lateinit var scrollListener: EndlessRecyclerViewScrollListener
    lateinit var mLayoutManager: LinearLayoutManager
    var subdata: ArrayList<Record> = ArrayList()

    lateinit var mAdapter: SearchAdapter
    var searchList: List<Search> = java.util.ArrayList()

    var mTabCatagoryAdapterCategory: TabCatagoryAdapter? = null
    var data_listTabCatagoryAdapter: TabCatagoryAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_category, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //   setTabCategoryItemDecoration()
        getArgumentData()
        setCategoryItemDecoration()
    }

    private fun setScrollListener() {
        scrollListener = object : EndlessRecyclerViewScrollListener(mLayoutManager) {
            override fun onLoadMore(page: Int) {

                if (currentPagedatasize.equals(Const.PAGINATION_LIMIT)) {
                    currentPageNo++
                    Log.e("scroll", "onLoadMore" + currentPagedatasize + " Page no" + currentPageNo)
                    val searchtext = edt_search.text.toString().trim()
                    if (searchtext.isEmpty()) {
                        fi_search_data.visibility = View.GONE
                        licategory.visibility = View.VISIBLE
                        currentPageNo = 1
                        setScrollListener()
                        CallApiCategory(false)
                    } else {
                        fi_search_data.visibility = View.VISIBLE
                        licategory.visibility = View.GONE
                        currentPageNo = 1
                        setScrollListener()
                        CallApiCategorySearch(searchtext)
                    }

                    Log.e("scroll", "onLoadMore true")
                }
                Log.e("scroll", "onLoadMore false")
            }
        }
        rv_tab_category?.addOnScrollListener(scrollListener)
        rvCategory?.addOnScrollListener(scrollListener)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        homeActivity = activity as HomeActivity
        mLayoutManager = LinearLayoutManager(context)

     //   Realm.init(requireContext())
        realm = Realm.getDefaultInstance()

        ivBack.visibility = View.VISIBLE
        ivEdit.visibility = View.GONE
        txt_toolbar.text = getString(R.string.title_category)

        mprogressbar = ProgressBarHandler(requireActivity())

        init(true)
        setRefresh()
    }

    private fun setRefresh() {
        currentPageNo = 1
        setScrollListener()
        swf_category?.setOnRefreshListener {
            //init(false)
            val searchtext = edt_search.text.toString().trim()
            if (searchtext.isEmpty()) {
                fi_search_data.visibility = View.GONE
                licategory.visibility = View.VISIBLE
                CallApiCategory(false)
            } else {
                fi_search_data.visibility = View.VISIBLE
                licategory.visibility = View.GONE
                CallApiCategorySearch(searchtext)
            }
        }
    }


    private fun init(isprogress: Boolean) {
        CallApiCategory(isprogress)

        li_search.setOnClickListener(this)
        ivBack.setOnClickListener(this)
        ivSearch.setOnClickListener(this)
        as_tvClearAll.setOnClickListener(this)

        edt_search.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                li_serachistory?.visibility = View.GONE
                subdata.clear()
                subdata = ArrayList()
                ivSearch.visibility = View.GONE
                mTabCatagoryAdapterCategory?.notifyDataSetChanged()
                activity?.hideKeyboard(edt_search)
                val searchtext = edt_search.text.toString().trim()
                if (searchtext.isEmpty()) {
                    fi_search_data.visibility = View.GONE
                    licategory.visibility = View.VISIBLE
                    CallApiCategory(isprogress)
                } else {
                    fi_search_data.visibility = View.VISIBLE
                    licategory.visibility = View.GONE
                    CallApiCategorySearch(searchtext)
                }
            }
            true
        }

        edt_search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (!isshowTab!!) {
                    return
                }
                val searchtextlength = s.toString().trim().length
                if (searchtextlength == 0) {
                    activity?.hideKeyboard(edt_search)
                    fi_search_data.visibility = View.GONE
                    licategory.visibility = View.VISIBLE
                    li_serachistory?.visibility = View.GONE
                    CallApiCategory(isprogress)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s?.length == 0) {
                    ivSearch.visibility = View.GONE
                    edt_search.isCursorVisible = false
                } else {
                    ivSearch.visibility = View.VISIBLE
                    edt_search.isCursorVisible = true
                }


            }
        })


        edt_search.setOnTouchListener { v, event ->
            edt_search.requestFocus()
            showRecentSearchList()
            edt_search.isCursorVisible = true
            ivSearch.visibility = View.VISIBLE
            val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(edt_search, InputMethodManager.SHOW_IMPLICIT)
            return@setOnTouchListener true
        }
    }

    fun CallApiCategory(isprogress: Boolean) {
        if (isInternetAvailable(requireActivity())) {
            if (App.isShowProgressbar && isprogress)
                mprogressbar.showProgressBar()

            val param = ParamCategory().apply {
                page = currentpage.toString()
                limit = Const.PAGINATION_LIMIT
            }
            val retrofit = WebService.getRetrofit(requireActivity()).create(ApiClient::class.java)
            retrofit.GetCategory(param)
                .enqueue(object : Callback<ResponseCategory> {
                    override fun onFailure(call: Call<ResponseCategory>, t: Throwable) {
                        mprogressbar.hideProgressBar()
                        swf_category?.isRefreshing = false
                    }

                    override fun onResponse(
                        call: Call<ResponseCategory>,
                        response: Response<ResponseCategory>?
                    ) {
                        mprogressbar.hideProgressBar()
                        swf_category?.isRefreshing = false

                        if (response != null) {
                            if (response.isSuccessful) {
                                val responseLogin = response.body()

                                if (responseLogin != null) {
                                    if (responseLogin.status == API_SUCESS) {
                                        if (isAdded) {
//                                            arrdata.clear()
                                            arrdata.addAll(responseLogin.data.records)

                                            if (currentpage == responseLogin.data.TotalPage) {
                                                setData(arrdata, isprogress)
                                            } else {
                                                ++currentpage
                                                CallApiCategory(false)
                                            }
                                        }
                                    } else {
                                        //showToast(getString(R.string.err_mob_invalid))
                                    }
                                }
                            } else {
                                activity?.showToast(
                                    getString(R.string.msg_common_error),
                                    Const.ALERT
                                )
                            }
                        }
                    }
                })
        } else {
            NetworkErrorDialog(requireActivity())
            swf_category?.isRefreshing = false
        }
    }

    private fun setData(data: List<Record>, isprogress: Boolean) {

        val itemcount: Int? = data.size
        if (itemcount != 0) {
            if (isshowTab!!) {
                if(id.equals("0")) {
                    data.get(0).isSelected = true
                }
            } else {
                if(id.equals("0")){
                    data.get(0).isSelected = true
                }else{
                    for (i in data.indices) {
                        if (id.equals(data.get(i).ID)) {
                            data.get(i).isSelected = true
                            break
                        }
                    }
                }


            }

            rvMainCategory.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                val data_list = MainCategoryAdapter(this@CategoryFragment, data)
                adapter = data_list
            }

            if(isshowTab){
                if(id.equals("0")){
                    CallApiSubCategory(data.get(0).ID, isprogress)
                    Log.e("////////","//data.get(0).ID true///"+data.get(0).ID)
                }else{
                    loadProductCatalog(id.toString(),"New Kurta")
                }

            }else{
                if(id.equals("0")){
                    Log.e("////////","//data.get(0).ID false///"+data.get(0).ID)
                    CallApiSubCategory(data.get(0).ID, isprogress)
                }else{
                    if(type.equals("catlog")){
                        Log.e("////////","loadProductCatalog")
                        loadProductCatalog(id.toString(),title.toString())
                    }else{
                        Log.e("////////","//data.get(0).ID/false else//"+data.get(0).ID)
                        id?.let {
                            CallApiSubCategory(it, isprogress)
                        }
                    }

                }

            }

        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.as_tvClearAll -> {
                realm!!.executeTransaction { realm ->
                    val deleteSearchList: RealmResults<Search> =
                        realm.where(Search::class.java).findAll()
                    if (deleteSearchList != null) {
                        deleteSearchList.deleteAllFromRealm()
                        mAdapter!!.notifyDataSetChanged()
                        li_serachistory.setVisibility(View.GONE)
                    }
                }
            }
            R.id.li_search -> {
                edt_search.requestFocus()
                edt_search.isCursorVisible = true
                ivSearch.visibility = View.VISIBLE
                val imm = activity?.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(edt_search, InputMethodManager.SHOW_IMPLICIT)
            }

            R.id.ivSearch -> {
                subdata.clear()
                subdata = ArrayList()
                mTabCatagoryAdapterCategory?.notifyDataSetChanged()
                activity?.hideKeyboard(edt_search)
                ivSearch.visibility = View.GONE
                li_serachistory?.visibility = View.GONE
                val searchtext = edt_search.text.toString().trim()

                if (searchtext.isEmpty()) {
                    fi_search_data.visibility = View.GONE
                    licategory.visibility = View.VISIBLE
                    currentPageNo = 1
                    setScrollListener()
                    CallApiCategory(false)
                } else {
                    fi_search_data.visibility = View.VISIBLE
                    licategory.visibility = View.GONE
                    currentPageNo = 1
                    setScrollListener()
                    CallApiCategorySearch(searchtext)
                }
                /*fi_search_data.visibility = View.VISIBLE
                licategory.visibility = View.GONE
                CallApiCategorySearch(searchtext)*/
            }

            R.id.ivBack -> {
                activity?.hideKeyboard(edt_search)
                homeActivity?.selectLastMenu()
                fragmentManager?.popBackStack()
            }

        }

    }

    fun CallApiCategorySearch(searchtxt: String) {
        realm!!.executeTransaction { realm ->
            val maxValue: Number? = realm.where(Search::class.java).max("id")
            val id = if (maxValue != null) maxValue.toInt() + 1 else 1
            val searchAdapter: Search = realm.createObject(Search::class.java, id)
            if (searchAdapter.equals(searchtxt)) {
                Log.e("SearchActivity", "onQueryTextSubmit==if==$searchtxt")
            } else {
                searchAdapter.setsearchHistory(searchtxt)
                searchAdapter.setuserId(getUserId())
                realm.copyToRealmOrUpdate(searchAdapter)
                Log.e("SearchActivity", "onQueryTextSubmit==else==$searchtxt")
            }
        }

        if (isInternetAvailable(requireActivity())) {
            val param = ParamSearchCategory().apply {
                limit = Const.PAGINATION_LIMIT
                page = currentPageNo.toString()
                name = searchtxt
            }
            val retrofit = WebService.getRetrofit(requireActivity()).create(ApiClient::class.java)
            retrofit.GetSearchByCategory(param)
                .enqueue(object : Callback<ResponseSearchCategory> {
                    override fun onFailure(call: Call<ResponseSearchCategory>, t: Throwable) {
                        swf_category?.isRefreshing = false
                    }

                    override fun onResponse(
                        call: Call<ResponseSearchCategory>,
                        response: Response<ResponseSearchCategory>?
                    ) {
                        swf_category?.isRefreshing = false

                        if (response != null) {
                            if (response.isSuccessful) {
                                val responseproduct = response.body()
                                if (responseproduct != null) {
                                    if (responseproduct.status == API_SUCESS) {
                                        if (currentPageNo == 1) {
                                            rvCategory.visibility = View.VISIBLE
                                            liNoproduct.visibility = View.GONE
                                        }
                                        currentPagedatasize =
                                            responseproduct.data.records.size.toString()
                                        val lastsize = subdata.size
                                        subdata.addAll(responseproduct.data.records)
                                        if (currentPageNo == 1) {
                                            setDataSub()
                                        } else {
                                            mTabCatagoryAdapterCategory?.notifyItemRangeInserted(
                                                lastsize + 1,
                                                currentPagedatasize.toInt()
                                            )
                                            mTabCatagoryAdapterCategory?.notifyItemRangeChanged(
                                                lastsize + 1,
                                                currentPagedatasize.toInt()
                                            )
                                        }
                                    } else if (responseproduct.status == API_DATA_NOT_AVAILBLE) {
                                        if (currentPageNo == 1) {
                                            rvCategory.visibility = View.GONE
                                            liNoproduct.visibility = View.VISIBLE
                                        }
                                    }
                                }
                            } else {
                                activity?.showToast(
                                    getString(R.string.msg_common_error),
                                    Const.ALERT
                                )
                            }
                        }
                    }
                })

        } else {
            NetworkErrorDialog(requireActivity())
            swf_category?.isRefreshing = false
        }
    }

    fun loadProductCatalog(id: String, name: String) {

        activity?.hideKeyboard(edt_search)
        val fragment = ProductListFragment().getInstance(id, name)
        /*   homeActivity?.txt_toolbar?.text = getString(R.string.title_category)
           homeActivity?.ivEdit?.visibility = View.GONE
           homeActivity?.ivBack?.visibility = View.VISIBLE*/
        homeActivity?.deselectAllMenu()
        homeActivity?.supportFragmentManager
            ?.beginTransaction()
            ?.add(R.id.cm_fragmentContainer, fragment, fragment::class.java.name)
            ?.addToBackStack(null)
            ?.commit()


    }

    fun CallApiSubCategory(subcatid: String, isprogress: Boolean) {

        Log.e("////////","//subcatid/"+subcatid)
        if (isprogress)
            mprogressbar.showProgressBar()

        val param = ParamSubCategory().apply {
            categoryId = subcatid
            page = currentPageNo.toString()
            limit = Const.PAGINATION_LIMIT
        }
        val retrofit = WebService.getRetrofit(requireActivity()).create(ApiClient::class.java)
        retrofit.GetSubCategory(param)
            .enqueue(object : Callback<ResponseCategory> {
                override fun onFailure(call: Call<ResponseCategory>, t: Throwable) {
                    mprogressbar.hideProgressBar()
                }

                override fun onResponse(
                    call: Call<ResponseCategory>,
                    response: Response<ResponseCategory>?
                ) {
                    mprogressbar.hideProgressBar()
                    if (response != null) {
                        if (response.isSuccessful) {
                            val responseLogin = response.body()

                            if (responseLogin != null) {
                                if (responseLogin.status == API_SUCESS) {
                                    if (currentPageNo == 1) {
                                        rv_tab_category?.visibility = View.VISIBLE
                                        subdata = ArrayList()
                                    }
                                    currentPagedatasize = responseLogin.data.records.size.toString()
                                    val lastsize = subdata.size
                                    subdata.addAll(responseLogin.data.records)
                                    if (currentPageNo == 1) {
                                        setDataSub()
                                    } else {
                                        data_listTabCatagoryAdapter?.notifyItemRangeInserted(
                                            lastsize + 1,
                                            currentPagedatasize.toInt()
                                        )
                                        data_listTabCatagoryAdapter?.notifyItemRangeChanged(
                                            lastsize + 1,
                                            currentPagedatasize.toInt()
                                        )
                                    }
                                } else if (responseLogin.status == API_DATA_NOT_AVAILBLE) {
                                    if (currentPageNo == 1) {
                                        rv_tab_category?.visibility = View.GONE
                                    }
                                    //showToast(getString(R.string.err_mob_invalid))

                                }
                            }
                        } else {
                            activity?.showToast(getString(R.string.msg_common_error), Const.ALERT)
                        }
                    }
                }
            })
    }

    private fun setDataSub() {
        if (isAdded) {
            val displayMetrics = DisplayMetrics()
            activity?.windowManager?.defaultDisplay?.getMetrics(displayMetrics)
            val screenWidth: Int = displayMetrics.widthPixels

            rv_tab_category?.apply {
                data_listTabCatagoryAdapter = TabCatagoryAdapter(this@CategoryFragment, subdata)
                adapter = data_listTabCatagoryAdapter
                setTabCategoryItemDecoration()
            }


            rvCategory.apply {
                mTabCatagoryAdapterCategory = TabCatagoryAdapter(this@CategoryFragment, subdata)
                adapter = mTabCatagoryAdapterCategory
            }


            if (screenWidth > 0) {
                val m60PercentScreenWidth = (screenWidth * 56) / 100
                val m40PercentScreenWidth = (screenWidth * 36) / 100

                data_listTabCatagoryAdapter?.mFirstWidth = ((screenWidth / 2.0f) - 35f).toInt()
                data_listTabCatagoryAdapter?.notifyDataSetChanged()

                mTabCatagoryAdapterCategory?.mFirstWidth = ((screenWidth / 2.0f) - 35f).toInt()
                mTabCatagoryAdapterCategory?.notifyDataSetChanged()
            }
        }
    }

    private fun setTabCategoryItemDecoration() {
        val spanCount = 2
        val spacing = 0 // 50px
        val includeEdge = false
        rv_tab_category?.layoutManager = GridLayoutManager(context, spanCount)
//        rv_tab_category?.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        rv_tab_category?.addItemDecoration(
            GridSpacingItemDecoration(
                spanCount,
                spacing,
                includeEdge
            )
        )
    }

    private fun setCategoryItemDecoration() {
        val spanCount = 2
        val spacing = 25 // 50px
        val includeEdge = false
        rvCategory.layoutManager = GridLayoutManager(context, spanCount)
        rvCategory.addItemDecoration(GridSpacingItemDecoration(spanCount, spacing, includeEdge))
    }


    private fun showRecentSearchList() {
        realm!!.executeTransaction(object : Realm.Transaction {
            val searchAdapters = realm!!.where(Search::class.java)
                .findAllSorted("id", Sort.DESCENDING)
                .where()
                .distinct("searchHistory")


            override fun execute(realm: Realm) {

                Log.e("searchAdapters_", searchAdapters.size.toString() + "")

                if (searchAdapters.size > 0) {
                    searchAdapters[0].setuserId(getUserId())
                    as_tvClearAll?.visibility=View.VISIBLE
                }
                else{
                    as_tvClearAll?.visibility=View.GONE
                }
                li_serachistory?.setVisibility(View.VISIBLE)
                licategory.visibility = View.GONE
                fi_search_data.visibility = View.GONE
                /* } else {
                     li_serachistory?.setVisibility(View.GONE)
                     licategory.visibility = View.VISIBLE
                 }*/
                searchList = searchAdapters
                Log.e("searchAdapters_", searchList.toString())

                rv_search?.apply {
                    layoutManager =
                        LinearLayoutManager(context)
                    mAdapter = SearchAdapter(searchList)
                    adapter = mAdapter
                }
                mAdapter.setOnItemClickListener(object : SearchAdapter.MyClickListener {
                    override fun onItemClick(position: Int, v: View) {
                        when (v.id) {
                            R.id.search_text -> {
                                subdata.clear()
                                subdata = ArrayList()
                                mTabCatagoryAdapterCategory?.notifyDataSetChanged()
                                activity?.hideKeyboard(edt_search)
                                edt_search.setText(searchList.get(position).getsearchHistory())
                                edt_search.setSelection(
                                    searchList.get(position).getsearchHistory().length
                                )
                                val searchtext = edt_search.text.toString().trim()
                                ivSearch.visibility = View.GONE
                                fi_search_data.visibility = View.VISIBLE
                                li_serachistory?.visibility = View.GONE
                                CallApiCategorySearch(searchtext)
                            }

                        }
                    }
                })
            }
        })
    }

}

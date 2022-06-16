package com.ffi.my_orders

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.ffi.App
import com.ffi.HomeActivity
import com.ffi.R
import com.ffi.Utils.*
import com.ffi.my_orderdetails.OrderDetailFragment
import com.ffi.api.ApiClient
import com.ffi.api.WebService
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import kotlinx.android.synthetic.main.fragment_my_order.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyOrderFragment() : Fragment(), View.OnClickListener {
    var isShowtab: Boolean = false
    var isshowdetailauto: Boolean = false

    fun getInstance(isShowtab: Boolean, isshowdetailauto: Boolean): MyOrderFragment {
        val mMyOrderFragment = MyOrderFragment()
        val args = Bundle()
        args.putBoolean(Const.IS_SHOW_TAB, isShowtab)
        args.putBoolean(Const.IS_SHOW_DETAIL_AUDIO, isshowdetailauto)
        mMyOrderFragment.setArguments(args)
        return mMyOrderFragment
    }

    private fun getArgumentData() {
        if(arguments != null){
            if(arguments?.containsKey(Const.IS_SHOW_TAB) != null &&
                arguments?.containsKey(Const.IS_SHOW_TAB)!!){
                isShowtab = arguments?.getBoolean(Const.IS_SHOW_TAB)!!
            }
            if(arguments?.containsKey(Const.IS_SHOW_DETAIL_AUDIO) != null &&
                arguments?.containsKey(Const.IS_SHOW_DETAIL_AUDIO)!!){
                isshowdetailauto = arguments?.getBoolean(Const.IS_SHOW_DETAIL_AUDIO)!!
            }
        }
    }


    lateinit var mprogressbar: ProgressBarHandler

    lateinit var order_list: OrderListAdapter
    var homeActivity: HomeActivity? = null
    var currentPageNo = 1
    var currentPagedatasize = ""
    lateinit var scrollListener: EndlessRecyclerViewScrollListener
    lateinit var mLayoutManager: LinearLayoutManager
    var arrOrders: ArrayList<Record> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_order, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        homeActivity = activity as HomeActivity

        mprogressbar = ProgressBarHandler(requireActivity())
        getArgumentData()
        init(true)
        setRefresh()
    }

    private fun setRefresh() {
        swf_orderlist?.setOnRefreshListener {
            init(false)
        }
    }

    private fun init(isprogress: Boolean) {
        mLayoutManager = LinearLayoutManager(context)
        ivBack.visibility = View.VISIBLE
        ivEdit.visibility = View.GONE
        txt_toolbar.text = getString(R.string.title_my_order)

        if (isShowtab) {
            homeActivity?.navigation?.visibility = View.VISIBLE
        } else {
            homeActivity?.navigation?.visibility = View.GONE
        }

        edt_search.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                activity?.hideKeyboard(edt_search)
                val searchtext = edt_search.text.toString().trim()
                if (searchtext.isEmpty()) {
                    if (!getUserId().isEmpty()) {
                        currentPageNo = 1
                        setScrollListener()
                        Log.e("api", "callapi 86 " + currentPageNo)
                        CallApiOrderList(isprogress)
                    }
                } else {
                    if (!getUserId().isEmpty()) {
                        currentPageNo = 1
                        setScrollListener()
                        CallApiSearchOrder(searchtext)
                    }
                }
            }
            true
        }

        edt_search.setOnTouchListener { v, event ->
            edt_search.requestFocus()
            edt_search.isCursorVisible = true
            ivSearch.visibility = View.VISIBLE
            val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(edt_search, InputMethodManager.SHOW_IMPLICIT)
            return@setOnTouchListener true
        }

        edt_search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val searchtextlength = s.toString().trim().length
                Log.e("api", "callapi length-" + searchtextlength)
                if (searchtextlength == 0) {
                    currentPageNo = 1
                    Log.e("api", "callapi 101 " + currentPageNo)
                    CallApiOrderList(isprogress)
                    setScrollListener()
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
        setListener()

        if (!getUserId().isEmpty()) {
            Log.e("api", "call api 132")
            CallApiOrderList(isprogress)
            setScrollListener()
        } else {
            rvOrders?.visibility = View.GONE
            tvNoOrder?.visibility = View.VISIBLE
            tvMsg.text = getString(R.string.msg_no_orders)
        }
    }

    private fun setScrollListener() {
        scrollListener = object : EndlessRecyclerViewScrollListener(mLayoutManager) {
            override fun onLoadMore(page: Int) {
                Log.e("scroll", "onLoadMore" + currentPagedatasize + " Page no" + page)
                if (currentPagedatasize.equals(Const.PAGINATION_LIMIT)) {
                    currentPageNo++
                    val searchtext = edt_search.text.toString()
                    if (searchtext.isEmpty()) {
                        Log.e("api", "callapi 140 " + currentPageNo)
                        CallApiOrderList(false)
                    } else {
                        CallApiSearchOrder(searchtext)
                    }
                    Log.e("scroll", "onLoadMore true")
                }
                Log.e("scroll", "onLoadMore false")
            }
        }
        rvOrders?.addOnScrollListener(scrollListener)
    }

    private fun setListener() {
        ivBack.setOnClickListener(this)
        li_search.setOnClickListener(this)
        ivSearch.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBack -> {
                Log.e("res", isShowtab.toString())
                if (isShowtab) {
                    homeActivity?.deselectAllMenu()
                    homeActivity?.replaceFragment(App.fragmentaccount, Const.TAG_FRAG_ACCOUNT)
                } else {
                    homeActivity?.selectFirstMenu()
                    homeActivity?.navigation?.visibility = View.VISIBLE
                    homeActivity?.replaceFragment(App.fragmenthome, Const.TAG_FRAG_HOME)
                }
                activity?.hideKeyboard(edt_search)
            }
            R.id.li_search -> {
                edt_search.requestFocus()
                edt_search.isCursorVisible = true
                ivSearch.visibility = View.VISIBLE
                val imm =
                    activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(edt_search, InputMethodManager.SHOW_IMPLICIT)
            }
            R.id.ivSearch -> {
                val searchtext = edt_search.text.toString().trim()
                if (searchtext.isEmpty()) {
                    if (!getUserId().isEmpty()) {
                        currentPageNo = 1
                        setScrollListener()
                        Log.e("api", "callapi 86 " + currentPageNo)
                        CallApiOrderList(false)
                    }
                } else {
                    if (!getUserId().isEmpty()) {
                        currentPageNo = 1
                        setScrollListener()
                        CallApiSearchOrder(searchtext)
                    }
                }

                /* if (!getUserId().isEmpty()) {
                     val searchtext = edt_search.text.toString().trim()
                     currentPageNo = 1
                     setScrollListener()
                     CallApiSearchOrder(searchtext)
                 }*/
            }
        }
    }

    fun CallApiOrderList(isLoaderVisible: Boolean) {

        if (isInternetAvailable(requireActivity())) {
            if (currentPageNo == 1 && isLoaderVisible) {
                arrOrders = ArrayList()
                mprogressbar.showProgressBar()
            }
            val param = ParamOrderList().apply {
                limit = Const.PAGINATION_LIMIT
                page = currentPageNo.toString()
                orderStatus = "1"
            }

            val retrofit = WebService.getRetrofit(requireActivity()).create(ApiClient::class.java)
            retrofit.GetOrderList(param)
                .enqueue(object : Callback<ResponseOrderList> {
                    override fun onFailure(call: Call<ResponseOrderList>, t: Throwable) {
                        mprogressbar.hideProgressBar()
                        swf_orderlist?.isRefreshing = false
                        Log.e("res", "on failer")
                    }

                    override fun onResponse(
                        call: Call<ResponseOrderList>,
                        response: Response<ResponseOrderList>?
                    ) {
                        mprogressbar.hideProgressBar()
                        swf_orderlist?.isRefreshing = false
                        if (response != null) {
                            if (response.isSuccessful) {
                                val responseLogin = response.body()

                                if (responseLogin != null) {
                                    if (responseLogin.status == API_SUCESS) {

                                        if (isAdded) {
                                            if (currentPageNo.toString().equals("1")) {
                                                arrOrders = ArrayList()
                                            }
                                            val data = responseLogin.data
                                            currentPagedatasize = data.records.size.toString()
                                            arrOrders.addAll(data.records)
                                            if (currentPageNo.toString().equals("1")) {
                                                setData()
                                            } else {
                                                order_list.notifyDataSetChanged()
                                            }
                                        }

                                    } else if (responseLogin.status == API_DATA_NOT_AVAILBLE) {
                                        if (currentPageNo.toString().equals("1")) {
                                            rvOrders?.visibility = View.GONE
                                            tvNoOrder?.visibility = View.VISIBLE
                                            tvMsg.text = getString(R.string.msg_no_orders)
                                        }
                                        //showToast(getString(R.string.err_mob_invalid))

                                    }
                                }
                            } else {
                                requireActivity().showToast(
                                    getString(R.string.msg_common_error),
                                    Const.ALERT
                                )
                            }
                        }
                    }
                })

        } else {
            swf_orderlist?.isRefreshing = false
            NetworkErrorDialog(requireActivity())
        }
    }

    private fun setData() {
        if (isAdded) {
            rvOrders?.visibility = View.VISIBLE
            tvNoOrder?.visibility = View.GONE

            rvOrders?.apply {
                layoutManager = mLayoutManager
                order_list = OrderListAdapter(this@MyOrderFragment, arrOrders)
                itemAnimator = DefaultItemAnimator()
                adapter = order_list
            }

            /*if (!isShowtab && isshowdetailauto) {
                try {
                    arrOrders.get(0).orderId?.let { loadOrderDetails(it) }
                } catch (e: IndexOutOfBoundsException) {
                    e.printStackTrace()
                }
            }*/
        }
    }

    fun loadOrderDetails(orderId: String) {
        activity?.hideKeyboard(edt_search)
        try {
            val fragment = OrderDetailFragment().getInstance(orderId, isShowtab)
            //    homeActivity.replaceFragment(fragment, Const.TAG_FRAG_ORDER_DETAIL)
            // homeActivity?.deselectAllMenu()
            homeActivity?.supportFragmentManager
                ?.beginTransaction()
                ?.add(R.id.cm_fragmentContainer, fragment, fragment::class.java.name)
                ?.addToBackStack(null)
                ?.commit()
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }
        //homeActivity?.replaceFragment(fragment, Const.TAG_FRAG_ORDER_DETAIL)
    }

    fun CallApiSearchOrder(id: String) {
        if (isInternetAvailable(requireActivity())) {
            if (currentPageNo == 1) {
                arrOrders = ArrayList()
                mprogressbar.showProgressBar()
            }

            val param = ParamSearchOrders().apply {
                limit = Const.PAGINATION_LIMIT
                page = currentPageNo.toString()
                orderId = id
            }

            val retrofit = WebService.getRetrofit(requireActivity()).create(ApiClient::class.java)
            retrofit.GetSerachfromOrderId(param)
                .enqueue(object : Callback<ResponseSearchOrder> {
                    override fun onFailure(call: Call<ResponseSearchOrder>, t: Throwable) {
                        mprogressbar.hideProgressBar()
                        swf_orderlist?.isRefreshing = false
                        Log.e("res", "onError")
                    }

                    override fun onResponse(
                        call: Call<ResponseSearchOrder>,
                        response: Response<ResponseSearchOrder>?
                    ) {
                        Log.e("res", "onResponse")
                        mprogressbar.hideProgressBar()
                        swf_orderlist?.isRefreshing = false
                        if (response != null) {
                            if (response.isSuccessful) {
                                val responseLogin = response.body()

                                if (responseLogin != null) {
                                    if (responseLogin.status == API_SUCESS) {
                                        val data = responseLogin.data

                                        if (data.records.size > 0) {

                                            rvOrders.visibility = View.VISIBLE
                                            tvNoOrder.visibility = View.GONE

                                            if (currentPageNo.toString().equals("1")) {
                                                arrOrders = ArrayList()
                                            }

                                            currentPagedatasize = data.records.size.toString()
                                            arrOrders.addAll(data.records)
                                            if (currentPageNo.toString().equals("1")) {
                                                setData()
                                            } else {
                                                order_list.notifyDataSetChanged()
                                            }
                                        } else {
                                            if (currentPageNo == 1) {
                                                rvOrders?.visibility = View.GONE
                                                tvNoOrder?.visibility = View.VISIBLE
                                                tvMsg.text = getString(R.string.no_id_found)
                                            }

                                        }
                                    } else if (responseLogin.status == API_DATA_NOT_AVAILBLE) {
                                        rvOrders?.visibility = View.GONE
                                        tvNoOrder?.visibility = View.VISIBLE
                                        //showToast(getString(R.string.err_mob_invalid))

                                    }
                                }
                            } else {
                                requireActivity().showToast(
                                    getString(R.string.msg_common_error),
                                    Const.ALERT
                                )
                            }
                        }
                    }
                })
        } else {
            swf_orderlist?.isRefreshing = false
            NetworkErrorDialog(requireActivity())
        }
    }

}

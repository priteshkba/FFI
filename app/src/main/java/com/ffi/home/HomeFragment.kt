package com.ffi.home

import android.Manifest
import android.app.Dialog

import android.app.ProgressDialog
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.FacebookSdk
import com.facebook.share.Sharer
import com.facebook.share.model.ShareMedia
import com.facebook.share.model.ShareMediaContent
import com.facebook.share.model.SharePhoto
import com.facebook.share.model.ShareVideo
import com.facebook.share.widget.ShareDialog
import com.ffi.App
import com.ffi.HomeActivity
import com.ffi.R
import com.ffi.Utils.*
import com.ffi.catalog.CollectionFragment
import com.ffi.category.CategoryFragment
import com.ffi.category.SearchHome
import com.ffi.category.SearchHomeAdapter
import com.ffi.model.ParamAddREmoveWishItem
import com.ffi.model.ResponseAddRemoveWishItem
import com.ffi.productdetail.ProductDetailFragment
import com.ffi.productlist.ProductListFragment
import com.ffi.api.ApiClient
import com.ffi.api.WebService
import com.ffi.model.ParamAddShareddata
import com.ffi.model.ResponseAddSharedData
import com.ffi.productlist.Media
import com.ffi.productlist.Record
import io.realm.Realm
import io.realm.RealmResults
import io.realm.Sort
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import kotlinx.android.synthetic.main.fragment_category.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.as_tvClearAll
import kotlinx.android.synthetic.main.fragment_home.edt_search
import kotlinx.android.synthetic.main.fragment_home.fi_search_data
import kotlinx.android.synthetic.main.fragment_home.ivSearch
import kotlinx.android.synthetic.main.fragment_home.liNoproduct
import kotlinx.android.synthetic.main.fragment_home.li_search
import kotlinx.android.synthetic.main.fragment_home.li_serachistory
import kotlinx.android.synthetic.main.fragment_home.rvCategory
import kotlinx.android.synthetic.main.fragment_home.rv_search
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.*
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import kotlin.collections.ArrayList

public class HomeFragment : Fragment(), View.OnClickListener, ViewPager.OnPageChangeListener {


    var imageUriArray: ArrayList<Uri> = ArrayList<Uri>()
    var videoUriArray: ArrayList<Uri> = ArrayList<Uri>()
    var bothUriArray: ArrayList<Uri> = ArrayList<Uri>()
    var imageUrifb: ArrayList<Uri> = ArrayList<Uri>() //for facebook share
    var imageUriGmail: ArrayList<Uri> = ArrayList<Uri>() //for facebook share

    private val WRITE_PERMISSION = 0
    private val SHARE_DESC = 10
    var sharedesc = ""

    private var isDialogOpen = false


    lateinit var btnDone: TextView
    lateinit var liDownloads: LinearLayout


    lateinit var mprogressbar: ProgressBarHandler

    var datasearch: ArrayList<RecordXXX> = ArrayList()

    var realm: Realm? = null
    lateinit var searchAdapter: SearchHomeAdapter
    var searchList: List<SearchHome> = java.util.ArrayList()

    lateinit var Catalog_list: CatalogAdapter
    lateinit var Category_list: CatagoryAdapter
    lateinit var pop_Category_list: PopulerCatagoryAdapter
    lateinit var top_list: Top50Adapter
    lateinit var loaderDialogView: LoaderDialogView

    var mSearchProductAdapter: SearchProductAdapter? = null

    var currentPageNo = 1
    var currentPageForScrollNo = 1

    var currentPagedatasize = ""
    lateinit var scrollListener: EndlessRecyclerViewScrollListener
    lateinit var mLayoutManager: LinearLayoutManager

    var homeActivity: HomeActivity? = null

    private var dotsCount: Int = 0
    lateinit var data: Data
    private var dots: Array<ImageView?> = emptyArray()
    lateinit var mImageResources: ArrayList<Banner>
    private var mAdapter: ViewPagerBannerAdapter? = null
    var timer: Timer? = null
    val DELAY_MS: Long = 3000 //delay in milliseconds before task is to be executed
    val PERIOD_MS: Long = 3000


    lateinit var shareDialog: ShareDialog
    lateinit var callbackManager: CallbackManager

    private val callback: FacebookCallback<Sharer.Result> =
        object : FacebookCallback<Sharer.Result> {
            override fun onSuccess(result: Sharer.Result) {
                Log.e("TAG", "Succesfully posted")
                // Write some code to do some operations when you shared content successfully.
            }

            override fun onCancel() {
                Log.e("TAG", "Cancel occured")
                // Write some code to do some operations when you cancel sharing content.
            }

            override fun onError(error: FacebookException) {
//                Log.e("TAG", error.message)
                // Write some code to do some operations when some error occurs while sharing content.
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        FacebookSdk.sdkInitialize(requireActivity().applicationContext)
        callbackManager = CallbackManager.Factory.create()
        shareDialog = ShareDialog(this)
        shareDialog.registerCallback(callbackManager, callback, 100)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeActivity?.selectLastMenu()
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setCatalogItemDecoration()
        setCategoryItemDecoration()
        setPopularItemDecoration()
        setTop50ListItemDecoration()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mprogressbar = ProgressBarHandler(requireActivity())
        mLayoutManager = LinearLayoutManager(context)

        // Realm.init(requireContext())
        realm = Realm.getDefaultInstance()

        homeActivity = activity as HomeActivity
        homeActivity?.navigation?.visibility = View.VISIBLE
        txt_toolbar.text = getString(R.string.title_home)
        ivBack.visibility = View.GONE

        init(true)
        setListener()
        setRefresh()
    }

    private fun setRefresh() {
        swf_home?.setOnRefreshListener {
            init(false)
            setListener()
        }
    }

    private fun callApiHomeData(isLoader: Boolean) {
        if (isInternetAvailable(requireActivity())) {
            if (isLoader && App.isShowProgressbar) {
                mprogressbar.showProgressBar()
            }
            loaderDialogView = LoaderDialogView(requireActivity())
            //loaderDialogView.show()

            val retrofit = WebService.getRetrofit(requireActivity()).create(ApiClient::class.java)
            retrofit.GetHomeData()
                .enqueue(object : Callback<ResponseHome> {
                    override fun onFailure(call: Call<ResponseHome>, t: Throwable) {
                        mprogressbar.hideProgressBar()
                        swf_home?.isRefreshing = false
                        Log.e("home", "Onfailer " + t.message)
                    }

                    override fun onResponse(
                        call: Call<ResponseHome>,
                        response: Response<ResponseHome>?
                    ) {
                        mprogressbar.hideProgressBar()
                        swf_home?.isRefreshing = false
                        if (response != null) {
                            if (response.isSuccessful) {
                                val responseLogin = response.body()

                                if (responseLogin != null) {
                                    if (responseLogin.status == API_SUCESS) {

                                        if (isAdded) {
                                            data = responseLogin.data

                                            storeCartItem(data.CartItems.toInt())
                                            if (getCartItem() == 0) {
                                                homeActivity?.badgetext?.visibility = View.GONE
                                            } else {
                                                homeActivity?.badgetext?.visibility = View.VISIBLE
                                                homeActivity?.badgetext?.text =
                                                    getCartItem().toString()
                                            }

                                            if (data.VersionUpdate.equals("1")) {
                                                homeActivity?.StartUpdate()
                                            }

                                            //set Banner
                                            if (data.Banners != null) {
                                                mImageResources = ArrayList()
                                                mImageResources.addAll(data.Banners)
                                            }

                                            if (mImageResources.size >= 1) {
                                                mAdapter = ViewPagerBannerAdapter(
                                                    this@HomeFragment,
                                                    mImageResources
                                                )
                                                val pagerPadding = 30
                                                vp_banner?.clipToPadding = false
                                                vp_banner?.pageMargin = dpToPx(30)
                                                vp_banner?.setPadding(
                                                    pagerPadding,
                                                    0,
                                                    pagerPadding,
                                                    0
                                                )
                                                vp_banner.adapter = mAdapter
                                                vp_banner.currentItem = 0
                                                vp_banner.setOnPageChangeListener(this@HomeFragment)
                                                setUiPageViewController()
                                            }

                                            if (data.Banners != null && data.Banners.size > 0) {
                                                frame_banner.visibility = View.VISIBLE
                                            } else {
                                                frame_banner.visibility = View.GONE
                                            }

                                            //Set Populer catalog
                                            tv_title_catalog.text = data.PopularCatalogue?.Title
                                            data.PopularCatalogue?.let { setDataCatalog(it) }

                                            //set Main Category
                                            setDataCategory(data.MainCategory)

                                            //set Populer category
                                            tv_populer_catagory.text =
                                                data.PopularCategory?.Title ?: ""
                                            data.PopularCategory?.let { setPopulerCategory(it) }


                                            //set Populer category
                                            tv_top_item.text = data.TopProduct.Title
                                            setTop50(data.TopProduct)

                                        }
                                    } else {
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
            NetworkErrorDialog(requireActivity())
            swf_home?.isRefreshing = false
        }
    }


    private fun init(isprogress: Boolean) {

        edt_search.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                datasearch.clear()
                datasearch = ArrayList()
                mSearchProductAdapter?.notifyDataSetChanged()

                val searchtext = edt_search.text.toString().trim()
                li_serachistory?.visibility = View.GONE

                activity?.hideKeyboard(edt_search)
                ivSearch.visibility = View.GONE

                if (searchtext.isEmpty()) {
                    fi_search_data.visibility = View.GONE
                    li_home_data.visibility = View.VISIBLE
                    callApiHomeData(isprogress)
                } else {
                    rvProducts?.visibility = View.GONE
                    liNoproduct?.visibility = View.GONE
                    fi_search_data.visibility = View.VISIBLE
                    li_home_data.visibility = View.GONE
                    currentPageNo = 1
                    setScrollListener()
                    CallApiSearchProduct(searchtext, isprogress)
                }
            }
            true
        }

        callApiHomeData(isprogress)

        edt_search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val searchtextlength = s.toString().trim().length
                if (searchtextlength == 0) {
                    activity?.hideKeyboard(edt_search)
                    li_serachistory?.visibility = View.GONE
                    fi_search_data.visibility = View.GONE
                    li_home_data.visibility = View.VISIBLE
                    callApiHomeData(isprogress)
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
    }

    private fun setListener() {
        tv_catalog_viewall.setOnClickListener(this)
        tv_category_viewall.setOnClickListener(this)
        li_search.setOnClickListener(this)
        ivSearch.setOnClickListener(this)
        tv_viewalltop.setOnClickListener(this)
        as_tvClearAll.setOnClickListener(this)

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

    private fun setTop50(topProduct: TopProduct) {
        if (isAdded) {
            rv_top_50.apply {
                top_list = Top50Adapter(this@HomeFragment)
                top_list.addModels(topProduct.Records)
                adapter = top_list
            }
            if (topProduct != null && topProduct.Records != null
                && topProduct.Records.size != null
                && topProduct.Records.size > 0
            ) {
                ll_top50items.visibility = View.VISIBLE
                rv_top_50.visibility = View.VISIBLE
            } else {
                ll_top50items.visibility = View.GONE
                rv_top_50.visibility = View.GONE
            }

            val displayMetrics = DisplayMetrics()
            activity?.windowManager?.defaultDisplay?.getMetrics(displayMetrics)
            val screenWidth: Int = displayMetrics.widthPixels

            if (screenWidth > 0) {
                val m60PercentScreenWidth = (screenWidth * 56) / 100
                top_list.mFirstWidth = m60PercentScreenWidth
            }

        }
    }

    private fun setPopulerCategory(popularCategory: PopularCategory) {
        if (isAdded) {
            rv_pop_category.apply {
                pop_Category_list = PopulerCatagoryAdapter(this@HomeFragment)
                popularCategory.Records?.let { pop_Category_list.addModels(it) }
                adapter = pop_Category_list
            }
            if (popularCategory != null && popularCategory.Records != null
                && popularCategory.Records?.size != null
                && popularCategory.Records?.size!! > 0
            ) {
                rl_popular_category.visibility = View.VISIBLE
                rv_pop_category.visibility = View.VISIBLE
            } else {
                rl_popular_category.visibility = View.GONE
                rv_pop_category.visibility = View.GONE
            }
        }
    }

    private fun setDataCategory(data: List<MainCategory>) {
        if (isAdded) {
            rvCategory?.apply {
                //layoutManager = LinearLayoutManager(context)
                Category_list = CatagoryAdapter(this@HomeFragment)
                Category_list.addModels(data)
                adapter = Category_list
            }
        }
    }

    private fun setCategoryItemDecoration() {
        val spanCount = 3
        val spacing = 20 // 50px
        val includeEdge = false
        rvCategory.layoutManager = GridLayoutManager(context, spanCount)
        rvCategory.addItemDecoration(GridSpacingItemDecoration(spanCount, spacing, includeEdge))
    }

    private fun setPopularItemDecoration() {
        val spanCount = 3
        val spacing = 20 // 50px
        val includeEdge = false
        rv_pop_category.layoutManager = GridLayoutManager(context, spanCount)
        rv_pop_category.addItemDecoration(
            GridSpacingItemDecoration(
                spanCount,
                spacing,
                includeEdge
            )
        )
    }

    private fun setTop50ListItemDecoration() {
        val spanCount = 3
        val spacing = 5 // 50px
        val includeEdge = false
        rv_top_50.layoutManager = GridLayoutManager(context, spanCount)
        rv_top_50.addItemDecoration(GridSpacingItemDecoration(spanCount, spacing, includeEdge))
    }


    private fun setDataCatalog(popularCatalogue: PopularCatalogue) {
        if (isAdded) {
            rv_pop_catalog.apply {
                //layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                Catalog_list = CatalogAdapter(this@HomeFragment)
                Catalog_list.addModels(popularCatalogue.Records)
                adapter = Catalog_list
            }

            if (popularCatalogue.Records != null && popularCatalogue.Records.size > 0) {
                fl_popular_catalog.visibility = View.VISIBLE
            } else {
                fl_popular_catalog.visibility = View.GONE
            }
        }
    }

    private fun setCatalogItemDecoration() {
        val spanCount = 2
        val spacing = 20// 50px
        val includeEdge = false
        rv_pop_catalog.layoutManager = GridLayoutManager(context, spanCount)
        rv_pop_catalog.addItemDecoration(GridSpacingItemDecoration(spanCount, spacing, includeEdge))
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.as_tvClearAll -> {
                realm!!.executeTransaction { realm ->
                    val deleteSearchList: RealmResults<SearchHome> =
                        realm.where(SearchHome::class.java).findAll()
                    if (deleteSearchList != null) {
                        deleteSearchList.deleteAllFromRealm()
                        mAdapter?.notifyDataSetChanged()
                        li_serachistory.setVisibility(View.GONE)
                    }
                }
            }
            R.id.tv_catalog_viewall -> {
                activity?.hideKeyboard(edt_search)
                homeActivity?.setCollectionScreen()
                homeActivity?.menu?.findItem(R.id.menu_collection)?.isChecked = true
            }
            R.id.tv_viewalltop -> {
                activity?.hideKeyboard(edt_search)
                loadProductList("-1", tv_top_item.text.toString())
            }
            R.id.tv_category_viewall -> {
                activity?.hideKeyboard(edt_search)
                loadCategoryFragment(true, "0", "", "")
            }
            R.id.ivSearch -> {
                li_serachistory?.visibility = View.GONE
                datasearch.clear()
                datasearch = ArrayList()
                mSearchProductAdapter?.notifyDataSetChanged()
                val searchtext = edt_search.text.toString().trim()
                activity?.hideKeyboard(edt_search)
                if (searchtext.isEmpty()) {
                    //return
                    fi_search_data.visibility = View.GONE
                    li_home_data.visibility = View.VISIBLE
                    ivSearch.visibility = View.GONE
                    callApiHomeData(false)
                } else {
                    rvProducts?.visibility = View.GONE
                    liNoproduct?.visibility = View.GONE
                    fi_search_data.visibility = View.VISIBLE
                    li_home_data.visibility = View.GONE

                    CallApiSearchProduct(searchtext, true)
                }
            }
            R.id.li_search -> {
                edt_search.requestFocus()
                edt_search.isCursorVisible = true
                ivSearch.visibility = View.VISIBLE
                val imm =
                    activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(edt_search, InputMethodManager.SHOW_IMPLICIT)
            }
        }
    }

    fun loadCategoryFragment(isShowTab: Boolean, id: String, type: String, title: String) {

        Log.e("////////", "loadCategoryFragment  isShowTab" + isShowTab)
        Log.e("////////", "loadCategoryFragment  isShowTab" + id)
        Log.e("////////", "loadCategoryFragment  isShowTab" + type)


        activity?.hideKeyboard(edt_search)
        val fragment = CategoryFragment().getInstance(id, isShowTab, type, title)
        homeActivity?.deselectAllMenu()
        homeActivity?.supportFragmentManager
            ?.beginTransaction()
            ?.add(R.id.cm_fragmentContainer, fragment, fragment::class.java.name)
            ?.addToBackStack(null)
            ?.commit()
    }

    fun loadProductList(id: String, categoryName: String) {
        activity?.hideKeyboard(edt_search)
        val fragment = ProductListFragment().getInstance(id, categoryName)
        homeActivity?.deselectAllMenu()
        homeActivity?.supportFragmentManager
            ?.beginTransaction()
            ?.add(R.id.cm_fragmentContainer, fragment, fragment::class.java.name)
            ?.addToBackStack(null)
            ?.commit()
    }

    fun loadProductDetail(id: String) {
        activity?.hideKeyboard(edt_search)
        val fragment = ProductDetailFragment().getInstance(id, "")
        homeActivity?.deselectAllMenu()
        homeActivity?.supportFragmentManager
            ?.beginTransaction()
            ?.add(R.id.cm_fragmentContainer, fragment, fragment::class.java.name)
            ?.addToBackStack(null)
            ?.commit()
    }

    ///////////////////


    inner class DownloadTask(
        val imgurl: List<com.ffi.home.Media>?,
        val id: String,
        val isDownloadonly: Boolean
    ) :
        AsyncTask<Void, Void, String>() {

        var dialog = ProgressDialog(activity)

        override fun onPreExecute() {
            super.onPreExecute()

            if (isDownloadonly) {
                dialog.setMessage(getString(R.string.downloading_images))
                dialog.show()
            }
            val trustAllCerts: Array<TrustManager> = arrayOf<TrustManager>(
                object : X509TrustManager {
                    override fun checkClientTrusted(
                        certs: Array<X509Certificate?>?,
                        authType: String?
                    ) {
                    }

                    override fun checkServerTrusted(
                        certs: Array<X509Certificate?>?,
                        authType: String?
                    ) {
                    }

                    override fun getAcceptedIssuers(): Array<X509Certificate>? {
                        return null
                    }
                }


            )

            // Activate the new trust manager
            try {
                val sc: SSLContext = SSLContext.getInstance("SSL")
                sc.init(null, trustAllCerts, SecureRandom())
                HttpsURLConnection.setDefaultSSLSocketFactory(sc.socketFactory)
            } catch (e: java.lang.Exception) {
            }


        }

        override fun doInBackground(vararg params: Void?): String {
            try {

                if (imgurl?.size ?: 0 > 0) {
                    for (i in imgurl?.indices!!) {
                        Log.e("videourl", "type media: " + imgurl.get(i).mediaTypeId)
                        if (imgurl.get(i).mediaTypeId.equals(Const.MEDIA_VIDEO)) {
                            val filenamesplit = imgurl.get(i).media.split("/").toTypedArray()
                            val size = filenamesplit.size
                            Log.e("videourl", "type video")


                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {


                                val ffidir = File(
                                    requireContext().cacheDir,
                                    getString(R.string.app_name)
                                )
                                if (!ffidir.exists()) {
                                    ffidir.mkdir()
                                }
                                /*val mypath = File(ffidir, id + "_" +
                                        size?.minus(1)?.let { filenamesplit.get(it) })*/
                                val mypath = File(
                                    ffidir, id + "_" +
                                            "video1.mp4"
                                )

                                try {
                                    Log.e("videourl", getMediaLink() + imgurl.get(i).media)
                                    val outputStream =
                                        FileOutputStream(
                                            java.lang.String.valueOf(mypath)
                                        )


                                    val videoUrl = URL(getMediaLink() + imgurl.get(i).media)
                                    val conc = videoUrl.openConnection() as HttpURLConnection
                                    conc.doInput = true
                                    conc.connect()
                                    val inst: InputStream = conc.inputStream
                                    var buffer: ByteArray = ByteArray(1024)
                                    var len1: Int = 0
                                    while ((inst.read(buffer).also({ len1 = it })) > 0) {
                                        outputStream.write(buffer, 0, len1)
                                    }
                                    outputStream.close()
                                    Log.e("videourl", "local path: " + mypath.toString())


                                    videoUriArray.add(Uri.parse(mypath.toString()))
                                    imageUrifb.add(Uri.fromFile(mypath))
                                    imageUriGmail.add(Uri.parse("content://" + mypath.absolutePath))

                                } catch (e: java.lang.Exception) {
                                    Log.e("videourl", "catch:")
                                    Log.e("videourl", "catch:" + e.message)
                                }
                            } else {
                                Log.e("videourl", "else:")

                                val values = ContentValues()
                                val fileName =
                                    id + "_" + size?.minus(1)?.let { filenamesplit.get(it) }

                                values.put(
                                    MediaStore.MediaColumns.DISPLAY_NAME,
                                    fileName
                                )

                                values.put(
                                    MediaStore.MediaColumns.MIME_TYPE,
                                    "video/mp4"
                                )

                                values.put(
                                    MediaStore.MediaColumns.RELATIVE_PATH,
                                    Environment.DIRECTORY_DOWNLOADS + "/FFI_Videos/"
                                )
                                // MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                                val uri: Uri? = requireActivity().contentResolver.insert(
                                    MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                                    values!!
                                )
                                try {
                                    val outputStream: OutputStream? =
                                        requireActivity().contentResolver.openOutputStream(uri!!)


                                    val videoUrl = URL(getMediaLink() + imgurl.get(i).media)
                                    val conc = videoUrl.openConnection() as HttpURLConnection
                                    conc.doInput = true
                                    conc.connect()
                                    val inst: InputStream = conc.inputStream
                                    var buffer: ByteArray = ByteArray(1024)
                                    var len1: Int = 0
                                    while ((inst.read(buffer).also({ len1 = it })) > 0) {
                                        outputStream?.write(buffer, 0, len1)
                                    }
                                    outputStream?.close()

                                    if (uri != null) {
                                        Log.e("videourl", "videolocal path: " + uri!!.toString())
                                        videoUriArray.add(uri!!)
                                        imageUrifb.add(uri!!)
                                        imageUriGmail.add(uri!!)
                                    }

                                } catch (e: java.lang.Exception) {
                                    Log.e("videourl", "catch2:")
                                    Log.e("videourl", "catch2:" + e.message)
                                }
                            }


                        } else {
                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                                val filenamesplit = imgurl.get(i).media.split("/").toTypedArray()
                                val size = filenamesplit.size


                                val ffidir = File(Environment.getExternalStorageDirectory(), "FFI")
                                if (!ffidir.exists()) {
                                    ffidir.mkdir()
                                }
                                val mypath = File(ffidir, id + "_" +
                                        size.minus(1).let { filenamesplit.get(it) })

                                if (!mypath.exists()) {
                                    val outputStream =
                                        FileOutputStream(
                                            java.lang.String.valueOf(mypath)
                                        )
                                    val bitmap =
                                        getBitmapFromURL(getMediaLink() + imgurl.get(i).media)
                                    bitmap?.compress(
                                        Bitmap.CompressFormat.JPEG, 90, outputStream
                                    )
                                    outputStream.close()
                                }

                                imageUriArray.add(Uri.parse(mypath.toString()))
                                imageUrifb.add(Uri.fromFile(mypath))
                                imageUriGmail.add(Uri.parse("content://" + mypath.absolutePath))
                            } else {
                                val filenamesplit = imgurl.get(i).media.split("/").toTypedArray()
                                val size = filenamesplit.size

                                val values = ContentValues()
                                val fileName =
                                    id + "_" + size?.minus(1)?.let { filenamesplit.get(it) }

                                values.put(
                                    MediaStore.MediaColumns.DISPLAY_NAME,
                                    fileName
                                )

                                values.put(
                                    MediaStore.MediaColumns.MIME_TYPE,
                                    "image/jpeg"
                                )

                                values.put(
                                    MediaStore.MediaColumns.RELATIVE_PATH,
                                    Environment.DIRECTORY_PICTURES + "/FFI_Images/"
                                )


                                val uri: Uri? = requireActivity().contentResolver.insert(
                                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                    values!!
                                ) //important!

                                try {
                                    val outputStream: OutputStream? =
                                        requireActivity().contentResolver.openOutputStream(uri!!)
                                    val bitmap =
                                        getBitmapFromURL(getMediaLink() + imgurl.get(i).media)
                                    bitmap?.compress(
                                        Bitmap.CompressFormat.JPEG, 90, outputStream
                                    )
                                    outputStream?.close()

                                } catch (e: java.lang.Exception) {
                                    Log.e("tagUri", e.message.toString())
                                }

                                if (uri != null) {
                                    imageUriArray.add(uri!!)
                                    imageUrifb.add(uri!!)
                                    imageUriGmail.add(uri!!)
                                }
                            }
                        }
                    }
                } else {
                    if (this@HomeFragment::btnDone.isInitialized) {
                        btnDone.visibility = View.VISIBLE
                        liDownloads.visibility = View.GONE
                    }
                }
                return ""
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return ""
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            if (isDownloadonly) {
                activity?.showToast(getString(R.string.msg_imges_downloaded), Const.TOAST)
                if (dialog.isShowing)
                    dialog.cancel()
            }

            if (this@HomeFragment::btnDone.isInitialized) {
                btnDone.visibility = View.VISIBLE
                liDownloads.visibility = View.GONE
            }
        }
    }


    fun DialogShare(model: RecordXXX, sourceapp: String) {
        Log.e("share", "call method DialogShare")
        if (isDialogOpen)
            return
        isDialogOpen = true
        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            isDialogOpen = false
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                WRITE_PERMISSION
            )
        } else {


            var hasAnyVideo = false
            for (mediafile in model.media) {
                if (mediafile.mediaTypeId.equals(Const.MEDIA_VIDEO)) {
                    hasAnyVideo = true
                }
            }

            var hasAnyImage = false
            for (mediafile in model.media) {
                if (mediafile.mediaTypeId.equals(Const.MEDIA_IMAGE)) {
                    Log.e("hasImage", mediafile.media!!)
                    hasAnyImage = true
                }
            }

            imageUriArray.clear()
            imageUrifb.clear()
            videoUriArray.clear()

            val dialog = Dialog(requireActivity())
            val window = dialog.window
            window!!.setLayout(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            window.setGravity(Gravity.CENTER)
            dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
            dialog.setContentView(R.layout.dailog_share)

            val ivclose = dialog.findViewById<ImageView>(R.id.ivclose)
            btnDone = dialog.findViewById<TextView>(R.id.btn_done)
            liDownloads = dialog.findViewById(R.id.li_downloading)


            val tvdesc = dialog.findViewById<TextView>(R.id.tvDesc)
            val tvshareimg = dialog.findViewById<TextView>(R.id.tvshareimg)
            val tvaddmargin = dialog.findViewById<TextView>(R.id.tvaddmargin)
            val tvsharevideo = dialog.findViewById<TextView>(R.id.tvsharevideo)


            val rdDesc = dialog.findViewById<CheckBox>(R.id.rdDesc)
            val rdimg = dialog.findViewById<CheckBox>(R.id.rdimage)
            val rdmargin = dialog.findViewById<CheckBox>(R.id.rdmargin)
            val rdvideo = dialog.findViewById<CheckBox>(R.id.rdvideo)
            var edt_margin = dialog.findViewById<EditText>(R.id.edt_margin)

            val llSaheVideo = dialog.findViewById<LinearLayout>(R.id.llSaheVideo)

            if (hasAnyVideo) {
                llSaheVideo.visibility = View.VISIBLE
                // tvshareimg.text=getString(R.string.title_share_imageVideo)
            } else {
                llSaheVideo.visibility = View.GONE
                //  tvshareimg.text=getString(R.string.title_share_image)
            }


            val llShareImage = dialog.findViewById<LinearLayout>(R.id.llShareImage)
            if (hasAnyImage) {
                llShareImage.visibility = View.VISIBLE
            } else {
                llShareImage.visibility = View.GONE
            }

            tvdesc.setOnClickListener {
                rdDesc.isChecked = !rdDesc.isChecked
            }

            tvshareimg.setOnClickListener {
                rdimg.isChecked = !rdimg.isChecked
            }

            tvaddmargin.setOnClickListener {
                rdmargin.isChecked = !rdmargin.isChecked
            }
            tvsharevideo?.setOnClickListener {
                rdvideo?.isChecked = !rdvideo.isChecked
            }

            rdDesc.setOnCheckedChangeListener { buttonView, isChecked ->
                sharedesc = ""

                if (isChecked) {
                    sharedesc = model.title + "\n" +
                            model.description
                }
            }


            btnDone.setOnClickListener {
                Log.e("share", "click button share done")
                if (!rdimg.isChecked && !rdDesc.isChecked && !rdvideo.isChecked) {
                    if (hasAnyImage && !hasAnyVideo) {
                        activity?.showToast(getString(R.string.err_select_to_share), Const.TOAST)
                    } else if (!hasAnyImage && hasAnyVideo) {
                        activity?.showToast(getString(R.string.err_select_to_share3), Const.TOAST)
                    } else {
                        activity?.showToast(getString(R.string.err_select_to_share2), Const.TOAST)
                    }
                    return@setOnClickListener
                }

                if (rdimg.isChecked) {
                    if (imageUriArray.size <= 0) {
                        activity?.showToast(getString(R.string.err_img_dwing), Const.TOAST)
                        return@setOnClickListener
                    }
                }
                CallApiSharedData(model.iD)
                if (rdmargin.isChecked) {
                    val strmargin = edt_margin.text.toString()
                    if (strmargin.isEmpty()) {
                        activity?.showToast(getString(R.string.err_enter_margin), Const.TOAST)
                        return@setOnClickListener
                    } else {
                        sharedesc += "\n" + "*Price " + getString(R.string.currency) + " " + (strmargin.toFloat() + model.price.replace(
                            ",",
                            ""
                        ).toFloat()) + "*"
                    }
                }

                if (rdDesc.isChecked) {
                    val clipboard: ClipboardManager =
                        activity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip: ClipData = ClipData.newPlainText("copy", sharedesc)
                    clipboard.setPrimaryClip(clip)
                    activity?.showBottomToast(getString(R.string.desc_copy_msg))
                }


                if (sourceapp.equals(Const.WHATSAPP)) {
                    if (rdimg.isChecked) {
                        ShareOnWhatsapp(true, rdvideo.isChecked)
                    } else {
                        ShareOnWhatsapp(false, rdvideo.isChecked)
                    }
                } else if (sourceapp.equals(Const.FACEBOOK)) {
                    ShareOnFacebook(rdimg.isChecked,rdvideo.isChecked)
                } else if (sourceapp.equals(Const.OTHERS)) {
                    ShareOnAnyApp(model,rdimg.isChecked, rdvideo.isChecked)
                }
                isDialogOpen = false
                dialog.cancel()
            }

            ivclose.setOnClickListener {
                isDialogOpen = false
                dialog.dismiss()
            }
            dialog.setCancelable(false)
            dialog.setCanceledOnTouchOutside(false)
            dialog.show()
            DownloadTask(
                model.media,
                model.iD,
                false
            ).execute()

        }
    }

    private fun CallApiSharedData(id: String) {
        if (isInternetAvailable(requireActivity())) {
            val param = ParamAddShareddata().apply {
                referenceId = id
                typeId = Const.PRODUCT_SHARE
            }
            val retrofit = WebService.getRetrofit(requireActivity()).create(ApiClient::class.java)
            retrofit.AddSharedProducts(param)
                .enqueue(object : Callback<ResponseAddSharedData> {
                    override fun onFailure(call: Call<ResponseAddSharedData>, t: Throwable) {
                    }

                    override fun onResponse(
                        call: Call<ResponseAddSharedData>,
                        response: Response<ResponseAddSharedData>?
                    ) {
                        mprogressbar.hideProgressBar()
                    }
                })
        } else {
            NetworkErrorDialog(requireActivity())
        }
    }

    private fun ShareOnWhatsapp(isimg: Boolean, isVideo: Boolean) {

        val baseActivity = BaseActivity()
        val isWhatsUpInstalled =
            baseActivity.isAppInstalled(context, getString(R.string.pckg_whtsapp))
        val isWhatsUpBusinessInstalled =
            baseActivity.isAppInstalled(context, getString(R.string.pckg_whtsapp_business))

        if (isWhatsUpInstalled) {
            shareDataSocial(getString(R.string.pckg_whtsapp), isimg, isVideo)
        } else if (isWhatsUpBusinessInstalled) {
            shareDataSocial(getString(R.string.pckg_whtsapp_business), isimg, isVideo)
        } else {
            activity?.showToast(getString(R.string.whatsapp_no_installed), Const.ALERT)
        }
    }

    private fun shareDataSocial(pckgName: String, isimg: Boolean, isVideo: Boolean) {
        Log.e("////", "pckgName//" + pckgName)
        val whatsappIntent = Intent(Intent.ACTION_SEND)
        whatsappIntent.setPackage(pckgName)
        activity?.showBottomToast(getString(R.string.msg_desc_copeied))
        try {

            if (isimg && !isVideo) {
                //  whatsappIntent.type = "image/png"
                whatsappIntent.type = "*/*"
                whatsappIntent.putExtra(Intent.EXTRA_STREAM, imageUriArray)
                whatsappIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                startActivityForResult(whatsappIntent, SHARE_DESC)
            }

            if (!isimg && isVideo) {
                //  whatsappIntent.type = "image/png"
                whatsappIntent.type = "*/*"
                whatsappIntent.putExtra(Intent.EXTRA_STREAM, videoUriArray)
                whatsappIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                startActivityForResult(whatsappIntent, SHARE_DESC)
            } else if (isimg && isVideo) {
                //  whatsappIntent.type = "image/png"
                bothUriArray.clear()
                bothUriArray.addAll(imageUriArray)
                bothUriArray.addAll(videoUriArray)
                whatsappIntent.type = "*/*"
                whatsappIntent.putExtra(Intent.EXTRA_STREAM, bothUriArray)
                whatsappIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                startActivityForResult(whatsappIntent, SHARE_DESC)
            } else {
                whatsappIntent.type = "text/plain"
                whatsappIntent.putExtra(Intent.EXTRA_TEXT, sharedesc)
                activity?.startActivity(whatsappIntent)
            }
        } catch (ex: ActivityNotFoundException) {
            activity?.showToast(getString(R.string.whatsapp_no_installed), Const.ALERT)
        }

    }

    fun getBitmapFromURL(src: String?): Bitmap? {
        return try {
            val url = URL(src)
            val connection =
                url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input = connection.inputStream
            BitmapFactory.decodeStream(input)
        } catch (e: IOException) {
            // Log exception
            null
        }
    }

    fun ShareOnFacebook(isimg: Boolean,isVideo: Boolean) {
        try {
            var sharePhoto: SharePhoto
            var tempUriArray: ArrayList<Uri> = ArrayList<Uri>()
            var arrphotos: ArrayList<ShareMedia> = ArrayList()
            if (isimg && !isVideo) {
               /* if (alreadyDownload) {
                    tempUriArray = imageUriArray


                } else {
                    if (productdata != null && productdata?.Media != null &&
                        imageUriArrayGlide.size == productdata?.Media?.size
                    ) {
                        tempUriArray = imageUriArrayGlide
                    } else {
                        tempUriArray = imageUriArray
                    }
                }*/
                tempUriArray = imageUriArray
                for (i in tempUriArray.indices) {
                    sharePhoto = SharePhoto.Builder()
                        .setImageUrl(tempUriArray.get(i))
                        .build()
                    arrphotos.add(sharePhoto)
                }

            } else if (!isimg && isVideo) {
                tempUriArray = videoUriArray

                for (i in tempUriArray.indices) {
                    var shareVideo: ShareVideo = ShareVideo.Builder()
                        .setLocalUrl(tempUriArray.get(i))
                        .build()
                    arrphotos.add(shareVideo)
                }

            } else if (isVideo && isimg) {

                for (i in imageUriArray.indices) {
                    sharePhoto = SharePhoto.Builder()
                        .setImageUrl(imageUriArray.get(i))
                        .build()
                    arrphotos.add(sharePhoto)
                }

                for (i in videoUriArray.indices) {
                    var shareVideo: ShareVideo = ShareVideo.Builder()
                        .setLocalUrl(videoUriArray.get(i))
                        .build()
                    arrphotos.add(shareVideo)
                }

            }

            /* for (i in imageUriArray.indices) {
                 val bitmap: Bitmap = MediaStore.Images.Media.getBitmap(
                     activity?.contentResolver,
                     imageUrifb.get(i)
                 )
                 if (i == 0) {
                     sharePhoto = SharePhoto.Builder()
                         .setBitmap(bitmap)
                         .setCaption(sharedesc)
                         .build()
                 } else {
                     sharePhoto = SharePhoto.Builder()
                         .setBitmap(bitmap)
                         .build()
                 }
                 arrphotos.add(sharePhoto)

             }*/


            val shareContent = ShareMediaContent.Builder()
                .addMedia(arrphotos as List<ShareMedia>?)
                .build()

            activity?.showBottomToast(getString(R.string.msg_desc_copeied))
            shareDialog.show(shareContent)
        } catch (e: IndexOutOfBoundsException) {
            Log.e("sharefb", "exe")
            Log.e("sharefb", "exe: " + e.message!!)
            e.printStackTrace()
        }
    }

    fun ShareOnAnyApp(model: RecordXXX,isimg: Boolean,isVideo: Boolean) {
        val shareIntent = Intent(Intent.ACTION_SEND_MULTIPLE)
        shareIntent.action = Intent.ACTION_SEND_MULTIPLE
        shareIntent.type = "text/plain"

        //    shareIntent.type = "image/*"
        shareIntent.type = "*/*"
        val arrUri: ArrayList<Uri> = ArrayList()

       /* try {
            if(activity==null){
                Log.e("shareother","activity null")
            }
            if(  activity?.contentResolver==null){
                Log.e("shareother","activity contentResolver null")
            }
            if(imageUrifb==null){
                Log.e("shareother","imageUrifb null")
            }


            for (i in imageUrifb.indices) {
                val bitmap: Bitmap = MediaStore.Images.Media.getBitmap(
                    activity?.contentResolver,
                    imageUrifb.get(i)
                )


                val os = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os)

                val path = MediaStore.Images.Media.insertImage(
                    requireContext().contentResolver, bitmap, null, null
                )
                arrUri.add(Uri.parse(path))
            }
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        } catch (e: NullPointerException) {
            e.printStackTrace()
        }*/
//arrUri
        if(isimg && !isVideo)
        {
            shareIntent.putExtra(Intent.EXTRA_STREAM, imageUriArray)
        } else if(!isimg && isVideo){
            shareIntent.putExtra(Intent.EXTRA_STREAM, videoUriArray)
        }else if(isimg && isVideo){
            bothUriArray.clear()
            bothUriArray.addAll(imageUriArray)
            bothUriArray.addAll(videoUriArray)
            shareIntent.putExtra(Intent.EXTRA_STREAM, bothUriArray)
        }
        shareIntent.putExtra(Intent.EXTRA_TEXT, sharedesc)
        shareIntent.putExtra(
            Intent.EXTRA_TITLE,
            requireActivity().getString(R.string.app_name)
        )
        shareIntent.putExtra(
            Intent.EXTRA_SUBJECT,
            requireActivity().getString(R.string.app_name) + " Product " + model.title
        )


        val openInChooser = Intent.createChooser(shareIntent, getString(R.string.title_share_with))
        requireActivity().startActivity(openInChooser)

    }
    ///////////////////


    fun loadCollectionItems(id: String, name: String) {
        activity?.hideKeyboard(edt_search)
        val fragment = CollectionFragment().getInstance(id, name)

        homeActivity?.deselectAllMenu()
        homeActivity?.supportFragmentManager
            ?.beginTransaction()
            ?.add(R.id.cm_fragmentContainer, fragment, fragment::class.java.name)
            ?.addToBackStack(null)
            ?.commit()

    }

    fun CallApiSearchProduct(searchtxt: String, isprogress: Boolean) {
        realm!!.executeTransaction { realm ->
            val maxValue: Number? = realm.where(SearchHome::class.java).max("id")
            val id = if (maxValue != null) maxValue.toInt() + 1 else 1
            val searchAdapter: SearchHome = realm.createObject(SearchHome::class.java, id)
            if (searchAdapter.equals(searchtxt)) {
                Log.e("SearchActivity", "onQueryTextSubmit==if==$searchtxt")
            } else {
                searchAdapter.setsearchHistory(searchtxt)
                searchAdapter.setuserId(getUserId())
                realm.copyToRealmOrUpdate(searchAdapter)
            }
        }

        if (isInternetAvailable(requireActivity())) {
            if (currentPageNo == 1) {
                mprogressbar.showProgressBar()
            }
            val param = ParamProductSearch().apply {
                limit = Const.PAGINATION_LIMIT
                page = currentPageNo.toString()
                name = searchtxt
            }

            val retrofit = WebService.getRetrofit(requireActivity()).create(ApiClient::class.java)
            retrofit.GetProductSearch(param)
                .enqueue(object : Callback<ResponseProductSearch> {
                    override fun onFailure(call: Call<ResponseProductSearch>, t: Throwable) {
                        Log.e("home", "Onfailer")
                        mprogressbar.hideProgressBar()
                        swf_home?.isRefreshing = false
                    }

                    override fun onResponse(
                        call: Call<ResponseProductSearch>,
                        response: Response<ResponseProductSearch>?
                    ) {
                        swf_home?.isRefreshing = false
                        mprogressbar.hideProgressBar()
                        if (response != null) {
                            if (response.isSuccessful) {
                                val responseproduct = response.body()
                                if (responseproduct != null) {
                                    Log.e("res", "line no 572 - " + responseproduct.status)

                                    if (responseproduct.status == API_SUCESS) {
                                        if (currentPageNo == 1) {
                                            if (fi_search_data != null)
                                                fi_search_data.visibility = View.VISIBLE
                                            if (rvProducts != null)
                                                rvProducts.visibility = View.VISIBLE
                                            if (liNoproduct != null)
                                                liNoproduct.visibility = View.GONE
                                        }
                                        currentPagedatasize =
                                            responseproduct.data.records.size.toString()
                                        val lastsize = datasearch.size
                                        datasearch.addAll(responseproduct.data.records)

                                        if (currentPageNo == 1) {
                                            Log.e("//////here", "////if")
                                            setData()
                                        } else {
                                            Log.e("//////here", "////else")

                                            mSearchProductAdapter?.notifyItemRangeInserted(
                                                lastsize + 1,
                                                currentPagedatasize.toInt()
                                            )
                                            mSearchProductAdapter?.notifyItemRangeChanged(
                                                lastsize + 1,
                                                currentPagedatasize.toInt()
                                            )
                                        }
                                    } else if (responseproduct?.status == API_DATA_NOT_AVAILBLE) {
                                        Log.e("res", "line no 594")
                                        if (currentPageNo == 1) {
                                            Log.e("res", "line no 596")
                                            rvProducts?.visibility = View.GONE
                                            fi_search_data?.visibility = View.VISIBLE
                                            liNoproduct?.visibility = View.VISIBLE
                                        }
                                    }
                                }
                            }
                        } else {
                            requireActivity().showToast(
                                getString(R.string.msg_common_error),
                                Const.ALERT
                            )
                        }
                    }
                })
        } else {
            NetworkErrorDialog(requireActivity())
            swf_home?.isRefreshing = false
        }
    }

    private fun setData() {

        if (isAdded) {
            Log.e("//////here", "////isAdded")

            rvProducts.apply {
                layoutManager = LinearLayoutManager(context)
                mSearchProductAdapter = SearchProductAdapter(this@HomeFragment, datasearch)
                adapter = mSearchProductAdapter
            }

            val displayMetrics = DisplayMetrics()
            activity?.windowManager?.defaultDisplay?.getMetrics(displayMetrics)
            val screenWidth: Int = displayMetrics.widthPixels

            if (screenWidth > 0) {
                val m60PercentScreenWidth = (screenWidth * 56) / 100
                val m40PercentScreenWidth = (screenWidth * 36) / 100
                mSearchProductAdapter?.mFirstWidth = m60PercentScreenWidth
                mSearchProductAdapter?.mSecondWidth = m40PercentScreenWidth
                mSearchProductAdapter?.notifyDataSetChanged()
            }

        } else {
            Log.e("//////here", "////not added")

        }
    }

    fun CallApiAddWishList(WishStatus: String, productid: String) {
        val param = ParamAddREmoveWishItem().apply {
            productId = productid
            status = WishStatus
        }
        val retrofit = WebService.getRetrofit(requireActivity()).create(ApiClient::class.java)
        retrofit.AddRemoveWishItem(param)
            .enqueue(object : Callback<ResponseAddRemoveWishItem> {
                override fun onFailure(call: Call<ResponseAddRemoveWishItem>, t: Throwable) {
                }

                override fun onResponse(
                    call: Call<ResponseAddRemoveWishItem>,
                    response: Response<ResponseAddRemoveWishItem>?
                ) {
                    if (response != null) {
                        if (response.isSuccessful) {
                            val responseproduct = response.body()
                            if (responseproduct != null) {
                                if (responseproduct.status == API_SUCESS) {
                                    //  showToast(getString(R.string.msg_product_add_cart))
                                    if (WishStatus.equals(Const.AVAILBLE_IN_WISH)) {
                                        activity?.showToast(
                                            getString(R.string.msg_add_wish),
                                            Const.TOAST
                                        )
                                    } else {
                                        activity?.showToast(
                                            getString(R.string.msg_remove_wish),
                                            Const.TOAST
                                        )
                                    }
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
    }


    private fun setUiPageViewController() {
        dotsCount = mAdapter?.count!!
        dots = arrayOfNulls<ImageView>(this.dotsCount)

        countdots.removeAllViews()

        for (i in 0 until dotsCount) {
            dots[i] = ImageView(requireActivity())
            dots[i]?.setImageDrawable(resources.getDrawable(R.drawable.banner_nonselecteditem_dot))

            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(4, 0, 4, 0)

            countdots.addView(dots[i], params)
        }
        if (dots.size >= 1)
            dots[0]?.setImageDrawable(resources.getDrawable(R.drawable.banner_selecteditem_dot))

        val handler = Handler()
        val Update = Runnable {
            if (currentPageForScrollNo == mImageResources.size) {
                currentPageForScrollNo = 0
            }
            if (vp_banner != null) {
                vp_banner.setCurrentItem(currentPageForScrollNo++, true)
            }
        }

        timer = Timer() // This will create a new Thread
        timer!!.schedule(object : TimerTask() {
            override fun run() {
                handler.post(Update)
            }
        }, DELAY_MS, PERIOD_MS)
    }

    override fun onPageScrollStateChanged(state: Int) {
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
    }

    override fun onPageSelected(position: Int) {
        for (i in 0 until dotsCount) {
            dots[i]?.setImageDrawable(resources.getDrawable(R.drawable.banner_nonselecteditem_dot))
        }

        dots[position]?.setImageDrawable(resources.getDrawable(R.drawable.banner_selecteditem_dot))
    }


    fun dpToPx(dp: Int): Int {
        val displayMetrics: DisplayMetrics = activity?.resources?.displayMetrics!!
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)).toInt()
    }


    fun BannerRedirection(position: Int) {
        activity?.hideKeyboard(edt_search)
        val bannerdata = mImageResources.get(position).LinkParams
        for (i in bannerdata.indices) {
            val param = bannerdata.get(i).ParameterName

            if (param.equals("productId")) {
                loadProductDetail(bannerdata.get(i).ParamValue)
                break
            } else if (param.equals("categoryId") && bannerdata.get(i).ParentCategoryId.toString()
                    .equals("0")
            ) {
                loadProductCatalog(id.toString(), mImageResources.get(position).Title)
                //  loadCategoryFragment(false, bannerdata.get(i).ParamValue, "catlog", mImageResources.get(position).Title)
                break
            } else if (param.equals("categoryId") && !bannerdata.get(i).ParentCategoryId.toString()
                    .equals("0")
            ) {
                loadCategoryFragment(false, bannerdata.get(i).ParentCategoryId.toString(), "", "")
                break
            }
        }

    }

    private fun loadProductCatalog(id: String, title: String) {
        activity?.hideKeyboard(edt_search)
        val fragment = ProductListFragment().getInstance(id, title)
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

    override fun onStop() {
        super.onStop()
        if (getUserId().isEmpty())
            App.isShowProgressbar = false
    }

    private fun showRecentSearchList() {
        realm!!.executeTransaction(object : Realm.Transaction {
            val searchAdapters = realm!!.where(SearchHome::class.java)
                .findAllSorted("id", Sort.DESCENDING)
                .where()
                .distinct("searchHistory")

            override fun execute(realm: Realm) {

                Log.e("searchAdapters_", searchAdapters.size.toString() + "")

                if (searchAdapters.size > 0) {
                    searchAdapters[0].setuserId(getUserId())
                    as_tvClearAll?.visibility = View.VISIBLE
                } else {
                    as_tvClearAll?.visibility = View.GONE
                }
                li_serachistory?.visibility = View.VISIBLE
                li_home_data?.visibility = View.GONE
                fi_search_data?.visibility = View.GONE
                /* } else {
                     li_serachistory?.visibility = View.GONE
                     li_home_data?.visibility = View.VISIBLE
                     // fi_search_data?.visibility = View.VISIBLE
                 }*/
                searchList = searchAdapters
                Log.e("searchAdapters_", searchList.toString())

                rv_search?.apply {
                    layoutManager =
                        LinearLayoutManager(context)
                    searchAdapter = SearchHomeAdapter(searchList)
                    adapter = searchAdapter
                }
                searchAdapter.setOnItemClickListener(object : SearchHomeAdapter.MyClickListener {
                    override fun onItemClick(position: Int, v: View) {
                        when (v.id) {
                            R.id.search_text -> {
                                datasearch.clear()
                                datasearch = ArrayList()
                                mSearchProductAdapter?.notifyDataSetChanged()
                                activity?.hideKeyboard(edt_search)
                                edt_search.setText(searchList.get(position).getsearchHistory())
                                edt_search.setSelection(
                                    searchList.get(position).getsearchHistory().length
                                )
                                val searchtext = edt_search.text.toString().trim()
                                ivSearch.visibility = View.GONE
                                rvProducts?.visibility = View.GONE
                                liNoproduct?.visibility = View.GONE
                                fi_search_data.visibility = View.VISIBLE
                                li_serachistory?.visibility = View.GONE
                                CallApiSearchProduct(searchtext, false)
                            }

                        }
                    }
                })
            }
        })
    }

    private fun setScrollListener() {
        scrollListener = object : EndlessRecyclerViewScrollListener(mLayoutManager) {
            override fun onLoadMore(page: Int) {

                if (currentPagedatasize.equals(Const.PAGINATION_LIMIT)) {
                    currentPageNo++
                    Log.e("scroll", "onLoadMore" + currentPagedatasize + " Page no" + currentPageNo)
                    val searchtext = edt_search.text.toString().trim()
                    CallApiSearchProduct(searchtext, false)
                }
            }
        }
        rvProducts?.addOnScrollListener(scrollListener)
    }
}

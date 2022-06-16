package com.ffi.productlist

import android.Manifest
import android.app.Dialog
import android.app.ProgressDialog
import android.content.*
import android.os.*
import java.io.*
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
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
import com.ffi.HomeActivity
import com.ffi.R
import com.ffi.Utils.*
import com.ffi.Utils.Const.Companion.RESULT_FILTER
import com.ffi.filter.FilterActivity
import com.ffi.filter.ParamFilterResult
import com.ffi.filter.ResponseFilterResultProduct
import com.ffi.model.ParamAddShareddata
import com.ffi.model.ResponseAddSharedData
import com.ffi.model.sortby.ParamSortby
import com.ffi.model.sortby.ResponseSortbyinproduct
import com.ffi.productdetail.ProductDetailFragment
import com.ffi.productlist.topproduct.ParamGetProduct
import com.ffi.productlist.topproduct.ResponseGetTopProduct
import com.ffi.api.ApiClient
import com.ffi.api.WebService
import kotlinx.android.synthetic.main.custom_toolbar.*
import kotlinx.android.synthetic.main.fragment_catalog.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class ProductListFragment() : Fragment(), View.OnClickListener {
    var id: String? = null
    var title: String? = null

    fun getInstance(id: String, name: String): ProductListFragment {
        val mProductListFragment = ProductListFragment()
        val args = Bundle()
        args.putString(Const.ID, id)
        args.putString(Const.NAME, name)
        mProductListFragment.setArguments(args)
        return mProductListFragment
    }

    private fun getArgumentData() {
        if (arguments != null) {
            if (arguments?.containsKey(Const.ID) != null &&
                arguments?.containsKey(Const.ID)!!
            ) {
                id = arguments?.getString(Const.ID)
            }
            if (arguments?.containsKey(Const.NAME) != null &&
                arguments?.containsKey(Const.NAME)!!
            ) {
                title = arguments?.getString(Const.NAME)
            }
        }
    }

    private val WRITE_PERMISSION = 0
    private val SHARE_DESC = 10

    private var isDialogOpen = false
    var sharedesc = ""
    var arrProducts: ArrayList<Record> = ArrayList()

    lateinit var catalog_list: CatalogAdapter
    var homeActivity: HomeActivity? = null

    var imageUriArray: ArrayList<Uri> = ArrayList<Uri>()
    var videoUriArray: ArrayList<Uri> = ArrayList<Uri>()
    var bothUriArray: ArrayList<Uri> = ArrayList<Uri>()
    var imageUrifb: ArrayList<Uri> = ArrayList<Uri>() //for facebook share
    var imageUriGmail: ArrayList<Uri> = ArrayList<Uri>() //for facebook share

    lateinit var mprogressbar: ProgressBarHandler

    var currentPageNo = 1
    var currentPagedatasize = ""
    lateinit var scrollListener: EndlessRecyclerViewScrollListener
    lateinit var mLayoutManager: LinearLayoutManager

    lateinit var shareDialog: ShareDialog
    lateinit var callbackManager: CallbackManager

    lateinit var btnDone: TextView
    lateinit var liDownloads: LinearLayout

    var strFilterdata = ""
    var strSortbyId = "0"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FacebookSdk.sdkInitialize(requireActivity().applicationContext)
        callbackManager = CallbackManager.Factory.create()
        shareDialog = ShareDialog(this)
        shareDialog.registerCallback(callbackManager, callback, 100)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_catalog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mprogressbar = ProgressBarHandler(requireActivity())
        mLayoutManager = LinearLayoutManager(context)

        homeActivity = activity as HomeActivity

        getArgumentData()

        //homeActivity.header.visibility = View.VISIBLE
        txt_toolbar.text = title
        ivBack.visibility = View.VISIBLE



        setListener()

        currentPageNo = 1
        setScrollListener()
        setRefresh()
        if (id.equals("-1")) {
            tvshareproducts.visibility = View.GONE

            CallApiTopProduct(true)
        } else {

            CallApiProductList(true)

        }
    }

    private fun setScrollListener() {
        scrollListener = object : EndlessRecyclerViewScrollListener(mLayoutManager) {
            override fun onLoadMore(page: Int) {

                if (currentPagedatasize.equals(Const.PAGINATION_LIMIT)) {
                    currentPageNo++
                    Log.e("scroll", "onLoadMore" + currentPagedatasize + " Page no" + currentPageNo)
                    if (id.equals("-1")) {
                        tvshareproducts.visibility = View.GONE
                        if (!strFilterdata.isEmpty()) {
                            CallApiFilter(strFilterdata)
                        } else if (!strSortbyId.equals("0")) {
                            CallApiSortby(strSortbyId)
                        } else {
                            CallApiTopProduct(false)
                        }
                    } else {
                        if (!strFilterdata.isEmpty()) {
                            CallApiFilter(strFilterdata)
                        } else if (!strSortbyId.equals("0")) {
                            CallApiSortby(strSortbyId)
                        } else {
                            CallApiProductList(false)
                        }
                    }

                    Log.e("scroll", "onLoadMore true")
                }
                Log.e("scroll", "onLoadMore false")
            }
        }
        rvCatalog?.addOnScrollListener(scrollListener)
    }

    private fun setRefresh() {
        swf_productlist?.setOnRefreshListener {
            currentPageNo = 1
            if (id.equals("-1")) {
                tvshareproducts.visibility = View.GONE
                if (!strFilterdata.isEmpty()) {
                    CallApiFilter(strFilterdata)
                } else if (!strSortbyId.equals("0")) {
                    CallApiSortby(strSortbyId)
                } else {
                    CallApiTopProduct(false)
                }
            } else {
                if (!strFilterdata.isEmpty()) {
                    CallApiFilter(strFilterdata)
                } else if (!strSortbyId.equals("0")) {
                    CallApiSortby(strSortbyId)
                } else {
                    CallApiProductList(false)
                }
            }
        }
    }

    private fun CallApiTopProduct(isprogress: Boolean) {
        if (isInternetAvailable(requireActivity())) {

            if (isprogress)
                mprogressbar.showProgressBar()

            val param = ParamGetProduct().apply {
                page = currentPageNo.toString()
                limit = Const.PAGINATION_LIMIT
            }
            val retrofit = WebService.getRetrofit(requireActivity()).create(ApiClient::class.java)
            retrofit.GetTopProduct(param)
                .enqueue(object : Callback<ResponseGetTopProduct> {
                    override fun onFailure(call: Call<ResponseGetTopProduct>, t: Throwable) {
                        mprogressbar.hideProgressBar()
                        swf_productlist?.isRefreshing = false
                    }

                    override fun onResponse(
                        call: Call<ResponseGetTopProduct>,
                        response: Response<ResponseGetTopProduct>?
                    ) {
                        mprogressbar.hideProgressBar()
                        swf_productlist?.isRefreshing = false

                        if (response != null) {
                            if (response.isSuccessful) {
                                val responseproductlist = response.body()
                                if (responseproductlist != null) {
                                    if (responseproductlist.status == API_SUCESS) {
                                        if (currentPageNo == 1) {
                                            arrProducts = ArrayList()
                                            rvCatalog?.visibility = View.VISIBLE
                                            tvNoproduct?.visibility = View.GONE
                                        }
                                        currentPagedatasize =
                                            responseproductlist.data.records.size.toString()
                                        val lastsize = arrProducts.size
                                        arrProducts.addAll(responseproductlist.data.records)
                                        Log.e("data", arrProducts.toString())
                                        if (currentPageNo == 1) {
                                            setData()
                                        } else {
                                            Log.e("data", arrProducts.toString() + " update")
                                            catalog_list.notifyItemRangeInserted(
                                                lastsize + 1,
                                                currentPagedatasize.toInt()
                                            )
                                            catalog_list.notifyItemRangeChanged(
                                                lastsize + 1,
                                                currentPagedatasize.toInt()
                                            )
                                        }
                                    } else if (responseproductlist.status == API_DATA_NOT_AVAILBLE) {
                                        if (currentPageNo == 1) {
                                            rvCatalog?.visibility = View.GONE
                                            tvNoproduct?.visibility = View.VISIBLE
                                        }
                                        currentPagedatasize = "0"
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
            swf_productlist?.isRefreshing = false
        }
    }

    private fun setListener() {
        tvshareproducts.setOnClickListener(this)
        rl_sortby.setOnClickListener(this)
        ivBack.setOnClickListener(this)
        tvrefine.setOnClickListener(this)
    }

    fun CallApiProductList(isprogress: Boolean) {
        if (isInternetAvailable(requireActivity())) {
            if (isprogress)
                mprogressbar.showProgressBar()

            val param = ParamProductList().apply {
                categoryId = id!!
                page = currentPageNo.toString()
                limit = Const.PAGINATION_LIMIT
            }
            val retrofit = WebService.getRetrofit(requireActivity()).create(ApiClient::class.java)
            retrofit.GetProductList(param)
                .enqueue(object : Callback<ResponsnsProductList> {
                    override fun onFailure(call: Call<ResponsnsProductList>, t: Throwable) {
                        mprogressbar.hideProgressBar()
                        swf_productlist?.isRefreshing = false
                    }

                    override fun onResponse(
                        call: Call<ResponsnsProductList>,
                        response: Response<ResponsnsProductList>?
                    ) {
                        mprogressbar.hideProgressBar()
                        swf_productlist?.isRefreshing = false

                        if (response != null) {
                            if (response.isSuccessful) {
                                val responseproductlist = response.body()

                                if (responseproductlist != null) {
                                    if (responseproductlist.status == API_SUCESS) {
                                        if (currentPageNo == 1) {
                                            arrProducts = ArrayList()
                                            rvCatalog?.visibility = View.VISIBLE
                                            tvNoproduct?.visibility = View.GONE
                                        }
                                        currentPagedatasize =
                                            responseproductlist.data.records.size.toString()
                                        val lastsize = arrProducts.size
                                        arrProducts.addAll(responseproductlist.data.records)
                                        if (currentPageNo == 1) {
                                            setData()
                                        } else {
                                            catalog_list.notifyItemRangeInserted(
                                                lastsize + 1,
                                                currentPagedatasize.toInt()
                                            )
                                            catalog_list.notifyItemRangeChanged(
                                                lastsize + 1,
                                                currentPagedatasize.toInt()
                                            )
                                            //catalog_list.notifyDataSetChanged()
                                        }
                                    } else if (responseproductlist.status == API_DATA_NOT_AVAILBLE) {
                                        if (currentPageNo == 1) {
                                            rvCatalog?.visibility = View.GONE
                                            tvNoproduct?.visibility = View.VISIBLE
                                        }
                                        currentPagedatasize = "0"
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
            swf_productlist?.isRefreshing = false
        }
    }

    private fun setData() {
        if (isAdded) {
            rvCatalog?.apply {
                layoutManager = mLayoutManager
                catalog_list =
                    CatalogAdapter(this@ProductListFragment, arrProducts)
                //catalog_list.addModels(arrProducts)
                itemAnimator = DefaultItemAnimator()
                adapter = catalog_list
            }

            val displayMetrics = DisplayMetrics()
            activity?.windowManager?.defaultDisplay?.getMetrics(displayMetrics)
            val screenWidth: Int = displayMetrics.widthPixels
            Log.e("tagScreen", "screenWidth " + screenWidth)

            if (screenWidth > 0) {
                val m60PercentScreenWidth = (screenWidth * 56) / 100
                val m40PercentScreenWidth = (screenWidth * 36) / 100

                Log.e("tagScreen", "m60PercentScreenWidth " + m60PercentScreenWidth)
                Log.e("tagScreen", "m40PercentScreenWidth " + m40PercentScreenWidth)
                Log.e(
                    "tagScreen",
                    "divided height " + ((m60PercentScreenWidth / 1.5f) - 12f).toInt()
                )


                catalog_list.mFirstWidth = m60PercentScreenWidth
                catalog_list.mSecondWidth = m40PercentScreenWidth
                catalog_list.notifyDataSetChanged()
            }

//            checkPermissionAndDownloadImages()
        }
    }

    fun loadProductDetail(id: String) {
        val fragment = ProductDetailFragment().getInstance(id, "")
        homeActivity?.deselectAllMenu()
        homeActivity?.supportFragmentManager
            ?.beginTransaction()
            ?.add(R.id.cm_fragmentContainer, fragment, fragment::class.java.name)
            ?.addToBackStack(null)
            ?.commit()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBack -> {
                homeActivity?.selectLastMenu()
                fragmentManager?.popBackStack()
            }
            R.id.tvshareproducts -> {
                if (isDialogOpen)
                    return
                isDialogOpen = true
                DialogShare(arrProducts, Const.OTHERS)
            }

            R.id.rl_sortby -> {
                SortbyDialog()
            }

            R.id.tvrefine -> {
                val intent = Intent(requireActivity(), FilterActivity::class.java)
                startActivityForResult(intent, RESULT_FILTER)
            }
        }

    }

    fun DialogShare(model: List<Record>, sourceapp: String) {
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
            for (j in model.indices) {
                for (mediafile in model.get(j).Media) {
                    if (mediafile.MediaTypeId.equals(Const.MEDIA_VIDEO)) {
                        hasAnyVideo = true
                    }
                }
            }


            var hasAnyImage = false
            for (j in model.indices) {
                for (mediafile in model.get(j).Media) {
                    if (mediafile.MediaTypeId.equals(Const.MEDIA_IMAGE)) {
                        hasAnyImage = true
                    }
                }
            }



            videoUriArray.clear()
            imageUriArray.clear()
            imageUrifb.clear()

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
            btnDone = dialog.findViewById(R.id.btn_done)
            liDownloads = dialog.findViewById(R.id.li_downloading)


            val rdDesc = dialog.findViewById<RadioButton>(R.id.rdDesc)
            val rdimg = dialog.findViewById<RadioButton>(R.id.rdimage)
            val rdmargin = dialog.findViewById<RadioButton>(R.id.rdmargin)
            var edt_margin = dialog.findViewById<EditText>(R.id.edt_margin)
            val rdvideo = dialog.findViewById<CheckBox>(R.id.rdvideo)
            val tvsharevideo = dialog.findViewById<TextView>(R.id.tvsharevideo)


            val llSaheVideo = dialog.findViewById<LinearLayout>(R.id.llSaheVideo)
            if (hasAnyVideo) {
                llSaheVideo.visibility = View.VISIBLE
            } else {
                llSaheVideo.visibility = View.GONE
            }
            tvsharevideo?.setOnClickListener {
                rdvideo?.isChecked = !rdvideo.isChecked
            }
            btnDone.setOnClickListener {
                if (!rdimg.isChecked && !rdDesc.isChecked && !rdvideo.isChecked) {
                    if(hasAnyImage && !hasAnyVideo)
                    {
                        activity?.showToast(getString(R.string.err_select_to_share), Const.TOAST)
                    }else if(!hasAnyImage && hasAnyVideo){
                        activity?.showToast(getString(R.string.err_select_to_share3), Const.TOAST)
                    }else{
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

                CallApiSharedData(id!!)
                if (rdmargin.isChecked) {
                    val strmargin = edt_margin.text.toString()
                    if (strmargin.isEmpty()) {
                        activity?.showToast(getString(R.string.err_enter_margin), Const.TOAST)
                        return@setOnClickListener
                    } else {
                        sharedesc += "\n" + "*Price " + getString(R.string.currency) + " " + strmargin + "*"
                    }
                }

                if (rdDesc.isChecked) {
                    val clipboard: ClipboardManager =
                        activity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip: ClipData = ClipData.newPlainText("copy", sharedesc)
                    clipboard.setPrimaryClip(clip)
                    activity?.showBottomToast(getString(R.string.desc_copy_msg))
                }

                // ShareOnAnyApp(model)
                isDialogOpen = false
                dialog.cancel()
            }

            ivclose.setOnClickListener {
                dialog.dismiss()
                isDialogOpen = false
            }
            dialog.setCancelable(false)
            dialog.setCanceledOnTouchOutside(false)
            dialog.show()
        }
    }

    fun DialogShare(model: Record, sourceapp: String) {
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
            for (mediafile in model.Media) {
                if (mediafile.MediaTypeId.equals(Const.MEDIA_VIDEO)) {
                    hasAnyVideo = true
                }
            }

            var hasAnyImage = false
            for (mediafile in model.Media) {
                if (mediafile.MediaTypeId.equals(Const.MEDIA_IMAGE)) {
                    Log.e("hasImage", mediafile.Media!!)
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
            } else {
                llSaheVideo.visibility = View.GONE
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
                    sharedesc = model.Title + "\n" +
                            model.Description
                }
            }


            btnDone.setOnClickListener {
                Log.e("share", "click button share done")
                if (!rdimg.isChecked && !rdDesc.isChecked && !rdvideo.isChecked) {
                    if(hasAnyImage && !hasAnyVideo)
                    {
                        activity?.showToast(getString(R.string.err_select_to_share), Const.TOAST)
                    }else if(!hasAnyImage && hasAnyVideo){
                        activity?.showToast(getString(R.string.err_select_to_share3), Const.TOAST)
                    }else{
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
                CallApiSharedData(model.ID)
                if (rdmargin.isChecked) {
                    val strmargin = edt_margin.text.toString()
                    if (strmargin.isEmpty()) {
                        activity?.showToast(getString(R.string.err_enter_margin), Const.TOAST)
                        return@setOnClickListener
                    } else {
                        sharedesc += "\n" + "*Price " + getString(R.string.currency) + " " + (strmargin.toFloat() + model.Price.replace(
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
                model.Media,
                model.ID,
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

    inner class DownloadTask(
        val imgurl: List<Media>?,
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
                        Log.e("videourl", "type media: " + imgurl.get(i).MediaTypeId)
                        if (imgurl.get(i).MediaTypeId.equals(Const.MEDIA_VIDEO)) {
                            val filenamesplit = imgurl.get(i).Media.split("/").toTypedArray()
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
                                    Log.e("videourl", getMediaLink() + imgurl.get(i).Media)
                                    val outputStream =
                                        FileOutputStream(
                                            java.lang.String.valueOf(mypath)
                                        )


                                    val videoUrl = URL(getMediaLink() + imgurl.get(i).Media)
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


                                    val videoUrl = URL(getMediaLink() + imgurl.get(i).Media)
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
                                val filenamesplit = imgurl.get(i).Media.split("/").toTypedArray()
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
                                        getBitmapFromURL(getMediaLink() + imgurl.get(i).Media)
                                    bitmap?.compress(
                                        Bitmap.CompressFormat.JPEG, 90, outputStream
                                    )
                                    outputStream.close()
                                }

                                imageUriArray.add(Uri.parse(mypath.toString()))
                                imageUrifb.add(Uri.fromFile(mypath))
                                imageUriGmail.add(Uri.parse("content://" + mypath.absolutePath))
                            } else {
                                val filenamesplit = imgurl.get(i).Media.split("/").toTypedArray()
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
                                        getBitmapFromURL(getMediaLink() + imgurl.get(i).Media)
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
                    if (this@ProductListFragment::btnDone.isInitialized) {
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

            if (this@ProductListFragment::btnDone.isInitialized) {
                btnDone.visibility = View.VISIBLE
                liDownloads.visibility = View.GONE
            }
        }
    }

    fun ShareOnFacebook(isimg: Boolean,isVideo: Boolean) {
        try {
            var sharePhoto: SharePhoto
            var tempUriArray: ArrayList<Uri> = ArrayList<Uri>()
            var arrphotos: ArrayList<ShareMedia> = ArrayList()
            if (isimg && !isVideo) {
                /*if (alreadyDownload) {
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

            /*  for (i in imageUriArray.indices) {
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
            e.printStackTrace()
        }
    }

    fun ShareOnAnyApp(model: Record,isimg: Boolean,isVideo: Boolean) {
        val shareIntent = Intent(Intent.ACTION_SEND_MULTIPLE)
        shareIntent.action = Intent.ACTION_SEND_MULTIPLE
        shareIntent.type = "text/plain"

        //    shareIntent.type = "image/*"
        shareIntent.type = "*/*"
        val arrUri: ArrayList<Uri> = ArrayList()

    /*    try {
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
        }

        shareIntent.putExtra(Intent.EXTRA_STREAM, arrUri)*/




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
            requireActivity().getString(R.string.app_name) + " Product " + model.Title
        )


        val openInChooser = Intent.createChooser(shareIntent, getString(R.string.title_share_with))
        requireActivity().startActivity(openInChooser)

    }

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

    open fun ShareDesc() {
        /*   AlertDialog.Builder(context)
               .setTitle(getString(R.string.title_share_content))
               .setMessage(sharedesc)
               .setPositiveButton("No",
                   DialogInterface.OnClickListener { dialog, which ->
                       // ShareOnAnyApp(false)

                   })
               .setNegativeButton("Yes",
                   DialogInterface.OnClickListener { dialog, which ->
                       ShareOnWhatsapp(false)

                   })

               .setCancelable(false)
               .show()
   */
        val dialog = CustomDialog(requireActivity(), getString(R.string.title_share_content),
            sharedesc,
            "Yes",
            "No",
            CustomDialog.ItemReturnListener {
                ShareOnWhatsapp(false, false)
            })
        dialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SHARE_DESC) {
            if (sharedesc.length > 0)
                ShareDesc()
        } else if (requestCode == RESULT_FILTER) {
            if (data != null) {
                val Selecteddata = data.getStringExtra(Const.FILTER_DATA)
                strFilterdata = Selecteddata?.replace("[", "")?.replace("]", "")!!
                currentPageNo = 1
                setScrollListener()
                CallApiFilter(strFilterdata)
            }
        }
    }

    private fun SortbyDialog() {
        val dialog = Dialog(
            requireActivity(),
            R.style.DialogSortby
        )

        dialog.setContentView(R.layout.custom_dailog_sortby)

        /*  dialog.setCancelable(true)
          dialog.setCanceledOnTouchOutside(true)*/
        val lp = dialog.window!!.attributes
        lp.dimAmount = 0.75f // Dim level. 0.0 - no dim, 1.0 - completely opaque
        dialog.window!!.attributes = lp

        val rl_recommend = dialog.findViewById<RelativeLayout>(R.id.rl_recommend)
        val rl_whatsnew = dialog.findViewById<RelativeLayout>(R.id.rl_whatsnew)
        val rl_high_to_low = dialog.findViewById<RelativeLayout>(R.id.rl_high_to_low)
        val rl_low_to_high = dialog.findViewById<RelativeLayout>(R.id.rl_low_to_high)

        rl_recommend.setOnClickListener {
            strSortbyId = Const.SORT_RECOMMENDED
            currentPageNo = 1
            setScrollListener()
            CallApiSortby(strSortbyId)
            dialog.cancel()
        }

        rl_whatsnew.setOnClickListener {
            strSortbyId = Const.SORT_NEW
            currentPageNo = 1
            setScrollListener()
            CallApiSortby(strSortbyId)
            dialog.cancel()
        }

        rl_high_to_low.setOnClickListener {
            strSortbyId = Const.SORT_HIGH_TO_LOW_PRICE
            currentPageNo = 1
            setScrollListener()
            CallApiSortby(strSortbyId)
            dialog.cancel()
        }

        rl_low_to_high.setOnClickListener {
            strSortbyId = Const.SORT_LOW_TO_HIGH_PRICE
            currentPageNo = 1
            setScrollListener()
            CallApiSortby(strSortbyId)
            dialog.cancel()
        }

        dialog.show()

    }

    fun CallApiSortby(sortRecommended: String) {
        strFilterdata = ""
        if (isInternetAvailable(requireActivity())) {
            mprogressbar.showProgressBar()

            val param = ParamSortby().apply {
                if (id == null || (id != null && id.equals("-1"))) {
                    categoryId = "0"
                    typeId = Const.SORT_PRODUCT
                } else {
                    categoryId = id!!
                    typeId = Const.SORT_CATEGORY
                }
                catalogueId = "0"

                sortTypeId = sortRecommended
                page = currentPageNo.toString()
                limit = Const.PAGINATION_LIMIT
            }
            val retrofit = WebService.getRetrofit(requireActivity()).create(ApiClient::class.java)
            retrofit.Sortbyinproduct(param)
                .enqueue(object : Callback<ResponseSortbyinproduct> {
                    override fun onFailure(call: Call<ResponseSortbyinproduct>, t: Throwable) {
                        mprogressbar.hideProgressBar()
                        swf_productlist?.isRefreshing = false
                    }

                    override fun onResponse(
                        call: Call<ResponseSortbyinproduct>,
                        response: Response<ResponseSortbyinproduct>?
                    ) {
                        mprogressbar.hideProgressBar()
                        swf_productlist?.isRefreshing = false

                        if (response != null) {
                            if (response.isSuccessful) {
                                val responseproductlist = response.body()
                                if (responseproductlist != null) {
                                    if (responseproductlist.status == API_SUCESS) {
                                        if (currentPageNo == 1) {
                                            arrProducts = ArrayList()
                                            rvCatalog?.visibility = View.VISIBLE
                                            tvNoproduct?.visibility = View.GONE
                                        }
                                        currentPagedatasize =
                                            responseproductlist.data.records.size.toString()
                                        val lastsize = arrProducts.size
                                        arrProducts.addAll(responseproductlist.data.records)
                                        if (currentPageNo == 1) {
                                            setData()
                                        } else {
                                            //catalog_list.notifyDataSetChanged()
                                            catalog_list.notifyItemRangeInserted(
                                                lastsize + 1,
                                                currentPagedatasize.toInt()
                                            )
                                            catalog_list.notifyItemRangeChanged(
                                                lastsize + 1,
                                                currentPagedatasize.toInt()
                                            )
                                        }

                                    } else if (responseproductlist.status == API_DATA_NOT_AVAILBLE) {
                                        if (currentPageNo == 1) {
                                            rvCatalog?.visibility = View.GONE
                                            tvNoproduct?.visibility = View.VISIBLE
                                        }
                                        currentPagedatasize = "0"
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
        }
    }

    private fun CallApiFilter(strSelecteddata: String) {
        strSortbyId = "0"
        if (isInternetAvailable(requireActivity())) {
            mprogressbar.showProgressBar()

            val param = ParamFilterResult().apply {
                if (id == null || (id != null && id.equals("-1"))) {
                    categoryId = "0"
                } else {
                    categoryId = id!!
                }
                variantIds = strSelecteddata
                page = currentPageNo.toString()
                limit = Const.PAGINATION_LIMIT
            }
            val retrofit = WebService.getRetrofit(requireActivity()).create(ApiClient::class.java)
            retrofit.ResultforFilterDataProduct(param)
                .enqueue(object : Callback<ResponseFilterResultProduct> {
                    override fun onFailure(call: Call<ResponseFilterResultProduct>, t: Throwable) {
                        mprogressbar.hideProgressBar()
                        swf_productlist?.isRefreshing = false
                    }

                    override fun onResponse(
                        call: Call<ResponseFilterResultProduct>,
                        response: Response<ResponseFilterResultProduct>?
                    ) {
                        mprogressbar.hideProgressBar()
                        swf_productlist?.isRefreshing = false

                        if (response != null) {
                            if (response.isSuccessful) {
                                val responseproductlist = response.body()
                                if (responseproductlist != null) {
                                    if (responseproductlist.status == API_SUCESS) {
                                        if (currentPageNo == 1) {
                                            arrProducts = ArrayList()
                                            rvCatalog?.visibility = View.VISIBLE
                                            tvNoproduct?.visibility = View.GONE
                                        }
                                        currentPagedatasize =
                                            responseproductlist.data.records.size.toString()
                                        arrProducts.addAll(responseproductlist.data.records)
                                        if (currentPageNo == 1) {
                                            setData()
                                        } else {
                                            catalog_list.notifyDataSetChanged()
                                        }

                                    } else if (responseproductlist.status == API_DATA_NOT_AVAILBLE) {
                                        if (currentPageNo == 1) {
                                            rvCatalog?.visibility = View.GONE
                                            tvNoproduct?.visibility = View.VISIBLE
                                        }
                                        currentPagedatasize = "0"
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


}
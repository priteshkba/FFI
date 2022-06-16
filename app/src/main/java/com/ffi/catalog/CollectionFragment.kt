package com.ffi.catalog

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.provider.Settings
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
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
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
import com.ffi.GlideApp
import com.ffi.HomeActivity
import com.ffi.R
import com.ffi.Utils.*
import com.ffi.Utils.Const.Companion.RESULT_FILTER
import com.ffi.collectiondetail.Media
import com.ffi.collectiondetail.ParamCollectionDetail
import com.ffi.collectiondetail.Record
import com.ffi.collectiondetail.ResponseCollectionDetails
import com.ffi.filter.FilterActivity
import com.ffi.filter.ParamFilterResult
import com.ffi.filter.ResponseFilterResult
import com.ffi.model.ParamAddShareddata
import com.ffi.model.ResponseAddSharedData
import com.ffi.model.sortby.ParamSortby
import com.ffi.model.sortby.ResponseSortby
import com.ffi.my_orders.MyOrderFragment
import com.ffi.productdetail.ProductDetailFragment
import com.ffi.api.ApiClient
import com.ffi.api.WebService
import kotlinx.android.synthetic.main.custom_toolbar.*
import kotlinx.android.synthetic.main.fragment_collection.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class CollectionFragment() : Fragment(), View.OnClickListener {

    var id: String = ""
    var name: String = ""

    fun getInstance(id: String, name: String): CollectionFragment {
        val mCollectionFragment = CollectionFragment()
        val args = Bundle()
        args.putString(Const.ID, id)
        args.putString(Const.NAME, name)
        mCollectionFragment.setArguments(args)
        return mCollectionFragment
    }

    private fun getArgumentData() {
        if (arguments != null) {
            if (arguments?.containsKey(Const.ID) != null &&
                arguments?.containsKey(Const.ID)!!
            ) {
                id = arguments?.getString(Const.ID)!!
            }
            if (arguments?.containsKey(Const.NAME) != null &&
                arguments?.containsKey(Const.NAME)!!
            ) {
                name = arguments?.getString(Const.NAME)!!
            }
        }
    }

    lateinit var collection_list: CollectionAdapter
    var homeActivity: HomeActivity? = null

    private val WRITE_PERMISSION = 0
    private val SHARE_DESC = 10
    private val SHARE_TO_ANY = 190

    val unicode: Int = 0x27A1
    val bullet: String = "/u27A1"

    private var isDialogOpen = false
    var sharedesc = ""
    var sharedescFacebook = ""
    var arrdata: ArrayList<Record> = ArrayList()


    var strFilterdata = ""
    var strSortbyId = "0"

    var shouldDownloadMultiple = false

    var currentPageNo = 1
    var currentPagedatasize = ""
    lateinit var scrollListener: EndlessRecyclerViewScrollListener
    lateinit var mLayoutManager: LinearLayoutManager


    lateinit var btnDone: TextView
    lateinit var liDownloads: LinearLayout

    //    var mDownloadTask: DownloadTask? = null
//    var alreadyDownload: Boolean = false
//    var imageUriArray: ArrayList<Uri> = ArrayList<Uri>()
    var imageUriArrayGlide: ArrayList<Uri> = ArrayList<Uri>()
    var videoUriArrayGlide: ArrayList<Uri> = ArrayList<Uri>()
    var bothUriArrayGlide: ArrayList<Uri> = ArrayList<Uri>()
    var imageUriArrayGlideMultiple: ArrayList<Uri> = ArrayList<Uri>()
    var imageUriArrayFacebook: ArrayList<Uri> = ArrayList<Uri>()
    var mMediaArrayList: List<Media> = ArrayList<Media>()
    var mProductID: String = ""
    var mTotalSize = 0
//    var imageUrifb: ArrayList<Uri> = ArrayList<Uri>() //for facebook share
//    var imageUriGmail: ArrayList<Uri> = ArrayList<Uri>() //for facebook share

    lateinit var shareDialog: ShareDialog
    lateinit var callbackManager: CallbackManager

    lateinit var mprogressbar: ProgressBarHandler


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
        return inflater.inflate(R.layout.fragment_collection, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getArgumentData()
        init()
        setRefresh()
    }

    private fun init() {
        homeActivity = activity as HomeActivity
        mLayoutManager = LinearLayoutManager(context)
        //homeActivity.header.visibility = View.VISIBLE
        ivBack.visibility = View.VISIBLE
        ivEdit.visibility = View.GONE
        txt_toolbar.text = name

    }

    private fun setRefresh() {
        currentPageNo = 1
        setScrollListener()
        swf_collection_list?.setOnRefreshListener {
            if (!strFilterdata.isEmpty()) {
                CallApiFilter(strFilterdata)
            } else if (!strSortbyId.equals("0")) {
                CallApiSortby(strSortbyId)
            } else {
                CallApiCatalogItemList(false)
            }
        }
    }

    private fun setScrollListener() {
        scrollListener = object : EndlessRecyclerViewScrollListener(mLayoutManager) {
            override fun onLoadMore(page: Int) {

                if (currentPagedatasize.equals(Const.PAGINATION_LIMIT)) {
                    currentPageNo++
                    Log.e("scroll", "onLoadMore" + currentPagedatasize + " Page no" + currentPageNo)
                    if (!strFilterdata.isEmpty()) {
                        CallApiFilter(strFilterdata)
                    } else if (!strSortbyId.equals("0")) {
                        CallApiSortby(strSortbyId)
                    } else {
                        CallApiCatalogItemList(false)
                    }

                    Log.e("scroll", "onLoadMore true")
                }
                Log.e("scroll", "onLoadMore false")
            }
        }
        rvCatalog?.addOnScrollListener(scrollListener)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mprogressbar = ProgressBarHandler(requireActivity())

        setListener()
        currentPageNo = 1
        setScrollListener()
        CallApiCatalogItemList(true)
    }

    private fun setListener() {
        ivBack.setOnClickListener(this)
        btn_shareall.setOnClickListener(this)
        lisortby.setOnClickListener(this)
        tvrefine.setOnClickListener(this)
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
            R.id.lisortby -> {
                SortbyDialog()
            }

            R.id.ivBack -> {
                homeActivity?.selectLastMenu()
                fragmentManager?.popBackStack()
            }
            R.id.btn_shareall -> {
                if (getUserId().isEmpty()) {
                    activity?.showLoginMsg()
                } else {
                    if (isDialogOpen)
                        return
                    isDialogOpen = true
                    shouldDownloadMultiple = true
                    DialogShare()
                }
            }
            R.id.tvrefine -> {
                val intent = Intent(requireActivity(), FilterActivity::class.java)
                startActivityForResult(intent, RESULT_FILTER)
            }

        }
    }


    fun CallApiCatalogItemList(isprogress: Boolean) {
        if (isInternetAvailable(requireActivity())) {
            if (App.isShowProgressbar && isprogress)
                mprogressbar.showProgressBar()

            val param = ParamCollectionDetail().apply {
                page = currentPageNo.toString()
                limit = Const.PAGINATION_LIMIT
                catalogueId = id
            }
            val retrofit = WebService.getRetrofit(requireActivity()).create(ApiClient::class.java)
            retrofit.GetCatalogDetails(param)
                .enqueue(object : Callback<ResponseCollectionDetails> {
                    override fun onFailure(call: Call<ResponseCollectionDetails>, t: Throwable) {
                        mprogressbar.hideProgressBar()
                        swf_collection_list?.isRefreshing = false
                    }

                    override fun onResponse(
                        call: Call<ResponseCollectionDetails>,
                        response: Response<ResponseCollectionDetails>?
                    ) {
                        mprogressbar.hideProgressBar()
                        swf_collection_list?.isRefreshing = false

                        if (response != null) {
                            if (response.isSuccessful) {
                                val responsecollection = response.body()
                                if (responsecollection != null) {
                                    if (responsecollection.status == API_SUCESS) {
                                        if (isAdded) {
                                            if (currentPageNo == 1) {
                                                arrdata = ArrayList()
                                                liNoproduct?.visibility = View.GONE
                                                btn_shareall?.visibility = View.VISIBLE
                                                rvCatalog?.visibility = View.VISIBLE
                                            }

                                            currentPagedatasize =
                                                responsecollection.data.records.size.toString()
                                            val lastsize = arrdata.size
                                            arrdata.addAll(responsecollection.data.records)
                                            if (currentPageNo == 1) {
                                                setData()
                                            } else {
                                                collection_list.notifyItemRangeInserted(
                                                    lastsize + 1,
                                                    currentPagedatasize.toInt()
                                                )
                                                collection_list.notifyItemRangeChanged(
                                                    lastsize + 1,
                                                    currentPagedatasize.toInt()
                                                )
                                            }
                                        }
                                    } else if (responsecollection.status == API_DATA_NOT_AVAILBLE) {
                                        if (isAdded && currentPageNo == 1) {
                                            btn_shareall?.visibility = View.GONE
                                            liNoproduct?.visibility = View.VISIBLE
                                            rvCatalog?.visibility = View.GONE
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
            swf_collection_list?.isRefreshing = false
        }
    }

    private fun setData() {
        if (isAdded) {
            rvCatalog.apply {
                layoutManager = mLayoutManager
                collection_list =
                    CollectionAdapter(this@CollectionFragment, arrdata, requireContext())
                //collection_list.addModels(arrdata)
                itemAnimator = DefaultItemAnimator()
                adapter = collection_list
            }

            val displayMetrics = DisplayMetrics()
            activity?.windowManager?.defaultDisplay?.getMetrics(displayMetrics)
            val screenWidth: Int = displayMetrics.widthPixels

            if (screenWidth > 0) {
                val m60PercentScreenWidth = (screenWidth * 56) / 100
                val m40PercentScreenWidth = (screenWidth * 36) / 100
                collection_list.mFirstWidth = m60PercentScreenWidth
                collection_list.mSecondWidth = m40PercentScreenWidth
                collection_list.notifyDataSetChanged()
            }

        }
    }


    fun DialogShare() = if (ActivityCompat.checkSelfPermission(
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
        Log.e("opendialog", "123")
        var hasAnyVideo = false
        for (j in arrdata.indices) {
            for (mediafile in arrdata.get(j).media) {
                if (mediafile.mediaTypeId.equals(Const.MEDIA_VIDEO)) {
                    hasAnyVideo = true
                }
            }
        }


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
        val edt_margin = dialog.findViewById<EditText>(R.id.edt_margin)

        val llSaheVideo = dialog.findViewById<LinearLayout>(R.id.llSaheVideo)
        if (hasAnyVideo) {
            llSaheVideo.visibility = View.VISIBLE
        } else {
            llSaheVideo.visibility = View.GONE
        }

        imageUriArrayGlideMultiple.clear()
        imageUriArrayFacebook.clear()
        imageUriArrayGlide.clear()
        videoUriArrayGlide.clear()

        mTotalSize = 0
        for (j in arrdata.indices) {
            val data = arrdata.get(j)
            mTotalSize += data.media.size
        }

        for (j in arrdata.indices) {
            val data = arrdata.get(j)
            DownloadTask(
                data.media,
                data.iD,
                false
            ).execute()
        }

        /* for (j in arrdata.indices) {
             val data = arrdata.get(j)
             val imgurl: List<Media> = data.media
             for (k in imgurl.indices) {
                 DownloadTask1(
                     imgurl.get(k).media,
                     data.iD,
                     false
                 ).execute()
             }

         }*/

//                if (data.media != null && data.media.size > 0) {
//                    for ((i, mImage) in data.media?.withIndex()!!) {
//                        Log.e(
//                            "tagGlideImageCheck",
//                            "checkGlideImages() i " + i + "  && " + getMediaLink() + mImage.media
//                        )
//                        GlideApp.with(activity?.applicationContext!!)
//                            .asBitmap()
//                            .load(getMediaLink() + mImage.media)
//                            .into(object : CustomTarget<Bitmap>() {
//                                override fun onLoadCleared(placeholder: Drawable?) {
//
//                                }
//
//                                override fun onResourceReady(
//                                    resource: Bitmap,
//                                    transition: Transition<in Bitmap>?
//                                ) {
//
//                                    Log.e(
//                                        "tagGlideImageCheck",
//                                        "checkGlideImages() onResourceReady " + j
//                                    )
////                            imageUriArrayGlide.add(resource)
//                                    val mFile =
//                                        activity?.getOutputMediaFile(i.toString() + "_" + j.toString())
//                                    if (mFile != null) {
//                                        Log.e(
//                                            "tagGlideImageCheck",
//                                            "checkGlideImages() onResourceReady " + i + " && mFile not null "
//                                                    + mFile.absolutePath
//                                        )
//                                        activity?.storeImage(resource, mFile)
//                                        Log.e(
//                                            "tagGlideImageCheck",
//                                            "checkGlideImages() onResourceReady i " + i
//                                                    + "  && Uri.fromFile(resource) " + Uri.fromFile(
//                                                mFile
//                                            )
//                                        )
//                                        imageUriArrayGlideMultiple.add(Uri.fromFile(mFile))
//                                    }
//                                }
//
//                            })

        /*   btnDone.visibility = View.VISIBLE
                        liDownloads.visibility = View.GONE
                    }
                    else{
                        btnDone.visibility = View.VISIBLE
                        liDownloads.visibility = View.GONE
                    }*/
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
        btnDone.setOnClickListener {
            if (!rdimg.isChecked && !rdDesc.isChecked && !rdvideo.isChecked) {
                activity?.showToast(getString(R.string.err_select_to_share), Const.TOAST)
                return@setOnClickListener
            }

            if (rdimg.isChecked) {
                if (imageUriArrayGlideMultiple.size != mTotalSize &&
                    mTotalSize != 0
                ) {
                    activity?.showToast(getString(R.string.err_img_not_appearing), Const.TOAST)
                    return@setOnClickListener
                }
            }

            CallApiSharedData(id, Const.COLLECTION_SHARE)

            if (rdmargin.isChecked && rdDesc.isChecked) {
                val strmargin = edt_margin.text.toString()
                if (strmargin.isEmpty()) {
                    activity?.showToast(getString(R.string.err_enter_margin), Const.TOAST)
                    return@setOnClickListener
                } else {
                    for (i in arrdata.indices) {
                        val tempdata = arrdata.get(i)
                        if (i == 0) {
                            sharedesc =
                                Character.toChars(unicode)
                                    .toString() + " " + "\u27A1 " + tempdata.title + "\n" +
                                        tempdata.description + "\n" + "*Price : " +
                                        getString(R.string.currency) + " " + (tempdata.price.replace(
                                    ",",
                                    ""
                                ).toFloat() + strmargin.toFloat()) + "*"
                        } else {
                            sharedesc += "\n\n" + " " + "\u27A1 " + tempdata.title + "\n" +
                                    tempdata.description + "\n" + "*Price : " +
                                    getString(R.string.currency) + " " + (tempdata.price.replace(
                                ",",
                                ""
                            ).toFloat() + strmargin.toFloat()) + "*"
                        }
                    }

                    Log.e("Bullet_", sharedesc + "_")

                }
            } else if (rdDesc.isChecked) {
                for (i in arrdata.indices) {
                    val tempdata = arrdata.get(i)
                    if (i == 0) {
                        sharedesc =
                            " " + "\u27A1 " + tempdata.title + "\n" +
                                    tempdata.description + "\n" + "*Price : " +
                                    getString(R.string.currency) + " " + tempdata.price + "*"
                    } else {
                        sharedesc += "\n\n" + " " + "\u27A1 " + tempdata.title + "\n" +
                                tempdata.description + "\n" + "*Price : " +
                                getString(R.string.currency) + " " + tempdata.price + "*"
                    }
                }
            }

            if (rdDesc.isChecked) {
                val clipboard: ClipboardManager =
                    activity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip: ClipData = ClipData.newPlainText("copy", sharedesc)
                clipboard.setPrimaryClip(clip)
                activity?.showBottomToast(getString(R.string.desc_copy_msg))
            }

            if (rdimg.isChecked) {
                ShareOnAnyApp(true, rdvideo.isChecked)
            } else {
                ShareOnAnyApp(false, rdvideo.isChecked)
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
    }


    fun DialogShare(model: Record, sourceapp: String) {
        Log.e("opendialog", "DialogShare collectionFragment 12345")
        shouldDownloadMultiple = false
        if (isDialogOpen)
            return
        isDialogOpen = true
        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                WRITE_PERMISSION
            )
            isDialogOpen = false
        } else {
            Log.e("opendialog", "456")
            var hasAnyVideo = false
            for (mediafile in model.media) {
                if (mediafile.mediaTypeId.equals(Const.MEDIA_VIDEO)) {
                    Log.e("hasVideo", mediafile.media!!)
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




            videoUriArrayGlide.clear()
            imageUriArrayGlide.clear()
            imageUriArrayGlideMultiple.clear()
            imageUriArrayFacebook.clear()

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
            val edt_margin = dialog.findViewById<EditText>(R.id.edt_margin)


            val llSaheVideo = dialog.findViewById<LinearLayout>(R.id.llSaheVideo)

            if (hasAnyVideo) {
                llSaheVideo.visibility = View.VISIBLE
                //  tvshareimg.text = getString(R.string.title_share_imageVideo)
            } else {
                llSaheVideo.visibility = View.GONE
                //tvshareimg.text = getString(R.string.title_share_image)
            }

            val llShareImage = dialog.findViewById<LinearLayout>(R.id.llShareImage)
            if (hasAnyImage) {
                llShareImage.visibility = View.VISIBLE
            } else {
                llShareImage.visibility = View.GONE
            }


            mMediaArrayList = model.media
            mProductID = model.iD
            // checkGlideImages()
//            clearAndRexecuteDownloader(model.media, model.iD)

            mTotalSize = 0
            mTotalSize += model.media.size

            DownloadTask(
                model.media,
                model.iD,
                false
            ).execute()

            Log.e("Call_", "Call_");

            /*  for (j in arrdata.indices) {
                  val data = arrdata.get(j)
                  val imgurl: List<Media> = data.media
                  for (k in imgurl.indices) {
                      DownloadTask1(
                          imgurl.get(k).media,
                          data.iD,
                          false
                      ).execute()
                  }

              }*/

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

                if (rdimg.isChecked && !hasAnyImage) {
                    activity?.showToast(
                        getString(R.string.err_img_not_appearing),
                        Const.TOAST
                    )
                    return@setOnClickListener
                }

                if (rdimg.isChecked) {
                    if (model.media != null &&
                        imageUriArrayGlide.size == model.media.size
                    ) {
                        if (imageUriArrayGlide.size <= 0) {
                            activity?.showToast(
                                getString(R.string.err_img_not_appearing),
                                Const.TOAST
                            )
                            return@setOnClickListener
                        }
                    }
                }

                CallApiSharedData(model.iD, Const.PRODUCT_SHARE)
                if (rdmargin.isChecked) {
                    val strmargin = edt_margin.text.toString()
                    if (strmargin.isEmpty()) {
                        activity?.showToast(getString(R.string.err_enter_margin), Const.TOAST)
                        return@setOnClickListener
                    } else {
                        sharedesc += "\n" + "*Price " + getString(R.string.currency) + " " + (strmargin.toFloat() + model.price.toFloat()) + "*"
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

                    ShareOnFacebook(rdimg.isChecked, rdvideo.isChecked)
                } else if (sourceapp.equals(Const.OTHERS)) {

                    if (rdimg.isChecked)
                        ShareOnAnyApp(true, rdvideo.isChecked)
                    else
                        ShareOnAnyApp(false, rdvideo.isChecked)

//                        ShareOnAnyApp(model)
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

        }
    }

    private fun CallApiSharedData(id: String, type: String) {
        if (isInternetAvailable(requireActivity())) {
            val param = ParamAddShareddata().apply {
                referenceId = id
                typeId = type
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

    private fun shareDataSocial(packageName: String, isimg: Boolean, isVideo: Boolean) {

        Log.e("////", "pckgName//" + packageName)

        val whatsappIntent = Intent(Intent.ACTION_SEND)
        whatsappIntent.setPackage(packageName)
        activity?.showBottomToast(getString(R.string.msg_desc_copeied))
        try {
            if (isimg && !isVideo) {
                // whatsappIntent.type = "image/png"
                whatsappIntent.type = "*/*"
                whatsappIntent.putExtra(Intent.EXTRA_STREAM, imageUriArrayGlide)
                whatsappIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                startActivityForResult(whatsappIntent, SHARE_DESC)
            } else if (!isimg && isVideo) {
                // whatsappIntent.type = "image/png"
                whatsappIntent.type = "*/*"
                whatsappIntent.putExtra(Intent.EXTRA_STREAM, videoUriArrayGlide)
                whatsappIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                startActivityForResult(whatsappIntent, SHARE_DESC)
            } else if (isimg && isVideo) {
                // whatsappIntent.type = "image/png"
                bothUriArrayGlide.clear()
                bothUriArrayGlide.addAll(imageUriArrayGlide)
                bothUriArrayGlide.addAll(videoUriArrayGlide)
                whatsappIntent.type = "*/*"
                whatsappIntent.putExtra(Intent.EXTRA_STREAM, bothUriArrayGlide)
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

    inner class DownloadTask(
        val imgurl: List<Media>,
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

            // Create a new trust manager that trust all certificates
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

        // Do the task in background/non UI thread
        override fun doInBackground(vararg params: Void?): String {
            try {

                if (imgurl.size > 0) {
                    for (i in imgurl.indices) {
                        Log.e("res", "482")
                        Log.e("videourl", "type media: " + imgurl.get(i).mediaTypeId)
                        if (imgurl.get(i).mediaTypeId.equals(Const.MEDIA_VIDEO)) {
                            val filenamesplit = imgurl.get(i).media.split("/").toTypedArray()
                            val size = filenamesplit.size

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


                                    //imageUriArray.add(Uri.parse(mypath.toString()))

                                    imageUriArrayGlideMultiple.add(Uri.parse(mypath.toString()))
                                    videoUriArrayGlide.add(Uri.parse(mypath.toString()))
                                    imageUriArrayFacebook.add(Uri.fromFile(mypath))
                                    Log.e(
                                        "imageUriArray_",
                                        imageUriArrayGlideMultiple.size.toString() + "_" + imageUriArrayGlide.size + "_" + imageUriArrayFacebook.size
                                    )


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
                                        // imageUriArray.add(uri!!)

                                        imageUriArrayGlideMultiple.add(uri!!)
                                        videoUriArrayGlide.add(uri!!)
                                        imageUriArrayFacebook.add(uri!!)
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
                                    Log.e("res", "496")
                                    val bitmap =
                                        getBitmapFromURL(getMediaLink() + imgurl.get(i).media)
                                    bitmap?.compress(
                                        Bitmap.CompressFormat.JPEG, 100, outputStream
                                    )
                                    Log.e("res", "500")

                                    outputStream.close()
                                }

                                imageUriArrayGlideMultiple.add(Uri.parse(mypath.toString()))
                                imageUriArrayGlide.add(Uri.parse(mypath.toString()))
                                imageUriArrayFacebook.add(Uri.fromFile(mypath))
                                Log.e(
                                    "imageUriArray_",
                                    imageUriArrayGlideMultiple.size.toString() + "_" + imageUriArrayGlide.size + "_" + imageUriArrayFacebook.size
                                )
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
                                    imageUriArrayGlideMultiple.add(uri!!)
                                    imageUriArrayGlide.add(uri!!)
                                    imageUriArrayFacebook.add(uri!!)
//                                Log.e("done", mypath.toString() + " download")
//                                Log.e("done", imageUriArray.size.toString() + " size")
//                                Log.e("done", imageUrifb.size.toString() + " size")
                                }


                            }
                        }

                        /*  imageUrifb.add(Uri.fromFile(mypath))
                      imageUriGmail.add(Uri.parse("content://" + mypath.absolutePath))
                      Log.e("done", mypath.toString() + " download")
                      Log.e("done", imageUriArray.size.toString() + " size")
                      Log.e("done", imageUrifb.size.toString() + " size")*/
                    }
                } else {
                    if (this@CollectionFragment::btnDone.isInitialized) {
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

            if (imageUriArrayGlideMultiple.size == mTotalSize) {
                if (this@CollectionFragment::btnDone.isInitialized) {
                    btnDone.visibility = View.VISIBLE
                    liDownloads.visibility = View.GONE
                }
            }
        }
    }

    fun ShareOnFacebook(isimg: Boolean, isVideo: Boolean) {
        try {
            var sharePhoto: SharePhoto? = null
            Log.e(
                "imageUriArray_",
                sharedesc + "_"
            )
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

                tempUriArray = imageUriArrayGlide
                for (i in tempUriArray.indices) {
                    sharePhoto = SharePhoto.Builder()
                        .setImageUrl(tempUriArray.get(i))
                        .build()
                    arrphotos.add(sharePhoto)
                }

            } else if (!isimg && isVideo) {
                tempUriArray = videoUriArrayGlide

                for (i in tempUriArray.indices) {
                    var shareVideo: ShareVideo = ShareVideo.Builder()
                        .setLocalUrl(tempUriArray.get(i))
                        .build()
                    arrphotos.add(shareVideo)
                }

            } else if (isVideo && isimg) {

                for (i in imageUriArrayGlide.indices) {
                    sharePhoto = SharePhoto.Builder()
                        .setImageUrl(imageUriArrayGlide.get(i))
                        .build()
                    arrphotos.add(sharePhoto)
                }

                for (i in videoUriArrayGlide.indices) {
                    var shareVideo: ShareVideo = ShareVideo.Builder()
                        .setLocalUrl(videoUriArrayGlide.get(i))
                        .build()
                    arrphotos.add(shareVideo)
                }

            }

            /* for (i in imageUriArrayFacebook.indices) {
                 try {
                     val bitmap: Bitmap = MediaStore.Images.Media.getBitmap(
                         activity?.contentResolver,
                         imageUriArrayFacebook.get(i)
                     )
                     if (i == 0) {
                         sharePhoto = SharePhoto.Builder()
                             .setBitmap(bitmap)
                             .setCaption(sharedesc)
                             .build()

                     } else {
                         sharePhoto = SharePhoto.Builder()
                             .setBitmap(bitmap)
                             .setCaption(sharedesc)
                             .build()
                     }
                     arrphotos.add(sharePhoto)
                 } catch (e: java.lang.Exception) {
                     if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                         App.requestStoragePermission(requireContext())
                     }
                     e.printStackTrace()
                 }

             }*/


            val shareContent = ShareMediaContent.Builder()
                .addMedia(arrphotos as List<ShareMedia>?)
                //.addMedium(sharePhoto)
                .build()

            activity?.showBottomToast(getString(R.string.msg_desc_copeied))


            shareDialog.show(shareContent)

            /* val bitmap: Bitmap = MediaStore.Images.Media.getBitmap(
                 activity?.contentResolver,
                 imageUriArrayFacebook.get(0)
             )
             sharePhoto = SharePhoto.Builder()
                 .setBitmap(bitmap)
                 .setCaption(sharedesc)
                 .build()
             arrphotos.add(sharePhoto)*/
        } catch (e: IndexOutOfBoundsException) {
            e.printStackTrace()
        }
    }

    fun ShareOnAnyApp(model: Record) {
        val shareIntent = Intent(Intent.ACTION_SEND_MULTIPLE)
        shareIntent.action = Intent.ACTION_SEND_MULTIPLE
        shareIntent.type = "text/plain"

        // shareIntent.type = "image/*"
        shareIntent.type = "*/*"
        val arrUri: ArrayList<Uri> = ArrayList()

        try {
            for (i in imageUriArrayFacebook.indices) {
                val bitmap: Bitmap = MediaStore.Images.Media.getBitmap(
                    activity?.contentResolver,
                    imageUriArrayFacebook.get(i)
                )
                val os = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os)

                val path = MediaStore.Images.Media.insertImage(
                    requireContext().contentResolver, bitmap, null, null
                )
                arrUri.add(Uri.parse(path))
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

        shareIntent.putExtra(Intent.EXTRA_STREAM, arrUri)
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

    fun ShareOnAnyApp(isimg: Boolean, isVideo: Boolean) {
        val builder: StrictMode.VmPolicy.Builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())

        val shareIntent = Intent(Intent.ACTION_SEND_MULTIPLE)
        shareIntent.action = Intent.ACTION_SEND_MULTIPLE

        shareIntent.putExtra(
            Intent.EXTRA_TITLE,
            requireActivity().getString(R.string.app_name)
        )
        shareIntent.putExtra(
            Intent.EXTRA_SUBJECT,
            requireActivity().getString(R.string.app_name) + " Collection " + name
        )

        if (isimg && !isVideo) {
            shareIntent.type = "image/*"
            // shareIntent.putExtra(Intent.EXTRA_STREAM, imageUriArrayGlideMultiple)
            shareIntent.putExtra(Intent.EXTRA_STREAM, imageUriArrayFacebook)
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            val openInChooser =
                Intent.createChooser(shareIntent, getString(R.string.title_share_with))
            startActivityForResult(openInChooser, SHARE_TO_ANY)
        } else if (!isimg && isVideo) {
            shareIntent.type = "*/*"
            // shareIntent.putExtra(Intent.EXTRA_STREAM, imageUriArrayGlideMultiple)
            shareIntent.putExtra(Intent.EXTRA_STREAM, videoUriArrayGlide)
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            val openInChooser =
                Intent.createChooser(shareIntent, getString(R.string.title_share_with))
            startActivityForResult(openInChooser, SHARE_TO_ANY)
        } else if (isimg && isVideo) {
            shareIntent.type = "*/*"
            // shareIntent.putExtra(Intent.EXTRA_STREAM, imageUriArrayGlideMultiple)
            bothUriArrayGlide.clear()
            bothUriArrayGlide.addAll(imageUriArrayFacebook)
            bothUriArrayGlide.addAll(videoUriArrayGlide)
            shareIntent.putExtra(Intent.EXTRA_STREAM, bothUriArrayGlide)
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            val openInChooser =
                Intent.createChooser(shareIntent, getString(R.string.title_share_with))
            startActivityForResult(openInChooser, SHARE_TO_ANY)
        } else {
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT, sharedesc)
            val openInChooser =
                Intent.createChooser(shareIntent, getString(R.string.title_share_with))
            activity?.startActivity(shareIntent)
        }


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

    fun ShareDesc() {
        /* AlertDialog.Builder(context)
             .setTitle(getString(R.string.title_share_content))
             .setMessage(sharedesc)
             .setPositiveButton("No",
                 DialogInterface.OnClickListener { dialog, which ->
                     // ShareOnAnyApp(false)

                 })
             .setCancelable(false)
             .setNegativeButton("Yes",
                 DialogInterface.OnClickListener { dialog, which ->
                     ShareOnWhatsapp(false)

                 })
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
        Log.e("share", requestCode.toString())
        if (requestCode == SHARE_DESC) {
            if (sharedesc.length > 0)
                ShareDesc()
        } else if (requestCode == SHARE_TO_ANY) {
            if (sharedesc.length > 0) {
                AlertDialog.Builder(context)
                    .setTitle(getString(R.string.title_share_content))
                    .setMessage(sharedesc)
                    .setPositiveButton("No",
                        DialogInterface.OnClickListener { dialog, which ->
                            // ShareOnAnyApp(false)

                        })
                    .setCancelable(false)
                    .setNegativeButton("Yes",
                        DialogInterface.OnClickListener { dialog, which ->
                            ShareOnAnyApp(false, false)

                        })
                    .show()
            }
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

        /*dialog.setCancelable(true)
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
                categoryId = "0"
                catalogueId = id
                typeId = Const.SORT_CATALOG
                sortTypeId = sortRecommended
                page = currentPageNo.toString()
                limit = Const.PAGINATION_LIMIT
            }
            val retrofit = WebService.getRetrofit(requireActivity()).create(ApiClient::class.java)
            retrofit.Sortby(param)
                .enqueue(object : Callback<ResponseSortby> {
                    override fun onFailure(call: Call<ResponseSortby>, t: Throwable) {
                        mprogressbar.hideProgressBar()
                    }

                    override fun onResponse(
                        call: Call<ResponseSortby>,
                        response: Response<ResponseSortby>?
                    ) {
                        mprogressbar.hideProgressBar()
                        if (response != null) {
                            if (response.isSuccessful) {
                                val responseproductlist = response.body()
                                if (responseproductlist != null) {
                                    if (responseproductlist.status == API_SUCESS) {
                                        if (currentPageNo == 1) {
                                            arrdata = ArrayList()
                                            rvCatalog?.visibility = View.VISIBLE
                                            btn_shareall?.visibility = View.VISIBLE
                                            liNoproduct?.visibility = View.GONE
                                        }

                                        currentPagedatasize =
                                            responseproductlist.data.records.size.toString()
                                        val lastsize = arrdata.size
                                        arrdata.addAll(responseproductlist.data.records)
                                        if (currentPageNo == 1) {
                                            setData()
                                        } else {
                                            collection_list.notifyItemRangeInserted(
                                                lastsize + 1,
                                                currentPagedatasize.toInt()
                                            )
                                            collection_list.notifyItemRangeChanged(
                                                lastsize + 1,
                                                currentPagedatasize.toInt()
                                            )
                                        }

                                    } else if (responseproductlist.status == API_DATA_NOT_AVAILBLE) {
                                        if (currentPageNo == 1) {
                                            rvCatalog?.visibility = View.GONE
                                            btn_shareall?.visibility = View.GONE
                                            liNoproduct?.visibility = View.VISIBLE
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
                categoryId = "0"
                variantIds = strSelecteddata
                page = currentPageNo.toString()
                limit = Const.PAGINATION_LIMIT
            }
            val retrofit = WebService.getRetrofit(requireActivity()).create(ApiClient::class.java)
            retrofit.ResultforFilterData(param)
                .enqueue(object : Callback<ResponseFilterResult> {
                    override fun onFailure(call: Call<ResponseFilterResult>, t: Throwable) {
                        mprogressbar.hideProgressBar()
                        swf_collection_list?.isRefreshing = false
                    }

                    override fun onResponse(
                        call: Call<ResponseFilterResult>,
                        response: Response<ResponseFilterResult>?
                    ) {
                        mprogressbar.hideProgressBar()
                        swf_collection_list?.isRefreshing = false

                        if (response != null) {
                            if (response.isSuccessful) {
                                val responseproductlist = response.body()
                                if (responseproductlist != null) {
                                    if (responseproductlist.status == API_SUCESS) {
                                        if (currentPageNo == 1) {
                                            arrdata = ArrayList()
                                            liNoproduct?.visibility = View.GONE
                                            btn_shareall?.visibility = View.VISIBLE
                                            rvCatalog?.visibility = View.VISIBLE
                                        }

                                        currentPagedatasize =
                                            responseproductlist.data.records.size.toString()
                                        val lastsize = arrdata.size
                                        arrdata.addAll(responseproductlist.data.records)
                                        if (currentPageNo == 1) {
                                            setData()
                                        } else {
                                            collection_list.notifyItemRangeInserted(
                                                lastsize + 1,
                                                currentPagedatasize.toInt()
                                            )
                                            collection_list.notifyItemRangeChanged(
                                                lastsize + 1,
                                                currentPagedatasize.toInt()
                                            )
                                        }


                                    } else if (responseproductlist.status == API_DATA_NOT_AVAILBLE) {
                                        if (currentPageNo == 1) {
                                            liNoproduct?.visibility = View.VISIBLE
                                            btn_shareall?.visibility = View.GONE
                                            rvCatalog?.visibility = View.GONE
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
            Log.e("res", "bitmap 676")
            BitmapFactory.decodeStream(input)
        } catch (e: IOException) {
            // Log exception
            null
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == WRITE_PERMISSION) {
            checkPermissionAndDownloadImages()
        }
    }

/*private fun clearAndRexecuteDownloader(mMedia: List<Media>, productid: String) {
    if(!alreadyDownload){
        imageUriArray.clear()

        if(mDownloadTask != null){
            mDownloadTask?.cancel(true)
            mDownloadTask = null
        }
        mDownloadTask = DownloadTask(
            mMedia, productid, true
        )
        mDownloadTask?.execute()
    }
}*/

    private fun checkPermissionAndDownloadImages() {
        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            /*if(mMediaArrayList != null && mMediaArrayList.size > 0){
                clearAndRexecuteDownloader(mMediaArrayList, mProductID)
            }*/
        }
    }

    private fun checkGlideImages() {
        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            imageUriArrayGlide.clear()
            Log.e("tagGlideImageCheck", "checkGlideImages() ")
            if (mMediaArrayList != null && mMediaArrayList.size > 0) {
                for ((i, mImage) in mMediaArrayList.withIndex()) {
                    Log.e(
                        "tagGlideImageCheck",
                        "checkGlideImages() i " + i + "  && " + getMediaLink() + mImage.media
                    )
                    GlideApp.with(activity?.applicationContext!!)
                        .asBitmap()
                        .load(getMediaLink() + mImage.media)
                        .into(object : CustomTarget<Bitmap>() {
                            override fun onLoadCleared(placeholder: Drawable?) {

                            }

                            override fun onResourceReady(
                                resource: Bitmap,
                                transition: Transition<in Bitmap>?
                            ) {
                                Log.e(
                                    "tagGlideImageCheck",
                                    "checkGlideImages() onResourceReady " + i
                                )
//                            imageUriArrayGlide.add(resource)
                                val mFile = activity?.getOutputMediaFile(i.toString())
                                if (mFile != null) {
                                    Log.e(
                                        "tagGlideImageCheck",
                                        "checkGlideImages() onResourceReady " + i + " && mFile not null "
                                                + mFile.absolutePath
                                    )
                                    activity?.storeImage(resource, mFile)
                                    Log.e(
                                        "tagGlideImageCheck",
                                        "checkGlideImages() onResourceReady i " + i
                                                + "  && Uri.fromFile(resource) " + Uri.fromFile(
                                            mFile
                                        )
                                    )
                                    imageUriArrayGlide.add(Uri.fromFile(mFile))
                                }
                            }

                        })
                }

                btnDone.visibility = View.VISIBLE
                liDownloads.visibility = View.GONE
            } else {
                btnDone.visibility = View.VISIBLE
                liDownloads.visibility = View.GONE
            }
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                WRITE_PERMISSION
            )
        }
    }


}

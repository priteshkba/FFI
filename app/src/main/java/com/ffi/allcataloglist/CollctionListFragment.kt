package com.ffi.allcataloglist

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
import com.ffi.App
import com.ffi.HomeActivity
import com.ffi.R
import com.ffi.Utils.*
import com.ffi.catalog.CollectionFragment
import com.ffi.catalog.ParamCatalogList
import com.ffi.catalog.Record
import com.ffi.catalog.ResponseCatalogList
import com.ffi.model.ParamAddShareddata
import com.ffi.model.ResponseAddSharedData
import com.ffi.productlist.Media
import com.ffi.api.ApiClient
import com.ffi.api.WebService
import kotlinx.android.synthetic.main.custom_toolbar.*
import kotlinx.android.synthetic.main.fragment_collction_list.*
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

class CollctionListFragment : Fragment() {

    private val WRITE_PERMISSION = 0
    private val SHARE_DESC = 10
    lateinit var model: com.ffi.collectiondetail.Record

    var sharedesc = ""
    val unicode: Int = 0x27A1

    private var isDialogOpen = false
    lateinit var collection_list: CollectionListAdapter
    lateinit var mprogressbar: ProgressBarHandler
    var homeActivity: HomeActivity? = null

    var currentPageNo = 1
    var currentPagedatasize = ""
    lateinit var scrollListener: EndlessRecyclerViewScrollListener
    lateinit var mLayoutManager: LinearLayoutManager
    var data: ArrayList<Record> = ArrayList()

    lateinit var shareDialog: ShareDialog
    lateinit var callbackManager: CallbackManager

    var imageUriArray: ArrayList<Uri> = ArrayList<Uri>()
    var videoUriArray: ArrayList<Uri> = ArrayList<Uri>()
    var bothUriArray: ArrayList<Uri> = ArrayList<Uri>()
    var imageUriArrayDownload: ArrayList<Uri> = ArrayList<Uri>()
    var imageUrifb: ArrayList<Uri> = ArrayList<Uri>() //for facebook share
    var imageUriGmail: ArrayList<Uri> = ArrayList<Uri>() //for facebook share

    lateinit var btnDone: TextView
    lateinit var liDownloads: LinearLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e("saved", "line no 101")
        // Initialize facebook SDK.
        FacebookSdk.sdkInitialize(requireActivity().getApplicationContext());

        // Create a callbackManager to handle the login responses.
        callbackManager = CallbackManager.Factory.create()


        shareDialog = ShareDialog(this)

        // this part is optional
        shareDialog.registerCallback(callbackManager, callback, 100)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        Log.e("saved", "savedinsta-" + savedInstanceState + "line no 119")
        return inflater.inflate(R.layout.fragment_collction_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        homeActivity = activity as HomeActivity
        ivBack.visibility = View.GONE
        ivEdit.visibility = View.GONE
        txt_toolbar.text = getString(R.string.menu_collection)

        mprogressbar = ProgressBarHandler(requireActivity())
        mprogressbar.hideProgressBar()
        mLayoutManager = LinearLayoutManager(context)

        setRefresh()
        currentPageNo = 1
        setScrollListener()
        CallApiCatalog(true)
    }

    private fun setRefresh() {
        swf_collectionlist?.setOnRefreshListener {
            currentPageNo = 1
            setScrollListener()
            CallApiCatalog(false)
        }
    }

    private fun setScrollListener() {
        Log.e("scroll", "setScrollListener")
        scrollListener = object : EndlessRecyclerViewScrollListener(mLayoutManager) {
            override fun onLoadMore(page: Int) {
                Log.e("scroll", "onLoadMore" + currentPagedatasize + " Page no" + page)
                if (currentPagedatasize.equals(Const.PAGINATION_LIMIT)) {
                    currentPageNo++
                    CallApiCatalog(false)
                    Log.e("scroll", "onLoadMore true")
                }
                Log.e("scroll", "onLoadMore false")
            }
        }
        rvCollectionlist.addOnScrollListener(scrollListener)
    }


    fun CallApiCatalog(isprogress: Boolean) {
        if (isInternetAvailable(requireActivity())) {
            if (App.isShowProgressbar && isprogress)
                mprogressbar.showProgressBar()

            val param = ParamCatalogList().apply {
                page = currentPageNo.toString()
                limit = Const.PAGINATION_LIMIT
            }
            val retrofit = WebService.getRetrofit(requireActivity()).create(ApiClient::class.java)
            retrofit.GetCatalogList(param)
                .enqueue(object : Callback<ResponseCatalogList> {
                    override fun onFailure(call: Call<ResponseCatalogList>, t: Throwable) {
                        mprogressbar.hideProgressBar()
                        swf_collectionlist?.isRefreshing = false
                    }

                    override fun onResponse(
                        call: Call<ResponseCatalogList>,
                        response: Response<ResponseCatalogList>?
                    ) {
                        mprogressbar.hideProgressBar()
                        swf_collectionlist?.isRefreshing = false

                        if (response != null) {
                            if (response.isSuccessful) {
                                val responseLogin = response.body()

                                if (responseLogin != null) {
                                    if (responseLogin.status == API_SUCESS) {
                                        if (isAdded) {
                                            currentPagedatasize =
                                                responseLogin.data.records.size.toString()
                                            if (currentPageNo.toString().equals("1")) {
                                                data = ArrayList()
                                            }
                                            data.addAll(responseLogin.data.records)
                                            if (currentPageNo.toString().equals("1")) {
                                                setData(data)
                                            } else {
                                                collection_list.notifyDataSetChanged()
                                            }
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
            swf_collectionlist?.isRefreshing = false
        }
    }

    private fun setData(data: List<Record>) {
        if (isAdded) {
            rvCollectionlist.apply {
                layoutManager = mLayoutManager
                collection_list = CollectionListAdapter(
                    this@CollctionListFragment,
                    data
                )
                itemAnimator = DefaultItemAnimator()
                adapter = collection_list

            }


            val displayMetrics = DisplayMetrics()
            activity?.windowManager?.defaultDisplay?.getMetrics(displayMetrics)
            val screenWidth: Int = displayMetrics.widthPixels

            if (screenWidth > 0) {
                collection_list.mFirstWidth = screenWidth
                collection_list.notifyDataSetChanged()
            }

        }
    }


    fun loadCollectionItems(id: String, name: String) {
        val fragment =
            CollectionFragment().getInstance(id, name)//.newInstance(refProv)


        homeActivity?.deselectAllMenu()
        homeActivity?.supportFragmentManager
            ?.beginTransaction()
            ?.add(R.id.cm_fragmentContainer, fragment, fragment::class.java.name)
            ?.addToBackStack(null)
            ?.commit()

    }

    /* model: String,
            sourceapp: String*/
    fun DialogShare(
        arrdata: List<com.ffi.collectiondetail.Record>,
        sourceapp: String
    ) {
        Log.e("share", "1 call method DialogShare 1234")
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


            var hasAnyVideo = false
            for (j in arrdata.indices) {
                for (mediafile in arrdata.get(j).media) {
                    if (mediafile.mediaTypeId.equals(Const.MEDIA_VIDEO)) {
                        hasAnyVideo = true
                    }
                }
            }


            imageUriArray.clear()
            imageUriArrayDownload.clear()
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
            val edt_margin = dialog.findViewById<EditText>(R.id.edt_margin)

            val llSaheVideo = dialog.findViewById<LinearLayout>(R.id.llSaheVideo)
            llSaheVideo.visibility = View.GONE
            /*if (hasAnyVideo) {
                llSaheVideo.visibility = View.VISIBLE
            } else {
                llSaheVideo.visibility = View.GONE
            }*/
            tvshareimg.text = getString(R.string.title_share_image)
            /*  DownloadTask(
                  model.Media, model.ID, false
              ).execute()*/

            var totalSize = 0
            for (j in arrdata.indices) {
                val data = arrdata.get(j)
                totalSize += data.media.size
            }
            for (j in arrdata.indices) {

                    model = arrdata.get(j)
                    DownloadTaskAll(
                        totalSize,
                        model.media,
                        model.iD,
                        false
                    ).execute()


            }
            tvdesc.setOnClickListener {
                if (rdDesc.isChecked) {
                    rdDesc.isChecked = false
                } else {
                    rdDesc.isChecked = true
                }
            }

            tvshareimg.setOnClickListener {
                if (rdimg.isChecked) {
                    rdimg.isChecked = false
                } else {
                    rdimg.isChecked = true
                }
            }

            tvaddmargin.setOnClickListener {
                if (rdmargin.isChecked) {
                    rdmargin.isChecked = false
                } else {
                    rdmargin.isChecked = true
                }
            }

            tvsharevideo?.setOnClickListener {
                rdvideo?.isChecked = !rdvideo.isChecked
            }
            rdDesc.setOnCheckedChangeListener { buttonView, isChecked ->
                sharedesc = ""

                if (isChecked) {
                    sharedesc = "\u27A1 " + model.title + "\n" +
                            model.description
                }
            }

            btnDone.setOnClickListener {
                if (!rdimg.isChecked && !rdDesc.isChecked) {
                    activity?.showToast(getString(R.string.err_select_to_share), Const.TOAST)
                    return@setOnClickListener
                }

                if (rdimg.isChecked) {
                    //if (imageUriArray.size <= 0) {

                    if (imageUriArray.size != totalSize &&
                        totalSize != 0
                    ) {
                        activity?.showToast(getString(R.string.err_img_dwing), Const.TOAST)
                        return@setOnClickListener
                    }
                }

                CallApiSharedData(model.iD)

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
                            sharedesc += "\n\n" + "\u27A1 " + tempdata.title + "\n" +
                                    tempdata.description + "\n" + "*Price : " +
                                    getString(R.string.currency) + " " + tempdata.price + "*"
                        }
                    }
                }

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


                if (sourceapp.equals(Const.WHATSAPP)) {
                    if (rdimg.isChecked) {
                        ShareOnWhatsapp(true, rdvideo.isChecked)
                    } else {
                        ShareOnWhatsapp(false, rdvideo.isChecked)
                    }
                } else if (sourceapp.equals(Const.FACEBOOK)) {
                    ShareOnFacebook(rdimg.isChecked, false) //todo only share images not video
                } else if (sourceapp.equals(Const.OTHERS)) {
                    ShareOnAnyApp(model)
                }
                isDialogOpen = false
                dialog.cancel()
            }

            ivclose.setOnClickListener {
                dialog.dismiss()
                isDialogOpen = false
                videoUriArray.clear()
                imageUriArray.clear()
                imageUriArrayDownload.clear()
                imageUrifb.clear()
            }
            dialog.setCancelable(false)
            dialog.setCanceledOnTouchOutside(false)
            dialog.show()
        }
    }

    private fun CallApiSharedData(id: String) {
        if (isInternetAvailable(requireActivity())) {
            val param = ParamAddShareddata().apply {
                referenceId = id
                typeId = Const.COLLECTION_SHARE
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

    private fun shareDataSocial(packageName: String, isimg: Boolean, isVideo: Boolean) {

        Log.e("////", "pckgName//" + packageName)

        val whatsappIntent = Intent(Intent.ACTION_SEND)
        whatsappIntent.setPackage(packageName)
        activity?.showBottomToast(getString(R.string.msg_desc_copeied))
        try {
            if (isimg && !isVideo) {
                whatsappIntent.setType("image/png")
                whatsappIntent.putExtra(Intent.EXTRA_STREAM, imageUriArray)
                whatsappIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                startActivityForResult(whatsappIntent, SHARE_DESC)
            } else if (!isimg && isVideo) {
                whatsappIntent.setType("*/*")
                whatsappIntent.putExtra(Intent.EXTRA_STREAM, videoUriArray)
                whatsappIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                startActivityForResult(whatsappIntent, SHARE_DESC)
            } else if (isimg && isVideo) {
                bothUriArray.clear()
                bothUriArray.addAll(imageUriArray)
                bothUriArray.addAll(videoUriArray)
                whatsappIntent.setType("*/*")
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

    /*   fun getBitmapFromURL(src: String?): Bitmap? {
           return try {
               val url = URL(src)
               val connection: HttpURLConnection = url
                   .openConnection() as HttpURLConnection
               connection.setDoInput(true)
               connection.connect()
               val input: InputStream = connection.getInputStream()
               BitmapFactory.decodeStream(input)
           } catch (e: IOException) {
               e.printStackTrace()
               null
           }
       }*/

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        if (requestCode == WRITE_PERMISSION) {

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
                HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory())
            } catch (e: java.lang.Exception) {
            }

        }

        // Do the task in background/non UI thread
        override fun doInBackground(vararg params: Void?): String {
            try {
                Log.e("res", "single download 480 size " + imgurl.size)

                if (imgurl?.size ?: 0 > 0) {
                    for (i in imgurl.indices) {
                        Log.e("res", "482")
                        if (imgurl.get(i).MediaTypeId.equals(Const.MEDIA_VIDEO)) {
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
                                    // imageUriArray.add(Uri.parse(mypath.toString()))
                                    videoUriArray.add(Uri.parse(mypath.toString()))

                                } catch (e: java.lang.Exception) {
                                    Log.e("videourl", "catch:")
                                    Log.e("videourl", "catch:" + e.message)
                                }
                            } else {

                                val filenamesplit = imgurl.get(i).Media.split("/").toTypedArray()
                                val size = filenamesplit.size
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
                                    Environment.DIRECTORY_DOWNLOADS + LOCAL_VIDEO_FOLDER
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
                                        size?.minus(1)?.let { filenamesplit.get(it) })

                                if (!mypath.exists()) {
                                    val outputStream =
                                        FileOutputStream(
                                            java.lang.String.valueOf(mypath)
                                        )
                                    Log.e("res", "496")
                                    val bitmap =
                                        getBitmapFromURL(getMediaLink() + imgurl.get(i).Media)
                                    bitmap?.compress(
                                        Bitmap.CompressFormat.JPEG, 100, outputStream
                                    )
                                    Log.e("res", "500")

                                    outputStream.close()
                                }

                                imageUriArray.add(Uri.parse(mypath.toString()))
                                imageUriArrayDownload.add(Uri.parse(mypath.toString()))
                                imageUrifb.add(Uri.fromFile(mypath))
                                imageUriGmail.add(Uri.parse("content://" + mypath.absolutePath))
                                Log.e("done", mypath.toString() + " download")
                                Log.e("done", imageUriArray.size.toString() + " size")
                                Log.e("done", imageUrifb.size.toString() + " size")
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
                                    imageUriArrayDownload.add(uri!!)
                                    imageUrifb.add(uri!!)
                                    imageUriGmail.add(uri!!)
//                                Log.e("done", mypath.toString() + " download")
//                                Log.e("done", imageUriArray.size.toString() + " size")
//                                Log.e("done", imageUrifb.size.toString() + " size")
                                }


                            }

                        }


                    }
                } else {
                    if (this@CollctionListFragment::btnDone.isInitialized) {
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
            if (isDownloadonly && imageUriArray.size > 0) {
                activity?.showToast(getString(R.string.msg_imges_downloaded), Const.TOAST)
                if (dialog.isShowing)
                    dialog.cancel()
            }

            if (this@CollctionListFragment::btnDone.isInitialized) {
                btnDone.visibility = View.VISIBLE
                liDownloads.visibility = View.GONE
            }
        }
    }

    inner class DownloadTaskAll(
        val totalSize: Int,
        val imgurl: List<com.ffi.collectiondetail.Media>,
        val id: String,
        val isDownloadonly: Boolean
    ) :
        AsyncTask<Void, Void, String>() {

        var dialog = ProgressDialog(activity)

        override fun onPreExecute() {
            super.onPreExecute()

            if (isDownloadonly) {
                dialog.setMessage(getString(R.string.downloading_images))

                if (dialog.isShowing) {

                } else {
                    dialog.show()
                }
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
                HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory())
            } catch (e: java.lang.Exception) {
            }

        }

        // Do the task in background/non UI thread
        override fun doInBackground(vararg params: Void?): String {
            try {
                Log.e("res_", "all download 480 size " + imgurl.size)

                if (imgurl?.size ?: 0 > 0) {
                    for (i in imgurl.indices) {
                        Log.e("res", "482")
                        /*if (imgurl.get(i).mediaTypeId.equals(Const.MEDIA_VIDEO)) {
                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {


                                val ffidir = File(
                                    requireContext().cacheDir,
                                    getString(R.string.app_name)
                                )
                                if (!ffidir.exists()) {
                                    ffidir.mkdir()
                                }

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

                                } catch (e: java.lang.Exception) {
                                    Log.e("videourl", "catch:")
                                    Log.e("videourl", "catch:" + e.message)
                                }
                            }
                            else {
                                val filenamesplit = imgurl.get(i).media.split("/").toTypedArray()
                                val size = filenamesplit.size
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
                                    Environment.DIRECTORY_DOWNLOADS + LOCAL_VIDEO_FOLDER
                                )

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
                                    }

                                } catch (e: java.lang.Exception) {
                                    Log.e("videourl", "catch2:")
                                    Log.e("videourl", "catch2:" + e.message)
                                }
                            }
                        } else {
                            if (imgurl.get(i).mediaTypeId.equals(Const.MEDIA_IMAGE)) {
                                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                                    val filenamesplit =
                                        imgurl.get(i).media.split("/").toTypedArray()
                                    val size = filenamesplit.size
                                    val ffidir =
                                        File(Environment.getExternalStorageDirectory(), "FFI")
                                    if (!ffidir.exists()) {
                                        ffidir.mkdir()
                                    }
                                    val mypath = File(ffidir, id + "_" +
                                            size?.minus(1)?.let { filenamesplit.get(it) })

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

                                    imageUriArray.add(Uri.parse(mypath.toString()))
                                    imageUriArrayDownload.add(Uri.parse(mypath.toString()))
                                    imageUrifb.add(Uri.fromFile(mypath))
                                    imageUriGmail.add(Uri.parse("content://" + mypath.absolutePath))
                                    Log.e("done", mypath.toString() + " download")
                                    Log.e("done", imageUriArray.size.toString() + " size")
                                    Log.e("done", imageUrifb.size.toString() + " size")
                                } else {
                                    val filenamesplit =
                                        imgurl.get(i).media.split("/").toTypedArray()
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
                                        imageUriArrayDownload.add(uri!!)
                                        imageUrifb.add(uri!!)
                                        imageUriGmail.add(uri!!)
//                                Log.e("done", mypath.toString() + " download")
//                                Log.e("done", imageUriArray.size.toString() + " size")
//                                Log.e("done", imageUrifb.size.toString() + " size")
                                    }
                                }
                            }
                        }*/


                        if (imgurl.get(i).mediaTypeId.equals(Const.MEDIA_IMAGE)) {
                          //  if (limit < imagelimitTodownload) {
                                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                                    val filenamesplit =
                                        imgurl.get(i).media.split("/").toTypedArray()
                                    val size = filenamesplit.size
                                    val ffidir =
                                        File(Environment.getExternalStorageDirectory(), "FFI")
                                    if (!ffidir.exists()) {
                                        ffidir.mkdir()
                                    }
                                    val mypath = File(ffidir, id + "_" +
                                            size?.minus(1)?.let { filenamesplit.get(it) })

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

                                    imageUriArray.add(Uri.parse(mypath.toString()))
                                    imageUriArrayDownload.add(Uri.parse(mypath.toString()))
                                    imageUrifb.add(Uri.fromFile(mypath))
                                    imageUriGmail.add(Uri.parse("content://" + mypath.absolutePath))
                                    Log.e("done", mypath.toString() + " download")
                                    Log.e("done", imageUriArray.size.toString() + " size")
                                    Log.e("done", imageUrifb.size.toString() + " size")
                                  //  limit++
                                }
                                else {
                                    val filenamesplit =
                                        imgurl.get(i).media.split("/").toTypedArray()
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
                                        imageUriArrayDownload.add(uri!!)
                                        imageUrifb.add(uri!!)
                                        imageUriGmail.add(uri!!)
                                       // limit++
//                                Log.e("done", mypath.toString() + " download")
//                                Log.e("done", imageUriArray.size.toString() + " size")
//                                Log.e("done", imageUrifb.size.toString() + " size")
                                    }
                                }
                          //  }
                        }

                    }
                } else {
                    if (this@CollctionListFragment::btnDone.isInitialized) {
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
            /*    if (isDownloadonly && imageUriArray.size > 0) {
                    activity?.showToast(getString(R.string.msg_imges_downloaded))
                    if (dialog.isShowing)
                        dialog.cancel()
                }*/

            if (isDownloadonly) {
                if (dialog.isShowing) {
                    dialog.cancel()
                }
            }

            Log.e(
                "imageUriArray_",
                imageUriArray.size.toString() + "_" + totalSize.toString() + "_" + isDownloadonly + "__" + imageUriArrayDownload.size
            )
            if (imageUriArray.size == totalSize) {
                if (this@CollctionListFragment::btnDone.isInitialized) {
                    btnDone.visibility = View.VISIBLE
                    liDownloads.visibility = View.GONE
                }
            }

            if (imageUriArrayDownload.size == totalSize) {
                if (isDownloadonly) {
                    activity?.showToast(getString(R.string.msg_imges_downloaded), Const.TOAST)
                    if (dialog.isShowing) {
                        dialog.cancel()
                    }
                    imageUriArrayDownload.clear()
                }
            }
        }
    }

    fun ShareOnFacebook(isimg: Boolean, isVideo: Boolean) {
        Log.e("Facebook_", "Facebook_")
        try {
            var sharePhoto: SharePhoto
            // var arrphotos: ArrayList<SharePhoto> = ArrayList()
            /*   for (i in imageUriArray.indices) {
                   val bitmap: Bitmap = MediaStore.Images.Media.getBitmap(
                       activity?.getContentResolver(),
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
                for (i in 0..5) { //todo share 6 image only
                    if(tempUriArray.size>i)
                    {
                        sharePhoto = SharePhoto.Builder()
                            .setImageUrl(tempUriArray.get(i))
                            .build()
                        arrphotos.add(sharePhoto)
                    }

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


            val shareContent = ShareMediaContent.Builder()
                .addMedia(arrphotos as List<ShareMedia>?)
                .build()

            activity?.showBottomToast(getString(R.string.msg_desc_copeied))
            shareDialog.show(shareContent)
        } catch (e: IndexOutOfBoundsException) {
            e.printStackTrace()
        }
    }

    fun ShareOnAnyApp(model: com.ffi.collectiondetail.Record) {
        val shareIntent = Intent(Intent.ACTION_SEND_MULTIPLE)
        shareIntent.action = Intent.ACTION_SEND_MULTIPLE
        shareIntent.type = "text/plain"

        shareIntent.type = "image/*"
        val arrUri: ArrayList<Uri> = ArrayList()

        for (i in imageUrifb.indices) {
            val bitmap: Bitmap = MediaStore.Images.Media.getBitmap(
                activity?.getContentResolver(),
                imageUrifb.get(i)
            )
            val os = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os)

            val path = MediaStore.Images.Media.insertImage(
                requireContext().getContentResolver(), bitmap, null, null
            )
            arrUri.add(Uri.parse(path))
        }

        shareIntent.putExtra(Intent.EXTRA_STREAM, arrUri)
        shareIntent.putExtra(Intent.EXTRA_TEXT, sharedesc)
        shareIntent.putExtra(
            Intent.EXTRA_TITLE,
            requireActivity().getString(R.string.app_name)
        )
        shareIntent.putExtra(
            Intent.EXTRA_SUBJECT,
            requireActivity().getString(R.string.app_name) + " Collection " + model.title
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
        /*AlertDialog.Builder(context)
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
            .show()*/

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
        callbackManager.onActivityResult(requestCode, resultCode, data);
        Log.e("share", requestCode.toString())
        if (requestCode == SHARE_DESC) {
            if (sharedesc.length > 0)
                ShareDesc()
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

    override fun onStop() {
        super.onStop()
        if (getUserId().isEmpty())
            App.isShowProgressbar = false
    }
}
package com.ffi.sharedcatalog

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
import com.facebook.share.widget.ShareDialog
import com.ffi.HomeActivity
import com.ffi.R
import com.ffi.Utils.*
import com.ffi.catalog.CollectionFragment
import com.ffi.model.*
import com.ffi.api.ApiClient
import com.ffi.api.WebService
import kotlinx.android.synthetic.main.custom_toolbar.*
import kotlinx.android.synthetic.main.fragment_shared_catalog.*
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


class SharedCatalogFragment : Fragment(), View.OnClickListener {

    lateinit var mprogressbar: ProgressBarHandler
    var homeActivity: HomeActivity? = null

    private var isDialogOpen = false
    var currentPageNo = 1
    var currentPagedatasize = ""
    lateinit var scrollListener: EndlessRecyclerViewScrollListener
    lateinit var mLayoutManager: LinearLayoutManager
    var arrShared: ArrayList<Record> = ArrayList()

    lateinit var btnDone: TextView
    lateinit var liDownloads: LinearLayout

    private val WRITE_PERMISSION = 0
    private val SHARE_DESC = 10

    var sharedesc = ""

    var imageUriArray: ArrayList<Uri> = ArrayList<Uri>()
    var imageUrifb: ArrayList<Uri> = ArrayList<Uri>() //for facebook share
    var imageUriGmail: ArrayList<Uri> = ArrayList<Uri>() //for facebook share

    lateinit var shareDialog: ShareDialog
    lateinit var callbackManager: CallbackManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mLayoutManager = LinearLayoutManager(requireActivity())
        mprogressbar = ProgressBarHandler(requireActivity())

        FacebookSdk.sdkInitialize(requireActivity().getApplicationContext())
        callbackManager = CallbackManager.Factory.create()
        shareDialog = ShareDialog(this)

        shareDialog.registerCallback(callbackManager, callback, 100)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment


        return inflater.inflate(R.layout.fragment_shared_catalog, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        homeActivity = activity as HomeActivity
        ivBack.visibility = View.VISIBLE
        ivEdit.visibility = View.GONE
        txt_toolbar.text = getString(R.string.title_shared_catalog)

        if (rvShared.layoutManager != null)
            rvShared.layoutManager = mLayoutManager


        ivBack.setOnClickListener(this)

        setScrollListener()
        if (!getUserId().isEmpty()) {
            CallApiGetSharedData(true)
        }
    }

    fun CallApiGetSharedData(isLoader: Boolean) {
        if (isInternetAvailable(requireActivity())) {

            if (isLoader) {
                mprogressbar.showProgressBar()
            }

            val param = ParamGetSharedData().apply {
                limit = Const.PAGINATION_LIMIT
                page = currentPageNo.toString()
                typeId = "1"
            }


            val retrofit = WebService.getRetrofit(requireActivity()).create(ApiClient::class.java)
            retrofit.GetSharedSharedData(param)
                .enqueue(object : Callback<ResponseGetSharedData> {
                    override fun onFailure(call: Call<ResponseGetSharedData>, t: Throwable) {
                        mprogressbar.hideProgressBar()
                    }

                    override fun onResponse(
                        call: Call<ResponseGetSharedData>,
                        response: Response<ResponseGetSharedData>?
                    ) {
                        mprogressbar.hideProgressBar()
                        if (response != null) {
                            if (response.isSuccessful) {
                                val body = response.body()
                                if (body != null) {
                                    if (body.status.equals(API_SUCESS)) {

                                        val responseshared = response.body()

                                        if (responseshared != null) {
                                            if (currentPageNo.equals("1")) {
                                                arrShared = ArrayList()
                                            }

                                            val data = responseshared.data
                                            currentPagedatasize = data.records.size.toString()
                                            arrShared.addAll(data.records)
                                            setData(data.records)
                                        }
                                    } else if (body.status.equals(API_DATA_NOT_AVAILBLE)) {
                                        if (isAdded) {
                                            rvShared.visibility = View.GONE
                                            tvNoShare.visibility = View.VISIBLE
                                        }
                                    }
                                }
                            } else {
                                activity?.showToast(getString(R.string.msg_common_error),Const.ALERT)
                            }
                        }
                    }
                })
        } else {
            NetworkErrorDialog(requireActivity())
        }
    }

    private fun setData(records: List<Record>) {
        if (isAdded) {

            rvShared.visibility = View.VISIBLE
            tvNoShare.visibility = View.GONE

            var shared_list : SharedCatalogAdapter
            rvShared.apply {
                layoutManager = LinearLayoutManager(requireActivity())
                shared_list = SharedCatalogAdapter(this@SharedCatalogFragment)
                shared_list.addModels(arrShared)
                itemAnimator = DefaultItemAnimator()
                adapter = shared_list
            }

            val displayMetrics = DisplayMetrics()
            activity?.windowManager?.defaultDisplay?.getMetrics(displayMetrics)
            val screenWidth: Int = displayMetrics.widthPixels

            if (screenWidth > 0) {
                val m60PercentScreenWidth = (screenWidth * 56) / 100
                val m40PercentScreenWidth = (screenWidth * 36) / 100
                shared_list.mFirstWidth = m60PercentScreenWidth
                shared_list.mSecondWidth = m40PercentScreenWidth
                shared_list.notifyDataSetChanged()
            }
        }
    }

    private fun setScrollListener() {
        scrollListener = object : EndlessRecyclerViewScrollListener(mLayoutManager) {
            override fun onLoadMore(page: Int) {
                if (currentPagedatasize.equals(Const.PAGINATION_LIMIT)) {
                    currentPageNo = page
                    CallApiGetSharedData(false)
                }
            }
        }
        rvShared.addOnScrollListener(scrollListener)
    }


    fun DialogShare(model: Record, sourceapp: String) {
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
        } else {
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
            btnDone = dialog.findViewById<TextView>(R.id.btn_done)
            liDownloads = dialog.findViewById(R.id.li_downloading)

            val tvdesc = dialog.findViewById<TextView>(R.id.tvDesc)
            val tvshareimg = dialog.findViewById<TextView>(R.id.tvshareimg)
            val tvaddmargin = dialog.findViewById<TextView>(R.id.tvaddmargin)

            val rdDesc = dialog.findViewById<CheckBox>(R.id.rdDesc)
            val rdimg = dialog.findViewById<CheckBox>(R.id.rdimage)
            val rdmargin = dialog.findViewById<CheckBox>(R.id.rdmargin)
            val edt_margin = dialog.findViewById<EditText>(R.id.edt_margin)

            DownloadTask(
                model.media,
                model.iD,
                false
            ).execute()

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

            rdDesc.setOnCheckedChangeListener { buttonView, isChecked ->
                sharedesc = ""

                if (isChecked) {
                    sharedesc = model.name + "\n" +
                            model.description
                }
            }


            btnDone.setOnClickListener {
                if (!rdimg.isChecked && !rdDesc.isChecked) {
                    activity?.showToast(getString(R.string.err_select_to_share),Const.TOAST)
                    return@setOnClickListener
                }

                if (rdimg.isChecked) {
                    if (imageUriArray.size <= 0) {
                        activity?.showToast(getString(R.string.err_img_dwing),Const.TOAST)
                        return@setOnClickListener
                    }
                }

                CallApiSharedData(model.iD, Const.PRODUCT_SHARE)
                if (rdmargin.isChecked) {
                    val strmargin = edt_margin.text.toString()
                    if (strmargin.isEmpty()) {
                        activity?.showToast(getString(R.string.err_enter_margin),Const.TOAST)
                        return@setOnClickListener
                    } else {
                        sharedesc += "\n" + "*Price " + getString(R.string.currency) + " " + (strmargin.toFloat() /*+ model.price.toFloat()*/)+"*"
                    }
                }

                val clipboard: ClipboardManager =
                    activity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip: ClipData = ClipData.newPlainText("copy", sharedesc)
                clipboard.setPrimaryClip(clip)


                if (sourceapp.equals(Const.WHATSAPP)) {
                    if (rdimg.isChecked) {
                        ShareOnWhatsapp(true)
                    } else {
                        ShareOnWhatsapp(false)
                    }
                } else if (sourceapp.equals(Const.FACEBOOK)) {
                    ShareOnFacebook()
                } else if (sourceapp.equals(Const.OTHERS)) {
                    ShareOnAnyApp(model)
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
            val retrofit = WebService.getRetrofit(requireActivity())?.create(ApiClient::class.java)
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

    private fun ShareOnWhatsapp(isimg: Boolean) {

        val baseActivity = BaseActivity()
        val isWhatsUpInstalled = baseActivity.isAppInstalled(context,getString(R.string.pckg_whtsapp))
        val isWhatsUpBusinessInstalled = baseActivity.isAppInstalled(context,getString(R.string.pckg_whtsapp_business))

        if(isWhatsUpInstalled)
        {
            shareDataSocial(getString(R.string.pckg_whtsapp),isimg)
        }
        else if(isWhatsUpBusinessInstalled)
        {
            shareDataSocial(getString(R.string.pckg_whtsapp_business),isimg)
        }
        else{
            activity?.showToast(getString(R.string.whatsapp_no_installed),Const.ALERT)
        }


    }

    private fun shareDataSocial(pckgName: String, isimg: Boolean) {
        Log.e("////","pckgName//"+pckgName)

        val whatsappIntent = Intent(Intent.ACTION_SEND)
        whatsappIntent.setPackage(pckgName)
        activity?.showBottomToast(getString(R.string.msg_desc_copeied))
        try {
            if (isimg) {
                whatsappIntent.setType("image/png")
                whatsappIntent.putExtra(Intent.EXTRA_STREAM, imageUriArray)
                whatsappIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                startActivityForResult(whatsappIntent, SHARE_DESC)
            } else {
                whatsappIntent.type = "text/plain"
                whatsappIntent.putExtra(Intent.EXTRA_TEXT, sharedesc)
                activity?.startActivity(whatsappIntent)
            }
        } catch (ex: ActivityNotFoundException) {
            activity?.showToast(getString(R.string.whatsapp_no_installed),Const.ALERT)
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

                for (i in imgurl.indices) {
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
                            val bitmap = getBitmapFromURL(getMediaLink() + imgurl.get(i).media)
                            bitmap?.compress(
                                Bitmap.CompressFormat.JPEG, 100, outputStream
                            )
                            outputStream.close()
                        }

                        imageUriArray.add(Uri.parse(mypath.toString()))
                        imageUrifb.add(Uri.fromFile(mypath))
                        imageUriGmail.add(Uri.parse("content://" + mypath.absolutePath))
                    }
                    else {
                        val filenamesplit = imgurl.get(i).media.split("/").toTypedArray()
                        val size = filenamesplit.size
                        val values = ContentValues()
                        val fileName = id + "_" + size?.minus(1)?.let { filenamesplit.get(it) }

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
                            val bitmap = getBitmapFromURL(getMediaLink() + imgurl.get(i).media)
                            bitmap?.compress(
                                Bitmap.CompressFormat.JPEG, 90, outputStream
                            )
                            outputStream?.close()

                        }catch (e: java.lang.Exception){
                            Log.e("tagUri", e.message.toString())
                        }

                        if(uri != null){
                            imageUriArray.add(uri!!)
                            imageUrifb.add(uri!!)
                            imageUriGmail.add(uri!!)
                        }
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
                activity?.showToast(getString(R.string.msg_imges_downloaded),Const.TOAST)
                if (dialog.isShowing)
                    dialog.cancel()
            }

            if (this@SharedCatalogFragment::btnDone.isInitialized) {
                btnDone.visibility = View.VISIBLE
                liDownloads.visibility = View.GONE
            }
        }
    }

    fun ShareOnFacebook() {
        try {
            var sharePhoto: SharePhoto
            var arrphotos: ArrayList<SharePhoto> = ArrayList()
            for (i in imageUriArray.indices) {
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

    fun ShareOnAnyApp(model: Record) {
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
            requireActivity().getString(R.string.app_name) + " Product " + model.name
        )


        val openInChooser = Intent.createChooser(shareIntent, getString(R.string.title_share_with))
        requireActivity().startActivity(openInChooser)
    }


    fun ShareDesc() {
        /*  AlertDialog.Builder(context)
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
              .show()*/

        val dialog = CustomDialog(requireActivity(), getString(R.string.title_share_content),
            sharedesc,
            "Yes",
            "No",
            CustomDialog.ItemReturnListener {
                ShareOnWhatsapp(false)
            })
        dialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SHARE_DESC) {
            if (sharedesc.length > 0)
                ShareDesc()
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

    fun loadCollectionItems(id: String, name: String) {
        val fragment = CollectionFragment().getInstance(id, name)//.newInstance(refProv)
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
//                homeActivity?.replaceFragment(MyAccountFragment(), "4")
                fragmentManager?.popBackStack()
            }
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
}
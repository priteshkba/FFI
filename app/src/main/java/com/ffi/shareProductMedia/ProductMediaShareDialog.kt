package com.ffi.shareProductMedia

import android.app.Activity
import android.app.Dialog
import android.content.*
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat.startActivityForResult
import com.ffi.R
import com.ffi.Utils.*
import com.ffi.api.ApiClient
import com.ffi.api.WebService
import com.ffi.model.ParamAddShareddata
import com.ffi.model.ResponseAddSharedData
import com.ffi.productdetail.Data
import com.ffi.productdetail.ProductDetailFragment
import com.ffi.productlist.Media
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


class ProductMediaShareDialog(context: Context,val productid:String, val model: Data, val sourceapp: String,val activity: Activity) :
    Dialog(context) {
      var tvdesc: TextView? = null
      var tvshareimg: TextView? = null
      var tvaddmargin: TextView? = null
      var tvsharevideo: TextView? = null
      var btnDone: TextView? = null

      var rdDesc: CheckBox? = null
      var rdimg: CheckBox? = null
      var rdmargin: CheckBox? = null
      var rdvideo: CheckBox? = null
      var edt_margin: EditText? = null
      var llSaheVideo: LinearLayout? = null
      var liDownloads: LinearLayout? = null
    var sharedesc = ""
    var alreadyDownload: Boolean = false
    var imageUriArray: ArrayList<Uri> = ArrayList<Uri>()
    var videoUriArray: ArrayList<Uri> = ArrayList<Uri>()
    var bothUris:ArrayList<Uri> = ArrayList<Uri>()

    private val SHARE_DESC = 10


    var mDownloadTask: DownloadTask? = null

    private var isDialogOpen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dailog_share)
        window!!.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        window!!.setGravity(Gravity.CENTER)
        window!!.setBackgroundDrawableResource(android.R.color.transparent)

        setCancelable(false)
        setCanceledOnTouchOutside(false)


        tvdesc = findViewById(R.id.tvDesc)
        tvshareimg = findViewById(R.id.tvshareimg)
        tvaddmargin = findViewById(R.id.tvaddmargin)
        tvsharevideo = findViewById(R.id.tvsharevideo)

        rdDesc = findViewById<CheckBox>(R.id.rdDesc)
        rdimg = findViewById<CheckBox>(R.id.rdimage)
        rdmargin = findViewById<CheckBox>(R.id.rdmargin)
        rdvideo = findViewById<CheckBox>(R.id.rdvideo)
        edt_margin = findViewById<EditText>(R.id.edt_margin)
         llSaheVideo = findViewById(R.id.llSaheVideo)

        btnDone = findViewById<TextView>(R.id.btn_done)
        liDownloads = findViewById(R.id.li_downloading)
        val ivclose = findViewById<ImageView>(R.id.ivclose)
        var edt_margin = findViewById<EditText>(R.id.edt_margin)


        var hasAnyVideo = false
        for (mediafile in model.Media) {
            if (mediafile.MediaTypeId.equals(Const.MEDIA_VIDEO)) {
                hasAnyVideo = true
            }
        }
        if (hasAnyVideo) {
            llSaheVideo?.visibility = View.VISIBLE
        } else {
            llSaheVideo?.visibility = View.GONE
        }

        clearAndRexecuteDownloader(model.Media)

        tvdesc?.setOnClickListener {
            rdDesc?.isChecked = !rdDesc?.isChecked!!
        }

        tvshareimg?.setOnClickListener {
            rdimg?.isChecked = !rdimg?.isChecked!!
        }

        tvaddmargin?.setOnClickListener {
            rdmargin?.isChecked = !rdmargin?.isChecked!!
        }

        tvsharevideo?.setOnClickListener {
            rdvideo?.isChecked = !rdvideo?.isChecked!!
        }
        rdvideo?.setOnCheckedChangeListener { buttonView, isChecked ->

        }

        rdDesc?.setOnCheckedChangeListener { buttonView, isChecked ->
            sharedesc = ""

            if (isChecked) {
                sharedesc = model.Title + "\n" +
                        model.Description
            }
        }

        btnDone?.setOnClickListener {
            if (!rdimg?.isChecked!! && !rdDesc?.isChecked!! && !rdvideo?.isChecked!!) {
                //todo change message
                activity?.showToast(context.getString(R.string.err_select_to_share), Const.TOAST)
                return@setOnClickListener
            }

            if (rdimg?.isChecked!!) {
                if (alreadyDownload) {
                    if (imageUriArray.size <= 0) {
                        activity?.showToast(
                            context.getString(R.string.err_img_not_appearing),
                            Const.TOAST
                        )
                        return@setOnClickListener
                    }
                } else {
                    if (model != null && model?.Media != null
                    ) {
                        if (imageUriArray.size <= 0) {
                            activity?.showToast(
                                context.getString(R.string.err_img_not_appearing),
                                Const.TOAST
                            )
                            return@setOnClickListener
                        }
                    }
                }
            }
            CallApiSharedData(productid)
            if (rdmargin?.isChecked!!) {
                val strmargin = edt_margin.text.toString()
                if (strmargin.isEmpty()) {
                    activity?.showToast(context.getString(R.string.err_enter_margin), Const.TOAST)
                    return@setOnClickListener
                } else {
                    sharedesc += "\n" + "*Price " + context.getString(R.string.currency) + " " + (strmargin.toFloat() + model.Price.replace(
                        ",",
                        ""
                    ).toFloat()) + "*"
                }
            }

            val clipboard: ClipboardManager =
                activity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip: ClipData = ClipData.newPlainText("copy", sharedesc)
            clipboard.setPrimaryClip(clip)


            if (sourceapp.equals(Const.WHATSAPP)) {

                if (rdimg?.isChecked!!) {

                    ShareOnWhatsapp(true, rdvideo?.isChecked!!)
                } else {
                    ShareOnWhatsapp(false, rdvideo?.isChecked!!)
                }
            } else if (sourceapp.equals(Const.FACEBOOK)) {
               // ShareOnFacebook()
            } else if (sourceapp.equals(Const.OTHERS)) {
//                    ShareOnAnyApp(model)
              /*  if (rdimg?.isChecked!!)
                  ShareOnAnyApp(true,rdvideo?.isChecked!!)
                else
                   ShareOnAnyApp(false,rdvideo?.isChecked!!)*/
            }
            isDialogOpen = false
          cancel()
        }

        ivclose.setOnClickListener {
            isDialogOpen = false
        dismiss()
        }


    }

    private fun clearAndRexecuteDownloader(mMedia: List<Media>) {
        if (!alreadyDownload) {
            imageUriArray.clear()
            videoUriArray.clear()

            if (mDownloadTask != null) {
                mDownloadTask?.cancel(true)
                mDownloadTask = null
            }
            mDownloadTask = DownloadTask(
                mMedia, productid, true
            )
            mDownloadTask?.execute()
        } else {

                btnDone?.visibility = View.VISIBLE
                liDownloads?.visibility = View.GONE

        }
    }

    inner class DownloadTask(
        val imgurl: List<Media>,
        val id: String,
        val isLast: Boolean
    ) : AsyncTask<Void, Void, String>() {

        override fun onPreExecute() {
            super.onPreExecute()
            imageUriArray.clear()
            videoUriArray.clear()

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

        private fun writeFileContent(uri: Uri?) {
            try {
                val file = uri?.let { context.contentResolver.openFileDescriptor(it, "w") }

                file?.let {
                    val fileOutputStream = FileOutputStream(
                        it.fileDescriptor
                    )
                    val textContent = "This is the dummy text."

                    fileOutputStream.write(textContent.toByteArray())

                    fileOutputStream.close()
                    it.close()
                }

            } catch (e: FileNotFoundException) {
//print logs
            } catch (e: IOException) {
//print logs
            }

        }

        // Do the task in background/non UI thread
        @RequiresApi(Build.VERSION_CODES.R)
        override fun doInBackground(vararg params: Void?): String {
            try {
                Log.e("res", "480 size " + imgurl.size)
                if (imgurl.size > 0) {
                    for (i in imgurl.indices) {
                        Log.e("res", "482")
                        val filenamesplit = imgurl.get(i).Media.split("/").toTypedArray()
                        val size = filenamesplit.size
                        Log.e("videourl", "type media: " + imgurl.get(i).MediaTypeId)
                        if (imgurl.get(i).MediaTypeId.equals(Const.MEDIA_VIDEO)) {
                            Log.e("videourl", "type video")


                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {


                                val ffidir = File(
                                    context.cacheDir,
                                    context.getString(R.string.app_name)
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
                            }
                            else {
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
                                val uri: Uri? = activity.contentResolver.insert(
                                    MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                                    values!!
                                )
                                try {
                                    val outputStream: OutputStream? =
                                        activity.contentResolver.openOutputStream(uri!!)


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
                                val ffidir = File(
                                    Environment.DIRECTORY_PICTURES,
                                    context.getString(R.string.app_name)
                                )
                                if (!ffidir.exists()) {
                                    ffidir.mkdir()
                                }
                                val mypath = File(ffidir, id + "_" +
                                        size?.minus(1)?.let { filenamesplit.get(it) })

                                try {
                                    val outputStream =
                                        FileOutputStream(
                                            java.lang.String.valueOf(mypath)
                                        )
                                    val bitmap =
                                        getBitmapFromURL(getMediaLink() + imgurl.get(i).Media)
                                    bitmap?.compress(
                                        Bitmap.CompressFormat.JPEG, 90, outputStream
                                    )
                                    Log.e("res", "500")

                                    outputStream.close()
                                } catch (e: java.lang.Exception) {
                                    Log.e("Catch", e.message.toString())
                                }

                                Log.e("res", "496")
                                imageUriArray.add(Uri.parse(mypath.toString()))
                            } else {
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
                                    Environment.DIRECTORY_PICTURES + LOCAL_IMAGE_FOLDER
                                )


                                val uri: Uri? = activity.contentResolver.insert(
                                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                    values!!
                                ) //important!

                                try {
                                    val outputStream: OutputStream? =
                                        activity.contentResolver.openOutputStream(uri!!)
                                    val bitmap =
                                        getBitmapFromURL(getMediaLink() + imgurl.get(i).Media)
                                    bitmap?.compress(
                                        Bitmap.CompressFormat.JPEG, 90, outputStream
                                    )
                                    outputStream?.close()

                                } catch (e: java.lang.Exception) {
                                    Log.e("tagUri", e.message.toString())
                                    Log.e("tagUri", "image errior")
                                }

                                if (uri != null) {
                                    imageUriArray.add(uri!!)
                                }


                            }
                        }

                    }
                } else {


                        btnDone?.visibility = View.VISIBLE
                        liDownloads?.visibility = View.GONE

                }
                return ""
            } catch (e: Exception) {
                Log.e("tagUri", " imageUriArray uri main error " + e.message)
                e.printStackTrace()
            }

            return ""
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            Log.e("tagGlideImageCheck", result.toString() + " download")
            alreadyDownload = true
            if (isLast) {
//                activity?.showToast(getString(R.string.msg_imges_downloaded))
            }


                btnDone?.visibility = View.VISIBLE
                liDownloads?.visibility = View.GONE


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
        } catch (e: RuntimeException) {
            null
        }
    }


    private fun CallApiSharedData(id: String) {
        if (isInternetAvailable(activity)) {
            val param = ParamAddShareddata().apply {
                referenceId = id
                typeId = Const.PRODUCT_SHARE
            }
            val retrofit = WebService.getRetrofit(activity).create(ApiClient::class.java)
            retrofit.AddSharedProducts(param)
                .enqueue(object : Callback<ResponseAddSharedData> {
                    override fun onFailure(call: Call<ResponseAddSharedData>, t: Throwable) {
                    }

                    override fun onResponse(
                        call: Call<ResponseAddSharedData>,
                        response: Response<ResponseAddSharedData>?
                    ) {
                       // mprogressbar.hideProgressBar()
                    }
                })

        } else {
            NetworkErrorDialog(activity)
        }
    }
    private fun ShareOnWhatsapp(isimg: Boolean, isVideo: Boolean) {

        val baseActivity = BaseActivity()
        val isWhatsUpInstalled =
            baseActivity.isAppInstalled(context, context.getString(R.string.pckg_whtsapp))
        val isWhatsUpBusinessInstalled =
            baseActivity.isAppInstalled(context, context.getString(R.string.pckg_whtsapp_business))

        if (isWhatsUpInstalled) {
            shareDataSocial(context.getString(R.string.pckg_whtsapp), isimg, isVideo)
        } else if (isWhatsUpBusinessInstalled) {
            shareDataSocial(context.getString(R.string.pckg_whtsapp_business), isimg, isVideo)
        } else {
            activity?.showToast(context.getString(R.string.whatsapp_no_installed), Const.ALERT)
        }
    }

    private fun shareDataSocial(pckgName: String, isimg: Boolean, isVideo: Boolean) {
        Log.e("////", "pckgName//" + pckgName)

        val whatsappIntent = Intent(Intent.ACTION_SEND)
        whatsappIntent.setPackage(pckgName)
        activity?.showBottomToast(context.getString(R.string.msg_desc_copeied))
        try {
            Log.e("share", "image: " + isimg.toString())
            Log.e("share", "video: " + isVideo.toString())

            if (isimg && !isVideo) {
                whatsappIntent.type = "image/png" //png
                // whatsappIntent.type = "*/*" //png

                Log.e(
                    "tagGlideImageCheck",
                    "ShareOnWhatsapp() called imageUriArrayGlide " + imageUriArray.size
                )
                for (mImage in imageUriArray) {
                    Log.e(
                        "tagGlideImageCheck",
                        "ShareOnWhatsapp() called imageUriArrayGlide mImage uri is " + mImage
                    )
                }
                if (alreadyDownload) {
                    Log.e(
                        "tagGlideImageCheck",
                        "ShareOnWhatsapp() whatsappIntent.putExtra(Intent.EXTRA_STREAM, imageUriArray)"
                    )
                    try {
                        whatsappIntent.putExtra(Intent.EXTRA_STREAM, imageUriArray)
                    } catch (e: Exception) {
                        Log.e("Stream", e.message.toString())
                    }
                } else {
                    whatsappIntent.putExtra(Intent.EXTRA_STREAM, imageUriArray)
                }
                whatsappIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                activity.startActivityForResult(whatsappIntent, SHARE_DESC)
            }
            else if (!isimg && isVideo) {
                //  whatsappIntent.type = "image/png" //png
                whatsappIntent.type = "*/*" //png

                Log.e(
                    "tagGlideImageCheck",
                    "ShareOnWhatsapp() called imageUriArrayGlide " + imageUriArray.size
                )
                for (mImage in imageUriArray) {
                    Log.e(
                        "tagGlideImageCheck",
                        "ShareOnWhatsapp() called imageUriArrayGlide mImage uri is " + mImage
                    )
                }
                if (alreadyDownload) {
                    Log.e(
                        "tagGlideImageCheck",
                        "ShareOnWhatsapp() whatsappIntent.putExtra(Intent.EXTRA_STREAM, imageUriArray)"
                    )
                    try {
                        whatsappIntent.putExtra(Intent.EXTRA_STREAM, videoUriArray)
                    } catch (e: Exception) {
                        Log.e("Stream", e.message.toString())
                    }
                } else {
                    whatsappIntent.putExtra(Intent.EXTRA_STREAM, videoUriArray)
                }
                whatsappIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                activity.startActivityForResult(whatsappIntent, SHARE_DESC)
            }
            else if (isimg && isVideo) {
                //  whatsappIntent.type = "image/png" //png
                whatsappIntent.type = "*/*" //png

                Log.e(
                    "tagGlideImageCheck",
                    "ShareOnWhatsapp() called imageUriArrayGlide " + imageUriArray.size
                )
                for (mImage in imageUriArray) {
                    Log.e(
                        "tagGlideImageCheck",
                        "ShareOnWhatsapp() called imageUriArrayGlide mImage uri is " + mImage
                    )
                }
                bothUris.clear()
                bothUris.addAll(imageUriArray)
                bothUris.addAll(videoUriArray)


                if (alreadyDownload) {
                    Log.e(
                        "tagGlideImageCheck",
                        "ShareOnWhatsapp() whatsappIntent.putExtra(Intent.EXTRA_STREAM, imageUriArray)"
                    )
                    try {
                        whatsappIntent.putExtra(Intent.EXTRA_STREAM, bothUris)
                    } catch (e: Exception) {
                        Log.e("Stream", e.message.toString())
                    }
                } else {
                    whatsappIntent.putExtra(Intent.EXTRA_STREAM, bothUris)
                }
                whatsappIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                activity.startActivityForResult(whatsappIntent, SHARE_DESC)
            } else {
                whatsappIntent.type = "text/plain"
                whatsappIntent.putExtra(Intent.EXTRA_TEXT, sharedesc)
                activity?.startActivity(whatsappIntent)
            }
        } catch (ex: ActivityNotFoundException) {
            activity?.showToast(context.getString(R.string.whatsapp_no_installed), Const.ALERT)
        }
    }


}
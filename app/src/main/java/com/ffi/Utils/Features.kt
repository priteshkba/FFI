package com.ffi.Utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.*
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.startActivity
import com.ffi.App
import com.ffi.HomeActivity
import com.ffi.R
import com.ffi.WebviewChatActivity
import com.ffi.edit_profile.ResponseProfile
import com.ffi.login.LoginActivity
import com.ffi.model.RespCommonModel
import com.ffi.api.ApiClient
import com.ffi.api.WebService
import com.google.gson.Gson
import com.livechatinc.inappchat.ChatWindowActivity
import com.livechatinc.inappchat.ChatWindowConfiguration.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.lang.Double.parseDouble
import java.net.URISyntaxException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

//var progressDialog: ProgressDialog? = null

private var alertDialogBuilder: AlertDialog.Builder? = null
private var alertDialog: AlertDialog? = null

private var pd: ProgressDialog? = null
private var mAlertDialog: AlertDialog? = null

fun Context.openLiveChatWithProfile(mprogressbar: ProgressBarHandler) {
    if (isInternetAvailable(this@openLiveChatWithProfile)) {
        mprogressbar.showProgressBar()
        val retrofit = WebService.getRetrofit(this@openLiveChatWithProfile).create(ApiClient::class.java)
        retrofit.GetProfile().enqueue(object : Callback<ResponseProfile> {
                override fun onFailure(call: Call<ResponseProfile>, t: Throwable) {
                    try {
                        if (!(this@openLiveChatWithProfile as Activity).isFinishing) {
                            mprogressbar.hideProgressBar()
                        }
                    } catch (e: java.lang.Exception) {
                    }
                }

                override fun onResponse(
                    call: Call<ResponseProfile>,
                    response: Response<ResponseProfile>?
                ) {
                    try {
                        if (!(this@openLiveChatWithProfile as Activity).isFinishing) {
                            mprogressbar.hideProgressBar()
                            val responseurl = response?.body()
                            Log.e("tagLiveChat", "responseurl " + responseurl)
                            if (responseurl != null) {
                                Log.e("tagLiveChat", "responseurl.status " + responseurl.status)
                                if (responseurl.status == API_SUCESS) {
                                    startFullScreenChat(responseurl)
                                }
                            } else {
//                                showToast(
//                                    getString(R.string.msg_common_error),
//                                    Const.ALERT
//                                )
                                startFullScreenChat(null)
                            }
                        }
                    } catch (e: java.lang.Exception) {
                    }
                }
            })
    } else {
        NetworkErrorDialog(this@openLiveChatWithProfile)
    }
}


fun Context.startFullScreenChat(mResponseProfile: ResponseProfile?) {
    try{
        Log.e("tagLiveChat", "mResponseProfile.data " + mResponseProfile?.data)
        var mEmailMobile = ""
        if(mResponseProfile?.data != null){
//            var fullScreenChatWindow: ChatWindowView? = null
//            Log.e("tagLiveChat", "getEmail() " + mResponseProfile.data.emailAddress)
//            Log.e("tagLiveChat", "getMobileNo() " + mResponseProfile.data.mobileNumber)
//
            mEmailMobile = if(mResponseProfile?.data?.emailAddress != null && !TextUtils.isEmpty(mResponseProfile.data.emailAddress)){
                mResponseProfile?.data?.emailAddress
            }
            else {
                if(mResponseProfile?.data?.mobileNumber != null && !TextUtils.isEmpty(mResponseProfile.data.mobileNumber)){
                    mResponseProfile?.data?.mobileNumber
                }
                else {
                    ""
                }
            }
//
//            Log.e("tagLiveChat", "mEmailMobile " + mEmailMobile)
//
//            App.mChatWindowConfiguration = ChatWindowConfiguration(
//                Const.liveChat_licence,
//                Const.liveChat_group,
//                getFullName(),
//                mEmailMobile,
//                null
//            )
//
//            if (fullScreenChatWindow == null) {
//                fullScreenChatWindow = ChatWindowView.createAndAttachChatWindowInstance(this@startFullScreenChat as Activity)
//                fullScreenChatWindow.setUpWindow(App.mChatWindowConfiguration)
////        fullScreenChatWindow.setUpListener(this@MyAccountFragment)
////        fullScreenChatWindow.setUpListener(null)
//                fullScreenChatWindow.initialize()
//            }
//            fullScreenChatWindow?.showChatWindow()
        }

        val isLogin = getUserLoginStatus()
        //showHashKey()

        val intent = Intent(this@startFullScreenChat, ChatWindowActivity::class.java)
        // val intent = Intent(this@startFullScreenChat, WebviewChatActivity::class.java)
        intent.putExtra(Const.TITLE, getString(R.string.strLiveSupport))
        intent.putExtra(KEY_LICENCE_NUMBER, Const.liveChat_licence)
        intent.putExtra(KEY_GROUP_ID, Const.liveChat_group)

        Log.e("tagLiveChat", "isLogin " + isLogin)

        if (isLogin) {
            intent.putExtra(KEY_VISITOR_NAME, getFullName())
            intent.putExtra(KEY_VISITOR_EMAIL, mEmailMobile)
        } else {
            intent.putExtra(KEY_VISITOR_NAME, "")
            intent.putExtra(KEY_VISITOR_EMAIL, "")
        }
        startActivity(intent)
    }catch (e: java.lang.Exception){
        Log.e("tagLiveChat", "e.message " + e.message)
    }
}


fun Context.showToast(msg: String, type: String) {
    // Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    try{
        if (this!=null && !(this as Activity).isFinishing) {
            if (type.equals(Const.ALERT)) {
                if (App.isDialogOpen)
                    return
                val dialog = android.app.AlertDialog.Builder(this)
                    .setTitle(this.getString(R.string.app_name))
                    .setMessage(msg)
                    .setNegativeButton("OK", DialogInterface.OnClickListener { dialog, which ->
                        dialog.cancel()
                        App.isDialogOpen = false
                    }).setCancelable(false)
                if (this!=null && !(this as Activity).isFinishing && dialog != null)
                    dialog.show()
                App.isDialogOpen = true
            } else if (type.equals(Const.TOAST)) {
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
            }
        }
    }catch (e: java.lang.Exception){}
}

fun Context.showBottomToast(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}

fun Context.showLoginMsg() {
    val dialog = CustomDialog(this, "",
        getString(R.string.msg_login_require),
        "Yes",
        "No",
        CustomDialog.ItemReturnListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            App.isRedirect = true
        })

    if (dialog != null)
        dialog.show()
    /*  val dialog = android.app.AlertDialog.Builder(this)
          .setTitle(this.getString(R.string.app_name))
          .setMessage(R.string.msg_login_require)
          .setPositiveButton("NO", DialogInterface.OnClickListener { dialog, which ->
              dialog.cancel()
              App.isDialogOpen = false
          })
          .setNegativeButton("YES", DialogInterface.OnClickListener { dialog, which ->

          }).setCancelable(false)
    */
}

fun Context.showLog(tag: String, msg: String) {
    Log.d(tag, msg)
}

fun Context.isValidString(value: String): Boolean {
    var isValid = false
    if (value != null && value.isNotEmpty()) {
        isValid = true
    }

    return isValid
}

fun Context.isValidEmail(email: String): Boolean = email.isNotEmpty() &&
        Patterns.EMAIL_ADDRESS.matcher(email).matches()

fun Context.isMatchPassword(password: String, confirm_pwd: String): Boolean = password.isNotEmpty()
        && confirm_pwd.isNotEmpty()
        && password.equals(confirm_pwd)

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

fun Context.getFormattedDate(
    strDate: String, sourceFormate: String,
    destinyFormate: String
): String {
    var df: SimpleDateFormat

    df = SimpleDateFormat(sourceFormate)
    var date: Date? = null
    var formatedDate = ""
    try {
        if (!strDate.isEmpty()) {
            date = df.parse(strDate)
        }
    } catch (e: ParseException) {
        e.printStackTrace()
    }

    df = SimpleDateFormat(destinyFormate)
    if (date != null) {
        formatedDate = df.format(date)
    }
    return formatedDate
}


@SuppressLint("NewApi")
@Throws(URISyntaxException::class)
fun getFilePath(context: Context, uri: Uri): String? {
    var uri = uri
    var selection: String? = null
    var selectionArgs: Array<String>? = null
    // Uri is different in versions after KITKAT (Android 4.4), we need to
    if (Build.VERSION.SDK_INT >= 19 && DocumentsContract.isDocumentUri(
            context.applicationContext,
            uri
        )
    ) {
        if (isExternalStorageDocument(uri)) {
            val docId = DocumentsContract.getDocumentId(uri)
            val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
        } else if (isDownloadsDocument(uri)) {
            val id = DocumentsContract.getDocumentId(uri)
            uri = ContentUris.withAppendedId(
                Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id)
            )
        } else if (isMediaDocument(uri)) {
            val docId = DocumentsContract.getDocumentId(uri)
            val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val type = split[0]
            if ("image" == type) {
                uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            } else if ("video" == type) {
                uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            } else if ("audio" == type) {
                uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            }
            selection = "_id=?"
            selectionArgs = arrayOf(split[1])
        }
    }
    if ("content".equals(uri.scheme!!, ignoreCase = true)) {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        var cursor: Cursor? = null
        try {
            cursor = context.contentResolver
                .query(uri, projection, selection, selectionArgs, null)
            val column_index = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            if (cursor!!.moveToFirst()) {
                return cursor!!.getString(column_index)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    } else if ("selectedfile".equals(uri.scheme!!, ignoreCase = true)) {
        return uri.path
    }
    return null
}

fun isExternalStorageDocument(uri: Uri): Boolean {
    return "com.android.externalstorage.documents" == uri.authority
}

fun isDownloadsDocument(uri: Uri): Boolean {
    return "com.android.providers.downloads.documents" == uri.authority
}

fun isMediaDocument(uri: Uri): Boolean {
    return "com.android.providers.media.documents" == uri.authority
}

fun getAndroidVersion(): String? {
    val release: String = Build.VERSION.RELEASE
    return release
}

fun getDeviceName(): String? {
    val manufacturer = Build.MANUFACTURER
    val model = Build.MODEL
    return if (model.startsWith(manufacturer)) {
        model
    } else manufacturer + " " + model
}


fun toDateString(timeMilli: Long): String {
    val calc = Calendar.getInstance()
    calc.setTimeInMillis(timeMilli)
    return String.format(
        Locale.CHINESE,
        "%04d.%02d.%02d %02d:%02d:%02d:%03d",
        calc.get(Calendar.YEAR),
        calc.get(Calendar.MONTH) + 1,
        calc.get(Calendar.DAY_OF_MONTH),
        calc.get(Calendar.HOUR_OF_DAY),
        calc.get(Calendar.MINUTE),
        calc.get(Calendar.SECOND),
        calc.get(Calendar.MILLISECOND)
    )
}

fun getVersionName(context: Context): String? {
    val packageManager = context.packageManager
    var packInfo: PackageInfo? = null
    var version: String? = null
    try {
        packInfo = packageManager.getPackageInfo(context.packageName, 0)
        version = packInfo!!.versionName
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
    }

    return version
}

fun getVersionCode(context: Context): Int {
    val packageManager = context.packageManager
    var packInfo: PackageInfo? = null
    var version = 0
    try {
        packInfo = packageManager.getPackageInfo(context.packageName, 0)
        version = packInfo!!.versionCode
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
    }

    return version
}

fun getImageUri(inContext: Context, inImage: Bitmap): Uri {
    val bytes = ByteArrayOutputStream()
    inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
    val path =
        MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null)
    return Uri.parse(path)
}

fun Context.getDeviceId(): String {
    return Settings.Secure.getString(
        getContentResolver(),
        Settings.Secure.ANDROID_ID
    )
}

fun isInternetAvailable(context: Context): Boolean {

    val cm: ConnectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo()?.isConnected()!!
}


fun NetworkErrorDialog(context: Context) {
    android.app.AlertDialog.Builder(context)
        .setTitle(context.getString(R.string.app_name))
        .setMessage(context.getString(R.string.err_no_internet))
        .setNegativeButton("OK", null)
        .setCancelable(false)
        .show()
}


fun Context.showBackButtonExitDialog(mActivity: Activity) {
    android.app.AlertDialog.Builder(this)
        .setTitle(getString(R.string.app_name))
        .setMessage(getString(R.string.msg_confirmation_exit))
        .setPositiveButton("Yes",
            DialogInterface.OnClickListener { dialog, which ->
                mActivity.finish()
            })
        .setNegativeButton("No", null)
        .setCancelable(false)
        .show()
}


fun Context.getOutputMediaFile(imageFileName: String): File? {
    val mediaStorageDir = File(
        Environment.getExternalStorageDirectory(),
        getString(R.string.app_name)
    )

    if (!mediaStorageDir.exists()) {
        if (!mediaStorageDir.mkdirs()) {
            return null
        }
    }
//    val timeStamp = SimpleDateFormat("ddMMyyyy_HHmm").format(Date())
    val mediaFile: File
    var mImageName = imageFileName
    mediaFile = File(mediaStorageDir.path + File.separator.toString() + mImageName)
    if (mediaFile.exists()) {
        mediaFile.delete()
    }
    mediaFile.createNewFile()
    return mediaFile
}

fun Context.storeImage(image: Bitmap, mFile: File) {
    try {
        val fos = FileOutputStream(mFile)
        image.compress(Bitmap.CompressFormat.JPEG, 90, fos)
        fos.close()
    } catch (e: Exception) {
        Log.d("tagGlideImageCheck", "File not found: " + e.message)
    }
}

fun CheckNameonlyNumber(text: String): Boolean {
    try {
        val num = parseDouble(text)
        return true //only numbers
    } catch (e: NumberFormatException) {
        return false//string
    }
}


fun Context.handleForbiddenResponse(mResponse: okhttp3.Response): okhttp3.Response? {
    try{
        Log.e("tagResponseCode", "handleForbiddenResponse called")
        if(mResponse.peekBody(Long.MAX_VALUE) != null && !TextUtils.isEmpty(
                mResponse.peekBody(Long.MAX_VALUE).toString()
            )){
            Log.e(
                "tagResponseCode",
                "handleForbiddenResponse  " + mResponse.peekBody(Long.MAX_VALUE).string()
            )
            var mCommonModel = Gson().fromJson(
                mResponse.peekBody(Long.MAX_VALUE).string(),
                RespCommonModel::class.java
            )
            if(mCommonModel != null && mCommonModel.status != null) {
                Log.e(
                    "tagResponseCode",
                    "handleForbiddenResponse  " + mCommonModel.status.toString()
                )
//                if(mCommonModel.status == -999){
//                    showAppUpdateAlert()
//                    return null
//                }
            }
        }
    }catch (e: java.lang.Exception){
        Log.e("tagResponseCode", "response handleForbiddenResponse error is " + e.message)
    }
    return mResponse
}


fun Context.handleForbiddenResponseJson(mResponse: String?): Boolean {
    try{
        Log.e("tagResponseCode", "handleForbiddenResponse called")
        if(mResponse != null && !TextUtils.isEmpty(mResponse)){
            Log.e(
                "tagResponseCode",
                "handleForbiddenResponse  " + mResponse
            )
            var mCommonModel = Gson().fromJson(
                mResponse,
                RespCommonModel::class.java
            )
            if(mCommonModel != null && mCommonModel.status != null) {
                Log.e(
                    "tagResponseCode",
                    "handleForbiddenResponse  " + mCommonModel.status.toString()
                )
                if(mCommonModel.status == -999){
                    showAppUpdateAlert()
                    return false
                }
            }
            return true
        }
        return true
    }catch (e: java.lang.Exception){
        Log.e("tagResponseCode", "response handleForbiddenResponse error is " + e.message)
        return true
    }
    return true
}


fun Context.showAppUpdateAlert() {
    Handler(this@showAppUpdateAlert.mainLooper).post(Runnable {
        mAlertDialog?.dismiss()
//        val mAlertBuilder = AlertDialog.Builder(this@showAppUpdateAlert, R.style.AppAlertDialogTheme)
//        mAlertBuilder.setMessage(getString(R.string.strAppUpdateAlert1))
//            .setCancelable(false)
//            .setPositiveButton(getString(R.string.strLaunchPlayStore),
//                DialogInterface.OnClickListener { dialog, which ->
//                    try {
//                        val appPackageName: String = packageName // getPackageName() from Context or Activity object
//                        try {
//                            startActivity(
//                                Intent(
//                                    Intent.ACTION_VIEW, Uri.parse(
//                                        "market://details?id=$appPackageName"
//                                    )
//                                )
//                            )
//                        } catch (anfe: ActivityNotFoundException) {
//                            startActivity(
//                                Intent(
//                                    Intent.ACTION_VIEW, Uri.parse(
//                                        "https://play.google.com/store/apps/details?id=$appPackageName"
//                                    )
//                                )
//                            )
//                        }
//                    } catch (e: java.lang.Exception) {
//                        e.printStackTrace()
//                    }
//                })
//        mAlertDialog = mAlertBuilder.create()
//        mAlertDialog?.show()

        val mAlertBuilder = AlertDialog.Builder(this@showAppUpdateAlert, R.style.CustomAlertDialog)
        val mParentView: View = LayoutInflater.from(this@showAppUpdateAlert).inflate(
            R.layout.dialog_alert_update,
            null
        )
        val tvPlayStore = mParentView.findViewById(R.id.tvPlayStore) as TextView

        mAlertBuilder.setView(mParentView)
        mAlertBuilder
            .setTitle("")
            .setMessage("")
            .setCancelable(false)

        tvPlayStore.setOnClickListener {
            try {
                val appPackageName: String = packageName // getPackageName() from Context or Activity object
                try {
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW, Uri.parse(
                                "market://details?id=$appPackageName"
                            )
                        )
                    )
                } catch (anfe: ActivityNotFoundException) {
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW, Uri.parse(
                                "https://play.google.com/store/apps/details?id=$appPackageName"
                            )
                        )
                    )
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
        mAlertDialog = mAlertBuilder.create()
        mAlertDialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        mAlertDialog?.window?.setGravity(Gravity.CENTER)
        mAlertDialog?.setCancelable(false)
        mAlertDialog?.show()
        mAlertDialog?.setOnShowListener(DialogInterface.OnShowListener {
            try {
                if (mAlertDialog != null && mAlertDialog?.window != null) {
                    mAlertDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    mAlertDialog?.window?.setLayout(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    mAlertDialog?.window?.setGravity(Gravity.TOP)
                }
            } catch (e: java.lang.Exception) {
            }
        })

    })
}

fun Activity.whatAppToFFIBussinessNumber(context: Context){
    val url =
        "https://api.whatsapp.com/send?phone=" + " " + getString(R.string.FFIBussinessWhatappNumber)
    val simplaWhatappPackage = "com.whatsapp"
    val WhatappBussinessPackage = "com.whatsapp.w4b"
    val i = Intent(Intent.ACTION_VIEW)
    var hasWhatApp = false
    if (isAppInstalled(context,simplaWhatappPackage)) {
        hasWhatApp = true
        i.setPackage(simplaWhatappPackage)
    } else if (isAppInstalled(context,WhatappBussinessPackage)) {
        hasWhatApp = true
        i.setPackage(WhatappBussinessPackage)
    } else {
        hasWhatApp = false
    }
    if (hasWhatApp) {
        i.data = Uri.parse(url)
        startActivity(i)
    } else {
        showToast(getString(R.string.whatappNotInstalled), Const.ALERT)
    }
}
fun isAppInstalled(context: Context,packageName: String): Boolean {
    val pm = context.getPackageManager();
    var app_installed = false
    try {
        pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
        app_installed = true;
    } catch (e: PackageManager.NameNotFoundException) {
        app_installed = false;
    }
    return app_installed;
}


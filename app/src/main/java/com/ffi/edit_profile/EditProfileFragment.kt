package com.ffi.edit_profile

import android.Manifest
import android.app.Activity.RESULT_OK
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.androidnetworking.interfaces.UploadProgressListener
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.ffi.*
import com.ffi.api.Apis
import com.ffi.Utils.*
import com.ffi.home.HomeFragment
import com.ffi.api.ApiClient
import com.ffi.api.WebService
import com.ffi.login.UpdateSocialLoginModel
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import kotlinx.android.synthetic.main.fragment_edit_profile.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONException
import org.json.JSONObject
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class EditProfileFragment() : Fragment(), View.OnClickListener {

    var isShowTab: Boolean = false
    var BlockBackArrow: Boolean = false

    fun getInstance(isShowTab: Boolean, BlockBackArrow: Boolean): EditProfileFragment {
        val mEditProfileFragment = EditProfileFragment()
        val args = Bundle()
        args.putBoolean(Const.IS_SHOW_TAB, isShowTab)
        args.putBoolean(Const.IS_BLOCK_BACK_ARROW, BlockBackArrow)
        mEditProfileFragment.setArguments(args)
        return mEditProfileFragment
    }

    private fun getArgumentData() {
        if(arguments != null){
            if(arguments?.containsKey(Const.IS_SHOW_TAB) != null &&
                arguments?.containsKey(Const.IS_SHOW_TAB)!!){
                isShowTab = arguments?.getBoolean(Const.IS_SHOW_TAB)!!
            }
            if(arguments?.containsKey(Const.IS_BLOCK_BACK_ARROW) != null &&
                arguments?.containsKey(Const.IS_BLOCK_BACK_ARROW)!!){
                BlockBackArrow = arguments?.getBoolean(Const.IS_BLOCK_BACK_ARROW)!!
            }
        }
    }

    var homeActivity: HomeActivity? = null
    lateinit var mprogressbar: ProgressBarHandler

    val RC_CAMERA_PERM = 123
    val RC_GALLERY_PERM = 124
    private var currentPhotoPath = ""
    var finalImageFile: File? = null
    var posGender: Int = 0
    lateinit var arraySpinner: Array<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_profile, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        getArgumentData()
        init()

        setListener()
        CallApiGetProfile()
    }

    private fun CallApiGetProfile() {
        if (isInternetAvailable(requireActivity())) {
            mprogressbar.showProgressBar()
            val retrofit = WebService.getRetrofit(requireActivity()).create(ApiClient::class.java)
            retrofit.GetProfile()
                .enqueue(object : Callback<ResponseProfile> {
                    override fun onFailure(call: Call<ResponseProfile>, t: Throwable) {
                        mprogressbar.hideProgressBar()
                    }

                    override fun onResponse(
                        call: Call<ResponseProfile>,
                        response: Response<ResponseProfile>?
                    ) {
                        if (response != null) {
                            if (response.isSuccessful) {
                                val responseurl = response.body()

                                if (responseurl != null) {
                                    if (responseurl.status == API_SUCESS) {
                                        if (isAdded) {
                                            setData(responseurl.data)
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
        }

    }

    private fun setData(data: Data) {
        if (data.emailAddress.isEmpty()) {
            edt_email.isEnabled = true
        } else {
            edt_email.setText(data.emailAddress)
            edt_email.isEnabled = false
        }

        if (data.mobileNumber.isEmpty()) {
            edt_phone_no.isEnabled = true
        } else {
            edt_phone_no.setText(data.mobileNumber)
            edt_phone_no.isEnabled = false
        }

        edt_fname.setText(data.firstName)
        edt_lname.setText(data.lastName)

        tvphonecode.text = requireActivity().getString(R.string.plus) + data.phoneCode

        if (data.media.isEmpty()) {
            mprogressbar.hideProgressBar()
        }



        try {
            GlideApp.with(requireActivity())
                .asBitmap()
                .load(getMediaLink() + data.media)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        glideAnimation: Transition<in Bitmap>?
                    ) {
                        if (isAdded) {
                            val file =
                                GetFileFromBitmap().getFileFromBitmap(resource, requireActivity())
                            currentPhotoPath = file?.absolutePath!!
                            ci_userprofile.setImageBitmap(resource)
                            mprogressbar.hideProgressBar()
                        }
                    }

                    override fun onLoadFailed(errorDrawable: Drawable?) {
                        super.onLoadFailed(errorDrawable)
                        mprogressbar.hideProgressBar()
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                        mprogressbar.hideProgressBar()
                    }

                })
        } catch (e: IllegalStateException) {
            e.printStackTrace()
            mprogressbar.hideProgressBar()
            Log.e("res", "error" + e.toString())
        }

        for (i in arraySpinner.indices) {
            if (data.gender.equals(arraySpinner.get(i))) {
                spGender.setSelection(i)
            }
        }
    }

    private fun init() {
        mprogressbar = ProgressBarHandler(requireActivity())

        homeActivity = activity as HomeActivity
        ivBack.visibility = View.VISIBLE
        ivEdit.visibility = View.GONE
        txt_toolbar.text = getString(R.string.title_profile)

        if (isShowTab) {
            homeActivity?.navigation?.visibility = View.VISIBLE
        } else {
            homeActivity?.navigation?.visibility = View.GONE
        }


        // edt_businessname.setText("")

        arraySpinner = arrayOf(getString(R.string.male), getString(R.string.female))
        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
            requireActivity(),
            android.R.layout.simple_spinner_item, arraySpinner
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spGender.adapter = adapter

        spGender.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>?,
                selectedItemView: View?,
                position: Int,
                id: Long
            ) {
                posGender = position + 1
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
                posGender = 1
            }
        }
    }

    private fun setListener() {
        btn_done.setOnClickListener(this)
        ivBack.setOnClickListener(this)
        btn_done.setOnClickListener(this)
        ci_userprofile.setOnClickListener(this)
        iv_edit_img.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBack -> {
                if (BlockBackArrow)
                    return
                homeActivity?.selectLastMenu()
//                homeActivity?.replaceFragment(MyAccountFragment(), "4")
                if (isShowTab) {
                    fragmentManager?.popBackStack()
                } else {
                    homeActivity?.replaceFragment(HomeFragment(), "1")
                }
            }
            R.id.btn_done -> {
                isValid()
            }
            R.id.ci_userprofile -> {
                dailogOpenAttachments()
            }
            R.id.iv_edit_img -> {
                dailogOpenAttachments()
            }
        }
    }

    private fun isValid() {
        val strfname = edt_fname.text.toString().trim()
        val strlname = edt_lname.text.toString().trim()
        val strphone = edt_phone_no.text.toString().trim()
        val stremail = edt_email.text.toString().trim()

        if (currentPhotoPath.isEmpty()) {
            activity?.showToast(getString(R.string.err_profile_photo), Const.ALERT)
        } else if (strfname.isEmpty()) {
            activity?.showToast(getString(R.string.err_fname), Const.ALERT)
            edt_fname.requestFocus()
        } else if (strlname.isEmpty()) {
            activity?.showToast(getString(R.string.err_lname), Const.ALERT)
            edt_lname.requestFocus()
        } else if (strphone.isEmpty()) {
            activity?.showToast(getString(R.string.err_phone_number), Const.ALERT)
            edt_phone_no.requestFocus()
        } else if (stremail.isEmpty()) {
            activity?.showToast(getString(R.string.err_email), Const.ALERT)
            edt_email.requestFocus()
        } else if (!activity?.isValidEmail(stremail)!!) {
            activity?.showToast(getString(R.string.err_valid_email), Const.ALERT)
            edt_email.requestFocus()
        } else {
            if (isInternetAvailable(requireActivity())) {
                CallApiEditProfile()
            } else {
                NetworkErrorDialog(requireActivity())
            }
        }
        //}
    }

    private fun CallApiEditProfile() {
        mprogressbar.showProgressBar()

        val stremail = edt_email.text.toString()
        val strphone = edt_phone_no.text.toString()
        val strlname = edt_lname.text.toString()
        val strfname = edt_fname.text.toString()


//        val okHttpClient = UnsafeOkHttpClient.getUnsafeOkHttpClient()
//        AndroidNetworking.initialize(requireActivity(), okHttpClient)
//        AndroidNetworking.enableLogging()
//        AndroidNetworking.upload(Apis.BASE_URL + Apis.API_UPDATE_PROFILE)
//            .addMultipartFile("ProfileImage", File(currentPhotoPath))
//            .addMultipartParameter("Gender", posGender.toString())
//            .addMultipartParameter("PhoneCode", "91")
//            .addMultipartParameter("MobileNumber", strphone)
//            .addMultipartParameter("EmailAddress", stremail)
//            .addMultipartParameter("LastName", strlname)
//            .addMultipartParameter("FirstName", strfname)
//            .addHeaders(Const.OS_VERSION, getAndroidVersion()!!)
//            .addHeaders(Const.DEVICE_NAME, getDeviceName()!!)
//            .addHeaders(Const.USER_TOKEN, getUserToken())
//            .addHeaders(Const.USER_ID, getUserId())
//            .addHeaders(Const.LANGUAGE_ID, Const.LANG_ENGLISH)
//            .addHeaders(Const.DEVICE_ID, activity?.getDeviceId())
//            .addHeaders(Const.APP_ID, Const.APP_ID_VERSION)
//            .addHeaders(Const.APP_VERSION_NO, BuildConfig.VERSION_NAME)
//            .addHeaders(Const.DEVICE_TYPE, Const.DEVICE_TYPE_ANDROID)
//            .setPriority(Priority.IMMEDIATE)
//            .build()
//            .setUploadProgressListener(UploadProgressListener { bytesUploaded, totalBytes -> // do anything with progress
//            })
//            .getAsJSONObject(object : JSONObjectRequestListener {
//                override fun onResponse(response: JSONObject) {
//                    // do anything with response
//                    mprogressbar.hideProgressBar()
//                    try {
//                        val gson = Gson()
//                        if(WebService.context.handleForbiddenResponseJson(response.toString())){
//                            val responseProfile =
//                                gson.fromJson(response.toString(), ResponseProfile::class.java)
//                            val data = responseProfile.data
//
//
//                            setDataPrefrence(data)
//                            activity?.showBottomToast(getString(R.string.msg_profile_updated))
////                        homeActivity?.replaceFragment(MyAccountFragment(), "4")
//                            if (BlockBackArrow) {
//                                homeActivity?.replaceFragment(HomeFragment(), "1")
//                            } else {
//                                fragmentManager?.popBackStack()
//                            }
//                        }
//                    } catch (e: JSONException) {
//                        e.printStackTrace()
//                    }
//
//                }
//
//                override fun onError(error: ANError) {
//                    mprogressbar.hideProgressBar()
//                    Log.e("image upload", error.toString())
//                    // handle error
//                }
//            })


        if(activity != null && !activity?.isFinishing!!){
            val retrofit = WebService.getRetrofit(activity?.applicationContext!!).create(ApiClient::class.java)

            var mMultipartBody: MultipartBody.Part? = null
            if (currentPhotoPath != null && !TextUtils.isEmpty(currentPhotoPath)) {
                var mUploadFile = File(currentPhotoPath)
                if (mUploadFile != null && mUploadFile.exists()) {
                    val requestFile = RequestBody.create(
                        "multipart/form-data".toMediaTypeOrNull(),
                        mUploadFile!!
                    )
                    mMultipartBody = MultipartBody.Part.createFormData(
                        "ProfileImage",
                        mUploadFile!!.getName(),
                        requestFile
                    )
                }
            }

            if(mMultipartBody != null){
                retrofit.UpdateProfile(mMultipartBody!!, posGender, "91", strphone.toLong(), stremail, strlname, strfname)
                    .enqueue(mCallbackResponseProfile)
            }
            else {
                retrofit.UpdateProfile(posGender, "91", strphone.toLong(), stremail, strlname, strfname)
                    .enqueue(mCallbackResponseProfile)
            }
        }

    }


    val mCallbackResponseProfile = object : Callback<ResponseProfile> {
        override fun onFailure(call: Call<ResponseProfile>, t: Throwable) {
            mprogressbar.hideProgressBar()
        }

        override fun onResponse(
            call: Call<ResponseProfile>,
            response: Response<ResponseProfile>?
        ) {
            try{
                mprogressbar.hideProgressBar()
                if (response != null) {
                    if (response.isSuccessful) {
                        val responseLogin = response.body()

                        if (responseLogin != null && responseLogin.data != null) {
                            val data = responseLogin.data

                            setDataPrefrence(data)
                            activity?.showBottomToast(getString(R.string.msg_profile_updated))
//                        homeActivity?.replaceFragment(MyAccountFragment(), "4")
                            Log.e("tagMediaLoad", "App.fragmentaccount called ")
                            if(App.fragmentaccount != null){
                                Log.e("tagMediaLoad", "App.fragmentaccount != null called ")
                                App.fragmentaccount.setData()
                            }
                            if (BlockBackArrow) {
                                homeActivity?.replaceFragment(HomeFragment(), "1")
                            } else {
                                fragmentManager?.popBackStack()
                            }
                        }
                    }
                }
            }catch (e: Exception){}
        }
    }

    private fun setDataPrefrence(data: Data) {
        storeFirstName(data.firstName)
        storeLastName(data.lastName)
        storeMobileNo(data.mobileNumber)
        storePhoneCode(data.phoneCode)
        storeEmail(data.emailAddress)
        storeProfileUrl(data.media)
    }


    private fun dailogOpenAttachments() {
        try {
            val dialog = Dialog(requireActivity())
            val window = dialog.window
            window!!.setLayout(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
            dialog.setContentView(R.layout.dailog_select_image_attachment)
            val tvGallery = dialog.findViewById<TextView>(R.id.tvGallery)
            val tvCamera = dialog.findViewById<TextView>(R.id.tvCamera)
            val tvCancel = dialog.findViewById<TextView>(R.id.tvCancel)
            tvGallery.setOnClickListener {
                dialog.dismiss()
                openGallery()
            }
            tvCamera.setOnClickListener {
                dialog.dismiss()
                openCamera()
            }
            tvCancel.setOnClickListener { dialog.dismiss() }
            dialog.setCancelable(false)
            dialog.setCanceledOnTouchOutside(false)
            dialog.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //  @AfterPermissionGranted(RC_GALLERY_PERM)
    fun openGallery() {
        if (hasStoragePermission()) {
            try {
                val galleryIntent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(galleryIntent, RC_GALLERY_PERM)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        } else {
            EasyPermissions.requestPermissions(
                this,
                "This app needs access to your storage so you can select picture.",
                RC_GALLERY_PERM,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        }
    }

    //@AfterPermissionGranted(RC_CAMERA_PERM)
    fun openCamera() {
        if (hasCameraPermission()) {
            try {
                val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                if (takePictureIntent.resolveActivity(activity?.packageManager!!) != null) {
                    var photoFile: File? = null
                    try {
                        photoFile = createImageFile()
                    } catch (ex: IOException) {
                        ex.printStackTrace()
                    }
                    if (photoFile != null) {
                        val photoURI =
                            FileProvider.getUriForFile(
                                requireActivity(),
                                requireActivity().packageName,
                                photoFile
                            )
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                        startActivityForResult(takePictureIntent, RC_CAMERA_PERM)
                    }
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        } else {
            EasyPermissions.requestPermissions(
                this,
                "This app needs access to your camera so you can take pictures.",
                RC_CAMERA_PERM,
                Manifest.permission.CAMERA
            )
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File? {
        val timeStamp =
            SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)
                .format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir: File? =
            activity?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
            imageFileName,  /* prefix */
            ".png",  /* suffix */
            storageDir /* directory */
        )
        currentPhotoPath = image.absolutePath
        return image
    }


    private fun hasCameraPermission(): Boolean {
        return EasyPermissions.hasPermissions(requireActivity(), Manifest.permission.CAMERA)
    }

    private fun hasStoragePermission(): Boolean {
        return EasyPermissions.hasPermissions(
            requireActivity(),
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
            //  Toast.makeText(this, "Returned from app settings to activity", Toast.LENGTH_SHORT).show();
        }
        if (requestCode == RC_CAMERA_PERM && resultCode == RESULT_OK) {
            try {
                File(currentPhotoPath)
                GlideApp.with(this)
                    .load(currentPhotoPath)
                    .placeholder(R.drawable.profileplaceholder)
                    .into(ci_userprofile)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
        if (requestCode == RC_GALLERY_PERM && resultCode == RESULT_OK) {
            try {
                val selectedImageURI = data?.data
                finalImageFile = File(getRealPathFromURI(selectedImageURI))
                currentPhotoPath = finalImageFile.toString()
                File(currentPhotoPath)
                GlideApp.with(this)
                    .load(currentPhotoPath)
                    .placeholder(R.drawable.profileplaceholder)
                    .into(ci_userprofile)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getRealPathFromURI(uri: Uri?): String? {
        var path = ""
        if (activity?.contentResolver != null) {
            val cursor: Cursor =
                activity?.contentResolver!!.query(uri!!, null, null, null, null)!!
            if (cursor != null) {
                cursor.moveToFirst()
                val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
                path = cursor.getString(idx)
                cursor.close()
            }
        }
        return path
    }

    class GetFileFromBitmap {
        fun getFileFromBitmap(bitmap: Bitmap, context: Context): File? {
            val cache = context.externalCacheDir
            val shareFile = File(cache, "myFile.jpg")
            try {
                val out = FileOutputStream(shareFile)
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                out.flush()
                out.close()
                return shareFile
            } catch (e: Exception) {

            }
            return null
        }
    }
}
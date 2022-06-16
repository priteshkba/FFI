package com.ffi.my_orderdetails

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.Build
import java.io.ByteArrayOutputStream
import android.provider.DocumentsContract
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.LinearLayout
import android.widget.TextView
import android.annotation.SuppressLint
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.androidnetworking.interfaces.UploadProgressListener
import com.ffi.BuildConfig
import android.content.ContentUris
import com.ffi.GlideApp
import com.ffi.HomeActivity
import com.ffi.R
import com.ffi.Utils.*
import com.ffi.api.ApiClient
import com.ffi.api.Apis
import com.ffi.api.WebService
import com.ffi.edit_profile.ResponseProfile
import com.ffi.model.ResponseAddSharedData
import com.ffi.writereview.WriteReviewActivity
import com.google.gson.Gson
import kotlinx.android.synthetic.main.custom_toolbar.*
import kotlinx.android.synthetic.main.dialog_add_note.*
import kotlinx.android.synthetic.main.dialog_return_product_reason.*
import kotlinx.android.synthetic.main.fragment_order_detail.*
import org.json.JSONException
import org.json.JSONObject
import pub.devrel.easypermissions.EasyPermissions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import okhttp3.RequestBody
import okhttp3.MultipartBody
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import android.graphics.Bitmap
import android.graphics.BitmapFactory


class OrderDetailFragment() : Fragment(), View.OnClickListener {

    var OId: String = ""
    val isshowtab: Boolean = false
    val RC_CAMERA_PERM = 123
    val RC_GALLERY_PERM = 124
    private var currentPhotoPath = ""
    var finalImageFile: File? = null
    lateinit var photoFile: File

    fun getInstance(OId: String, isshowtab: Boolean): OrderDetailFragment {
        val mOrderDetailFragment = OrderDetailFragment()
        val args = Bundle()
        args.putString(Const.ID, OId)
        args.putBoolean(Const.IS_SHOW_TAB, isshowtab)
        mOrderDetailFragment.setArguments(args)
        return mOrderDetailFragment
    }

    private fun getArgumentData() {
        if (arguments != null) {
            if (arguments?.containsKey(Const.ID) != null &&
                arguments?.containsKey(Const.ID)!!
            ) {
                OId = arguments?.getString(Const.ID)!!
            }
        }
    }

    lateinit var mprogressbar: ProgressBarHandler
    lateinit var orderitem_list: OrderItemAdapter
    lateinit var order_note_adapter: AdapterNotes
    var homeActivity: HomeActivity? = null
    var order_note_list: ArrayList<Note> = ArrayList()
    var order_item_list: ArrayList<Item> = ArrayList()

    // lateinit var order_note_list: ArrayList<ResponseOrderDetail>
    //lateinit var order_item_list: ArrayList<ResponseOrderDetail>
    lateinit var return_return: ArrayList<ProductReturnReasonResponse.DataEntity>
    var dialogNoteImage: Dialog? = null
    var dialogReturn: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_order_detail, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mprogressbar = ProgressBarHandler(requireActivity())

        homeActivity = activity as HomeActivity

        getArgumentData()

        ivBack.visibility = View.VISIBLE
        ivEdit.visibility = View.GONE
        txt_toolbar.text = getString(R.string.title_my_order)


        ivBack.setOnClickListener(this)
        liwhtsapp.setOnClickListener(this)
        order_note_list = ArrayList()
        order_item_list = ArrayList()
        return_return = ArrayList()
        CallApiOrderDetails(true)
        CallGetAddressData(true);
        setRefresh()
    }

    private fun CallGetAddressData(isprogress: Boolean) {

        if (isInternetAvailable(requireActivity())) {

            if (isprogress)
                mprogressbar.showProgressBar()

            val param = ParamOrderAddressDeatils().apply {
                orderId = OId
            }

//            val okHttpClient = UnsafeOkHttpClient.getUnsafeOkHttpClient()
//            AndroidNetworking.initialize(context, okHttpClient)
//            AndroidNetworking.enableLogging()
//            AndroidNetworking.post(Apis.BASE_URL + Apis.API_ADDRESS_DATA)
//                .addHeaders(Const.OS_VERSION, getAndroidVersion()!!)
//                .addHeaders(Const.DEVICE_NAME, getDeviceName()!!)
//                .addHeaders(Const.USER_TOKEN, getUserToken())
//                .addHeaders(Const.USER_ID, getUserId())
//                .addHeaders(Const.LANGUAGE_ID, Const.LANG_ENGLISH)
//                .addHeaders(Const.DEVICE_ID, context?.getDeviceId())
//                .addHeaders(Const.DEVICE_TYPE, Const.DEVICE_TYPE_ANDROID)
//                .addHeaders(Const.APP_ID, Const.APP_ID_VERSION)
//                .addHeaders(Const.APP_VERSION_NO, BuildConfig.VERSION_NAME)
//                .addHeaders(Const.APP_ID, Const.APP_ID_VERSION)
//                .addHeaders(Const.APP_VERSION_NO, BuildConfig.VERSION_NAME)
//                .addQueryParameter(param)
//                .setPriority(Priority.IMMEDIATE)
//                .build()
//                .setUploadProgressListener(UploadProgressListener { bytesUploaded, totalBytes -> // do anything with progress
//                })
//                .getAsJSONObject(object : JSONObjectRequestListener {
//                    override fun onResponse(response: JSONObject) {
//                        mprogressbar.hideProgressBar()
//                        swf_order_detail?.isRefreshing = false
//                        if (WebService.context.handleForbiddenResponseJson(response.toString())) {
//                            Log.e("///////response", response.toString())
//                            try {
//                                val gson = Gson()
//                                val responseProfile = gson.fromJson(
//                                    response.toString(),
//                                    AddressDetailResponse::class.java
//                                )
//                                setAddressData(responseProfile.data)
//
//                            } catch (e: JSONException) {
//                                e.printStackTrace()
//                                Log.e("image", e.toString())
//                                mprogressbar.hideProgressBar()
//                            }
//                        }
//                    }
//
//                    override fun onError(error: ANError) {
//                        mprogressbar.hideProgressBar()
//                        Log.e("image upload", error.toString())
//                        // handle error
//                    }
//                })


            if (activity != null && !activity?.isFinishing!!) {
                val retrofit = WebService.getRetrofit(activity?.applicationContext!!).create(
                    ApiClient::class.java
                )

                retrofit.ApiAddressData(OId)
                    .enqueue(object : Callback<AddressDetailResponse> {
                        override fun onFailure(call: Call<AddressDetailResponse>, t: Throwable) {
                            mprogressbar.hideProgressBar()
                        }

                        override fun onResponse(
                            call: Call<AddressDetailResponse>,
                            response: Response<AddressDetailResponse>?
                        ) {
                            mprogressbar.hideProgressBar()
                            if (response != null) {
                                if (response.isSuccessful) {
                                    val responseLogin = response.body()

                                    if (responseLogin != null && responseLogin.data != null) {
                                        setAddressData(responseLogin.data)
                                    }
                                }
                            }
                        }
                    })
            }

        } else {
            NetworkErrorDialog(requireActivity())
            swf_order_detail?.isRefreshing = false
        }
    }


    private fun setAddressData(data: AddressDetailResponse.DataEntity?) {
        try {
            Log.e("tagGsonM", Gson().toJson(data))
            if (tvResellername != null) {
                tvResellername.text = data?.resellername
            }
            tvCustName.text = data?.customername
            tvResellercontact.text = data?.resellercontact
            tvfromcontact.text = data?.fromcontact

            if (data?.completeaddress.equals("")) {
                var mStrAddress =
                    data?.building + ", " + data?.street + ", " + data?.landmark + ", " + data?.city + ", " + data?.pincode + ", " + data?.state + ", " + data?.country
                tvAddress.text = mStrAddress

            } else {
                tvAddress.text = data?.completeaddress
            }
            tvfromno.text = data?.fromname

            if (data?.fromname.equals("")) {
                tvfromno.text = " - "
            }
            if (data?.fromcontact.equals("")) {
                tvfromcontact.text = " - "
            }

        } catch (e: Exception) {
        }
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
                Log.e("addnote", "tvCamera click")
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

    fun openGallery2() {
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

    fun openGallery() {
        if (hasStoragePermission()) {
            try {
                val galleryIntent =
                    Intent(Intent.ACTION_GET_CONTENT)
                galleryIntent.type = "image/*"
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

    fun openCamera2() {
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

    fun openCamera() {
        if (hasCameraPermission()) {
            Log.e("addnote", "hasCameraPermission")
            try {
                Log.e("addnote", "hasCameraPermission try")
                val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                if (takePictureIntent.resolveActivity(activity?.packageManager!!) != null) {

                    try {
                        photoFile = getTemporaryCameraFile()
                    } catch (ex: IOException) {
                        ex.printStackTrace()
                    }
                    if (photoFile != null) {
                        val photoURI =
                            getValidUri(photoFile, requireContext())
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                        startActivityForResult(takePictureIntent, RC_CAMERA_PERM)
                    }
                }
            } catch (e: java.lang.Exception) {
                Log.e("addnote", "hasCameraPermission catch")
                Log.e("addnote", "hasCameraPermission catch: " + e?.message)
                e.printStackTrace()
            }
        } else {
            Log.e("addnote", "hasCameraPermission not")
            EasyPermissions.requestPermissions(
                this,
                "This app needs access to your camera so you can take pictures.",
                RC_CAMERA_PERM,
                Manifest.permission.CAMERA
            )
        }
    }

    fun getValidUri(file: File, context: Context?): Uri {
        val outputUri: Uri
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val authority = context!!.packageName /*+ ".fileprovider"*/
            outputUri = FileProvider.getUriForFile(context, authority, file)
        } else {
            outputUri = Uri.fromFile(file)
        }
        return outputUri
    }

    private fun setRefresh() {
        swf_order_detail?.setOnRefreshListener {
            CallApiOrderDetails(false)
            CallGetAddressData(false);
        }
    }


    private fun CallApiOrderDetails(isprogress: Boolean) {

        if (isInternetAvailable(requireActivity())) {

            if (isprogress)
                mprogressbar.showProgressBar()

            val param = ParamOrderDeatils().apply {
                orderId = OId
            }

//            val okHttpClient = UnsafeOkHttpClient.getUnsafeOkHttpClient()
//            AndroidNetworking.initialize(context, okHttpClient)
//            AndroidNetworking.enableLogging()
//            AndroidNetworking.post(Apis.BASE_URL + Apis.API_MY_OREDER_DETAILS)
//                .addHeaders(Const.OS_VERSION, getAndroidVersion()!!)
//                .addHeaders(Const.DEVICE_NAME, getDeviceName()!!)
//                .addHeaders(Const.USER_TOKEN, getUserToken())
//                .addHeaders(Const.USER_ID, getUserId())
//                .addHeaders(Const.LANGUAGE_ID, Const.LANG_ENGLISH)
//                .addHeaders(Const.DEVICE_ID, context?.getDeviceId())
//                .addHeaders(Const.DEVICE_TYPE, Const.DEVICE_TYPE_ANDROID)
//                .addHeaders(Const.APP_ID, Const.APP_ID_VERSION)
//                .addHeaders(Const.APP_VERSION_NO, BuildConfig.VERSION_NAME)
//                .addQueryParameter(param)
//                .setPriority(Priority.IMMEDIATE)
//                .build()
//                .setUploadProgressListener(UploadProgressListener { bytesUploaded, totalBytes -> // do anything with progress
//                })
//                .getAsJSONObject(object : JSONObjectRequestListener {
//                    override fun onResponse(response: JSONObject) {
//                        mprogressbar.hideProgressBar()
//                        swf_order_detail?.isRefreshing = false
//                        if (WebService.context.handleForbiddenResponseJson(response.toString())) {
//                            Log.e("///////response", response.toString())
//
//                            try {
//                                val gson = Gson()
//                                val responseProfile =
//                                    gson.fromJson(response.toString(), ResponseOrderDetail::class.java)
//                                val data = responseProfile.data
//                                setData(responseProfile.data)
//
//                            } catch (e: JSONException) {
//                                e.printStackTrace()
//                                Log.e("image", e.toString())
//                                mprogressbar.hideProgressBar()
//                            }
//                        }
//                    }
//
//                    override fun onError(error: ANError) {
//                        mprogressbar.hideProgressBar()
//                        Log.e("image upload", error.toString())
//                        // handle error
//                    }
//                })

            if (activity != null && !activity?.isFinishing!!) {
                val retrofit = WebService.getRetrofit(activity?.applicationContext!!).create(
                    ApiClient::class.java
                )

                retrofit.ApiMyOrderDetails(OId)
                    .enqueue(object : Callback<ResponseOrderDetail> {
                        override fun onFailure(call: Call<ResponseOrderDetail>, t: Throwable) {
                            mprogressbar.hideProgressBar()
                        }

                        override fun onResponse(
                            call: Call<ResponseOrderDetail>,
                            response: Response<ResponseOrderDetail>?
                        ) {
                            mprogressbar.hideProgressBar()
                            if (response != null) {
                                if (response.isSuccessful) {
                                    val responseLogin = response.body()

                                    if (responseLogin != null && responseLogin.data != null) {
                                        setData(responseLogin.data)
                                    }
                                }
                            }
                        }
                    })
            }

        } else {
            NetworkErrorDialog(requireActivity())
            swf_order_detail?.isRefreshing = false
        }
    }


/*
    private fun CallApiOrderDetails(isprogress: Boolean) {

        if (isInternetAvailable(requireActivity())) {

            if (isprogress)
                mprogressbar.showProgressBar()

            val param = ParamOrderDeatils().apply {
                orderId = OId
            }

            Log.e("tagGsonM", "OId " + OId)

            val retrofit = WebService.getRetrofit(requireActivity()).create(ApiClient::class.java)

            retrofit.GetOrderDetails(param).enqueue(object : Callback<OrderDetailNewResponse> {

                    override fun onFailure(call: Call<OrderDetailNewResponse>, t: Throwable) {
                        Log.e("tagGsonM", "onFailure ")
                        mprogressbar.hideProgressBar()
                        swf_order_detail?.isRefreshing = false
                    }

                    override fun onResponse(
                        call: Call<OrderDetailNewResponse>,
                        response: Response<OrderDetailNewResponse>?
                    ) {
                        mprogressbar.hideProgressBar()
                        swf_order_detail?.isRefreshing = false

                        if (response != null) {
                            if (response.isSuccessful) {
                                Log.e("////tagGsonM", Gson().toJson(response))

                               */
/* val responsecart = response.body()
                                Log.e("////tagGsonM", Gson().toJson(responsecart.toString()))

                                if (responsecart != null) {

                                    if (responsecart.status == API_SUCESS) {
                                        val data = responsecart.data
                                        if (isAdded) {
                                            setData(data)
                                        }
                                    }
                                }*//*

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
            swf_order_detail?.isRefreshing = false
        }
    }
*/


    fun setData(data: Data) {
        try {
            Log.e("tagGsonM", Gson().toJson(data))

            if (!data.orderStatus.equals("") && data.orderStatus!!.toInt() < 500) {
                if (ivAddNote != null) {
                    ivAddNote.visibility = View.VISIBLE
                }
            } else {
                if (ivAddNote != null) {
                    ivAddNote.visibility = View.GONE
                }
            }
            tvDelivery_status.text = data.orderStatusName
            tvOrderId.text = data.orderId
            tvCustName.text = data.customerName
            tvTotalQty.text = data.totalQuantity.toString()

            tvPlaceOn.text = requireActivity().getFormattedDate(
                data.createdDateTime!!,
                Const.INPUT_DATE_FORMATE,
                Const.OUTPUT_DATE_FORAMTE
            )

            tvSubTotal.text = resources.getString(R.string.currency) + " " + data.subTotal
            tvGrandTotal.text = resources.getString(R.string.currency) + " " + data.grandTotal
            tvmembership?.text =
                resources.getString(R.string.currency) + " " + data.membershipDiscount

            val strgst = data.gST
            if (strgst!!.isEmpty() || strgst.equals(Const.DUMMY_VAUE)) {
                rl_gst.visibility = View.GONE
            } else {
                tvGST.text = getString(R.string.currency) + " " + data.gST
                rl_gst.visibility = View.VISIBLE
            }

            val strshipingcharg = data.shippingCharge
            if (strshipingcharg!!.isEmpty() || strshipingcharg.equals(Const.DUMMY_VAUE)) {
                rl_ShipCharge.visibility = View.GONE
            } else {
                tv_ship_charge.text = getString(R.string.currency) + " " + data.shippingCharge
                rl_ShipCharge.visibility = View.VISIBLE
            }
            order_note_list.clear()
            data.notes?.let { order_note_list.addAll(it) }

            rvNotes.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, true)
                order_note_adapter = AdapterNotes(this@OrderDetailFragment, order_note_list)
                adapter = order_note_adapter
            }
            rvOrder.apply {
                layoutManager = LinearLayoutManager(context)
                order_item_list.clear()
                data.items?.let { order_item_list.addAll(it) }
                orderitem_list = OrderItemAdapter(this@OrderDetailFragment, order_item_list)
                adapter = orderitem_list
            }
            if (ivAddNote != null) {
                ivAddNote.setOnClickListener {
                    openDialogAddNote()
                }
            }
        } catch (e: Exception) {
        }
    }

    private fun openDialogAddNote() {
        try {
            dialogNoteImage = Dialog(ivAddNote.context, R.style.mydialog)
            val window = dialogNoteImage!!.window
            window!!.setLayout(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            window.setGravity(Gravity.CENTER)
            dialogNoteImage!!.window!!.setBackgroundDrawableResource(android.R.color.white)
            dialogNoteImage!!.setContentView(R.layout.dialog_add_note)
            dialogNoteImage!!.setCancelable(false)
            dialogNoteImage!!.setCanceledOnTouchOutside(true)
            dialogNoteImage!!.show()

            dialogNoteImage!!.tvUploadImage.setOnClickListener {
                dailogOpenAttachments()
            }
            dialogNoteImage!!.tvAddNote.setOnClickListener {
                if (dialogNoteImage!!.edAddNote.text.toString().equals("")) {
                    Toast.makeText(
                        dialogNoteImage!!.tvAddNote.context,
                        "Please add note",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    dialogNoteImage!!.dismiss()
                    callAddNoteApi(
                        dialogNoteImage!!.edAddNote.text.toString().trim(),
                        dialogNoteImage!!.edAddNote.context
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun callAddNoteApi(note: String, context: Context?) {
        Log.e("/////note", note + "...")
        if (isInternetAvailable(requireActivity())) {
            mprogressbar.showProgressBar()

            val retrofit = WebService.getRetrofit(requireActivity()).create(ApiClient::class.java)


            val orderId_request: RequestBody = RequestBody.create(
                "text/plain".toMediaTypeOrNull(),
                OId
            )

            val note_request: RequestBody = RequestBody.create(
                "text/plain".toMediaTypeOrNull(),
                note
            )

            var call: Call<ResponseOrderDetail>? = null;
            if (finalImageFile != null) {

                var mMultipartBody: MultipartBody.Part? = null
                //val file = File(currentPhotoPath);
                val file = finalImageFile
                if (file != null && file.exists()) {

                    val requestFile = RequestBody.create(
                        "image/*".toMediaTypeOrNull(),
                        file
                    )
                    //image/*
                    //multipart/form-data
                    mMultipartBody = MultipartBody.Part.createFormData(
                        "NotesImage",
                        file.getName(),
                        requestFile
                    )

                }
                //with image

                call = retrofit.addNotes_withImage(
                    orderId_request,
                    note_request,
                    mMultipartBody!!
                )
                if (mMultipartBody == null) {
                    Log.e("addnote", "mMultipartBody null")
                    mprogressbar.hideProgressBar()
                } else {
                    Log.e("addnote", "mMultipartBody has image")
                }
            } else {
                call = retrofit.addNotes_withoutImage(orderId_request, note_request)
            }

            call?.enqueue(object : Callback<ResponseOrderDetail> {
                override fun onResponse(
                    call: Call<ResponseOrderDetail>,
                    response: Response<ResponseOrderDetail>
                ) {
                    finalImageFile=null

                    Log.e("addnote", "onResponse")
                    mprogressbar.hideProgressBar()
                    if (response.isSuccessful) {
                        Log.e("addnote", "response.isSuccessful")
                        if (response.body() != null) {
                            Log.e("addnote", "body not null")
                            Toast.makeText(
                                context,
                                response.body()?.message!!,
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Log.e("addnote", "body null")
                        }
                        CallApiOrderDetails(false)
                    } else {
                        Log.e(
                            "addnote",
                            "not response.isSuccessful: " + response.message() + " : " + response.code()
                        )

                    }

                }

                override fun onFailure(call: Call<ResponseOrderDetail>, t: Throwable) {
                    mprogressbar.hideProgressBar()
                    Log.e("addnote", "onFailure")
                    Log.e("addnote", t.message!!)
                }

            })
        } else {
            NetworkErrorDialog(requireActivity())
            swf_order_detail?.isRefreshing = false
        }

    }

    private fun callAddNoteApi2(note: String, context: Context?) {
        Log.e("/////note", note + "...")
        if (isInternetAvailable(requireActivity())) {
            mprogressbar.showProgressBar()

            val okHttpClient = UnsafeOkHttpClient.getUnsafeOkHttpClient()
            AndroidNetworking.initialize(requireActivity(), okHttpClient)
            AndroidNetworking.enableLogging()

            if (finalImageFile != null) {

                AndroidNetworking.upload(Apis.BASE_URL + Apis.API_ADD_NOTE)
                    .addMultipartFile("NotesImage", File(currentPhotoPath))
                    .addMultipartParameter("notes", note)
                    .addMultipartParameter("orderId", OId)
                    .addHeaders(Const.OS_VERSION, getAndroidVersion()!!)
                    .addHeaders(Const.DEVICE_NAME, getDeviceName()!!)
                    .addHeaders(Const.USER_TOKEN, getUserToken())
                    .addHeaders(Const.USER_ID, getUserId())
                    .addHeaders(Const.LANGUAGE_ID, Const.LANG_ENGLISH)
                    .addHeaders(Const.DEVICE_ID, activity?.getDeviceId())
                    .addHeaders(Const.DEVICE_TYPE, Const.DEVICE_TYPE_ANDROID)
                    .setPriority(Priority.IMMEDIATE)
                    .build()
                    .getAsJSONObject(object : JSONObjectRequestListener {
                        override fun onResponse(response: JSONObject) {
                            mprogressbar.hideProgressBar()
                            Toast.makeText(
                                context,
                                response.optString("message"),
                                Toast.LENGTH_SHORT
                            ).show()
                            Log.e("///////", "add note respone//" + response.toString())
                            finalImageFile = null
                            CallApiOrderDetails(false)

                            /*   try {
                                   val gson = Gson()
                                   val responseProfile = gson.fromJson(response.toString(), res::class.java)
                                   val data = responseProfile.data
                               } catch (e: JSONException) {
                                   e.printStackTrace()
                               }*/

                        }

                        override fun onError(error: ANError) {
                            mprogressbar.hideProgressBar()
                            Log.e("image upload", error.toString())
                            // handle error
                        }
                    })
            } else {
                AndroidNetworking.upload(Apis.BASE_URL + Apis.API_ADD_NOTE)
                    .addMultipartParameter("NotesImage", "")
                    .addMultipartParameter("notes", note)
                    .addMultipartParameter("orderId", OId)
                    .addHeaders(Const.OS_VERSION, getAndroidVersion()!!)
                    .addHeaders(Const.DEVICE_NAME, getDeviceName()!!)
                    .addHeaders(Const.USER_TOKEN, getUserToken())
                    .addHeaders(Const.USER_ID, getUserId())
                    .addHeaders(Const.LANGUAGE_ID, Const.LANG_ENGLISH)
                    .addHeaders(Const.DEVICE_ID, activity?.getDeviceId())
                    .addHeaders(Const.DEVICE_TYPE, Const.DEVICE_TYPE_ANDROID)
                    .setPriority(Priority.IMMEDIATE)
                    .build()
                    .getAsJSONObject(object : JSONObjectRequestListener {
                        override fun onResponse(response: JSONObject) {
                            mprogressbar.hideProgressBar()
                            Log.e(
                                "///////",
                                "add note respone not image//" + response.toString()
                            )
                            Toast.makeText(
                                context,
                                response.optString("message"),
                                Toast.LENGTH_SHORT
                            ).show()
                            CallApiOrderDetails(false)

                            /*   try {
                                   val gson = Gson()
                                   val responseProfile = gson.fromJson(response.toString(), res::class.java)
                                   val data = responseProfile.data
                               } catch (e: JSONException) {
                                   e.printStackTrace()
                               }*/

                        }

                        override fun onError(error: ANError) {
                            mprogressbar.hideProgressBar()
                            Log.e("image upload", error.toString())
                            // handle error
                        }
                    })
            }


        } else {
            NetworkErrorDialog(requireActivity())
            swf_order_detail?.isRefreshing = false
        }

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBack -> {
                // homeActivity?.selectLastMenu()
                /* homeActivity?.replaceFragment(
                    MyOrderFragment(isshowtab, false),
                    Const.TAG_FRAG_ORDERLIST
                )*/
                fragmentManager?.popBackStack()
                ivBack.isEnabled = false
            }
            R.id.liwhtsapp -> {
                OpenWhatsApp()
            }
        }
    }

    fun loadReview(id: String) {
        val intent = Intent(requireActivity(), WriteReviewActivity::class.java)
        intent.putExtra(Const.PRODUCT_ID, id)
        intent.putExtra(Const.ORDER_ID, OId)
        startActivity(intent)
    }

    fun returnOrder(productId: String) {
        getReturnProductReasonList(productId)
    }

    private fun getReturnProductReasonList(productId: String) {

        if (isInternetAvailable(requireActivity())) {
            mprogressbar.showProgressBar()

            val okHttpClient = UnsafeOkHttpClient.getUnsafeOkHttpClient()
            AndroidNetworking.initialize(requireActivity(), okHttpClient)
            AndroidNetworking.enableLogging()

            AndroidNetworking.get(Apis.BASE_URL + Apis.API_RETURN_PRODUCT_REASON)
                .addHeaders(Const.OS_VERSION, getAndroidVersion()!!)
                .addHeaders(Const.DEVICE_NAME, getDeviceName()!!)
                .addHeaders(Const.USER_TOKEN, getUserToken())
                .addHeaders(Const.USER_ID, getUserId())
                .addHeaders(Const.LANGUAGE_ID, Const.LANG_ENGLISH)
                .addHeaders(Const.DEVICE_ID, activity?.getDeviceId())
                .addHeaders(Const.DEVICE_TYPE, Const.DEVICE_TYPE_ANDROID)
                .addHeaders(Const.APP_ID, Const.APP_ID_VERSION)
                .addHeaders(Const.APP_VERSION_NO, BuildConfig.VERSION_NAME)
                .setPriority(Priority.IMMEDIATE)
                .build()
                .getAsJSONObject(object : JSONObjectRequestListener {
                    override fun onResponse(response: JSONObject) {
                        mprogressbar.hideProgressBar()
                        Log.e("///////", "REASONE//" + response.toString())
                        try {
                            val gson = Gson()
                            if (WebService.context.handleForbiddenResponseJson(response.toString())) {
                                val reasonData = gson.fromJson(
                                    response.toString(),
                                    ProductReturnReasonResponse::class.java
                                )
                                if (reasonData.status == 1) {
                                    return_return.clear()
                                    return_return.addAll(reasonData.data!!)

                                    if (return_return.size > 0) {
                                        openReturnProductialog(productId, return_return)
                                    }
                                }
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }

                    }

                    override fun onError(error: ANError) {
                        mprogressbar.hideProgressBar()
                    }
                })
        } else {
            NetworkErrorDialog(requireActivity())
        }
    }

    private fun openReturnProductialog(
        productId: String,
        return_return: ArrayList<ProductReturnReasonResponse.DataEntity>
    ) {
        try {
            dialogReturn = Dialog(ivAddNote.context, R.style.mydialog)
            val window = dialogReturn!!.window
            window!!.setLayout(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            window.setGravity(Gravity.CENTER)
            dialogReturn!!.window!!.setBackgroundDrawableResource(android.R.color.white)
            dialogReturn!!.setContentView(R.layout.dialog_return_product_reason)
            dialogReturn!!.setCancelable(false)
            dialogReturn!!.setCanceledOnTouchOutside(true)
            dialogReturn!!.show()

            dialogReturn!!.spinnerReason.adapter = customSpinnerAdapter(context, return_return)
            dialogReturn!!.spinnerReason.onItemSelectedListener

            dialogReturn!!.spinnerReason.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>,
                        view: View,
                        position: Int,
                        id: Long
                    ) {

                        val reasonId = return_return.get(position).id
                        val reasonIdValue = return_return.get(position).reasontext

                        if (reasonIdValue.equals("Other")) {
                            dialogReturn!!.edtOtherReason.visibility = View.VISIBLE
                        } else {
                            dialogReturn!!.edtOtherReason.visibility = View.GONE
                        }
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {

                    }
                }

            dialogReturn!!.tvAddReason.setOnClickListener {

                val reasonId =
                    return_return.get(dialogReturn!!.spinnerReason.selectedItemPosition).id
                val reasonIdValue =
                    return_return.get(dialogReturn!!.spinnerReason.selectedItemPosition).reasontext

                if (reasonIdValue.equals("Other")) {
                    if (TextUtils.isEmpty(dialogReturn!!.edtOtherReason.text.toString())) {
                        Toast.makeText(
                            dialogReturn!!.context,
                            "Please enter reason of return product",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        callReturnProduct(
                            productId,
                            reasonId,
                            reasonIdValue,
                            dialogReturn!!.edtOtherReason.text.toString()
                        )
                    }
                } else {
                    dialogReturn!!.edtOtherReason.visibility = View.GONE
                    callReturnProduct(productId, reasonId, reasonIdValue, "")
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun callReturnProduct(
        productId: String,
        reasonId: String?,
        ReasonText: String?,
        OtherReturnReason: String
    ) {

        if (isInternetAvailable(requireActivity())) {

            mprogressbar.showProgressBar()

            val okHttpClient = UnsafeOkHttpClient.getUnsafeOkHttpClient()
            AndroidNetworking.initialize(requireActivity(), okHttpClient)
            AndroidNetworking.enableLogging()

            val jsonObjectOrderDetailId = JSONObject()
            try {
                jsonObjectOrderDetailId.put("OrderDetailId", productId)
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            val jsonObject = JSONObject()
            try {
                jsonObject.put("OrderId", tvOrderId.text.toString())
                jsonObject.put("OrderDetailId", jsonObjectOrderDetailId.toString())
                jsonObject.put("ReturnReasonId", reasonId)
                jsonObject.put("OtherReturnReason", OtherReturnReason)

            } catch (e: JSONException) {
                e.printStackTrace()
            }

            Log.e("///////////", jsonObject.toString())
            Log.e("///////////", jsonObjectOrderDetailId.toString())


            AndroidNetworking.post(Apis.BASE_URL + Apis.API_RETURN_ORDER)
                .addHeaders(Const.OS_VERSION, getAndroidVersion()!!)
                .addHeaders(Const.DEVICE_NAME, getDeviceName()!!)
                .addHeaders(Const.USER_TOKEN, getUserToken())
                .addHeaders(Const.USER_ID, getUserId())
                .addHeaders(Const.LANGUAGE_ID, Const.LANG_ENGLISH)
                .addHeaders(Const.DEVICE_ID, activity?.getDeviceId())
                .addHeaders(Const.DEVICE_TYPE, Const.DEVICE_TYPE_ANDROID)
                .addHeaders(Const.APP_ID, Const.APP_ID_VERSION)
                .addHeaders(Const.APP_VERSION_NO, BuildConfig.VERSION_NAME)
                .addJSONObjectBody(jsonObject)
                .setPriority(Priority.IMMEDIATE)
                .build()
                .getAsJSONObject(object : JSONObjectRequestListener {
                    override fun onResponse(response: JSONObject) {
                        mprogressbar.hideProgressBar()
                        if (dialogReturn!! != null) {
                            dialogReturn!!.dismiss()
                        }
                        if (WebService.context.handleForbiddenResponseJson(response.toString())) {
                            Log.e("///////", "REASONE return//" + response.toString())
                            try {
                                val gson = Gson()
                                val reasonData = gson.fromJson(
                                    response.toString(),
                                    ResponseAddSharedData::class.java
                                )
                                activity?.showToast(
                                    reasonData.message,
                                    Const.ALERT
                                )
                                if (reasonData.status == 1) {
                                    order_item_list.clear()
                                    return_return.clear()
                                    order_note_list.clear()
                                    CallApiOrderDetails(true)
                                    CallGetAddressData(true);

                                }
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }
                        }
                    }

                    override fun onError(error: ANError) {
                        mprogressbar.hideProgressBar()
                        dialogReturn!!.dismiss()

                    }
                })
        } else {
            NetworkErrorDialog(requireActivity())
        }

    }

    override fun onResume() {
        super.onResume()
        ivBack.isEnabled = true
    }


    private fun OpenWhatsApp() {
//        val intent = Intent(activity, WebviewChatActivity::class.java)
//        intent.putExtra(Const.URL, "https://tawk.to/chat/5fa16c2be019ee7748f05f73/default")
//        intent.putExtra(Const.TITLE, getString(R.string.strLiveSupport))
//        intent.putExtra(Const.CUSTOM_TEXT, "My order Id is " + OId)
//        startActivity(intent)

        activity?.openLiveChatWithProfile(mprogressbar)

        /*val url =
            "https://api.whatsapp.com/send?phone=" + " +916351859147&text=My order Id is " + OId
        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse(url)
        startActivity(i)*/
    }

    @Throws(IOException::class)
    private fun createImageFile(): File? {
        val timeStamp =
            SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)
                .format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        /*val storageDir: File? =
            activity?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)*/
        val storageDir: File? = activity?.cacheDir
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
        if (requestCode == RC_CAMERA_PERM /*&& resultCode == Activity.RESULT_OK*/) {
            /* try {
                 dialogNoteImage!!.ivNote.visibility = View.VISIBLE
                 finalImageFile = File(currentPhotoPath)
                 GlideApp.with(this)
                     .load(currentPhotoPath)
                     .placeholder(R.drawable.profileplaceholder)
                     .into(dialogNoteImage!!.ivNote)
             } catch (e: java.lang.Exception) {
                 e.printStackTrace()
             }*/

            try {
                dialogNoteImage!!.ivNote.visibility = View.VISIBLE
                //finalImageFile = photoFile
                /*val thumbnail = data!!.extras!!.get("data") as Bitmap

                Log.d(
                    "camara",
                    CacheStore().saveCacheFile("", thumbnail)
                        .toString()
                )
                photoFile = File(
                   CacheStore()
                        .saveCacheFile(context?.externalCacheDir.toString(), thumbnail)
                        .toString()
                )*/
                Log.d("PROFILE_FILE", photoFile!!.absolutePath)
                compresss1(photoFile!!.absolutePath)


            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
        if (requestCode == RC_GALLERY_PERM && resultCode == Activity.RESULT_OK) {
            try {
                val selectedImageURI: Uri = data?.data!!
                // finalImageFile = File(getRealPathFromURI(selectedImageURI))
                finalImageFile = File(getPathFromUri(requireContext(), selectedImageURI))
                currentPhotoPath = finalImageFile.toString()
               // File(currentPhotoPath)
                dialogNoteImage!!.ivNote.visibility = View.VISIBLE
                GlideApp.with(this)
                    .load(currentPhotoPath)
                    .placeholder(R.drawable.profileplaceholder)
                    .into(dialogNoteImage!!.ivNote)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getPathFromUri(context: Context?, uri: Uri): String? {
        val isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).toTypedArray()
                val type = split[0]
                if ("primary".equals(type, ignoreCase = true)) {
                    return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                }

                //  handle non-primary volumes
            } else if (isDownloadsDocument(uri)) {
                val id = DocumentsContract.getDocumentId(uri)
                val contentUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"),
                    java.lang.Long.valueOf(id)
                )
                return getDataColumn(
                    context!!,
                    contentUri,
                    null,
                    null
                )
            } else if (isMediaDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).toTypedArray()
                val type = split[0]
                var contentUri: Uri? = null
                if ("image" == type) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                } else if ("video" == type) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                } else if ("audio" == type) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }
                val selection = "_id=?"
                val selectionArgs = arrayOf(
                    split[1]
                )
                return getDataColumn(
                    context!!,
                    contentUri,
                    selection,
                    selectionArgs!!
                )
            }
        } else if ("content".equals(uri.scheme, ignoreCase = true)) {

            // Return the remote address
            return if (isGooglePhotosUri(uri)) uri.lastPathSegment else getDataColumn(
                context!!,
                uri,
                null,
                null
            )
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            return uri.path
        }
        return null
    }

    fun compresss1(realPath:String) {
        val imageCompression: ImageCompressionAsyncTask =
            @SuppressLint("StaticFieldLeak")
            object : ImageCompressionAsyncTask() {
                @SuppressLint("WrongThread")
                override fun onPostExecute(imageBytes: ByteArray) {
                    // image here is compressed & ready to be sent to the server

                    val bitmap = BitmapFactory.decodeByteArray(
                        imageBytes,
                        0,
                        imageBytes.size
                    )

                    val byteArrayOutputStream: ByteArrayOutputStream =
                        ByteArrayOutputStream()
                    bitmap.compress(
                        Bitmap.CompressFormat.JPEG,
                        100,
                        byteArrayOutputStream
                    )
                    val bytesofImage = byteArrayOutputStream.toByteArray()

                    GlideApp.with(requireContext())
                        .load(bitmap)
                        .placeholder(R.drawable.profileplaceholder)
                        .into(dialogNoteImage!!.ivNote)
                    //finalImageFile = photoFile
                    finalImageFile = File(
                        CacheStore().saveCacheFile(
                            context?.externalCacheDir.toString(),
                            bitmap
                        ).toString()
                    )
                    //currentPhotoPath = finalImageFile?.absolutePath!!

                }
            }
        imageCompression.execute(realPath) // imagePath as a string
    }

    fun isGooglePhotosUri(uri: Uri): Boolean {
        return "com.google.android.apps.photos.content" == uri.authority
    }


    fun getDataColumn(
        context: Context, uri: Uri?, selection: String?,
        selectionArgs: Array<String>?
    ): String? {
        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(
            column
        )
        try {
            cursor = context.contentResolver.query(
                uri!!, projection, selection, selectionArgs,
                null
            )
            if (cursor != null && cursor.moveToFirst()) {
                val index = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(index)
            }
        } finally {
            cursor?.close()
        }
        return null
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


    fun getTemporaryCameraFile(): File {
        val storageDir = getAppExternalDataDirectoryFile()
        val file = File(storageDir, getTemporaryCameraFileName())
        try {
            file.createNewFile()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return file
    }

    private fun getAppExternalDataDirectoryFile(): File {
        val dataDirectoryFile = File(getAppExternalDataDirectoryPath())
        dataDirectoryFile.mkdirs()
        return dataDirectoryFile
    }

    private fun getAppExternalDataDirectoryPath(): String {
        val sb = StringBuilder()
        sb.append(Environment.getExternalStorageDirectory())
            .append(File.separator)
            .append("Android")
            .append(File.separator)
            .append("data")
            .append(File.separator)
            .append(requireActivity().packageName)
            .append(File.separator)
        return sb.toString()
    }

    private fun getTemporaryCameraFileName(): String {
        return "camera_" + System.currentTimeMillis() + ".jpg"
    }

}


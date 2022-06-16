package com.ffi.addreslist

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.androidnetworking.interfaces.UploadProgressListener
import com.ffi.api.Apis
import com.ffi.BuildConfig
import com.ffi.GlideApp
import com.ffi.HomeActivity
import com.ffi.R
import com.ffi.Utils.*
import com.ffi.add_address.AddAddressActivity
import com.ffi.cart.ParamCheckout
import com.ffi.cart.ResponseCheckout
import com.ffi.edit_profile.ResponseProfile
import com.ffi.payment.*
import com.ffi.api.ApiClient
import com.ffi.api.WebService
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_address_list.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import org.json.JSONException
import org.json.JSONObject
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.jvm.Throws

class AddressListActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var address_list: AddresListAdapter
    lateinit var mprogressbar: ProgressBarHandler
    private val RESULT_ADD_ADDRESS = 2222
    var responseAddress: List<Data> = listOf()
    private var selectedAddresid = ""
    private var isWallet = false

    var PaymentValue = ""
    val RC_CAMERA_PERM = 123
    val RC_GALLERY_PERM = 124
    private var currentPhotoPath = ""
    lateinit var finalImageFile: File
    var strcopyaddressid: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address_list)

        init()
        setListener()

        CallApiAddressList()
    }

    private fun CallApiAddressList() {
        if (isInternetAvailable(this)) {
            mprogressbar.showProgressBar()

            val retrofit = WebService.getRetrofit(this@AddressListActivity).create(ApiClient::class.java)
            retrofit.GetAddressList()
                .enqueue(object : Callback<ResponseAddressList> {
                    override fun onFailure(call: Call<ResponseAddressList>, t: Throwable) {
                        mprogressbar.hideProgressBar()
                    }

                    override fun onResponse(
                        call: Call<ResponseAddressList>,
                        response: Response<ResponseAddressList>?
                    ) {
                        mprogressbar.hideProgressBar()
                        if (response != null) {
                            if (response.isSuccessful) {
                                val body = response.body()
                                if (body != null) {
                                    if (body.status.equals(API_SUCESS)) {
                                        rvAddress.visibility = View.VISIBLE
                                        tvSelectAddress.visibility = View.VISIBLE
                                        liadd_address?.visibility = View.GONE
                                        ivEdit?.visibility = View.VISIBLE
                                        responseAddress = body.data
                                        setData(responseAddress)
                                    } else if (body.status.equals(API_DATA_NOT_AVAILBLE)) {
                                        rvAddress.visibility = View.GONE
                                        tvSelectAddress.visibility = View.GONE
                                        liadd_address?.visibility = View.VISIBLE
                                        ivEdit?.visibility = View.GONE
                                        //tvNoAddress.visibility = View.VISIBLE
                                    }
                                }
                            } else {
                                showToast(getString(R.string.msg_common_error),Const.ALERT)
                            }
                        }
                    }
                })

        } else {
            NetworkErrorDialog(this)
        }
    }

    private fun setData(responseAddress: List<Data>) {
        rvAddress.apply {
            layoutManager = LinearLayoutManager(context)
            address_list = AddresListAdapter(
                this@AddressListActivity,
                responseAddress as ArrayList<Data>
            )
            itemAnimator = null
            adapter = address_list
        }
    }

    private fun init() {
        mprogressbar = ProgressBarHandler(this)
        val bundle = intent.extras

        if (bundle != null) {
            if (bundle.containsKey(Const.USE_CREDIT)) {
                isWallet = bundle.getBoolean(Const.USE_CREDIT)
            }
        }


        GlideApp.with(this)
            .load(R.mipmap.ic_add)
            .into(ivEdit)
        ivEdit.visibility = View.VISIBLE
        ivBack.visibility = View.VISIBLE
        txt_toolbar.text = getString(R.string.title_address_list)

        /*      edt_copyaddress.addTextChangedListener(
                  object : TextWatcher {
                      override fun afterTextChanged(s: Editable?) {
                          if (s?.toString()?.trim()?.length == 1) {
                              if (responseAddress.size > 0)
                                  address_list.DeselectAll()
                              selectedAddresid = ""
                          }
                      }

                      override fun beforeTextChanged(
                          s: CharSequence?,
                          start: Int,
                          count: Int,
                          after: Int
                      ) {

                      }

                      override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                      }

                  }
              )


              edt_customername.addTextChangedListener(
                  object : TextWatcher {
                      override fun afterTextChanged(s: Editable?) {
                          if (s?.toString()?.trim()?.length == 1) {
                              if (responseAddress.size > 0)
                                  address_list.DeselectAll()
                              selectedAddresid = ""
                          }
                      }

                      override fun beforeTextChanged(
                          s: CharSequence?,
                          start: Int,
                          count: Int,
                          after: Int
                      ) {

                      }

                      override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                      }

                  }
              )*/
    }

    private fun setListener() {
        ivBack.setOnClickListener(this)
        ivEdit.setOnClickListener(this)
        btn_next.setOnClickListener(this)
        liadd_address.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.liadd_address -> {
                liadd_address.isEnabled = false
                val intent = Intent(this, AddAddressActivity::class.java)
                startActivityForResult(intent, RESULT_ADD_ADDRESS)
            }
            R.id.ivBack -> {
                onBackPressed()
            }

            R.id.ivEdit -> {
                ivEdit.isEnabled = false
                val intent = Intent(this, AddAddressActivity::class.java)
                startActivityForResult(intent, RESULT_ADD_ADDRESS)
            }
            R.id.btn_next -> {
//                val strcopyaddress = edt_copyaddress.text.toString().trim()
//                val strname = edt_customername.text.toString().trim()
                if (selectedAddresid.isEmpty()) {
                    showToast(getString(R.string.err_select_address),Const.ALERT)
                } else if (!selectedAddresid.isEmpty()) {
                    CallApiCheckout(selectedAddresid)
                } /*else if (selectedAddresid.isEmpty()) {
                    if (strname.isEmpty()) {
                        showToast(getString(R.string.err_cust_name))
                        edt_customername.requestFocus()
                    } else if (strcopyaddress.isEmpty()) {
                        showToast("Please paste address")
                        edt_copyaddress.requestFocus()
                    } else {
                        if (strcopyaddressid == null)
                            CallApiAddCopyAddress()
                        else {
                            CallApiCheckout(strcopyaddressid!!)
                        }
                    }

                }*/
            }

        }
    }

    private fun CallApiPaymentType() {
        if (isInternetAvailable(this)) {

            mprogressbar.showProgressBar()

            val retrofit = WebService.getRetrofit(this).create(ApiClient::class.java)
            retrofit.GetPaymentType()
                .enqueue(object : Callback<ResponsePaymentType> {
                    override fun onFailure(call: Call<ResponsePaymentType>, t: Throwable) {
                        mprogressbar.hideProgressBar()
                    }

                    override fun onResponse(
                        call: Call<ResponsePaymentType>,
                        response: Response<ResponsePaymentType>?
                    ) {
                        mprogressbar.hideProgressBar()

                        if (response != null) {
                            if (response.isSuccessful) {
                                val responsepayment = response.body()
                                if (responsepayment != null) {
                                    if (responsepayment.status == API_SUCESS) {

                                       // OpenPaymentDialog(responsepayment.data)
                                    }
                                }
                            } else {
                                showToast(getString(R.string.msg_common_error),Const.ALERT)
                            }
                        }
                    }
                })

        } else {
            NetworkErrorDialog(this)
        }
    }

//    private fun OpenPaymentDialog(data: ArrayList<DataX>?) {
//        try {
//            val dialog = Dialog(this, R.style.mydialog)
//            val window = dialog.window
//            window!!.setLayout(
//                LinearLayout.LayoutParams.MATCH_PARENT,
//                LinearLayout.LayoutParams.WRAP_CONTENT
//            )
//            window.setGravity(Gravity.BOTTOM)
//            dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
//            dialog.setContentView(R.layout.dialog_payment_type)
//
//            val recyclerview = dialog.findViewById<RecyclerView>(R.id.recyclerview)
//
//            recyclerview.apply {
//                layoutManager = LinearLayoutManager(context)
//                val service_list = PaymentListAdapter(this@AddressListActivity,
//                    PaymentListClickInterface {
//                        dialog.cancel()
//                        val id = data?.get(it)?.id
//                        if (id.equals(Const.BANK_TRANSFER)) {
//                            PaymentValue = data?.get(it)?.value.toString()
//                            dailogOpenAttachments()
//                        } else {
//                            CallApiPaymnet(data?.get(it)?.value)
//                        }
//                        /* ProductId = arrProducts.get(it).ID
//                         for (i in arrProducts.indices) {
//                             if (i == it) {
//                                 arrProducts.get(i).isChecked = true
//                             } else {
//                                 arrProducts.get(i).isChecked = false
//                             }
//                         }*/
//                    })
//                data?.let { service_list.addModels(it) }
//                adapter = service_list
//            }
//
//            dialog.setCancelable(false)
//            dialog.setCanceledOnTouchOutside(true)
//            dialog.show()
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }

    fun setAddressId(id: String) {
        selectedAddresid = id
        storeDefultAddressId(id)
    }


    fun DeleteAddress(iD: String, position: Int) {
        DeletConfimationDialog(iD, position)
    }

    private fun CallApiDeleteItem(iD: String, position: Int) {
        if (isInternetAvailable(this)) {

            mprogressbar.showProgressBar()

            val param = ParamRemoveAddress().apply {
                addressId = iD
            }

            val retrofit = WebService.getRetrofit(this).create(ApiClient::class.java)
            retrofit.RemoveAddress(param)
                .enqueue(object : Callback<ResponseDeleteAddress> {
                    override fun onFailure(call: Call<ResponseDeleteAddress>, t: Throwable) {
                        mprogressbar.hideProgressBar()
                    }

                    override fun onResponse(
                        call: Call<ResponseDeleteAddress>,
                        response: Response<ResponseDeleteAddress>?
                    ) {
                        mprogressbar.hideProgressBar()

                        if (response != null) {
                            if (response.isSuccessful) {
                                val responsecart = response.body()
                                if (responsecart != null) {
                                    if (responsecart.status == API_SUCESS) {
                                        if (responseAddress.size == 1) {
                                            tvSelectAddress.visibility = View.GONE
                                            liadd_address.visibility = View.VISIBLE
                                            ivEdit?.visibility = View.GONE
                                        }
                                        address_list.move(position)
                                    }
                                }
                            } else {
                                showToast(getString(R.string.msg_common_error),Const.ALERT)
                            }
                        }
                    }
                })

        } else {
            NetworkErrorDialog(this)
        }
    }

    private fun CallApiCheckout(iD: String) {
        mprogressbar.showProgressBar()

        val param = ParamCheckout().apply {
            addressId = iD
            payWithWallet = isWallet
        }

        val retrofit = WebService.getRetrofit(this).create(ApiClient::class.java)
        retrofit.Checkout()
            .enqueue(object : Callback<ResponseCheckout> {
                override fun onFailure(call: Call<ResponseCheckout>, t: Throwable) {
                    mprogressbar.hideProgressBar()
                }

                override fun onResponse(
                    call: Call<ResponseCheckout>,
                    response: Response<ResponseCheckout>?
                ) {
                    mprogressbar.hideProgressBar()

                    if (response != null) {
                        if (response.isSuccessful) {
                            val responsecart = response.body()
                            if (responsecart != null) {
                                if (responsecart.status == API_SUCESS) {
                                    if (responsecart.data.makePayment) {
                                        storeCartItem(0)
//                                        val intent = Intent(
//                                            this@AddressListActivity,
//                                            HomeActivity::class.java
//                                        )
//                                        intent.putExtra(Const.ORDER_ID, "1")
//                                        startActivity(intent)

                                        SuccesDialog("Order successfully ")
                                    } else {
                                        CallApiPaymentType()
                                    }
                                }
                            }
                        } else {
                            showToast(getString(R.string.msg_common_error),Const.ALERT)
                        }
                    }
                }
            })
    }

    private fun DeletConfimationDialog(iD: String, position: Int) {
        val dialog = CustomDialog(this,"",
            getString(R.string.msg_confirmation_delete),
            "Yes",
            "No",
            CustomDialog.ItemReturnListener {
                CallApiDeleteItem(iD, position)
            })
        dialog.show()
    }


    private fun CallApiPaymnet(value: String?) {

        mprogressbar.showProgressBar()
        val param = ParamPayment().apply {
            paymentType = value?.toInt()
            payWithWallet = isWallet
        }

        val retrofit = WebService.getRetrofit(this).create(ApiClient::class.java)
      /*  retrofit.MakePayment(param)
            .enqueue(object : Callback<ResponsePayment> {
                override fun onFailure(call: Call<ResponsePayment>, t: Throwable) {
                    mprogressbar.hideProgressBar()
                }

                override fun onResponse(
                    call: Call<ResponsePayment>,
                    response: Response<ResponsePayment>?
                ) {
                    mprogressbar.hideProgressBar()
                    if (response != null) {
                        if (response.isSuccessful) {
                            val responsepayment = response.body()
                            if (responsepayment != null) {
                                if (responsepayment.status == API_SUCESS) {
                                    if (responsepayment.paymentProcessUrl != null && !responsepayment.paymentProcessUrl!!.isEmpty()) {
                                        responsepayment.paymentProcessUrl?.let { storePaymentUrl(it) }
                                        responsepayment.paymentSuccessUrl?.let { storeSuccessUrl(it) }
                                        responsepayment.paymentCancelUrl?.let { storeCancelUrl(it) }
                                        val intent =
                                            Intent(
                                                this@AddressListActivity,
                                                PaymentActivity::class.java
                                            )
                                        startActivity(intent)
                                    } else {
                                        storeCartItem(0)
                                        *//*val intent = Intent(
                                            this@AddressListActivity,
                                            HomeActivity::class.java
                                        )
                                        intent.putExtra(Const.ORDER_ID, "1")
                                        startActivity(intent)*//*
                                        SuccesDialog(responsepayment.message.toString())
                                    }
                                }
                            }
                        } else {
                            showToast(getString(R.string.msg_common_error),Const.ALERT)
                        }
                    }
                }
            })*/
    }


    private fun hasCameraPermission(): Boolean {
        return EasyPermissions.hasPermissions(this, Manifest.permission.CAMERA)
    }

    private fun hasStoragePermission(): Boolean {
        return EasyPermissions.hasPermissions(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
            //  Toast.makeText(this, "Returned from app settings to activity", Toast.LENGTH_SHORT).show();
        }
        if (requestCode == RC_CAMERA_PERM && resultCode == Activity.RESULT_OK) {
            try {
                File(currentPhotoPath)
                CallApiMakePayment()
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
        if (requestCode == RESULT_ADD_ADDRESS) {
            CallApiAddressList()
            ivEdit.isEnabled = true
            liadd_address.isEnabled = true
        }
        if (requestCode == RC_GALLERY_PERM && resultCode == Activity.RESULT_OK) {
            try {
                val selectedImageURI = data?.data
                finalImageFile = File(getRealPathFromURI(selectedImageURI))
                currentPhotoPath = finalImageFile.toString()
                File(currentPhotoPath)
                CallApiMakePayment()
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getRealPathFromURI(uri: Uri?): String? {
        var path = ""
        if (getContentResolver() != null) {
            val cursor: Cursor =
                getContentResolver()!!.query(uri!!, null, null, null, null)!!
            if (cursor != null) {
                cursor.moveToFirst()
                val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
                path = cursor.getString(idx)
                cursor.close()
            }
        }
        return path
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
                if (takePictureIntent.resolveActivity(getPackageManager()!!) != null) {
                    var photoFile: File? = null
                    try {
                        photoFile = createImageFile()
                    } catch (ex: IOException) {
                        ex.printStackTrace()
                    }
                    if (photoFile != null) {
                        val photoURI =
                            FileProvider.getUriForFile(
                                this,
                                getPackageName(),
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
            getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
            imageFileName,  /* prefix */
            ".png",  /* suffix */
            storageDir /* directory */
        )
        currentPhotoPath = image.absolutePath
        return image
    }


    private fun dailogOpenAttachments() {
        try {
            val dialog = Dialog(this)
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

            val tvbanktransfer = dialog.findViewById<TextView>(R.id.tvbanktransfer)
            tvbanktransfer.visibility = View.VISIBLE

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


    private fun CallApiMakePayment() {

        mprogressbar.showProgressBar()

        val okHttpClient = UnsafeOkHttpClient.getUnsafeOkHttpClient()
        AndroidNetworking.initialize(this, okHttpClient)
        AndroidNetworking.enableLogging()
        AndroidNetworking.upload(Apis.BASE_URL + Apis.API_MAKE_PAYMENT)
                .addMultipartFile("transactionImage", File(currentPhotoPath))
                .addMultipartFile("NotesImage",finalImageFile)
                .addMultipartParameter("paymentType", PaymentValue)
            .addMultipartParameter("payWithWallet", isWallet.toString())
            .addHeaders(Const.OS_VERSION, getAndroidVersion()!!)
            .addHeaders(Const.DEVICE_NAME, getDeviceName()!!)
            .addHeaders(Const.USER_TOKEN, getUserToken())
            .addHeaders(Const.USER_ID, getUserId())
            .addHeaders(Const.LANGUAGE_ID, Const.LANG_ENGLISH)
            .addHeaders(Const.DEVICE_ID, getDeviceId())
            .addHeaders(Const.DEVICE_TYPE, Const.DEVICE_TYPE_ANDROID)
            .addHeaders(Const.APP_ID, Const.APP_ID_VERSION)
            .addHeaders(Const.APP_VERSION_NO, BuildConfig.VERSION_NAME)
            .setPriority(Priority.IMMEDIATE)
            .build()
            .setUploadProgressListener(UploadProgressListener { bytesUploaded, totalBytes -> // do anything with progress
            })
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {
                    // do anything with response
                    mprogressbar.hideProgressBar()
                    try {
                        val gson = Gson()
                        if(WebService.context.handleForbiddenResponseJson(response.toString())){
                            val responseProfile =
                                gson.fromJson(response.toString(), ResponseProfile::class.java)
                            val data = responseProfile.data

                            storeCartItem(0)
                            /*   val intent = Intent(
                                   this@AddressListActivity,
                                   HomeActivity::class.java
                               )
                               intent.putExtra(Const.ORDER_ID, "1")
                               startActivity(intent)*/

                            SuccesDialog(responseProfile.message)
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }

                }

                override fun onError(error: ANError) {
                    mprogressbar.hideProgressBar()
                    Log.e("image upload", error.toString())
                    // handle error
                }
            })
    }

    private fun SuccesDialog(msg: String) {
        val dialog = AlertDialog.Builder(this)
            .setTitle(getString(R.string.app_name))
            .setMessage(getString(R.string.msg_order_success))
            .setPositiveButton("OK",
                DialogInterface.OnClickListener { dialog, which ->
                    val intent = Intent(this, HomeActivity::class.java)
                    intent.putExtra(Const.ORDER_ID, "1")
                    startActivity(intent)
                    finishAffinity()
                }).setCancelable(false)
        try {
            if (dialog != null && !isFinishing)
                dialog.show()
        } catch (e: WindowManager.BadTokenException) {
            e.printStackTrace()
        }
    }


}

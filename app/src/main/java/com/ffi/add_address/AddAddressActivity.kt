package com.ffi.add_address

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.ClipData
import android.content.DialogInterface
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ffi.App
import com.ffi.HomeActivity
import com.ffi.R
import com.ffi.Utils.*
import com.ffi.cart.ResponseCheckout
import com.ffi.payment.*
import com.ffi.api.ApiClient
import com.ffi.api.WebService
import com.wajahatkarim3.longimagecamera.LongImageCameraActivity
import com.wajahatkarim3.longimagecamera.getMultipalImageListener
import kotlinx.android.synthetic.main.activity_add_address.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class AddAddressActivity : AppCompatActivity(), View.OnClickListener, getMultipalImageListener {

    lateinit var mprogressbar: ProgressBarHandler
    lateinit var arrCountry: ArrayList<Data>
    lateinit var arrState: ArrayList<DataX>
    var PaymentValue = ""
    val RC_CAMERA_PERM = 123
    val RC_GALLERY_PERM = 124

    private var currentPhotoPath = ""

    private var strCountryId = ""
    private var strStateId = ""
    private var strcustname = ""
    private var strmobile = ""
    private var strbuilding = ""
    private var strstreet = ""
    private var strlandmark = ""
    private var strpincode = ""
    private var strcity = ""
    private var mStrNote = ""
    private var str_reseller_name = ""
    private var str_reseller_no = ""
    private var str_from_name = ""
    private var str_from_no = ""
    private var strcopyaddress = ""
    private var strname = ""
    var sedt_from_name = ""

    private var isNoAddressSelcted = false
    private var ischeckwallet = false
    private var isEnoughBal = false
    private var StrAddressId: String? = null
    private var StrCopyAddressId: String? = null
    var finalImageFile: File? = null

    var AddressType = 0
    var strAddressData = ""
    lateinit var paramdata: ParamAddAddress

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_address)

        mprogressbar = ProgressBarHandler(this)
        init()
    }

    private fun getImages() {
        LongImageCameraActivity.launch(
            this,
            LongImageCameraActivity.ImageMergeMode.HORIZONTAL,
            this
        );
    }

    private fun init() {
        val bundle = intent.extras
        if (bundle != null) {
            if (bundle.containsKey(Const.USE_CREDIT)) {
                ischeckwallet = bundle.getBoolean(Const.USE_CREDIT)
                isNoAddressSelcted = true
                //   Toast.makeText(this, "Wallet checkd - " + ischeckwallet, Toast.LENGTH_LONG).show()
            }
            if (bundle.containsKey("note")) {
                mStrNote = bundle.getString("note")!!
                Log.e("///note/", mStrNote)
            }
            if (bundle.containsKey("noteImage")) {
                finalImageFile = bundle.getSerializable("noteImage") as File?
                if (finalImageFile != null) {
                    Log.e("////", "noteImage// not null")
                } else {
                    Log.e("////", "noteImage// null")
                }
            }
        }
        ivEdit.visibility = View.GONE
        ivBack.visibility = View.VISIBLE
        txt_toolbar.text = getString(R.string.title_add_address)

        /*if (rd_add_addres.isChecked) {
            li_add_addres.visibility = View.VISIBLE
            cv_copy_address.visibility = View.GONE
        } else if (rd_copyaddres.isChecked) {
            li_add_addres.visibility = View.GONE
            cv_copy_address.visibility = View.VISIBLE
        }*/

        setListener()
        CallApiCountryList()


        spState.setOnItemSelectedListener(object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                if (position >= 0 && arrState != null && !arrState.isEmpty()) {
                    strStateId = arrState.get(position).iD
                } else {
                    strStateId = ""
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        })

    }

    private fun CallApiCountryList() {
        if (isInternetAvailable(this)) {
            mprogressbar.showProgressBar()

            val retrofit =
                WebService.getRetrofit(this@AddAddressActivity).create(ApiClient::class.java)
            retrofit.GetCountryList()
                .enqueue(object : Callback<ResponseCountryList> {
                    override fun onFailure(call: Call<ResponseCountryList>, t: Throwable) {
                        mprogressbar.hideProgressBar()
                    }

                    override fun onResponse(
                        call: Call<ResponseCountryList>,
                        response: Response<ResponseCountryList>?
                    ) {
                        mprogressbar.hideProgressBar()
                        if (response != null) {
                            if (response.isSuccessful) {
                                val body = response.body()
                                if (body != null) {
                                    if (body.status.equals(API_SUCESS)) {

                                        arrCountry = ArrayList<Data>()
                                        arrCountry = body.data
                                        spCountry.adapter = SpinnerAdapter(
                                            this@AddAddressActivity,
                                            arrCountry
                                        )
                                        for (i in arrCountry.indices) {
                                            if (arrCountry.get(i).name.equals("India", true)) {
                                                spCountry.setSelection(i)
                                                strCountryId = arrCountry.get(i).id
                                                CallApiStateList(strCountryId)
                                                break
                                            }
                                        }
                                    }
                                }
                            } else {
                                showToast(getString(R.string.msg_common_error), Const.ALERT)
                            }
                        }
                    }
                })
        } else {
            NetworkErrorDialog(this)
        }
    }

    private fun CallApiStateList(id: String) {
        if (isInternetAvailable(this)) {
            mprogressbar.showProgressBar()

            val param = ParamStateList().apply {
                countryId = id
            }
            val retrofit =
                WebService.getRetrofit(this@AddAddressActivity).create(ApiClient::class.java)
            retrofit.GetStateList(param)
                .enqueue(object : Callback<ResponseStateList> {
                    override fun onFailure(call: Call<ResponseStateList>, t: Throwable) {
                        mprogressbar.hideProgressBar()
                    }

                    override fun onResponse(
                        call: Call<ResponseStateList>,
                        response: Response<ResponseStateList>?
                    ) {
                        mprogressbar.hideProgressBar()
                        if (response != null) {
                            if (response.isSuccessful) {
                                val body = response.body()
                                if (body != null) {
                                    if (body.status.equals(API_SUCESS)) {

                                        arrState = ArrayList()
                                        arrState = body.data
                                        val spinnerSubCategoryAdapter = SpinnerStateAdapter(
                                            this@AddAddressActivity,
                                            arrState
                                        )
                                        spState.setAdapter(spinnerSubCategoryAdapter)
                                    }
                                }
                            } else {
                                showToast(getString(R.string.msg_common_error), Const.ALERT)
                            }
                        }
                    }
                })
        } else {
            NetworkErrorDialog(this)
        }
    }

    private fun setListener() {

        edt_name.addTextChangedListener(
            object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    if (s?.toString()?.trim()?.length == 1) {
                        /* edt_copyaddress.setText("")
                         edt_customername.setText("")*/
                    }
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {

                }

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {

                }

            }
        )


        edt_copyaddress.addTextChangedListener(
            object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    if (s?.toString()?.trim()?.length == 1) {
                        EraseDataofAddAddress()
                    }
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {

                }

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {

                }

            }
        )


        edt_customername.addTextChangedListener(
            object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    if (s?.toString()?.trim()?.length == 1) {
                        EraseDataofAddAddress()
                    }
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {

                }

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {

                }

            }
        )
        /*rd_add_addres.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                li_add_addres.visibility = View.VISIBLE
                cv_copy_address.visibility = View.GONE
            }
        }

        rd_copyaddres.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                li_add_addres.visibility = View.GONE
                cv_copy_address.visibility = View.VISIBLE
            }
        }*/

        btn_add_adress?.setOnClickListener(this)
        btn_paste_add_adress?.setOnClickListener(this)
        ivBack.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBack -> {
                onBackPressed()
            }
            R.id.btn_paste_add_adress -> {
                hideKeyboard(btn_paste_add_adress)
                //edt_customer_no
                str_from_name = edt_fromname.text.toString().trim()
                str_from_no = edt_fromno.text.toString().trim()

                str_reseller_name = edt_reselname.text.toString().trim()
                str_reseller_no = edt_reseller_contact.text.toString().trim()

                strcopyaddress = edt_copyaddress.text.toString().trim()
                strname = edt_customername.text.toString().trim()
                strcustname = edt_name.text.toString().trim()
                strmobile = edt_customer_no.text.toString().trim()

                if (StrCopyAddressId != null) {
                    // CallApiCheckout(Const.ADD_ADDRESS, param.toString())
                } else if (strname.isEmpty()) {
                    showToast(getString(R.string.err_cust_name), Const.ALERT)
                    edt_customername.requestFocus()
                } else if (strname.isNotBlank() && CheckNameonlyNumber(strname)) {
                    showToast(getString(R.string.err_valid_name), Const.ALERT)
                    edt_customername.requestFocus()
                } else if (strmobile.isEmpty()) {
                    showToast(getString(R.string.err_valid_mob3), Const.ALERT)
                    edt_customer_no.requestFocus()
                } else if (strmobile.length != 10) {
                    showToast(getString(R.string.err_valid_mob), Const.ALERT)
                    edt_customer_no.requestFocus()
                }/* else if (str_reseller_name.isNotEmpty() && CheckNameonlyNumber(str_reseller_name)) {
                    showToast(getString(R.string.err_valid_name), Const.ALERT)
                    edt_reselname.requestFocus()
                } else if (!str_reseller_no.isEmpty() && str_reseller_no.length < 10) {
                    showToast(getString(R.string.err_valid_mob), Const.ALERT)
                    edt_reseller_contact.requestFocus()
                }*/
                else if (str_from_name.isEmpty()) {
                    showToast(getString(R.string.err_valid_name2), Const.ALERT)
                    edt_fromname.requestFocus()
                } else if (CheckNameonlyNumber(str_from_name)) {
                    showToast(getString(R.string.err_valid_name2), Const.ALERT)
                    edt_fromname.requestFocus()
                } else if (str_from_no.isEmpty()) {
                    showToast(getString(R.string.err_valid_mob2), Const.ALERT)
                    edt_fromno.requestFocus()
                } else if (str_from_no.length != 10) {
                    showToast(getString(R.string.err_valid_mob), Const.ALERT)
                    edt_fromno.requestFocus()
                } else if (strcopyaddress.isEmpty()) {
                    showToast("Please paste address", Const.ALERT)
                    edt_copyaddress.requestFocus()
                } else {
                    paramdata = ParamAddAddress().apply {
                        addressId = "0"
                        completeAddress = strcopyaddress
                        customerName = strname
                        fromcontact = str_from_no
                        fromname = str_from_name
                        ResellerName = str_reseller_name
                        ResellerContact = str_reseller_no
                        CustomerContact = strmobile
                    }
                    AddressType = Const.COPY_ADDRESS
                    strAddressData = paramdata.toString()
                    //showToast(paramdata.toString(), Const.ALERT)
                    CallApiCheckout()
                    //CallApiAddCopyAddress()
                }
            }
            R.id.btn_add_adress -> {
                hideKeyboard(btn_add_adress)
                strcopyaddress = edt_copyaddress.text.toString().trim()
                strname = edt_customername.text.toString().trim()
                strcustname = edt_name.text.toString().trim()
                /* if (strcopyaddress.isEmpty() && strname.isEmpty() && strcustname.isEmpty()) {
                     showToast(getString(R.string.err_select_address), Const.ALERT)
                 } else*/ if (StrAddressId != null) {
                    //  CallApiCheckout(Const.ADD_ADDRESS, param.toString())
                } else/* if (strcopyaddress.isEmpty() && strname.isEmpty() && !strcustname.isEmpty()) */ {
                    isValid()
                }/* else {

                }*/
            }
        }
    }

    fun EraseDataofAddAddress() {
        /* edt_name.setText("")
         edt_phone_no.setText("")
         edt_flat.setText("")
         edt_stret.setText("")
         edt_landmark.setText("")
         edt_pincode.setText("")
         edt_city.setText("")
         edt_from_name.setText("")
         edt_from_contact.setText("")*/
    }

    fun isValid() {
        // sedt_from_name =edt_from_name.text.toString().trim()
        strcustname = edt_name.text.toString().trim()
        strmobile = edt_phone_no.text.toString().trim()
        strbuilding = edt_flat.text.toString().trim()
        strstreet = edt_stret.text.toString().trim()
        strlandmark = edt_landmark.text.toString().trim()
        strpincode = edt_pincode.text.toString().trim()
        strcity = edt_city.text.toString().trim()

        str_from_name = edt_from_name.text.toString().trim()
        str_from_no = edt_from_contact.text.toString().trim()

        str_reseller_name = edt_reseler_name.text.toString().trim()
        str_reseller_no = edt_resler_phone.text.toString().trim()


        if (strcustname.isEmpty()) {
            showToast(getString(R.string.err_cust_name), Const.ALERT)
            edt_name.requestFocus()
        } else if (CheckNameonlyNumber(strcustname)) {
            showToast(getString(R.string.err_valid_name), Const.ALERT)
            edt_name.requestFocus()
        } else if (strmobile.isEmpty()) {
            showToast(getString(R.string.err_valid_mob3), Const.ALERT)
            edt_phone_no.requestFocus()
        } else if (strmobile.length != 10) {
            showToast(getString(R.string.err_valid_mob), Const.ALERT)
            edt_phone_no.requestFocus()
        } /*else if (str_reseller_name.isNotEmpty() && CheckNameonlyNumber(str_reseller_name)) {
            showToast(getString(R.string.err_valid_name), Const.ALERT)
            edt_reseler_name.requestFocus()
        } else if (!str_reseller_no.isEmpty() && str_reseller_no.length < 10) {
            showToast(getString(R.string.err_valid_mob), Const.ALERT)
            edt_resler_phone.requestFocus()
        }*/ else if (strbuilding.isEmpty()) {
            showToast(getString(R.string.err_flat), Const.ALERT)
            edt_flat.requestFocus()
        } else if (strstreet.isEmpty()) {
            showToast(getString(R.string.err_street), Const.ALERT)
            edt_stret.requestFocus()
        } else if (strlandmark.isEmpty()) {
            showToast(getString(R.string.err_landmark), Const.ALERT)
            edt_landmark.requestFocus()
        } else if (strpincode.isEmpty()) {
            showToast(getString(R.string.err_pincode), Const.ALERT)
            edt_pincode.requestFocus()
        } else if (strpincode.length < 6) {
            showToast(getString(R.string.err_valid_pincode), Const.ALERT)
            edt_pincode.requestFocus()
        } else if (strcity.isEmpty()) {
            showToast(getString(R.string.err_city), Const.ALERT)
            edt_city.requestFocus()
        } else if (str_from_name.isEmpty() || CheckNameonlyNumber(str_from_name)) {
            showToast(getString(R.string.err_valid_name2), Const.ALERT)
            edt_from_name.requestFocus()
        } else if (str_from_no.isEmpty() ) {
            showToast(getString(R.string.err_valid_mob2), Const.ALERT)
            edt_from_contact.requestFocus()
        }
        else if (str_from_no.length != 10) {
            showToast(getString(R.string.err_valid_mob), Const.ALERT)
            edt_from_contact.requestFocus()
        } else {
            paramdata = ParamAddAddress().apply {
                customerName = edt_name.text.toString()
                mobileNumber = edt_phone_no.text.toString()
                building = edt_flat.text.toString()
                street = edt_stret.text.toString()
                landmark = edt_landmark.text.toString()
                pincode = edt_pincode.text.toString()
                city = edt_landmark.text.toString()
                countryId = strCountryId
                stateId = strStateId
                fromcontact = str_from_no
                fromname = str_from_name
                ResellerName = str_reseller_name
                ResellerContact = str_reseller_no
            }
            strAddressData = paramdata.toString()
            AddressType = Const.ADD_ADDRESS
//            showToast(paramdata.toString(), Const.ALERT)
            CallApiCheckout()
            //CallApiAddAddress()
        }
    }


    fun CallApiAddAddress() {

        if (isInternetAvailable(this)) {
            mprogressbar.showProgressBar()

            val param = ParamAddAddress().apply {
                customerName = edt_name.text.toString()
                mobileNumber = edt_phone_no.text.toString()
                building = edt_flat.text.toString()
                street = edt_stret.text.toString()
                landmark = edt_landmark.text.toString()
                pincode = edt_pincode.text.toString()
                city = edt_landmark.text.toString()
                countryId = strCountryId
                stateId = strStateId
                fromcontact = str_from_no
                fromname = str_from_name
                ResellerName = str_reseller_name
                ResellerContact = str_reseller_no
            }

            val retrofit =
                WebService.getRetrofit(this@AddAddressActivity).create(ApiClient::class.java)
            retrofit.AddAddress(param)
                .enqueue(object : Callback<ResponseAddAddress> {
                    override fun onFailure(call: Call<ResponseAddAddress>, t: Throwable) {
                        mprogressbar.hideProgressBar()
                        btn_add_adress.isEnabled = true
                    }

                    override fun onResponse(
                        call: Call<ResponseAddAddress>,
                        response: Response<ResponseAddAddress>?
                    ) {
                        mprogressbar.hideProgressBar()
                        if (response != null) {
                            if (response.isSuccessful) {
                                val body = response.body()
                                if (body != null) {
                                    if (body.status.equals(API_SUCESS)) {
                                        showBottomToast(body.message)

                                        App.isDialogOpen = false
                                        /*  if (isNoAddressSelcted) {
                                              *//* intent = Intent(
                                                 this@AddAddressActivity,
                                                 AddressListActivity::class.java
                                             )
                                             intent.putExtra(Const.USE_CREDIT, ischeckwallet)
                                             startActivity(intent)*//*
                                        } else {
                                            setResult(RESULT_ADD_ADDRESS)

                                        }
                                        finish()*/
                                        StrAddressId = body.addressId
                                        CallApiCheckout()
                                    } else {
                                        btn_add_adress.isEnabled = true
                                    }
                                }
                            } else {
                                btn_add_adress.isEnabled = true
                                showToast(getString(R.string.msg_common_error), Const.ALERT)
                            }
                        }
                    }
                })

        } else {
            NetworkErrorDialog(this)
        }
    }

    fun CallApiAddCopyAddress() {
        if (isInternetAvailable(this)) {
            mprogressbar.showProgressBar()

            val param = ParamCopyAddress().apply {
                addressId = "0"
                completeAddress = strcopyaddress
                CustomerName = strname
                fromcontact = str_from_no
                fromname = str_from_name
                ResellerName = str_reseller_name
                ResellerContact = str_reseller_no
            }

            val retrofit =
                WebService.getRetrofit(this@AddAddressActivity).create(ApiClient::class.java)
            retrofit.AddCopyAddress(param)
                .enqueue(object : Callback<ResponseCopyAddress> {
                    override fun onFailure(call: Call<ResponseCopyAddress>, t: Throwable) {
                        mprogressbar.hideProgressBar()
                    }

                    override fun onResponse(
                        call: Call<ResponseCopyAddress>,
                        response: Response<ResponseCopyAddress>?
                    ) {
                        mprogressbar.hideProgressBar()
                        if (response != null) {
                            if (response.isSuccessful) {
                                val body = response.body()
                                if (body != null) {
                                    if (body.status.equals(API_SUCESS)) {
                                        /* if (isNoAddressSelcted) {
                                             intent = Intent(
                                                 this@AddAddressActivity,
                                                 AddressListActivity::class.java
                                             )
                                             intent.putExtra(Const.USE_CREDIT, ischeckwallet)
                                             startActivity(intent)
                                         } else {
                                             setResult(RESULT_ADD_ADDRESS)
                                         }
                                         finish()*/
                                        StrCopyAddressId = body.data.AddressId
                                        CallApiCheckout()
                                        //showToast(body.message)
                                    } else {
                                    }
                                }
                            } else {
                                showToast(getString(R.string.msg_common_error), Const.ALERT)
                            }
                        }
                    }
                })

        } else {
            NetworkErrorDialog(this)
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
                                    Log.e("//////////repo", responsepayment.message + "////")

                                    if (responsepayment.status == API_SUCESS) {


                                        /*   if (ischeckwallet) {
                                               var count = 0
                                               for (i in responsepayment.data?.indices!!) {
                                                   if (responsepayment.data!!.get(i).value.equals("1")) {
                                                       CallApiPaymnet(responsepayment.data?.get(i)?.value)
                                                       count++
                                                       break
                                                   }
                                               }
                                               if (count == 0) {
                                                   OpenPaymentDialog(responsepayment.data)
                                               }
                                           } else {*/
                                        OpenPaymentDialog(responsepayment.data)
                                        //}
                                    }
                                }
                            } else {
                                /*    Toast.makeText(
                                        this@AddAddressActivity,
                                        "Api payment type code - " + response.code() + " message - " + response.message(),
                                        Toast.LENGTH_LONG
                                    ).show()*/
                                showToast(getString(R.string.msg_common_error), Const.ALERT)
                            }
                        }
                    }
                })

        } else {
            NetworkErrorDialog(this)
        }
    }

    private fun OpenPaymentDialog(data: java.util.ArrayList<com.ffi.payment.DataX>?) {
        try {
            val dialog = Dialog(this, R.style.mydialog)
            val window = dialog.window
            window!!.setLayout(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            window.setGravity(Gravity.BOTTOM)
            dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
            dialog.setContentView(R.layout.dialog_payment_type)

            val recyclerview = dialog.findViewById<RecyclerView>(R.id.recyclerview)

            recyclerview.apply {
                layoutManager = LinearLayoutManager(context)
                val service_list = PaymentListAdapter(this@AddAddressActivity,
                    PaymentListClickInterface {
                        dialog.cancel()
                        val id = data?.get(it)?.id
                        if (id.equals(Const.BANK_TRANSFER)) {
                            if (ischeckwallet) {
                                val msg = "You can not make payment by wallet and " + data!!.get(
                                    it
                                )?.displayName + " at time"
                                Toast.makeText(this@AddAddressActivity, msg, Toast.LENGTH_LONG)
                                    .show()
                            } else {
                                PaymentValue = data?.get(it)?.value.toString()
                                dailogOpenAttachments()
                            }
                        } else {
                            CallApiPaymnet(data?.get(it)?.value)
                        }
                    })
                data?.let { service_list.addModels(it) }
                adapter = service_list
            }

            dialog.setCancelable(false)
            dialog.setCanceledOnTouchOutside(true)
            dialog.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    //without image
    private fun CallApiPaymnet(value: String?) {

        Log.e("///////mStrNote", "///" + mStrNote)

        mprogressbar.showProgressBar()
        var mMultipartBody: MultipartBody.Part? = null

        if (finalImageFile != null && finalImageFile!!.exists()) {
            val requestFile = RequestBody.create(
                "image/*".toMediaTypeOrNull(),
                finalImageFile!!
            )

            //multipart/form-data
            //image/*
            mMultipartBody = MultipartBody.Part.createFormData(
                "NotesImage",
                finalImageFile!!.getName(),
                requestFile
            )
        }
        val retrofit = WebService.getRetrofit(this).create(ApiClient::class.java)

        if (finalImageFile != null && mMultipartBody != null) {
            Log.e("///////mStrNote", "///1")

                    retrofit.MakePayment(
                value?.toInt()!!,
                ischeckwallet,
                AddressType,
                paramdata,
                mStrNote,
                mMultipartBody!!
            )
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
                                            responsepayment.paymentProcessUrl?.let {
                                                storePaymentUrl(
                                                    it
                                                )
                                            }
                                            responsepayment.paymentSuccessUrl?.let {
                                                storeSuccessUrl(
                                                    it
                                                )
                                            }
                                            responsepayment.paymentCancelUrl?.let {
                                                storeCancelUrl(
                                                    it
                                                )
                                            }
                                            val intent =
                                                Intent(
                                                    this@AddAddressActivity,
                                                    PaymentActivity::class.java
                                                )
                                            startActivity(intent)
                                        } else {
                                            storeCartItem(0)
                                            SuccesDialog(responsepayment.message.toString())
                                        }
                                    }
                                }
                            } else {
                                /*    Toast.makeText(
                                        this@AddAddressActivity,
                                        "Payment code - " + response.code() + " messagee - " + response.message(),
                                        Toast.LENGTH_LONG
                                    ).show()*/
                                showToast(getString(R.string.msg_common_error), Const.ALERT)
                            }
                        }
                    }
                })
        }
        else if (!TextUtils.isEmpty(mStrNote)) {
            Log.e("///////mStrNote", "///2")

            retrofit.MakePaymentWithoutFile(
                value?.toInt()!!,
                ischeckwallet,
                AddressType,
                mStrNote,
                "",
                paramdata
            )
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
                                            responsepayment.paymentProcessUrl?.let {
                                                storePaymentUrl(
                                                    it
                                                )
                                            }
                                            responsepayment.paymentSuccessUrl?.let {
                                                storeSuccessUrl(
                                                    it
                                                )
                                            }
                                            responsepayment.paymentCancelUrl?.let {
                                                storeCancelUrl(
                                                    it
                                                )
                                            }
                                            val intent =
                                                Intent(
                                                    this@AddAddressActivity,
                                                    PaymentActivity::class.java
                                                )
                                            startActivity(intent)
                                        } else {
                                            storeCartItem(0)
                                            SuccesDialog(responsepayment.message.toString())
                                        }
                                    }
                                }
                            } else {
                                /*    Toast.makeText(
                                        this@AddAddressActivity,
                                        "Payment code - " + response.code() + " messagee - " + response.message(),
                                        Toast.LENGTH_LONG
                                    ).show()*/
                                showToast(getString(R.string.msg_common_error), Const.ALERT)
                            }
                        }
                    }
                })
        } else {
            Log.e("///////mStrNote", "///3")
            retrofit.MakePayment(
                value?.toInt()!!,
                ischeckwallet,
                AddressType,
                paramdata
            )
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
                                            responsepayment.paymentProcessUrl?.let {
                                                storePaymentUrl(
                                                    it
                                                )
                                            }
                                            responsepayment.paymentSuccessUrl?.let {
                                                storeSuccessUrl(
                                                    it
                                                )
                                            }
                                            responsepayment.paymentCancelUrl?.let {
                                                storeCancelUrl(
                                                    it
                                                )
                                            }
                                            val intent =
                                                Intent(
                                                    this@AddAddressActivity,
                                                    PaymentActivity::class.java
                                                )
                                            startActivity(intent)
                                        } else {
                                            storeCartItem(0)
                                            SuccesDialog(responsepayment.message.toString())
                                        }
                                    }
                                }
                            } else {
                                /*    Toast.makeText(
                                        this@AddAddressActivity,
                                        "Payment code - " + response.code() + " messagee - " + response.message(),
                                        Toast.LENGTH_LONG
                                    ).show()*/
                                showToast(getString(R.string.msg_common_error), Const.ALERT)
                            }
                        }
                    }
                })
        }
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
                //openCamera()
                getImages()
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
                val galleryIntent = Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                ).putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
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

    private fun hasCameraPermission(): Boolean {
        return EasyPermissions.hasPermissions(this, Manifest.permission.CAMERA)
    }

    private fun hasStoragePermission(): Boolean {
        return EasyPermissions.hasPermissions(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }

    @Throws(IOException::class)
    private fun createImageFile(): File? {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
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

    private fun CallApiCheckout() {
        mprogressbar.showProgressBar()

        /*  val param = ParamCheckout().apply {
              addressId = iD
              payWithWallet = ischeckwallet
          }*/

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
                                    if (ischeckwallet) {
                                        if (responsecart.data.makePayment) {
                                            isEnoughBal = true
                                            CallApiPaymnet("1")
                                        } else {
                                            isEnoughBal = false
                                            CallApiPaymentType()
                                        }
                                    } else {
                                        CallApiPaymentType()
                                    }

                                } else {
                                    showToast(responsecart.message, Const.ALERT)

                                }
                            }
                        } else {
                            /* Toast.makeText(
                                 this@AddAddressActivity,
                                 "Api checkout code - " + response.code() + " message - " + response.message(),
                                 Toast.LENGTH_LONG
                             ).show()*/
                            showToast(getString(R.string.msg_common_error), Const.ALERT)
                        }
                    }
                }
            })
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
        if (resultCode === RESULT_OK && requestCode === LongImageCameraActivity.LONG_IMAGE_RESULT_CODE && data != null) {

            val imageFileName: ArrayList<String> =
                data.getStringArrayListExtra(LongImageCameraActivity.IMAGE_PATH_KEY)!!
            Log.e("///////image", imageFileName.toString())
            Log.e("///////imageFileName", imageFileName.size.toString())

            val Images: ArrayList<Uri> = ArrayList()

            if (imageFileName.size > 0) {
                for (i in 0 until imageFileName.size) {
                    Images.add(Uri.parse(imageFileName.get(i)))
                }
                CallApiMakePaymentMultipal(Images, "camera")
            }
        }
        if (requestCode == RC_GALLERY_PERM && resultCode == Activity.RESULT_OK) {

            val Images: ArrayList<Uri> = ArrayList()
            if (data?.clipData != null) {
                val mClipData: ClipData = data?.clipData!!
                if (mClipData.getItemCount() > 5) {
                    Toast.makeText(this, "You can add only 5 images", Toast.LENGTH_SHORT).show()
                } else {
                    for (i in 0 until mClipData.itemCount) {
                        val item = mClipData.getItemAt(i)
                        val imageUri = item.uri
                        Log.e("///////imageUri", imageUri.toString())

                        Images.add(imageUri)
                    }
                    CallApiMakePaymentMultipal(Images, "gallary")
                }

            } else if (data?.data != null) {
                Images.add(data?.data!!)
                CallApiMakePaymentMultipal(Images, "gallary")

            }

            Log.e("//////images", "///" + Images.size)

            /*  try {
                val selectedImageURI = data?.data
                finalImageFile = File(getRealPathFromURI(selectedImageURI))
                currentPhotoPath = finalImageFile.toString()
                File(currentPhotoPath)
                CallApiMakePayment()
                // CallApiPaymnet(PaymentValue)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
                Log.e("image", "line no 986 - " + e.toString())
            }*/
        }
    }

    private fun CallApiMakePaymentMultipal(Images: ArrayList<Uri>, type: String) {

        mprogressbar.showProgressBar()

        val ImagesFile: ArrayList<MultipartBody.Part> = ArrayList()

        Log.e("//////Images", "//function/" + Images.size)

        for (i in 0 until Images.size) {

            Log.e("//////uir", "//function///" + Images.get(i).path)

            var file: File

            if (type.equals("camera")) {
                file = File(Images.get(i).path)
            } else {
                file = File(getRealPathFromURI(Images.get(i)))
            }


            var mMultipartBody: MultipartBody.Part? = null

            if (file != null && file.exists()) {
                Log.e("//////file", "//exist/" + Images.size)
                val requestFile = RequestBody.create(
                    "multipart/form-data".toMediaTypeOrNull(),
                    file
                )
                mMultipartBody = MultipartBody.Part.createFormData(
                    "transactionImages[]",
                    file.getName(),
                    requestFile
                )
                ImagesFile.add(mMultipartBody)
            }
        }

        Log.e("//////ImagesFile", "///" + ImagesFile.size)

        var mMultipartBodyNote: MultipartBody.Part? = null

        if (finalImageFile != null && finalImageFile!!.exists()) {
            val requestFile = RequestBody.create(
                "multipart/form-data".toMediaTypeOrNull(),
                finalImageFile!!
            )
            mMultipartBodyNote = MultipartBody.Part.createFormData(
                "NotesImage",
                finalImageFile!!.getName(),
                requestFile
            )
        }
        val retrofit = WebService.getRetrofit(this).create(ApiClient::class.java)

        if (finalImageFile != null && mMultipartBodyNote != null) {

            retrofit.MakePaymentMultipalImage(
                PaymentValue.toInt(),
                ischeckwallet,
                AddressType,
                paramdata,
                ImagesFile,
                mStrNote,
                mMultipartBodyNote!!
            )
                .enqueue(object : Callback<ResponsePayment> {
                    override fun onFailure(call: Call<ResponsePayment>, t: Throwable) {
                        mprogressbar.hideProgressBar()
                        Log.e("//////error", "///" + t.message)

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
                                            responsepayment.paymentProcessUrl?.let {
                                                storePaymentUrl(
                                                    it
                                                )
                                            }
                                            responsepayment.paymentSuccessUrl?.let {
                                                storeSuccessUrl(
                                                    it
                                                )
                                            }
                                            responsepayment.paymentCancelUrl?.let {
                                                storeCancelUrl(
                                                    it
                                                )
                                            }
                                            val intent =
                                                Intent(
                                                    this@AddAddressActivity,
                                                    PaymentActivity::class.java
                                                )
                                            startActivity(intent)
                                        } else {
                                            storeCartItem(0)
                                            SuccesDialog(responsepayment.message.toString())
                                        }
                                    }
                                }
                            } else {
                                /*  Toast.makeText(
                                      this@AddAddressActivity,
                                      "code - " + response.code() + " message - " + response.message(),
                                      Toast.LENGTH_LONG
                                  ).show()*/
                                showToast(getString(R.string.msg_common_error), Const.ALERT)
                            }
                        }
                    }
                })
        } else if (!TextUtils.isEmpty(mStrNote)) {

            retrofit.MakePaymentMultipalImage(
                PaymentValue.toInt(),
                ischeckwallet,
                AddressType,
                paramdata,
                ImagesFile,
                mStrNote,
                ""
            )
                .enqueue(object : Callback<ResponsePayment> {
                    override fun onFailure(call: Call<ResponsePayment>, t: Throwable) {
                        mprogressbar.hideProgressBar()
                        Log.e("//////error", "///" + t.message)

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
                                            responsepayment.paymentProcessUrl?.let {
                                                storePaymentUrl(
                                                    it
                                                )
                                            }
                                            responsepayment.paymentSuccessUrl?.let {
                                                storeSuccessUrl(
                                                    it
                                                )
                                            }
                                            responsepayment.paymentCancelUrl?.let {
                                                storeCancelUrl(
                                                    it
                                                )
                                            }
                                            val intent =
                                                Intent(
                                                    this@AddAddressActivity,
                                                    PaymentActivity::class.java
                                                )
                                            startActivity(intent)
                                        } else {
                                            storeCartItem(0)
                                            SuccesDialog(responsepayment.message.toString())
                                        }
                                    }
                                }
                            } else {
                                /*  Toast.makeText(
                                      this@AddAddressActivity,
                                      "code - " + response.code() + " message - " + response.message(),
                                      Toast.LENGTH_LONG
                                  ).show()*/
                                showToast(getString(R.string.msg_common_error), Const.ALERT)
                            }
                        }
                    }
                })
        } else {
            retrofit.MakePaymentMultipalImage(
                PaymentValue.toInt(),
                ischeckwallet,
                AddressType,
                paramdata,
                ImagesFile
            )
                .enqueue(object : Callback<ResponsePayment> {
                    override fun onFailure(call: Call<ResponsePayment>, t: Throwable) {
                        mprogressbar.hideProgressBar()
                        Log.e("//////error", "///" + t.message)

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
                                            responsepayment.paymentProcessUrl?.let {
                                                storePaymentUrl(
                                                    it
                                                )
                                            }
                                            responsepayment.paymentSuccessUrl?.let {
                                                storeSuccessUrl(
                                                    it
                                                )
                                            }
                                            responsepayment.paymentCancelUrl?.let {
                                                storeCancelUrl(
                                                    it
                                                )
                                            }
                                            val intent =
                                                Intent(
                                                    this@AddAddressActivity,
                                                    PaymentActivity::class.java
                                                )
                                            startActivity(intent)
                                        } else {
                                            storeCartItem(0)
                                            SuccesDialog(responsepayment.message.toString())
                                        }
                                    }
                                }
                            } else {
                                /*  Toast.makeText(
                                      this@AddAddressActivity,
                                      "code - " + response.code() + " message - " + response.message(),
                                      Toast.LENGTH_LONG
                                  ).show()*/
                                showToast(getString(R.string.msg_common_error), Const.ALERT)
                            }
                        }
                    }
                })
        }





        Log.e("param", paramdata.toString())
        /*    val okHttpClient = UnsafeOkHttpClient.getUnsafeOkHttpClient()
            AndroidNetworking.initialize(this, okHttpClient)
            AndroidNetworking.enableLogging()
            AndroidNetworking.upload(Apis.BASE_URL + Apis.API_MAKE_PAYMENT)
                .addMultipartFile("transactionImage", File(currentPhotoPath))
                .addMultipartParameter("paymentType", PaymentValue)
                .addMultipartParameter("payWithWallet", ischeckwallet.toString())
                .addMultipartParameter("addressType", AddressType.toString())
                .addMultipartParameter("addressData", paramdata.toString())
                .addHeaders(Const.OS_VERSION, getAndroidVersion()!!)
                .addHeaders(Const.DEVICE_NAME, getDeviceName()!!)
            .addHeaders(Const.APP_ID, Const.APP_ID_VERSION)
            .addHeaders(Const.APP_VERSION_NO, BuildConfig.VERSION_NAME)
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
                            val responseProfile =
                                gson.fromJson(response.toString(), ResponseProfile::class.java)
                            val data = responseProfile.data

                            storeCartItem(0)
                            *//*   val intent = Intent(
                               this@AddressListActivity,
                               HomeActivity::class.java
                           )
                           intent.putExtra(Const.ORDER_ID, "1")
                           startActivity(intent)*//*

                        SuccesDialog(responseProfile.message)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        Log.e("image", e.toString())
                        mprogressbar.hideProgressBar()
                    }

                }

                override fun onError(error: ANError) {
                    mprogressbar.hideProgressBar()
                    Log.e("image upload", error.toString())
                    // handle error
                }
            })*/
    }

    //With Image
    private fun CallApiMakePayment() {
        mprogressbar.showProgressBar()
        var mMultipartBody: MultipartBody.Part? = null
        var file = File(currentPhotoPath)
        if (file != null && file.exists()) {
            val requestFile = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)
            mMultipartBody =
                MultipartBody.Part.createFormData("transactionImages", file.getName(), requestFile)
        }
        var mMultipartBodyNote: MultipartBody.Part? = null

        if (finalImageFile != null && finalImageFile!!.exists()) {
            val requestFile = RequestBody.create(
                "multipart/form-data".toMediaTypeOrNull(),
                finalImageFile!!
            )
            mMultipartBodyNote = MultipartBody.Part.createFormData(
                "NotesImage",
                finalImageFile!!.getName(),
                requestFile
            )
        }

        val retrofit = WebService.getRetrofit(this).create(ApiClient::class.java)

        if (finalImageFile != null && mMultipartBodyNote!! != null) {
            retrofit.MakePayment(
                PaymentValue.toInt(),
                ischeckwallet,
                AddressType,
                paramdata,
                mMultipartBody!!,
                mStrNote,
                mMultipartBodyNote!!
            )
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
                                            responsepayment.paymentProcessUrl?.let {
                                                storePaymentUrl(
                                                    it
                                                )
                                            }
                                            responsepayment.paymentSuccessUrl?.let {
                                                storeSuccessUrl(
                                                    it
                                                )
                                            }
                                            responsepayment.paymentCancelUrl?.let {
                                                storeCancelUrl(
                                                    it
                                                )
                                            }
                                            val intent =
                                                Intent(
                                                    this@AddAddressActivity,
                                                    PaymentActivity::class.java
                                                )
                                            startActivity(intent)
                                        } else {
                                            storeCartItem(0)
                                            SuccesDialog(responsepayment.message.toString())
                                        }
                                    }
                                }
                            } else {
                                /*  Toast.makeText(
                                      this@AddAddressActivity,
                                      "code - " + response.code() + " message - " + response.message(),
                                      Toast.LENGTH_LONG
                                  ).show()*/
                                showToast(getString(R.string.msg_common_error), Const.ALERT)
                            }
                        }
                    }
                })

        } else if (!TextUtils.isEmpty(mStrNote)) {

            retrofit.MakePayment(
                PaymentValue.toInt(),
                ischeckwallet,
                AddressType,
                paramdata,
                mMultipartBody!!,
                mStrNote,
                ""
            )
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
                                            responsepayment.paymentProcessUrl?.let {
                                                storePaymentUrl(
                                                    it
                                                )
                                            }
                                            responsepayment.paymentSuccessUrl?.let {
                                                storeSuccessUrl(
                                                    it
                                                )
                                            }
                                            responsepayment.paymentCancelUrl?.let {
                                                storeCancelUrl(
                                                    it
                                                )
                                            }
                                            val intent =
                                                Intent(
                                                    this@AddAddressActivity,
                                                    PaymentActivity::class.java
                                                )
                                            startActivity(intent)
                                        } else {
                                            storeCartItem(0)
                                            SuccesDialog(responsepayment.message.toString())
                                        }
                                    }
                                }
                            } else {
                                /*  Toast.makeText(
                                      this@AddAddressActivity,
                                      "code - " + response.code() + " message - " + response.message(),
                                      Toast.LENGTH_LONG
                                  ).show()*/
                                showToast(getString(R.string.msg_common_error), Const.ALERT)
                            }
                        }
                    }
                })
        } else {
            retrofit.MakePayment(
                PaymentValue.toInt(),
                ischeckwallet,
                AddressType,
                paramdata,
                mMultipartBody!!
            )
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
                                            responsepayment.paymentProcessUrl?.let {
                                                storePaymentUrl(
                                                    it
                                                )
                                            }
                                            responsepayment.paymentSuccessUrl?.let {
                                                storeSuccessUrl(
                                                    it
                                                )
                                            }
                                            responsepayment.paymentCancelUrl?.let {
                                                storeCancelUrl(
                                                    it
                                                )
                                            }
                                            val intent =
                                                Intent(
                                                    this@AddAddressActivity,
                                                    PaymentActivity::class.java
                                                )
                                            startActivity(intent)
                                        } else {
                                            storeCartItem(0)
                                            SuccesDialog(responsepayment.message.toString())
                                        }
                                    }
                                }
                            } else {
                                /*  Toast.makeText(
                                      this@AddAddressActivity,
                                      "code - " + response.code() + " message - " + response.message(),
                                      Toast.LENGTH_LONG
                                  ).show()*/
                                showToast(getString(R.string.msg_common_error), Const.ALERT)
                            }
                        }
                    }
                })
        }




        Log.e("param", paramdata.toString())

        /*    val okHttpClient = UnsafeOkHttpClient.getUnsafeOkHttpClient()
            AndroidNetworking.initialize(this, okHttpClient)
            AndroidNetworking.enableLogging()
            AndroidNetworking.upload(Apis.BASE_URL + Apis.API_MAKE_PAYMENT)
                .addMultipartFile("transactionImage", File(currentPhotoPath))
                .addMultipartParameter("paymentType", PaymentValue)
                .addMultipartParameter("payWithWallet", ischeckwallet.toString())
                .addMultipartParameter("addressType", AddressType.toString())
                .addMultipartParameter("addressData", paramdata.toString())
                .addHeaders(Const.OS_VERSION, getAndroidVersion()!!)
                .addHeaders(Const.DEVICE_NAME, getDeviceName()!!)
                .addHeaders(Const.USER_TOKEN, getUserToken())
                .addHeaders(Const.USER_ID, getUserId())
                .addHeaders(Const.LANGUAGE_ID, Const.LANG_ENGLISH)
                .addHeaders(Const.DEVICE_ID, getDeviceId())
            .addHeaders(Const.APP_ID, Const.APP_ID_VERSION)
            .addHeaders(Const.APP_VERSION_NO, BuildConfig.VERSION_NAME)
                .addHeaders(Const.DEVICE_TYPE, Const.DEVICE_TYPE_ANDROID)
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
                            val responseProfile =
                                gson.fromJson(response.toString(), ResponseProfile::class.java)
                            val data = responseProfile.data

                            storeCartItem(0)
                            *//*   val intent = Intent(
                               this@AddressListActivity,
                               HomeActivity::class.java
                           )
                           intent.putExtra(Const.ORDER_ID, "1")
                           startActivity(intent)*//*

                        SuccesDialog(responseProfile.message)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        Log.e("image", e.toString())
                        mprogressbar.hideProgressBar()
                    }

                }

                override fun onError(error: ANError) {
                    mprogressbar.hideProgressBar()
                    Log.e("image upload", error.toString())
                    // handle error
                }
            })*/
    }

    fun getRealPathFromURI(uri: Uri?): String? {
        var path = ""
        if (getContentResolver() != null) {
            val cursor: Cursor = getContentResolver()!!.query(uri!!, null, null, null, null)!!
            if (cursor != null) {
                cursor.moveToFirst()
                val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
                path = cursor.getString(idx)
                cursor.close()
            }
        }
        return path
    }

    override fun onDestroy() {
        super.onDestroy()
        StrCopyAddressId = null
        StrAddressId = null
    }

    override fun onBackPressed() {
        val dialog = CustomDialog(this, "",
            getString(R.string.msg_confirmation_back_address),
            "Yes",
            "No",
            CustomDialog.ItemReturnListener {
                super.onBackPressed()
            })
        dialog.show()
    }

    fun createPartFromString(descriptionString: String?): RequestBody {
        return if (descriptionString == null) RequestBody.create(
            MultipartBody.FORM,
            ""
        ) else RequestBody.create(
            MultipartBody.FORM, descriptionString
        )
    }

    public override fun onImageSelected(bitmap: Bitmap?) {
        Log.e("/////bitmap", bitmap.toString())
    }
}

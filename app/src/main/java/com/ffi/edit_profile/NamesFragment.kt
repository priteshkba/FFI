package com.ffi.edit_profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.androidnetworking.AndroidNetworking
import com.ffi.HomeActivity
import com.ffi.R
import com.ffi.Utils.*
import com.ffi.api.ApiClient
import com.ffi.api.Apis
import com.ffi.api.WebService
import com.ffi.home.HomeFragment
import com.ffi.login.UpdateSocialLoginModel
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_names.*
import pub.devrel.easypermissions.AppSettingsDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NamesFragment() : Fragment(),
    View.OnClickListener {

    var homeActivity: HomeActivity? = null
    lateinit var mprogressbar: ProgressBarHandler

    var posGender: Int = 0
    lateinit var arraySpinner: Array<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_names, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


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
                                            mprogressbar.hideProgressBar()
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

        edt_fname.setText(data.firstName)
        edt_lname.setText(data.lastName)


        if (data.media.isEmpty()) {
            mprogressbar.hideProgressBar()
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
        homeActivity?.navigation?.visibility = View.GONE


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
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_done -> {
                isValid()
            }
        }
    }

    private fun isValid() {
        val strfname = edt_fname.text.toString().trim()
        val strlname = edt_lname.text.toString().trim()

        if (strfname.isEmpty()) {
            activity?.showToast(getString(R.string.err_fname), Const.ALERT)
            edt_fname.requestFocus()
        } else if (strlname.isEmpty()) {
            activity?.showToast(getString(R.string.err_lname), Const.ALERT)
            edt_lname.requestFocus()
        } else {
            if (isInternetAvailable(requireActivity())) {
                CallApiEditProfile()
            } else {
                NetworkErrorDialog(requireActivity())
            }
        }
    }

    private fun CallApiEditProfile() {
        mprogressbar.showProgressBar()

        val strlname = edt_lname.text.toString()
        val strfname = edt_fname.text.toString()

        Log.e("tagRespEditProfile", "strlname " + strlname)
        Log.e("tagRespEditProfile", "strfname " + strfname)
        Log.e("tagRespEditProfile", "posGender.toString() " + posGender.toString())
        Log.e("tagRespEditProfile", "getAndroidVersion()!! " + getAndroidVersion()!!)
        Log.e("tagRespEditProfile", "getDeviceName()!! " + getDeviceName()!!)
        Log.e("tagRespEditProfile", "getUserToken() " + getUserToken())
        Log.e("tagRespEditProfile", "getUserId()!! " + getUserId())
        Log.e("tagRespEditProfile", "activity?.getDeviceId() " + activity?.getDeviceId())

        val okHttpClient = UnsafeOkHttpClient.getUnsafeOkHttpClient()
        AndroidNetworking.initialize(requireActivity(), okHttpClient)
        AndroidNetworking.enableLogging()

        Log.e("tagRespEditProfile", "calling api " + (Apis.BASE_URL + Apis.API_SOCIAL_UPDATE_PROFILE))

//        AndroidNetworking.post(Apis.BASE_URL + Apis.API_SOCIAL_UPDATE_PROFILE)
//            .addBodyParameter("Gender", posGender.toString())
//            .addBodyParameter("LastName", strlname)
//            .addBodyParameter("FirstName", strfname)
//            .addHeaders(Const.OS_VERSION, getAndroidVersion()!!)
//            .addHeaders(Const.DEVICE_NAME, getDeviceName()!!)
//            .addHeaders(Const.USER_TOKEN, getUserToken())
//            .addHeaders(Const.USER_ID, getUserId())
//            .addHeaders(Const.LANGUAGE_ID, Const.LANG_ENGLISH)
//            .addHeaders(Const.DEVICE_ID, activity?.getDeviceId())
//            .addHeaders(Const.DEVICE_TYPE, Const.DEVICE_TYPE_ANDROID)
//            .addHeaders(Const.APP_ID, Const.APP_ID_VERSION)
//            .addHeaders(Const.APP_VERSION_NO, BuildConfig.VERSION_NAME)
//            .setPriority(Priority.IMMEDIATE)
//            .build()
////            .setUploadProgressListener(UploadProgressListener { bytesUploaded, totalBytes -> // do anything with progress
////            })
//            .getAsJSONObject(object : JSONObjectRequestListener {
//                override fun onResponse(response: JSONObject) {
//                    // do anything with response
//                    Log.e("tagRespEditProfile", "CallApiEditProfile onResponse()!! " + response)
//                    mprogressbar.hideProgressBar()
//                    try {
//                        val gson = Gson()
//                        if(WebService.context.handleForbiddenResponseJson(response.toString())){
//                            Log.e("tagRespEditProfile", "CallApiEditProfile onResponse()!! within handleForbiddenResponseJson ")
//                            val responseProfile =
//                                gson.fromJson(response.toString(), ResponseProfile::class.java)
//                            val data = responseProfile.data
//
//                            setDataPrefrence(data)
//                            activity?.showBottomToast(getString(R.string.msg_profile_updated))
//                            homeActivity?.replaceFragment(HomeFragment(), "1")
//                        }
//
//                    } catch (e: JSONException) {
//                        Log.e("tagRespEditProfile", "CallApiEditProfile onResponse()!! within handleForbiddenResponseJson error " + e.message)
//                        e.printStackTrace()
//                    }
//
//                }
//
//                override fun onError(error: ANError) {
//                    Log.e("tagRespEditProfile", "CallApiEditProfile onError error " + error.message)
//                    mprogressbar.hideProgressBar()
//                    Log.e("image upload", error.toString())
//                    // handle error
//                }
//            })


        if(activity != null && !activity?.isFinishing!!){
            val retrofit = WebService.getRetrofit(activity?.applicationContext!!).create(ApiClient::class.java)

            val param = UpdateSocialLoginModel().apply {
                Gender = posGender.toString()
                FirstName = strfname
                LastName = strlname
            }

            retrofit.UpdateSocialLogin(param)
                .enqueue(object : Callback<ResponseProfile> {
                    override fun onFailure(call: Call<ResponseProfile>, t: Throwable) {
                        mprogressbar.hideProgressBar()
                    }

                    override fun onResponse(
                        call: Call<ResponseProfile>,
                        response: Response<ResponseProfile>?
                    ) {
                        mprogressbar.hideProgressBar()
                        if (response != null) {
                            if (response.isSuccessful) {
                                val responseLogin = response.body()

                                if (responseLogin != null && responseLogin.data != null) {
                                    val data = responseLogin.data

                                    setDataPrefrence(data!!)
                                    activity?.showBottomToast(getString(R.string.msg_profile_updated))
                                    homeActivity?.replaceFragment(HomeFragment(), "1")
                                }
                            }
                        }
                    }
                })
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


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
            //  Toast.makeText(this, "Returned from app settings to activity", Toast.LENGTH_SHORT).show();
        }
    }
}
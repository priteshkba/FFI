package com.ffi.signup

import android.app.Dialog
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.CountDownTimer
import android.os.SystemClock
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.chaos.view.PinView
import com.ffi.App
import com.ffi.HomeActivity
import com.ffi.R
import com.ffi.Utils.*
import com.ffi.WebviewActivity
import com.ffi.login.ParamLogin
import com.ffi.login.ResponseLogin
import com.ffi.otpscreen.Data
import com.ffi.otpscreen.ParamOTP
import com.ffi.otpscreen.ResponseOTP
import com.ffi.api.ApiClient
import com.ffi.api.WebService
import com.google.android.gms.auth.api.phone.SmsRetriever
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_signup.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignupActivity : AppCompatActivity(), View.OnClickListener,
    MySMSBroadcastReceiver.OTPReceiveListener {

    lateinit var mprogressbar: ProgressBarHandler

    var strfname = ""
    var strlname = ""
    var stremail = ""
    var strmob = ""
    var googleid = ""
    var facebookid = ""

    lateinit var userotp: String
    lateinit var serverotp: String

    private val smsBroadcast: MySMSBroadcastReceiver = MySMSBroadcastReceiver()
    private var lastTimeSel: Long = 0
    private var mLastClickTime: Long = 0

    lateinit var edt1: EditText
    lateinit var edt2: EditText
    lateinit var edt3: EditText
    lateinit var edt4: EditText
    lateinit var pinView: PinView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        mprogressbar = ProgressBarHandler(this)

        GetIntentData()
        setListener()

    }

    private fun setListener() {
        btn_continue.setOnClickListener(this)
        tvTerms.setOnClickListener(this)
        tvprivacy.setOnClickListener(this)
    }

    private fun GetIntentData() {
        if (intent != null) {
            edt_email.setText(intent.getStringExtra(Const.EMAIL))
            edt_fname.setText(intent.getStringExtra(Const.DISPLAY_NAME))
            edt_lname.setText(intent.getStringExtra(Const.LAST_NAME))

            if (intent.hasExtra(Const.GOOGLE_ID)) {
                googleid = intent.getStringExtra(Const.GOOGLE_ID)!!
            }

            if (intent.hasExtra(Const.FACEBOOK_ID)) {
                facebookid = intent.getStringExtra(Const.FACEBOOK_ID)!!
            }
        }
    }

    private fun CallApiLogin(IsOpenDialog: Boolean) {
        if (isInternetAvailable(this)) {
            mprogressbar.showProgressBar()

            val param = ParamLogin().apply {
                MobileNumber = strmob
                PhoneCode = "91"
                DeviceToken = getFcmToken()
            }

            val retrofit = WebService.getRetrofit(this@SignupActivity).create(ApiClient::class.java)
            retrofit.DoLogin(param)
                .enqueue(object : Callback<ResponseLogin> {
                    override fun onFailure(call: Call<ResponseLogin>, t: Throwable) {
                        mprogressbar.hideProgressBar()
                        btn_continue.isEnabled = true
                    }

                    override fun onResponse(
                        call: Call<ResponseLogin>,
                        response: Response<ResponseLogin>?
                    ) {
                        mprogressbar.hideProgressBar()
                        if (response != null) {
                            if (response.isSuccessful) {
                                val responseLogin = response.body()

                                if (responseLogin != null) {
                                    if (responseLogin.status == API_SUCESS) {
                                        if (!IsOpenDialog) {
                                            startSMSListener()

                                            smsBroadcast.initOTPListener(this@SignupActivity)
                                            val intentFilter = IntentFilter()
                                            intentFilter.addAction(SmsRetriever.SMS_RETRIEVED_ACTION)
                                            applicationContext.registerReceiver(
                                                smsBroadcast,
                                                intentFilter
                                            )

                                            DialogOTP()
                                            serverotp = responseLogin.data.otp.toString()
                                            btn_continue.isEnabled = true
                                        }
                                    } else {
                                        showToast(getString(R.string.err_mob_invalid), Const.ALERT)
                                        btn_continue.isEnabled = true
                                    }
                                }
                            } else {
                                showToast(getString(R.string.msg_common_error), Const.ALERT)
                                btn_continue.isEnabled = true
                            }
                        }
                    }
                })
        } else {
            NetworkErrorDialog(this)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_continue -> {
                isvalid()
            }
            R.id.tvTerms -> {
                lastTimeSel = SystemClock.elapsedRealtime() - mLastClickTime
                if (lastTimeSel > 0 && lastTimeSel < 500) {
                    return
                }
                mLastClickTime = SystemClock.elapsedRealtime()
                val intent = Intent(this, WebviewActivity::class.java)
                intent.putExtra(Const.URL, getTerms())
                intent.putExtra(Const.TITLE, getString(R.string.menu_terms_condition))
                startActivity(intent)
            }
            R.id.tvprivacy -> {
                lastTimeSel = SystemClock.elapsedRealtime() - mLastClickTime
                if (lastTimeSel > 0 && lastTimeSel < 500) {
                    return
                }
                mLastClickTime = SystemClock.elapsedRealtime()
                val intent = Intent(this, WebviewActivity::class.java)
                intent.putExtra(Const.URL, getPrivacy())
                intent.putExtra(Const.TITLE, "Privacy Policy")
                startActivity(intent)
            }
        }
    }

    private fun isvalid() {
        strfname = edt_fname.text.toString()
        strlname = edt_lname.text.toString()
        stremail = edt_email.text.toString()
        strmob = edt_mob.text.toString()

        if (strfname.isEmpty()) {
            showToast(getString(R.string.err_fname), Const.ALERT)
            edt_fname.requestFocus()
        } else if (strlname.isEmpty()) {
            showToast(getString(R.string.err_lname), Const.ALERT)
            edt_lname.requestFocus()
        } else if (stremail.isEmpty()) {
            showToast(getString(R.string.err_email), Const.ALERT)
            edt_email.requestFocus()
        } else if (strmob.isEmpty()) {
            showToast(getString(R.string.err_enter_mob_no), Const.ALERT)
            edt_mob.requestFocus()
        } else {
            CallApiLogin(false)
        }
    }

    fun DialogOTP() {
        val dialog = Dialog(this)
        val window = dialog.window
        window!!.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        window.setGravity(Gravity.CENTER)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setContentView(R.layout.dailog_otp)

        val btncontinue = dialog.findViewById<TextView>(R.id.btn_continue)
        val ivclosw = dialog.findViewById<ImageView>(R.id.ivclose)


        val tvResend = dialog.findViewById<TextView>(R.id.tvResend)

        object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                if ((millisUntilFinished / 1000).toInt() != 0) {
                    tvResend.text = getString(
                        R.string.lbl_resend_counter,
                        millisUntilFinished / 1000
                    )
                }
                //here you can have your logic to set text to edittext
            }

            override fun onFinish() {
                tvResend.text = getString(R.string.lbl_resend)
            }
        }.start()

        tvResend.setOnClickListener {
            if (tvResend.text.toString().equals(getString(R.string.lbl_resend))) {
                CallApiLogin(true)

                startSMSListener()

                smsBroadcast.initOTPListener(this)
                val intentFilter = IntentFilter()
                intentFilter.addAction(SmsRetriever.SMS_RETRIEVED_ACTION)
                applicationContext.registerReceiver(smsBroadcast, intentFilter)

            }
        }

        edt1 = dialog.findViewById<EditText>(R.id.edt1)
        edt2 = dialog.findViewById<EditText>(R.id.edt2)
        edt3 = dialog.findViewById<EditText>(R.id.edt3)
        edt4 = dialog.findViewById<EditText>(R.id.edt4)
        pinView = dialog.findViewById<PinView>(R.id.pinView)

        edt1.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val text = s.toString()
                if (text.length >= 1) {
                    edt2.requestFocus()
                    edt1.background = resources.getDrawable(R.drawable.bg_edittext_otp)
                } else {
                    edt1.background = resources.getDrawable(R.drawable.bg_edittext_otp)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().length > 1)
                    edt1.setText((s?.get(s.toString().length - 1)?.toString() ?: ""))
                edt2.requestFocus()
                edt2.setSelection(edt2.text.toString().trim().length)
                Log.e("focus", "line no 504")
            }

        })
        edt2.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

                val text = s.toString()
                if (text.length >= 1) {
                    edt3.requestFocus()
                    edt2.background = resources.getDrawable(R.drawable.bg_edittext_otp)
                } else if (text.length == 0) {
                    edt1.requestFocus()
                    edt2.background = resources.getDrawable(R.drawable.bg_edittext_otp)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                if (s.toString().length > 1)
                    edt2.setText((s?.get(s.toString().length - 1)?.toString() ?: ""))
                edt3.requestFocus()
                edt3.setSelection(edt3.text.toString().trim().length)
            }

        })
        edt3.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val text = s.toString()
                if (text.length >= 1) {
                    edt4.requestFocus()
                    edt3.background = resources.getDrawable(R.drawable.bg_edittext_otp)
                } else if (text.length == 0) {
                    edt2.requestFocus()
                    edt3.background = resources.getDrawable(R.drawable.bg_edittext_otp)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().length > 1)
                    edt3.setText((s?.get(s.toString().length - 1)?.toString() ?: ""))
                edt4.requestFocus()
                edt4.setSelection(edt4.text.toString().trim().length)
                Log.e("focus", "line no 504")
            }

        })
        edt4.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val text = s.toString()
                if (text.length >= 1) {
                    edt4.background = resources.getDrawable(R.drawable.bg_edittext_otp)
                } else if (text.length == 0) {
                    edt3.requestFocus()
                    edt4.background = resources.getDrawable(R.drawable.bg_edittext_otp)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().length > 1)
                    edt4.setText((s?.get(s.toString().length - 1)?.toString() ?: ""))

                Log.e("focus", "line no 504")
            }
        })

        btncontinue.setOnClickListener {
            /* userotp =
                 edt1.text.toString() + edt2.text.toString() + edt3.text.toString() + edt4.text.toString()*/

            userotp = pinView.text.toString()

            if (userotp.length != 4) {
                showToast(getString(R.string.err_correct_otp), Const.ALERT)
            } else if (!userotp.equals(serverotp)) {
                showToast(getString(R.string.err_incorrect_otp), Const.ALERT)
            } else {
                lastTimeSel = SystemClock.elapsedRealtime() - mLastClickTime
                if (lastTimeSel > 0 && lastTimeSel < 500) {
                    return@setOnClickListener
                }
                mLastClickTime = SystemClock.elapsedRealtime()
                CallApiVerifyOTP()
            }
        }
        ivclosw.setOnClickListener { dialog.dismiss() }
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()

    }

    private fun CallApiVerifyOTP() {
        if (isInternetAvailable(this)) {
            mprogressbar.showProgressBar()

            val param = ParamOTP().apply {
                MobileNumber = strmob
                PhoneCode = "91"
                OTP = userotp
                FirstName = strfname
                LastName = strlname
                GoogleId = googleid
                FacebookId = facebookid
                EmailAddress = stremail
                DeviceToken = getFcmToken()
            }

            val retrofit = WebService.getRetrofit(this@SignupActivity).create(ApiClient::class.java)
            retrofit.VerifyOtp(param)
                .enqueue(object : Callback<ResponseOTP> {
                    override fun onFailure(call: Call<ResponseOTP>, t: Throwable) {
                        mprogressbar.hideProgressBar()
                        // btn_continue.isEnabled = true
                    }

                    override fun onResponse(
                        call: Call<ResponseOTP>,
                        response: Response<ResponseOTP>?
                    ) {
                        mprogressbar.hideProgressBar()
                        if (response != null) {
                            if (response.isSuccessful) {
                                val body = response.body()
                                if (body != null) {
                                    if (body.status.equals(API_SUCESS)) {
                                        val responseLogin = body.data
                                        setPrefrence(responseLogin)
                                        App.isDialogOpen = false
                                        /*  if (App.isRedirect) {
                                              App.isRedirect = false
                                              finish()
                                          } else {*/
                                        val intent =
                                            Intent(
                                                this@SignupActivity,
                                                HomeActivity::class.java
                                            )
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP and Intent.FLAG_ACTIVITY_NEW_TASK)
                                        intent.putExtra(Const.FROM_LOGIN, true)
                                        startActivity(intent)
                                        finishAffinity()

                                        //  }
                                    } else {
                                        //btn_continue.isEnabled = true
                                        showToast(
                                            getString(R.string.err_incorrect_otp),
                                            Const.ALERT
                                        )
                                    }
                                }
                            } else {
                                //btn_continue.isEnabled = true
                                showToast(getString(R.string.msg_common_error), Const.ALERT)
                            }
                        }
                    }
                })

        } else {
            NetworkErrorDialog(this)
        }
    }

    fun setPrefrence(responseLogin: Data) {
        storeUserLoginStatus(true)
        storeUserId(responseLogin.UserId)
        storeUserToken(responseLogin.UserToken)

        storeMediaLink(responseLogin.BasicUrls.MediaUrl)
        storeBasicUrl(responseLogin.BasicUrls.SiteUrl)
        storePaymentUrl(responseLogin.BasicUrls.PaymentUrl)
        storeSuccessUrl(responseLogin.BasicUrls.PaymentSuccessUrl)
        storeCancelUrl(responseLogin.BasicUrls.PaymentCancelUrl)


        storeFirstName(responseLogin.FirstName)
        storeLastName(responseLogin.LastName)
        storeMobileNo(responseLogin.MobileNumber)
        storeEmail(responseLogin.EmailAddress)
        storePhoneCode(responseLogin.PhoneCode)
        storeDefultAddressId(responseLogin.DefaultAddressId)
    }

    private fun startSMSListener() {
        val client = SmsRetriever.getClient(this)
        val task = client.startSmsRetriever()
        task.addOnSuccessListener {
            //   Toast.makeText(OTPActivity.this, "SMS Retriever starts", Toast.LENGTH_LONG).show();
        }
        task.addOnFailureListener {
            // Toast.makeText(OTPActivity.this, "Error", Toast.LENGTH_LONG).show();
        }
    }

    override fun onOTPReceived(otp: String) {
        if (smsBroadcast != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(smsBroadcast)
        }

        val otpdigit = otp.split("".toRegex()).toTypedArray()
        edt1.setText(otpdigit[1])
        edt2.setText(otpdigit[2])
        edt3.setText(otpdigit[3])
        edt4.setText(otpdigit[4])
        pinView.setText(otp)

        /* userotp =
             edt1.text.toString() + edt2.text.toString() + edt3.text.toString() + edt4.text.toString()*/
        userotp = pinView.text.toString()

        lastTimeSel = SystemClock.elapsedRealtime() - mLastClickTime
        if (lastTimeSel > 0 && lastTimeSel < 500) {
            return
        }
        mLastClickTime = SystemClock.elapsedRealtime()
        CallApiVerifyOTP()
    }

    override fun onOTPTimeOut() {

    }

}

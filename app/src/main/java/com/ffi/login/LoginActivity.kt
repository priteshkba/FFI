package com.ffi.login

import android.R.attr
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.content.IntentFilter
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.CountDownTimer
import android.os.SystemClock
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.chaos.view.PinView
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.ffi.App
import com.ffi.HomeActivity
import com.ffi.R
import com.ffi.Utils.*
import com.ffi.WebviewActivity
import com.ffi.home.ResponseBasicUrl
import com.ffi.otpscreen.Data
import com.ffi.otpscreen.ParamOTP
import com.ffi.otpscreen.ResponseOTP
import com.ffi.signup.SignupActivity
import com.ffi.api.ApiClient
import com.ffi.api.WebService
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.reflect.Field
import java.util.*


class LoginActivity : AppCompatActivity(), View.OnClickListener,
    MySMSBroadcastReceiver.OTPReceiveListener {

    lateinit var mprogressbar: ProgressBarHandler
    lateinit var callbackManager: CallbackManager

    lateinit var userotp: String
    lateinit var serverotp: String

    lateinit var googleSignInOptions: GoogleSignInOptions
    lateinit var mGoogleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 121

    var googleid: String = ""
    var facebookid: String = ""
    var emailid: String = ""
    var firstname: String = ""
    var lastname: String = ""
    var mobile_no: String = ""

    private val smsBroadcast: MySMSBroadcastReceiver = MySMSBroadcastReceiver()
    private var lastTimeSel: Long = 0
    private var mLastClickTime: Long = 0

    lateinit var edt1: EditText
    lateinit var edt2: EditText
    lateinit var edt3: EditText
    lateinit var edt4: EditText
    lateinit var pinView: PinView

    var valueedt1 = ""
    var valueedt2 = ""
    var valueedt3 = ""
    var valueedt4 = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mprogressbar = ProgressBarHandler(this)

        initGoogle()
        initFacebook()

        setListner()
        CallApiBasicUrl()
    }

    private fun initFacebook() {

        callbackManager = CallbackManager.Factory.create()

        LoginManager.getInstance().registerCallback(callbackManager,
            object : FacebookCallback<LoginResult?> {
                override fun onSuccess(loginResult: LoginResult?) {

                    Log.e("///////success", "facebook")
                    // App code
                    val request: GraphRequest = GraphRequest.newMeRequest(
                        loginResult!!.accessToken,
                        object : GraphRequest.GraphJSONObjectCallback {

                            override fun onCompleted(
                                `object`: JSONObject?,
                                response: GraphResponse?
                            ) {

                                // Application code
                                try {
                                    facebookid = `object`!!.getString("id")
                                    val strdisplayname = `object`.getString("name").split(" ")
                                    if (strdisplayname.size > 1) {
                                        firstname = strdisplayname[0]
                                        lastname = strdisplayname[1]
                                    } else {
                                        firstname = strdisplayname[0]
                                    }
                                    googleid = ""
                                    emailid = `object`.getString("email")
                                    CallApiSocialLogin()
                                } catch (e: JSONException) {
                                    e.printStackTrace()
                                    //showBottomToast("")
                                }

                            }
                        })
                    val parameters = Bundle()
                    parameters.putString("fields", "id,name,email,picture.width(300).height(300)")
                    request.parameters = parameters
                    //request.setParameters(parameters)
                    request.executeAsync()
                }

                override fun onCancel() {
                    Log.e("////res", "cancel");
                }

                override fun onError(exception: FacebookException) {
                    Log.e("////res", "error-" + exception.toString());
                }
            })
    }

    private fun initGoogle() {
        googleSignInOptions =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)
    }

    private fun setListner() {
        btn_google.setOnClickListener(this)
        btn_facebook.setOnClickListener(this)
        btn_next.setOnClickListener(this)
        tvSkip.setOnClickListener(this)
        tvTerms.setOnClickListener(this)
        tvprivacy.setOnClickListener(this)
        /*fbLoginBtn.setReadPermissions( Arrays.asList("public_profile", "email"))
        fbLoginBtn.registerCallback(callbackManager,
            object : FacebookCallback<LoginResult?> {
                override fun onSuccess(loginResult: LoginResult?) {

                    Log.e("///////success","facebook")
                    // App code
                    val request: GraphRequest = GraphRequest.newMeRequest(
                        loginResult!!.accessToken,
                        object : GraphRequest.GraphJSONObjectCallback {

                            override fun onCompleted(`object`: JSONObject?, response: GraphResponse?) {

                                // Application code
                                try {
                                    facebookid = `object`!!.getString("id")
                                    val strdisplayname = `object`.getString("name").split(" ")
                                    if (strdisplayname.size > 1) {
                                        firstname = strdisplayname[0]
                                        lastname = strdisplayname[1]
                                    } else {
                                        firstname = strdisplayname[0]
                                    }
                                    googleid = ""
                                    emailid = `object`.getString("email")
                                    CallApiSocialLogin()
                                } catch (e: JSONException) {
                                    e.printStackTrace()
                                    //showBottomToast("")
                                }

                            }
                        })
                    val parameters = Bundle()
                    parameters.putString("fields","id,name,email,picture.width(300).height(300)")
                    request.parameters=parameters
                    //request.setParameters(parameters)
                    request.executeAsync()
                }

                override fun onCancel() {
                    Log.e("////res", "cancel");
                }

                override fun onError(exception: FacebookException) {
                    Log.e("////res", "error-" + exception.toString());
                }
            })
        fbLoginBtn.setOnClickListener {
            FacebookLogin()
        }*/
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tvTerms -> {
                lastTimeSel = SystemClock.elapsedRealtime() - mLastClickTime;
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
                lastTimeSel = SystemClock.elapsedRealtime() - mLastClickTime;
                if (lastTimeSel > 0 && lastTimeSel < 500) {
                    return
                }
                mLastClickTime = SystemClock.elapsedRealtime()
                val intent = Intent(this, WebviewActivity::class.java)
                intent.putExtra(Const.URL, getPrivacy())
                intent.putExtra(Const.TITLE, "Privacy Policy")
                startActivity(intent)
            }
            R.id.btn_next -> {
                lastTimeSel = SystemClock.elapsedRealtime() - mLastClickTime;
                if (lastTimeSel > 0 && lastTimeSel < 500) {
                    return
                }
                mLastClickTime = SystemClock.elapsedRealtime()
                isValid()
            }
            R.id.btn_google -> {
                lastTimeSel = SystemClock.elapsedRealtime() - mLastClickTime;
                if (lastTimeSel > 0 && lastTimeSel < 500) {
                    return
                }
                mLastClickTime = SystemClock.elapsedRealtime()
                val account = GoogleSignIn.getLastSignedInAccount(this)
                GoogleLogin()
            }
            R.id.btn_facebook -> {
                lastTimeSel = SystemClock.elapsedRealtime() - mLastClickTime;
                if (lastTimeSel > 0 && lastTimeSel < 500) {
                    return
                }
                mLastClickTime = SystemClock.elapsedRealtime()
                logoutFromFacebook()
                FacebookLogin()
            }
            R.id.tvSkip -> {
                lastTimeSel = SystemClock.elapsedRealtime() - mLastClickTime;
                if (lastTimeSel > 0 && lastTimeSel < 500) {
                    return
                }
                mLastClickTime = SystemClock.elapsedRealtime()
                App.isDialogOpen = false
                storeSKipStatus(true)
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                finishAffinity()
            }

        }
    }

    private fun FacebookLogin() {

        val connectivityManager =
            getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {

            LoginManager.getInstance()
                .logInWithReadPermissions(this, Arrays.asList("public_profile", "email"))
        } else {
            NetworkErrorDialog(this)
        }
    }

    private fun CallApiLogin(IsDialogOpen: Boolean) {
        if (isInternetAvailable(this)) {
            mprogressbar.showProgressBar()

            val param = ParamLogin().apply {
                MobileNumber = mobile_no
                PhoneCode = "91"
                DeviceToken = getFcmToken()
            }

            val retrofit = WebService.getRetrofit(this@LoginActivity).create(ApiClient::class.java)
            retrofit.DoLogin(param)
                .enqueue(object : Callback<ResponseLogin> {
                    override fun onFailure(call: Call<ResponseLogin>, t: Throwable) {
                        mprogressbar.hideProgressBar()
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

                                        serverotp = responseLogin.data.otp.toString()
                                        if (!IsDialogOpen) {
                                            startSMSListener()

                                            smsBroadcast.initOTPListener(this@LoginActivity)
                                            val intentFilter = IntentFilter()
                                            intentFilter.addAction(SmsRetriever.SMS_RETRIEVED_ACTION)
                                            applicationContext.registerReceiver(
                                                smsBroadcast,
                                                intentFilter
                                            )

                                            DialogOTP()
                                        }
                                        showToast(getString(R.string.msg_otp_sent), Const.TOAST)

                                    } else {
                                        if (responseLogin.message != null && !TextUtils.isEmpty(
                                                responseLogin.message
                                            )
                                        )
                                            showToast(responseLogin.message, Const.ALERT)
                                        else {
                                            showToast(
                                                getString(R.string.err_mob_invalid),
                                                Const.ALERT
                                            )
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

    private fun CallApiSocialLogin() {
        if (isInternetAvailable(this)) {

            mprogressbar.showProgressBar()

            val param = ParamSocialLogin().apply {
                EmailAddress = emailid
                GoogleId = googleid
                FacebookId = facebookid
                FirstName = firstname
                LastName = lastname
            }

            Log.e("/////param", param.toString())

            val retrofit = WebService.getRetrofit(this@LoginActivity).create(ApiClient::class.java)
            retrofit.SocialLogin(param)
                .enqueue(object : Callback<ResponseSocialLogin> {
                    override fun onFailure(call: Call<ResponseSocialLogin>, t: Throwable) {
                        mprogressbar.hideProgressBar()
                    }

                    override fun onResponse(
                        call: Call<ResponseSocialLogin>,
                        response: Response<ResponseSocialLogin>?
                    ) {
                        mprogressbar.hideProgressBar()
                        if (response != null) {
                            if (response.isSuccessful) {
                                val responseLogin = response.body()

                                if (responseLogin != null) {
                                    if (responseLogin.status == API_DATA_NOT_AVAILBLE) {
                                        val intent =
                                            Intent(this@LoginActivity, SignupActivity::class.java)
                                        intent.putExtra(Const.DISPLAY_NAME, firstname)
                                        intent.putExtra(Const.LAST_NAME, lastname)
                                        intent.putExtra(Const.EMAIL, emailid)
                                        intent.putExtra(Const.GOOGLE_ID, googleid)
                                        intent.putExtra(Const.FACEBOOK_ID, facebookid)
                                        startActivity(intent)
                                    } else if (responseLogin.status == API_SUCESS) {
                                        storeIsSocialLogin(true)
                                        setPrefrence(responseLogin.data)

                                        if (App.isRedirect) {
                                            App.isRedirect = false
                                            finish()
                                        } else {
                                            val intent =
                                                Intent(this@LoginActivity, HomeActivity::class.java)
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP and Intent.FLAG_ACTIVITY_NEW_TASK)
                                            intent.putExtra(Const.FROM_LOGIN, false)
                                            startActivity(intent)
                                            finishAffinity()
                                        }

                                    } else {
                                        showToast(getString(R.string.err_mob_invalid), Const.ALERT)
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


    private fun isValid() {
        mobile_no = edtMobile.text.toString()

        if (mobile_no.length == 0) {
            showToast(getString(R.string.err_enter_mob_no), Const.ALERT)
            edtMobile.requestFocus()
        } else if (mobile_no.length < 10) {
            showToast(getString(R.string.err_valid_mob), Const.ALERT)
            edtMobile.requestFocus()
        } else {
            CallApiLogin(false)
        }
    }


    fun GoogleLogin() {
        val signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)

        /*  if (requestCode == RC_SIGN_IN) {
              val task = GoogleSignIn.getSignedInAccountFromIntent(data)
              handleSignInResult(task)
          }*/
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {

        Log.e("//////task", completedTask.toString());
        try {

            val account = completedTask.getResult(ApiException::class.java)
            Log.e("////res", "account...." + account)

            Log.e("////res", "succesfull" + account?.id + " Email id-" + account?.email)

            googleid = account?.id!!
            facebookid = ""
            emailid = account?.email!!
            val strdisplayname = account?.displayName.toString().split(" ")
            if (strdisplayname.size > 1) {
                firstname = strdisplayname[0]
                lastname = strdisplayname[1]
            } else {
                firstname = strdisplayname[0]
            }
            CallApiSocialLogin()


        } catch (e: ApiException) {
            Log.e(
                "TAG",
                "signInResult:failed code=" + e.statusCode
            )
            Log.e(
                "TAG",
                "signInResult:failed message=" + e
            )
        }
    }

    protected fun logoutFromFacebook() {
        //Method to Logout Facebook
        LoginManager.getInstance().logOut()
    }

    override fun onResume() {
        super.onResume()
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

        setTimer(tvResend)

        tvResend.setOnClickListener {
            if (tvResend.text.toString().equals(getString(R.string.lbl_resend))) {
                setTimer(tvResend)
                smsBroadcast.initOTPListener(this)
                startSMSListener()
                val intentFilter = IntentFilter()
                intentFilter.addAction(SmsRetriever.SMS_RETRIEVED_ACTION)
                applicationContext.registerReceiver(smsBroadcast, intentFilter)

                CallApiLogin(true)
            }
        }

        edt1 = dialog.findViewById<EditText>(R.id.edt1)
        edt2 = dialog.findViewById<EditText>(R.id.edt2)
        edt3 = dialog.findViewById<EditText>(R.id.edt3)
        edt4 = dialog.findViewById<EditText>(R.id.edt4)
        pinView = dialog.findViewById<PinView>(R.id.pinView)

        edt1.requestFocus()

        edt1.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                Log.e("focus", "line no 483 - " + s.toString())
                val text = s.toString()
                if (text.length >= 1) {
                    edt2.requestFocus()
                    edt2.setSelection(edt2.text.toString().trim().length)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                Log.e("focus", s.toString() + " - " + valueedt1)

                if (s.toString().length > 1) {
                    Log.e("focus", s.toString() + " - " + valueedt1 + " line no 502")
                    var cnt = 0
                    for (i in s.toString().indices) {
                        if (!s?.get(i)?.toString().equals(valueedt1)) {
                            valueedt1 = s?.get(i).toString()
                            edt1.setText(valueedt1)
                            cnt = 0
                            break
                        }
                    }

                    if (cnt == 0) {
                        Log.e("focus", s.toString() + " - " + valueedt1 + " line no 514")
                        valueedt1 = s?.get(0).toString()
                        edt1.setText(valueedt1)
                    }
                } else {
                    Log.e("focus", s.toString() + " - " + valueedt1 + " line no 519")
                    valueedt1 = s.toString()
                }
                if (count >= 1) {
                    edt2.requestFocus()
                    edt2.setSelection(edt2.text.toString().trim().length)
                }
            }
        })


        edt2.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val text = s.toString()
                if (text.length >= 1) {
                    edt3.requestFocus()
                    edt3.setSelection(edt3.text.toString().trim().length)
                } else if (text.length == 0) {
                    /*edt1.requestFocus()
                    edt1.setSelection(edt1.text.toString().trim().length)*/
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // if (start == 1) {
                if (s.toString().length > 1) {
                    var cnt = 0
                    for (i in s.toString().indices) {
                        if (!s?.get(i)?.toString().equals(valueedt2)) {
                            valueedt2 = s?.get(i).toString()
                            edt2.setText(valueedt2)
                            cnt++
                            break
                        }
                    }

                    if (cnt == 0) {
                        valueedt2 = s?.get(0).toString()
                        edt2.setText(valueedt2)
                    }
                } else {
                    valueedt2 = s.toString()
                }
                if (count >= 1) {
                    edt3.requestFocus()
                    edt3.setSelection(edt3.text.toString().trim().length)
                }
                //}
            }

        })
        edt3.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val text = s.toString()
                if (text.length >= 1) {
                    edt4.requestFocus()
                    edt4.setSelection(edt4.text.toString().trim().length)
                } else if (text.length == 0) {
                    /*edt2.requestFocus()
                    edt2.setSelection(edt2.text.toString().trim().length)*/
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().length > 1) {

                    var cnt = 0
                    for (i in s.toString().indices) {
                        if (!s?.get(i)?.toString().equals(valueedt3)) {
                            valueedt3 = s?.get(i).toString()
                            edt3.setText(valueedt3)
                            cnt++
                            break
                        }
                    }
                    Log.e("focus", "count - " + cnt)
                    if (cnt == 0) {
                        valueedt3 = s?.get(0).toString()
                        edt3.setText(valueedt3)
                    }
                } else {
                    valueedt3 = s.toString()
                }
                if (count >= 1) {
                    edt4.requestFocus()
                    edt4.setSelection(edt4.text.toString().trim().length)
                    Log.e("focus", "line no 504")
                }
            }

        })
        edt4.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val text = s.toString()
                if (text.length >= 1) {

                } else if (text.length == 0) {
                    /*edt3.requestFocus()
                    edt3.setSelection(edt3.text.toString().trim().length)*/
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().length > 1) {
                    var cnt = 0
                    for (i in s.toString().indices) {
                        if (!s?.get(i)?.toString().equals(valueedt4)) {
                            valueedt4 = s?.get(i).toString()
                            edt4.setText(valueedt4)
                            cnt++
                            break
                        }
                    }

                    if (cnt == 0) {
                        valueedt4 = s?.get(0).toString()
                        edt4.setText(valueedt4)
                    }
                } else {
                    valueedt4 = s.toString()
                }

            }
            //   }

        })

        btncontinue.setOnClickListener {
            /*userotp =
                edt1.text.toString() + edt2.text.toString() + edt3.text.toString() + edt4.text.toString()*/

            userotp = pinView.text.toString()

            if (userotp.length != 4) {
                showToast(getString(R.string.err_correct_otp), Const.ALERT)
            } else if (!userotp.equals(serverotp)) {
                showToast(getString(R.string.err_incorrect_otp), Const.ALERT)
            } else {
                lastTimeSel = SystemClock.elapsedRealtime() - mLastClickTime;
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
        dialog?.show()
    }


    private fun setTimer(txt: TextView) {
        object : CountDownTimer(60000, 1000) {
            @SuppressLint("StringFormatMatches")
            override fun onTick(millisUntilFinished: Long) {
                if ((millisUntilFinished / 1000).toInt() != 0) {
                    txt.setText(
                        getString(
                            R.string.lbl_resend_counter,
                            millisUntilFinished / 1000
                        )
                    )
                }
            }

            override fun onFinish() {
                txt.setText(getString(R.string.lbl_resend))
            }
        }.start()
    }

    private fun CallApiVerifyOTP() {
        if (isInternetAvailable(this)) {
            mprogressbar.showProgressBar()
            val param = ParamOTP().apply {
                MobileNumber = mobile_no
                PhoneCode = "91"
                OTP = userotp
                FirstName = ""
                LastName = ""
                GoogleId = googleid
                FacebookId = facebookid
                EmailAddress = ""
                DeviceToken = getFcmToken()
            }
            val retrofit = WebService.getRetrofit(this@LoginActivity).create(ApiClient::class.java)
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
                                        setPrefrenceLogin(responseLogin)
                                        if (App.isRedirect) {
                                            App.isRedirect = false
                                            finish()
                                        } else if (responseLogin.FirstName.isEmpty() && responseLogin.LastName.isEmpty()) {
                                            val intent =
                                                Intent(this@LoginActivity, HomeActivity::class.java)
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP and Intent.FLAG_ACTIVITY_NEW_TASK)
                                            intent.putExtra(Const.TAG_FRAG_EDIT_PROFILE, true)
                                            startActivity(intent)
                                            finishAffinity()
                                        } else {
                                            val intent =
                                                Intent(this@LoginActivity, HomeActivity::class.java)
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP and Intent.FLAG_ACTIVITY_NEW_TASK)
                                            startActivity(intent)
                                            finishAffinity()
                                        }
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

    fun setPrefrenceLogin(data: Data) {
        storeUserLoginStatus(true)
        storeUserId(data.UserId)
        storeUserToken(data.UserToken)
        storeMediaLink(data.BasicUrls.MediaUrl)
        storeBasicUrl(data.BasicUrls.SiteUrl)


        storeFirstName(data.FirstName)
        storeLastName(data.LastName)
        storeMobileNo(data.MobileNumber)
        storePhoneCode(data.PhoneCode)
        storeEmail(data.EmailAddress)
        storeProfileUrl(data.Media)
        storeDefultAddressId(data.DefaultAddressId)
    }

    fun setPrefrence(responseLogin: DataSocial) {
        storeUserLoginStatus(true)
        storeUserId(responseLogin.UserId)
        storeUserToken(responseLogin.UserToken)
        responseLogin.BasicUrls.MediaUrl?.let { storeMediaLink(it) }
        storeBasicUrl(responseLogin.BasicUrls.SiteUrl)


        storeFirstName(responseLogin.FirstName)
        storeLastName(responseLogin.LastName)
        storeMobileNo(responseLogin.MobileNumber)
        storePhoneCode(responseLogin.PhoneCode)
        storeEmail(responseLogin.EmailAddress)
        storeProfileUrl(responseLogin.Media)
        storeDefultAddressId(responseLogin.DefaultAddressId)
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

        Log.e("OTP Received", otp)

        pinView.setText(otp)

        /*userotp =
            edt1.text.toString() + edt2.text.toString() + edt3.text.toString() + edt4.text.toString()*/

        userotp = pinView.text.toString()

        lastTimeSel = SystemClock.elapsedRealtime() - mLastClickTime;
        if (lastTimeSel > 0 && lastTimeSel < 500) {
            return
        }
        mLastClickTime = SystemClock.elapsedRealtime()
        CallApiVerifyOTP()
    }

    override fun onOTPTimeOut() {

    }

    private fun startSMSListener() {
        val client = SmsRetriever.getClient(this)
        val task = client.startSmsRetriever()
        task.addOnSuccessListener {
            // Toast.makeText(this, "SMS Retriever starts", Toast.LENGTH_LONG).show();
        }
        task.addOnFailureListener {
            //Toast.makeText(this, "Error", Toast.LENGTH_LONG).show();
        }
    }

    private fun CallApiBasicUrl() {
        if (isInternetAvailable(this)) {
            mprogressbar.showProgressBar()
            val retrofit = WebService.getRetrofit(this@LoginActivity).create(ApiClient::class.java)
            retrofit.GetBasicUrls()
                .enqueue(object : Callback<ResponseBasicUrl> {
                    override fun onFailure(call: Call<ResponseBasicUrl>, t: Throwable) {
                        mprogressbar.hideProgressBar()
                    }

                    override fun onResponse(
                        call: Call<ResponseBasicUrl>,
                        response: Response<ResponseBasicUrl>?
                    ) {
                        mprogressbar.hideProgressBar()
                        if (response != null) {
                            if (response.isSuccessful) {
                                val responseurl = response.body()

                                if (responseurl != null) {
                                    if (responseurl.status == API_SUCESS) {
                                        responseurl.data.MediaUrl?.let { storeMediaLink(it) }
                                        storeBasicUrl(responseurl.data.SiteUrl)
                                        storePaymentUrl(responseurl.data.PaymentUrl)
                                        storeSuccessUrl(responseurl.data.PaymentSuccessUrl)
                                        storeCancelUrl(responseurl.data.PaymentCancelUrl)
                                        storeTerms(responseurl.data.TermsCondition)
                                        storePrivacy(responseurl.data.PrivacyPolicy)
                                        storeAboutUs(responseurl.data.AboutUs)
                                        storeContactUs(responseurl.data.ContactUs)
                                        storeRefundUrl(responseurl.data.CancellationPolicy)
                                    }
//                                    else {
//                                        showToast(getString(R.string.err_mob_invalid), Const.ALERT)
//                                    }
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

    override fun onPause() {
        super.onPause()
        App.isDialogOpen = false

    }

    @SuppressLint("SoonBlockedPrivateApi")
    fun setCursorColor(view: EditText, @ColorInt color: Int) {
        try {
            // Get the cursor resource id
            var field: Field = TextView::class.java.getDeclaredField("mCursorDrawableRes")
            field.setAccessible(true)
            val drawableResId: Int = field.getInt(view)

            // Get the editor
            field = TextView::class.java.getDeclaredField("mEditor")
            field.setAccessible(true)
            val editor: Any = field.get(view)
            Log.e("length", "line no 825")
            // Get the drawable and set a color filter
            val drawable: Drawable = ContextCompat.getDrawable(view.context, drawableResId)!!
            drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN)
            val drawables: Array<Drawable> = arrayOf<Drawable>(drawable, drawable)
            Log.e("length", "line no 830")
            // Set the drawables
            field = editor.javaClass.getDeclaredField("mCursorDrawable")
            field.setAccessible(true)
            field.set(editor, drawables)
        } catch (ignored: Exception) {
            Log.e("length", "line no 837")
            ignored.printStackTrace()
        }
    }
}

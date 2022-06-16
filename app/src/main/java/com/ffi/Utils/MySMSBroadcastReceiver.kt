package com.ffi.Utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status

class MySMSBroadcastReceiver : BroadcastReceiver() {

    private var otpReceiver: OTPReceiveListener? = null

    fun initOTPListener(receiver: OTPReceiveListener) {
        this.otpReceiver = receiver
    }

    override fun onReceive(context: Context, intent: Intent) {
        Log.e("sms","onRecieve")
        if (SmsRetriever.SMS_RETRIEVED_ACTION == intent.action) {
            Log.e("sms","SmsRetriever line 22")
            val extras = intent.extras
            val status = extras!!.get(SmsRetriever.EXTRA_STATUS) as Status
            Log.e("sms","SmsRetriever line 25 sttus code" + status.statusCode)
            when (status.statusCode) {
                CommonStatusCodes.SUCCESS -> {
                    // Get SMS message contents
                    var otp: String = extras.get(SmsRetriever.EXTRA_SMS_MESSAGE) as String
                    // Extract one-time code from the message and complete verification
                    // by sending the code back to your server for SMS authenticity.
                    // But here we are just passing it to MainActivity
                    Log.e("sms",otp)
                    if (otpReceiver != null) {
                        val pos = otp.indexOf("OTP")
                        otp = otp.substring(pos + 7, pos + 11)
                        Log.e("sms",otp + " 4 key")
                        //"<#> Your ExampleApp code is: ", "").split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
                        otpReceiver?.onOTPReceived(otp)
                    }
                }

                CommonStatusCodes.TIMEOUT -> {
                // Waiting for SMS timed out (5 minutes)
                // Handle the error ...
                    otpReceiver?.onOTPTimeOut()
            }
            }
        }
    }

    interface OTPReceiveListener {

        fun onOTPReceived(otp: String)

        fun onOTPTimeOut()
    }
}

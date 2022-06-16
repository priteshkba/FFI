package com.ffi.payment

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.ffi.HomeActivity
import com.ffi.R
import com.ffi.Utils.*
import com.ffi.my_orders.MyOrderFragment
import kotlinx.android.synthetic.main.activity_payment.*
import kotlinx.android.synthetic.main.custom_toolbar.*

class PaymentActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var mprogressbar: ProgressBarHandler
    lateinit var fragment: MyOrderFragment
    var iscredit = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        fragment = MyOrderFragment().getInstance(false, true)
        mprogressbar = ProgressBarHandler(this)

        init()
    }

    private fun init() {
        val bundle = intent.extras
        if (bundle != null) {
            if (bundle.containsKey("credit")) {
                iscredit = bundle.getBoolean("credit")
            }
        }
        ivBack.visibility = View.VISIBLE
        ivEdit.visibility = View.GONE
        txt_toolbar.text = getString(R.string.title_payment)

        setListener()
        setUpWebView()
    }

    private fun setUpWebView() {
//        mprogressbar.showProgressBar()
        webview.getSettings().setLoadWithOverviewMode(true)
        webview.getSettings().setUseWideViewPort(true)

        //To upload data from
        webview.getSettings().setSaveFormData(true)
        webview.getSettings().setBuiltInZoomControls(true)
        webview.getSettings().setDisplayZoomControls(false)
        webview.getSettings().setPluginState(WebSettings.PluginState.ON)
        webview.getSettings().setJavaScriptEnabled(true)
        webview.getSettings().setUseWideViewPort(true)
        webview.setWebViewClient(myWebClient())
        webview.loadUrl(getPaymentUrl())
    }

    inner class myWebClient : WebViewClient() {
        override fun shouldOverrideUrlLoading(
            view: WebView,
            request: WebResourceRequest
        ): Boolean {
            view.loadUrl(view.url!!)
            return true
        }

        override fun onPageStarted(
            view: WebView,
            url: String,
            favicon: Bitmap?
        ) {
            super.onPageStarted(view, url, favicon)
            Log.e("FindError", "pageStartUrl==$url")
        }

        override fun onLoadResource(view: WebView, url: String) {
            super.onLoadResource(view, url)
            if (url == getSuccessUrl()) {
                PaymentDialog(getString(R.string.msg_payment_success), true)
            } else if (url == getCancelUrl()) {
                PaymentDialog(getString(R.string.msg_payment_cancel), false)
            }
            Log.e("FindError", "pageLoadResourceUrl==$url")
        }

        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)
            Log.e("FindError", "pageFinishUrl==$url")
            mprogressbar.hideProgressBar()
        }
    }

    private fun setListener() {
        ivBack.setOnClickListener(this)
        btn_payment.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBack -> {
                finish()
            }
            R.id.btn_payment -> {
                // CallApiPaymnet()
            }
        }
    }


    private fun PaymentDialog(msg: String, isSuccess: Boolean) {
        if (!this@PaymentActivity.isFinishing) {
            val dialog = AlertDialog.Builder(this)
                .setTitle(getString(R.string.app_name))
                .setMessage(msg)
                .setPositiveButton("OK",
                    DialogInterface.OnClickListener { dialog, which ->
                        if (!this@PaymentActivity.isFinishing) {
                            if (isSuccess) {
                                if (iscredit) {
                                    finish()
                                } else {
                                    val intent = Intent(this, HomeActivity::class.java)
                                    intent.putExtra(Const.ORDER_ID, "1")
                                    startActivity(intent)
                                    finishAffinity()
                                }
                            } else {
                                finish()
                            }
                        }
                    }).setCancelable(false)
            try {
                if (dialog != null && !isFinishing)
                    dialog.show()
            } catch (e: WindowManager.BadTokenException) {
                e.printStackTrace()
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        webview.stopLoading()
    }

}

package com.ffi

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.ffi.Utils.Const
import com.ffi.Utils.ProgressBarHandler
import kotlinx.android.synthetic.main.activity_payment.*
import kotlinx.android.synthetic.main.custom_toolbar.*

class WebviewActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var mprogressbar: ProgressBarHandler
    var url = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview)

        mprogressbar = ProgressBarHandler(this)

        init()
    }

    fun init() {
        val bundle = intent.extras
        if (bundle != null) {
            if (bundle.containsKey(Const.TITLE)) {
                txt_toolbar.setText(bundle.getString(Const.TITLE))
            }

            if (bundle.containsKey((Const.URL))) {
                url = bundle.getString(Const.URL).toString()
            }
        }
        ivBack.visibility = View.VISIBLE
        ivEdit.visibility = View.GONE

        ivBack.setOnClickListener(this)

        setUpWebView()
    }


    private fun setUpWebView() {
        mprogressbar.showProgressBar()
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
        webview.loadUrl(url)
        Log.e("res", "webview url" + webview.url)
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

            Log.e("FindError", "pageLoadResourceUrl==$url")
        }

        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)
            Log.e("FindError", "pageFinishUrl==$url")
            mprogressbar.hideProgressBar()
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBack -> {
                onBackPressed()
            }
        }
    }

}
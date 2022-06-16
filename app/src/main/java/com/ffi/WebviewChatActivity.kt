package com.ffi

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.ffi.Utils.*
import com.livechatinc.inappchat.ChatWindowConfiguration
import com.livechatinc.inappchat.ChatWindowErrorType
import com.livechatinc.inappchat.ChatWindowFragment
import com.livechatinc.inappchat.ChatWindowView
import com.livechatinc.inappchat.models.NewMessageModel
import kotlinx.android.synthetic.main.custom_toolbar.*


class WebviewChatActivity : AppCompatActivity(), View.OnClickListener,
    ChatWindowView.ChatWindowEventsListener {

    lateinit var mprogressbar: ProgressBarHandler
    var url = ""
    var customText = ""
//    var mChatWindowView: ChatWindowView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chatview)

        mprogressbar = ProgressBarHandler(this)

        init()
        Log.e("tagLiveChat", "WebviewChatActivity onCreate called ")
        startEmbeddedChat()
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

            if (bundle.containsKey((Const.CUSTOM_TEXT))) {
                customText = bundle.getString(Const.CUSTOM_TEXT).toString()
            }

        }
        ivBack.visibility = View.VISIBLE
        ivEdit.visibility = View.GONE

        ivBack.setOnClickListener(this)

        setUpChatView()
    }

    private fun setUpChatView() {
        Log.e("tagLiveChat", "setUpChatView onCreate called ")
//         mChatWindowView = ChatWindowView(this@WebviewChatActivity)

    }

    fun startEmbeddedChat() {
//        Log.e("tagLiveChat", "setUpChatView onCreate mChatWindowView?.isInitialized " + mChatWindowView?.isInitialized)
//        if (!mChatWindowView?.isInitialized!!) {
//
//        }


        var mEmailMobile = if(getEmail() != null && !TextUtils.isEmpty(getEmail())){
            getEmail()
        }
        else {
            if(getMobileNo() != null && !TextUtils.isEmpty(getMobileNo())){
                getMobileNo()
            }
            else {
                ""
            }
        }

        Log.e("tagLiveChat", "setUpChatView Const.liveChat_licence " + Const.liveChat_licence)
        Log.e("tagLiveChat", "setUpChatView Const.liveChat_group " + Const.liveChat_group)

        App.mChatWindowConfiguration = ChatWindowConfiguration(
            Const.liveChat_licence,
            Const.liveChat_group,
            getFullName(),
            mEmailMobile,
            null
        )

//            Log.e("tagLiveChat", "mChatWindowView?.setUpWindow(App.mChatWindowConfiguration) ")
//            mChatWindowView?.setUpWindow(App.mChatWindowConfiguration)
//            mChatWindowView?.setUpListener(this@WebviewChatActivity)
//            mChatWindowView?.initialize()
//
//            mChatWindowView?.showChatWindow()

        var mFragment = ChatWindowFragment.newInstance(Const.liveChat_licence, Const.liveChat_group)

        fragmentManager
            .beginTransaction()
            .replace(R.id.embedded_chat_window, mFragment)
            .addToBackStack("chat_fragment")
            .commit();
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBack -> {
                onBackPressed()
            }
        }
    }

    override fun onChatWindowVisibilityChanged(visible: Boolean) {
        Log.e("tagLiveChat", "onChatWindowVisibilityChanged visible " + visible)
    }

    override fun onNewMessage(message: NewMessageModel?, windowVisible: Boolean) {
        Log.e("tagLiveChat", "onNewMessage windowVisible " + windowVisible + " && message " + message)
    }

    override fun onStartFilePickerActivity(intent: Intent?, requestCode: Int) {
        Log.e("tagLiveChat", "onChatWindowVisibilityChanged visible requestCode " + requestCode)
    }

    override fun onError(
        errorType: ChatWindowErrorType?,
        errorCode: Int,
        errorDescription: String?
    ): Boolean {
        Log.e("tagLiveChat", "onError errorType " + errorType)
        Log.e("tagLiveChat", "onError errorCode " + errorCode)
        Log.e("tagLiveChat", "onError errorDescription " + errorDescription)
        return false
    }

    override fun handleUri(uri: Uri?): Boolean {
        Log.e("tagLiveChat", "handleUri " + uri)
        return false
    }

    override fun onBackPressed() {
        finish()
    }

}
package com.ffi.myaccount

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.ffi.*
import com.ffi.Utils.*
import com.ffi.edit_profile.EditProfileFragment
import com.ffi.home.ResponseHome
import com.ffi.home.ViewPagerBannerAdapter
import com.ffi.login.LoginActivity
import com.ffi.membership.MembershipFragment
import com.ffi.my_orders.MyOrderFragment
import com.ffi.payment.PaymentActivity
import com.ffi.sharedcatalog.SharedCatalogFragment
import com.ffi.wallet.*
import com.ffi.wishlist.WishlistFragment
import com.ffi.api.ApiClient
import com.ffi.api.WebService
import com.livechatinc.inappchat.ChatWindowConfiguration
import com.livechatinc.inappchat.ChatWindowErrorType
import com.livechatinc.inappchat.ChatWindowView
import com.livechatinc.inappchat.models.NewMessageModel
import kotlinx.android.synthetic.main.custom_toolbar.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_my_account.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MyAccountFragment : Fragment(), View.OnClickListener,
    ChatWindowView.ChatWindowEventsListener {

    var homeActivity: HomeActivity? = null
    lateinit var mprogressbar: ProgressBarHandler
    var strWallet = ""

    lateinit var dialog: Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeActivity = activity as HomeActivity
        homeActivity?.selectLastMenu()
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_account, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    public fun setData() {
        try {
            if (getFirstName().isEmpty() && getLastName().isEmpty()) {
                tvUsername.visibility = View.GONE
            } else {
                tvUsername.text = getFirstName() + " " + getLastName()
            }

            if (getIsSocialLogin()) {
                tvMobno.text = getEmail()
            } else {
                if (getMobileNo().isEmpty()) {
                    tvMobno.visibility = View.GONE
                } else {
                    tvMobno.text = "(" + getPhoneCode() + ") - " + getMobileNo()
                }
            }

            if (getUserId().isEmpty()) {
                tvlogout?.text = getString(R.string.lbl_login)
            } else {
                tvlogout?.text = getString(R.string.menu_logout)
            }

            Log.e(
                "tagMediaLoad",
                "getMediaLink() + getProfileUrl() " + (getMediaLink() + getProfileUrl())
            )
            GlideApp.with(requireActivity()).clear(cv_profile)
            GlideApp.with(requireActivity()).clear(ivcoverpic)

            GlideApp.with(requireActivity())
                .load(getMediaLink() + getProfileUrl())
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .placeholder(R.drawable.profileplaceholder)
                .into(cv_profile)

            GlideApp.with(requireActivity())
                .load(getMediaLink() + getProfileUrl())
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .placeholder(R.drawable.profileplaceholder)
                .into(ivcoverpic)
        } catch (e: Exception) {
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mprogressbar = ProgressBarHandler(requireActivity())

        init()

        setData()
        setListner()

        if (!getUserId().isEmpty()) {
            CallApiGetWallet()
            callApiHomeData(false)
        }


    }

    private fun callApiHomeData(isLoader: Boolean) {

        if (isInternetAvailable(requireActivity())) {
            if (isLoader && App.isShowProgressbar) {
                mprogressbar.showProgressBar()
            }
            val retrofit = WebService.getRetrofit(requireActivity()).create(ApiClient::class.java)
            retrofit.GetHomeData()
                .enqueue(object : Callback<ResponseHome> {
                    override fun onFailure(call: Call<ResponseHome>, t: Throwable) {
                        mprogressbar.hideProgressBar()
                        swf_home?.isRefreshing = false
                        Log.e("home", "Onfailer " + t.message)
                    }

                    override fun onResponse(
                        call: Call<ResponseHome>,
                        response: Response<ResponseHome>?
                    ) {
                        mprogressbar.hideProgressBar()
                        swf_home?.isRefreshing = false
                        if (response != null) {
                            if (response.isSuccessful) {
                                val responseLogin = response.body()

                                if (responseLogin != null) {
                                    if (responseLogin.status == API_SUCESS) {
                                        tvActiveMembership?.text =
                                            "Active Membership - " + responseLogin.data.GetCurrentMembership
                                    }
                                } else {
                                    //showToast(getString(R.string.err_mob_invalid))
                                }
                            }
                        } else {
                            requireActivity().showToast(
                                getString(R.string.msg_common_error),
                                Const.ALERT
                            )

                        }
                    }
                })
        } else {
            NetworkErrorDialog(requireActivity())
            swf_home?.isRefreshing = false
        }
    }

    private fun init() {
        txt_toolbar.text = getString(R.string.my_account)
        ivEdit.visibility = View.VISIBLE
        ivBack.visibility = View.GONE
    }

    private fun setListner() {
        rl_shared.setOnClickListener(this)
        rl_wishlist.setOnClickListener(this)
        rl_logout.setOnClickListener(this)
        rl_my_order.setOnClickListener(this)
        rl_wallet.setOnClickListener(this)
        tvAddCredit.setOnClickListener(this)
        rl_membership.setOnClickListener(this)
        rl_whtsapp.setOnClickListener(this)
        rlTerms.setOnClickListener(this)
        rl_privacy.setOnClickListener(this)
        rl_refund.setOnClickListener(this)
        rlAbout.setOnClickListener(this)
        rlContactUs.setOnClickListener(this)
        ivEdit.setOnClickListener(this)
        rl_my_wallet.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.rl_whtsapp -> {
                requireActivity().whatAppToFFIBussinessNumber(requireContext())
            }
            R.id.rl_membership -> {
                loadMemberShip()
            }
            R.id.tvAddCredit -> {
                if (getUserId().isEmpty()) {
                    /* val intent = Intent(activity, LoginActivity::class.java)
                     activity?.startActivity(intent)
                     App.isRedirect = true*/
                    activity?.showLoginMsg()
                } else {
                    DialogWallet()
                }
            }
            R.id.rl_wallet -> {
                if (getUserId().isEmpty()) {
                    /* val intent = Intent(activity, LoginActivity::class.java)
                     activity?.startActivity(intent)
                     App.isRedirect = true*/
                    activity?.showLoginMsg()
                } else {
                    DialogWallet()
                }
            }
            R.id.rl_my_wallet -> {
                if (getUserId().isEmpty()) {
                    activity?.showLoginMsg()
                } else {
                    val intent = Intent(
                        requireActivity(),
                        MyWalletActivity::class.java
                    ).putExtra("strWallet", strWallet)
                    startActivity(intent)
                }

            }
            R.id.rlTerms -> {
                val intent = Intent(requireActivity(), WebviewActivity::class.java)
                intent.putExtra(Const.URL, getTerms())
                intent.putExtra(Const.TITLE, getString(R.string.menu_terms_condition))
                startActivity(intent)
            }
            R.id.rl_privacy -> {
                val intent = Intent(requireActivity(), WebviewActivity::class.java)
                intent.putExtra(Const.URL, getPrivacy())
                intent.putExtra(Const.TITLE, getString(R.string.lbl_privacy_policy))
                startActivity(intent)
            }
            R.id.rl_refund -> {
                val intent = Intent(requireActivity(), WebviewActivity::class.java)
                intent.putExtra(Const.URL, getRefund())
                intent.putExtra(Const.TITLE, getString(R.string.lbl_refund_policy))
                startActivity(intent)
            }
            R.id.rlAbout -> {
                val intent = Intent(requireActivity(), WebviewActivity::class.java)
                intent.putExtra(Const.URL, getAboutUs())
                intent.putExtra(Const.TITLE, getString(R.string.menu_about_us))
                startActivity(intent)
            }
            R.id.rlContactUs -> {
                val intent = Intent(requireActivity(), WebviewActivity::class.java)
                intent.putExtra(Const.URL, getContactUs())
                intent.putExtra(Const.TITLE, getString(R.string.lbl_contact_us))
                startActivity(intent)
            }
            R.id.rl_wishlist -> {
                if (getUserId().isEmpty()) {
                    /*val intent = Intent(activity, LoginActivity::class.java)
                    activity?.startActivity(intent)
                    App.isRedirect = true*/
                    activity?.showLoginMsg()
                } else {
                    loadWishList()
                }
            }
            R.id.rl_my_order -> {
                if (getUserId().isEmpty()) {
                    /* val intent = Intent(activity, LoginActivity::class.java)
                     activity?.startActivity(intent)
                     App.isRedirect = true*/
                    activity?.showLoginMsg()
                } else {
                    loadMyOrder()
                }
            }
            R.id.rl_logout -> {
                if (getUserId().isEmpty()) {
                    storeSKipStatus(false)
                    val intent =
                        Intent(requireActivity(), LoginActivity::class.java)
                    startActivity(intent)
                    requireActivity().finishAffinity()

                } else {
                    LogoutDialog()
                }


            }
            R.id.ivEdit -> {
                if (getUserId().isEmpty()) {
                    /*  val intent = Intent(activity, LoginActivity::class.java)
                    activity?.startActivity(intent)
                    App.isRedirect = true*/
                    activity?.showLoginMsg()
                } else {
                    loadEditProfile()
                }
            }
            R.id.rl_shared -> {
                if (getUserId().isEmpty()) {
                    /*  val intent = Intent(activity, LoginActivity::class.java)
                    activity?.startActivity(intent)
                    App.isRedirect = true*/
                    activity?.showLoginMsg()
                } else {
                    loadSharedCatalog()
                }
            }
        }
    }


//    fun startFullScreenChat() {
//
//        var fullScreenChatWindow: ChatWindowView? = null
//        var mEmailMobile = if(getEmail() != null && !TextUtils.isEmpty(getEmail())){
//            getEmail()
//        }
//        else {
//            if(getMobileNo() != null && !TextUtils.isEmpty(getMobileNo())){
//                getMobileNo()
//            }
//            else {
//                ""
//            }
//        }
//
//        App.mChatWindowConfiguration = ChatWindowConfiguration(
//            Const.liveChat_licence,
//            Const.liveChat_group,
//            getFullName(),
//            mEmailMobile,
//            null
//        )
//
//        if (fullScreenChatWindow == null) {
//            fullScreenChatWindow = ChatWindowView.createAndAttachChatWindowInstance(requireActivity())
//            fullScreenChatWindow.setUpWindow(App.mChatWindowConfiguration)
//            fullScreenChatWindow.setUpListener(this@MyAccountFragment)
//            fullScreenChatWindow.initialize()
//        }
//        fullScreenChatWindow?.showChatWindow()
//    }

    private fun loadMemberShip() {
        val fragment = MembershipFragment()
        homeActivity?.deselectAllMenu()
        homeActivity?.supportFragmentManager
            ?.beginTransaction()
            ?.add(R.id.cm_fragmentContainer, fragment, Const.TAG_FRAG_MEMBERSHIP_PLAN)
            ?.addToBackStack(null)
            ?.commit()
    }

    private fun loadSharedCatalog() {
        val fragment = SharedCatalogFragment()
        homeActivity?.deselectAllMenu()
        homeActivity?.supportFragmentManager
            ?.beginTransaction()
            ?.add(R.id.cm_fragmentContainer, fragment, Const.TAG_FRAG_SHARED_CATALOG)
            ?.addToBackStack(null)
            ?.commit()
    }

    private fun loadEditProfile() {
        val fragment = EditProfileFragment().getInstance(true, false)
        homeActivity?.deselectAllMenu()
        homeActivity?.supportFragmentManager
            ?.beginTransaction()
            ?.add(R.id.cm_fragmentContainer, fragment, Const.TAG_FRAG_EDIT_PROFILE)
            ?.addToBackStack(null)
            ?.commit()
    }

    private fun LogoutDialog() {
        AlertDialog.Builder(context)
            .setTitle(getString(R.string.app_name))
            .setMessage(getString(R.string.msg_confirmation_logout))
            .setPositiveButton("Yes",
                DialogInterface.OnClickListener { dialog, which ->

                    if (getUserId().isEmpty()) {
                        val intent =
                            Intent(requireActivity(), LoginActivity::class.java)
                        startActivity(intent)
                        requireActivity().finishAffinity()

                    } else {
                        CallApiLogout()
                    }

                })
            .setNegativeButton("No", null)
            .setCancelable(false)
            .show()
    }

    private fun loadMyOrder() {
        val fragment = MyOrderFragment().getInstance(true, false)
        homeActivity?.deselectAllMenu()
        //homeActivity?.replaceFragment(fragment, Const.TAG_FRAG_ORDERLIST)
        homeActivity?.supportFragmentManager
            ?.beginTransaction()
            ?.add(R.id.cm_fragmentContainer, fragment, Const.TAG_FRAG_ORDERLIST)
            ?.addToBackStack(null)
            ?.commit()
    }

    private fun loadWishList() {
        val fragment = WishlistFragment()//.newInstance(refProv)
        homeActivity?.deselectAllMenu()
        homeActivity?.supportFragmentManager
            ?.beginTransaction()
            ?.add(R.id.cm_fragmentContainer, fragment, Const.TAG_FRAG_WISHLIST)
            ?.addToBackStack(null)
            ?.commit()
    }

    private fun CallApiLogout() {
        if (isInternetAvailable(requireActivity())) {

            mprogressbar.showProgressBar()

            val param = ParamLogout().apply {
                DeviceToken = getFcmToken()
            }

            val retrofit = WebService.getRetrofit(requireActivity()).create(ApiClient::class.java)
            retrofit.Logout(param)
                .enqueue(object : Callback<ResponseLogout> {
                    override fun onFailure(call: Call<ResponseLogout>, t: Throwable) {
                        mprogressbar.hideProgressBar()
                    }

                    override fun onResponse(
                        call: Call<ResponseLogout>,
                        response: Response<ResponseLogout>?
                    ) {
                        mprogressbar.hideProgressBar()
                        if (response != null) {
                            if (response.isSuccessful) {
                                val ResponseLogout = response.body()

                                if (ResponseLogout != null) {
                                    if (ResponseLogout.status == API_SUCESS) {
                                        removeUserData()
                                        storeIsSocialLogin(false)
                                        val intent =
                                            Intent(requireActivity(), LoginActivity::class.java)
                                        startActivity(intent)
                                        requireActivity().finishAffinity()
                                    } else {
                                        //               requireActivity().showToast(getString(R.string.err_mob_invalid))

                                    }
                                }
                            } else {
                                requireActivity().showToast(
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

    private fun CallApiGetWallet() {

        if (isInternetAvailable(requireActivity())) {
            mprogressbar.showProgressBar()
            val param = ParamWallet().apply {
                userId = getUserId()
            }

            val retrofit = WebService.getRetrofit(requireActivity()).create(ApiClient::class.java)
            retrofit.GetWallet(param)
                .enqueue(object : Callback<ResponseWallet> {
                    override fun onFailure(call: Call<ResponseWallet>, t: Throwable) {
                        mprogressbar.hideProgressBar()
                    }

                    override fun onResponse(
                        call: Call<ResponseWallet>,
                        response: Response<ResponseWallet>?
                    ) {
                        mprogressbar.hideProgressBar()
                        if (response != null) {
                            if (response.isSuccessful) {
                                val responsewallet = response.body()
                                if (responsewallet != null) {
                                    if (responsewallet.status == API_SUCESS) {
                                        if (isAdded && activity != null) {
                                            strWallet = responsewallet.walletAmount.toString()
                                            tvWallet.text =
                                                "CREDIT - " + getString(R.string.currency) + " " + strWallet
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

    fun DialogWallet() {
        dialog = Dialog(requireActivity())
        val window = dialog.window
        window!!.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        window.setGravity(Gravity.CENTER)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setContentView(R.layout.custom_add_amount)

        val edtamnt = dialog.findViewById<EditText>(R.id.edtamnt)
        val cad_btnOk = dialog.findViewById<Button>(R.id.cad_btnOk)
        val tvcreedit = dialog.findViewById<TextView>(R.id.tvcreedit)
        val rd1 = dialog.findViewById<RadioButton>(R.id.rd1)
        val rd2 = dialog.findViewById<RadioButton>(R.id.rd2)
        val rd3 = dialog.findViewById<RadioButton>(R.id.rd3)

        tvcreedit.text = getString(R.string.currency) + " " + strWallet

        rd1.setOnCheckedChangeListener { compoundButton: CompoundButton, b: Boolean ->
            if (b) {
                edtamnt.setText("5000")
            }
        }

        rd2.setOnCheckedChangeListener { compoundButton: CompoundButton, b: Boolean ->
            if (b) {
                edtamnt.setText("10000")
            }
        }

        rd3.setOnCheckedChangeListener { compoundButton: CompoundButton, b: Boolean ->
            if (b) {
                edtamnt.setText("20000")
            }
        }


        edtamnt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                Log.e("length", s?.length.toString())
                if (s?.toString()?.trim()?.length == 1) {
                    rd1.isChecked = false
                    rd2.isChecked = false
                    rd3.isChecked = false
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

        })



        cad_btnOk.setOnClickListener {
            val stramnt = edtamnt.text.toString()
            if (stramnt.length > 0) {
                CallApiAddWAllet(stramnt)
            } else {
                activity?.showToast(getString(R.string.err_add_amount), Const.ALERT)
            }

        }

        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)
        dialog.show()
    }

    fun CallApiAddWAllet(stramnt: String) {
        if (isInternetAvailable(requireActivity())) {
            mprogressbar.showProgressBar()
            val param = ParamAddWallet().apply {
                userId = getUserId()
                amount = stramnt
            }

            val retrofit = WebService.getRetrofit(requireActivity()).create(ApiClient::class.java)
            retrofit.AddWallet(param)
                .enqueue(object : Callback<ResponseAddWallet> {
                    override fun onFailure(call: Call<ResponseAddWallet>, t: Throwable) {
                        mprogressbar.hideProgressBar()
                    }

                    override fun onResponse(
                        call: Call<ResponseAddWallet>,
                        response: Response<ResponseAddWallet>?
                    ) {
                        mprogressbar.hideProgressBar()
                        if (response != null) {
                            if (response.isSuccessful) {
                                val responsewallet = response.body()
                                if (responsewallet != null) {
                                    if (responsewallet.status == API_SUCESS) {
                                        if (isAdded) {

                                            responsewallet.paymentProcessUrl?.let {
                                                storePaymentUrl(
                                                    it
                                                )
                                            }
                                            responsewallet.paymentSuccessUrl?.let {
                                                storeSuccessUrl(
                                                    it
                                                )
                                            }
                                            responsewallet.paymentCancelUrl?.let { storeCancelUrl(it) }
                                            val intent =
                                                Intent(
                                                    requireActivity(),
                                                    PaymentActivity::class.java
                                                )
                                            intent.putExtra("credit", true)
                                            startActivity(intent)
                                            /* tvWallet.text = "Wallet Balance - " +
                                                     getString(R.string.currency) + " " + responsewallet.walletAmount*/
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


    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        Log.e("res", isVisibleToUser.toString())
    }

    override fun onResume() {
        super.onResume()
        if (this::dialog.isInitialized && dialog != null) {
            dialog.cancel()
        }

        if (!getUserId().isEmpty()) {
            CallApiGetWallet()
            callApiHomeData(false)
        }

    }

    override fun onChatWindowVisibilityChanged(visible: Boolean) {
        Log.e("tagLiveChat", "onChatWindowVisibilityChanged visible " + visible)
    }

    override fun onNewMessage(message: NewMessageModel?, windowVisible: Boolean) {
        Log.e(
            "tagLiveChat",
            "onNewMessage windowVisible " + windowVisible + " && message " + message
        )
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


}

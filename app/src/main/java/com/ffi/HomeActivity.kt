package com.ffi

import android.Manifest
import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.ffi.Utils.*
import com.ffi.allcataloglist.CollctionListFragment
import com.ffi.api.ApiClient
import com.ffi.api.WebService
import com.ffi.cart.CartFragment
import com.ffi.catalog.CollectionFragment
import com.ffi.edit_profile.EditProfileFragment
import com.ffi.edit_profile.NamesFragment
import com.ffi.home.HomeFragment
import com.ffi.home.ResponseBasicUrl
import com.ffi.my_orders.MyOrderFragment
import com.ffi.myaccount.MyAccountFragment
import com.ffi.productdetail.ProductDetailFragment
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallState
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import kotlinx.android.synthetic.main.activity_home.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class HomeActivity : AppCompatActivity() {

    lateinit var mprogressbar: ProgressBarHandler
    private var lastSelectedIndex: Int = 0
    lateinit var menu: Menu
    private var orderid = ""

    lateinit var badgetext: TextView
    var isNotificatom = ""
    var bundle: Bundle? = null
    var canDoubleBack: Boolean = true

    lateinit var mAppUpdateManager: AppUpdateManager
    private val RC_APP_UPDATE = 11
    var isupdate = false
    internal var fragment: Fragment? = null
    private var fragmentManager: androidx.fragment.app.FragmentManager? = null

    fun StartUpdate() {

        isupdate = true
        mAppUpdateManager = AppUpdateManagerFactory.create(this)
        mAppUpdateManager.registerListener(installStateUpdatedListener)

        mAppUpdateManager.getAppUpdateInfo().addOnSuccessListener { appUpdateInfo: AppUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE /*AppUpdateType.IMMEDIATE*/)
            ) {
                try {
                    mAppUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo,
                        AppUpdateType.FLEXIBLE /*AppUpdateType.IMMEDIATE*/,
                        this,
                        RC_APP_UPDATE
                    )
                } catch (e: SendIntentException) {
                    e.printStackTrace()
                }
            } else if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                popupSnackbarForCompleteUpdate()
            } else {
                Log.e("TAG", "checkForAppUpdateAvailability: something else")
            }
        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        fabSupport.setOnClickListener {
//           openLiveChatWithProfile(mprogressbar)

            whatAppToFFIBussinessNumber(this)


//        val intent = Intent(activity, WebviewChatActivity::class.java)
//        intent.putExtra(Const.URL, "https://tawk.to/chat/5fa16c2be019ee7748f05f73/default")
//        intent.putExtra(Const.TITLE, getString(R.string.strLiveSupport))
//        startActivity(intent)
        }



        mprogressbar = ProgressBarHandler(this)
        CallApiBasicUrl()
        setBadgeCount()

        bundle = intent?.extras
        if (bundle != null) {


            if (bundle!!.containsKey(Const.FROM_LOGIN)) {
                replaceFragment(App.fragmenthome, Const.TAG_FRAG_HOME)
                val fromlogin = bundle!!.getBoolean(Const.FROM_LOGIN)
                if (!fromlogin) {
                    CallApiBasicUrl()
                }
            } else if (bundle!!.containsKey(Const.ORDER_ID)) {
                orderid = bundle!!.getString(Const.ORDER_ID).toString()
                if (!orderid.isEmpty()) {
                    storeCartItem(0)
                    App.isOrderDone = true
                    badgetext.visibility = View.GONE
                    loadOrderDetails()
                }
            } else if (bundle!!.containsKey(Const.TAG_FRAG_EDIT_PROFILE)) {
                val fragment = NamesFragment()
                deselectAllMenu()
                replaceFragment(fragment, Const.TAG_FRAG_EDIT_PROFILE)
                canDoubleBack = true
            } else if (bundle!!.containsKey(Const.NOTI_USER_REGISTER)) {
                loadEditProfile()
            } else if (bundle!!.containsKey(Const.NOTI_ADD_WALET)) {
                setAcountScreen()
            } else if (bundle?.containsKey(Const.NOTI_NEW_PRODUCT)!!) {
                val id = bundle?.getString(Const.NOTI_NEW_PRODUCT)
                id?.let { setProductDetail(it) }
            } else if (bundle?.containsKey(Const.NOTI_NEW_CATALOGUE)!!) {
                val id = bundle?.getString(Const.NOTI_NEW_CATALOGUE)
                val title = bundle?.getString(Const.TITLE)
                id?.let { title?.let { it1 -> loadCollectionItems(it, it1) } }
            } else if (bundle?.containsKey(Const.NOTI_DEFUALT)!!) {
                val fragment = HomeFragment()
                supportFragmentManager
                    ?.beginTransaction()
                    ?.add(R.id.cm_fragmentContainer, fragment, fragment::class.java.name)
                    ?.addToBackStack(null)
                    ?.commit()
            } else {
                replaceFragment(App.fragmenthome, Const.TAG_FRAG_HOME)
                CallApiBasicUrl()
            }
        } else {
            replaceFragment(App.fragmenthome, Const.TAG_FRAG_HOME)
            CallApiBasicUrl()
        }
        menu = navigation.menu
        setBottomBarListner()

    }


    private fun setBadgeCount() {
        val bottomNavigationMenuView: BottomNavigationMenuView =
            navigation.getChildAt(0) as BottomNavigationMenuView
        val v = bottomNavigationMenuView.getChildAt(2)
        val itemView = v as BottomNavigationItemView

        val badge: View = LayoutInflater.from(this)
            .inflate(R.layout.layout_cart_no, itemView, true)
        badgetext = badge.findViewById<TextView>(R.id.notifications_badge)
    }


    private fun CallApiBasicUrl() {
        if (isInternetAvailable(this)) {
            mprogressbar.showProgressBar()
            val retrofit = WebService.getRetrofit(this@HomeActivity).create(ApiClient::class.java)
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
                                        storeTerms(responseurl.data.TermsCondition)
                                        storePrivacy(responseurl.data.PrivacyPolicy)
                                        storeAboutUs(responseurl.data.AboutUs)
                                        storeContactUs(responseurl.data.ContactUs)
                                        storeRefundUrl(responseurl.data.CancellationPolicy)
                                    } else {
                                        if (responseurl.status != -999) {
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

    private fun setBottomBarListner() {

        navigation.setOnNavigationItemSelectedListener { item ->
            selectLastMenu()
            when (item.itemId) {
                R.id.menu_home -> {

                    Log.e("////", "menu_home click")

                    App.isShowProgressbar = true
                    if (lastSelectedIndex == 0) {
                        clearBackstackTillFragment(lastSelectedIndex)
                        return@setOnNavigationItemSelectedListener true
                    } else {
                        setHomeScreen()
                        return@setOnNavigationItemSelectedListener true
                    }
                }
                R.id.menu_collection -> {
                    Log.e("////", "menu_collection click")

                    App.isShowProgressbar = true
                    if (lastSelectedIndex == 1) {
                        clearBackstackTillFragment(lastSelectedIndex)
                        return@setOnNavigationItemSelectedListener true
                    } else {
                        setCollectionScreen()
                        return@setOnNavigationItemSelectedListener true
                    }
                }

                R.id.menu_cart -> {

                    Log.e("////", "menu_cart click")
                    Log.e("////lastSelectedIndex", lastSelectedIndex.toString())

                    App.isShowProgressbar = true
                    setCartScreen()
                    return@setOnNavigationItemSelectedListener true

                    /*   if (lastSelectedIndex == 2) {
                        clearBackstackTillFragment(lastSelectedIndex)
                        return@setOnNavigationItemSelectedListener true
                    } else {
                        setCartScreen()
                        return@setOnNavigationItemSelectedListener true
                    }*/
                }

                R.id.menu_acount -> {
                    Log.e("////", "menu_acount click")

                    if (getUserId().isEmpty()) {
                        mprogressbar.hideProgressBar()
                        Handler().postDelayed(Runnable {
                            setAcountScreen()
                        }, 500)
                    } else {
                        setAcountScreen()
                    }
                    return@setOnNavigationItemSelectedListener true

                    /*if (lastSelectedIndex == 3) {
                        clearBackstackTillFragment(lastSelectedIndex)
                        return@setOnNavigationItemSelectedListener true
                    } else {

                        if (getUserId().isEmpty()) {
                            mprogressbar.hideProgressBar()
                            Handler().postDelayed(Runnable { setAcountScreen() }, 500)
                        } else {
                            setAcountScreen()
                        }
                        return@setOnNavigationItemSelectedListener true
                    }*/
                }
            }
            false

        }
    }

    fun setCollectionScreen() {
        if (getCartItem() != 0) {
            badgetext.visibility = View.VISIBLE
        }
        lastSelectedIndex = 1
        clearBackstackTillFragment(lastSelectedIndex)

        // replaceFragment(App.fragmentcollection, Const.TAG_FRAG_COLLECTION)
        val fragment = App.fragmentcollection
        supportFragmentManager
            ?.beginTransaction()
            ?.add(R.id.cm_fragmentContainer, fragment, Const.TAG_FRAG_COLLECTION)
            ?.addToBackStack(null)
            ?.commit()

    }

    fun setCartScreen() {

        badgetext.visibility = View.GONE
        lastSelectedIndex = 2
        clearBackstackTillFragment(lastSelectedIndex)

        //replaceFragment(App.fragmentcart, Const.TAG_FRAG_CART)

        val fragment = App.fragmentcart
        supportFragmentManager
            ?.beginTransaction()
            ?.add(R.id.cm_fragmentContainer, fragment, Const.TAG_FRAG_CART)
            ?.addToBackStack(null)
            ?.commit()
        //clearBackstack()
    }

    fun setAcountScreen() {

        selecAccounMenu()

        if (getCartItem() != 0) {
            badgetext.visibility = View.VISIBLE
        }
        lastSelectedIndex = 3
        clearBackstackTillFragment(lastSelectedIndex)
        //  replaceFragment(App.fragmentaccount, Const.TAG_FRAG_ACCOUNT)

        try {
            val fragment = App.fragmentaccount

            if (!fragment.isAdded) {
                supportFragmentManager
                    ?.beginTransaction()
                    ?.add(R.id.cm_fragmentContainer, fragment, Const.TAG_FRAG_ACCOUNT)
                    ?.addToBackStack(null)
                    ?.commit()
            } else {
                replaceFragment(App.fragmentaccount, Const.TAG_FRAG_ACCOUNT)
            }
        } catch (e: Exception) {

        }

    }

    private fun setHomeScreen() {
        if (getCartItem() != 0) {
            badgetext.visibility = View.VISIBLE
        }

        lastSelectedIndex = 0
        clearBackstackTillFragment(lastSelectedIndex)
        replaceFragment(App.fragmenthome, Const.TAG_FRAG_HOME)
        /* val fragment = App.fragmenthome
         supportFragmentManager
             ?.beginTransaction()
             ?.add(R.id.cm_fragmentContainer, fragment, Const.TAG_FRAG_HOME)
             ?.addToBackStack(null)
             ?.commit()*/
    }


    fun loadOrderDetails() {
        // clearBackstack()
        //  clearBackstackTillFragment(lastSelectedIndex)
        try {
            canDoubleBack = false
            val fragment = MyOrderFragment().getInstance(false, true)
            supportFragmentManager
                ?.beginTransaction()
                ?.add(R.id.cm_fragmentContainer, fragment, Const.TAG_FRAG_ORDERLIST)
                ?.addToBackStack(null)
                ?.commit()
            //replaceFragment(fragment, Const.TAG_FRAG_ORDERLIST)
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
        }

    }

    fun replaceFragment(fragment: Fragment?, tag: String) {
        if (fragment != null) {
            Log.e("res", fragment.tag + " tag - " + tag)
            fragmentManager = supportFragmentManager
            if (tag.equals("1")) {
                navigation.visibility = View.VISIBLE
            }
            val fragmentTransaction: FragmentTransaction =
                this.supportFragmentManager.beginTransaction()

            // Get all Fragment list.
            val fragmentList =
                fragmentManager?.fragments

            var count = 0

            if (fragmentList != null) {
                val size: Int = fragmentList.size
                for (i in 0 until size) {
                    val fragmentfind = fragmentList[i]
                    if (fragmentfind != null) {
                        val fragmentTag = fragmentfind.tag
                        // If Fragment tag name is equal then return it.
                        Log.e("res", "frag - " + fragmentTag + " tag - " + tag)
                        if (fragmentTag == tag) {
                            fragmentTransaction.show(fragment)
                            count++
                        } else {
                            fragmentTransaction.hide(fragmentfind)
                        }
                    }
                }
            }
            try {
                Log.e("res", "add frag count " + count + " isadded - " + fragment.isAdded)
                fragmentManager?.executePendingTransactions()
                if (count == 0 && !fragment.isAdded) {
                    // The fragment to be selected does not (yet) exist in the fragment manager, add it.
                    fragmentTransaction.add(R.id.cm_fragmentContainer, fragment, tag)
                    Log.e("res", "add frag")
                } else {
                    Log.e("res", "add frag else")
                }
            } catch (e: java.lang.Exception) {
                Log.e("res", e.toString())
            }
            fragmentTransaction.commit()
        }
    }

    fun clearBackstack() {
        try {
            val fragmentManager = supportFragmentManager
            for (i in 0 until fragmentManager.fragments.size) {
                fragmentManager.popBackStackImmediate()
            }
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }
    }

    fun clearBackstackTillFragment(lastSelectedIndex: Int) {
        val fragmentManager = supportFragmentManager
        if (lastSelectedIndex == 0) {
            clearBackstack()
            replaceFragment(App.fragmenthome, Const.TAG_FRAG_HOME)
        }
        try {
            for (i in 0 until fragmentManager.fragments.size) {
                //for (i in 0 until fragmentManager.backStackEntryCount) {
                if (fragmentManager.fragments[i] != null) {
                    Log.e(
                        "tagFragment",
                        "fragmentManager.fragments[i] " + fragmentManager.fragments[i]
                    )
                    when (lastSelectedIndex) {
                        0 -> {
                            if (fragmentManager.fragments[i] is HomeFragment) {
                            } else {
                                fragmentManager.popBackStackImmediate()
                            }
                        }
                        1 -> {
                            if (fragmentManager.fragments[i] is CollctionListFragment) {
                            } else {
                                fragmentManager.popBackStackImmediate()
                            }
                        }
                        2 -> {
                            if (fragmentManager.fragments[i] is CartFragment) {

                            } else {
                                fragmentManager.popBackStackImmediate()
                            }
                        }
                        3 -> {
                            if (fragmentManager.fragments[i] is MyAccountFragment) {

                            } else {
                                fragmentManager.popBackStackImmediate()
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("home", "line no 349  " + e.toString())
        }
        selectLastMenu()
    }


    public fun deselectAllMenu() {
        canDoubleBack = false
        navigation.menu.setGroupCheckable(0, false, true)
    }

    public fun selectLastMenu() {
        canDoubleBack = true
        navigation.menu.setGroupCheckable(0, true, true)
    }

    public fun selectFirstMenu() {
        canDoubleBack = true
        navigation.menu.setGroupCheckable(1, true, true)
    }

    public fun selecAccounMenu() {
        canDoubleBack = true
        val item: MenuItem = menu.getItem(3)
        item.setChecked(true)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        bundle = intent.extras
        if (bundle != null) {
            if (bundle?.containsKey(Const.ORDER_ID)!!) {
                isNotificatom = bundle?.getString(Const.ORDER_ID).toString()
            } else if (bundle?.containsKey(Const.NOTI_USER_REGISTER)!!) {
                loadEditProfile()
            } else if (bundle?.containsKey(Const.NOTI_ADD_WALET)!!) {
                setAcountScreen()
            } else if (bundle?.containsKey(Const.NOTI_NEW_PRODUCT)!!) {
                val id = bundle?.getString(Const.NOTI_NEW_PRODUCT)
                id?.let { setProductDetail(it) }
            } else if (bundle?.containsKey(Const.NOTI_NEW_CATALOGUE)!!) {
                val id = bundle?.getString(Const.NOTI_NEW_CATALOGUE)
                val title = bundle?.getString(Const.TITLE)
                id?.let { title?.let { it1 -> loadCollectionItems(it, it1) } }
            } else if (bundle?.containsKey(Const.NOTI_DEFUALT)!!) {
                val fragment = HomeFragment()
                supportFragmentManager
                    ?.beginTransaction()
                    ?.add(R.id.cm_fragmentContainer, fragment, fragment::class.java.name)
                    ?.addToBackStack(null)
                    ?.commit()
            }
        }
    }

    fun loadCollectionItems(id: String, name: String) {

        val fragment = CollectionFragment().getInstance(id, name)
        deselectAllMenu()
        supportFragmentManager
            ?.beginTransaction()
            ?.add(R.id.cm_fragmentContainer, fragment, Const.TAG_FRAG_COLLECTION)
            ?.addToBackStack(null)
            ?.commit()

    }

    public fun setProductDetail(id: String) {
        deselectAllMenu()
        val fragment = ProductDetailFragment().getInstance(id, "")
        supportFragmentManager
            ?.beginTransaction()
            ?.add(R.id.cm_fragmentContainer, fragment, fragment::class.java.name)
            ?.addToBackStack(null)
            ?.commit()
    }

    override fun onResume() {
        super.onResume()
        if (isNotificatom.isNotEmpty()) {
            storeCartItem(0)
            badgetext.visibility = View.GONE
            loadOrderDetails()
        }
        setBadgeCount()
    }


    override fun onBackPressed() {
        if (canDoubleBack) {
            showBackButtonExitDialog(this@HomeActivity)
        } else {
            if (fragmentManager == null) {
                fragmentManager = supportFragmentManager
            }
            val f = fragmentManager?.findFragmentById(R.id.cm_fragmentContainer)
            if (f is MyOrderFragment) {
                if (App.isOrderDone) {
                    App.isOrderDone = false
                    replaceFragment(App.fragmenthome, Const.TAG_FRAG_HOME)
                } else {
                    replaceFragment(App.fragmentaccount, Const.TAG_FRAG_ACCOUNT)
                }
            } else {
                super.onBackPressed()
            }


        }
    }

    private fun loadEditProfile() {
        val fragment = EditProfileFragment().getInstance(false, false)
        deselectAllMenu()
        replaceFragment(fragment, Const.TAG_FRAG_EDIT_PROFILE)
    }

    var installStateUpdatedListener: InstallStateUpdatedListener =
        object : InstallStateUpdatedListener {
            override fun onStateUpdate(state: InstallState) {
                if (state.installStatus() === InstallStatus.DOWNLOADED) {
                    //CHECK THIS if AppUpdateType.FLEXIBLE, otherwise you can skip
                    popupSnackbarForCompleteUpdate()
                } else if (state.installStatus() === InstallStatus.INSTALLED) {
                    if (mAppUpdateManager != null) {
                        mAppUpdateManager!!.unregisterListener(this)
                    }
                } else {
                    Log.i("TAG", "InstallStateUpdatedListener: state: " + state.installStatus())
                }
            }
        }

    override fun onActivityResult(requestCode: Int, resultCode: Int, @Nullable data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_APP_UPDATE) {
            if (resultCode != RESULT_OK) {
                Log.e("TAG", "onActivityResult: app download failed")
            }
        }

    }

    private fun popupSnackbarForCompleteUpdate() {
        val snackbar: Snackbar = Snackbar.make(
            findViewById(R.id.main_constraint),
            "New app is ready!",
            Snackbar.LENGTH_INDEFINITE
        )
        snackbar.setAction("Install") { view ->
            if (mAppUpdateManager != null) {
                mAppUpdateManager!!.completeUpdate()
            }
        }
        snackbar.setActionTextColor(resources.getColor(R.color.colorPrimary))
        snackbar.show()
    }

    override fun onStop() {
        super.onStop()
        if (this::mAppUpdateManager.isInitialized && mAppUpdateManager != null && isupdate) {
            mAppUpdateManager.unregisterListener(installStateUpdatedListener)
        }
    }


}
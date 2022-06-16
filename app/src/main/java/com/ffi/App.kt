package com.ffi

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.util.Log
import androidx.annotation.RequiresApi
import com.facebook.appevents.AppEventsLogger
import com.ffi.Utils.initPreferences
import com.ffi.allcataloglist.CollctionListFragment
import com.ffi.cart.CartFragment
import com.ffi.home.HomeFragment
import com.ffi.myaccount.MyAccountFragment
import com.livechatinc.inappchat.ChatWindowConfiguration
import io.realm.Realm
import io.realm.RealmConfiguration


class App : Application() {

    override fun onCreate() {
        super.onCreate()

        initPreferences()

        Realm.init(this)
        val realmConfig = RealmConfiguration.Builder()
            .name("search.realm")
            .schemaVersion(0)
            .build()
        Realm.setDefaultConfiguration(realmConfig)

        AppEventsLogger.activateApp(this)
    }

    companion object {
        var isDialogOpen = false
        var isShowProgressbar = true
        var isRedirect = false
        var isOrderDone = false
        var permissionGranted=false
        val fragmenthome = HomeFragment()
        val fragmentcart = CartFragment()
        val fragmentaccount = MyAccountFragment()
        var fragmentcollection = CollctionListFragment()
        var PREF_NAME="ManageStorage"
        var PREF_KEY="Allow"
        var mChatWindowConfiguration: ChatWindowConfiguration? = null

        var posisition: Int = 0

        @kotlin.jvm.JvmField
        var notificationid = 0

        @RequiresApi(Build.VERSION_CODES.R)
        fun checkManageExternalPermission(context: Context): Boolean {
            val permission = Manifest.permission.MANAGE_EXTERNAL_STORAGE
            val res: Int = context.checkCallingOrSelfPermission(permission)
            return res == PackageManager.PERMISSION_GRANTED
        }

        @RequiresApi(Build.VERSION_CODES.R)
        fun requestStoragePermission(context: Context) {

            if(context.checkCallingOrSelfPermission(Manifest.permission.MANAGE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_DENIED){

            }

        }

        fun savePref(context: Context){
            val sharedpreferences: SharedPreferences =
                context.getSharedPreferences(PREF_NAME, MODE_PRIVATE)

            val editor: SharedPreferences.Editor = sharedpreferences.edit()
            editor.putString(PREF_KEY, "value")
            editor.commit()
        }

//        fun checkManageStorage(context: Context):Boolean{
//            var checkStoragae=false
//
//            if (if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
//                    Environment.isExternalStorageManager()
//                } else {
//                    Log.e("Version","Check")
//                    return false
//                }
//            ) {
//
//            } else {
//                val permissionIntent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
//                context.startActivity(permissionIntent)
//                checkStoragae=true
//            }
//            return checkStoragae
//        }

    }

}

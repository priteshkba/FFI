package com.ffi.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.Signature
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Base64
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.ffi.HomeActivity
import com.ffi.R
import com.ffi.Utils.*
import com.ffi.login.LoginActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.InstanceIdResult
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*
import kotlin.collections.ArrayList


class SplashActivity : AppCompatActivity() {

    private val HASH_TYPE = "SHA-256"
    val NUM_HASHED_BYTES = 9
    val NUM_BASE64_CHAR = 11

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        showHashKey()
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )


        setFCMToken(this, Runnable {
            Handler().postDelayed(Runnable {
                var mainIntent: Intent
                getAppSignatures()
                val isLogin = getUserLoginStatus()
                val isSkip = getSkipStatus()
             //   showHashKey()
                if (isLogin || isSkip) {
                    if (getFirstName().isEmpty() && getLastName().isEmpty() && !isSkip) {
                        mainIntent =
                            Intent(this, HomeActivity::class.java)
                        mainIntent.putExtra(Const.TAG_FRAG_EDIT_PROFILE, true)
                        startActivity(mainIntent)
                    } else {
                        mainIntent = Intent(this, HomeActivity::class.java)
                        startActivity(mainIntent)
                    }
                } else {
                    mainIntent = Intent(this, LoginActivity::class.java)
                    startActivity(mainIntent)
                }
                finish()
            }, 2000)
        })
    }

    fun setFCMToken(activity: SplashActivity, runnable: Runnable) {
        require(!(activity == null || runnable == null)) { "Parameter must not be null" }
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(object :
            OnCompleteListener<InstanceIdResult?> {
            override fun onComplete(task: Task<InstanceIdResult?>) {
                if (task.isSuccessful()) {
                    val instanceIdResult: InstanceIdResult? = task.getResult()
                    if (instanceIdResult != null) {
                        val token: String = instanceIdResult.getToken()
                        storeFCMToken(token)
                        Log.e("////////////","getFcmToken///"+token)
                        runnable.run()
                    } else {
                        storeFCMToken("")
                        runnable.run()
                        Toast.makeText(
                            activity,
                            "InstanceIdResult result is null",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else {
                    val exception = task.getException()
                    if (exception != null) {
                        Toast.makeText(activity, exception.message, Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(activity, "Null value of Exception.", Toast.LENGTH_LONG)
                            .show()
                    }
                    storeFCMToken("")
                    runnable.run()
                }
            }
        })
    }

    fun showHashKey() {
        // Add code to print out the key hash
        try {
            @SuppressLint("PackageManagerGetSignatures") val info: PackageInfo =
                getPackageManager().getPackageInfo(
                    getApplicationContext().getPackageName(),
                    PackageManager.GET_SIGNATURES
                )
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                Log.d(
                    "KeyHash:",
                    Base64.encodeToString(md.digest(), Base64.DEFAULT)
                )
            }
        } catch (e: PackageManager.NameNotFoundException) {
            Log.e("fgconnect","ex1:")

            e.printStackTrace()
        } catch (e: NoSuchAlgorithmException) {
            Log.e("fgconnect","ex2:")
            e.printStackTrace()
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private fun hash(packageName: String, signature: String): String? {
        val appInfo: String = "$packageName $signature"
        try {
            val messageDigest = MessageDigest.getInstance(HASH_TYPE)
            messageDigest.update(appInfo.toByteArray())
            var hashSignature = messageDigest.digest()

            // truncated into NUM_HASHED_BYTES
            hashSignature = Arrays.copyOfRange(hashSignature, 0, NUM_HASHED_BYTES)
            // encode into Base64
            var base64Hash =
                Base64.encodeToString(hashSignature, Base64.NO_PADDING or Base64.NO_WRAP)
            base64Hash = base64Hash.substring(0, NUM_BASE64_CHAR)
            Log.e("TAG", String.format("pkg: %s -- hash: %s", packageName, base64Hash))
            return base64Hash
        } catch (e: NoSuchAlgorithmException) {
            Log.e("TAG", "hash:NoSuchAlgorithm", e)
        }
        return null
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    fun getAppSignatures(): ArrayList<String>? {
        val appCodes: ArrayList<String> = ArrayList()
        try {
            // Get all package signatures for the current package
            val packageName = packageName
            val packageManager = packageManager
            val signatures: Array<Signature> = packageManager.getPackageInfo(
                packageName,
                PackageManager.GET_SIGNATURES
            ).signatures

            // For each signature create a compatible hash
            for (signature in signatures) {
                val hash = hash(packageName, signature.toCharsString())
                if (hash != null) {
                    appCodes.add(String.format("%s", hash))
                }
            }
        } catch (e: PackageManager.NameNotFoundException) {
            Log.e("TAG", "Unable to find package to obtain hash.", e)
        }
        return appCodes
    }
}

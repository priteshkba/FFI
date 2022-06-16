package com.ffi.api

import android.content.Context
import android.content.Intent
import android.os.Build
import com.ffi.BuildConfig
import com.ffi.Utils.*
import com.ffi.Utils.Const.Companion.DEVICE_TYPE_ANDROID
import com.ffi.Utils.Const.Companion.LANG_ENGLISH
import com.ffi.login.LoginActivity
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.IOException
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import android.os.*
import java.util.*
import android.util.Log

object WebService {

    private var retrofit: Retrofit? = null
    private var client: OkHttpClient? = null
    lateinit var context: Context

    fun getRetrofit(applicationContext: Context): Retrofit {
        context = applicationContext

        if (retrofit == null) {
            val gson = GsonBuilder().setLenient().create()
            retrofit = Retrofit.Builder()
                .baseUrl(Apis.BASE_URL)
                .client(getUnsafeOkHttpClient())
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        }
        return retrofit!!

    }

    private fun getUnsafeOkHttpClient(): OkHttpClient? {
        return try {
            var trustAllCerts: Array<TrustManager>? = null
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                trustAllCerts =
                arrayOf<TrustManager>(
                    object : X509TrustManager {
                        @Throws(CertificateException::class)
                        override fun checkClientTrusted(
                            chain: Array<X509Certificate>,
                            authType: String
                        ) {
                        }
                        @Throws(CertificateException::class)
                        override fun checkServerTrusted(
                            chain: Array<X509Certificate>,
                            authType: String
                        ) {
                        }
                        override fun getAcceptedIssuers(): Array<X509Certificate?> {
                            return arrayOfNulls(0)
                        }
                    }
                )

            }


            val okHttpClient = OkHttpClient.Builder()
            okHttpClient.addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))

            okHttpClient.addInterceptor { chain ->
                val original = chain.request()
//                Log.e("tagHeader", "getUserToken() " + getUserToken())
//                Log.e("tagHeader", "getUserId() " + getUserId())
                val request = original.newBuilder()
                    .header(Const.DEVICE_ID, context.getDeviceId())
                    .header(Const.DEVICE_TYPE, DEVICE_TYPE_ANDROID)
                    .header(Const.OS_VERSION, getAndroidVersion()!!)
                    .header(Const.DEVICE_NAME, getDeviceName()!!)
                    .header(Const.USER_TOKEN, getUserToken())
                    .header(Const.USER_ID, getUserId())
                    .header(Const.LANGUAGE_ID, LANG_ENGLISH)
                    .header(Const.APP_ID, Const.APP_ID_VERSION)
                    .header(Const.APP_VERSION_NO, BuildConfig.VERSION_NAME) // //Const.APP_VERSION_TO_SEND //BuildConfig.VERSION_NAME
                    .method(original.method, original.body)
                    .build()
                chain.proceed(request)
            }
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {

                val sslContext = SSLContext.getInstance("SSL")
                sslContext.init(null, trustAllCerts, SecureRandom())
                val sslSocketFactory = sslContext.socketFactory

                okHttpClient.connectTimeout(2, TimeUnit.MINUTES)
                    .readTimeout(2, TimeUnit.MINUTES)
                    .retryOnConnectionFailure(true)
                    .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                    .build()
                okHttpClient.sslSocketFactory(sslSocketFactory, trustAllCerts!![0] as X509TrustManager)
                okHttpClient.hostnameVerifier(HostnameVerifier { hostname, session -> true })
                okHttpClient.build()
            }




            okHttpClient.addInterceptor(object : Interceptor {
                @Throws(IOException::class)
                override fun intercept(chain: Interceptor.Chain): Response {
                    val request = chain.request()
                    val response = chain.proceed(request)
                    try {
//                        Log.e("tagResponseCode", "response  ${response} ")
//                        Log.e("tagResponseCode", "response.code ${response.code} ")
//                        Log.e("tagResponseCode", "response.body ${response.body?.toString()} ")
                        if (response.code == 401) {
                            removeUserData()
                            val intent =
                                Intent(context, LoginActivity::class.java)
                            intent.flags =
                                Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                            context.startActivity(intent)
//                            throw  Exception("Server error code: " + response.code + " with error message: " + response.message);
                            return response
                        }
                        else {
                            return context.handleForbiddenResponse(response)!!
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                        return response
                    }
//                    return response
                }
            }).connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(1, TimeUnit.MINUTES)
                .retryOnConnectionFailure(true)
//                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
//                .sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
//                .hostnameVerifier(HostnameVerifier { hostname, session -> true })
                .build()
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }
}
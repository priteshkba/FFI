package com.ffi.Utils

import android.content.Context
import android.content.SharedPreferences
import android.text.TextUtils
import android.util.Log

var sharedPreferences: SharedPreferences? = null
var editor: SharedPreferences.Editor? = null

var sharedPreferencestoken: SharedPreferences? = null
var editortoken: SharedPreferences.Editor? = null
val SHARED_PREF_FCM_TOKEN = "USER_DATA_PREFS_TOKEN"

val SHARED_PREF_NAME = "USER_DATA_PREFS"
val IS_LOGIN = "IS_LOGIN"
val IS_SKIP = "IS_SKIP"

val IS_SOCIAL_LOGIN = "IS_SOCIAL_LOGIN"
val FCM_TOKEN = "fcmtoken"
val USER_ID = "USER_ID"
val MOBILE_NO = "MOBILE_NO"
val FIRST_NAME = "FIRST_NAME"
val LAST_NAME = "LAST_NAME"
val EMAIL = "EMAIL"
val PHONE_CODE = "phone_code"
val DEFUALT_ADDRESS_ID = "addressid"
val PHOTO_URL = "profilepic"

val MEDIA_LINK = "media_link"
val BASIC_URL = "basic_url"
val PAYMENT_LINK = "paymenturl"
val SUCCESS_URL = "successurl"
val CANCEL_URL = "cancelurl"
val TERMS_URL = "termsurl"
val PRIVACY_URL = "privacyurl"
val REFUND_URL = "Refundurl"
val ABOUT_US = "abputus"
val CONTACT_US = "contactus"

val CART_ITEMS = "cartitems"

val USER_TOKEN = "USER_TOKEN"


fun Context.initPreferences() {
    sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
    sharedPreferencestoken = getSharedPreferences(SHARED_PREF_FCM_TOKEN, Context.MODE_PRIVATE)
}

fun storeUserId(user_id: String) {
    editor = sharedPreferences?.edit()
    editor?.putString(USER_ID, user_id)
    editor?.commit()
}

fun getUserId(): String {
    return sharedPreferences?.getString(USER_ID, "") ?: ""
}

fun storeBasicUrl(link: String) {
    editor = sharedPreferences?.edit()
    editor?.putString(BASIC_URL, link)
    editor?.commit()
}

fun getBasicUrl(): String {
    return sharedPreferences?.getString(BASIC_URL, "") ?: ""
}

fun storeAboutUs(link: String) {
    editor = sharedPreferences?.edit()
    editor?.putString(ABOUT_US, link)
    editor?.commit()
}

fun getAboutUs(): String {
    return sharedPreferences?.getString(ABOUT_US, "") ?: ""
}

fun storeContactUs(link: String) {
    editor = sharedPreferences?.edit()
    editor?.putString(CONTACT_US, link)
    editor?.commit()
}

fun getContactUs(): String {
    return sharedPreferences?.getString(CONTACT_US, "") ?: ""
}

fun storeRefundUrl(link: String) {
    editor = sharedPreferences?.edit()
    editor?.putString(REFUND_URL, link)
    editor?.commit()
}

fun getRefund(): String {
    return sharedPreferences?.getString(REFUND_URL, "") ?: ""
}


fun storeMediaLink(link: String) {
    editor = sharedPreferences?.edit()
    editor?.putString(MEDIA_LINK, link)
    editor?.commit()
}

fun getMediaLink(): String {
    return sharedPreferences?.getString(MEDIA_LINK, "") ?: ""
}


fun storePhoneCode(phonecode: String) {
    editor = sharedPreferences?.edit()
    editor?.putString(PHONE_CODE, phonecode)
    editor?.commit()
}

fun getPhoneCode(): String {
    return sharedPreferences?.getString(PHONE_CODE, "") ?: ""
}


fun storeDefultAddressId(addressid: String) {
    editor = sharedPreferences?.edit()
    editor?.putString(DEFUALT_ADDRESS_ID, addressid)
    editor?.commit()
}

fun getDefualtAddressId(): String {
    return sharedPreferences?.getString(DEFUALT_ADDRESS_ID, "") ?: ""
}


fun storeTerms(terms: String) {
    editor = sharedPreferences?.edit()
    editor?.putString(TERMS_URL, terms)
    editor?.commit()
}

fun getTerms(): String {
    return sharedPreferences?.getString(TERMS_URL, "") ?: ""
}


fun storePrivacy(terms: String) {
    editor = sharedPreferences?.edit()
    editor?.putString(PRIVACY_URL, terms)
    editor?.commit()
}

fun getPrivacy(): String {
    return sharedPreferences?.getString(PRIVACY_URL, "") ?: ""
}


fun storeUserLoginStatus(is_login: Boolean) {
    editor = sharedPreferences?.edit()
    editor?.putBoolean(IS_LOGIN, is_login)
    editor?.commit()
}

fun getUserLoginStatus(): Boolean {
    return sharedPreferences?.getBoolean(IS_LOGIN, false) ?: false
}

fun storeSKipStatus(is_skip: Boolean) {
    editor = sharedPreferences?.edit()
    editor?.putBoolean(IS_SKIP, is_skip)
    editor?.commit()
}

fun getSkipStatus(): Boolean {
    return sharedPreferences?.getBoolean(IS_SKIP, false) ?: false
}


fun storeMobileNo(user_name: String) {
    editor = sharedPreferences?.edit()
    editor?.putString(MOBILE_NO, user_name)
    editor?.commit()
}

fun getMobileNo(): String {
    return sharedPreferences?.getString(MOBILE_NO, "") ?: ""
}


fun storeEmail(email: String) {
    editor = sharedPreferences?.edit()
    editor?.putString(EMAIL, email)
    editor?.commit()
}

fun getEmail(): String {
    return sharedPreferences?.getString(EMAIL, "") ?: ""
}


fun storeUserToken(user_token: String) {
    editor = sharedPreferences?.edit()
    editor?.putString(USER_TOKEN, user_token)
    editor?.commit()
}

fun getUserToken(): String {
    return sharedPreferences?.getString(USER_TOKEN, "") ?: ""
}

fun storeFirstName(first_name: String) {
    editor = sharedPreferences?.edit()
    editor?.putString(FIRST_NAME, first_name)
    editor?.commit()
}

fun getFirstName(): String {
    return sharedPreferences?.getString(FIRST_NAME, "") ?: ""
}

fun storeLastName(last_name: String) {
    editor = sharedPreferences?.edit()
    editor?.putString(LAST_NAME, last_name)
    editor?.commit()
}

fun getLastName(): String {
    return sharedPreferences?.getString(LAST_NAME, "") ?: ""
}

fun getFullName(): String {
    if(!TextUtils.isEmpty(getFirstName()) && !TextUtils.isEmpty(getLastName())){
        return getFirstName() + " " + getLastName()
    }
    else {
        if(!TextUtils.isEmpty(getFirstName())){
            if(!TextUtils.isEmpty(getLastName())){
                return getFirstName() + " " + getLastName()
            }
            else {
                return getFirstName()
            }
        }
        else {
            if(!TextUtils.isEmpty(getLastName())){
                return getLastName()
            }
            else {
                return getLastName()
            }
        }
    }
}

fun storePaymentUrl(link: String) {
    editor = sharedPreferences?.edit()
    editor?.putString(PAYMENT_LINK, link)
    editor?.commit()
}

fun getPaymentUrl(): String {
    return sharedPreferences?.getString(PAYMENT_LINK, "") ?: ""
}


fun storeSuccessUrl(link: String) {
    editor = sharedPreferences?.edit()
    editor?.putString(SUCCESS_URL, link)
    editor?.commit()
}

fun getSuccessUrl(): String {
    return sharedPreferences?.getString(SUCCESS_URL, "") ?: ""
}


fun storeIsSocialLogin(link: Boolean) {
    editor = sharedPreferences?.edit()
    editor?.putBoolean(IS_SOCIAL_LOGIN, link)
    editor?.commit()
}

fun getIsSocialLogin(): Boolean {
    return sharedPreferences?.getBoolean(IS_SOCIAL_LOGIN, false) ?: false
}


fun storeCancelUrl(link: String) {
    editor = sharedPreferences?.edit()
    editor?.putString(CANCEL_URL, link)
    editor?.commit()
}

fun getCancelUrl(): String {
    return sharedPreferences?.getString(CANCEL_URL, "") ?: ""
}

fun storeProfileUrl(link: String) {
    editor = sharedPreferences?.edit()
    editor?.putString(PHOTO_URL, link)
    editor?.commit()
}

fun getProfileUrl(): String {
    return sharedPreferences?.getString(PHOTO_URL, "") ?: ""
}

fun storeFCMToken(fcm_token: String) {
    editor = sharedPreferencestoken?.edit()
    editor?.putString(FCM_TOKEN, fcm_token)
    editor?.commit()

    Log.e("token", "store " + sharedPreferencestoken?.getString(FCM_TOKEN, "") ?: "")
}

fun getFcmToken(): String {
    return sharedPreferencestoken?.getString(FCM_TOKEN, "") ?: ""
}


fun storeCartItem(no: Int) {
    editor = sharedPreferences?.edit()
    editor?.putInt(CART_ITEMS, no)
    editor?.commit()
}

fun getCartItem(): Int? {
    return sharedPreferences?.getInt(CART_ITEMS, 0)
}


fun removeUserData() {
    editor = sharedPreferences?.edit()
    editor?.clear()
    /*   editor?.remove(USER_TOKEN)*/
    editor?.commit()
}
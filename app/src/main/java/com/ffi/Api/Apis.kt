package com.ffi.api

object Apis {
//    const val BASE_URL = "https://testupdates.com/FFIAPI/"
    const val BASE_URL = "https://ffistores.com/FFI_Api/"
 //   const val BASE_URL = "http://103.209.147.70/FFI_Api/index.php/"

    const val AUTH_V1 = "v1/auth/"
    const val CATALOG_V1 = "v1/catalogue/"

    const val API_LOGIN = AUTH_V1 + "login"
    const val API_VERIFY_OTP = AUTH_V1 + "verifyOTP"
    const val API_SOCIAL_LOGIN = AUTH_V1 + "socialLoginV2"
    const val API_SOCIAL_UPDATE_PROFILE = "v1/user/updateSocialUserProfile"
    const val API_HOME_DATA = "v1/home/getData"
    const val API_GET_TOP_PRODUCT = "v1/product/topProduct"
    const val API_LOGOUT = AUTH_V1 + "logout"
    const val API_BASIC_URL = "v1/config/urls"
    const val API_ADDRESS_DATA = "v1/address/GetAddressByOrderId"
    const val API_CATEGORY_LIST = "v1/category/list"
    const val API_CATALOG = CATALOG_V1 + "list"
    const val API_CATALOG_DETAILS = CATALOG_V1 + "detail"
    const val API_PRODUCT_LIST = "v1/category/detail"
    const val API_PRODUCT_DETAIL = "v1/product/detail"
    const val API_ADD_PRODUCT = "v1/cart/add"

    const val API_PRODUCT_ADD_REVIEW = "v1/review/add"
    const val API_REVIEWS = "v1/product/review"
    const val API_GET_RATING_ID = "v1/config/ratingPoints"

    const val API_CART_LIST = "v1/cart/items"
    const val API_CART_REMOVE_ITEM = "v1/cart/remove"
    const val API_CHECKOUT = "v1/cart/checkoutv2"
    const val API_MAKE_PAYMENT = "v1/payment/makePaymentV2"
    const val API_PAYMENT_TYPE = "v1/payment/getPaymentTypeList"
    const val API_MY_OREDER_LIST = "v1/order/list"
    const val API_MY_OREDER_DETAILS = "v1/order/detail"
    const val API_ADD_NOTE = "v1/order/notesadd"
    const val API_RETURN_PRODUCT_REASON = "v1/order/GetOrderReturnReasons"
    const val API_ADD_REMOVE_WISHLIST = "v1/wishlist/addremove"
    const val API_WISHLIST = "v1/wishlist/items"
    const val API_WALLET_HISTORY = "v1/wallet/getWalletTransactionDetail"
    const val API_UPDATE_PROFILE = "v1/user/updateProfile"
    const val API_GET_PROFILE = "v1/user/getData"
    const val API_GET_SHARED_DATA = "v1/user/getSharedContent"
    const val API_ADD_SHARED_DATA = "v1/user/addSharedContent"

    //Address related APIs
    const val API_ADD_ADDRESS = "v1/address/add"
    const val API_ADDRESS_LIST = "v1/address/list"
    const val API_ADDRESS_REMOVE = "v1/address/remove"
    const val API_COPY_ADDRESS = "v1/address/copyaddress"
    const val API_COUNTRY = "v1/common/countries"
    const val API_STATE_LIST = "v1/common/states"

    //Search apis
    const val API_SEARCH_ORDER = "v1/order/search"
    const val API_CATEGORY_SEARCH = "v1/category/search"
    const val API_PRODUCT_SEARCH = "v1/product/search"

    //sortby and filter
    const val API_GET_FILTER_DATA = "v1/home/filterData"
    const val API_SEND_REQUEST_FILTER = "v1/home/filterResult"
    const val API_SORT_BY = "v1/home/sortByResult"

    const val API_GET_WALLET = "v1/wallet/getWalletAmount"
    const val API_ADD_WALLET = "v1/wallet/addMoney"
    const val API_MEMBERSHIP = "v1/membership/membershipList"
    const val API_ADD_MEBER_SHIP = "v1/membership/addMembershipToUser"

    const val API_RETURN_ORDER = "v1/order/OrderReturnRequest"

}
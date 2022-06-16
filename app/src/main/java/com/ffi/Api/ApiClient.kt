package com.ffi.api

import com.ffi.api.Apis
import com.ffi.add_address.*
import com.ffi.addreslist.ParamRemoveAddress
import com.ffi.addreslist.ResponseAddressList
import com.ffi.addreslist.ResponseDeleteAddress
import com.ffi.cart.ParamCartItemremove
import com.ffi.cart.ResponseCartItemRemove
import com.ffi.cart.ResponseCartItems
import com.ffi.cart.ResponseCheckout
import com.ffi.catalog.ParamCatalogList
import com.ffi.catalog.ResponseCatalogList
import com.ffi.category.*
import com.ffi.collectiondetail.ParamCollectionDetail
import com.ffi.collectiondetail.ResponseCollectionDetails
import com.ffi.edit_profile.ResponseProfile
import com.ffi.filter.ParamFilterResult
import com.ffi.filter.ResponseFilter
import com.ffi.filter.ResponseFilterResult
import com.ffi.filter.ResponseFilterResultProduct
import com.ffi.home.ParamProductSearch
import com.ffi.home.ResponseBasicUrl
import com.ffi.home.ResponseHome
import com.ffi.home.ResponseProductSearch
import com.ffi.login.*
import com.ffi.membership.ParamAddMeberShip
import com.ffi.membership.ParamMemberShip
import com.ffi.membership.ResponseAddMebership
import com.ffi.membership.ResponseMeberShip
import com.ffi.model.*
import com.ffi.model.sortby.ParamSortby
import com.ffi.model.sortby.ResponseSortby
import com.ffi.model.sortby.ResponseSortbyinproduct
import com.ffi.my_orderdetails.*
import com.ffi.my_orders.ParamOrderList
import com.ffi.my_orders.ParamSearchOrders
import com.ffi.my_orders.ResponseOrderList
import com.ffi.my_orders.ResponseSearchOrder
import com.ffi.myaccount.ParamLogout
import com.ffi.myaccount.ResponseLogout
import com.ffi.otpscreen.ParamOTP
import com.ffi.otpscreen.ResponseOTP
import com.ffi.payment.ResponsePayment
import com.ffi.payment.ResponsePaymentType
import com.ffi.productdetail.ParamAddProductNew
import com.ffi.productdetail.ParamProductDetail
import com.ffi.productdetail.ResponseAddProduct
import com.ffi.productdetail.ResponseProductDetails
import com.ffi.productlist.ParamProductList
import com.ffi.productlist.ResponsnsProductList
import com.ffi.productlist.topproduct.ParamGetProduct
import com.ffi.productlist.topproduct.ResponseGetTopProduct
import com.ffi.reviewlist.ParamReviews
import com.ffi.reviewlist.ResponseReviews
import com.ffi.wallet.*
import com.ffi.wishlist.ResponseWishList
import com.ffi.writereview.ParamAddReview
import com.ffi.writereview.ResponseAddReview
import com.ffi.writereview.ResponseRatingsId
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiClient {

    @POST(Apis.API_LOGIN)
    fun DoLogin(@Body param: ParamLogin): Call<ResponseLogin>

    @POST(Apis.API_VERIFY_OTP)
    fun VerifyOtp(@Body param: ParamOTP): Call<ResponseOTP>

    @POST(Apis.API_SOCIAL_LOGIN)
    fun SocialLogin(@Body param: ParamSocialLogin): Call<ResponseSocialLogin>

    @POST(Apis.API_SOCIAL_UPDATE_PROFILE)
    fun UpdateSocialLogin(@Body param: UpdateSocialLoginModel): Call<ResponseProfile>

    @Multipart
    @POST(Apis.API_UPDATE_PROFILE)
    fun UpdateProfile(@Part file: MultipartBody.Part,
                      @Part("Gender") Gender: Int,
                      @Part("PhoneCode") PhoneCode: String,
                      @Part("MobileNumber") MobileNumber: Long,
                      @Part("EmailAddress") EmailAddress: String,
                      @Part("LastName") LastName: String,
                      @Part("FirstName") FirstName: String,
    ): Call<ResponseProfile>

    @Multipart
    @POST(Apis.API_UPDATE_PROFILE)
    fun UpdateProfile(@Part("Gender") Gender: Int,
                      @Part("PhoneCode") PhoneCode: String,
                      @Part("MobileNumber") MobileNumber: Long,
                      @Part("EmailAddress") EmailAddress: String,
                      @Part("LastName") LastName: String,
                      @Part("FirstName") FirstName: String,
    ): Call<ResponseProfile>

    @POST(Apis.API_LOGOUT)
    fun Logout(@Body param: ParamLogout): Call<ResponseLogout>

    @POST(Apis.API_BASIC_URL)
    fun GetBasicUrls(): Call<ResponseBasicUrl>

    @POST(Apis.API_HOME_DATA)
    fun GetHomeData(): Call<ResponseHome>

    @POST(Apis.API_CATEGORY_LIST)
    fun GetCategory(@Body param: ParamCategory): Call<ResponseCategory>

    @POST(Apis.API_CATEGORY_LIST)
    fun GetSubCategory(@Body param: ParamSubCategory): Call<ResponseCategory>

    @POST(Apis.API_CATALOG)
    fun GetCatalogList(@Body param: ParamCatalogList): Call<ResponseCatalogList>

    @POST(Apis.API_PRODUCT_LIST)
    fun GetProductList(@Body param: ParamProductList): Call<ResponsnsProductList>

    @POST(Apis.API_PRODUCT_DETAIL)
    fun GetProductDetails(@Body param: ParamProductDetail): Call<ResponseProductDetails>

    @POST(Apis.API_REVIEWS)
    fun GetAllReviews(@Body param: ParamReviews): Call<ResponseReviews>

    @POST(Apis.API_PRODUCT_ADD_REVIEW)
    fun WriteReview(@Body param: ParamAddReview): Call<ResponseAddReview>

    @POST(Apis.API_ADD_PRODUCT)
    fun AddProducts(@Body param: ParamAddProductNew): Call<ResponseAddProduct>

    @POST(Apis.API_CART_LIST)
    fun GetCartList(): Call<ResponseCartItems>

    @POST(Apis.API_WISHLIST)
    fun GetWishList(): Call<ResponseWishList>

    @POST(Apis.API_WALLET_HISTORY)
    fun GetWalletHistory(@Body param: ParamWalletHistory): Call<ResponseWalletHistory>

    @POST(Apis.API_ADD_REMOVE_WISHLIST)
    fun AddRemoveWishItem(@Body param: ParamAddREmoveWishItem): Call<ResponseAddRemoveWishItem>

    @POST(Apis.API_CART_REMOVE_ITEM)
    fun CartDeleteItem(@Body param: ParamCartItemremove): Call<ResponseCartItemRemove>

    @POST(Apis.API_ADD_ADDRESS)
    fun AddAddress(@Body param: ParamAddAddress): Call<ResponseAddAddress>

    @POST(Apis.API_CHECKOUT)
    fun Checkout(): Call<ResponseCheckout>
    //fun Checkout(@Body param: ParamCheckout): Call<ResponseCheckout>

    @POST(Apis.API_ADDRESS_LIST)
    fun GetAddressList(): Call<ResponseAddressList>

    @POST(Apis.API_ADDRESS_REMOVE)
    fun RemoveAddress(@Body param: ParamRemoveAddress): Call<ResponseDeleteAddress>

    @GET(Apis.API_COUNTRY)
    fun GetCountryList(): Call<ResponseCountryList>

    @POST(Apis.API_STATE_LIST)
    fun GetStateList(@Body param: ParamStateList): Call<ResponseStateList>

    @POST(Apis.API_COPY_ADDRESS)
    fun AddCopyAddress(@Body param: ParamCopyAddress): Call<ResponseCopyAddress>

    @Multipart
    @POST(Apis.API_MAKE_PAYMENT)
    fun MakePayment( @Part("paymentType") paymentType: Int,
                     @Part("payWithWallet") payWithWallet: Boolean,
                     @Part("addressType") addressType: Int,
                     @Part("addressData") addressData: ParamAddAddress,
                     @Part("Notes") notes: String,
                     @Part file: MultipartBody.Part
    ): Call<ResponsePayment>
    @Multipart
    @POST(Apis.API_MAKE_PAYMENT)
    fun MakePayment( @Part("paymentType") paymentType: Int,
                                @Part("payWithWallet") payWithWallet: Boolean,
                                @Part("addressType") addressType: Int,
                                @Part("addressData") addressData: ParamAddAddress
    ): Call<ResponsePayment>
    @Multipart
    @POST(Apis.API_MAKE_PAYMENT)
    fun MakePaymentWithoutFile( @Part("paymentType") paymentType: Int,
                     @Part("payWithWallet") payWithWallet: Boolean,
                     @Part("addressType") addressType: Int,
                                @Part("Notes") notes: String,
                                @Part("NotesImage") NotesImage: String,
                                @Part("addressData") addressData: ParamAddAddress
    ): Call<ResponsePayment>

    @Multipart
    @POST(Apis.API_MAKE_PAYMENT)
    fun MakePayment(
        @Part("paymentType") paymentType: Int,
        @Part("payWithWallet") payWithWallet: Boolean,
        @Part("addressType") addressType: Int,
        @Part("addressData") addressData: ParamAddAddress,
        @Part file: MultipartBody.Part,
        @Part("Notes") notes: String,
        @Part mMultipartBodyNote: MultipartBody.Part
    ): Call<ResponsePayment>

    @Multipart
    @POST(Apis.API_MAKE_PAYMENT)
    fun MakePayment(
        @Part("paymentType") paymentType: Int,
        @Part("payWithWallet") payWithWallet: Boolean,
        @Part("addressType") addressType: Int,
        @Part("addressData") addressData: ParamAddAddress,
        @Part mMultipartBodyNote: MultipartBody.Part
    ): Call<ResponsePayment>

    @Multipart
    @POST(Apis.API_MAKE_PAYMENT)
    fun MakePayment(
            @Part("paymentType") paymentType: Int,
            @Part("payWithWallet") payWithWallet: Boolean,
            @Part("addressType") addressType: Int,
            @Part("addressData") addressData: ParamAddAddress,
            @Part file: MultipartBody.Part,
            @Part("Notes") notes: String,
            @Part("NotesImage") NotesImage: String

    ): Call<ResponsePayment>

    @Multipart
    @POST(Apis.API_MAKE_PAYMENT)
    fun MakePaymentMultipalImage(
            @Part("paymentType") paymentType: Int,
            @Part("payWithWallet") payWithWallet: Boolean,
            @Part("addressType") addressType: Int,
            @Part("addressData") addressData: ParamAddAddress,
            @Part file: ArrayList<MultipartBody.Part>,
            @Part("Notes") notes: String,
            @Part mMultipartBodyNote: MultipartBody.Part
    ): Call<ResponsePayment>

    @Multipart
    @POST(Apis.API_MAKE_PAYMENT)
    fun MakePaymentMultipalImage(
            @Part("paymentType") paymentType: Int,
            @Part("payWithWallet") payWithWallet: Boolean,
            @Part("addressType") addressType: Int,
            @Part("addressData") addressData: ParamAddAddress,
            @Part file: ArrayList<MultipartBody.Part>,
            @Part("Notes") notes: String,
            @Part("NotesImage") NotesImage: String
    ): Call<ResponsePayment>
    @Multipart
    @POST(Apis.API_MAKE_PAYMENT)
    fun MakePaymentMultipalImage(
        @Part("paymentType") paymentType: Int,
        @Part("payWithWallet") payWithWallet: Boolean,
        @Part("addressType") addressType: Int,
        @Part("addressData") addressData: ParamAddAddress,
        @Part file: ArrayList<MultipartBody.Part>
    ): Call<ResponsePayment>


    @POST(Apis.API_MY_OREDER_LIST)
    fun GetOrderList(@Body param: ParamOrderList): Call<ResponseOrderList>

    @POST(Apis.API_MY_OREDER_DETAILS)
    fun GetOrderDetails(@Body param: ParamOrderDeatils): Call<OrderDetailNewResponse>

    @POST(Apis.API_ADDRESS_DATA)
    fun GetAddressData(@Body param: ParamOrderAddressDeatils): Call<AddressDetailResponse>

    @POST(Apis.API_ADD_NOTE)
    fun addNotes(@Body param: ParamOrderDeatils): Call<ResponseOrderDetail>



    @Multipart
    @POST(Apis.API_ADD_NOTE)
    fun addNotes_withImage(
        @Part("orderId") orderId: RequestBody,
        @Part("notes") notes: RequestBody,
        @Part file: MultipartBody.Part
    ): Call<ResponseOrderDetail>

    @Multipart
    @POST(Apis.API_ADD_NOTE)
    fun addNotes_withoutImage(
        @Part("orderId") orderId: RequestBody,
        @Part("notes") notes: RequestBody
    ): Call<ResponseOrderDetail>


    @POST(Apis.API_CATALOG_DETAILS)
    fun GetCatalogDetails(@Body param: ParamCollectionDetail): Call<ResponseCollectionDetails>

    @POST(Apis.API_PRODUCT_SEARCH)
    fun GetProductSearch(@Body param: ParamProductSearch): Call<ResponseProductSearch>

    @POST(Apis.API_SEARCH_ORDER)
    fun GetSerachfromOrderId(@Body param: ParamSearchOrders): Call<ResponseSearchOrder>

    @POST(Apis.API_CATEGORY_SEARCH)
    fun GetSearchByCategory(@Body param: ParamSearchCategory): Call<ResponseSearchCategory>

    @POST(Apis.API_ADD_SHARED_DATA)
    fun AddSharedProducts(@Body param: ParamAddShareddata): Call<ResponseAddSharedData>

    @POST(Apis.API_GET_SHARED_DATA)
    fun GetSharedSharedData(@Body param: ParamGetSharedData): Call<ResponseGetSharedData>

    @POST(Apis.API_GET_TOP_PRODUCT)
    fun GetTopProduct(@Body param: ParamGetProduct): Call<ResponseGetTopProduct>

    @POST(Apis.API_SORT_BY)
    fun Sortby(@Body param: ParamSortby): Call<ResponseSortby>

    @POST(Apis.API_SORT_BY)
    fun Sortbyinproduct(@Body param: ParamSortby): Call<ResponseSortbyinproduct>

    @POST(Apis.API_GET_FILTER_DATA)
    fun GetfilterData(): Call<ResponseFilter>

    @POST(Apis.API_SEND_REQUEST_FILTER)
    fun ResultforFilterData(@Body param: ParamFilterResult): Call<ResponseFilterResult>

    @POST(Apis.API_SEND_REQUEST_FILTER)
    fun ResultforFilterDataProduct(@Body param: ParamFilterResult): Call<ResponseFilterResultProduct>

    @GET(Apis.API_GET_PROFILE)
    fun GetProfile(): Call<ResponseProfile>

    @GET(Apis.API_GET_RATING_ID)
    fun GetRatingid(): Call<ResponseRatingsId>

    @POST(Apis.API_PAYMENT_TYPE)
    fun GetPaymentType(): Call<ResponsePaymentType>

    @POST(Apis.API_GET_WALLET)
    fun GetWallet(@Body Param: ParamWallet): Call<ResponseWallet>

    @POST(Apis.API_ADD_WALLET)
    fun AddWallet(@Body param: ParamAddWallet): Call<ResponseAddWallet>

    @POST(Apis.API_MEMBERSHIP)
    fun GetMemberShipPlan(@Body param: ParamMemberShip): Call<ResponseMeberShip>

    @POST(Apis.API_ADD_MEBER_SHIP)
    fun AddMemberShip(@Body param: ParamAddMeberShip): Call<ResponseAddMebership>

    @POST(Apis.API_ADDRESS_DATA)
    fun ApiAddressData(@Query("OrderId") OrderId: String): Call<AddressDetailResponse>

    @POST(Apis.API_MY_OREDER_DETAILS)
    fun ApiMyOrderDetails(@Query("orderId") OrderId: String): Call<ResponseOrderDetail>

}
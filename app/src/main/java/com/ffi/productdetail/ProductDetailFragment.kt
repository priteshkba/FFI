package com.ffi.productdetail

import android.Manifest
import android.app.Dialog
import android.content.*
import android.content.Context.CONNECTIVITY_SERVICE
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.text.TextUtils
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.facebook.share.Sharer
import com.facebook.share.model.ShareMedia
import com.facebook.share.model.ShareMediaContent
import com.facebook.share.model.SharePhoto
import com.facebook.share.model.ShareVideo
import com.facebook.share.widget.ShareDialog
import com.ffi.GlideApp
import com.ffi.HomeActivity
import com.ffi.R
import com.ffi.Utils.*
import com.ffi.api.ApiClient
import com.ffi.api.WebService
import com.ffi.model.ParamAddREmoveWishItem
import com.ffi.model.ParamAddShareddata
import com.ffi.model.ResponseAddRemoveWishItem
import com.ffi.model.ResponseAddSharedData
import com.ffi.productlist.Media
import com.ffi.reviewlist.ReviewListActivity
import kotlinx.android.synthetic.main.activity_product_detail.*
import kotlinx.android.synthetic.main.layout_product_detail.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.*
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import android.content.pm.ResolveInfo
import android.net.ConnectivityManager
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import org.json.JSONException
import org.json.JSONObject


class ProductDetailFragment : Fragment(),
    View.OnClickListener {

    var productid = ""
    var variationId = ""

    public fun getInstance(productid: String, variationId: String): ProductDetailFragment {
        val mProductDetailFragment = ProductDetailFragment()
        val args = Bundle()
        args.putString(Const.PRODUCT_ID, productid)
        args.putString(Const.VARIATION_ID, variationId)
        mProductDetailFragment.setArguments(args)
        return mProductDetailFragment
    }

    lateinit var productdata: Data
    var varientsize: Int = 0

    lateinit var mprogressbar: ProgressBarHandler
    lateinit var review_list: ReviewListShortAdapter
    lateinit var varient_list: VarientListAdapter
    var arrRecyclerView: ArrayList<RecyclerView> = arrayListOf()
    var listOfVarientCombination: List<Variation>? = null
    var originalVariationList: ArrayList<VariationList>? = arrayListOf()
    var tempVarientList: ArrayList<Varient>? = arrayListOf()
    var tempMediaArrayList: List<Media>? = null
    private var mAdapter: ViewPagerImagesAdapter? = null
    private var isDialogOpen = false

    lateinit var arrVarient: HashMap<String, ArrayList<Varient>>

    private var dotsCount: Int = 0

    lateinit var btnDone: TextView
    lateinit var liDownloads: LinearLayout

    private var dots: Array<ImageView?> = emptyArray()

    var mImageResources: ArrayList<Media>? = ArrayList()
    var homeActivity: HomeActivity? = null

    var SelectedVarientId = "0"
    var isWishList = "0"
    var SelectedVarPos = ""
    private val SHARE_TO_ANY = 190

    private val WRITE_PERMISSION = 0
    private val SHARE_DESC = 10

    var sharedesc = ""

    var mDownloadTask: DownloadTask? = null
    var alreadyDownload: Boolean = false
    var imageUriArray: ArrayList<Uri> = ArrayList<Uri>()
    var videoUriArray: ArrayList<Uri> = ArrayList<Uri>()
    var bothUris: ArrayList<Uri> = ArrayList<Uri>()
    var imageUriArrayGlide: ArrayList<Uri> = ArrayList<Uri>()
//    var imageUrifb: ArrayList<Uri> = ArrayList<Uri>() //for facebook share
//    var imageUriGmail: ArrayList<Uri> = ArrayList<Uri>() //for facebook share

    lateinit var shareDialog: ShareDialog
    lateinit var callbackManager: CallbackManager
    var width: Int = 0

    var facebookid: String = ""
    var emailid: String = ""
    var firstname: String = ""
    var lastname: String = ""
    var googleid: String = ""

    private fun getArgumentData() {
        if (arguments != null) {
            if (arguments?.containsKey(Const.PRODUCT_ID) != null &&
                arguments?.containsKey(Const.PRODUCT_ID)!!
            ) {
                productid = arguments?.getString(Const.PRODUCT_ID)!!
            }
            if (arguments?.containsKey(Const.VARIATION_ID) != null &&
                arguments?.containsKey(Const.VARIATION_ID)!!
            ) {
                variationId = arguments?.getString(Const.VARIATION_ID)!!
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //    FacebookSdk.sdkInitialize(requireActivity().applicationContext)

        callbackManager = CallbackManager.Factory.create()

        shareDialog = ShareDialog(this)

        shareDialog.registerCallback(callbackManager, callback, 100)

        /* initFacebook()
        FacebookLogin()*/
    }

    private fun initFacebook() {

        callbackManager = CallbackManager.Factory.create()

        LoginManager.getInstance().registerCallback(callbackManager,
            object : FacebookCallback<LoginResult?> {
                override fun onSuccess(loginResult: LoginResult?) {

                    Log.e("///////success", "facebook")
                    // App code
                    val request: GraphRequest = GraphRequest.newMeRequest(
                        loginResult!!.accessToken,
                        object : GraphRequest.GraphJSONObjectCallback {

                            override fun onCompleted(
                                `object`: JSONObject?,
                                response: GraphResponse?
                            ) {

                                // Application code
                                try {
                                    facebookid = `object`!!.getString("id")
                                    val strdisplayname = `object`.getString("name").split(" ")
                                    if (strdisplayname.size > 1) {
                                        firstname = strdisplayname[0]
                                        lastname = strdisplayname[1]
                                    } else {
                                        firstname = strdisplayname[0]
                                    }
                                    googleid = ""
                                    emailid = `object`.getString("email")
                                    // CallApiSocialLogin()
                                } catch (e: JSONException) {
                                    e.printStackTrace()
                                    //showBottomToast("")
                                }

                            }
                        })
                    val parameters = Bundle()
                    parameters.putString("fields", "id,name,email,picture.width(300).height(300)")
                    request.parameters = parameters
                    //request.setParameters(parameters)
                    request.executeAsync()
                }

                override fun onCancel() {
                    Log.e("////res", "cancel");
                }

                override fun onError(exception: FacebookException) {
                    Log.e("////res", "error-" + exception.toString());
                }
            })
    }

    private fun FacebookLogin() {

        val connectivityManager =
            requireContext().getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {

            LoginManager.getInstance().logInWithReadPermissions(
                this, Arrays.asList(
                    "public_profile", "email"
                )
            )
        } else {
            NetworkErrorDialog(requireContext())
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val displayMetrics = DisplayMetrics()
        activity?.getWindowManager()?.getDefaultDisplay()?.getMetrics(displayMetrics)
        width = displayMetrics.widthPixels
        return inflater.inflate(R.layout.activity_product_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getArgumentData()
        arrVarient = HashMap()
        mprogressbar = ProgressBarHandler(requireActivity())

        homeActivity = activity as HomeActivity
        //homeActivity.header.visibility = View.GONE

        /*   val params = LayoutParams(
               LayoutParams.MATCH_PARENT,
               LayoutParams.WRAP_CONTENT
           )*/
        val params: ViewGroup.LayoutParams = pager.getLayoutParams()
        params.width = ViewGroup.LayoutParams.MATCH_PARENT
        params.height = (width * 1.5f).toInt()
        pager.setLayoutParams(params)

        init()
        CallapiProductDetails(true)

    }

    private fun CallapiProductDetails(isprogress: Boolean) {
        try {

            if (isInternetAvailable(requireActivity())) {
                if (isprogress)
                    mprogressbar.showProgressBar()

                val param = ParamProductDetail().apply {
                    productId = productid
                }
                val retrofit =
                    WebService.getRetrofit(requireActivity()).create(ApiClient::class.java)
                retrofit.GetProductDetails(param)
                    .enqueue(object : Callback<ResponseProductDetails> {
                        override fun onFailure(call: Call<ResponseProductDetails>, t: Throwable) {
                            mprogressbar.hideProgressBar()
                            // swf_product_details?.isRefreshing = false
                            Log.e("res", "on failer")
                        }

                        override fun onResponse(
                            call: Call<ResponseProductDetails>,
                            response: Response<ResponseProductDetails>?
                        ) {
                            Log.e("res", "on response")
                            mprogressbar.hideProgressBar()
                            //swf_product_details?.isRefreshing = false

                            if (response != null) {
                                if (response.isSuccessful) {
                                    val responseproduct = response.body()
                                    if (responseproduct != null) {
                                        if (responseproduct.status == API_SUCESS) {
                                            productdata = responseproduct.data!!
                                            if (isAdded)
                                                setData(productdata)

                                            Log.e("tagGlideImageCheck", "setData() called ")
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
                //swf_product_details?.isRefreshing = false
            }
        } catch (e: java.lang.Exception) {

        }
    }

    private fun setData(data: Data) {

        tvProductName?.text = data.Title
        tvPrice?.text = getString(R.string.currency) + " " + data.Price
        tvDesc?.text = data.Description
        tvmembership?.text = getString(R.string.currency) + " " + data.MembershipDiscount

        tempMediaArrayList = data.Media
        varientsize = data.variation?.size!!

        var dispatchTextP = ""
        dispatchTextP = data.DispatchTime!!
        /*   if (data.ProductType != null && !TextUtils.isEmpty(data.ProductType)) {
               if (data.ProductType?.equals("2") && data.VariationId != null) {
                   dispatchTextP = data.DispatchTime!!
               } else {
                   dispatchTextP = data.DispatchTime!!
               }
           }*/

        Log.e("tagGlideImageCheck", "setData() called 223")
        loadMediaAndPriceOfVariation(data.Price, data.Media, null, dispatchTextP)
        Log.e("tagGlideImageCheck", "setData() called 225")
        SelectedVarientId = data.VariationId
        isWishList = data.IsWishlist
        GlideApp.with(this)
            .load(R.drawable.ic_wish)
            .into(ivWish)

        Log.e("tagGlideImageCheck", "setData() called 230")
        //set Rating and Review
        if (data.ReviewRating.overAllRating.equals("0")) {
            tvNoReview?.visibility = View.VISIBLE
            lireviews?.visibility = View.GONE
        } else {
            tvAvgRating?.text = data.ReviewRating.overAllRating.toString()
            tvTotalRating?.text = data.ReviewRating.TotalRating.toString() + " Ratings"

            try {
                rcp1?.progress = data.ReviewRating.ratingList.get(4).Percentage.toFloat()
                rcp2?.progress = data.ReviewRating.ratingList.get(3).Percentage.toFloat()
                rcp3?.progress = data.ReviewRating.ratingList.get(2).Percentage.toFloat()
                rcp4?.progress = data.ReviewRating.ratingList.get(1).Percentage.toFloat()
                rcp5?.progress = data.ReviewRating.ratingList.get(0).Percentage.toFloat()
            } catch (e: IndexOutOfBoundsException) {
                e.printStackTrace()
            }

            Log.e("tagGlideImageCheck", "setData() called 250")
            rvReview?.apply {
                layoutManager = LinearLayoutManager(context)
                review_list = ReviewListShortAdapter(requireActivity())
                review_list.addModels(data.ReviewRating.comments)
                adapter = review_list
            }

            tvNoReview?.visibility = View.GONE
            lireviews?.visibility = View.VISIBLE
        }

        Log.e("tagGlideImageCheck", "setData() called 261")


        //Set Varient of Product Dynamiccly
//        SelectedVarientId = data.variation.get(0).VariationId

        listOfVarientCombination = data.variation
        originalVariationList?.clear()
        originalVariationList = data.VariationList
        clearAndResetLayout(originalVariationList!!)

        if (data.ProductType != null && !TextUtils.isEmpty(data.ProductType)) {
            if (data.ProductType?.equals("2") && data.VariationId != null) {
                SelectedVarientId = data.VariationId.toString()
                Log.e("tagProduct", "SelectedVarientId " + SelectedVarientId)
            } else {
                //  tvProdustDetailDespechDesc?.text = data.DispatchTime!!
            }
        }

        checkValidVarient(false)
        checkInWishList()
        //checkGlideImages()
        //   checkPermissionAndDownloadImages()

        /*val rvsize = arrRecyclerView.size
        for (i in 0 until rvsize) {
            if (i == 0) {
                arrRecyclerView.get(i).visibility = View.VISIBLE
            } else {
                arrRecyclerView.get(i).visibility = View.GONE
            }
        }*/

        mainlayout?.visibility = View.VISIBLE
    }


    private fun clearAndResetLayout(originalVariationList: List<VariationList>) {
        li_varient.removeAllViews()
        arrRecyclerView.clear()
        for (i in originalVariationList.indices) {
            val mTextView = TextView(requireActivity())
            val mName =
                (originalVariationList.get(i).name?.substring(0, 1)?.toUpperCase(Locale.ROOT)
                    ?: "") + originalVariationList.get(i).name?.substring(1)
            Log.e("//////name", mName + "...")
            mTextView.setText(mName)
            mTextView.setTextColor(ContextCompat.getColor(requireActivity(), R.color.color_black))
            mTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f)
            mTextView.setTypeface(Typeface.DEFAULT_BOLD)
            val params: LinearLayout.LayoutParams =
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            params.setMargins(20, 5, 10, 2)
            mTextView.setLayoutParams(params)

            val mRecyclerView = RecyclerView(requireActivity())

            var mVarientListAdapterNew: VarientListAdapterNew? =
                VarientListAdapterNew(
                    requireActivity(),
                    i,
                    originalVariationList.get(i).iD,
                    originalVariationList.get(i),
                    object : VarientListClickInterfaceNew {
                        override fun varientClickedPosItem(
                            clickedPosition: Int,
                            mRecyclerViewPosition: Int,
                            mVariationListID: Int,
                            mClickedVarient: Varient?
                        ) {
                            if (arrRecyclerView.size > 1) {
                                reloadAllVarientWithNewValue(
                                    clickedPosition,
                                    mRecyclerViewPosition,
                                    mVariationListID,
                                    mClickedVarient
                                )
                            }
                            checkValidVarient(false)
//                            checkInWishList()
                        }
                    })

            mRecyclerView.layoutManager =
                LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
            val paramsRecyclerView: LinearLayout.LayoutParams =
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            paramsRecyclerView.setMargins(20, 5, 15, 15)
            mRecyclerView.setLayoutParams(paramsRecyclerView)
            mRecyclerView.adapter = mVarientListAdapterNew

            li_varient.addView(mTextView)
            arrRecyclerView.add(mRecyclerView)
            li_varient.addView(mRecyclerView)
        }

        if (originalVariationList.size == 1) {
            for ((i, mRecyclerView) in arrRecyclerView.withIndex()) {
                val mVarientListAdapterNew: VarientListAdapterNew? =
                    mRecyclerView.adapter as VarientListAdapterNew
                var mList = mVarientListAdapterNew?.mVariationList?.list!!
                for (mItem in mList) {
                    for (mVariationID in listOfVarientCombination!!) {
                        if (mVariationID.Code != null &&
                            !TextUtils.isEmpty(mVariationID.Code)
                        ) {
                            if (mItem.getiD().toString() == mVariationID.Code) {
                                mItem.set_isEnable(true)
                                if (mVariationID.Quantity != null &&
                                    !TextUtils.isEmpty(mVariationID.Quantity)
                                ) {
                                    mItem.quantity = mVariationID.Quantity.toInt()
                                } else {
                                    mItem.quantity = 0
                                }
                                mItem.isQuantityFound = true
                            }
                        }
                    }
                }

                mVarientListAdapterNew.notifyDataSetChanged()
            }
        } else {

        }

    }


    private fun reloadAllVarientWithNewValue(
        clickedPosition: Int,
        mRecyclerViewPosition: Int,
        mVariationListID: Int,
        mClickedVarient: Varient?
    ) {
        SelectedVarientId = "0"
        var mOldSelectedVariationListID: ArrayList<Int> = arrayListOf()
        for (mRecyclerView in arrRecyclerView) {
            val mVarientListAdapterNew: VarientListAdapterNew? =
                mRecyclerView.adapter as VarientListAdapterNew
            for (mItem in mVarientListAdapterNew?.mVariationList?.list!!) {
                Log.e("tagSelectedQuan2", "mItem " + mItem.value)
                if (mItem.selected) {
                    if (!mOldSelectedVariationListID.contains(mItem.getiD())) {
                        mOldSelectedVariationListID.add(mItem.getiD())
                    }
                }
            }
        }

        val mOldVariationID = mClickedVarient?.getiD()
        for ((i, mRecyclerView) in arrRecyclerView.withIndex()) {
            val mVarientListAdapterNew: VarientListAdapterNew? =
                mRecyclerView.adapter as VarientListAdapterNew
            for (mItem in mVarientListAdapterNew?.mVariationList?.list!!) {
                if (mItem.getiD() == mClickedVarient?.getiD()) {

                } else {
                    mItem.set_isEnable(false)
                    mItem.setSelected(false)
                }
            }
            mVarientListAdapterNew.notifyDataSetChanged()
        }
        var mContainVariationListID: ArrayList<Int> = arrayListOf()
        for (mVariation in listOfVarientCombination!!) {
            val mTypeList = mVariation.Type
            var isTypeContainCurrentVariant = false
            for (mType in mTypeList) {
                if (mType.VariantId == mOldVariationID) {
                    isTypeContainCurrentVariant = true
                }
            }

            if (isTypeContainCurrentVariant) {
                for (mType in mTypeList) {
                    if (!mContainVariationListID.contains(mType.VariantId)) {
                        mContainVariationListID.add(mType.VariantId)
                    }
                }
            }
        }
        var mCode = ""
        var mCodeReverse = ""
        for ((i, mRecyclerView) in arrRecyclerView.withIndex()) {
            val mVarientListAdapterNew: VarientListAdapterNew? =
                mRecyclerView.adapter as VarientListAdapterNew
            var mList = mVarientListAdapterNew?.mVariationList?.list!!
            for (mItem in mList) {
                for (mVariationID in mContainVariationListID) {
                    if (mItem.getiD() == mVariationID) {
                        mItem.set_isEnable(true)
                        for (mOldSelectedItemID in mOldSelectedVariationListID) {
                            if (mItem.getiD() == mOldSelectedItemID) {
                                mCode = mItem.getiD().toString() + mCode
                                mCodeReverse = mCodeReverse + mItem.getiD().toString()
                                mItem.setSelected(true)
                                break
                            }
                        }
                    }
                }
            }

            mVarientListAdapterNew.notifyDataSetChanged()
        }

        Log.e("tagSelectedQuan", " ")
        Log.e("tagSelectedQuan", "mCode " + mCode)
        Log.e("tagSelectedQuan", "mCodeReverse " + mCodeReverse)

        var mFinalQuantity = 0
        var mIsFinalQuantityFound = false
        if (mCode != null && !TextUtils.isEmpty(mCode)) {
            for (mVariation in listOfVarientCombination!!) {
                if (mVariation.Code != null && !TextUtils.isEmpty(mVariation.Code) &&
                    (mVariation.Code.equals(mCode) || mVariation.Code.equals(mCodeReverse))
                ) {
                    if (mVariation.Quantity != null && !TextUtils.isEmpty(mVariation.Quantity)) {
                        mFinalQuantity = mVariation.Quantity.toInt()
                    } else {
                        mFinalQuantity = 0
                    }
                    mIsFinalQuantityFound = true
                    break
                }
            }
        }

        for ((i, mRecyclerView) in arrRecyclerView.withIndex()) {
            val mVarientListAdapterNew: VarientListAdapterNew? =
                mRecyclerView.adapter as VarientListAdapterNew
            var mList = mVarientListAdapterNew?.mVariationList?.list!!
            for (mItem in mList) {
                for (mVariationID in mContainVariationListID) {
                    if (mItem.getiD() == mVariationID) {
                        mItem.set_isEnable(true)
                        for (mOldSelectedItemID in mOldSelectedVariationListID) {
                            if (mItem.getiD() == mOldSelectedItemID) {
                                mItem.quantity = mFinalQuantity
                                mItem.isQuantityFound = mIsFinalQuantityFound
                            }
                        }
                    }
                }
            }

            mVarientListAdapterNew.notifyDataSetChanged()
        }

        // clearAndResetLayout(originalVariationList!!)

    }


    fun checkValidVarient(shouldshowMessage: Boolean): Boolean {
        Log.e("tagGlideImageCheck", "setData() called 444 - " + productdata.toString())
        var mSelectedVariationMultipleID: String = ""
        var isAnyVarientSelected = false
        Log.e("tagGlideImageCheck", "setData() called 447")
        for (mVariationMain in originalVariationList!!) {
            Log.e("tagGlideImageCheck", "setData() called 449")
            isAnyVarientSelected = false
            Log.e("tagGlideImageCheck", "setData() called 451" + mVariationMain.list?.size)

            try {
                for (mVariationListItem in mVariationMain.list!!) {
                    Log.e("tagGlideImageCheck", "setData() called 453")
                    if (mVariationListItem.selected) {
                        Log.e("tagGlideImageCheck", "setData() called 454")
                        isAnyVarientSelected = true
                        Log.e(
                            "tagGlideImageCheck",
                            "setData() called 456" + mSelectedVariationMultipleID
                        )
                        if (TextUtils.isEmpty(mSelectedVariationMultipleID)) {
                            Log.e("tagGlideImageCheck", "setData() called 459")
                            mSelectedVariationMultipleID = "" + mVariationListItem.getiD()
                        } else {
                            Log.e("tagGlideImageCheck", "setData() called 462")
                            mSelectedVariationMultipleID =
                                mSelectedVariationMultipleID + "," + mVariationListItem.getiD()
                        }
                        break
                    }
                }
            } catch (e: NullPointerException) {
                Log.e("tagGlideImageCheck", "setData() called 477")
                e.printStackTrace()
            }

            Log.e(
                "tagVarient",
                "isAnyVarientSelected " + isAnyVarientSelected + " " + mVariationMain.name
            )
            if (!isAnyVarientSelected) {
                if (shouldshowMessage) {
                    activity?.showToast(
                        getString(R.string.alert_varient) + " " + mVariationMain.name,
                        Const.ALERT
                    )
                }
                return false
            }
        }

        Log.e("tagVarient", "isAnyVarientSelected " + isAnyVarientSelected + " ***** ")
        if (isAnyVarientSelected) {
            for (mVariation in listOfVarientCombination!!) {
                val mTypeList = mVariation.Type
                var mSelectedVariationTypeID: String = ""
                for (mType in mTypeList) {
                    if (TextUtils.isEmpty(mSelectedVariationTypeID)) {
                        mSelectedVariationTypeID = "" + mType.VariantId
                    } else {
                        mSelectedVariationTypeID = mSelectedVariationTypeID + "," + mType.VariantId
                    }
                }
                Log.e("tagVarient", "mSelectedVariationMultipleID " + mSelectedVariationMultipleID)
                Log.e("tagVarient", "mSelectedVariationTypeID " + mSelectedVariationTypeID)
                if (mSelectedVariationTypeID.equals(mSelectedVariationMultipleID)) {
                    SelectedVarientId = mVariation.VariationId
                    Log.e("tagVarient", "SelectedVarientId " + SelectedVarientId)
//                    checkInWishList()
                    loadMediaAndPriceOfVariation(
                        mVariation.Price,
                        null,
                        mVariation.Media,
                        mVariation.DispatchTime
                    )
                    break
                }
            }
        }
        return true
    }

    private fun loadMediaAndPriceOfVariation(
        mPrice: String,
        mMediaArraylist: List<Media>?,
        mMedia: Media?, dispatchTextText: String
    ) {
        tvProdustDetailDespechDesc?.text = dispatchTextText
        tvPrice.text = getString(R.string.currency) + " " + mPrice
        mImageResources?.clear()
        mImageResources = ArrayList()
        if (mMediaArraylist != null) {
            mImageResources?.addAll(mMediaArraylist)
        } else {
            mImageResources?.add(mMedia!!)
        }
        if (mImageResources?.size!! >= 1) {
            mAdapter = ViewPagerImagesAdapter(requireActivity(), mImageResources!!)
            pager.adapter = mAdapter
            pager.currentItem = 0
            //pager.offscreenPageLimit = 1
            setUiPageViewController()
            pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageScrollStateChanged(position: Int) {
                    super.onPageScrollStateChanged(position)

                }

                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    for (i in 0 until dotsCount) {
                        dots[i]?.setImageDrawable(resources.getDrawable(R.drawable.banner_nonselecteditem_dot))
                    }

                    dots[position]?.setImageDrawable(resources.getDrawable(R.drawable.banner_selecteditem_dot))
                }
            })

            val displayMetrics = DisplayMetrics()
            activity?.windowManager?.defaultDisplay?.getMetrics(displayMetrics)
            val screenWidth: Int = displayMetrics.widthPixels
            val screenHeight: Int = displayMetrics.heightPixels
//            mAdapter?.mFirstWidth = (screenWidth * 60) / 100
            mAdapter?.mFirstWidth = screenWidth
            mAdapter?.notifyDataSetChanged()

        }
    }

    fun checkInWishList() {
        try {
            Log.e("tagVarient", " ")
            Log.e("tagVarient", "checkInWishList() called")
//            var isWishListContain = checkWishlistContainItem()
            var isWishListContain = false
            if (isWishList == "1") {
                isWishListContain = true
            }
            Log.e("tagVarient", "checkInWishList() called isWishListContain " + isWishListContain)
            if (isWishListContain) {
                GlideApp.with(this@ProductDetailFragment)
                    .load(R.drawable.ic_wish_fill)
                    .into(ivWish)
            } else {
                GlideApp.with(this@ProductDetailFragment)
                    .load(R.drawable.ic_wish)
                    .into(ivWish)
            }
        } catch (e: Exception) {
        }
    }


    private fun init() {
        tv_old_price.paintFlags = tv_old_price.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        setListener()
    }

    private fun setListener() {
        ivback?.setOnClickListener(this)
        tvTotalRating?.setOnClickListener(this)
        btnAddCart?.setOnClickListener(this)
        ivWish?.setOnClickListener(this)
        li_shareother?.setOnClickListener(this)
        li_sharefb?.setOnClickListener(this)
        li_sharewhtsapp?.setOnClickListener(this)
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.li_shareother -> {
//                if(App.checkManageStorage(requireContext())){
//                    return
//                }

                if (isDialogOpen)
                    return
                if (getUserId().isEmpty()) {
                    /*val intent = Intent(activity, LoginActivity::class.java)
                    activity?.startActivity(intent)
                    App.isRedirect = true*/
                    activity?.showLoginMsg()
                } else {
                    isDialogOpen = true
                    DialogShare(productdata!!, Const.OTHERS)
                }
            }

            R.id.li_sharefb -> {
//                if(App.checkManageStorage(requireContext())){
//                    return
//                }
                if (isDialogOpen)
                    return
                if (getUserId().isEmpty()) {
                    /*val intent = Intent(activity, LoginActivity::class.java)
                    activity?.startActivity(intent)
                    App.isRedirect = true*/
                    activity?.showLoginMsg()
                } else {
                    isDialogOpen = true
                    DialogShare(productdata!!, Const.FACEBOOK)
                }
            }

            R.id.li_sharewhtsapp -> {
//                if(App.checkManageStorage(requireContext())){
//                    return
//                }
                if (isDialogOpen)
                    return
                if (getUserId().isEmpty()) {
                    /*val intent = Intent(activity, LoginActivity::class.java)
                    activity?.startActivity(intent)
                    App.isRedirect = true*/
                    activity?.showLoginMsg()
                } else {
                    isDialogOpen = true
                    DialogShare(productdata!!, Const.WHATSAPP)
                }
            }

            R.id.ivWish -> {
                if (getUserId().isEmpty()) {
                    /*val intent = Intent(activity, LoginActivity::class.java)
                    activity?.startActivity(intent)
                    App.isRedirect = true*/
                    activity?.showLoginMsg()
                } else {
                    if (productdata != null &&
                        productdata?.ProductType != null && !TextUtils.isEmpty(productdata?.ProductType)
                    ) {
//                        if (productdata?.ProductType?.equals("2")!!) {
////                            if (checkValidVarient(true)) {
////
////                            }
//
//                        } else {
//                            wishlistWithSelectionIDZero()
//                        }

                        var isWishListContain = false
                        if (isWishList == "1") {
                            isWishListContain = true
                        }
                        if (isWishListContain) {
                            GlideApp.with(this@ProductDetailFragment)
                                .load(R.drawable.ic_wish)
                                .into(ivWish)
                            CallApiAddWishList("0")
                        } else {
                            GlideApp.with(this@ProductDetailFragment)
                                .load(R.drawable.ic_wish_fill)
                                .into(ivWish)
                            CallApiAddWishList("1")
                        }
                    } else {
                        wishlistWithSelectionIDZero()
                    }
                }
            }


            R.id.tvTotalRating -> {
                tvTotalRating.isEnabled = false
                val intent = Intent(requireActivity(), ReviewListActivity::class.java)
                intent.putExtra(Const.PRODUCT_ID, productid)
                startActivity(intent)
            }
            R.id.ivback -> {
                homeActivity?.selectLastMenu()
                fragmentManager?.popBackStack()
            }


            R.id.btnAddCart -> {
                if (getUserId().isEmpty()) {
                    /* val intent = Intent(activity, LoginActivity::class.java)
                     activity?.startActivity(intent)
                     App.isRedirect = true*/
                    activity?.showLoginMsg()
                } else {
                    if (productdata != null &&
                        productdata?.ProductType != null && !TextUtils.isEmpty(productdata?.ProductType)
                    ) {
                        if (productdata?.ProductType?.equals("2")!!) {
                            if (checkValidVarient(true)) {
                                CallApiAddToCart()
                            }
                        } else {
                            if (productdata?.VariationId != null) {
                                SelectedVarientId = productdata?.VariationId.toString()
                            } else {
                                SelectedVarientId = "0"
                            }
                            CallApiAddToCart()
                        }
                    }
                }
            }
        }
    }

    private fun wishlistWithSelectionIDZero() {
//        if (productdata?.VariationId != null) {
//            SelectedVarientId = productdata?.VariationId.toString()
//        } else {
//            SelectedVarientId = "0"
//        }
//        var isWishListContain = checkWishlistContainItem()
        var isWishListContain = false
        if (isWishList == "1") {
            isWishListContain = true
        }
        if (isWishListContain) {
            GlideApp.with(this@ProductDetailFragment)
                .load(R.drawable.ic_wish)
                .into(ivWish)
            CallApiAddWishList("0")
        } else {
            GlideApp.with(this@ProductDetailFragment)
                .load(R.drawable.ic_wish_fill)
                .into(ivWish)
            CallApiAddWishList("1")
        }
    }

    private fun checkWishlistContainItem(): Boolean {
        try {
            Log.e("tagVarient", "checkWishlistContainItem() called " + SelectedVarientId)
            if (productdata != null && productdata?.WishlistVariationIds != null) {
                Log.e(
                    "tagVarient",
                    "checkInWishList() called " + productdata?.WishlistVariationIds?.size
                )
                for (mStr in productdata?.WishlistVariationIds!!) {
                    Log.e(
                        "tagVarient",
                        "checkInWishList() called checkWishlistContainItem mStr " + mStr
                    )
                }
                if (productdata?.WishlistVariationIds?.contains(SelectedVarientId) != null &&
                    productdata?.WishlistVariationIds?.contains(SelectedVarientId)!!
                ) {
                    return true
                } else {
                    return false
                }
            }
        } catch (e: Exception) {
            return false
        }
        return false
    }

    private fun CallApiAddWishList(wishlistStatus: String) {
        if (isInternetAvailable(requireActivity())) {
            mprogressbar.showProgressBar()
//            if (SelectedVarientId == null || (SelectedVarientId != null && TextUtils.isEmpty(
//                    SelectedVarientId
//                ))
//            ) {
//                SelectedVarientId = "0"
//            }
            val param = ParamAddREmoveWishItem().apply {
                productId = productid
//                variationId = SelectedVarientId
                variationId = "0"
                status = wishlistStatus
            }
            val retrofit = WebService.getRetrofit(requireActivity()).create(ApiClient::class.java)
            retrofit.AddRemoveWishItem(param)
                .enqueue(object : Callback<ResponseAddRemoveWishItem> {
                    override fun onFailure(call: Call<ResponseAddRemoveWishItem>, t: Throwable) {
                    }

                    override fun onResponse(
                        call: Call<ResponseAddRemoveWishItem>,
                        response: Response<ResponseAddRemoveWishItem>?
                    ) {
                        if (response != null) {
                            if (response.isSuccessful) {
                                val responseproduct = response.body()
                                if (responseproduct != null) {
                                    if (responseproduct.status == API_SUCESS) {
                                        CallapiProductDetails(false)
                                    } else {
                                        mprogressbar.hideProgressBar()
                                    }
                                }
                            } else {
                                mprogressbar.hideProgressBar()
                                requireActivity().showToast(
                                    getString(R.string.msg_common_error),
                                    Const.ALERT
                                )
                            }
                        } else {
                            mprogressbar.hideProgressBar()
                        }
                    }
                })
        } else {
            mprogressbar.hideProgressBar()
            NetworkErrorDialog(requireActivity())
        }
    }

    private fun setUiPageViewController() {
        dotsCount = mAdapter?.itemCount ?: 0
        dots = arrayOfNulls<ImageView>(this.dotsCount)

        viewPagerCountDots.removeAllViews()

        for (i in 0 until dotsCount) {
            dots[i] = ImageView(requireActivity())
            dots[i]?.setImageDrawable(resources.getDrawable(R.drawable.banner_nonselecteditem_dot))

            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(4, 0, 4, 0)

            viewPagerCountDots.addView(dots[i], params)
        }
        if (dots.size >= 1)
            dots[0]?.setImageDrawable(resources.getDrawable(R.drawable.banner_selecteditem_dot))
    }


    fun CallApiAddToCart() {
        if (isInternetAvailable(requireActivity())) {
            mprogressbar.showProgressBar()

            val param = ParamAddProductNew().apply {
                productId = productid
                variationId = SelectedVarientId
                addFromWishlist = "0"
                status = Const.STATUS_ADD_CART
            }
            param.addFromWishlist = "0"

            val retrofit = WebService.getRetrofit(requireActivity()).create(ApiClient::class.java)
            retrofit.AddProducts(param)
                .enqueue(object : Callback<ResponseAddProduct> {
                    override fun onFailure(call: Call<ResponseAddProduct>, t: Throwable) {
                        mprogressbar.hideProgressBar()
                    }

                    override fun onResponse(
                        call: Call<ResponseAddProduct>,
                        response: Response<ResponseAddProduct>?
                    ) {
                        mprogressbar.hideProgressBar()
                        if (response != null) {
                            if (response.isSuccessful) {
                                val responseproduct = response.body()
                                if (responseproduct != null) {
                                    if (responseproduct.status == API_SUCESS) {
                                        if (isAdded) {
                                            storeCartItem(getCartItem()?.plus(1)!!)
                                            Log.e("res", "744")
                                            /*     homeActivity.setCartScreen()
                                                 homeActivity.menu.findItem(R.id.menu_cart)
                                                     .setChecked(true)*/
                                            requireActivity().showToast(
                                                getString(R.string.msg_product_add_cart),
                                                Const.TOAST
                                            )
                                        }
                                    } else if (responseproduct.status == API_DATA_NOT_AVAILBLE) {
                                        requireActivity().showToast(
                                            responseproduct.message,
                                            Const.ALERT
                                        )
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

    override fun onResume() {
        super.onResume()
        tvTotalRating.isEnabled = true
        /*tvallreview.isEnabled = true
        ivarrow.isEnabled = true*/
    }


    fun DialogShare(model: Data, sourceapp: String) {
        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                WRITE_PERMISSION
            )
            isDialogOpen = false
        } else {
            var hasAnyVideo = false
            for (mediafile in model.Media) {
                if (mediafile.MediaTypeId.equals(Const.MEDIA_VIDEO)) {
                    hasAnyVideo = true
                }
            }


            var hasAnyImage = false
            for (mediafile in model.Media) {
                if (mediafile.MediaTypeId.equals(Const.MEDIA_IMAGE)) {
                    Log.e("hasImage", mediafile.Media!!)
                    hasAnyImage = true
                }
            }


            val dialog = Dialog(requireActivity())
            val window = dialog.window
            window!!.setLayout(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            window.setGravity(Gravity.CENTER)
            dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
            dialog.setContentView(R.layout.dailog_share)

            val ivclose = dialog.findViewById<ImageView>(R.id.ivclose)
            btnDone = dialog.findViewById<TextView>(R.id.btn_done)
            liDownloads = dialog.findViewById(R.id.li_downloading)

            val tvdesc = dialog.findViewById<TextView>(R.id.tvDesc)
            val tvshareimg = dialog.findViewById<TextView>(R.id.tvshareimg)
            val tvaddmargin = dialog.findViewById<TextView>(R.id.tvaddmargin)
            val tvsharevideo = dialog.findViewById<TextView>(R.id.tvsharevideo)

            val rdDesc = dialog.findViewById<CheckBox>(R.id.rdDesc)
            val rdimg = dialog.findViewById<CheckBox>(R.id.rdimage)
            val rdmargin = dialog.findViewById<CheckBox>(R.id.rdmargin)
            val rdvideo = dialog.findViewById<CheckBox>(R.id.rdvideo)
            var edt_margin = dialog.findViewById<EditText>(R.id.edt_margin)


            val llSaheVideo = dialog.findViewById<LinearLayout>(R.id.llSaheVideo)
            if (hasAnyVideo) {
                llSaheVideo.visibility = View.VISIBLE
            } else {
                llSaheVideo.visibility = View.GONE
            }

            val llShareImage = dialog.findViewById<LinearLayout>(R.id.llShareImage)
            if (hasAnyImage) {
                llShareImage.visibility = View.VISIBLE
            } else {
                llShareImage.visibility = View.GONE
            }

            clearAndRexecuteDownloader(model.Media)

            tvdesc?.setOnClickListener {
                rdDesc.isChecked = !rdDesc.isChecked
            }

            tvshareimg?.setOnClickListener {
                rdimg.isChecked = !rdimg.isChecked
            }

            tvaddmargin?.setOnClickListener {
                rdmargin.isChecked = !rdmargin.isChecked
            }

            tvsharevideo?.setOnClickListener {
                rdvideo?.isChecked = !rdvideo.isChecked
            }
            rdvideo?.setOnCheckedChangeListener { buttonView, isChecked ->

            }

            rdDesc?.setOnCheckedChangeListener { buttonView, isChecked ->
                sharedesc = ""

                if (isChecked) {
                    sharedesc = model.Title + "\n" +
                            model.Description
                }
            }



            btnDone?.setOnClickListener {
                if (!rdimg.isChecked && !rdDesc.isChecked && !rdvideo.isChecked) {

                    if (hasAnyImage && !hasAnyVideo) {
                        activity?.showToast(getString(R.string.err_select_to_share), Const.TOAST)
                    } else if (!hasAnyImage && hasAnyVideo) {
                        activity?.showToast(getString(R.string.err_select_to_share3), Const.TOAST)
                    } else {
                        activity?.showToast(getString(R.string.err_select_to_share2), Const.TOAST)
                    }
                    return@setOnClickListener
                }

                if (rdimg.isChecked) {
                    if (alreadyDownload) {
                        if (imageUriArray.size <= 0) {
                            activity?.showToast(
                                getString(R.string.err_img_not_appearing),
                                Const.TOAST
                            )
                            return@setOnClickListener
                        }
                    } else {
                        if (productdata != null && productdata?.Media != null &&
                            imageUriArrayGlide.size == productdata?.Media?.size
                        ) {
                            if (imageUriArrayGlide.size <= 0) {
                                activity?.showToast(
                                    getString(R.string.err_img_not_appearing),
                                    Const.TOAST
                                )
                                return@setOnClickListener
                            }
                        } else {
                            if (imageUriArray.size <= 0) {
                                activity?.showToast(
                                    getString(R.string.err_img_not_appearing),
                                    Const.TOAST
                                )
                                return@setOnClickListener
                            }
                        }
                    }
                }
                CallApiSharedData(productid)
                if (rdmargin.isChecked) {
                    val strmargin = edt_margin.text.toString()
                    if (strmargin.isEmpty()) {
                        activity?.showToast(getString(R.string.err_enter_margin), Const.TOAST)
                        return@setOnClickListener
                    } else {
                        sharedesc += "\n" + "*Price " + getString(R.string.currency) + " " + (strmargin.toFloat() + model.Price.replace(
                            ",",
                            ""
                        ).toFloat()) + "*"
                    }
                }

                val clipboard: ClipboardManager =
                    activity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip: ClipData = ClipData.newPlainText("copy", sharedesc)
                clipboard.setPrimaryClip(clip)


                if (sourceapp.equals(Const.WHATSAPP)) {

                    if (rdimg.isChecked) {

                        ShareOnWhatsapp(true, rdvideo.isChecked)
                    } else {
                        ShareOnWhatsapp(false, rdvideo.isChecked)
                    }
                } else if (sourceapp.equals(Const.FACEBOOK)) {
                    ShareOnFacebook(rdimg.isChecked, rdvideo.isChecked)
                } else if (sourceapp.equals(Const.OTHERS)) {
//                    ShareOnAnyApp(model)
                    if (rdimg.isChecked)
                        ShareOnAnyApp(true, rdvideo.isChecked)
                    else
                        ShareOnAnyApp(false, rdvideo.isChecked)
                }
                isDialogOpen = false
                dialog.cancel()
            }

            ivclose.setOnClickListener {
                isDialogOpen = false
                dialog.dismiss()
            }

            dialog.setCancelable(false)
            dialog.setCanceledOnTouchOutside(false)
            dialog.show()
        }
    }

    private fun clearAndRexecuteDownloader(mMedia: List<Media>) {
        if (!alreadyDownload) {
            imageUriArray.clear()
            videoUriArray.clear()

            if (mDownloadTask != null) {
                mDownloadTask?.cancel(true)
                mDownloadTask = null
            }
            mDownloadTask = DownloadTask(
                mMedia, productid, true
            )
            mDownloadTask?.execute()
        } else {
            if (this@ProductDetailFragment::btnDone.isInitialized) {
                btnDone.visibility = View.VISIBLE
                liDownloads.visibility = View.GONE
            }
        }
    }

    private fun CallApiSharedData(id: String) {
        if (isInternetAvailable(requireActivity())) {
            val param = ParamAddShareddata().apply {
                referenceId = id
                typeId = Const.PRODUCT_SHARE
            }
            val retrofit = WebService.getRetrofit(requireActivity()).create(ApiClient::class.java)
            retrofit.AddSharedProducts(param)
                .enqueue(object : Callback<ResponseAddSharedData> {
                    override fun onFailure(call: Call<ResponseAddSharedData>, t: Throwable) {
                    }

                    override fun onResponse(
                        call: Call<ResponseAddSharedData>,
                        response: Response<ResponseAddSharedData>?
                    ) {
                        mprogressbar.hideProgressBar()
                    }
                })

        } else {
            NetworkErrorDialog(requireActivity())
        }
    }

    private fun ShareOnWhatsapp(isimg: Boolean, isVideo: Boolean) {

        val baseActivity = BaseActivity()
        val isWhatsUpInstalled =
            baseActivity.isAppInstalled(context, getString(R.string.pckg_whtsapp))
        val isWhatsUpBusinessInstalled =
            baseActivity.isAppInstalled(context, getString(R.string.pckg_whtsapp_business))

        if (isWhatsUpInstalled) {
            shareDataSocial(getString(R.string.pckg_whtsapp), isimg, isVideo)
        } else if (isWhatsUpBusinessInstalled) {
            shareDataSocial(getString(R.string.pckg_whtsapp_business), isimg, isVideo)
        } else {
            activity?.showToast(getString(R.string.whatsapp_no_installed), Const.ALERT)
        }
    }

    private fun shareDataSocial(pckgName: String, isimg: Boolean, isVideo: Boolean) {
        Log.e("////", "pckgName//" + pckgName)

        val whatsappIntent = Intent(Intent.ACTION_SEND)
        whatsappIntent.setPackage(pckgName)
        activity?.showBottomToast(getString(R.string.msg_desc_copeied))
        try {
            Log.e("share", "image: " + isimg.toString())
            Log.e("share", "video: " + isVideo.toString())

            if (isimg && !isVideo) {
                whatsappIntent.type = "image/png" //png
                // whatsappIntent.type = "*/*" //png

                Log.e(
                    "tagGlideImageCheck",
                    "ShareOnWhatsapp() called imageUriArrayGlide " + imageUriArrayGlide.size
                )
                for (mImage in imageUriArrayGlide) {
                    Log.e(
                        "tagGlideImageCheck",
                        "ShareOnWhatsapp() called imageUriArrayGlide mImage uri is " + mImage
                    )
                }
                if (alreadyDownload) {
                    Log.e(
                        "tagGlideImageCheck",
                        "ShareOnWhatsapp() whatsappIntent.putExtra(Intent.EXTRA_STREAM, imageUriArray)"
                    )
                    try {
                        whatsappIntent.putExtra(Intent.EXTRA_STREAM, imageUriArray)
                    } catch (e: Exception) {
                        Log.e("Stream", e.message.toString())
                    }
                } else {
                    if (productdata != null && productdata?.Media != null &&
                        imageUriArrayGlide.size == productdata?.Media?.size
                    ) {
                        whatsappIntent.putExtra(Intent.EXTRA_STREAM, imageUriArrayGlide)
                    } else {
                        whatsappIntent.putExtra(Intent.EXTRA_STREAM, imageUriArray)
                    }
                }
                whatsappIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                startActivityForResult(whatsappIntent, SHARE_DESC)
            } else if (!isimg && isVideo) {
                //  whatsappIntent.type = "image/png" //png
                whatsappIntent.type = "*/*" //png

                Log.e(
                    "tagGlideImageCheck",
                    "ShareOnWhatsapp() called imageUriArrayGlide " + imageUriArrayGlide.size
                )
                for (mImage in imageUriArrayGlide) {
                    Log.e(
                        "tagGlideImageCheck",
                        "ShareOnWhatsapp() called imageUriArrayGlide mImage uri is " + mImage
                    )
                }
                if (alreadyDownload) {
                    Log.e(
                        "tagGlideImageCheck",
                        "ShareOnWhatsapp() whatsappIntent.putExtra(Intent.EXTRA_STREAM, imageUriArray)"
                    )
                    try {
                        whatsappIntent.putExtra(Intent.EXTRA_STREAM, videoUriArray)
                    } catch (e: Exception) {
                        Log.e("Stream", e.message.toString())
                    }
                } else {
                    if (productdata != null && productdata?.Media != null &&
                        imageUriArrayGlide.size == productdata?.Media?.size
                    ) {
                        whatsappIntent.putExtra(Intent.EXTRA_STREAM, imageUriArrayGlide)
                    } else {
                        whatsappIntent.putExtra(Intent.EXTRA_STREAM, videoUriArray)
                    }
                }
                whatsappIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                startActivityForResult(whatsappIntent, SHARE_DESC)
            } else if (isimg && isVideo) {
                //  whatsappIntent.type = "image/png" //png
                whatsappIntent.type = "*/*" //png

                Log.e(
                    "tagGlideImageCheck",
                    "ShareOnWhatsapp() called imageUriArrayGlide " + imageUriArrayGlide.size
                )
                for (mImage in imageUriArrayGlide) {
                    Log.e(
                        "tagGlideImageCheck",
                        "ShareOnWhatsapp() called imageUriArrayGlide mImage uri is " + mImage
                    )
                }
                bothUris.clear()
                bothUris.addAll(imageUriArray)
                bothUris.addAll(videoUriArray)


                if (alreadyDownload) {
                    Log.e(
                        "tagGlideImageCheck",
                        "ShareOnWhatsapp() whatsappIntent.putExtra(Intent.EXTRA_STREAM, imageUriArray)"
                    )
                    try {
                        whatsappIntent.putExtra(Intent.EXTRA_STREAM, bothUris)
                    } catch (e: Exception) {
                        Log.e("Stream", e.message.toString())
                    }
                } else {
                    if (productdata != null && productdata?.Media != null &&
                        imageUriArrayGlide.size == productdata?.Media?.size
                    ) {
                        whatsappIntent.putExtra(Intent.EXTRA_STREAM, imageUriArrayGlide)
                    } else {
                        whatsappIntent.putExtra(Intent.EXTRA_STREAM, bothUris)
                    }
                }
                whatsappIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                startActivityForResult(whatsappIntent, SHARE_DESC)
            } else {
                whatsappIntent.type = "text/plain"
                whatsappIntent.putExtra(Intent.EXTRA_TEXT, sharedesc)
                activity?.startActivity(whatsappIntent)
            }
        } catch (ex: ActivityNotFoundException) {
            activity?.showToast(getString(R.string.whatsapp_no_installed), Const.ALERT)
        }
    }

    inner class DownloadTask(
        val imgurl: List<Media>,
        val id: String,
        val isLast: Boolean
    ) : AsyncTask<Void, Void, String>() {

        override fun onPreExecute() {
            super.onPreExecute()
            imageUriArray.clear()
            videoUriArray.clear()

            // Create a new trust manager that trust all certificates
            val trustAllCerts: Array<TrustManager> = arrayOf<TrustManager>(
                object : X509TrustManager {
                    override fun checkClientTrusted(
                        certs: Array<X509Certificate?>?,
                        authType: String?
                    ) {
                    }

                    override fun checkServerTrusted(
                        certs: Array<X509Certificate?>?,
                        authType: String?
                    ) {
                    }

                    override fun getAcceptedIssuers(): Array<X509Certificate>? {
                        return null
                    }
                }
            )


            // Activate the new trust manager
            try {
                val sc: SSLContext = SSLContext.getInstance("SSL")
                sc.init(null, trustAllCerts, SecureRandom())
                HttpsURLConnection.setDefaultSSLSocketFactory(sc.socketFactory)
            } catch (e: java.lang.Exception) {
            }

        }

        private fun writeFileContent(uri: Uri?) {
            try {
                val file = uri?.let { requireContext().contentResolver.openFileDescriptor(it, "w") }

                file?.let {
                    val fileOutputStream = FileOutputStream(
                        it.fileDescriptor
                    )
                    val textContent = "This is the dummy text."

                    fileOutputStream.write(textContent.toByteArray())

                    fileOutputStream.close()
                    it.close()
                }

            } catch (e: FileNotFoundException) {
//print logs
            } catch (e: IOException) {
//print logs
            }

        }

        // Do the task in background/non UI thread
        @RequiresApi(Build.VERSION_CODES.R)
        override fun doInBackground(vararg params: Void?): String {
            try {
                Log.e("res", "480 size " + imgurl.size)
                if (imgurl.size > 0) {
                    for (i in imgurl.indices) {
                        Log.e("res", "482")
                        val filenamesplit = imgurl.get(i).Media.split("/").toTypedArray()
                        val size = filenamesplit.size
                        Log.e("videourl", "type media: " + imgurl.get(i).MediaTypeId)
                        if (imgurl.get(i).MediaTypeId.equals(Const.MEDIA_VIDEO)) {
                            Log.e("videourl", "type video")


                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {


                                val ffidir = File(
                                    requireContext().cacheDir,
                                    getString(R.string.app_name)
                                )
                                if (!ffidir.exists()) {
                                    ffidir.mkdir()
                                }
                                /*val mypath = File(ffidir, id + "_" +
                                        size?.minus(1)?.let { filenamesplit.get(it) })*/
                                val mypath = File(
                                    ffidir, id + "_" +
                                            "video1.mp4"
                                )

                                try {
                                    Log.e("videourl", getMediaLink() + imgurl.get(i).Media)
                                    val outputStream =
                                        FileOutputStream(
                                            java.lang.String.valueOf(mypath)
                                        )


                                    val videoUrl = URL(getMediaLink() + imgurl.get(i).Media)
                                    val conc = videoUrl.openConnection() as HttpURLConnection
                                    conc.doInput = true
                                    conc.connect()
                                    val inst: InputStream = conc.inputStream
                                    var buffer: ByteArray = ByteArray(1024)
                                    var len1: Int = 0
                                    while ((inst.read(buffer).also({ len1 = it })) > 0) {
                                        outputStream.write(buffer, 0, len1)
                                    }
                                    outputStream.close()
                                    Log.e("videourl", "local path: " + mypath.toString())
                                    // imageUriArray.add(Uri.parse(mypath.toString()))
                                    videoUriArray.add(Uri.parse(mypath.toString()))

                                } catch (e: java.lang.Exception) {
                                    Log.e("videourl", "catch:")
                                    Log.e("videourl", "catch:" + e.message)
                                }
                            } else {
                                Log.e("videourl", "else:")

                                val values = ContentValues()
                                val fileName =
                                    id + "_" + size?.minus(1)?.let { filenamesplit.get(it) }

                                values.put(
                                    MediaStore.MediaColumns.DISPLAY_NAME,
                                    fileName
                                )

                                values.put(
                                    MediaStore.MediaColumns.MIME_TYPE,
                                    "video/mp4"
                                )

                                values.put(
                                    MediaStore.MediaColumns.RELATIVE_PATH,
                                    Environment.DIRECTORY_DOWNLOADS + LOCAL_VIDEO_FOLDER
                                )
                                // MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                                val uri: Uri? = requireActivity().contentResolver.insert(
                                    MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                                    values!!
                                )
                                try {
                                    val outputStream: OutputStream? =
                                        requireActivity().contentResolver.openOutputStream(uri!!)


                                    val videoUrl = URL(getMediaLink() + imgurl.get(i).Media)
                                    val conc = videoUrl.openConnection() as HttpURLConnection
                                    conc.doInput = true
                                    conc.connect()
                                    val inst: InputStream = conc.inputStream
                                    var buffer: ByteArray = ByteArray(1024)
                                    var len1: Int = 0
                                    while ((inst.read(buffer).also({ len1 = it })) > 0) {
                                        outputStream?.write(buffer, 0, len1)
                                    }
                                    outputStream?.close()

                                    if (uri != null) {
                                        Log.e("videourl", "videolocal path: " + uri!!.toString())
                                        videoUriArray.add(uri!!)
                                    }

                                } catch (e: java.lang.Exception) {
                                    Log.e("videourl", "catch2:")
                                    Log.e("videourl", "catch2:" + e.message)
                                }
                            }
                        } else {
                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                                val ffidir = File(
                                    Environment.DIRECTORY_PICTURES,
                                    getString(R.string.app_name)
                                )
                                if (!ffidir.exists()) {
                                    ffidir.mkdir()
                                }
                                val mypath = File(ffidir, id + "_" +
                                        size?.minus(1)?.let { filenamesplit.get(it) })

                                try {
                                    val outputStream =
                                        FileOutputStream(
                                            java.lang.String.valueOf(mypath)
                                        )
                                    val bitmap =
                                        getBitmapFromURL(getMediaLink() + imgurl.get(i).Media)
                                    bitmap?.compress(
                                        Bitmap.CompressFormat.JPEG, 90, outputStream
                                    )
                                    Log.e("res", "500")

                                    outputStream.close()
                                } catch (e: java.lang.Exception) {
                                    Log.e("Catch", e.message.toString())
                                }

                                Log.e("res", "496")
                                imageUriArray.add(Uri.parse(mypath.toString()))
                            } else {
                                val values = ContentValues()
                                val fileName =
                                    id + "_" + size?.minus(1)?.let { filenamesplit.get(it) }

                                values.put(
                                    MediaStore.MediaColumns.DISPLAY_NAME,
                                    fileName
                                )

                                values.put(
                                    MediaStore.MediaColumns.MIME_TYPE,
                                    "image/jpeg"
                                )

                                values.put(
                                    MediaStore.MediaColumns.RELATIVE_PATH,
                                    Environment.DIRECTORY_PICTURES + LOCAL_IMAGE_FOLDER
                                )


                                val uri: Uri? = requireActivity().contentResolver.insert(
                                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                    values!!
                                ) //important!

                                try {
                                    val outputStream: OutputStream? =
                                        requireActivity().contentResolver.openOutputStream(uri!!)
                                    val bitmap =
                                        getBitmapFromURL(getMediaLink() + imgurl.get(i).Media)
                                    bitmap?.compress(
                                        Bitmap.CompressFormat.JPEG, 90, outputStream
                                    )
                                    outputStream?.close()

                                } catch (e: java.lang.Exception) {
                                    Log.e("tagUri", e.message.toString())
                                    Log.e("tagUri", "image errior")
                                }

                                if (uri != null) {
                                    imageUriArray.add(uri!!)
                                }


                            }
                        }

                    }
                } else {

                    if (this@ProductDetailFragment::btnDone.isInitialized) {
                        btnDone.visibility = View.VISIBLE
                        liDownloads.visibility = View.GONE
                    }
                }
                return ""
            } catch (e: Exception) {
                Log.e("tagUri", " imageUriArray uri main error " + e.message)
                e.printStackTrace()
            }

            return ""
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            Log.e("tagGlideImageCheck", result.toString() + " download")
            alreadyDownload = true
            if (isLast) {
//                activity?.showToast(getString(R.string.msg_imges_downloaded))
            }

            if (this@ProductDetailFragment::btnDone.isInitialized) {
                btnDone.visibility = View.VISIBLE
                liDownloads.visibility = View.GONE
            }

        }
    }

    fun ShareOnFacebook(isimg: Boolean, isVideo: Boolean) {
        Log.e("fbshare", "ShareOnFacebook")


        try {
            var sharePhoto: SharePhoto
            var tempUriArray: ArrayList<Uri> = ArrayList<Uri>()
            var arrphotos: ArrayList<ShareMedia> = ArrayList()
            if (isimg && !isVideo) {
                if (alreadyDownload) {
                    tempUriArray = imageUriArray


                } else {
                    if (productdata != null && productdata?.Media != null &&
                        imageUriArrayGlide.size == productdata?.Media?.size
                    ) {
                        tempUriArray = imageUriArrayGlide
                    } else {
                        tempUriArray = imageUriArray
                    }
                }
                for (i in tempUriArray.indices) {
                    sharePhoto = SharePhoto.Builder()
                        .setImageUrl(tempUriArray.get(i))
                        .build()
                    arrphotos.add(sharePhoto)
                }

            } else if (!isimg && isVideo) {
                tempUriArray = videoUriArray

                for (i in tempUriArray.indices) {
                    var shareVideo: ShareVideo = ShareVideo.Builder()
                        .setLocalUrl(tempUriArray.get(i))
                        .build()
                    arrphotos.add(shareVideo)
                }

            } else if (isVideo && isimg) {

                for (i in imageUriArray.indices) {
                    sharePhoto = SharePhoto.Builder()
                        .setImageUrl(imageUriArray.get(i))
                        .build()
                    arrphotos.add(sharePhoto)
                }

                for (i in videoUriArray.indices) {
                    var shareVideo: ShareVideo = ShareVideo.Builder()
                        .setLocalUrl(videoUriArray.get(i))
                        .build()
                    arrphotos.add(shareVideo)
                }

            }


/*
            for (i in tempUriArray.indices) {
                var bitmap: Bitmap? = null

                try {
                    bitmap = MediaStore.Images.Media.getBitmap(
                        activity?.contentResolver,
                        tempUriArray.get(i)
                    )
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                }
                if (i == 0) {
                    sharePhoto = SharePhoto.Builder()
                        .setBitmap(bitmap)
                        .setCaption(sharedesc)
                        .build()
                } else {
                    sharePhoto = SharePhoto.Builder()
                        .setBitmap(bitmap)
                        .build()
                }
                arrphotos.add(sharePhoto)

            }*/


            val shareContent = ShareMediaContent.Builder()
                .addMedia(arrphotos as List<ShareMedia>?)
                .build()


            activity?.showBottomToast(getString(R.string.msg_desc_copeied))
            shareDialog.show(shareContent, ShareDialog.Mode.AUTOMATIC)
        } catch (e: IndexOutOfBoundsException) {
            Log.e("sharefb", "exe")
            Log.e("sharefb", "exe: " + e.message!!)
            e.printStackTrace()
        }

    }

//    fun ShareOnAnyApp(model: Data) {
//        val shareIntent = Intent(Intent.ACTION_SEND_MULTIPLE)
//        shareIntent.action = Intent.ACTION_SEND_MULTIPLE
//        shareIntent.type = "text/plain"
//
//        shareIntent.type = "image/*"
//
//        val arrUri: ArrayList<Uri> = ArrayList()
//        var tempUriArray: ArrayList<Uri> = ArrayList<Uri>()
//        if (alreadyDownload) {
//            tempUriArray = imageUriArray
//        } else {
//            if (productdata != null && productdata?.Media != null &&
//                imageUriArrayGlide.size == productdata?.Media?.size
//            ) {
//                tempUriArray = imageUriArrayGlide
//            } else {
//                tempUriArray = imageUriArray
//            }
//        }
//
//        for (i in tempUriArray.indices) {
//            try {
//                val bitmap: Bitmap = MediaStore.Images.Media.getBitmap(
//                    activity?.contentResolver,
//                    tempUriArray.get(i)
//                )
//
//                val os = ByteArrayOutputStream()
//                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, os)
//
//                val path = MediaStore.Images.Media.insertImage(
//                    requireContext().contentResolver, bitmap, null, null
//                )
//                arrUri.add(Uri.parse(path))
//            } catch (e: FileNotFoundException) {
//                e.printStackTrace()
//            }
//
//
//        }
//
//        shareIntent.putExtra(Intent.EXTRA_STREAM, arrUri)
//        shareIntent.putExtra(Intent.EXTRA_TEXT, sharedesc)
//        shareIntent.putExtra(
//            Intent.EXTRA_TITLE,
//            requireActivity().getString(R.string.app_name)
//        )
//        shareIntent.putExtra(
//            Intent.EXTRA_SUBJECT,
//            requireActivity().getString(R.string.app_name) + " Product " + model.Title
//        )
//
//
//        val openInChooser = Intent.createChooser(shareIntent, getString(R.string.title_share_with))
//        requireActivity().startActivity(openInChooser)
//    }


    fun ShareOnAnyApp(model: Data) {
        val shareIntent = Intent(Intent.ACTION_SEND_MULTIPLE)
        shareIntent.action = Intent.ACTION_SEND_MULTIPLE
        shareIntent.type = "text/plain"

        shareIntent.type = "image/*"
        val arrUri: ArrayList<Uri> = ArrayList()

        try {
            for (i in imageUriArray.indices) {
                val bitmap: Bitmap = MediaStore.Images.Media.getBitmap(
                    activity?.contentResolver,
                    imageUriArray.get(i)
                )
                val os = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os)

                val path = MediaStore.Images.Media.insertImage(
                    requireContext().contentResolver, bitmap, null, null
                )
                arrUri.add(Uri.parse(path))
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

        shareIntent.putExtra(Intent.EXTRA_STREAM, arrUri)
        shareIntent.putExtra(Intent.EXTRA_TEXT, sharedesc)
        shareIntent.putExtra(
            Intent.EXTRA_TITLE,
            requireActivity().getString(R.string.app_name)
        )
        shareIntent.putExtra(
            Intent.EXTRA_SUBJECT,
            requireActivity().getString(R.string.app_name) + " Product " + model.Title
        )

        val openInChooser = Intent.createChooser(shareIntent, getString(R.string.title_share_with))
        requireActivity().startActivity(openInChooser)
    }

    fun ShareOnAnyApp(isimg: Boolean, isVideo: Boolean) {
        Log.e("shareany", "ShareOnAnyApp")
        val builder: StrictMode.VmPolicy.Builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())

        val shareIntent = Intent(Intent.ACTION_SEND_MULTIPLE)
        shareIntent.action = Intent.ACTION_SEND_MULTIPLE

        shareIntent.putExtra(
            Intent.EXTRA_TITLE,
            requireActivity().getString(R.string.app_name)
        )
        shareIntent.putExtra(
            Intent.EXTRA_SUBJECT,
            requireActivity().getString(R.string.app_name) + " Collection " + ""
        )

        if (isimg && !isVideo) {
            // shareIntent.type = "image/*"
            shareIntent.type = "*/*"
            // shareIntent.putExtra(Intent.EXTRA_STREAM, imageUriArrayGlideMultiple)
            shareIntent.putExtra(Intent.EXTRA_STREAM, imageUriArray)

            //shareIntent.putExtra(Intent.EXTRA_TEXT,imageUriArray)//
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            val openInChooser =
                Intent.createChooser(shareIntent, getString(R.string.title_share_with))
            startActivityForResult(openInChooser, SHARE_TO_ANY)
        } else if (!isimg && isVideo) {

            shareIntent.type = "*/*"

            shareIntent.putExtra(Intent.EXTRA_STREAM, videoUriArray)

            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            val openInChooser =
                Intent.createChooser(shareIntent, getString(R.string.title_share_with))
            startActivityForResult(openInChooser, SHARE_TO_ANY)
        } else if (isimg && isVideo) {
            // shareIntent.type = "image/*"
            shareIntent.type = "*/*"
            // shareIntent.putExtra(Intent.EXTRA_STREAM, imageUriArrayGlideMultiple)
            bothUris.clear()
            bothUris.addAll(imageUriArray)
            bothUris.addAll(videoUriArray)
            shareIntent.putExtra(Intent.EXTRA_STREAM, bothUris)

            //shareIntent.putExtra(Intent.EXTRA_TEXT,imageUriArray)//
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            val openInChooser =
                Intent.createChooser(shareIntent, getString(R.string.title_share_with))
            startActivityForResult(openInChooser, SHARE_TO_ANY)
        } else {
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT, sharedesc)
            val openInChooser =
                Intent.createChooser(shareIntent, getString(R.string.title_share_with))
            activity?.startActivity(shareIntent)
        }


    }


    private val callback: FacebookCallback<Sharer.Result> =
        object : FacebookCallback<Sharer.Result> {
            override fun onSuccess(result: Sharer.Result) {
                Log.e("TAG", "Succesfully posted")
                // Write some code to do some operations when you shared content successfully.
            }

            override fun onCancel() {
                Log.e("TAG", "Cancel occured")
                // Write some code to do some operations when you cancel sharing content.
            }

            override fun onError(error: FacebookException) {
                Log.e("TAG", error.message!!)
                Log.e("TAG", "fb share error")
                // Write some code to do some operations when some error occurs while sharing content.
            }
        }

    fun ShareDesc() {
        /*   AlertDialog.Builder(context)
               .setTitle(getString(R.string.title_share_content))
               .setMessage(sharedesc)
               .setPositiveButton("No",
                   DialogInterface.OnClickListener { dialog, which ->
                       // ShareOnAnyApp(false)
                   })
               .setNegativeButton("Yes",
                   DialogInterface.OnClickListener { dialog, which ->

                   })
               .setCancelable(false)
               .show()*/

        val dialog = CustomDialog(requireActivity(), getString(R.string.title_share_content),
            sharedesc,
            "Yes",
            "No",
            CustomDialog.ItemReturnListener {
                ShareOnWhatsapp(false, false)
            })
        dialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
        Log.e("share", requestCode.toString())
        if (requestCode == SHARE_DESC) {
            if (sharedesc.length > 0)
                ShareDesc()
        }
    }

    fun getBitmapFromURL(src: String?): Bitmap? {
        return try {
            val url = URL(src)
            val connection =
                url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input = connection.inputStream
            BitmapFactory.decodeStream(input)
        } catch (e: IOException) {
            // Log exception
            null
        } catch (e: RuntimeException) {
            null
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == WRITE_PERMISSION) {
            checkPermissionAndDownloadImages()
        }
    }

    private fun checkPermissionAndDownloadImages() {
        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            if (productdata != null && productdata?.Media != null) {
                clearAndRexecuteDownloader(productdata?.Media!!)
            }
        }
    }

    private fun checkGlideImages() {
        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            imageUriArrayGlide.clear()
            Log.e("tagGlideImageCheck", "checkGlideImages() ")
            if (productdata != null && productdata?.Media != null && productdata?.Media.size > 0) {
                for ((i, mImage) in productdata?.Media?.withIndex()!!) {
                    Log.e(
                        "tagGlideImageCheck",
                        "checkGlideImages() i " + i + "  && " + getMediaLink() + mImage.Media
                    )
                    GlideApp.with(this@ProductDetailFragment)
                        .asBitmap()
                        .load(getMediaLink() + mImage.Media)
                        .into(object : CustomTarget<Bitmap>() {
                            override fun onLoadCleared(placeholder: Drawable?) {

                            }

                            override fun onResourceReady(
                                resource: Bitmap,
                                transition: Transition<in Bitmap>?
                            ) {
                                Log.e(
                                    "tagGlideImageCheck",
                                    "checkGlideImages() onResourceReady " + i
                                )
//                            imageUriArrayGlide.add(resource)
                                val mFile = activity?.getOutputMediaFile(i.toString())
                                if (mFile != null) {
                                    Log.e(
                                        "tagGlideImageCheck",
                                        "checkGlideImages() onResourceReady " + i + " && mFile not null "
                                                + mFile.absolutePath
                                    )
                                    activity?.storeImage(resource, mFile)
                                    Log.e(
                                        "tagGlideImageCheck",
                                        "checkGlideImages() onResourceReady i " + i
                                                + "  && Uri.fromFile(resource) " + Uri.fromFile(
                                            mFile
                                        )
                                    )
                                    imageUriArrayGlide.add(Uri.fromFile(mFile))
                                }
                            }

                        })
                }
            } else {
                if (this@ProductDetailFragment::btnDone.isInitialized) {
                    btnDone.visibility = View.VISIBLE
                    liDownloads.visibility = View.GONE
                }
            }
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                WRITE_PERMISSION
            )
        }
    }
}

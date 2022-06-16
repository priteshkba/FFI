package com.ffi.wishlist

import android.Manifest
import android.app.Dialog
import android.graphics.Typeface
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ffi.HomeActivity
import com.ffi.R
import com.ffi.Utils.*
import com.ffi.model.ParamAddREmoveWishItem
import com.ffi.model.ResponseAddRemoveWishItem
import com.ffi.productdetail.*
import com.ffi.productdetail.Data
import com.ffi.api.ApiClient
import com.ffi.api.WebService
import com.google.gson.Gson
import com.wajahatkarim3.longimagecamera.LongImageCameraActivity
import kotlinx.android.synthetic.main.custom_toolbar.*
import kotlinx.android.synthetic.main.fragment_wishlist.*
import kotlinx.android.synthetic.main.layout_product_detail.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

class WishlistFragment : Fragment(), View.OnClickListener {

    lateinit var wish_list: WishListAdapter
    var homeActivity: HomeActivity? = null
    lateinit var mprogressbar: ProgressBarHandler
    var items: ArrayList<Item> = ArrayList()
    var moveToCartAlertDialog: Dialog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_wishlist, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mprogressbar = ProgressBarHandler(requireActivity())
        homeActivity = activity as HomeActivity

        init()
        
        if (!getUserId().isEmpty()) {
            CallApiWishList(true)
            setRefresh()
        } else {
            tvNoWishitem?.visibility = View.VISIBLE
            rvWish?.visibility = View.GONE
        }
    }

    private fun setRefresh() {
        swf_wishlist?.setOnRefreshListener {
            CallApiWishList(false)
        }
    }

    private fun init() {
        //homeActivity.header.visibility = View.GONE
        ivBack?.visibility = View.VISIBLE
        ivEdit?.visibility = View.GONE
        txt_toolbar?.text = getString(R.string.title_whsihlist)

        setListener()
    }

    private fun setListener() {
        ivBack.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBack -> {
                homeActivity?.selectLastMenu()
//                homeActivity?.replaceFragment(MyAccountFragment(), "4")
                fragmentManager?.popBackStack()
            }
        }
    }

    fun CallApiWishList(isprogress: Boolean) {
        if (isInternetAvailable(requireActivity())) {
            if (isprogress)
                mprogressbar.showProgressBar()


            val retrofit = WebService.getRetrofit(requireActivity()).create(ApiClient::class.java)
            retrofit.GetWishList()
                .enqueue(object : Callback<ResponseWishList> {
                    override fun onFailure(call: Call<ResponseWishList>, t: Throwable) {
                        mprogressbar.hideProgressBar()
                        swf_wishlist?.isRefreshing = false
                    }

                    override fun onResponse(
                        call: Call<ResponseWishList>,
                        response: Response<ResponseWishList>?
                    ) {
                        mprogressbar.hideProgressBar()
                        swf_wishlist?.isRefreshing = false

                        if (response != null) {
                            if (response.isSuccessful) {
                                val responsewish = response.body()
                                if (responsewish != null) {
                                    if (responsewish.status == API_SUCESS) {
                                        if(items != null){
                                            items.clear()
                                        }
                                        items = responsewish.data.items
                                        setData(items)

                                    } else if (responsewish.status == API_DATA_NOT_AVAILBLE) {
                                        tvNoWishitem?.visibility = View.VISIBLE
                                        rvWish?.visibility = View.GONE
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
        }
        else {
            NetworkErrorDialog(requireActivity())
            swf_wishlist?.isRefreshing = false
        }
    }

    private fun setData(data: ArrayList<Item>) {
        if (isAdded) {
            rvWish.apply {
                layoutManager = LinearLayoutManager(context)
                wish_list = WishListAdapter(this@WishlistFragment, data)
                adapter = wish_list
                itemAnimator = null

            }
        }
    }

    fun loadProductDetail(id: String, variationId: String) {
        val fragment = ProductDetailFragment().getInstance(id, variationId)//.newInstance(refProv)
        homeActivity?.deselectAllMenu()
        homeActivity?.supportFragmentManager
            ?.beginTransaction()
            ?.add(R.id.cm_fragmentContainer, fragment, fragment::class.java.name)
            ?.addToBackStack(null)?.commit()
    }

    fun CallApiAddToCart(id: String, Varid: String, position: Int) {
        if (isInternetAvailable(requireActivity())) {
            mprogressbar.showProgressBar()

            val param = ParamAddProductNew().apply {
                productId = id
                variationId = Varid
                status = Const.STATUS_ADD_CART
                addFromWishlist = "1"
            }
            param.productId = id
            param.variationId = Varid
            param.status = Const.STATUS_ADD_CART
            param.addFromWishlist = "1"

            Log.e("tagWishlist", "add " + Gson().toJson(param))

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
                                            homeActivity?.badgetext?.visibility = View.VISIBLE
                                            homeActivity?.badgetext?.text = getCartItem().toString()
                                            if ((items.size - 1) == 0) {
                                                tvNoWishitem?.visibility = View.VISIBLE
                                                rvWish?.visibility = View.GONE
                                            }
                                            wish_list.move(position)
//                                        CallApiWishList()

                                            activity?.showToast(getString(R.string.msg_product_add_cart),Const.TOAST)
                                        }
                                    } else if (responseproduct.status == API_DATA_NOT_AVAILBLE) {
                                        activity?.showToast(responseproduct.message,Const.ALERT)
                                    }
                                }
                            } else {
                                activity?.showToast(getString(R.string.msg_common_error),Const.ALERT)
                            }
                        }
                    }
                })
        } else {
            NetworkErrorDialog(requireActivity())
        }
    }


    fun openProductDetailAndShowVarientDialog(id: String, Varid: String, position: Int) {
        CallapiProductDetails(id, Varid, position, true)
    }


    private fun CallapiProductDetails(id: String, Varid: String, position: Int, isprogress: Boolean) {
        if (isInternetAvailable(requireActivity())) {
            if (isprogress)
                mprogressbar.showProgressBar()

            val param = ParamProductDetail().apply {
                productId = id
            }
            val retrofit = WebService.getRetrofit(requireActivity()).create(ApiClient::class.java)
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
                                        if (isAdded && responseproduct.data != null &&
                                            responseproduct.data?.VariationList != null &&
                                            responseproduct.data?.VariationList?.size != null &&
                                            responseproduct.data?.VariationList?.size!! > 0){
                                            showWishlistDialog(id, Varid, position, responseproduct.data!!)
                                        }
                                        else {
                                            CallApiAddToCart(id, Varid, position)
                                        }

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
        }
    }



    fun showWishlistDialog(id: String, Varid: String, position: Int, mResp: Data) {
        if(activity != null && !activity?.isFinishing!!){
            var arrRecyclerView: ArrayList<RecyclerView> = arrayListOf()
            var originalVariationList: List<VariationList> = mResp?.VariationList!!
            var listOfVarientCombination: List<Variation>? = mResp.variation
            if(moveToCartAlertDialog != null){
                moveToCartAlertDialog?.dismiss()
            }
            moveToCartAlertDialog = Dialog(requireActivity())
            val window = moveToCartAlertDialog?.window
            window?.setLayout(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            window?.setGravity(Gravity.CENTER)
            moveToCartAlertDialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
            moveToCartAlertDialog?.setContentView(R.layout.dailog_select_varient)
            moveToCartAlertDialog?.setCancelable(true)
            moveToCartAlertDialog?.setCanceledOnTouchOutside(true)

            val btnClose = moveToCartAlertDialog?.findViewById<TextView>(R.id.btnClose)
            val btnAddCart = moveToCartAlertDialog?.findViewById<TextView>(R.id.btnAddCart)
            val li_varient = moveToCartAlertDialog?.findViewById<LinearLayout>(R.id.li_varient)

            btnAddCart?.setOnClickListener {

                var mSelectedVariationMultipleID: String = ""
                var isAnyVarientSelected = false
                var SelectedVarientId: String? = null
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
                        activity?.showToast(
                            getString(R.string.alert_varient) + " " + mVariationMain.name,
                            Const.ALERT
                        )
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
                            break
                        }
                    }
                }

                if(SelectedVarientId != null && !TextUtils.isEmpty(SelectedVarientId)){
                    if(moveToCartAlertDialog != null){
                        moveToCartAlertDialog?.dismiss()
                    }
                    CallApiAddToCart(id, SelectedVarientId, position)
                }
            }

            btnClose?.setOnClickListener {
                if(moveToCartAlertDialog != null){
                    moveToCartAlertDialog?.dismiss()
                }
            }

            li_varient?.removeAllViews()
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
                                        mClickedVarient,
                                        listOfVarientCombination,
                                        arrRecyclerView
                                    )
                                }
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

                li_varient?.addView(mTextView)
                arrRecyclerView.add(mRecyclerView)
                li_varient?.addView(mRecyclerView)
            }

            moveToCartAlertDialog?.show()
        }
    }


    private fun reloadAllVarientWithNewValue(
        clickedPosition: Int,
        mRecyclerViewPosition: Int,
        mVariationListID: Int,
        mClickedVarient: Varient?,
        listOfVarientCombination: List<Variation>? = null,
        arrRecyclerView: ArrayList<RecyclerView>
    ) {
        var SelectedVarientId = "0"
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
        if(mCode != null && !TextUtils.isEmpty(mCode)){
            for (mVariation in listOfVarientCombination!!) {
                if(mVariation.Code != null && !TextUtils.isEmpty(mVariation.Code) &&
                    (mVariation.Code.equals(mCode) || mVariation.Code.equals(mCodeReverse))){
                    if(mVariation.Quantity != null && !TextUtils.isEmpty(mVariation.Quantity)){
                        mFinalQuantity = mVariation.Quantity.toInt()
                    }
                    else {
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

    }

    fun CallApiRemoveWishList(productid: String, SelectedVarientId: String, position: Int) {
        if (isInternetAvailable(requireActivity())) {
            mprogressbar.showProgressBar()
            val param = ParamAddREmoveWishItem().apply {
                productId = productid
//                variationId = SelectedVarientId
                variationId = "0"
                status = "0"
            }
            val retrofit = WebService.getRetrofit(requireActivity()).create(ApiClient::class.java)
            retrofit.AddRemoveWishItem(param)
                .enqueue(object : Callback<ResponseAddRemoveWishItem> {
                    override fun onFailure(call: Call<ResponseAddRemoveWishItem>, t: Throwable) {
                        mprogressbar.hideProgressBar()
                    }

                    override fun onResponse(
                        call: Call<ResponseAddRemoveWishItem>,
                        response: Response<ResponseAddRemoveWishItem>?
                    ) {
                        mprogressbar.hideProgressBar()
                        if (response != null) {
                            if (response.isSuccessful) {
                                val responseproduct = response.body()
                                if (responseproduct != null) {
                                    if (responseproduct.status == API_SUCESS) {
                                        storeCartItem(getCartItem()?.plus(1)!!)
                                        homeActivity?.badgetext?.visibility = View.VISIBLE
                                        homeActivity?.badgetext?.text = getCartItem().toString()
                                        if ((items.size - 1) == 0) {
                                            tvNoWishitem?.visibility = View.VISIBLE
                                            rvWish?.visibility = View.GONE

                                        }
                                        wish_list.move(position)
//                                        CallApiWishList()
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
}

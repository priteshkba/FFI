package com.ffi.my_orderdetails

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ffi.GlideApp
import com.ffi.R
import com.ffi.Utils.Const
import com.ffi.Utils.Const.Companion.ORDER_DELIVERED
import com.ffi.Utils.getMediaLink
import com.ffi.Utils.showBottomToast
import com.ffi.Utils.showToast
import com.ffi.api.WebService.context
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class OrderItemAdapter(
    val activity: OrderDetailFragment,
    val items: ArrayList<Item>
) :
    RecyclerView.Adapter<CardItemHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardItemHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.adapter_order, parent, false)

        return CardItemHolder(view)
    }

    override fun getItemCount() = items.size


    override fun onBindViewHolder(holder: CardItemHolder, position: Int) {
        var lastTimeSel: Long = 0
        var mLastClickTime: Long = 0

        val model = items[position]

        if(model.isActive.equals("0")){
            holder.li_main.background = holder.li_main.context.resources.getDrawable(R.drawable.bg_light_gray_curve)
        }

        holder.tvProductName.text = model.title
        holder.tvPrice.text = activity.resources.getString(R.string.currency) + " " + model.itemPrice
        holder.tvQty.text = model.quantity

        if (model.trackingCode.isNotEmpty() || model.trackingUrl.isNotEmpty()) {
            holder.li_tracking.visibility = View.VISIBLE
            holder.tvTreckingcode.text = model.trackingCode
            holder.tvTreckingurl.text = model.trackingUrl
        } else {
            holder.li_tracking.visibility = View.GONE
        }
        if(model.isActive.equals("0")){
          //  holder.tvReturnedProduct.visibility = View.VISIBLE
            holder.tvReturnedProduct.text = "Canceled Product"
            holder.li_review.visibility =View.GONE
        }else{
            holder.tvReturnedProduct.visibility = View.GONE
            holder.li_review.visibility =View.VISIBLE
        }

        if(!TextUtils.isEmpty(model.OtherReturnReason) || !TextUtils.isEmpty(model.ReasonText)){
            holder.tvReturnedProduct.visibility = View.VISIBLE
            if(model.ReturnReasonId==6){
                holder.tvReturnedProduct.text = "Return Reason : "+model.OtherReturnReason
            }else{
                holder.tvReturnedProduct.text = "Return Reason : "+model.ReasonText
            }
        }else{
            holder.tvReturnedProduct.visibility = View.GONE
        }
        if(!TextUtils.isEmpty(model.orderStatus)&&model.orderStatus.toInt()==500){
            if(model.isReturnRequested==0) {
                holder.li_return_product.visibility = View.VISIBLE
            }else{
                holder.li_return_product.visibility = View.GONE
            }
        }else{
            holder.li_return_product.visibility = View.GONE
        }

        holder.li_return_product.setOnClickListener {

            if(!model.isActive.equals("0")){

                if(!TextUtils.isEmpty(model.returnValidUpto.toString())){

                    val timeStamp = SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(model.returnValidUpto.toString())
                    val timeStampDevice = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
                    val timeStampDeviceDate = SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(timeStampDevice)

                    if(timeStamp!=null && timeStamp.after(timeStampDeviceDate)){
                        activity.returnOrder(model.orderDetailId)
                    }else{
                        activity.context?.showToast(activity.getString(R.string.msg_return_date), Const.TOAST)
                    }
                }
            }

        }
        holder.tvStatus.text = model.orderItemStatusName

        holder.tvTreckingcode.setOnClickListener {

            if(!model.isActive.equals("0")) {
                val clipboard: ClipboardManager =
                    activity.context?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip: ClipData =
                    ClipData.newPlainText("copy", holder.tvTreckingcode.text.toString().trim())
                clipboard.setPrimaryClip(clip)
                activity.requireContext().showBottomToast("Copied")
            }


        }

        holder.ivshare.setOnClickListener {
            if(!model.isActive.equals("0")) {
                val sharedesc = "You tracking details is of product : " + model.title + "\n" +
                        "Tracking code : " + model.trackingCode + "\n" +
                        "Tracking Url : " + model.trackingUrl
                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.type = "text/plain"
                shareIntent.putExtra(Intent.EXTRA_TEXT, sharedesc)
                shareIntent.putExtra(
                    Intent.EXTRA_TITLE,
                    activity.resources.getString(R.string.app_name)
                )
                val openInChooser = Intent.createChooser(
                    shareIntent,
                    activity.resources.getString(R.string.title_share_with)
                )
                activity.startActivity(openInChooser)
            }



        }

        var strvarient = ""
        for (i in model.type.indices) {
            if (i == 0) {
                strvarient = model.type.get(i).variantValue ?: ""
            } else {
                strvarient += ", " + model.type.get(i).variantValue
            }
        }

        try {
            GlideApp.with(activity)
                .load(getMediaLink() + model.media.get(0))
                .placeholder(R.color.color_light_grey)
                .into(holder.ivcatlog)

            Log.e("res", getMediaLink() + model.media.get(0))
        } catch (e: IndexOutOfBoundsException) {
            e.printStackTrace()
        }

        if (strvarient.isEmpty()) {
            holder.tvSize.visibility = View.GONE
        } else {
            holder.tvSize.visibility = View.VISIBLE
            holder.tvSize.text = strvarient
        }

        holder.li_review.setOnClickListener {

            if(!model.isActive.equals("0")) {

                lastTimeSel = SystemClock.elapsedRealtime() - mLastClickTime;
                if (lastTimeSel > 0 && lastTimeSel < 500) {
                    return@setOnClickListener
                }
                mLastClickTime = SystemClock.elapsedRealtime()

                if (model.orderStatus.equals(ORDER_DELIVERED)) {
                    if (model.reviewRating) {
                        activity.context?.showToast(
                            activity.getString(R.string.review_given),
                            Const.TOAST
                        )
                    } else {
                        activity.loadReview(model.productId)
                    }
                } else {
                    activity.context?.showToast(activity.getString(R.string.msg_review), Const.TOAST)
                }
            }

        }
    }

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

    }
}
package com.ffi.wishlist

import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ffi.GlideApp
import com.ffi.R
import com.ffi.Utils.getMediaLink

class WishListAdapter(
    val activity: WishlistFragment,
    val items: ArrayList<Item>
) :
    RecyclerView.Adapter<WishHolder>() {

    var lastTimeSel: Long = 0
    var mLastClickTime: Long = 0
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WishHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.adapter_wishlist, parent, false)

        return WishHolder(view)
    }

    override fun getItemCount() = items.size


    override fun onBindViewHolder(holder: WishHolder, position: Int) {
        val model = items[position]

        var strvarient = ""
        try {
            if (!model.Type.isNullOrEmpty()) {
                for (i in model.Type.indices) {
                    if (i == 0) {
                        strvarient = model.Type.get(i).variantValue.toString()
                    } else {
                        strvarient += ", " + model.Type.get(i).variantValue
                    }
                }


            }
        } catch (e: IndexOutOfBoundsException) {
            e.printStackTrace()
        }

        if (strvarient.isEmpty()) {
            holder.tvSize.visibility = View.GONE
        } else {
            holder.tvSize.visibility = View.VISIBLE
            holder.tvSize.text = strvarient
        }

        if (model.media.size >= 1) {
            GlideApp.with(activity)
                .load(getMediaLink() + model.media.get(0))
                .placeholder(R.color.color_light_grey)
                .into(holder.ivWishItem)
        }


        holder.tvPrice.text = activity.getString(R.string.currency) + " " + model.itemPrice
        holder.tvItemName.text = model.title
        holder.tvsoldby.text = model.Seller

        holder.ivAddCart.setOnClickListener {
            lastTimeSel = SystemClock.elapsedRealtime() - mLastClickTime;
            if (lastTimeSel > 0 && lastTimeSel < 500) {
                return@setOnClickListener
            }
            mLastClickTime = SystemClock.elapsedRealtime()
            activity.openProductDetailAndShowVarientDialog(model.productId, model.VariationId, position)
        }

        holder.ivDelete.setOnClickListener {
            lastTimeSel = SystemClock.elapsedRealtime() - mLastClickTime;
            if (lastTimeSel > 0 && lastTimeSel < 500) {
                return@setOnClickListener
            }
            mLastClickTime = SystemClock.elapsedRealtime()
            activity.CallApiRemoveWishList(model.productId, model.VariationId, position)
        }

        holder.li_main.setOnClickListener {
            activity.loadProductDetail(model.productId, model.VariationId)
        }
    }

    fun move(position: Int) {
        try {
            items.removeAt(position)
            notifyItemRemoved(position)
            notifyDataSetChanged()
        } catch (e: IndexOutOfBoundsException) {
            e.printStackTrace()
        }
    }

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
    }
}
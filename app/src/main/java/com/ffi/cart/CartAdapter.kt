package com.ffi.cart

import android.graphics.LinearGradient
import android.graphics.Shader
import android.graphics.Shader.TileMode
import android.os.SystemClock
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ffi.GlideApp
import com.ffi.R
import com.ffi.Utils.Const
import com.ffi.Utils.getCartItem
import com.ffi.Utils.getMediaLink


class CartAdapter(
    val activity: CartFragment,
    val items: ArrayList<Item>
) :
    RecyclerView.Adapter<CartHolder>() {
    var lastTimeSel: Long = 0
    var mLastClickTime: Long = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.adapter_cart, parent, false)
        return CartHolder(activity, view)
    }



    override fun onBindViewHolder(holder: CartHolder, position: Int) {

        val textShader: Shader = LinearGradient(
            0f,
            0f,
            0f,
            25f,
            intArrayOf(
                activity.resources.getColor(R.color.color_light_blue),
                activity.resources.getColor(R.color.colorPrimary)
            ),
            floatArrayOf(0f, 1f),
            TileMode.CLAMP
        )
        holder.tvQty.getPaint().setShader(textShader)

        val model = items[position]


        var strvarient = ""
        for (i in model.type.indices) {
            if (i == 0) {
                strvarient = model.type.get(i).variantValue.toString()
            } else {
                strvarient += ", " + model.type.get(i).variantValue
            }
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
                .into(holder.ivcatlog)
        }
        holder.tvProductName.text = model.title
        holder.tvQty.text = model.quantity

        if (model.OutOfStock) {
            holder.tvPrice.text = activity.getString(R.string.lbl_stock_unalbl)
            activity.context?.resources?.getColor(R.color.color_red)?.let {
                holder.tvPrice.setTextColor(
                    it
                )
            }
        } else {
            holder.tvPrice.text = activity.getString(R.string.currency) + " " + model.itemPrice
            activity.context?.resources?.getColor(R.color.color_black)?.let {
                holder.tvPrice.setTextColor(
                    it
                )
            }
        }
        holder.tvSeller.text = model.Seller

        holder.fi_main_layout.setOnClickListener {
            activity.loadProductDetail(model.productId)
        }

        holder.ivDelete.setOnClickListener {
            lastTimeSel = SystemClock.elapsedRealtime() - mLastClickTime;
            if (lastTimeSel > 0 && lastTimeSel < 500) {
                return@setOnClickListener
            }
            mLastClickTime = SystemClock.elapsedRealtime()
            activity.DeletConfimationDialog(model.iD, position, model.quantity)
        }

        holder.ivPlus.setOnClickListener {
            lastTimeSel = SystemClock.elapsedRealtime() - mLastClickTime;
            if (lastTimeSel > 0 && lastTimeSel < 500) {
                return@setOnClickListener
            }
            mLastClickTime = SystemClock.elapsedRealtime()
            ShowQuntity(true, holder.tvQty, model.productId, model.variationId)
        }

        holder.iv_wish.setOnClickListener {
            lastTimeSel = SystemClock.elapsedRealtime() - mLastClickTime;
            if (lastTimeSel > 0 && lastTimeSel < 500) {
                return@setOnClickListener
            }
            mLastClickTime = SystemClock.elapsedRealtime()
            activity.MoveConfimationDialog(model.productId, model.variationId, position)
        }

        holder.ivMinus.setOnClickListener {
            lastTimeSel = SystemClock.elapsedRealtime() - mLastClickTime;
            if (lastTimeSel > 0 && lastTimeSel < 500) {
                return@setOnClickListener
            }
            mLastClickTime = SystemClock.elapsedRealtime()
            ShowQuntity(false, holder.tvQty, model.productId, model.variationId)
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

    fun ShowQuntity(b: Boolean, tv: TextView, productId: String, varientId: String) {
        var qty: Int = tv.text.toString().toInt()
        if (b) {
            activity.CallApiUpdateCartItem(b, tv, productId, varientId)
        } else {
            if (qty <= 1) {
                return
            }
            activity.CallApiUpdateCartItem(b, tv, productId, varientId)
        }
        activity.homeActivity?.badgetext?.setText(getCartItem().toString())
    }

    override fun getItemCount(): Int {
        Log.e("//////","////size"+items.size)
        return items.size
    }

}
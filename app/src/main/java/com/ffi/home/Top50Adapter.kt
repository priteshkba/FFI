package com.ffi.home

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.ffi.GlideApp
import com.ffi.R
import com.ffi.Utils.Const
import com.ffi.Utils.getMediaLink

class Top50Adapter(val activity: HomeFragment) :
    RecyclerView.Adapter<Top50Holder>() {

    private var models = ArrayList<RecordXX>()
    var mFirstWidth = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Top50Holder {
        val view = LayoutInflater.from(activity.requireActivity())
            .inflate(R.layout.adapter_top_50, parent, false)
        return Top50Holder(view)
    }


    fun addModels(modellist: List<RecordXX>) {
        this.models.addAll(modellist)
        this.notifyDataSetChanged()
    }


    override fun getItemCount(): Int {
        var size = 6
        if (models.size <= 6) {
            size = models.size
        }
        return size
    }


    override fun onBindViewHolder(holder: Top50Holder, position: Int) {
        val model = models[position]

        //set Wishlist
        if (model.IsWishlist.equals(Const.AVAILBLE_IN_WISH)) {
            GlideApp.with(activity)
                .load(R.drawable.ic_wish_fill)
                .into(holder.ivFav)
        } else {
            GlideApp.with(activity)
                .load(R.drawable.ic_wish)
                .into(holder.ivFav)
        }

        if(mFirstWidth > 0){
//            holder.ivitem.layoutParams.width = mFirstWidth
//            holder.ivitem.layoutParams.height = (mFirstWidth * 1.6f).toInt()
        }

        holder.tvitemname.text = model.Title
        holder.tvItemsPrice.text = activity.getString(R.string.currency) + " " + model.Price

        GlideApp.with(activity)
            .asBitmap()
            .load(getMediaLink() + model.Media)
            .placeholder(R.color.color_light_grey)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap>?
                ) {
                    Log.e(
                        "size",
                        "height top50 - " + holder.ivitemImg.height + " width - " + holder.ivitemImg.width
                    )
                    holder.ivitemImg.setImageBitmap(resource)
                }

                override fun onLoadCleared(placeholder: Drawable?) {

                }
            })

        holder.litopitems.setOnClickListener {
            activity.loadProductDetail(model.ID)
        }

        holder.ivFav.setOnClickListener {
            if (model.IsWishlist.equals(Const.AVAILBLE_IN_WISH)) {
                model.IsWishlist = Const.NOT_AVAILBLE_IN_WISH
                GlideApp.with(activity)
                    .load(R.drawable.ic_wish)
                    .into(holder.ivFav)
                activity.CallApiAddWishList(Const.NOT_AVAILBLE_IN_WISH, model.ID)
            } else {
                model.IsWishlist = Const.AVAILBLE_IN_WISH
                GlideApp.with(activity)
                    .load(R.drawable.ic_wish_fill)
                    .into(holder.ivFav)
                activity.CallApiAddWishList(Const.AVAILBLE_IN_WISH, model.ID)
            }
        }


    }


    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
    }
}
package com.ffi.reviewlist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ffi.GlideApp
import com.ffi.R
import com.ffi.Utils.Const
import com.ffi.Utils.getFormattedDate
import com.ffi.Utils.getMediaLink


class ReviewListAdapter(
    val activity: ReviewListActivity,
    val items: List<Item>
) :
    RecyclerView.Adapter<ReviewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.adapter_reviews, parent, false)

        return ReviewHolder(view)
    }


    override fun getItemCount() = items.size


    override fun onBindViewHolder(holder: ReviewHolder, position: Int) {
        val model = items[position]

        GlideApp.with(activity)
            .load(getMediaLink() + model.profileImage)
            .placeholder(R.drawable.profileplaceholder)
            .into(holder.civimg)

        holder.tvusername.text = model.firstName + " " + model.lastName
        holder.tvdate.text = activity.getFormattedDate(
            model.createdDateTime,
            Const.INPUT_DATE_FORMATE, Const.OUTPUT_DATE_FORAMTE
        )
        if (model.rating != null) {
            if (model.rating.equals("Pending")) {
                holder.mrb_rating.rating = 0f
            } else {
                holder.mrb_rating.rating = model.rating.toFloat()
            }
        } else {
            holder.mrb_rating.rating = 0f
        }
        holder.tvlongcommnet.text = model.comment

    }


    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
    }
}
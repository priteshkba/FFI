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
import com.ffi.Utils.getMediaLink

class PopulerCatagoryAdapter(val activity: HomeFragment) :
    RecyclerView.Adapter<PopluerCategoryHolder>() {

    private var models = ArrayList<RecordX>()
    val mFirstWidth = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PopluerCategoryHolder {
        val view = LayoutInflater.from(activity.requireActivity())
            .inflate(R.layout.adapter_populer_category, parent, false)
        return PopluerCategoryHolder(view)
    }


    fun addModels(modellist: List<RecordX>) {
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


    override fun onBindViewHolder(holder: PopluerCategoryHolder, position: Int) {
        val model = models[position]

        holder.tvcategoryname.text = model.CategoryName

        GlideApp.with(activity)
            .asBitmap()
            .load(getMediaLink() + model.Media)
            .placeholder(R.color.color_light_grey)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    Log.e("size", "resource.width " + resource.width)
                    Log.e("size", "resource.height " + resource.height)
                    Log.e(
                        "size",
                        "holder.ivCategoryImg.measuredWidth " + holder.ivCategoryImg.measuredWidth
                    )
                    Log.e(
                        "size",
                        "holder.ivCategoryImg.measuredHeight " + holder.ivCategoryImg.measuredHeight
                    )

                    holder.ivCategoryImg.setImageBitmap(resource)

                    if(holder.ivCategoryImg.measuredHeight>0 && holder.ivCategoryImg.measuredWidth>0) {
                        Bitmap.createScaledBitmap(
                            resource,
                            holder.ivCategoryImg.measuredWidth,
                            (holder.ivCategoryImg.measuredWidth / 0.5).toInt(),
                            true
                        )
                    }

                }

                override fun onLoadCleared(placeholder: Drawable?) {

                }
            })
        Log.e("size", holder.ivCategoryImg.measuredWidth.toString())

        holder.li_maincategory.setOnClickListener {
            activity.loadCategoryFragment(false, model.ID, "", "")
        }

    }


    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
    }
}
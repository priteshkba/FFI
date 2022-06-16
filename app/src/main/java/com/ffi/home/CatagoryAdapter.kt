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


class CatagoryAdapter(val activity: HomeFragment) :
    RecyclerView.Adapter<CategoryHolder>() {

    private var models = ArrayList<MainCategory>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryHolder {
        val view = LayoutInflater.from(activity.requireActivity())
            .inflate(R.layout.adapter_category, parent, false)
        return CategoryHolder(view)
    }


    fun addModels(modellist: List<MainCategory>) {
        this.models.addAll(modellist)
        this.notifyDataSetChanged()
    }


    override fun getItemCount(): Int {
        var size = 3
        if (models.size <= 3) {
            size = models.size
        }
        return size
    }


    override fun onBindViewHolder(holder: CategoryHolder, position: Int) {
        val model = models[position]

        /* if((position%2) == 0)
         {
             holder.ivShade.visibility=View.VISIBLE
         }
         else{
             holder.ivShade.visibility=View.GONE
         }*/

        holder.tvcategoryname.text = model.Name
        holder.tvItems.text = model.TotalItems

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
                        "height img3 - " + resource.height + " width - " + resource.width
                    )
                    holder.ivCategoryImg.setImageBitmap(resource)
                }

                override fun onLoadCleared(placeholder: Drawable?) {

                }
            })

        holder.fi_main.setOnClickListener {
            activity.loadCategoryFragment(false, model.ID, "", "")
        }

    }


    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
    }
}
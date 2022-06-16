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

class CatalogAdapter(val activity: HomeFragment) :
    RecyclerView.Adapter<CatalogHolder>() {

    private var models = ArrayList<Record>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CatalogHolder {
        val view = LayoutInflater.from(activity.requireActivity())
            .inflate(R.layout.adapter_populer_catalog, parent, false)
        return CatalogHolder(view)
    }

    fun addModels(modellist: List<Record>) {
        this.models.addAll(modellist)
        this.notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        var size = 2
        if (models.size <= 2) {
            size = models.size
        }
        return size
    }


    override fun onBindViewHolder(holder: CatalogHolder, position: Int) {
        val model = models[position]
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
                        "height catalog - " + holder.ivcatlog.height + " width - " + holder.ivcatlog.width
                    )
                    holder.ivcatlog.setImageBitmap(resource)
                }

                override fun onLoadCleared(placeholder: Drawable?) {

                }
            })

        holder.tvcatlog.text = model.Name

        holder.li_catalog_Items.setOnClickListener {
            activity.loadCollectionItems(model.ID, model.Name)
        }


    }


    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
    }
}
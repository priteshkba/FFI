package com.ffi.category

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.ffi.GlideApp
import com.ffi.R
import com.ffi.Utils.Const
import com.ffi.Utils.getMediaLink

class SearchCategoryAdapter(val activity: CategoryFragment) :
    RecyclerView.Adapter<ViewHolder>() {

    private var models = ArrayList<RecordX>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var view = LayoutInflater.from(activity.requireActivity())
            .inflate(R.layout.adapter_single_image_category, parent, false)
        when (viewType) {
            Const.ONE_IMAGE -> {
                view = LayoutInflater.from(activity.requireActivity())
                    .inflate(R.layout.adapter_single_image_category, parent, false)
                return CatSingleImgHolder(view)
            }
            Const.TWO_IMAGE -> {
                view = LayoutInflater.from(activity.requireActivity())
                    .inflate(R.layout.adapter_double_img_category, parent, false)
                return CatDoubleImgHolder(view)
            }
        }
        return CatSingleImgHolder(view)
    }


    fun addModels(modellist: List<RecordX>) {
        this.models.addAll(modellist)
        this.notifyDataSetChanged()
    }


    override fun getItemCount() = models.size


    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = models[position]

        when (model.vietype) {
            Const.ONE_IMAGE -> {
                (holder as CatSingleImgHolder).tvcategoryname.text = model.name

                GlideApp.with(activity)
                    .load(getMediaLink()+model.media)
                    .placeholder(R.color.color_light_grey)
                    .into(holder.ivCategoryImg)

                holder.fi_main.setOnClickListener {
                    activity.loadProductCatalog(model.iD,model.name)
                }


            }
            Const.TWO_IMAGE -> {
                (holder as CatDoubleImgHolder).tvcategoryname.text = model.name

                GlideApp.with(activity)
                    .load(getMediaLink()+model.media)
                    .placeholder(R.color.color_light_grey)
                    .into(holder.ivCategoryImg)

                holder.tvcategoryname1.text = model.name

                GlideApp.with(activity)
                    .load(getMediaLink()+model.media)
                    .placeholder(R.color.color_light_grey)
                    .into(holder.ivCategoryImg1)

                holder.li_main.setOnClickListener { activity.loadProductCatalog(
                    model.iD,
                    model.name
                ) }
            }

        }


    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return when (models[position].vietype) {
            0 -> Const.ONE_IMAGE
            1 -> Const.TWO_IMAGE
            else -> -1
        }
    }
}
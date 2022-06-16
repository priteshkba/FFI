package com.ffi.category

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ffi.GlideApp
import com.ffi.R
import com.ffi.Utils.getMediaLink

class MainCategoryAdapter(
    val activity: CategoryFragment,
    val items: List<Record>
) :
    RecyclerView.Adapter<MainCategoryHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainCategoryHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.custom_tab_category, parent, false)

        return MainCategoryHolder(view)
    }

    override fun getItemCount() = items.size


    override fun onBindViewHolder(holder: MainCategoryHolder, position: Int) {
        val model = items[position]
        Log.e("res", "boolen " + model.isSelected + " name- " + model.Name)
        if (model.isSelected) {
            holder.cv_img.borderWidth = 5f
        } else {
            holder.cv_img.borderWidth = 0f
        }

        GlideApp.with(activity)
            .load(getMediaLink() + model.Media)
            .placeholder(R.color.color_light_grey)
            .into(holder.cv_img)

        holder.tvCategoryname.text = model.Name

        holder.li_main.setOnClickListener {
            Log.e("res", "postion--" + position)
            for (i in items.indices) {
                if (position == i) {
                    model.isSelected = true
                } else {
                    items.get(i).isSelected = false
                }
            }
            notifyItemChanged(position)
            notifyDataSetChanged()
            activity.CallApiSubCategory(model.ID, true)
        }

    }


    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
    }
}
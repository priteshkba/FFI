package com.ffi.filter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ffi.R

class MainFilterAdapter(
    val activity: FilterActivity,
    val data: List<Data>,
    val ClickInterface: FilterClickInterface
) :
    RecyclerView.Adapter<MainFilterHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainFilterHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.custom_spinner_text, parent, false)

        return MainFilterHolder(view)
    }


    override fun getItemCount() = data.size


    override fun onBindViewHolder(holder: MainFilterHolder, position: Int) {
        val model = data.get(position)
        holder.tv_main.setText(model.name)

        if (model.isSelected) {
            holder.tv_main.setBackgroundColor(activity.resources.getColor(R.color.color_white))
        } else {
            holder.tv_main.setBackgroundColor(activity.resources.getColor(R.color.color_light_grey))
        }

        holder.tv_main.setOnClickListener {
            for (i in data.indices) {
                if (i == position) {
                    model.isSelected = true
                } else {
                    data.get(i).isSelected = false
                }
            }

            notifyDataSetChanged()

            ClickInterface.vaarientClickedPosItem(position, model.iD)
        }
    }

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
    }
}
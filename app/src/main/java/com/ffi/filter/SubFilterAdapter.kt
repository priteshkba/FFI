package com.ffi.filter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ffi.R
import kotlinx.android.synthetic.main.adapter_subfilter.view.*

class SubFilterAdapter(
    val activity: FilterActivity,
    val data: List<DataX>,
    val recyclerView: RecyclerView,
    val ClickInterface: FilterClickInterface
) :
    RecyclerView.Adapter<SubFilterAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.adapter_subfilter, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = data.get(position)
        if (model.isSelected) {
            holder.chk.isChecked = true
        } else {
            holder.chk.isChecked = false
        }
        holder.chk.setText(model.variantValue)

        holder.chk.setOnCheckedChangeListener { buttonView, isChecked ->
            model.isSelected = isChecked
            recyclerView.post(Runnable {
                fun run() {
                    notifyItemChanged(position)
                }
            })
            ClickInterface.vaarientClickedPosItem(position, model.variantValueId)
        }


    }

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val chk = view.chk
    }


}



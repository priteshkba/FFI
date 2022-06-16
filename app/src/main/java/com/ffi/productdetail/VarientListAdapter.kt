package com.ffi.productdetail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ffi.R
import java.util.*
import kotlin.collections.ArrayList

class VarientListAdapter(
    val activity: ProductDetailFragment,
    val mRecyclerViewPosition: Int,
    val varientList: VariationList,
    val varientListClickInterface: VarientListClickInterface
) :
    RecyclerView.Adapter<VarientListHolder>() {

    lateinit var ValueListAdapter: SizeAdapter
    lateinit var dynamicVariantList: ArrayList<Varient>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VarientListHolder {
        val view = LayoutInflater.from(activity.requireActivity())
            .inflate(R.layout.adapter_varient, parent, false)
        return VarientListHolder(view)
    }

    override fun getItemCount() = 1


    override fun onBindViewHolder(holder: VarientListHolder, position: Int) {
        //val model = varientList[position]

        val name = (varientList.name?.substring(0, 1)
            ?.toUpperCase(Locale.ROOT) ?: "") + varientList.name?.substring(1)
        holder.tvVarientTitle.text = name

        ValueListAdapter = SizeAdapter(
            activity,
            mRecyclerViewPosition,
            varientList.iD,
            dynamicVariantList,
            varientListClickInterface
        )
        holder.rvVarient_Value.setLayoutManager(
            LinearLayoutManager(
                activity.requireActivity(),
                RecyclerView.HORIZONTAL,
                false
            )
        )
        holder.rvVarient_Value.setNestedScrollingEnabled(false)
        holder.rvVarient_Value.setAdapter(ValueListAdapter)
    }

    fun setDynamicVariantList(varent: List<Varient>?) {
        dynamicVariantList = ArrayList()
        dynamicVariantList.addAll(varent!!)
        notifyDataSetChanged()
    }


    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
    }
}
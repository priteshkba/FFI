package com.ffi.productdetail

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.adapter_varient.view.*

class VarientListHolder(itemview:View): RecyclerView.ViewHolder(itemview) {
    val tvVarientTitle=itemview.tvvarient_title
    val rvVarient_Value=itemview.rvvarient_value
}

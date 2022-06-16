package com.ffi.home

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.adapter_populer_catalog.view.*

class CatalogHolder(itemview:View): RecyclerView.ViewHolder(itemview) {
    val tvcatlog=itemview.tvCatalog
    val ivcatlog=itemview.ivCatalog
    val li_catalog_Items=itemview.li_catalog_Items

}

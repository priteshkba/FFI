package com.ffi.allcataloglist

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.adapter_collectionlist.view.*


class CollectionListHolder(itemview: View) :
    RecyclerView.ViewHolder(itemview) {

    val li_main = itemview.li_main
    val tvitemname=itemview.tvItems
    val tvprice=itemview.tvPrice
    val mainimg=itemview.ivimg1
    val tvDesc=itemview.tvDesc

    val tvOthers=itemview.tvOthers
    val tvfacebook=itemview.tvfacebook
    val tvDwonload=itemview.tvdownload

    val li_whatsapp=itemview.li_whatsapp
    val li_download=itemview.li_download
    val li_fb=itemview.li_fb
    val li_othershare=itemview.li_othershare

    val ivimg1=itemview.ivimg1



}

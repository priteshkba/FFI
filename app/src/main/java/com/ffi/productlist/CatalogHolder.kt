package com.ffi.productlist

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.adapter_catalog.view.*

class CatalogHolder(itemview: View) :
    RecyclerView.ViewHolder(itemview) {

    val li_main = itemview.li_main
    val li_img_count = itemview.li_img_count
    val tvitemname=itemview.tvItems
    val tvprice=itemview.tvPrice
    val mainimg=itemview.ivimg1
    val img2=itemview.ivimg2
    val img3=itemview.ivimg3
    val img_count=itemview.tvmoreimg_count

    val tvOthers=itemview.tvOthers
    val tvfacebook=itemview.tvfacebook
    val tvDwonload=itemview.tvdownload

    val li_whatsapp=itemview.li_whatsapp
    val li_download=itemview.li_download
    val li_fb=itemview.li_fb
    val li_othershare=itemview.li_othershare

    val ivplay1=itemview.ivplay1
    val ivplay2=itemview.ivplay2
    val ivplay3=itemview.ivplay3

    val cv_left_card=itemview.cv_left_card
    val ll_right_card=itemview.ll_right_card
    val cv_right_image1=itemview.cv_right_image1
    val cv_right_image2=itemview.cv_right_image2


}

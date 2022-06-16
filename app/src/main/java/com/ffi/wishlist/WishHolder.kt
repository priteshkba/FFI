package com.ffi.wishlist

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.adapter_wishlist.view.*

class WishHolder(itemview: View) :
    RecyclerView.ViewHolder(itemview) {

    val ivWishItem = itemview.ivWishItem
    val ivAddCart = itemview.ivAddCart

    val li_main = itemview.li_main

    val tvItemName = itemview.tvItemName
    val tvPrice = itemview.tvPrice
    val tvSize = itemview.tvSize
    val tvsoldby = itemview.tvsoldby

    val ivDelete = itemview.ivDelete

}

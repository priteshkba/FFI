package com.ffi.cart

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.adapter_cart.view.*

open class CartHolder(
    activity: CartFragment,
    itemview: View
) :
    RecyclerView.ViewHolder(itemview) {

  //  val swipeRevealLayout = itemview.swipe_layout
    val tvSize = itemview.tvSize
    val tvProductName = itemview.tvProductName
    val ivcatlog = itemview.ivProductImg
    val ivDelete = itemview.ivDelete
    val ivPlus = itemview.ivPlus
    val ivMinus = itemview.ivMinus
    val tvQty = itemview.tvQty
    val tvPrice = itemview.tvPrice
    val tvSeller = itemview.tvSeller
    val fi_main_layout = itemview.fi_main_layout
    val iv_wish = itemview.iv_wish

    @kotlin.jvm.JvmField
    var li_main = itemview.li_main

    /*@kotlin.jvm.JvmField
    var mActionContainer = itemview.view_list_repo_action_container*/

}

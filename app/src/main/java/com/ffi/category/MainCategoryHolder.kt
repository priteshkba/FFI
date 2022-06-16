package com.ffi.category

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.custom_tab_category.view.*

class MainCategoryHolder(itemview: View) :
    RecyclerView.ViewHolder(itemview) {

    val cv_img = itemview.cv_img
    val tvCategoryname = itemview.tvCategoryname

    val li_main = itemview.li_category


}

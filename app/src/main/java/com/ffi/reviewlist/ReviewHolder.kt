package com.ffi.reviewlist

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.adapter_reviews.view.*

class ReviewHolder(itemview: View) :
    RecyclerView.ViewHolder(itemview){

    val v_divider=itemview.v_divder
    val civimg=itemview.civ_user_img
    val tvusername=itemview.tvusername
    val tvdate=itemview.tvcomntdate
    val mrb_rating=itemview.mrb_rating
    val tvshortcmnt=itemview.tvshort_comment
    val tvlongcommnet=itemview.tvlongcomnt



}

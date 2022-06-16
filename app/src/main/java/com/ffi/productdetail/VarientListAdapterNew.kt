package com.ffi.productdetail

import android.app.Activity
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.widget.ImageViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.ffi.R

class VarientListAdapterNew(
    val mActivity: Activity,
    val mRecyclerViewPosition: Int,
    val mVariationListID: Int,
    val mVariationList: VariationList,
    val mVarientListClickInterfaceNew: VarientListClickInterfaceNew
) :
    RecyclerView.Adapter<SizeHolderNew>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SizeHolderNew {
        val view = LayoutInflater.from(mActivity).inflate(R.layout.adapter_size2, parent, false)
        return SizeHolderNew(view)
    }

    override fun getItemCount() = mVariationList.list?.size ?: 0


    override fun onBindViewHolder(holder: SizeHolderNew, position: Int) {
        val model = mVariationList.list?.get(position)
/*        Log.e("tagAdapter", " " )
        Log.e("tagAdapter", "position " + position)
        Log.e("tagAdapter", "model.value " + model.value)
        Log.e("tagAdapter", "model.selected " + model.selected)
        Log.e("tagAdapter", "model._isEnable " + model.get_isEnable())
        Log.e("tagAdapter", "model.code " + model.code)*/

        if(model?.get_isEnable()!!){
            if (model?.code.startsWith("#")) {
                holder.ivDivideLine.visibility = View.GONE
                holder.tvSize.visibility = View.GONE
                holder.li_color.visibility = View.VISIBLE

                if (model.selected) {
                    try{
                        if(model.code.equals("#ffffff")){
                            val layerDrawable = mActivity.resources
                                .getDrawable(R.drawable.shape_selected_black_color_corner) as LayerDrawable
                            val gradientDrawable = layerDrawable
                                .findDrawableByLayerId(R.id.inner_color) as GradientDrawable
                            val gradientDrawable2 = layerDrawable
                                .findDrawableByLayerId(R.id.border_color) as GradientDrawable
                            gradientDrawable.setColor(Color.parseColor(model.code))
                            gradientDrawable2.setStroke(5, Color.parseColor(model.code))
                            holder.li_view_selected.background = layerDrawable
                            holder.iv_view_selected.visibility = View.VISIBLE
                            if(model.quantity > 0){
                                holder.iv_view_selected.setImageResource(R.drawable.iv_check_24_black)
                            }
                            else {
                                holder.iv_view_selected.setImageResource(R.drawable.iv_close)
                            }
                            holder.iv_view_selected.setColorFilter(ContextCompat.getColor(mActivity, R.color.color_black), android.graphics.PorterDuff.Mode.MULTIPLY)
                            holder.iv_view_selected.setColorFilter(ContextCompat.getColor(mActivity, R.color.color_black), android.graphics.PorterDuff.Mode.SRC_IN)
                            ImageViewCompat.setImageTintList(holder.iv_view_selected, ColorStateList.valueOf(ContextCompat.getColor(mActivity, R.color.color_black)))
                        }
                        else {
                            val layerDrawable = mActivity.resources
                                .getDrawable(R.drawable.shape_selected_color_corner) as LayerDrawable
                            val gradientDrawable = layerDrawable
                                .findDrawableByLayerId(R.id.inner_color) as GradientDrawable
                            val gradientDrawable2 = layerDrawable
                                .findDrawableByLayerId(R.id.border_color) as GradientDrawable
                            gradientDrawable.setColor(Color.parseColor(model.code))
                            gradientDrawable2.setStroke(5, Color.parseColor(model.code))
                            if(model.quantity > 0){
                                holder.iv_view_selected.setImageResource(R.drawable.iv_check_24)
                            }
                            else {
                                holder.iv_view_selected.setImageResource(R.drawable.iv_close)
                                holder.iv_view_selected.setColorFilter(ContextCompat.getColor(mActivity, R.color.color_white), android.graphics.PorterDuff.Mode.MULTIPLY)
                                holder.iv_view_selected.setColorFilter(ContextCompat.getColor(mActivity, R.color.color_white), android.graphics.PorterDuff.Mode.SRC_IN)
                            }
                            holder.li_view_selected.background = layerDrawable
                            holder.iv_view_selected.visibility = View.VISIBLE
                        }
                    }catch (e: Exception){}
                }
                else {
                    try{
                        val unwrappedDrawable =
                            AppCompatResources.getDrawable(
                                mActivity, R.drawable.shape_color_corner)
                        val wrappedDrawable: Drawable? =
                            unwrappedDrawable?.let { DrawableCompat.wrap(it) }
                        if (wrappedDrawable != null) {
                            DrawableCompat.setTint(wrappedDrawable, Color.parseColor(model.code))
                        }
                        holder.li_view_selected.setBackground(wrappedDrawable)
                        holder.iv_view_selected.visibility = View.GONE
                    }catch (e: Exception){}
                }

            } else {
                if(model.quantity > 0){
                    holder.tvSize.visibility = View.VISIBLE
                    holder.ivDivideLine.visibility = View.GONE
                }
                else {
                    if(!model.isQuantityFound){
                        holder.tvSize.visibility = View.VISIBLE
                        holder.ivDivideLine.visibility = View.GONE
                    }
                    else {
                        holder.tvSize.visibility = View.VISIBLE
                        holder.ivDivideLine.visibility = View.GONE
                    }
                }
                holder.li_color.visibility = View.GONE
                holder.tvSize.setText(model.value)

            }

            if(model.selected){
                holder.tvSize.setTextColor(mActivity.resources.getColor(R.color.color_white))
                holder.tvSize.background = mActivity.resources.getDrawable(R.drawable.bg_selected_size)
                holder.tvSize.isSelected = true
            }
            else {
                holder.tvSize.setTextColor(mActivity.resources.getColor(R.color.color_black))
                holder.tvSize.background = mActivity.resources.getDrawable(R.drawable.bg_unselected_size)
                holder.tvSize.isSelected = false
            }
        }
        else {
            holder.ivDivideLine.visibility = View.GONE
            holder.tvSize.setText(model.value)
            holder.tvSize.setTextColor(mActivity.resources.getColor(R.color.divder_color))
            holder.tvSize.background = mActivity.resources.getDrawable(R.drawable.bg_disable_size)

            try{
                val unwrappedDrawable =
                    AppCompatResources.getDrawable(
                        mActivity, R.drawable.shape_color_corner)
                val wrappedDrawable: Drawable? =
                    unwrappedDrawable?.let { DrawableCompat.wrap(it) }
                if (wrappedDrawable != null) {
                    DrawableCompat.setTint(wrappedDrawable, Color.parseColor(model.code))
                }
                holder.li_view_selected.setBackground(wrappedDrawable)
                holder.iv_view_selected.visibility = View.GONE
            }catch (e: Exception){}

        }


        holder.tvSize.setOnClickListener {
            /*if (!model._isEnable) {
                mActivity.showToast("Item not availble in " + model.value)
                return@setOnClickListener
            }*/
            Log.e("tagVarient", "holder.tvSize.setOnClickListener clicked")
            holder.tvSize.isSelected = true
            model.selected = true
            model.set_isEnable(true)
            selectPosition(position)
            model.selected = true
            mVarientListClickInterfaceNew.varientClickedPosItem(position, mRecyclerViewPosition, mVariationListID, model)
            holder.tvSize.isSelected = true
            model.set_isEnable(true)
            notifyItemChanged(position)
        }

        holder.li_color.setOnClickListener {
            holder.tvSize.isSelected = true
            model.selected = true
            model.set_isEnable(true)
            selectPosition(position)
            model.selected = true
            mVarientListClickInterfaceNew.varientClickedPosItem(position, mRecyclerViewPosition, mVariationListID, model)
            holder.tvSize.isSelected = true
            model.set_isEnable(true)
            notifyItemChanged(position)
        }

    }

    fun selectPosition(position: Int){
        for((i, mItem) in mVariationList.list?.withIndex()!!){
            if(position == i){
                mItem.selected = true
                mItem.set_isEnable(true)
            }
            else {
                mItem.selected = false
            }
        }
        notifyDataSetChanged()
    }

}
package com.ffi.productdetail

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.RecyclerView
import com.ffi.R
import com.ffi.Utils.Const
import com.ffi.Utils.showToast
import java.util.*


class SizeAdapter(
    val activity: ProductDetailFragment,
    val mRecyclerViewPosition: Int,
    val varientid: Int,
    val arrlist: ArrayList<Varient>,
    val varientListClickInterface: VarientListClickInterface
) :
    RecyclerView.Adapter<SizeHolder>() {

    val ClickInterface: VarientListClickInterface = varientListClickInterface

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SizeHolder {
        val view = LayoutInflater.from(activity.requireActivity())
            .inflate(R.layout.adapter_size, parent, false)
        return SizeHolder(view)
    }

    override fun getItemCount() = arrlist.size


    override fun onBindViewHolder(holder: SizeHolder, position: Int) {
        val model = arrlist[position]

/*
        if (position == 0) {
            model.isSelected = true
        }
*/

        if (model.code.startsWith("#")) {
            holder.tvSize.visibility = View.GONE
            holder.li_color.visibility = View.VISIBLE

        } else {
            holder.tvSize.visibility = View.VISIBLE
            holder.li_color.visibility = View.GONE

            val name =
                model.value.substring(0, 1).toUpperCase(Locale.ROOT) + model.value.substring(1)
            holder.tvSize.text = name
        }

        // Log.e("var", model.value + "----" + model._isEnable)
        if (model._isEnable) {
            if (model.selected) {
                holder.tvSize.setTextColor(activity.resources.getColor(R.color.color_white))
                holder.tvSize.background =
                    activity.resources.getDrawable(R.drawable.bg_selected_size)

                if (model.code.startsWith("#")) {
                    val layerDrawable = activity.resources
                        .getDrawable(R.drawable.shape_selected_color_corner) as LayerDrawable
                    val gradientDrawable = layerDrawable
                        .findDrawableByLayerId(R.id.inner_color) as GradientDrawable
                    val gradientDrawable2 = layerDrawable
                        .findDrawableByLayerId(R.id.border_color) as GradientDrawable
                    gradientDrawable.setColor(Color.parseColor(model.code))
                    gradientDrawable2.setStroke(5, Color.parseColor(model.code))

                    holder.li_color.background = layerDrawable
                }

                //holder.li_color.background =
                //  activity.resources.getDrawable(R.drawable.bg_selected_size)
            } else {
                holder.tvSize.setTextColor(activity.resources.getColor(R.color.color_black))
                holder.tvSize.background =
                    activity.resources.getDrawable(R.drawable.bg_unselected_size)
                if (model.code.startsWith("#")) {
                    val unwrappedDrawable =
                        AppCompatResources.getDrawable(
                            activity.requireContext(),
                            R.drawable.shape_color_corner
                        )
                    val wrappedDrawable: Drawable? =
                        unwrappedDrawable?.let { DrawableCompat.wrap(it) }
                    if (wrappedDrawable != null) {
                        DrawableCompat.setTint(wrappedDrawable, Color.parseColor(model.code))
                    }
                    holder.li_color.setBackground(wrappedDrawable)
                }
            }

        } else {
            holder.tvSize.setTextColor(activity.resources.getColor(R.color.divder_color))
            holder.tvSize.background = activity.resources.getDrawable(R.drawable.bg_disable_size)

            if (model.code.startsWith("#")) {
                val unwrappedDrawable =
                    AppCompatResources.getDrawable(
                        activity.requireContext(),
                        R.drawable.shape_color_corner
                    )
                val wrappedDrawable: Drawable? =
                    unwrappedDrawable?.let { DrawableCompat.wrap(it) }
                if (wrappedDrawable != null) {
                    DrawableCompat.setTint(wrappedDrawable, Color.parseColor(model.code))
                }
                holder.li_color.setBackground(wrappedDrawable)
            }
            /*  holder.li_color.background =
                  activity.resources.getDrawable(R.drawable.bg_disable_size)*/
        }


        holder.tvSize.setOnClickListener {
            if (!model._isEnable) {
                activity.requireActivity().showToast(
                    activity.getString(R.string.product_availability) + " " + model.value,
                    Const.ALERT
                )
                return@setOnClickListener
            }
            holder.tvSize.background = activity.resources.getDrawable(R.drawable.bg_selected_size)
            holder.tvSize.setTextColor(activity.resources.getColor(R.color.color_white))

            ClickInterface.vaarientClickedPosItem(mRecyclerViewPosition, varientid, position, model)


            for (i in arrlist.indices) {
                if (i == position) {
                    model.selected = true
                } else {
                    arrlist.get(i).selected = false
                }
            }
            notifyDataSetChanged()
        }


        holder.li_color.setOnClickListener {
            if (!model._isEnable) {
                activity.requireActivity().showToast(
                    activity.requireActivity()
                        .getString(R.string.msg_item_not_available) + model.value, Const.ALERT
                )
                return@setOnClickListener
            }


            ClickInterface.vaarientClickedPosItem(mRecyclerViewPosition, varientid, position, model)

            for (i in arrlist.indices) {
                if (i == position) {
                    model.selected = true
                } else {
                    arrlist.get(i).selected = false
                }
            }

            notifyDataSetChanged()
        }


        if (model.shouldDisplay) {

        } else {
//            holder.tvSize.visibility = View.GONE
        }

    }


    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
    }
}
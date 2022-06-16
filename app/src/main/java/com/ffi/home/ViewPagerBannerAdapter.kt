package com.ffi.home

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.SystemClock
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.ffi.GlideApp
import com.ffi.R
import com.ffi.Utils.getMediaLink

class ViewPagerBannerAdapter(
    private val mContext: HomeFragment,
    private val mResources: ArrayList<Banner>
) :
    PagerAdapter() {

    var lastTimeSel: Long = 0
    var mLastClickTime: Long = 0

    override fun getCount(): Int {
        return mResources.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object` as LinearLayout
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val itemView =
            LayoutInflater.from(mContext.requireActivity())
                .inflate(R.layout.adapter_banner, container, false)

        val imageView = itemView.findViewById(R.id.ivbanner) as ImageView
        val tvtitle = itemView.findViewById(R.id.tvbanner_title) as TextView
        val tvdesc = itemView.findViewById(R.id.tvbanner_desc) as TextView

        GlideApp.with(mContext)
            .asBitmap()
            .load(getMediaLink() + mResources[position].Media)
            .placeholder(R.color.color_light_grey)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap>?
                ) {
                    Log.e(
                        "size",
                        "height banner - " + imageView.height + " width - " + imageView.width
                    )
                    imageView.setImageBitmap(resource)
                }

                override fun onLoadCleared(placeholder: Drawable?) {

                }
            })

        tvtitle.text = mResources[position].Title
        tvdesc.text = mResources[position].Description

        imageView.setOnClickListener {
            lastTimeSel = SystemClock.elapsedRealtime() - mLastClickTime;
            if (lastTimeSel > 0 && lastTimeSel < 500) {
                return@setOnClickListener
            }
            mLastClickTime = SystemClock.elapsedRealtime()
            mContext.BannerRedirection(position)
        }

        container.addView(itemView)
        return itemView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as LinearLayout)
    }
}
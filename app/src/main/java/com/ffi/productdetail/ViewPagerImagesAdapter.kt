package com.ffi.productdetail

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.SystemClock
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.ffi.FullImageview.FullImageActivity
import com.ffi.GlideApp
import com.ffi.R
import com.ffi.Utils.Const
import com.ffi.Utils.getMediaLink
import com.ffi.productlist.Media
import com.google.gson.Gson


class ViewPagerImagesAdapter(
    private val mContext: Context,
    private val mResources: ArrayList<Media>
) : RecyclerView.Adapter<ViewPagerImagesAdapter.ViewHolder?>() {

    var lastTimeSel: Long = 0
    var mLastClickTime: Long = 0
    lateinit var itemView: View
    var mFirstWidth = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.pager_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        GlideApp.with(mContext)
            .load(getMediaLink() + mResources[position].Media)
            .placeholder(R.color.color_light_grey)
            .error(R.color.color_light_grey)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    modelany: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {

                    android.os.Handler().post{
                        kotlin.run {
                            var firstImageUrl = ""

                            if (mResources[position].MediaTypeId.equals(Const.MEDIA_VIDEO)) {
                                for (i in mResources.indices) {
                                    if (!mResources.get(i).MediaTypeId.equals(Const.MEDIA_VIDEO)) {
                                        firstImageUrl = mResources.get(i).Media
                                        break
                                    }
                                }

                            } else {
                                firstImageUrl = mResources[position].Media
                            }

                            if (!firstImageUrl.isEmpty()) {
                                GlideApp.with(mContext)
                                    .load(getMediaLink() + firstImageUrl)
                                    .placeholder(R.color.color_light_grey)
                                    .error(R.color.color_light_grey)
                                    .into(holder.imageView)
                            } else {
                                GlideApp.with(mContext)
                                    .load(getMediaLink() + mResources[position].Media)
                                    .placeholder(R.color.color_light_grey)
                                    .error(R.color.color_light_grey)
                                    .into(holder.imageView)
                            }
                        }
                    }

                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }

            })
            .into(holder.imageView)




        Log.e("img", getMediaLink() + mResources[position].Media)

        if (mResources[position].MediaTypeId.equals(Const.MEDIA_VIDEO)) {
            holder.ivplay.visibility = View.VISIBLE
        } else {
            holder.ivplay.visibility = View.GONE
        }

        holder.imageView.setOnClickListener {
            lastTimeSel = SystemClock.elapsedRealtime() - mLastClickTime;
            if (lastTimeSel > 0 && lastTimeSel < 500) {
                return@setOnClickListener
            }
            mLastClickTime = SystemClock.elapsedRealtime()
            val intent = Intent(mContext, FullImageActivity::class.java)
            val gson = Gson()
            intent.putExtra(Const.DATA, gson.toJson(mResources))
            intent.putExtra(Const.POSITION, position)
            mContext.startActivity(intent)
        }

        if (mFirstWidth > 0) {
            Log.e("height ", mFirstWidth.toString())
            holder.rl_pager_item.layoutParams.width = mFirstWidth
            holder.rl_pager_item.layoutParams.height = (mFirstWidth * 1.5f).toInt()
        }

    }

    override fun getItemCount(): Int {
        return mResources.size
    }

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val frm_pager_item = itemView.findViewById(R.id.frm_pager_item) as FrameLayout
        val rl_pager_item = itemView.findViewById(R.id.rl_pager_item) as RelativeLayout
        val imageView = itemView.findViewById(R.id.img_pager_item) as ImageView
        val ivplay = itemView.findViewById(R.id.ivplay) as ImageView
    }
}
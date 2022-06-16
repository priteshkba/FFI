package com.ffi.home

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.ffi.GlideApp
import com.ffi.R
import com.ffi.Utils.*

class SearchProductAdapter(val activity: HomeFragment, val models: List<RecordXXX>) :
    RecyclerView.Adapter<com.ffi.productlist.CatalogHolder>() {

    //private var models = ArrayList<RecordXXX>()
    var mFirstWidth = 0
    var mSecondWidth = 0

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): com.ffi.productlist.CatalogHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.adapter_catalog, parent, false)

        return com.ffi.productlist.CatalogHolder(view)
    }

    /* fun addModels(modellist: List<RecordXXX>) {
         this.models.addAll(modellist)
         this.notifyDataSetChanged()
     }*/


    override fun getItemCount() = models.size


    override fun onBindViewHolder(holder: com.ffi.productlist.CatalogHolder, position: Int) {
        val model = models[position]

        if (model.media.size != 0) {


/*
            Glide.with(activity)
                .load(getMediaLink() + model.media.get(0).media)
                .placeholder(R.color.color_light_grey)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        models: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        android.os.Handler().post{
                            kotlin.run {
                                if (model.media.get(0).mediaTypeId.equals(Const.MEDIA_VIDEO)) {
                                    var firstImageUrl = ""
                                    for (i in model.media.indices) {
                                        if (!model.media.get(i).mediaTypeId.equals(Const.MEDIA_VIDEO)) {
                                            Log.e("thumnail", "index: " + i.toString())
                                            firstImageUrl = model.media.get(i).media
                                            break
                                        }
                                    }
                                    if (!firstImageUrl.isEmpty()) {
                                        GlideApp.with(activity)
                                            .load(getMediaLink() + firstImageUrl)
                                            .placeholder(R.color.color_light_grey)
                                            .into(holder.mainimg)
                                    } else {
                                        GlideApp.with(activity)
                                            .load(getMediaLink() + model.media.get(0).media)
                                            .placeholder(R.color.color_light_grey)
                                            .into(holder.mainimg)
                                    }
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
                .into(holder.mainimg)*/

            /////////////////////////

            DisplayThumbnail(activity,getMediaLink(),0,model,holder.mainimg)


            if (model.media.get(0).mediaTypeId.equals(Const.MEDIA_VIDEO)) {
                holder.ivplay1.visibility = View.VISIBLE
            } else {
                holder.ivplay1.visibility = View.GONE

            }
        }else{
            holder.ivplay1.visibility = View.GONE
        }

        if (model.media.size > 1) {





            /*GlideApp.with(activity)
                .load(getMediaLink() + model.media.get(1).media)
                .placeholder(R.color.color_light_grey)
                .listener(object :RequestListener<Drawable>{
                    override fun onLoadFailed(
                        e: GlideException?,
                        modelany: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        android.os.Handler().post{
                            kotlin.run {
                                var firstImageUrl = ""
                                if (model.media.get(1).mediaTypeId.equals(Const.MEDIA_VIDEO)) {
                                    Log.e("thumnail", "second image has video")
                                    for (i in model.media.indices) {
                                        if (!model.media.get(i).mediaTypeId.equals(Const.MEDIA_VIDEO)) {
                                            Log.e("thumnail", "index:: " + i.toString())
                                            firstImageUrl = model.media.get(i).media
                                            break
                                        }
                                    }
                                } else {
                                    Log.e("thumnail", "second image not has video")
                                    firstImageUrl = model.media.get(1).media
                                }
                                if (!firstImageUrl.isEmpty()) {
                                    GlideApp.with(activity)
                                        .load(getMediaLink() + firstImageUrl)
                                        .placeholder(R.color.color_light_grey)
                                        .into(holder.img2)
                                } else {
                                    GlideApp.with(activity)
                                        .load(getMediaLink() + model.media.get(1).media)
                                        .placeholder(R.color.color_light_grey)
                                        .into(holder.img2)
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
                .into(holder.img2)*/

            DisplayThumbnail(activity,getMediaLink(),1,model,holder.img2)

            if (model.media.get(1).mediaTypeId.equals(Const.MEDIA_VIDEO)) {
                holder.ivplay2.visibility = View.VISIBLE
            } else {
                holder.ivplay2.visibility = View.GONE

            }

        }else{
            holder.ivplay2.visibility = View.GONE
        }

        if (model.media.size > 2) {


            /*GlideApp.with(activity)
                .load(getMediaLink() + model.media.get(2).media)
                .placeholder(R.color.color_light_grey)
                .listener(object :RequestListener<Drawable>{
                    override fun onLoadFailed(
                        e: GlideException?,
                        modelany: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        android.os.Handler().post{
                            kotlin.run {
                                var firstImageUrl = ""
                                if (model.media.get(2).mediaTypeId.equals(Const.MEDIA_VIDEO)) {
                                    Log.e("thumnail", "third image has video")
                                    for (i in model.media.indices) {
                                        if (!model.media.get(i).mediaTypeId.equals(Const.MEDIA_VIDEO)) {
                                            Log.e("thumnail", "index:: " + i.toString())
                                            firstImageUrl = model.media.get(i).media
                                            break
                                        }
                                    }
                                } else {
                                    Log.e("thumnail", "second image not has video")
                                    firstImageUrl = model.media.get(2).media
                                }
                                if (!firstImageUrl.isEmpty()) {
                                    GlideApp.with(activity)
                                        .load(getMediaLink() + firstImageUrl)
                                        .placeholder(R.color.color_light_grey)
                                        .into(holder.img3)
                                } else {
                                    GlideApp.with(activity)
                                        .load(getMediaLink() + model.media.get(2).media)
                                        .placeholder(R.color.color_light_grey)
                                        .into(holder.img3)
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
                .into(holder.img3)*/

            DisplayThumbnail(activity,getMediaLink(),2,model,holder.img3)

            if (model.media.get(2).mediaTypeId.equals(Const.MEDIA_VIDEO)) {
                holder.ivplay3.visibility = View.VISIBLE
            } else {
                holder.ivplay3.visibility = View.GONE

            }
        }else{
            holder.ivplay3.visibility = View.GONE
        }

        if (model.media.size > 3) {
            holder.li_img_count.visibility = View.VISIBLE
            holder.img_count.text =
                activity.getString(R.string.plus) + (model.media.size - 3).toString()
        } else {
            holder.li_img_count.visibility = View.GONE
        }

        holder.tvitemname.text = model.title
        holder.tvprice.text = activity.getString(R.string.currency) + " " + model.price

        if (mFirstWidth > 0) {
            holder.cv_left_card.layoutParams.width = mFirstWidth
            holder.cv_left_card.layoutParams.height = (mFirstWidth * 1.7f).toInt()
        }

        if (mSecondWidth > 0) {
            holder.ll_right_card.layoutParams.width = mSecondWidth
            holder.ll_right_card.layoutParams.height = (mFirstWidth * 1.7f).toInt()

            holder.cv_right_image1.layoutParams.width = mSecondWidth
            holder.cv_right_image1.layoutParams.height = (((mFirstWidth * 1.7f) / 2f) - 12f).toInt()

            holder.cv_right_image2.layoutParams.width = mSecondWidth
            holder.cv_right_image2.layoutParams.height = (((mFirstWidth * 1.7f) / 2f) - 12f).toInt()
        }

        holder.li_main.setOnClickListener {
            activity.loadProductDetail(model.iD)
        }

        holder.li_download.setOnClickListener {
            Log.e("share", "click on download")
            activity.DownloadTask(model.media, model.iD, true).execute()

        }
        holder.li_fb.setOnClickListener {
            if (getUserId().isEmpty()) {
                /*val intent = Intent(activity.context, LoginActivity::class.java)
                activity.startActivity(intent)
                App.isRedirect = true*/
                activity.requireActivity().showLoginMsg()
            } else {
                activity.DialogShare(model, Const.FACEBOOK)
            }
        }

        holder.li_whatsapp.setOnClickListener {
            if (getUserId().isEmpty()) {
                /*val intent = Intent(activity.context, LoginActivity::class.java)
                activity.startActivity(intent)
                App.isRedirect = true*/
                activity.requireActivity().showLoginMsg()
            } else {
                activity.DialogShare(model, Const.WHATSAPP)
            }
        }
        holder.li_othershare.setOnClickListener {
            if (getUserId().isEmpty()) {
                /* val intent = Intent(activity.context, LoginActivity::class.java)
                 activity.startActivity(intent)
                 App.isRedirect = true*/
                activity.requireActivity().showLoginMsg()
            } else {
                activity.DialogShare(model, Const.OTHERS)
            }
        }
    }

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
    }
}
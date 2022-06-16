package com.ffi.sharedcatalog

import android.content.Intent
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.ffi.App
import com.ffi.GlideApp
import com.ffi.R
import com.ffi.Utils.*
import com.ffi.login.LoginActivity
import com.ffi.model.Record
import com.ffi.productlist.CatalogHolder
import java.io.File


class SharedCatalogAdapter(val activity: SharedCatalogFragment) :
    RecyclerView.Adapter<CatalogHolder>() {

    private var models = ArrayList<Record>()
    var mFirstWidth = 0
    var mSecondWidth = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CatalogHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.adapter_catalog, parent, false)

        return CatalogHolder(view)
    }


    fun addModels(modellist: List<Record>) {
        this.models.addAll(modellist)
        this.notifyDataSetChanged()
    }


    override fun getItemCount() = models.size


    override fun onBindViewHolder(holder: CatalogHolder, position: Int) {
        GlideApp.with(activity).clear(holder.img2)
        GlideApp.with(activity).clear(holder.mainimg)
        GlideApp.with(activity).clear(holder.img3)

        val model = models[position]

        if (model.media.size != 0) {

            Glide.with(activity)
                .asFile()
                .load(getMediaLink() + model.media.get(0).media)
                .placeholder(R.color.color_light_grey)
                .into(object: CustomTarget<File>() {
                    override fun onLoadCleared(placeholder: Drawable?) {

                    }

                    override fun onResourceReady(
                        resource: File,
                        transition: Transition<in File>?
                    ) {
                        Glide.with(activity)
                            .load(resource)
                            .placeholder(R.color.color_light_grey)
                            .into(holder.mainimg)
                    }

                })

            if (model.media.get(0).mediaTypeId.equals(Const.MEDIA_VIDEO)) {
                holder.ivplay1.visibility = View.VISIBLE
            }

           /* GlideApp.with(activity)
                .load(getMediaLink() + model.media.get(0).media)
                .placeholder(R.color.color_light_grey)
                .into(holder.mainimg)*/
        }else{
            GlideApp.with(activity)
                .load("")
                .placeholder(R.color.color_light_grey)
                .into(holder.mainimg)
        }

        if (model.media.size > 1) {

            Glide.with(activity)
                .asFile()
                .load(getMediaLink() + model.media.get(1).media)
                .placeholder(R.color.color_light_grey)
                .into(object: CustomTarget<File>() {
                    override fun onLoadCleared(placeholder: Drawable?) {

                    }

                    override fun onResourceReady(
                        resource: File,
                        transition: Transition<in File>?
                    ) {
                        Glide.with(activity)
                            .load(resource)
                            .placeholder(R.color.color_light_grey)
                            .into(holder.img2)
                    }

                })

        }else{
            GlideApp.with(activity)
                .load("")
                .placeholder(R.color.color_light_grey)
                .into(holder.img2)
        }

        if(mFirstWidth > 0){
            holder.cv_left_card.layoutParams.width = mFirstWidth
            holder.cv_left_card.layoutParams.height = (mFirstWidth * 1.6f).toInt()
        }

        if(mSecondWidth > 0){
            holder.ll_right_card.layoutParams.width = mSecondWidth
            holder.ll_right_card.layoutParams.height = (mFirstWidth * 1.6f).toInt()

            holder.cv_right_image1.layoutParams.width = mSecondWidth
            holder.cv_right_image1.layoutParams.height = (((mFirstWidth * 1.6f) / 2f) - 12f).toInt()

            holder.cv_right_image2.layoutParams.width = mSecondWidth
            holder.cv_right_image2.layoutParams.height = (((mFirstWidth * 1.6f) / 2f) - 12f).toInt()
        }

        if (model.media.size > 2) {

            Glide.with(activity)
                .asFile()
                .load(getMediaLink() + model.media.get(2).media)
                .placeholder(R.color.color_light_grey)
                .into(object: CustomTarget<File>() {
                    override fun onLoadCleared(placeholder: Drawable?) {

                    }

                    override fun onResourceReady(
                        resource: File,
                        transition: Transition<in File>?
                    ) {
                        Glide.with(activity)
                            .load(resource)
                            .placeholder(R.color.color_light_grey)
                            .into(holder.img3)
                    }

                })

        }else{
            GlideApp.with(activity)
                .load("")
                .placeholder(R.color.color_light_grey)
                .into(holder.img3)
        }

        if (model.media.size > 3) {
            holder.li_img_count.visibility = View.VISIBLE
            holder.img_count.text =
                activity.getString(R.string.plus) + (model.media.size - 3).toString()
        } else {
            holder.li_img_count.visibility = View.GONE
        }

        holder.tvitemname.text = model.name
        holder.tvprice.text = model.description


        holder.li_main.setOnClickListener {

            activity.loadCollectionItems(model.iD, model.name)
        }

        holder.tvOthers.setOnClickListener {
            if (getUserId().isEmpty()) {
                /*val intent = Intent(activity.context, LoginActivity::class.java)
                activity.startActivity(intent)
                App.isRedirect = true*/
                activity.requireActivity().showLoginMsg()
            } else {
                activity.DialogShare(model, Const.OTHERS)
            }
        }

        holder.li_othershare.setOnClickListener {
            if (getUserId().isEmpty()) {
                /*val intent = Intent(activity.context, LoginActivity::class.java)
                activity.startActivity(intent)
                App.isRedirect = true*/
                activity.requireActivity().showLoginMsg()
            } else {
                activity.DialogShare(model, Const.OTHERS)
            }
        }

        holder.tvfacebook.setOnClickListener {
            if (getUserId().isEmpty()) {
               /* val intent = Intent(activity.context, LoginActivity::class.java)
                activity.startActivity(intent)
                App.isRedirect = true*/
               activity.requireActivity().showLoginMsg()
            } else {
                activity.DialogShare(model, Const.FACEBOOK)
            }
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


        holder.tvDwonload.setOnClickListener {
            if (getUserId().isEmpty()) {
                /*val intent = Intent(activity.context, LoginActivity::class.java)
                activity.startActivity(intent)
                App.isRedirect = true*/
                activity.requireActivity().showLoginMsg()
            } else {

                activity.DownloadTask(
                    model.media,
                    model.iD,
                    true
                ).execute()
            }
        }

        holder.li_download.setOnClickListener {
            if (getUserId().isEmpty()) {
                /*val intent = Intent(activity.context, LoginActivity::class.java)
                activity.startActivity(intent)
                App.isRedirect = true*/
                activity.requireActivity().showLoginMsg()
            } else {
                activity.DownloadTask(
                    model.media,
                    model.iD,
                    true
                ).execute()
            }
        }
    }

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
    }
}
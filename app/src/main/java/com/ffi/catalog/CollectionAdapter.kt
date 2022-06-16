package com.ffi.catalog

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.ffi.App
import com.ffi.GlideApp
import com.ffi.R
import com.ffi.Utils.*
import com.ffi.productlist.CatalogHolder
import java.io.File

class CollectionAdapter(
    val activity: CollectionFragment,
    val models: List<com.ffi.collectiondetail.Record>,
    var context: Context
) :
    RecyclerView.Adapter<CatalogHolder>() {

    //private var models = ArrayList<com.ffi.collectiondetail.Record>()
    var mFirstWidth = 0
    var mSecondWidth = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CatalogHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.adapter_catalog, parent, false)

        return CatalogHolder(view)
    }


    /*fun addModels(modellist: List<com.ffi.collectiondetail.Record>) {
        this.models.addAll(modellist)
        this.notifyDataSetChanged()
    }*/


    override fun getItemCount() = models.size


    override fun onBindViewHolder(holder: CatalogHolder, position: Int) {
        val model = models[position]
        try {
            GlideApp.with(activity).clear(holder.img2)
            holder.img2.setImageResource(R.color.color_light_grey)
            holder.img2.setImageDrawable(null)
            holder.img2.setImageBitmap(null)

            GlideApp.with(activity).clear(holder.mainimg)
            holder.mainimg.setImageResource(R.color.color_light_grey)
            holder.mainimg.setImageDrawable(null)
            holder.mainimg.setImageBitmap(null)

            GlideApp.with(activity).clear(holder.img3)
            holder.img3.setImageResource(R.color.color_light_grey)
            holder.img3.setImageDrawable(null)
            holder.img3.setImageBitmap(null)
        } catch (e: Exception) {
            e.printStackTrace()
        }


        if (model.media.size != 0) {
            Log.e(
                "tagGlideImageCheck", "checkGlideImages() onResourceReady model.media.get(0).media "
                        + " " + getMediaLink() + model.media.get(0).media
            )

            /*Glide.with(activity)
                .load(getMediaLink() + model.media.get(0).media)
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
                                for (i in model.media.indices) {
                                    if (!model.media.get(i).mediaTypeId.equals(Const.MEDIA_VIDEO)) {
                                        Log.e("thumnail", "index: " + i.toString())
                                        firstImageUrl = model.media.get(i).media
                                        break
                                    }
                                }
                                if (!firstImageUrl.isEmpty()) {
                                    Glide.with(activity)
                                        .asFile()
                                        .load(getMediaLink() + firstImageUrl)
                                        .placeholder(R.color.color_light_grey)
                                        .into(object : CustomTarget<File>() {
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
                                } else {
                                    Glide.with(activity)
                                        .asFile()
                                        .load(getMediaLink() + model.media.get(0).media)
                                        .placeholder(R.color.color_light_grey)
                                        .into(object : CustomTarget<File>() {
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


            DisplayThumbnail3(activity, getMediaLink(), 0, model, holder.mainimg)

            if (model.media.get(0).mediaTypeId.equals(Const.MEDIA_VIDEO)) {
                holder.ivplay1.visibility = View.VISIBLE
            } else {
                holder.ivplay1.visibility = View.GONE
            }

            Log.e("img", getMediaLink() + model.media.get(0).media)
        }else{
            holder.ivplay1.visibility = View.GONE
        }

        if (model.media.size > 1) {
            Log.e(
                "tagGlideImageCheck", "product name: " + model.title
            )
            Log.e(
                "tagGlideImageCheck", "checkGlideImages() onResourceReady model.media.get(0).media "
                        + " " + getMediaLink() + model.media.get(1).media
            )


            /*  Glide.with(activity)
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
                                      Glide.with(activity)
                                          .asFile()
                                          .load(getMediaLink() + firstImageUrl)
                                          .placeholder(R.color.color_light_grey)
                                          .into(object : CustomTarget<File>() {
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
                                  } else {
                                      Glide.with(activity)
                                          .asFile()
                                          .load(getMediaLink() + model.media.get(1).media)
                                          .placeholder(R.color.color_light_grey)
                                          .into(object : CustomTarget<File>() {
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


            DisplayThumbnail3(activity, getMediaLink(), 1, model, holder.img2)

            if (model.media.get(1).mediaTypeId.equals(Const.MEDIA_VIDEO)) {
                holder.ivplay2.visibility = View.VISIBLE
            } else {
                holder.ivplay2.visibility = View.GONE
            }
        } else {
            holder.ivplay2.visibility = View.GONE
            Glide.with(activity)
                .load("")
                .placeholder(R.color.color_light_grey)
                .error(R.color.color_light_grey)
                .into(holder.img2)
        }

        if (mFirstWidth > 0) {
            holder.cv_left_card.layoutParams.width = mFirstWidth
            holder.cv_left_card.layoutParams.height = (mFirstWidth * 1.6f).toInt()
        }

        if (mSecondWidth > 0) {
            holder.ll_right_card.layoutParams.width = mSecondWidth
            holder.ll_right_card.layoutParams.height = (mFirstWidth * 1.6f).toInt()

            holder.cv_right_image1.layoutParams.width = mSecondWidth
            holder.cv_right_image1.layoutParams.height =
                (((mFirstWidth * 1.6f) / 2f) - 12f).toInt()

            holder.cv_right_image2.layoutParams.width = mSecondWidth
            holder.cv_right_image2.layoutParams.height =
                (((mFirstWidth * 1.6f) / 2f) - 12f).toInt()
        }


        if (model.media.size > 2) {
            Log.e(
                "tagGlideImageCheck", "checkGlideImages() onResourceReady model.media.get(0).media "
                        + " " + getMediaLink() + model.media.get(2).media
            )


            /*  Glide.with(activity)

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
                                      for (i in model.media.indices) {
                                          if (!model.media.get(i).mediaTypeId.equals(Const.MEDIA_VIDEO)) {
                                              firstImageUrl = model.media.get(i).media
                                              break
                                          }
                                      }
                                  } else {
                                      firstImageUrl = model.media.get(1).media
                                  }

                                  if (!firstImageUrl.isEmpty()) {
                                      Glide.with(activity)
                                          .asFile()
                                          .load(getMediaLink() + firstImageUrl)
                                          .placeholder(R.color.color_light_grey)
                                          .into(object : CustomTarget<File>() {
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
                                  } else {
                                      Glide.with(activity)
                                          .asFile()
                                          .load(getMediaLink() + model.media.get(2).media)
                                          .placeholder(R.color.color_light_grey)
                                          .into(object : CustomTarget<File>() {
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
            DisplayThumbnail3(activity, getMediaLink(), 2, model, holder.img3)

            if (model.media.get(2).mediaTypeId.equals(Const.MEDIA_VIDEO)) {
                holder.ivplay3.visibility = View.VISIBLE
            } else {
                holder.ivplay3.visibility = View.GONE
            }

        } else {
            holder.ivplay3.visibility = View.GONE
            Glide.with(activity)
                .load("")
                .placeholder(R.color.color_light_grey)
                .error(R.color.color_light_grey)
                .into(holder.img3)
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


        holder.li_main.setOnClickListener {

            activity.loadProductDetail(model.iD)
        }

        holder.tvOthers.setOnClickListener {
//            if(App.checkManageStorage(context)){
//                return@setOnClickListener
//            }
            if (getUserId().isEmpty()) {
                /* val intent = Intent(activity.context, LoginActivity::class.java)
                 activity.startActivity(intent)
                 App.isRedirect = true*/
                activity.requireActivity().showLoginMsg()
            } else {
                activity.DialogShare(model, Const.OTHERS)
            }
        }

        holder.li_othershare.setOnClickListener {

//            if(App.checkManageStorage(context)){
//                return@setOnClickListener
//            }
            if (getUserId().isEmpty()) {
                /* val intent = Intent(activity.context, LoginActivity::class.java)
                 activity.startActivity(intent)
                 App.isRedirect = true*/
                activity.requireActivity().showLoginMsg()
            } else {
                activity.DialogShare(model, Const.OTHERS)
            }
        }

        holder.tvfacebook.setOnClickListener {
//            if(App.checkManageStorage(context)){
//                return@setOnClickListener
//            }
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

//            if(App.checkManageStorage(context)){
//                return@setOnClickListener
//            }
            if (getUserId().isEmpty()) {
                activity.requireActivity().showLoginMsg()
            } else {
                activity.DialogShare(model, Const.FACEBOOK)
            }
        }

        holder.li_whatsapp.setOnClickListener {
//            if(App.checkManageStorage(context)){
//                return@setOnClickListener
//            }

            if (getUserId().isEmpty()) {
                activity.requireActivity().showLoginMsg()
            } else {
                activity.DialogShare(model, Const.WHATSAPP)
            }
        }


        holder.tvDwonload.setOnClickListener {
            /*if (getUserId().isEmpty()) {
                val intent = Intent(activity.context, LoginActivity::class.java)
                activity.startActivity(intent)
                App.isRedirect = true
                //activity.requireActivity().showToast(activity.getString(R.string.msg_login_require))
            } else {*/
            activity.DownloadTask(
                model.media,
                model.iD,
                true
            )
                .execute()

            //}
        }

        holder.li_download.setOnClickListener {
            /*if (getUserId().isEmpty()) {
                activity.requireActivity().showToast(activity.getString(R.string.msg_login_require))
            } else {*/
            activity.DownloadTask(
                model.media,
                model.iD,
                true
            ).execute()
            //}
        }
    }

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
    }


    override fun onViewRecycled(holder: CatalogHolder) {
        super.onViewRecycled(holder)
        try {
            GlideApp.with(activity).clear(holder.img2)
            holder.img2.setImageResource(R.color.color_light_grey)
            holder.img2.setImageDrawable(null)
            holder.img2.setImageBitmap(null)

            GlideApp.with(activity).clear(holder.mainimg)
            holder.mainimg.setImageResource(R.color.color_light_grey)
            holder.mainimg.setImageDrawable(null)
            holder.mainimg.setImageBitmap(null)

            GlideApp.with(activity).clear(holder.img3)
            holder.img3.setImageResource(R.color.color_light_grey)
            holder.img3.setImageDrawable(null)
            holder.img3.setImageBitmap(null)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
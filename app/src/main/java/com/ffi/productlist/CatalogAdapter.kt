package com.ffi.productlist

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.ffi.GlideApp
import com.ffi.R
import com.ffi.Utils.*
import java.util.logging.Handler

class CatalogAdapter(val activity: ProductListFragment, val models: List<Record>) :
    RecyclerView.Adapter<CatalogHolder>() {

    //private var models = ArrayList<Record>()
    var mFirstWidth = 0
    var mSecondWidth = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CatalogHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.adapter_catalog, parent, false)

        return CatalogHolder(view)
    }


    /*fun addModels(modellist: List<Record>) {
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

        if (model.Media.size != 0) {
            DisplayThumbnail(activity,getMediaLink(),0,model,holder.mainimg)

            if(model.Media.get(0).MediaTypeId.equals(Const.MEDIA_VIDEO)){
                holder.ivplay1.visibility=View.VISIBLE
            }else{
                holder.ivplay1.visibility=View.GONE

            }
        } else {
            holder.ivplay1.visibility=View.GONE
            Glide.with(activity)
                .load("")
                .placeholder(R.color.color_light_grey)
                .into(holder.mainimg)
        }

        if (model.Media.size > 1) {

            if (mFirstWidth > 0) {
                holder.cv_left_card.layoutParams.width = mFirstWidth
                holder.cv_left_card.layoutParams.height = (mFirstWidth * 1.7f).toInt()
            }

            if (mSecondWidth > 0) {
                holder.ll_right_card.layoutParams.width = mSecondWidth
                holder.ll_right_card.layoutParams.height = (mFirstWidth * 1.7f).toInt()

                holder.cv_right_image1.layoutParams.width = mSecondWidth
                holder.cv_right_image1.layoutParams.height =
                    (((mFirstWidth * 1.7f) / 2f) - 12f).toInt()

                holder.cv_right_image2.layoutParams.width = mSecondWidth
                holder.cv_right_image2.layoutParams.height =
                    (((mFirstWidth * 1.7f) / 2f) - 12f).toInt()
            }



            /*Glide.with(activity)
                .load(getMediaLink() + model.Media.get(1).Media)
                .placeholder(R.color.color_light_grey)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        modelany: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        android.os.Handler().post {
                            kotlin.run {
                                var firstImageUrl = ""
                                if (model.Media.get(1).MediaTypeId.equals(Const.MEDIA_VIDEO)) {
                                    Log.e("thumnail", "second image has video")
                                    for (i in model.Media.indices) {
                                        if (!model.Media.get(i).MediaTypeId.equals(Const.MEDIA_VIDEO)) {
                                            Log.e("thumnail", "index:: " + i.toString())
                                            firstImageUrl = model.Media.get(i).Media
                                            break
                                        }
                                    }
                                } else {
                                    Log.e("thumnail", "second image not has video")
                                    firstImageUrl = model.Media.get(1).Media
                                }
                                if (!firstImageUrl.isEmpty()) {
                                    Glide.with(activity)
                                        .load(getMediaLink() + firstImageUrl)
                                        .placeholder(R.color.color_light_grey)
                                        .into(holder.img2)
                                } else {
                                    Glide.with(activity)
                                        .load(getMediaLink() + model.Media.get(1).Media)
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

            if (model.Media.get(1).MediaTypeId.equals(Const.MEDIA_VIDEO)) {
                holder.ivplay2.visibility = View.VISIBLE
            } else {
                holder.ivplay2.visibility = View.GONE

            }
        } else {
            holder.ivplay2.visibility = View.GONE
            Glide.with(activity)
                .load("")
                .placeholder(R.color.color_light_grey)
                .into(holder.img2)
        }

        if (model.Media.size > 2) {

          /*  Glide.with(activity)
                .load(getMediaLink() + model.Media.get(2).Media)
                .placeholder(R.color.color_light_grey)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        modelany: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        android.os.Handler().post {
                            kotlin.run {
                                var firstImageUrl = ""
                                if (model.Media.get(2).MediaTypeId.equals(Const.MEDIA_VIDEO)) {
                                    for (i in model.Media.indices) {
                                        if (!model.Media.get(i).MediaTypeId.equals(Const.MEDIA_VIDEO)) {
                                            firstImageUrl = model.Media.get(i).Media
                                            break
                                        }
                                    }
                                } else {
                                    firstImageUrl = model.Media.get(1).Media
                                }
                                if (!firstImageUrl.isEmpty()) {
                                    Glide.with(activity)
                                        .load(getMediaLink() + firstImageUrl)
                                        .placeholder(R.color.color_light_grey)
                                        .into(holder.img3)
                                } else {
                                    Glide.with(activity)
                                        .load(getMediaLink() + model.Media.get(2).Media)
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

            if (model.Media.get(2).MediaTypeId.equals(Const.MEDIA_VIDEO)) {
                holder.ivplay3.visibility = View.VISIBLE
            } else {
                holder.ivplay3.visibility = View.GONE

            }
        } else {
            holder.ivplay3.visibility = View.GONE
            Glide.with(activity)
                .load("")
                .placeholder(R.color.color_light_grey)
                .into(holder.img3)
        }

        if (model.Media.size > 3) {
            holder.li_img_count.visibility = View.VISIBLE
            holder.img_count.text =
                activity.getString(R.string.plus) + (model.Media.size - 3).toString()
        } else {
            holder.li_img_count.visibility = View.GONE
        }

        holder.tvitemname.text = model.Title
        holder.tvprice.text = activity.getString(R.string.currency) + " " + model.Price

        holder.li_main.setOnClickListener {
            activity.loadProductDetail(model.ID)
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
                /* val intent = Intent(activity.context, LoginActivity::class.java)
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
            /*if (getUserId().isEmpty()) {
                val intent = Intent(activity.context, LoginActivity::class.java)
                activity.startActivity(intent)
                App.isRedirect = true
                //activity.requireActivity().showToast(activity.getString(R.string.msg_login_require))
            } else {*/
            activity.DownloadTask(
                model.Media,
                model.ID,
                true
            ).execute()
            // }
        }



        holder.li_download.setOnClickListener {
            /*  if (getUserId().isEmpty()) {
                  val intent = Intent(activity.context, LoginActivity::class.java)
                  activity.startActivity(intent)
                  App.isRedirect = true
                  //activity.requireActivity().showToast(activity.getString(R.string.msg_login_require))
              } else {*/

            activity.DownloadTask(
                model.Media,
                model.ID,
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
package com.ffi.category

import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.ffi.GlideApp
import com.ffi.R
import com.ffi.Utils.Const
import com.ffi.Utils.getMediaLink
import com.squareup.picasso.Picasso
import com.squareup.picasso.PicassoProvider

class TabCatagoryAdapter(val activity: CategoryFragment,val  models: ArrayList<Record>) :
    RecyclerView.Adapter<ViewHolder>() {

    //private var models = ArrayList<Record>()
    var mFirstWidth = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var view = LayoutInflater.from(activity.requireActivity())
            .inflate(R.layout.adapter_single_image_category, parent, false)
        return CatSingleImgHolder(view)
    }


  /*  fun addModels(modellist: List<Record>) {
        this.models.addAll(modellist)
        this.notifyDataSetChanged()
    }*/

    override fun getItemCount() = models.size


    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = models[position]

//        Log.e("tagMediaLinks", getMediaLink()+model.Media)
        if(activity.activity != null && activity.activity?.applicationContext != null){
            var mContext = activity.activity?.applicationContext
            var mMediaLink = getMediaLink() + model.Media

//            Picasso.get()
//                .load(mMediaLink)
//                .placeholder(R.color.color_light_grey)
//                .into((holder as CatSingleImgHolder).ivCategoryImg)

            Glide.with(mContext!!)
                .load(mMediaLink)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .placeholder(R.color.color_light_grey)
                .dontAnimate()
                .listener(object: RequestListener<Drawable>{
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        Log.e("tagMediaLinks","onBindViewHolder onLoadFailed isFirstResource "  + isFirstResource)
                        Log.e("tagMediaLinks","onBindViewHolder onLoadFailed error "  + e?.message)
                        if(activity.activity != null && activity.activity?.applicationContext != null){
                            Glide.with(mContext!!)
                                .load(mMediaLink)
                                .placeholder(R.color.color_light_grey)
                                .dontAnimate()
                                .thumbnail(0.2f)
                                .into((holder as CatSingleImgHolder).ivCategoryImg)
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
                        Log.e("tagMediaLinks","onBindViewHolder onResourceReady isFirstResource "  + isFirstResource)
                        Log.e("tagMediaLinks","onBindViewHolder onResourceReady isFirstResource "  + model.toString())
                        Log.e("tagMediaLinks","onBindViewHolder onResourceReady isFirstResource "  + dataSource?.name)
                        if(!isFirstResource){
                            Log.e("tagMediaLinks","onBindViewHolder onResourceReady resource "  + resource)
                            (holder as CatSingleImgHolder).ivCategoryImg.setImageDrawable(resource)
                        }
                        return false
                    }

                })
                .into((holder as CatSingleImgHolder).ivCategoryImg)
        }

        when (model.vietype) {
            Const.ONE_IMAGE -> {
                (holder as CatSingleImgHolder).tvcategoryname.text = model.Name

                if(mFirstWidth > 0){
                    holder.cv_img.layoutParams.width = mFirstWidth
                    holder.cv_img.layoutParams.height = (mFirstWidth * 1.6f).toInt()
                }


               /* Glide.with(activity)
                    .asBitmap()
                    .load(getMediaLink() + model.Media)
                    .placeholder(R.color.color_light_grey)
                    .into(object : CustomTarget<Bitmap>(){
                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                            Log.e("@tagsizebitmap", "resource.width " + resource.width)
                            Log.e("@tagsizebitmap", "resource.height " + resource.height)
                            Log.e("@tagsizebitmap", "holder.ivCategoryImg.measuredWidth " + holder.ivCategoryImg.measuredWidth)
                            Log.e("@tagsizebitmap", "holder.ivCategoryImg.measuredHeight " + holder.ivCategoryImg.measuredHeight)

                            *//*val background =
                                Bitmap.createBitmap(resource.width, resource.height, Bitmap.Config.ARGB_8888)

                            val originalWidth: Float = resource.getWidth().toFloat()
                            val originalHeight: Float = resource.getHeight().toFloat()

                            val canvas = Canvas(background)

                            val scale = resource.width / originalWidth
                            Log.e("@tagsizebitmap", "resource.height " + resource.height)


                            val xTranslation = 0.0f
                            val yTranslation = (resource.height - originalHeight * scale) / 2.0f

                            val transformation = Matrix()
                            transformation.postTranslate(xTranslation, yTranslation)
                            transformation.preScale(scale, scale)

                            val paint = Paint()
                            paint.setFilterBitmap(true)*//*

                            Bitmap.createScaledBitmap(resource, holder.ivCategoryImg.measuredWidth, (holder.ivCategoryImg.measuredWidth / 0.5).toInt(), true);

                        }
                        override fun onLoadCleared(placeholder: Drawable?) {

                        }
                    })
*/

                holder.fi_main.setOnClickListener {
                    activity.loadProductCatalog(model.ID,model.Name)
                }


            }

        }


    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return when (models[position].vietype) {
            0 -> Const.ONE_IMAGE
            1 -> Const.TWO_IMAGE
            else -> -1
        }
    }

}
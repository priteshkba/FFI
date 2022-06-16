package com.ffi.Utils

import android.graphics.drawable.Drawable
import android.util.Log
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.ffi.R
import com.ffi.home.RecordXXX
import com.ffi.productlist.Record

fun DisplayThumbnail(
    fargment: Fragment,
    baseUrl: String,
    position: Int,
    model: Record,
    imageView: ImageView
) {

    if (model.Media.get(position).MediaTypeId.equals(Const.MEDIA_VIDEO)) {
        Glide.with(fargment)
            .load(baseUrl + model.Media.get(position).Media)
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
                            Log.e("thumnail", "onFailed 234qwert")
                            // Log.e("thumnail", "onFailed product name :" + model.Title)
                            var firstImageUrl = ""
                            for (i in model.Media.indices) {
                                if (!model.Media.get(i).MediaTypeId.equals(Const.MEDIA_VIDEO)) {
                                    Log.e("thumnail", "index: " + i.toString())
                                    firstImageUrl = model.Media.get(i).Media
                                    break
                                }
                            }
                            if (!firstImageUrl.isEmpty()) {
                                Glide.with(fargment)
                                    .load(baseUrl + firstImageUrl)
                                    .placeholder(R.color.color_light_grey)
                                    .into(imageView)
                            } else {
                                Glide.with(fargment)
                                    .load(baseUrl + model.Media.get(position).Media)
                                    .placeholder(R.color.color_light_grey)
                                    .into(imageView)
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
            .into(imageView)
    } else {
        Glide.with(fargment)
            .load(baseUrl + model.Media.get(position).Media)
            .placeholder(R.color.color_light_grey)
            .into(imageView)
    }

}

fun DisplayThumbnail(
    fargment: Fragment,
    baseUrl: String,
    position: Int,
    model: RecordXXX,
    imageView: ImageView
) {
    Log.e("url123",baseUrl+model.media.get(position).media)

    if (model.media.get(position).mediaTypeId.equals(Const.MEDIA_VIDEO)) {
        Glide.with(fargment)
            .load(baseUrl + model.media.get(position).media)
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
                            Log.e("thumnail", "onFailed 234qwert")
                            // Log.e("thumnail", "onFailed product name :" + model.Title)
                            var firstImageUrl = ""
                            for (i in model.media.indices) {
                                if (!model.media.get(i).mediaTypeId.equals(Const.MEDIA_VIDEO)) {
                                    Log.e("thumnail", "index: " + i.toString())
                                    firstImageUrl = model.media.get(i).media
                                    break
                                }
                            }
                            if (!firstImageUrl.isEmpty()) {
                                Glide.with(fargment)
                                    .load(baseUrl + firstImageUrl)
                                    .placeholder(R.color.color_light_grey)
                                    .into(imageView)
                            } else {
                                Glide.with(fargment)
                                    .load(baseUrl + model.media.get(position).media)
                                    .placeholder(R.color.color_light_grey)
                                    .into(imageView)
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
            .into(imageView)
    } else {
        Glide.with(fargment)
            .load(baseUrl + model.media.get(position).media)
            .placeholder(R.color.color_light_grey)
            .into(imageView)
    }

}

//com.ffi.collectiondetail.Record

fun DisplayThumbnail3(
    fargment: Fragment,
    baseUrl: String,
    position: Int,
    model: com.ffi.collectiondetail.Record,
    imageView: ImageView
) {
    if (model.media.get(position).mediaTypeId.equals(Const.MEDIA_VIDEO)) {
        Glide.with(fargment)
            .load(baseUrl + model.media.get(position).media)
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
                            Log.e("thumnail", "onFailed 234qwert")
                            // Log.e("thumnail", "onFailed product name :" + model.Title)
                            var firstImageUrl = ""
                            for (i in model.media.indices) {
                                if (!model.media.get(i).mediaTypeId.equals(Const.MEDIA_VIDEO)) {
                                    Log.e("thumnail", "index: " + i.toString())
                                    firstImageUrl = model.media.get(i).media
                                    break
                                }
                            }
                            if (!firstImageUrl.isEmpty()) {
                                Glide.with(fargment)
                                    .load(baseUrl + firstImageUrl)
                                    .placeholder(R.color.color_light_grey)
                                    .into(imageView)
                            } else {
                                Glide.with(fargment)
                                    .load(baseUrl + model.media.get(position).media)
                                    .placeholder(R.color.color_light_grey)
                                    .into(imageView)
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
            .into(imageView)
    } else {
        Glide.with(fargment)
            .load(baseUrl + model.media.get(position).media)
            .placeholder(R.color.color_light_grey)
            .into(imageView)
    }
}
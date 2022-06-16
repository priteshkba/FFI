package com.ffi.productdetail

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.ffi.Utils.Const
import com.ffi.productdetail.ImageFragment
import com.ffi.productdetail.VideoFragment
import com.ffi.productlist.Media

class ViewPagerZoomImagesAdapter(
    private val context: Context,
    private val mContext: FragmentManager,
    private val mResources: ArrayList<Media>
) : FragmentPagerAdapter(mContext) {

    var mFirstWidth = 0

    override fun getCount(): Int {
        return mResources.size
    }


    /*   override fun isViewFromObject(view: View, `object`: Any): Boolean {
           return view === `object` as LinearLayout
       }*/

    /*  override fun instantiateItem(container: ViewGroup, position: Int): Any {
          val itemView = LayoutInflater.from(context).inflate(R.layout.zoom_image, container, false)

          val imageView = itemView.findViewById(R.id.img) as ImageView
          GlideApp.with(context)
              .load(getMediaLink() + mResources[position])
              .override(600, 450)
              .placeholder(R.color.color_light_grey)
              .into(imageView)


          container.addView(itemView)

          return itemView
      }*/

    override fun getItem(position: Int): Fragment {
        if (mResources[position].MediaTypeId.equals(Const.MEDIA_VIDEO)) {
            return VideoFragment(mResources[position].Media)
        } else {
            return ImageFragment().getInstance(mResources[position].Media, mFirstWidth)
        }
    }

//    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
//        container.removeView(`object` as LinearLayout)
//    }
}
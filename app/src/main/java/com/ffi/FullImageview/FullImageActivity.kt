package com.ffi.FullImageview

import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.ffi.R
import com.ffi.Utils.Const
import com.ffi.productdetail.ViewPagerZoomImagesAdapter
import com.ffi.productlist.Media
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_full_image.*
import kotlinx.android.synthetic.main.activity_product_detail.viewPagerCountDots


class FullImageActivity : AppCompatActivity(), ViewPager.OnPageChangeListener,
    View.OnClickListener {

    private var dotsCount: Int = 0
    private var dots: Array<ImageView?> = emptyArray()
    lateinit var mImageResources: ArrayList<Media>
    private var mAdapter: ViewPagerZoomImagesAdapter? = null
    var currentpos = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_image)

        init()
    }

    fun init() {
        ivBack.setOnClickListener(this)
        mImageResources = ArrayList()
        val bundle = intent.extras
        if (bundle != null) {
            if (bundle.containsKey(Const.DATA)) {
                val data = bundle.getString(Const.DATA)
                val gson = Gson()
                val arrdata: ArrayList<Media> =
                    gson.fromJson(data, object : TypeToken<ArrayList<Media?>?>() {}.type)

                if (data != null) {
                    mImageResources.addAll(arrdata)
                }
            }

            if (bundle.containsKey(Const.POSITION)) {
                currentpos = bundle.getInt(Const.POSITION)
            }
        }
        //mImageResources.addAll(data.Media)

        //varientsize = data.variation.size
        if (mImageResources.size >= 1) {
            mAdapter = ViewPagerZoomImagesAdapter(this, supportFragmentManager, mImageResources)
            fullimgpager.adapter = mAdapter
            fullimgpager.currentItem = currentpos
            fullimgpager.setOnPageChangeListener(this)
            setUiPageViewController()


            val displayMetrics = DisplayMetrics()
            windowManager?.defaultDisplay?.getMetrics(displayMetrics)
            val screenWidth: Int = displayMetrics.widthPixels
            val screenHeight: Int = displayMetrics.heightPixels
            mAdapter?.mFirstWidth = screenWidth
            mAdapter?.notifyDataSetChanged()

        }

    }


    private fun setUiPageViewController() {
        dotsCount = mAdapter?.count!!
        dots = arrayOfNulls<ImageView>(this.dotsCount)

        viewPagerCountDots.removeAllViews()

        for (i in 0 until dotsCount) {
            dots[i] = ImageView(this)
            dots[i]?.setImageDrawable(resources.getDrawable(R.drawable.banner_nonselecteditem_dot))

            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(4, 0, 4, 0)

            viewPagerCountDots.addView(dots[i], params)
        }
        if (dots.size >= 1)
            dots[currentpos]?.setImageDrawable(resources.getDrawable(R.drawable.banner_selecteditem_dot))
    }

    override fun onPageScrollStateChanged(state: Int) {
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
    }

    override fun onPageSelected(position: Int) {
        for (i in 0 until dotsCount) {
            dots[i]?.setImageDrawable(resources.getDrawable(R.drawable.banner_nonselecteditem_dot))
        }

        dots[position]?.setImageDrawable(resources.getDrawable(R.drawable.banner_selecteditem_dot))
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBack -> {
                finish()
            }
        }
    }
}
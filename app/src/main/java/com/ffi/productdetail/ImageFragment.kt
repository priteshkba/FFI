package com.ffi.productdetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ffi.GlideApp
import com.ffi.R
import com.ffi.Utils.Const
import com.ffi.Utils.getMediaLink
import kotlinx.android.synthetic.main.zoom_image.*

class ImageFragment : Fragment() {

    var media: String = ""
    var mFirstWidth: Int = 0

    public fun getInstance(media: String, mFirstWidth: Int): ImageFragment {
        val mImageFragment = ImageFragment()
        val args = Bundle()
        args.putString(Const.MEDIA_ID, media)
        args.putInt(Const.mFirstWidth, mFirstWidth)
        mImageFragment.setArguments(args)
        return mImageFragment
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.zoom_image, container, false)
    }

    private fun getArgumentData() {
        if (arguments != null) {
            if (arguments?.containsKey(Const.MEDIA_ID) != null &&
                arguments?.containsKey(Const.MEDIA_ID)!!
            ) {
                media = arguments?.getString(Const.MEDIA_ID)!!
            }
            if (arguments?.containsKey(Const.mFirstWidth) != null &&
                arguments?.containsKey(Const.mFirstWidth)!!
            ) {
                mFirstWidth = arguments?.getInt(Const.mFirstWidth)!!
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getArgumentData()
        GlideApp.with(requireActivity())
            .load(getMediaLink() + media)
            .placeholder(R.color.color_light_grey)
            .into(img)

        if(mFirstWidth > 0){
            img.layoutParams.width = mFirstWidth
            img.layoutParams.height = (mFirstWidth * 1.6f).toInt()
        }

    }
}
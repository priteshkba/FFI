package com.ffi.sharingProductDetail

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageManager
import android.view.Gravity
import android.widget.*
import androidx.core.app.ActivityCompat
import com.ffi.R
import com.ffi.Utils.Const
import com.ffi.Utils.showToast
import com.ffi.productdetail.Data

//ShareProductDetailDailog
/*
fun shareProductDetails(model: Data, sourceapp: String, activty: Activity, context: Context) {
     val WRITE_PERMISSION = 0
    var isDialogOpen = false
    if (ActivityCompat.checkSelfPermission(
            activty,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        ActivityCompat.requestPermissions(
            activty,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            WRITE_PERMISSION
        )
        isDialogOpen = false
    } else {
        var hasAnyVideo = false
        for (mediafile in model.Media) {
            if (mediafile.MediaTypeId.equals(Const.MEDIA_VIDEO)) {
                hasAnyVideo = true
            }
        }
        val dialog = Dialog(activty)
        val window = dialog.window
        window!!.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        window.setGravity(Gravity.CENTER)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setContentView(R.layout.dailog_share)

        val ivclose = dialog.findViewById<ImageView>(R.id.ivclose)
        btnDone = dialog.findViewById<TextView>(R.id.btn_done)
        liDownloads = dialog.findViewById(R.id.li_downloading)

        val tvdesc = dialog.findViewById<TextView>(R.id.tvDesc)
        val tvshareimg = dialog.findViewById<TextView>(R.id.tvshareimg)
        val tvaddmargin = dialog.findViewById<TextView>(R.id.tvaddmargin)
        val tvsharevideo = dialog.findViewById<TextView>(R.id.tvsharevideo)

        val rdDesc = dialog.findViewById<CheckBox>(R.id.rdDesc)
        val rdimg = dialog.findViewById<CheckBox>(R.id.rdimage)
        val rdmargin = dialog.findViewById<CheckBox>(R.id.rdmargin)
        val rdvideo = dialog.findViewById<CheckBox>(R.id.rdvideo)
        var edt_margin = dialog.findViewById<EditText>(R.id.edt_margin)

        val llSaheVideo = dialog.findViewById<LinearLayout>(R.id.llSaheVideo)
        if(hasAnyVideo){
            tvshareimg.text=context.getString(R.string.title_share_imageVideo)
        }else{
            tvshareimg.text=context.getString(R.string.title_share_image)
        }

        clearAndRexecuteDownloader(model.Media)

        tvdesc?.setOnClickListener {
            rdDesc.isChecked = !rdDesc.isChecked
        }

        tvshareimg?.setOnClickListener {
            rdimg.isChecked = !rdimg.isChecked
        }

        tvaddmargin?.setOnClickListener {
            rdmargin.isChecked = !rdmargin.isChecked
        }

        tvsharevideo?.setOnClickListener {
            rdvideo?.isChecked = !rdvideo.isChecked
        }
        rdvideo?.setOnCheckedChangeListener { buttonView, isChecked ->

        }

        rdDesc?.setOnCheckedChangeListener { buttonView, isChecked ->
            sharedesc = ""

            if (isChecked) {
                sharedesc = model.Title + "\n" +
                        model.Description
            }
        }



        btnDone?.setOnClickListener {
            if (!rdimg.isChecked && !rdDesc.isChecked) {
                activity?.showToast(getString(R.string.err_select_to_share), Const.TOAST)
                return@setOnClickListener
            }

            if (rdimg.isChecked) {
                if (alreadyDownload) {
                    if (imageUriArray.size <= 0) {
                        activity?.showToast(
                            getString(R.string.err_img_not_appearing),
                            Const.TOAST
                        )
                        return@setOnClickListener
                    }
                } else {
                    if (productdata != null && productdata?.Media != null &&
                        imageUriArrayGlide.size == productdata?.Media?.size
                    ) {
                        if (imageUriArrayGlide.size <= 0) {
                            activity?.showToast(
                                getString(R.string.err_img_not_appearing),
                                Const.TOAST
                            )
                            return@setOnClickListener
                        }
                    } else {
                        if (imageUriArray.size <= 0) {
                            activity?.showToast(
                                getString(R.string.err_img_not_appearing),
                                Const.TOAST
                            )
                            return@setOnClickListener
                        }
                    }
                }
            }
            CallApiSharedData(productid)
            if (rdmargin.isChecked) {
                val strmargin = edt_margin.text.toString()
                if (strmargin.isEmpty()) {
                    activity?.showToast(getString(R.string.err_enter_margin), Const.TOAST)
                    return@setOnClickListener
                } else {
                    sharedesc += "\n" + "*Price " + getString(R.string.currency) + " " + (strmargin.toFloat() + model.Price.replace(
                        ",",
                        ""
                    ).toFloat()) + "*"
                }
            }

            val clipboard: ClipboardManager =
                activity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip: ClipData = ClipData.newPlainText("copy", sharedesc)
            clipboard.setPrimaryClip(clip)


            if (sourceapp.equals(Const.WHATSAPP)) {

                if (rdimg.isChecked) {
                    ShareOnWhatsapp(true)
                } else {
                    ShareOnWhatsapp(false)
                }
            } else if (sourceapp.equals(Const.FACEBOOK)) {
                ShareOnFacebook()
            } else if (sourceapp.equals(Const.OTHERS)) {
//                    ShareOnAnyApp(model)
                if (rdimg.isChecked)
                    ShareOnAnyApp(true)
                else
                    ShareOnAnyApp(false)
            }
            isDialogOpen = false
            dialog.cancel()
        }

        ivclose.setOnClickListener {
            isDialogOpen = false
            dialog.dismiss()
        }

        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
    }
}*/

package com.ffi.cart

import android.Manifest
import android.annotation.SuppressLint
import android.os.Build
import android.app.Activity
import android.provider.DocumentsContract
import android.content.ContentUris
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.SystemClock
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.ffi.App
import com.ffi.GlideApp
import com.ffi.HomeActivity
import com.ffi.R
import com.ffi.Utils.*
import com.ffi.add_address.AddAddressActivity
import com.ffi.model.ParamAddREmoveWishItem
import com.ffi.model.ResponseAddRemoveWishItem
import com.ffi.productdetail.ParamAddProductNew
import com.ffi.productdetail.ProductDetailFragment
import com.ffi.productdetail.ResponseAddProduct
import com.ffi.api.ApiClient
import com.ffi.api.WebService
import com.ffi.my_orderdetails.CacheStore
import com.ffi.my_orderdetails.ImageCompressionAsyncTask
import kotlinx.android.synthetic.main.custom_toolbar.*
import kotlinx.android.synthetic.main.dialog_add_note.*
import kotlinx.android.synthetic.main.fragment_cart.*
import kotlinx.android.synthetic.main.fragment_cart.rl_ShipCharge
import kotlinx.android.synthetic.main.fragment_cart.rl_gst
import kotlinx.android.synthetic.main.fragment_cart.rl_membership
import kotlinx.android.synthetic.main.fragment_cart.rvOrder
import kotlinx.android.synthetic.main.fragment_cart.tvmembership
import kotlinx.android.synthetic.main.fragment_order_detail.*
import org.json.JSONObject
import pub.devrel.easypermissions.EasyPermissions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class CartFragment : Fragment(), View.OnClickListener {

    var lastTimeSel: Long = 0
    var mLastClickTime: Long = 0

    lateinit var mprogressbar: ProgressBarHandler
    lateinit var order_list: CartAdapter
    lateinit var arrlist: ArrayList<Item>
    var homeActivity: HomeActivity? = null

    lateinit var tvGrandTotal: TextView
    lateinit var tv_ship_charge: TextView
    lateinit var tvGST: TextView
    lateinit var tvSubTotal: TextView
    lateinit var dialog: CustomDialog
    val RC_CAMERA_PERM = 123
    val RC_GALLERY_PERM = 124
    private var currentPhotoPath = ""
    var finalImageFile: File? = null
    lateinit var photoFile: File
    var total = 0.0f
    var subtotal = 0.0f

    override fun onStop() {
        super.onStop()
        Log.e("/////","/onStop//")
        if (getUserId().isEmpty())
            App.isShowProgressbar = false
        if (this::dialog.isInitialized && dialog != null) {
            dialog.hide()
        }
    }

    override fun onStart() {
        super.onStart()
        Log.e("/////","/onStart//")

    }

    override fun onAttachFragment(childFragment: Fragment) {
        super.onAttachFragment(childFragment)
        Log.e("/////","/onAttachFragment//")

    }

    override fun onResume() {
        super.onResume()
        Log.e("/////","/onResume//")

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.e("/////","/onAttach 1//")

    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        Log.e("/////","/onAttach//")

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e("/////","/onCreate//")

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cart, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.e("/////","/onActivityCreated//")

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.e("/////","/onViewCreated//")
        tvGrandTotal = view.findViewById(R.id.tvGrandTotal)
        tv_ship_charge = view.findViewById(R.id.tv_ship_charge)
        tvGST = view.findViewById(R.id.tvGST)
        tvSubTotal = view.findViewById(R.id.tvSubTotal)

        mprogressbar = ProgressBarHandler(requireActivity())

        homeActivity = activity as HomeActivity
        //homeActivity.header.visibility = View.VISIBLE
        ivEdit.visibility = View.GONE
        txt_toolbar.text = getString(R.string.menu_cart)
        btn_checkout.setOnClickListener(this)

        setRefresh()
        if (getUserId().isEmpty()) {
            llNoCartitem?.visibility = View.VISIBLE
            tvNoCartitem?.visibility = View.VISIBLE
            cart_scrollview?.visibility = View.GONE
            swf_cart?.isRefreshing = false
        } else {
            Log.e("cart", "70")
            CallApiCartList(true)
        }

        ivAddNoteImage.setOnClickListener {
            dailogOpenAttachments()
        }

    }
    private fun dailogOpenAttachments() {
        try {
            val dialog = Dialog(requireActivity())
            val window = dialog.window
            window!!.setLayout(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            )
            dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
            dialog.setContentView(R.layout.dailog_select_image_attachment)
            val tvGallery = dialog.findViewById<TextView>(R.id.tvGallery)
            val tvCamera = dialog.findViewById<TextView>(R.id.tvCamera)
            val tvCancel = dialog.findViewById<TextView>(R.id.tvCancel)
            tvGallery.setOnClickListener {
                dialog.dismiss()
                openGallery()
            }
            tvCamera.setOnClickListener {
                dialog.dismiss()
                openCamera()
            }
            tvCancel.setOnClickListener { dialog.dismiss() }
            dialog.setCancelable(false)
            dialog.setCanceledOnTouchOutside(false)
            dialog.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun openGallery() {
        if (hasStoragePermission()) {
            try {
                val galleryIntent =
                        Intent(Intent.ACTION_PICK)
                galleryIntent.type = "image/*"
                startActivityForResult(galleryIntent, RC_GALLERY_PERM)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        } else {
            EasyPermissions.requestPermissions(
                    this,
                    "This app needs access to your storage so you can select picture.",
                    RC_GALLERY_PERM,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        }
    }

    fun openGallery2() {
        if (hasStoragePermission()) {
            try {
                val galleryIntent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(galleryIntent, RC_GALLERY_PERM)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        } else {
            EasyPermissions.requestPermissions(
                this,
                "This app needs access to your storage so you can select picture.",
                RC_GALLERY_PERM,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        }
    }
    @Throws(IOException::class)
    private fun createImageFile(): File? {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir: File? =
                activity?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
                imageFileName,  /* prefix */
                ".png",  /* suffix */
                storageDir /* directory */
        )
        currentPhotoPath = image.absolutePath
        return image
    }

    private fun hasCameraPermission(): Boolean {
        return EasyPermissions.hasPermissions(requireActivity(), Manifest.permission.CAMERA)
    }

    private fun hasStoragePermission(): Boolean {
        return EasyPermissions.hasPermissions(
                requireActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_CAMERA_PERM /*&& resultCode == Activity.RESULT_OK*/) {
            /*try {
                rlNoteImage.visibility = View.VISIBLE
                tvNoteImage.setText(finalImageFile?.name)
                finalImageFile = File(currentPhotoPath)
                 GlideApp.with(this)
                         .load(currentPhotoPath)
                         .placeholder(R.drawable.profileplaceholder)
                         .into(ivNoteImage)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }*/



            try {
                rlNoteImage.visibility = View.VISIBLE
                tvNoteImage.setText(finalImageFile?.name)
                compresssGalleryImage(photoFile!!.absolutePath)
                //finalImageFile = File(currentPhotoPath)
              /*  GlideApp.with(this)
                    .load(currentPhotoPath)
                    .placeholder(R.drawable.profileplaceholder)
                    .into(ivNoteImage)*/
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
       /* if (requestCode == RC_GALLERY_PERM && resultCode == Activity.RESULT_OK) {
            try {
                val selectedImageURI = data?.data
                finalImageFile = File(getRealPathFromURI(selectedImageURI))
                currentPhotoPath = finalImageFile.toString()
                rlNoteImage.visibility = View.VISIBLE
                tvNoteImage.setText(finalImageFile?.name)

                // File(currentPhotoPath)
                 GlideApp.with(this)
                         .load(currentPhotoPath)
                         .placeholder(R.drawable.profileplaceholder)
                         .into(ivNoteImage)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }*/
        if (requestCode == RC_GALLERY_PERM && resultCode == Activity.RESULT_OK) {
            try {
                val selectedImageURI: Uri = data?.data!!

                finalImageFile = File(getPathFromUri(requireContext(), selectedImageURI))
                currentPhotoPath = finalImageFile.toString()

                compresssGalleryImage(currentPhotoPath)
                rlNoteImage.visibility = View.VISIBLE
                tvNoteImage.setText(finalImageFile?.name)

                // File(currentPhotoPath)
               /* GlideApp.with(this)
                    .load(currentPhotoPath)
                    .placeholder(R.drawable.profileplaceholder)
                    .into(ivNoteImage)*/
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
    }


    fun compresssGalleryImage(realPath:String) {
        val imageCompression: ImageCompressionAsyncTask =
            @SuppressLint("StaticFieldLeak")
            object : ImageCompressionAsyncTask() {
                @SuppressLint("WrongThread")
                override fun onPostExecute(imageBytes: ByteArray) {
                    // image here is compressed & ready to be sent to the server

                    val bitmap = BitmapFactory.decodeByteArray(
                        imageBytes,
                        0,
                        imageBytes.size
                    )

                    val byteArrayOutputStream: ByteArrayOutputStream =
                        ByteArrayOutputStream()
                    bitmap.compress(
                        Bitmap.CompressFormat.JPEG,
                        100,
                        byteArrayOutputStream
                    )
                    val bytesofImage = byteArrayOutputStream.toByteArray()

                  /*  GlideApp.with(requireContext())
                        .load(bitmap)
                        .placeholder(R.drawable.profileplaceholder)
                        .into(dialogNoteImage!!.ivNote)*/

                    GlideApp.with(requireContext())
                        .load(bitmap)
                        .placeholder(R.drawable.profileplaceholder)
                        .into(ivNoteImage)
                    //finalImageFile = photoFile
                    finalImageFile = File(
                        CacheStore().saveCacheFile(
                            context?.externalCacheDir.toString(),
                            bitmap
                        ).toString()
                    )
                    //currentPhotoPath = finalImageFile?.absolutePath!!

                }
            }
        imageCompression.execute(realPath) // imagePath as a string
    }

//////


    fun getPathFromUri(context: Context?, uri: Uri): String? {
        val isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).toTypedArray()
                val type = split[0]
                if ("primary".equals(type, ignoreCase = true)) {
                    return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                }

                //  handle non-primary volumes
            } else if (isDownloadsDocument(uri)) {
                val id = DocumentsContract.getDocumentId(uri)
                val contentUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"),
                    java.lang.Long.valueOf(id)
                )
                return getDataColumn(
                    context!!,
                    contentUri,
                    null,
                    null
                )
            } else if (isMediaDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).toTypedArray()
                val type = split[0]
                var contentUri: Uri? = null
                if ("image" == type) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                } else if ("video" == type) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                } else if ("audio" == type) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }
                val selection = "_id=?"
                val selectionArgs = arrayOf(
                    split[1]
                )
                return getDataColumn(
                    context!!,
                    contentUri,
                    selection,
                    selectionArgs!!
                )
            }
        } else if ("content".equals(uri.scheme, ignoreCase = true)) {

            // Return the remote address
            return if (isGooglePhotosUri(uri)) uri.lastPathSegment else getDataColumn(
                context!!,
                uri,
                null,
                null
            )
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            return uri.path
        }
        return null
    }
    fun isGooglePhotosUri(uri: Uri): Boolean {
        return "com.google.android.apps.photos.content" == uri.authority
    }
    fun getDataColumn(
        context: Context, uri: Uri?, selection: String?,
        selectionArgs: Array<String>?
    ): String? {
        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(
            column
        )
        try {
            cursor = context.contentResolver.query(
                uri!!, projection, selection, selectionArgs,
                null
            )
            if (cursor != null && cursor.moveToFirst()) {
                val index = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(index)
            }
        } finally {
            cursor?.close()
        }
        return null
    }

    fun getTemporaryCameraFile(): File {
        val storageDir = getAppExternalDataDirectoryFile()
        val file = File(storageDir, getTemporaryCameraFileName())
        try {
            file.createNewFile()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return file
    }

    private fun getAppExternalDataDirectoryFile(): File {
        val dataDirectoryFile = File(getAppExternalDataDirectoryPath())
        dataDirectoryFile.mkdirs()
        return dataDirectoryFile
    }

    private fun getAppExternalDataDirectoryPath(): String {
        val sb = StringBuilder()
        sb.append(Environment.getExternalStorageDirectory())
            .append(File.separator)
            .append("Android")
            .append(File.separator)
            .append("data")
            .append(File.separator)
            .append(requireActivity().packageName) //
            .append(File.separator)
        return sb.toString()
    }

    private fun getTemporaryCameraFileName(): String {
        return "camera_" + System.currentTimeMillis() + ".jpg"
    }
    //////



    fun getRealPathFromURI(uri: Uri?): String? {
        var path = ""
        if (activity?.contentResolver != null) {
            val cursor: Cursor =
                    activity?.contentResolver!!.query(uri!!, null, null, null, null)!!
            if (cursor != null) {
                cursor.moveToFirst()
                val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
                path = cursor.getString(idx)
                cursor.close()
            }
        }
        return path
    }

    fun openCamera1() {
        if (hasCameraPermission()) {
            try {
                val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                if (takePictureIntent.resolveActivity(activity?.packageManager!!) != null) {
                    var photoFile: File? = null
                    try {
                        photoFile = createImageFile()
                    } catch (ex: IOException) {
                        ex.printStackTrace()
                    }
                    if (photoFile != null) {
                        val photoURI =
                                FileProvider.getUriForFile(
                                        requireActivity(),
                                        requireActivity().packageName,
                                        photoFile
                                )
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                        startActivityForResult(takePictureIntent, RC_CAMERA_PERM)
                    }
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        } else {
            EasyPermissions.requestPermissions(
                    this,
                    "This app needs access to your camera so you can take pictures.",
                    RC_CAMERA_PERM,
                    Manifest.permission.CAMERA
            )
        }
    }
    fun openCamera() {
        if (hasCameraPermission()) {
            try {
                val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                if (takePictureIntent.resolveActivity(activity?.packageManager!!) != null) {
                    try {
                        photoFile = getTemporaryCameraFile()
                    } catch (ex: IOException) {
                        ex.printStackTrace()
                    }
                    if (photoFile != null) {
                        val photoURI =
                            getValidUri(photoFile, requireContext())
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                        startActivityForResult(takePictureIntent, RC_CAMERA_PERM)
                    }
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        } else {
            EasyPermissions.requestPermissions(
                this,
                "This app needs access to your camera so you can take pictures.",
                RC_CAMERA_PERM,
                Manifest.permission.CAMERA
            )
        }
    }
    fun getValidUri(file: File, context: Context?): Uri {
        val outputUri: Uri
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val authority = context!!.packageName /*+ ".fileprovider"*/
            outputUri = FileProvider.getUriForFile(context, authority, file)
        } else {
            outputUri = Uri.fromFile(file)
        }
        return outputUri
    }
    private fun setRefresh() {
        swf_cart?.setOnRefreshListener {
            if (getUserId().isEmpty()) {
                tvNoCartitem?.visibility = View.VISIBLE
                llNoCartitem?.visibility = View.VISIBLE
                cart_scrollview?.visibility = View.GONE
                swf_cart?.isRefreshing = false
            } else {
                Log.e("cart", "70")
                CallApiCartList(false)
            }
        }
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_checkout -> {
                //CallApiCheckout()
                lastTimeSel = SystemClock.elapsedRealtime() - mLastClickTime;
                if (lastTimeSel > 0 && lastTimeSel < 1000) {
                    return
                }
                var count = 0
                mLastClickTime = SystemClock.elapsedRealtime()
                if (this::arrlist.isInitialized) {
                    for (i in arrlist.indices) {
                        if (arrlist.get(i).itemPrice.equals("0.00")) {
                            activity?.showToast(getString(R.string.outof_stock), Const.ALERT)
                            count++
                            break
                        }
                        if(arrlist.get(i).OutOfStock){
                            activity?.showToast(getString(R.string.outof_stock), Const.ALERT)
                            count++
                            break
                        }
                    }


                }

                Log.e("///////count",count.toString())
                if (count == 0) {

                    val intent = Intent(requireActivity(), AddAddressActivity::class.java)
                    val mStrNoteText =  edtAddNote.text.toString().trim()
                    if(TextUtils.isEmpty(mStrNoteText) && finalImageFile!=null){
                        Toast.makeText(context,"Please add note",Toast.LENGTH_SHORT).show()
                    }else{
                        if (chkwallet.isChecked) {
                            intent.putExtra(Const.USE_CREDIT, true)
                            intent.putExtra("note",mStrNoteText.trim())
                            intent.putExtra("noteImage", finalImageFile)
                        } else {
                            intent.putExtra(Const.USE_CREDIT, false)
                            intent.putExtra("note",mStrNoteText.trim())
                            intent.putExtra("noteImage", finalImageFile)
                        }
                        startActivity(intent)
                        edtAddNote.setText("")
                        finalImageFile=null
                    }
                }
            }
        }
    }

    fun CallApiCartList(isProgress: Boolean) {

        if (isInternetAvailable(requireActivity())) {
            if (isProgress && App.isShowProgressbar)
                mprogressbar.showProgressBar()

            val retrofit = WebService.getRetrofit(requireActivity()).create(ApiClient::class.java)
            retrofit.GetCartList()
                .enqueue(object : Callback<ResponseCartItems> {
                    override fun onFailure(call: Call<ResponseCartItems>, t: Throwable) {
                        mprogressbar.hideProgressBar()
                        swf_cart?.isRefreshing = false
                    }

                    override fun onResponse(
                        call: Call<ResponseCartItems>,
                        response: Response<ResponseCartItems>?
                    ) {
                        Log.e("cart", "onResponse 155")
                        mprogressbar.hideProgressBar()
                        swf_cart?.isRefreshing = false

                        if (response != null) {
                            if (response.isSuccessful) {
                                val responsecart = response.body()
                                if (responsecart != null) {
                                    try {
                                        if (responsecart.status == API_SUCESS) {
                                            if (isAdded) {
                                                llNoCartitem?.visibility = View.GONE
                                                tvNoCartitem?.visibility = View.GONE

                                                cart_scrollview?.visibility = View.VISIBLE
                                                val data = responsecart.data
                                                Log.e("cart", data.toString())
                                                setData(data)
                                            }
                                        } else if (responsecart.status == API_DATA_NOT_AVAILBLE) {
                                            llNoCartitem?.visibility = View.VISIBLE
                                            tvNoCartitem?.visibility = View.VISIBLE
                                            cart_scrollview?.visibility = View.GONE
                                        }
                                    } catch (e: IllegalStateException) {
                                        e.printStackTrace()
                                    }
                                }
                            } else {
                                activity?.showToast(
                                    getString(R.string.msg_common_error),
                                    Const.ALERT
                                )
                            }
                        }
                    }
                })
        } else {
            NetworkErrorDialog(requireActivity())
            swf_cart?.isRefreshing = false
        }
    }

    private fun setData(data: Data) {

        try {
            if (data.grandTotal.equals(Const.DUMMY_VAUE)) {
                btn_checkout.visibility = View.GONE
            } else {
                btn_checkout.visibility = View.VISIBLE
            }

            total = data.grandTotal.toFloat()
            subtotal = data.subTotal.toFloat()
            tvGrandTotal.text = getString(R.string.currency) + " " + String.format("%.2f", total)
            tvSubTotal.text = getString(R.string.currency) + " " + String.format("%.2f", subtotal)

            val strgst = data.GST
            if (strgst.isEmpty() || strgst.equals(Const.DUMMY_VAUE)) {
                rl_gst.visibility = View.GONE
            } else {
                tvGST.text = getString(R.string.currency) + " " + data.GST
                rl_gst.visibility = View.VISIBLE
            }


            val strwallet = data.walletBalance

            tvwalletamnt.text = getString(R.string.currency) + " " + strwallet
            rl_wallet.visibility = View.VISIBLE

            val strmemberdisscount = data.MembershipDiscount
            tvmembership.text = getString(R.string.currency) + " " + strmemberdisscount
            rl_membership.visibility = View.VISIBLE

            val strshipingcharg = data.ShippingCharges
            if (strshipingcharg.isEmpty() || strshipingcharg.equals(Const.DUMMY_VAUE)) {
                rl_ShipCharge.visibility = View.GONE
            } else {
                tv_ship_charge.text = getString(R.string.currency) + " " + data.ShippingCharges
                rl_ShipCharge.visibility = View.VISIBLE
            }
            arrlist = ArrayList()
            arrlist.clear()
            arrlist.removeAll(data.items)
            arrlist.addAll(data.items)
            Log.e("//////","///////"+arrlist.size.toString())
            rvOrder.apply {
                layoutManager = LinearLayoutManager(context)
                order_list = CartAdapter(this@CartFragment, arrlist)
                adapter = order_list
            }

        } catch (e: Exception) {
            Log.e("error", e.toString())
        }
    }

    fun DeletConfimationDialog(iD: String, position: Int, quantity: String) {
        dialog = CustomDialog(requireActivity(), "",
            getString(R.string.msg_confirmation_delete),
            "Yes",
            "No",
            CustomDialog.ItemReturnListener {
                DeleteCartItem(iD, position, quantity)
            })
        dialog.show()
    }


    fun MoveConfimationDialog(iD: String, Varid: String, position: Int) {
        dialog = CustomDialog(requireActivity(), "",
            getString(R.string.msg_confirmation_wish),
            "Yes",
            "No",
            CustomDialog.ItemReturnListener {
//                Log.e("tagWishList", "CustomDialog called");
                CallApiAddWishList(iD, Varid, position)
            })
        dialog.show()
    }

    fun DeleteCartItem(productId: String, position: Int, quantity: String) {

        if (isInternetAvailable(requireActivity())) {
            mprogressbar.showProgressBar()

            val param = ParamCartItemremove().apply {
                itemId = productId
            }

            val retrofit = WebService.getRetrofit(requireActivity()).create(ApiClient::class.java)
            retrofit.CartDeleteItem(param)
                .enqueue(object : Callback<ResponseCartItemRemove> {
                    override fun onFailure(call: Call<ResponseCartItemRemove>, t: Throwable) {
                        mprogressbar.hideProgressBar()
                    }

                    override fun onResponse(
                        call: Call<ResponseCartItemRemove>,
                        response: Response<ResponseCartItemRemove>?
                    ) {
                        mprogressbar.hideProgressBar()
                        if (response != null) {
                            if (response.isSuccessful) {
                                val responsecart = response.body()
                                if (responsecart != null) {

                                    if (responsecart.status == API_SUCESS) {

                                        activity?.showToast(getString(R.string.msg_product_remove_cart), Const.TOAST)
                                        storeCartItem(getCartItem()?.minus(quantity.toInt())!!)

                                        try {
                                            if ((arrlist.size - 1) == 0) {
                                                llNoCartitem?.visibility = View.VISIBLE
                                                tvNoCartitem?.visibility = View.VISIBLE
                                                cart_scrollview?.visibility = View.GONE
                                                homeActivity?.badgetext?.visibility = View.GONE

                                            } else {

                                                val data = responsecart.data
                                                total = data.grandTotal.toFloat()
                                                subtotal = data.subTotal.toFloat()

                                                val strmemberdisscount = data.MembershipDiscount
                                                tvmembership.text = getString(R.string.currency) + " " + strmemberdisscount
                                                rl_membership.visibility = View.VISIBLE

                                                tvGrandTotal.text = getString(R.string.currency) + " " + String.format("%.2f", total)
                                                tvSubTotal.text = getString(R.string.currency) + " " + String.format("%.2f", subtotal)

                                                val strgst = data.GST

                                                if (strgst.isEmpty() || strgst.equals(Const.DUMMY_VAUE)) {
                                                    rl_gst?.visibility = View.GONE
                                                } else {
                                                    tvGST.text = getString(R.string.currency) + " " + data.GST
                                                    rl_gst?.visibility = View.VISIBLE
                                                }

                                                val strshipingcharg = data.ShippingCharges
                                                if (strshipingcharg.isEmpty() || strshipingcharg.equals(Const.DUMMY_VAUE)) {
                                                    rl_ShipCharge?.visibility = View.GONE
                                                } else {
                                                    tv_ship_charge.text = getString(R.string.currency) + " " + data.ShippingCharges
                                                    rl_ShipCharge?.visibility = View.VISIBLE
                                                }


                                            }
                                            homeActivity?.badgetext?.setText(getCartItem().toString())
                                            arrlist.removeAt(position)
                                            order_list.notifyDataSetChanged()
                                         //   order_list.move(position)

                                            
                                        }
                                        catch (e: IndexOutOfBoundsException) {
                                            e.printStackTrace()
                                        } catch (e: NumberFormatException) {
                                            e.printStackTrace()
                                        }
                                    }
                                }
                            } else {
                                activity?.showToast(
                                    getString(R.string.msg_common_error),
                                    Const.ALERT
                                )
                            }
                        }
                    }
                })

        } else {
            NetworkErrorDialog(requireActivity())
        }

    }

    fun CallApiUpdateCartItem(
        b: Boolean,
        tv: TextView,
        productId: String,
        varientId: String
    ) {
        if (isInternetAvailable(requireActivity())) {
            mprogressbar.showProgressBar()

            var qty: Int = tv.text.toString().toInt()
            if (b) {
                ++qty
            } else {
                --qty
            }
            val param = ParamAddProductNew().apply {
                this.productId = productId
                variationId = varientId
                addFromWishlist = "0"
                quantity = qty.toString()
                status = Const.STATUS_UPDATE_CART
            }
            param.addFromWishlist = "0"

            val retrofit = WebService.getRetrofit(requireActivity()).create(ApiClient::class.java)
            retrofit.AddProducts(param)
                .enqueue(object : Callback<ResponseAddProduct> {
                    override fun onFailure(call: Call<ResponseAddProduct>, t: Throwable) {
                        mprogressbar.hideProgressBar()
                    }

                    override fun onResponse(
                        call: Call<ResponseAddProduct>,
                        response: Response<ResponseAddProduct>?
                    ) {
                        mprogressbar.hideProgressBar()
                        if (response != null) {
                            if (response.isSuccessful) {
                                val responseproduct = response.body()
                                if (responseproduct != null) {
                                    if (responseproduct.status == API_SUCESS) {

                                        if (b) {
                                            tv.text = (qty).toString()
                                            storeCartItem(getCartItem()?.plus(1)!!)
                                        } else {
                                            tv.text = (qty).toString()
                                            storeCartItem(getCartItem()?.minus(1)!!)
                                        }
                                        homeActivity?.badgetext?.setText(getCartItem().toString())

                                        val data = responseproduct.data
                                        tvGrandTotal.text =
                                            getString(R.string.currency) + " " + data.grandTotal
                                        tvSubTotal.text =
                                            getString(R.string.currency) + " " + data.subTotal

                                        val strmemberdisscount = data.MembershipDiscount
                                        tvmembership.text =
                                            getString(R.string.currency) + " " + strmemberdisscount
                                        rl_membership.visibility = View.VISIBLE

                                        val strgst = data.GST
                                        if (strgst.isEmpty() || strgst.equals(Const.DUMMY_VAUE)) {
                                            rl_gst?.visibility = View.GONE
                                        } else {
                                            tvGST.text =
                                                getString(R.string.currency) + " " + data.GST
                                            rl_gst?.visibility = View.VISIBLE
                                        }

                                        val strshipingcharg = data.ShippingCharges
                                        if (strshipingcharg.isEmpty() || strshipingcharg.equals(
                                                Const.DUMMY_VAUE
                                            )
                                        ) {
                                            rl_ShipCharge?.visibility = View.GONE
                                        } else {
                                            tv_ship_charge.text =
                                                getString(R.string.currency) + " " + data.ShippingCharges
                                            rl_ShipCharge?.visibility = View.VISIBLE
                                        }
                                    } else if (responseproduct.status == API_DATA_NOT_AVAILBLE) {
                                        activity?.showToast(responseproduct.message, Const.ALERT)
                                    }
                                }
                            } else {
                                requireActivity().showToast(
                                    getString(R.string.msg_common_error),
                                    Const.ALERT
                                )
                            }
                        }
                    }
                })

        } else {
            NetworkErrorDialog(requireActivity())
        }
    }

    fun loadProductDetail(id: String) {
        val fragment = ProductDetailFragment().getInstance(id, "")//.newInstance(refProv)
        homeActivity?.deselectAllMenu()
        homeActivity?.supportFragmentManager
            ?.beginTransaction()
            ?.add(R.id.cm_fragmentContainer, fragment, fragment::class.java.name)
            ?.addToBackStack(null)
            ?.commit()
    }

    fun CallApiAddWishList(Pid: String, VarId: String, Pos: Int) {
        if (isInternetAvailable(requireActivity())) {
//            Log.e("tagWishList", "CustomDialog called Pid " + Pid);
            val param = ParamAddREmoveWishItem().apply {
                productId = Pid
//                variationId = VarId
                variationId = "0"
                status = "1"

            }

            val retrofit = WebService.getRetrofit(requireActivity()).create(ApiClient::class.java)
            retrofit.AddRemoveWishItem(param)
                .enqueue(object : Callback<ResponseAddRemoveWishItem> {
                    override fun onFailure(call: Call<ResponseAddRemoveWishItem>, t: Throwable) {
//                        Log.e("tagWishList", "CustomDialog called onFailure " + t.message);
                    }

                    override fun onResponse(
                        call: Call<ResponseAddRemoveWishItem>,
                        response: Response<ResponseAddRemoveWishItem>?
                    ) {
//                        Log.e("tagWishList", "CustomDialog called onResponse ");
                        if (response != null) {
                            if (response.isSuccessful) {
                                val responseproduct = response.body()
                                if (responseproduct != null) {
                                    if (responseproduct.status == API_SUCESS) {
                                        try {
                                            if ((arrlist.size - 1) == 0) {
                                                llNoCartitem?.visibility = View.VISIBLE
                                                tvNoCartitem?.visibility = View.VISIBLE
                                                cart_scrollview.visibility = View.GONE
                                                homeActivity?.badgetext?.visibility = View.GONE
                                            } else {
//                                                Log.e("tagWishList", "CallApiAddWishList called arrlist " + arrlist.size);
//                                                Log.e("tagWishList", "CallApiAddWishList called Pos " + Pos);
                                                total = total - NumberFormat.getNumberInstance(java.util.Locale.US).parse(arrlist.get(Pos).itemPrice).toFloat()
                                                subtotal =
                                                    subtotal - NumberFormat.getNumberInstance(java.util.Locale.US).parse(arrlist.get(Pos).itemPrice).toFloat()
                                                tvGrandTotal.text =
                                                    getString(R.string.currency) + " " + String.format(
                                                        "%.2f",
                                                        total
                                                    )
                                                tvSubTotal.text =
                                                    getString(R.string.currency) + " " + String.format(
                                                        "%.2f",
                                                        subtotal
                                                    )
                                            }
                                            homeActivity?.badgetext?.setText(getCartItem().toString())
                                            order_list.move(Pos)
                                        } catch (e: IndexOutOfBoundsException) {
                                            Log.e("tagWishList", "CallApiAddWishList called e.message " + e.message);
                                            e.printStackTrace()
                                        } catch (e: Exception) {
                                            Log.e("tagWishList", "CallApiAddWishList called error final " + e.message);
                                            e.printStackTrace()
                                        }
                                    }
                                }
                            } else {
                                requireActivity().showToast(
                                    getString(R.string.msg_common_error),
                                    Const.ALERT
                                )
                            }
                        }
                    }
                })
        } else {
            NetworkErrorDialog(requireActivity())
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {

        }
    }

}

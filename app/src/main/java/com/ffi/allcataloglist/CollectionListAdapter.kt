package com.ffi.allcataloglist

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ffi.App
import com.ffi.GlideApp
import com.ffi.R
import com.ffi.Utils.*
import com.ffi.catalog.Record
import com.ffi.collectiondetail.ParamCollectionDetail
import com.ffi.collectiondetail.ResponseCollectionDetails
import com.ffi.api.ApiClient
import com.ffi.api.WebService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CollectionListAdapter(
    val activity: CollctionListFragment,
    val data: List<Record>
) :
    RecyclerView.Adapter<CollectionListHolder>() {

    var mFirstWidth = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollectionListHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.adapter_collectionlist, parent, false)

        return CollectionListHolder(view)
    }


    override fun getItemCount() = data.size


    override fun onBindViewHolder(holder: CollectionListHolder, position: Int) {
        val model = data[position]

        if (model.Media.size != 0) {
            GlideApp.with(activity)
                .load(getMediaLink() + model.Media.get(0).Media)
                .placeholder(R.color.color_light_grey)
                .into(holder.mainimg)
        }

        holder.tvDesc.text = model.Description
        holder.tvitemname.text = model.Name
        //holder.tvprice.text = activity.getString(R.string.rs) + " " + model.Price

        holder.li_main.setOnClickListener {
            App.posisition=position
            activity.loadCollectionItems(model.ID, model.Name)
        }

        if (mFirstWidth > 0) {
            holder.ivimg1.layoutParams.width = mFirstWidth
            holder.ivimg1.layoutParams.height = (mFirstWidth / 2f).toInt()
        }

        holder.tvOthers.setOnClickListener {
            if (getUserId().isEmpty()) {
/*                val intent = Intent(activity.context, LoginActivity::class.java)
                activity.startActivity(intent)
                App.isRedirect = true*/
                activity.requireActivity().showLoginMsg()
            } else {
                CallApiCatalogItemList(true, model, Const.OTHERS)

                //activity.DialogShare(model, Const.OTHERS)
            }
        }

        holder.li_othershare.setOnClickListener {
            if (getUserId().isEmpty()) {
                /* val intent = Intent(activity.context, LoginActivity::class.java)
                 activity.startActivity(intent)
                 App.isRedirect = true*/
                activity.requireActivity().showLoginMsg()
            } else {

                CallApiCatalogItemList(true, model, Const.OTHERS)

                //activity.DialogShare(model, Const.OTHERS)
            }
        }
        holder.tvfacebook.setOnClickListener {
            if (getUserId().isEmpty()) {
                /*  val intent = Intent(activity.context, LoginActivity::class.java)
                  activity.startActivity(intent)
                  App.isRedirect = true*/
                activity.requireActivity().showLoginMsg()
            } else {
                CallApiCatalogItemList(true, model, Const.FACEBOOK)

                // activity.DialogShare(model, Const.FACEBOOK)
            }
        }
        holder.li_fb.setOnClickListener {
            if (getUserId().isEmpty()) {
                /* val intent = Intent(activity.context, LoginActivity::class.java)
                 activity.startActivity(intent)
                 App.isRedirect = true*/
                activity.requireActivity().showLoginMsg()
            } else {
                CallApiCatalogItemList(true, model, Const.FACEBOOK)

                //activity.DialogShare(model, Const.FACEBOOK)
            }
        }

        holder.li_whatsapp.setOnClickListener {
            if (getUserId().isEmpty()) {
                /* val intent = Intent(activity.context, LoginActivity::class.java)
                 activity.startActivity(intent)
                 App.isRedirect = true*/
                activity.requireActivity().showLoginMsg()
            } else {

                CallApiCatalogItemList(true, model, Const.WHATSAPP)

                // activity.DialogShare(model, Const.WHATSAPP)
            }
        }

        holder.tvDwonload.setOnClickListener {
//            if (getUserId().isEmpty()) {
//                activity.requireActivity().showToast(activity.getString(R.string.msg_login_require))
//            } else {
            CallApiCatalogItemList(true, model, "")
            /*  activity.DownloadTask(
                  model.Media,
                  model.ID,
                  true
              ).execute()*/
            //}
        }

        holder.li_download.setOnClickListener {
            /*if (getUserId().isEmpty()) {
                activity.requireActivity().showToast(activity.getString(R.string.msg_login_require))
            } else {*/

            CallApiCatalogItemList(true, model, "")
            /*activity.DownloadTask(
                model.Media,
                model.ID,
                true
            ).execute()*/

        }
        //}

    }

    fun CallApiCatalogItemList(
        isprogress: Boolean,
        model: Record,
        strType: String
    ) {
        lateinit var mprogressbar: ProgressBarHandler
        mprogressbar = ProgressBarHandler(activity.requireActivity())

        var arrdata: List<com.ffi.collectiondetail.Record> = listOf()

        if (isInternetAvailable(activity.requireActivity())) {
            if (isprogress)
                mprogressbar.showProgressBar()

            val param = ParamCollectionDetail().apply {
                page = "1"
                limit = Const.PAGINATION_LIMIT
                catalogueId = model.ID
            }
            val retrofit =
                WebService.getRetrofit(activity.requireActivity()).create(ApiClient::class.java)
            retrofit.GetCatalogDetails(param)
                .enqueue(object : Callback<ResponseCollectionDetails> {
                    override fun onFailure(call: Call<ResponseCollectionDetails>, t: Throwable) {
                        mprogressbar.hideProgressBar()
                    }

                    override fun onResponse(
                        call: Call<ResponseCollectionDetails>,
                        response: Response<ResponseCollectionDetails>?
                    ) {
                        mprogressbar.hideProgressBar()

                        if (response != null) {
                            if (response.isSuccessful) {
                                val responseLogin = response.body()
                                if (responseLogin != null) {
                                    if (responseLogin.status == API_SUCESS) {
                                        if (activity.isAdded) {
                                            arrdata = ArrayList()
                                            arrdata = responseLogin.data.records

                                            var mTotalSize = 0
                                            for (j in arrdata.indices) {
                                                val data = arrdata.get(j)
                                                mTotalSize += data.media.size
                                            }

                                            Log.e("mTotalSize_", mTotalSize.toString() + "_");


                                            if (strType.equals("")) {
                                                for (j in arrdata.indices) {
                                                    val data = arrdata.get(j)
                                                    activity.DownloadTaskAll(
                                                        mTotalSize,
                                                        data.media,
                                                        data.iD,
                                                        true
                                                    ).execute()
                                                }
                                            } else if (strType.equals(Const.FACEBOOK)) {
                                                activity.DialogShare(
                                                    arrdata,
                                                    Const.FACEBOOK
                                                )
                                            } else if (strType.equals(Const.WHATSAPP)) {
                                                activity.DialogShare(
                                                    arrdata,
                                                    Const.WHATSAPP
                                                )
                                            } else if (strType.equals(Const.OTHERS)) {
                                                activity.DialogShare(
                                                    arrdata,
                                                    Const.OTHERS
                                                )
                                            }

                                        }
                                    } else if (responseLogin.status == API_DATA_NOT_AVAILBLE) {
                                        if (activity.isAdded) {

                                            activity.DownloadTask(
                                                model.Media,
                                                model.ID,
                                                true
                                            ).execute()
                                        }
                                    }
                                }
                            } else {
                                activity.requireActivity()
                                    .showToast(activity.getString(R.string.msg_common_error),Const.ALERT)
                            }
                        }
                    }
                })
        } else {
            NetworkErrorDialog(activity.requireActivity())
        }
    }

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
    }
}
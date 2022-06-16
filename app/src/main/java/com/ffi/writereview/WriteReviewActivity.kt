package com.ffi.writereview

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.ffi.R
import com.ffi.Utils.*
import com.ffi.api.ApiClient
import com.ffi.api.WebService
import kotlinx.android.synthetic.main.activity_write_review.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WriteReviewActivity : AppCompatActivity(), View.OnClickListener {

    private var strtitle = ""
    private var strdesc = ""
    private var strproductId = ""
    private var strOrderId = ""
    lateinit var mprogressbar: ProgressBarHandler

    lateinit var arrRatingId: List<Data>
    var strRatingid = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write_review)

        init()
    }

    private fun init() {
        strproductId = intent.getStringExtra(Const.PRODUCT_ID)!!
        strOrderId = intent.getStringExtra(
            Const.ORDER_ID
        )!!
        mprogressbar = ProgressBarHandler(this)

        arrRatingId = ArrayList()

        ivBack.visibility = View.VISIBLE
        ivEdit.visibility = View.GONE
        txt_toolbar.text = getString(R.string.title_review)

        CallApiRatingIds()
        setListener()
    }

    private fun setListener() {
        ivBack.setOnClickListener(this)
        btn_submit.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBack -> {
                onBackPressed()
            }
            R.id.btn_submit -> {
                isValid()
            }

        }
    }

    private fun isValid() {
        strtitle = edt_title.text.toString().trim()
        strdesc = edt_review.text.toString().trim()

        if (strtitle.isEmpty()) {
            showToast(getString(R.string.review_title), Const.ALERT)
        } else if (strdesc.isEmpty()) {
            showToast(getString(R.string.review_add), Const.ALERT)
        } else if (mrb_rating.rating.toString().equals("0.0")) {
            showToast(getString(R.string.give_rating), Const.ALERT)
        } else {
            CallApiAddReview()
        }
    }

    private fun CallApiAddReview() {
        if (isInternetAvailable(this)) {
            mprogressbar.showProgressBar()
            for (i in arrRatingId.indices) {
                val rating = mrb_rating.rating.toInt().toString()
                if (arrRatingId.get(i).rating.equals(rating)) {
                    strRatingid = arrRatingId.get(i).iD
                    break
                }
            }
            val param = ParamAddReview().apply {
                title = strtitle
                productId = strproductId
                comment = strdesc
                ratingId = strRatingid
                OrderId = strOrderId
            }

            val retrofit = WebService.getRetrofit(this@WriteReviewActivity).create(ApiClient::class.java)
            retrofit.WriteReview(param)
                .enqueue(object : Callback<ResponseAddReview> {
                    override fun onFailure(call: Call<ResponseAddReview>, t: Throwable) {
                        mprogressbar.hideProgressBar()
                    }

                    override fun onResponse(
                        call: Call<ResponseAddReview>,
                        response: Response<ResponseAddReview>?
                    ) {
                        mprogressbar.hideProgressBar()
                        if (response != null) {
                            if (response.isSuccessful) {
                                val body = response.body()
                                if (body != null) {
                                    if (body.status.equals(API_SUCESS)) {
                                        showToast(
                                            getString(R.string.msg_review_submit),
                                            Const.ALERT
                                        )
                                        finish()
                                    } else {
                                    }
                                }
                            } else {
                                showToast(getString(R.string.msg_common_error), Const.ALERT)
                            }
                        }
                    }
                })
        } else {
            NetworkErrorDialog(this)
        }
    }

    private fun CallApiRatingIds() {
        if (isInternetAvailable(this)) {
            mprogressbar.showProgressBar()

            val retrofit = WebService.getRetrofit(this@WriteReviewActivity).create(ApiClient::class.java)
            retrofit.GetRatingid()
                .enqueue(object : Callback<ResponseRatingsId> {
                    override fun onFailure(call: Call<ResponseRatingsId>, t: Throwable) {
                        mprogressbar.hideProgressBar()
                    }

                    override fun onResponse(
                        call: Call<ResponseRatingsId>,
                        response: Response<ResponseRatingsId>?
                    ) {
                        mprogressbar.hideProgressBar()
                        if (response != null) {
                            if (response.isSuccessful) {
                                val body = response.body()
                                if (body != null) {
                                    if (body.status.equals(API_SUCESS)) {
                                        arrRatingId = body.data
                                        for (i in arrRatingId.indices) {
                                            val rating = mrb_rating.rating.toInt().toString()
                                            if (arrRatingId.get(i).rating.equals(rating)) {
                                                strRatingid = arrRatingId.get(i).iD
                                                break
                                            }
                                        }
                                    }
                                }
                            } else {
                                showToast(getString(R.string.msg_common_error), Const.ALERT)
                            }
                        }
                    }
                })
        } else {
            NetworkErrorDialog(this)
        }
    }


}

package com.ffi.reviewlist

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.ffi.R
import com.ffi.Utils.*
import com.ffi.writereview.WriteReviewActivity
import com.ffi.api.ApiClient
import com.ffi.api.WebService
import kotlinx.android.synthetic.main.activity_review_list.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ReviewListActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var review_list: ReviewListAdapter
    lateinit var mprogressbar: ProgressBarHandler
    private var strproductId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review_list)

        mprogressbar = ProgressBarHandler(this)
        init()
    }

    private fun init() {
        strproductId = intent.getStringExtra(Const.PRODUCT_ID)!!

        ivBack.visibility = View.VISIBLE
        ivEdit.visibility = View.GONE
        txt_toolbar.text = getString(R.string.lbl_reviews)

        li_content.requestFocus()

        CallApiReview()
        setListener()
    }

    private fun setListener() {
        // tvTotalRating.setOnClickListener(this)
        ivBack.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBack -> {
                onBackPressed()
            }
            R.id.tvTotalRating -> {
                val intent = Intent(this, WriteReviewActivity::class.java)
                intent.putExtra(Const.PRODUCT_ID, strproductId)
                startActivity(intent)
            }
        }
    }

    private fun CallApiReview() {
        if (isInternetAvailable(this)) {
            mprogressbar.showProgressBar()

            val param = ParamReviews().apply {
                limit = Const.PAGINATION_LIMIT
                page = "1"
                productId = strproductId

            }

            val retrofit = WebService.getRetrofit(this@ReviewListActivity).create(ApiClient::class.java)
            retrofit.GetAllReviews(param)
                .enqueue(object : Callback<ResponseReviews> {
                    override fun onFailure(call: Call<ResponseReviews>, t: Throwable) {
                        mprogressbar.hideProgressBar()
                    }

                    override fun onResponse(
                        call: Call<ResponseReviews>,
                        response: Response<ResponseReviews>?
                    ) {
                        mprogressbar.hideProgressBar()
                        if (response != null) {
                            if (response.isSuccessful) {
                                val body = response.body()
                                if (body != null) {
                                    if (body.status.equals(API_SUCESS)) {
                                        nested_scroll.visibility = View.VISIBLE
                                        setData(body.data)
                                    } else {
                                    }
                                }
                            } else {
                                showToast(getString(R.string.msg_common_error),Const.ALERT)
                            }
                        }
                    }
                })
        } else {
            NetworkErrorDialog(this)
        }
    }

    private fun setData(data: Data) {
        tvAvgRating.text = data.overAllRating.toString()
        tvTotalRating.text = data.TotalRating.toString() + " Ratings"

        try {

            rcp1.progress = data.ratingList.get(4).Percentage.toFloat()
            rcp2.progress = data.ratingList.get(3).Percentage.toFloat()
            rcp3.progress = data.ratingList.get(2).Percentage.toFloat()
            rcp4.progress = data.ratingList.get(1).Percentage.toFloat()
            rcp5.progress = data.ratingList.get(0).Percentage.toFloat()
        } catch (e: IndexOutOfBoundsException) {
            e.printStackTrace()
        }

        rvReview.apply {
            layoutManager = LinearLayoutManager(context)
            review_list = ReviewListAdapter(this@ReviewListActivity, data.items)
            adapter = review_list
        }
    }
}

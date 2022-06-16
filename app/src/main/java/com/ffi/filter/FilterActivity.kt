package com.ffi.filter

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.ffi.R
import com.ffi.Utils.*
import com.ffi.Utils.Const.Companion.RESULT_FILTER
import com.ffi.api.ApiClient
import com.ffi.api.WebService
import kotlinx.android.synthetic.main.activity_filter.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FilterActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var mprogressbar: ProgressBarHandler

    var arrSelecteddataid: ArrayList<String> = ArrayList()
    var listdata: List<Data> = listOf()
    lateinit var subfilter_list: SubFilterAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filter)

        txt_toolbar.text = getString(R.string.title_filter)
        ivEdit.visibility = View.GONE
        ivBack.visibility = View.VISIBLE

        setListener()

        mprogressbar = ProgressBarHandler(this)
        CallApiGetFilterData()
    }

    private fun setListener() {
        btn_reset.setOnClickListener(this)
        btn_apply.setOnClickListener(this)
        ivBack.setOnClickListener(this)
    }

    private fun CallApiGetFilterData() {
        if (isInternetAvailable(this)) {
            mprogressbar.showProgressBar()

            val retrofit = WebService.getRetrofit(this).create(ApiClient::class.java)
            retrofit.GetfilterData()
                .enqueue(object : Callback<ResponseFilter> {
                    override fun onFailure(call: Call<ResponseFilter>, t: Throwable) {
                        mprogressbar.hideProgressBar()
                    }

                    override fun onResponse(
                        call: Call<ResponseFilter>,
                        response: Response<ResponseFilter>?
                    ) {
                        mprogressbar.hideProgressBar()
                        if (response != null) {
                            if (response.isSuccessful) {
                                val responsefilter = response.body()
                                if (responsefilter != null) {
                                    if (responsefilter.status == API_SUCESS) {
                                        listdata = responsefilter.data
                                        setData(listdata)
                                    } else if (responsefilter.status == API_DATA_NOT_AVAILBLE) {

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

    private fun setData(data: List<Data>) {
        for (i in data.indices) {
            if (i == 0) {
                data.get(i).isSelected = true
            } else {
                data.get(i).isSelected = false
            }
        }

        rvfilter.apply {
            layoutManager = LinearLayoutManager(this@FilterActivity)
            val catalog_list =
                MainFilterAdapter(
                    this@FilterActivity,
                    data,
                    FilterClickInterface { potition, varientid ->
                        setDataofSubfilter(data.get(potition).data)
                    })

            adapter = catalog_list
        }
        setDataofSubfilter(data.get(0).data)
    }

    private fun setDataofSubfilter(data: List<DataX>) {
        rvSubfilter.apply {
            layoutManager = LinearLayoutManager(this@FilterActivity)
            subfilter_list =
                SubFilterAdapter(
                    this@FilterActivity,
                    data, rvSubfilter,
                    FilterClickInterface { potition, varientid ->
                        if (arrSelecteddataid.contains(varientid)) {
                            arrSelecteddataid.remove(varientid)
                        } else {
                            arrSelecteddataid.add(varientid)
                        }
                    })
            adapter = subfilter_list
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBack -> {
                finish()
            }
            R.id.btn_reset -> {
                RemoveSelection()

            }
            R.id.btn_apply -> {
                val intent = Intent()
                intent.putExtra(Const.FILTER_DATA, arrSelecteddataid.toString())
                setResult(RESULT_FILTER, intent)
                finish()
            }
        }
    }

    private fun RemoveSelection() {
        arrSelecteddataid = ArrayList()
        for (i in listdata.indices) {
            val data = listdata.get(i).data
            for (j in data.indices) {
                data.get(j).isSelected = false
                /*runOnUiThread {
                    subfilter_list.notifyDataSetChanged()
                }*/
            }
        }

        setData(listdata)
    }
}
package com.ffi.wallet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ffi.HomeActivity
import com.ffi.R
import com.ffi.Utils.*
import com.ffi.api.ApiClient
import com.ffi.api.WebService
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import kotlinx.android.synthetic.main.fragment_wallet.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class WalletFragment : Fragment(), View.OnClickListener {

    var homeActivity: HomeActivity? = null
    lateinit var mprogressbar: ProgressBarHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_wallet, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mprogressbar = ProgressBarHandler(requireActivity())
        homeActivity = activity as HomeActivity

        init()
    }


    private fun init() {
        //homeActivity.header.visibility = View.VISIBLE
/*
        ivBack.visibility = View.VISIBLE
        ivEdit.visibility = View.GONE
*/

        setListener()
        CallApiGetWallet()
    }

    private fun CallApiGetWallet() {
        if (isInternetAvailable(requireActivity())) {
            mprogressbar.showProgressBar()
            val param = ParamWallet().apply {
                userId = getUserId()
            }

            val retrofit = WebService.getRetrofit(requireActivity()).create(ApiClient::class.java)
            retrofit.GetWallet(param)
                .enqueue(object : Callback<ResponseWallet> {
                    override fun onFailure(call: Call<ResponseWallet>, t: Throwable) {
                        mprogressbar.hideProgressBar()
                    }

                    override fun onResponse(
                        call: Call<ResponseWallet>,
                        response: Response<ResponseWallet>?
                    ) {
                        mprogressbar.hideProgressBar()
                        if (response != null) {
                            if (response.isSuccessful) {
                                val responsewallet = response.body()
                                if (responsewallet != null) {
                                    if (responsewallet.status == API_SUCESS) {
                                        if (isAdded) {
                                            tvWalletamnt.text = getString(R.string.currency) + " " + responsewallet.walletAmount
                                        }

                                    }
                                }
                            } else {
                                activity?.showToast(getString(R.string.msg_common_error),Const.ALERT)
                            }
                        }
                    }
                })
        } else {
            NetworkErrorDialog(requireActivity())
        }
    }

    private fun setListener() {
        ivBack.setOnClickListener(this)
        btn_add_wallet.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBack -> {
                fragmentManager?.popBackStack()
            }
            R.id.btn_add_wallet -> {
                
            }
        }
    }


}
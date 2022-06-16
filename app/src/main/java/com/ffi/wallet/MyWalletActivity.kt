package com.ffi.wallet

import android.app.Dialog
import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.ffi.R
import com.ffi.Utils.*
import com.ffi.payment.PaymentActivity
import com.ffi.api.ApiClient
import com.ffi.api.WebService
import kotlinx.android.synthetic.main.activity_my_wallet.*
import kotlinx.android.synthetic.main.fragment_my_account.*
import kotlinx.android.synthetic.main.fragment_wishlist.*
import kotlinx.android.synthetic.main.item_my_wallet_history.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MyWalletActivity : AppCompatActivity() {

    private var isPagination:Boolean=false
    private var mPage : Int = 1
     var dialog: Dialog? = null
    lateinit var mprogressbar: ProgressBarHandler
    var items: List<ResponseWalletHistory.TransactiondetailEntity> = ArrayList()
     var walletListAdapter: WalletListAdapter? = null
     var strWallet:String=""

    override fun onResume() {
        super.onResume()
        if(dialog!=null){
            CallApiGetWallet()
            mPage = 1
            callGetWalletHistoryAPI(mPage, true)
            if(dialog!!.isShowing){
                dialog!!.dismiss()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_wallet)
        initView()
    }

    private fun initView() {
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerviewWalletHistory.setLayoutManager(layoutManager)

        mprogressbar = ProgressBarHandler(this)
      /*  if(intent!=null && intent.hasExtra("strWallet")){
            strWallet = intent.getStringExtra("strWallet")!!
            if(!TextUtils.isEmpty(strWallet)){
                tvcredit.text = getString(R.string.currency) + " " + strWallet
            }
        }else{
            CallApiGetWallet()
        }*/
        CallApiGetWallet()

        swipeRefresh.setOnRefreshListener {
            mPage = 1 ;
            callGetWalletHistoryAPI(mPage, true)
            CallApiGetWallet()
        }
        ivBack.setOnClickListener { onBackPressed() }
        ivAddCart1.setOnClickListener {  DialogWallet() }
        tvClickHere.setPaintFlags(tvClickHere.getPaintFlags() or Paint.UNDERLINE_TEXT_FLAG)
        tvClickHere.setOnClickListener { DialogWallet() }
        tvAddCredit1.setOnClickListener { DialogWallet() }

        callGetWalletHistoryAPI(mPage, true)
    }
    private fun CallApiGetWallet() {
        if (isInternetAvailable(this)) {
            mprogressbar.showProgressBar()
            val param = ParamWallet().apply {
                userId = getUserId()
            }

            val retrofit = WebService.getRetrofit(this).create(ApiClient::class.java)
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

                                            strWallet = responsewallet.walletAmount.toString()

                                            if (!TextUtils.isEmpty(strWallet)) {
                                                tvcredit.text = getString(R.string.currency) + " " + strWallet
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

    fun DialogWallet() {
        dialog = Dialog(this)
        val window = dialog!!.window
        window!!.setLayout(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        )
        window.setGravity(Gravity.CENTER)
        dialog!!.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog!!.setContentView(R.layout.custom_add_amount)

        val edtamnt = dialog!!.findViewById<EditText>(R.id.edtamnt)
        val cad_btnOk = dialog!!.findViewById<Button>(R.id.cad_btnOk)
        val tvcreedit = dialog!!.findViewById<TextView>(R.id.tvcreedit)
        val rd1 = dialog!!.findViewById<RadioButton>(R.id.rd1)
        val rd2 = dialog!!.findViewById<RadioButton>(R.id.rd2)
        val rd3 = dialog!!.findViewById<RadioButton>(R.id.rd3)

        tvcreedit.text = getString(R.string.currency) + " " + strWallet

        rd1.setOnCheckedChangeListener { compoundButton: CompoundButton, b: Boolean ->
            if (b) {
                edtamnt.setText("5000")
            }
        }

        rd2.setOnCheckedChangeListener { compoundButton: CompoundButton, b: Boolean ->
            if (b) {
                edtamnt.setText("10000")
            }
        }

        rd3.setOnCheckedChangeListener { compoundButton: CompoundButton, b: Boolean ->
            if (b) {
                edtamnt.setText("20000")
            }
        }


        edtamnt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                Log.e("length", s?.length.toString())
                if (s?.toString()?.trim()?.length == 1) {
                    rd1.isChecked = false
                    rd2.isChecked = false
                    rd3.isChecked = false
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

        })



        cad_btnOk.setOnClickListener {
            val stramnt = edtamnt.text.toString()
            if (stramnt.length > 0) {
                CallApiAddWAllet(stramnt)
            } else {
               showToast(getString(R.string.err_add_amount), Const.ALERT)
            }

        }

        dialog!!.setCancelable(true)
        dialog!!.setCanceledOnTouchOutside(true)
        dialog!!.show()
    }

    fun CallApiAddWAllet(stramnt: String) {
        if (isInternetAvailable(this)) {
            mprogressbar.showProgressBar()
            val param = ParamAddWallet().apply {
                userId = getUserId()
                amount = stramnt
            }

            val retrofit = WebService.getRetrofit(this).create(ApiClient::class.java)
            retrofit.AddWallet(param)
                    .enqueue(object : Callback<ResponseAddWallet> {
                        override fun onFailure(call: Call<ResponseAddWallet>, t: Throwable) {
                            mprogressbar.hideProgressBar()
                        }

                        override fun onResponse(
                                call: Call<ResponseAddWallet>,
                                response: Response<ResponseAddWallet>?
                        ) {
                            mprogressbar.hideProgressBar()
                            if (response != null) {
                                if (response.isSuccessful) {
                                    val responsewallet = response.body()
                                    if (responsewallet != null) {
                                        if (responsewallet.status == API_SUCESS) {

                                            responsewallet.paymentProcessUrl?.let {
                                                storePaymentUrl(
                                                        it
                                                )
                                            }
                                            responsewallet.paymentSuccessUrl?.let {
                                                storeSuccessUrl(
                                                        it
                                                )
                                            }
                                            responsewallet.paymentCancelUrl?.let { storeCancelUrl(it) }
                                            val intent =
                                                    Intent(
                                                            this@MyWalletActivity,
                                                            PaymentActivity::class.java
                                                    )
                                            intent.putExtra("credit", true)
                                            startActivity(intent)
                                            /* tvWallet.text = "Wallet Balance - " +
                                                         getString(R.string.currency) + " " + responsewallet.walletAmount*/

                                        }
                                    }
                                } else {
                                    showToast(
                                            getString(R.string.msg_common_error),
                                            Const.ALERT
                                    )
                                }
                            }
                        }
                    })
        } else {
            NetworkErrorDialog(this)
        }
    }
    private fun callGetWalletHistoryAPI(imPage: Int, isprogress: Boolean) {
        if (isInternetAvailable(this)) {
            if (isprogress)
                mprogressbar.showProgressBar()

            val param = ParamWalletHistory().apply {
                limit = Const.PAGINATION_LIMIT
                page = mPage.toString()
                userId = getUserId()
            }
            val retrofit = WebService.getRetrofit(this).create(ApiClient::class.java)
            retrofit.GetWalletHistory(param).enqueue(object : Callback<ResponseWalletHistory> {
                override fun onFailure(call: Call<ResponseWalletHistory>, t: Throwable) {
                    mprogressbar.hideProgressBar()
                    swipeRefresh?.isRefreshing = false
                }

                override fun onResponse(
                        call: Call<ResponseWalletHistory>,
                        response: Response<ResponseWalletHistory>?
                ) {
                    mprogressbar.hideProgressBar()
                    swipeRefresh?.isRefreshing = false

                    if (response != null) {
                        if (response.isSuccessful) {
                            val responsewish = response.body()
                            if (responsewish != null) {
                                if (responsewish.status == API_SUCESS) {

                                    isPagination = responsewish.TotalPage > mPage

                                    if (responsewish.transactiondetail?.size!! > 0 && responsewish.TotalPage > 0) {
                                        tvNoData?.visibility = View.GONE
                                        recyclerviewWalletHistory?.visibility = View.VISIBLE
                                        swipeRefresh.visibility = View.VISIBLE
                                        items = responsewish.transactiondetail
                                        walletListAdapter = WalletListAdapter(this@MyWalletActivity, items);
                                        recyclerviewWalletHistory.adapter = walletListAdapter
                                    } else {
                                        swipeRefresh.visibility = View.GONE
                                        tvNoData?.visibility = View.VISIBLE
                                        recyclerviewWalletHistory?.visibility = View.GONE
                                    }
                                } else if (responsewish.status == API_DATA_NOT_AVAILBLE) {
                                    tvNoData?.visibility = View.VISIBLE
                                    swipeRefresh.visibility = View.GONE
                                    recyclerviewWalletHistory?.visibility = View.GONE
                                }
                            }
                        } else {
                            showToast(getString(R.string.msg_common_error), Const.ALERT)
                        }
                    }
                }
            })
        }
        else {
            NetworkErrorDialog(this)
            swipeRefresh?.isRefreshing = false
        }

    }


}
package com.ffi.membership

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DimenRes
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.ffi.HomeActivity
import com.ffi.R
import com.ffi.Utils.*
import com.ffi.payment.PaymentActivity
import com.ffi.api.ApiClient
import com.ffi.api.WebService
import kotlinx.android.synthetic.main.custom_toolbar.*
import kotlinx.android.synthetic.main.fragment_membership.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MembershipFragment : Fragment(), View.OnClickListener {

    lateinit var mprogressbar: ProgressBarHandler
    var homeActivity: HomeActivity? = null

    override fun onResume() {
        super.onResume()
        if (!getUserId().isEmpty()) {
            CallApiMemberShip()
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_membership, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mprogressbar = ProgressBarHandler(requireActivity())
        homeActivity = activity as HomeActivity

        init()
        setViewPagerItemDecoratoin()

        if (!getUserId().isEmpty()) {
            CallApiMemberShip()
        }
    }

    private fun init() {
        //homeActivity.header.visibility = View.VISIBLE
        ivBack.visibility = View.VISIBLE
        ivEdit.visibility = View.GONE
        txt_toolbar.text = getString(R.string.title_mebership_plan)

        setListener()
    }

    private fun setListener() {
        ivBack.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBack -> {
                homeActivity?.selectLastMenu()
//                homeActivity?.replaceFragment(MyAccountFragment(), "4")
                fragmentManager?.popBackStack()
            }
        }
    }

    private fun CallApiMemberShip() {
        if (isInternetAvailable(requireActivity())) {
            mprogressbar.showProgressBar()

            val param = ParamMemberShip().apply {
                userId = getUserId().toInt()
            }

            val retrofit = WebService.getRetrofit(requireActivity()).create(ApiClient::class.java)
            retrofit.GetMemberShipPlan(param)
                .enqueue(object : Callback<ResponseMeberShip> {
                    override fun onFailure(call: Call<ResponseMeberShip>, t: Throwable) {
                        mprogressbar.hideProgressBar()
                    }

                    override fun onResponse(
                            call: Call<ResponseMeberShip>,
                            response: Response<ResponseMeberShip>?
                    ) {
                        mprogressbar.hideProgressBar()
                        if (response != null) {
                            if (response.isSuccessful) {
                                val responsemember = response.body()
                                if (responsemember != null) {
                                    if (responsemember.status == API_SUCESS) {
                                        if (isAdded) {
                                            setData(responsemember.data?.membershipList,responsemember.data?.ActiveMembershipId!!)
                                        }

                                    } else if (responsemember.status == API_DATA_NOT_AVAILBLE) {

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

    private fun setData(membershipList: List<Membership>?, activeMembershipId: String) {

        val adapter = membershipList?.let { PackagesAdapter(it, this,activeMembershipId) }
        viewPager.setAdapter(adapter)
        viewPager.offscreenPageLimit = 1

      //  val nextItemVisiblePx = resources.getDimension(R.dimen.viewpager_next_item_visible)
        val nextItemVisiblePx = resources.getDimension(R.dimen.viewpager_current_item_horizontal_margin)
        val currentItemHorizontalMarginPx = resources.getDimension(R.dimen.viewpager_current_item_horizontal_margin)

        val pageTranslationX = nextItemVisiblePx + currentItemHorizontalMarginPx
        val pageTransformer = ViewPager2.PageTransformer { page: View, position: Float ->
            page.translationX = -pageTranslationX * position
            page.scaleY = 1 - (0.10f * Math.abs(position))
            page.alpha = 0.25f + (1 - Math.abs(position))
        }
        viewPager.setPageTransformer(pageTransformer)
    }

    private fun setViewPagerItemDecoratoin() {
        val itemDecoration = HorizontalMarginItemDecoration(
                requireActivity(),
                R.dimen.viewpager_current_item_horizontal_margin
        )
        viewPager.addItemDecoration(itemDecoration)

    }


    class HorizontalMarginItemDecoration(context: Context, @DimenRes horizontalMarginInDp: Int) :
        RecyclerView.ItemDecoration() {

        private val horizontalMarginInPx: Int =
            context.resources.getDimension(horizontalMarginInDp).toInt()

        override fun getItemOffsets(
                outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
        ) {
            outRect.right = horizontalMarginInPx
            outRect.left = horizontalMarginInPx
//            view.alpha =  0.5f
        }
    }


    fun CallApiAddWallet(iD: String?) {
        if (isInternetAvailable(requireActivity())) {
            mprogressbar.showProgressBar()

            val param = ParamAddMeberShip().apply {
                userId = getUserId().toInt()
                membershipId = iD?.toInt()
            }

            val retrofit = WebService.getRetrofit(requireActivity()).create(ApiClient::class.java)
            retrofit.AddMemberShip(param)
                .enqueue(object : Callback<ResponseAddMebership> {
                    override fun onFailure(call: Call<ResponseAddMebership>, t: Throwable) {
                        mprogressbar.hideProgressBar()
                    }

                    override fun onResponse(
                            call: Call<ResponseAddMebership>,
                            response: Response<ResponseAddMebership>?
                    ) {
                        mprogressbar.hideProgressBar()
                        if (response != null) {
                            if (response.isSuccessful) {
                                val responsemember = response.body()
                                if (responsemember != null) {
                                    if (responsemember.status == API_SUCESS) {
                                        if (isAdded) {
                                            responsemember.paymentProcessUrl?.let {
                                                storePaymentUrl(
                                                        it
                                                )
                                            }
                                            responsemember.paymentSuccessUrl?.let {
                                                storeSuccessUrl(
                                                        it
                                                )
                                            }
                                            responsemember.paymentCancelUrl?.let { storeCancelUrl(it) }
                                            val intent =
                                                    Intent(
                                                            requireActivity(),
                                                            PaymentActivity::class.java
                                                    )
                                            intent.putExtra("credit", true)
                                            startActivity(intent)
                                        }

                                    } else if (responsemember.status == API_DATA_NOT_AVAILBLE) {

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
}
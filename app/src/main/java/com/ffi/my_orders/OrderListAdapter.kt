package com.ffi.my_orders

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ffi.R
import com.ffi.Utils.Const
import com.ffi.Utils.getFormattedDate
import com.ffi.wishlist.OrderHolder

class OrderListAdapter(val activity: MyOrderFragment, val data: List<Record>) :
    RecyclerView.Adapter<OrderHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.adapter_my_order, parent, false)
        return OrderHolder(view)
    }

    override fun getItemCount() = data.size


    override fun onBindViewHolder(holder: OrderHolder, position: Int) {
        val model = data[position]
        holder.tvcust_name.text = model.CustomerName
        holder.tvOrderId.text = model.orderId
        holder.ResellerName.text = model.ResellerName

       // holder.tvAmnt.text = activity.resources.getString(R.string.currency) + " " + model.grandTotal
        if(!TextUtils.isEmpty(model.grandTotal)){
            holder.tvAmnt.text = activity.resources.getString(R.string.currency) + " " + model.grandTotal
        }
        else if(!TextUtils.isEmpty(model.TotalAmount)){
            holder.tvAmnt.text = activity.resources.getString(R.string.currency) + " " + model.TotalAmount
        }

        holder.tvDelivery_status.text = model.orderStatusName
        holder.tvPlaceOn.text = activity.requireActivity()
            .getFormattedDate(
                model.dateTime.toString(),
                Const.INPUT_DATE_FORMATE,
                Const.OUTPUT_DATE_FORAMTE
            )
        holder.li_order.setOnClickListener {
            activity.loadOrderDetails(model.orderId.toString())
        }

        holder.tvView.setOnClickListener {
            activity.loadOrderDetails(model.orderId.toString())
        }
    }


    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
    }
}
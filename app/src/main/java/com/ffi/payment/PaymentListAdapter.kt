package com.ffi.payment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ffi.R
import com.ffi.add_address.AddAddressActivity
import com.ffi.addreslist.AddressListActivity
import kotlinx.android.synthetic.main.adapter_payment_type.view.*


class PaymentListAdapter(
    val activity: AddAddressActivity,
    val varientListClickInterface: PaymentListClickInterface
) :
    RecyclerView.Adapter<PaymentListAdapter.ViewHolder>() {

    private var models = ArrayList<DataX>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(activity)
            .inflate(R.layout.adapter_payment_type, parent, false)
        return ViewHolder(view)
    }


    fun addModels(modellist: ArrayList<DataX>) {
        this.models.addAll(modellist)
        this.notifyDataSetChanged()
    }


    override fun getItemCount(): Int {

        val size = models.size

        return size
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = models[position]
        /*  if (model.isChecked) {
              holder.rdbtn.isChecked = true
          } else {
              holder.rdbtn.isChecked = false
          }
  */
        holder.rdbtn.text = model.displayName

        holder.rdbtn.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                varientListClickInterface.vaarientClickedPosItem(position)
            }
        }


    }

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val rdbtn = view.rdbtn
    }

}
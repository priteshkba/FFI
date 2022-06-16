package com.ffi.addreslist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ffi.GlideApp
import com.ffi.R
import com.ffi.Utils.storeDefultAddressId

class AddresListAdapter(
    val activity: AddressListActivity,
    val responseAddress: ArrayList<Data>
) :
    RecyclerView.Adapter<AddressListHolder>() {

    //  var lastSelectedPotion = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressListHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.adapter_addresslist, parent, false)

        return AddressListHolder(view)
    }


    override fun getItemCount() = responseAddress.size


    override fun onBindViewHolder(holder: AddressListHolder, position: Int) {
        val model = responseAddress[position]
        /*if (responseAddress.size == 1) {
            model.isSelected = "1"
        }*/

        if (model.isSelected.equals("1")) {
            activity.setAddressId(model.iD)
            storeDefultAddressId(model.iD)
            GlideApp.with(activity)
                .load(R.drawable.ic_address_selected)
                .into(holder.ivSelected)
        } else {
            GlideApp.with(activity)
                .load(R.drawable.ic_address_unselected)
                .into(holder.ivSelected)
        }

        holder.tvaddressNo.text =
            activity.resources.getString(R.string.lbl_address) + (position + 1).toString()

        var strAddress = ""
        if (model.CompleteAddress == null) {

            strAddress =
                model.CustomerName + "\n" + model.Building + ", " + model.Street + ", " +
                        model.Landmark + ", " +
                        model.city + ",\n" + model.stateName + " " + model.pincode
        } else {
            strAddress = model.CustomerName + "\n" + model.CompleteAddress
        }

        holder.tvAddress.text = strAddress

        holder.li_address.setOnClickListener {
            if (model.isSelected.equals("1")) {
                notifyItemChanged(position)
                activity.setAddressId("")
                model.isSelected = "0"
                return@setOnClickListener
            }

            for (i in responseAddress.indices) {
                if (i == position) {
                    model.isSelected = "1"
                } else {
                    responseAddress.get(i).isSelected = "0"
                }
                notifyDataSetChanged()
            }
            activity.setAddressId(model.iD)
        }

        holder.ivDelete.setOnClickListener {
            activity.DeleteAddress(model.iD, position)
        }


    }

    fun move(position: Int) {
        responseAddress.removeAt(position)
        notifyItemRemoved(position)
        notifyDataSetChanged()
    }

    fun DeselectAll() {
        for (i in responseAddress.indices) {
            // if (responseAddress.get(i).isSelected.equals("1")) {
            responseAddress.get(i).isSelected = "0"
            notifyItemChanged(i)
            //}
            //break
        }
        notifyDataSetChanged()
    }


    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
    }
}
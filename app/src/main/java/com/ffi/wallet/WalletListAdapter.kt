package com.ffi.wallet

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ffi.R
import kotlinx.android.synthetic.main.item_my_wallet_history.view.*
import java.text.SimpleDateFormat
import java.util.*

class WalletListAdapter(var myWalletActivity: MyWalletActivity, var items: List<ResponseWalletHistory.TransactiondetailEntity>) : RecyclerView.Adapter<WalletListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_my_wallet_history, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {


        val timeStamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).parse(items.get(position).transactiondate.toString())
        val strDateD = SimpleDateFormat("d", Locale.US).parse(items.get(position).transactiondate.toString()).toString()
        var format:String =""
        if(strDateD.equals("1"))
            format = "EE d'st' MMM, yyyy HH:mm:ss"
        else if(strDateD.equals("2") || strDateD.equals("22"))
            format = "EE d'nd' MMM, yyyy HH:mm:ss"
        else if(strDateD.equals("3") || strDateD.equals("23"))
            format = "EE d'rd' MMM, yyyy HH:mm:ss"
        else
            format = "EE d'th' MMM, yyyy HH:mm:ss"

        val mStrDate = SimpleDateFormat(format, Locale.US).format(timeStamp)

        holder.tvDate.text = mStrDate

        if(TextUtils.isEmpty(items.get(position).description.toString())){
            holder.tvNote.text = myWalletActivity.resources.getString(R.string.notes_empyt)
        }else{
            holder.tvNote.text = myWalletActivity.resources.getString(R.string.notes)+" "+items.get(position).description.toString()
        }
        if(TextUtils.isEmpty(items.get(position).TransactionId.toString())){
            holder.tvTransactionId.visibility = View.GONE
        }
        holder.tvTransactionId.text = myWalletActivity.resources.getString(R.string.transactionId)+items.get(position).TransactionId.toString()
        if(items.get(position).CreditDebit.equals(myWalletActivity.getString(R.string.str_debit))){
            holder.tvCreditAmount.text =items.get(position).amount.toString()+" "+myWalletActivity.resources.getString(R.string.transaction_debit)
            holder.tvCreditAmount.setTextColor(myWalletActivity.resources.getColor(R.color.color_red))
        }else{
            holder.tvCreditAmount.text =items.get(position).amount.toString()+" "+ myWalletActivity.resources.getString(R.string.transaction_credit)
            holder.tvCreditAmount.setTextColor(myWalletActivity.resources.getColor(R.color.color_green))
        }

    }
    override fun getItemCount(): Int {
        return items.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val  tvDate = itemView.tvDate
        val  tvNote = itemView.tvNote
        val  tvTransactionId = itemView.tvTransactionId
        val  tvCreditAmount = itemView.tvCreditAmount

    }
}
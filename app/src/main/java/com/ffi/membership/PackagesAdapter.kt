package com.ffi.membership

import android.content.DialogInterface
import android.graphics.Paint
import android.text.Html
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ffi.App
import com.ffi.R
import java.text.SimpleDateFormat
import java.util.*


class PackagesAdapter(
        private val models: List<Membership>,
        private val context: MembershipFragment,
        private  val activeMembershipId: String
) : RecyclerView.Adapter<PackagesAdapter.ViewHolder?>() {
    private val layoutInflater: LayoutInflater? = null

    override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_membership_final, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = models.get(position)
        Log.e("////////model", models.toString())
        var mStrDate=""
        if(!TextUtils.isEmpty(model.MembershipEndDate.toString())){
            val timeStamp = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.US).parse(model.MembershipEndDate.toString())
             mStrDate = SimpleDateFormat("dd/MM/yyyy", Locale.US).format(timeStamp)
        }


        var count = 0
        for (i in models.indices) {
            if (models.get(i).isActive.equals("1")) {
                ++count
                break
            }
        }

        if (count == 0) {
            for (i in models.indices) {
                if (model.membershipCost.equals("0")) {
                    models.get(i).isActive = "1"
                    break
                }
            }
        }
        if(model.MembershipEndDate.equals("")){
            holder.tvExpDate.visibility=View.GONE
        }else{
            holder.tvExpDate.visibility=View.VISIBLE
        }

        if (model.IsWholeseller.equals("1") ) {

                holder.tvbtn.visibility = View.GONE
                holder.cardViewButton.visibility = View.GONE
                holder.ivActive.visibility = View.GONE

                if(TextUtils.isEmpty(activeMembershipId) && model.CanPurchase.equals("0")){
                    holder.ivActive.visibility = View.VISIBLE
                    Glide.with(context).load(context.resources.getDrawable(R.drawable.ic_active_new)).into(holder.ivActive)
                }

            }
        else {

            if(TextUtils.isEmpty(activeMembershipId))
            {
                   holder.cardViewButton.visibility = View.VISIBLE
                   holder.tvbtn.visibility = View.VISIBLE
            }
            else{
                   if(model.iD.equals(activeMembershipId)){
                       holder.tvbtn.visibility = View.VISIBLE
                       holder.cardViewButton.visibility = View.VISIBLE
                   }else{
                       holder.tvbtn.visibility = View.GONE
                       holder.cardViewButton.visibility = View.GONE

                   }
               }

                if (model.isActive.equals("1")) {

                    Glide.with(context).load(context.resources.getDrawable(R.drawable.ic_active_new)).into(holder.ivActive)
                    holder.tvExpDate.setText("(Will expire on " + mStrDate + ")")

                    if(model.TotalUserId!!.toInt() % 2 == 0 ){
                        holder.tvbtn.text = context.getString(R.string.lbl_renew_already)
                    } else{
                        holder.tvbtn.text = context.getString(R.string.lbl_extend)
                    }

                }else{
                    holder.tvExpDate.setText("(Expired on " + mStrDate + ")")

                    if(model.isRenew.equals("1")){
                        Glide.with(context).load(context.resources.getDrawable(R.drawable.ic_expire_membership)).into(holder.ivActive)
                        holder.tvbtn.text = context.getString(R.string.lbl_renew_now)
                    }else{
                        holder.ivActive.visibility = View.INVISIBLE
                        if (model.CanPurchase.equals("1")) {
                            holder.tvbtn.text = context.getString(R.string.lbl_buy_now)
                        } else {
                            holder.tvbtn.text = context.getString(R.string.btn_not_availble)
                            holder.tvbtn.isEnabled = false;
                        }
                    }

                }
            }



        holder.tvtitle.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG)
        holder.tvtitle.setText(model.membershipName)
        holder.tvdesc.setText(Html.fromHtml(model.membershipDescription))
        holder.tvPricePerMonth.text = context.getString(R.string.currency) + " " + model.membershipCost

        val content = SpannableString(context.getString(R.string.currency) + " " + model.FirstTimeMembershipCost)
        content.setSpan(UnderlineSpan(), 0, content.length, 0)
        holder.tvPrice.setText(content)

        val contentRenewableCost = SpannableString(context.getString(R.string.currency) + " " + model.RenewableCost)
        contentRenewableCost.setSpan(UnderlineSpan(), 0, contentRenewableCost.length, 0)
        holder.tvRenewalPrice.setText(contentRenewableCost)

        holder.tvbtn.setOnClickListener {
            if(holder.tvbtn.text.equals(context.getString(R.string.lbl_renew_already))){

                val dialog = android.app.AlertDialog.Builder(holder.tvRenewalPrice.context)
                    .setTitle(context.getString(R.string.app_name))
                    .setMessage(context.getString(R.string.str_already_renewd))
                    .setNegativeButton("OK", DialogInterface.OnClickListener { dialog, which ->
                        dialog.cancel()
                        App.isDialogOpen = false
                    })
                    .setCancelable(false)

                dialog.show()
            } else{
                if (model.CanPurchase.equals("1")) {
                    context.CallApiAddWallet(model.iD)
                }
            }
          /*  if (model.CanPurchase.equals("1") && !model.isActive.equals("1")) {
                context.CallApiAddWallet(model.iD)
            }*/


        }
        /*holder.li_package.setOnClickListener {
            context.loadPackageDetails(model.iD)
        }*/
    }

    override fun getItemCount(): Int {
        return models.size
    }

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val tvtitle = view.findViewById<TextView>(R.id.lbl_title)
        val tvdesc = view.findViewById<TextView>(R.id.tvdesc)
        val tvPrice = view.findViewById<TextView>(R.id.tvPrice)
        val tvPricePerMonth = view.findViewById<TextView>(R.id.tvPricePerMonth)
        val ivActive = view.findViewById<ImageView>(R.id.ivActive)
        val tvbtn = view.findViewById<TextView>(R.id.btn_buy)
        val tvExpDate = view.findViewById<TextView>(R.id.tvExpDate)
        val tvRenewalPrice = view.findViewById<TextView>(R.id.tvRenewalPrice)
        val cardViewButton = view.findViewById<CardView>(R.id.cardViewButton)


    }


}


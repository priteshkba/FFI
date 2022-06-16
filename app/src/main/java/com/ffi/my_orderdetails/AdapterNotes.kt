package com.ffi.my_orderdetails

import android.app.Dialog
import android.content.Context
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.content.ClipData
import android.content.ClipboardManager
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ffi.R
import android.widget.Toast
import com.ffi.Utils.Const
import com.ffi.Utils.getFormattedDate
import kotlin.collections.ArrayList
import com.ffi.linkClickableTextView.TextViewClickable

class AdapterNotes(orderDetailFragment: OrderDetailFragment, orderNoteList: ArrayList<Note>) :
    RecyclerView.Adapter<AdapterNotes.ViewHolder>() {
    val copyLable="Copied!"

    private val orderNoteList: ArrayList<Note>
    var context: Context?
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val contactView = inflater.inflate(R.layout.adapter_item_notes, parent, false)
        return ViewHolder(contactView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (TextUtils.isEmpty(orderNoteList[position].note) || (orderNoteList[position].note == "\"\"")) {
            holder.textViewNote.text = " - "
        } else {
            holder.textViewNote.text = orderNoteList.get(position).note
        }




        holder.textViewNote.setOnLongClickListener {
            copyText(holder.textViewNote.context, holder.textViewNote.text.toString())
            return@setOnLongClickListener true
        }


        holder.textViewAddedBy.text = orderNoteList.get(position).createdBy
        if (!TextUtils.isEmpty(orderNoteList.get(position).createdDateTime)) {
            holder.textViewDate.text = context!!.getFormattedDate(
                orderNoteList.get(position).createdDateTime,
                Const.INPUT_NOTES_DATE_FORMATE,
                Const.OUTPUT_DATE_FORAMTE
            )
        }
        holder.textViewDate.text = orderNoteList.get(position).createdDateTime
        if (orderNoteList.get(position).image != "") {
            Glide.with((context)!!)
                .load(orderNoteList[position].image)
                .placeholder(R.color.color_light_grey)
                .into(holder.imageViewNote)
        } else {
            holder.imageViewNote.visibility = View.GONE
        }
        holder.imageViewNote.setOnClickListener(View.OnClickListener {
            val dialog = Dialog((context)!!)
            dialog.setContentView(R.layout.dialog_full_imageview)
            dialog.show()
            dialog.window!!.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            val imageView = dialog.findViewById<ImageView>(R.id.ivImage)
            val imageViewClose = dialog.findViewById<ImageView>(R.id.ivclose)
            Glide.with((context)!!)
                .load(orderNoteList[position].image.trim { it <= ' ' })
                .placeholder(R.color.color_light_grey)
                .into(imageView)
            imageViewClose.setOnClickListener({ v1: View? -> dialog.dismiss() })
        })
    }

    private fun copyText(vcontext: Context, textToCopy: String) {
        val clipboard = vcontext.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(copyLable, textToCopy)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, copyLable, Toast.LENGTH_SHORT).show()

    }

    override fun getItemCount(): Int {
        return orderNoteList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textViewAddedBy: TextView
        var textViewNote: TextViewClickable
        var textViewDate: TextView
        var imageViewNote: ImageView
        var viewLine: View

        init {
            textViewAddedBy = itemView.findViewById(R.id.tvAddedBy)
            textViewNote = itemView.findViewById(R.id.tvNote)
            imageViewNote = itemView.findViewById(R.id.ivNote)
            viewLine = itemView.findViewById(R.id.viewLine)
            textViewDate = itemView.findViewById(R.id.tvDate)

            textViewNote.setMovementMethod(LinkMovementMethod.getInstance())
        }
    }

    init {
        context = orderDetailFragment.context
        this.orderNoteList = orderNoteList
    }
}
package com.aplicativo.powercontrol.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.aplicativo.powercontrol.R
import com.aplicativo.powercontrol.dto.ElectricityBillDto
import com.aplicativo.powercontrol.utils.DateFacilitator
import kotlinx.android.synthetic.main.item_document.view.*

class DocumentAdapter(
    private val documents: ArrayList<ElectricityBillDto>,
    var itemClickListener: OnDocumentListener
) :
    RecyclerView.Adapter<DocumentAdapter.ViewHolder>() {
    private var index = -1

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewHolder =
            LayoutInflater.from(parent.context).inflate(R.layout.item_document, parent, false)
        return ViewHolder(viewHolder)
    }

    override fun getItemCount(): Int {
        return documents.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val document = documents[position]
        val month = DateFacilitator.getMonthByNumber(document.monthNumber).name.substring(0,3)
        val card = holder.itemView.materialCard_item_docs
        holder.itemView.textView_document_month.text = month

        card.setOnClickListener {
            index = position
            itemClickListener.onClickDocument(document)
            notifyDataSetChanged()
        }
        card.isChecked = index == position


    }

    interface OnDocumentListener {
        fun onClickDocument(document: ElectricityBillDto)
    }

}
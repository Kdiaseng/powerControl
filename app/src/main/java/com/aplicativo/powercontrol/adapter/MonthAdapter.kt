package com.aplicativo.powercontrol.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.recyclerview.widget.RecyclerView
import com.aplicativo.powercontrol.R
import com.aplicativo.powercontrol.adapter.MonthAdapter.ViewHolder
import kotlinx.android.synthetic.main.item_month.view.*

class MonthAdapter(val months: List<String>, var itemClickListener: OnMonthListener) :
    RecyclerView.Adapter<ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        fun bindView(month: String, action: OnMonthListener){
            itemView.textView_month.text = month
            action.onClickMonth(month)
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewHolder = LayoutInflater.from(parent.context).inflate(R.layout.item_month, parent, false)
        return ViewHolder(viewHolder)
    }

    override fun getItemCount(): Int {
        return months.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val moth =  months[position]
        holder.bindView(moth, itemClickListener)
    }
    public interface OnMonthListener {
        fun onClickMonth(month: String)
    }

}


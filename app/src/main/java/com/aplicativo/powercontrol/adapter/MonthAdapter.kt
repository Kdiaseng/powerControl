package com.aplicativo.powercontrol.adapter

import android.view.View
import android.view.ViewGroup

import androidx.recyclerview.widget.RecyclerView
import com.aplicativo.powercontrol.adapter.MonthAdapter.ViewHolder
import kotlinx.android.synthetic.main.item_month.view.*

class MonthAdapter(val months: ArrayList<String>, var itemClickListener: OnMonthListener) :
    RecyclerView.Adapter<ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        fun bindView(month: String, action: OnMonthListener){
            itemView.textView_month.text = month
            action.onClickMonth(month)
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

    }

    override fun getItemCount(): Int {

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

    }

}

interface OnMonthListener {
    fun onClickMonth(month: String)
}
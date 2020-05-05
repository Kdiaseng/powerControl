package com.aplicativo.powercontrol.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getColor

import androidx.recyclerview.widget.RecyclerView
import com.aplicativo.powercontrol.R
import com.aplicativo.powercontrol.adapter.MonthAdapter.ViewHolder
import com.aplicativo.powercontrol.dto.MesDto
import kotlinx.android.synthetic.main.item_month.view.*

class MonthAdapter(private val months: List<MesDto>, var itemClickListener: OnMonthListener,
                   var monthSelected: Int
) :
    RecyclerView.Adapter<ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        fun bindView(month: MesDto, action: OnMonthListener, monthSelected: Int){
            itemView.button_month_item.text = month.name
            if (monthSelected == month.number){
                itemView.button_month_item.setBackgroundColor(getColor(itemView.context,R.color.colorGreenDark))
            }
            itemView.button_month_item.setOnClickListener {
                   action.onClickMonth(month)
            }

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
        holder.bindView(moth, itemClickListener, monthSelected)
    }
    interface OnMonthListener {
        fun onClickMonth(month: MesDto)
    }

}


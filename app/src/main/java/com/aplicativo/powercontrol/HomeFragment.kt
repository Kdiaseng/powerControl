package com.aplicativo.powercontrol

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.aplicativo.powercontrol.adapter.MonthAdapter
import kotlinx.android.synthetic.main.card_data.*
import kotlinx.android.synthetic.main.fragment_home.*

/**
 * A simple [Fragment] subclass.
 */
class HomeFragment : Fragment(), MonthAdapter.OnMonthListener {

    companion object{
        val YEARS = listOf( "2018", "2019","2020")
        val MONTH = listOf("JAN", "FER","MAR", "ABR","MAI","JUN")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, YEARS)
        autoCompleteTextView_year.setAdapter(adapter)

        autoCompleteTextView_year.setOnTouchListener { _, _ ->
            autoCompleteTextView_year.showDropDown()
            return@setOnTouchListener false
        }

        loadMonthInRecyclerView(recyclerView_months)

    }

    private fun loadMonthInRecyclerView(recycle: RecyclerView?){
        recycle!!.adapter = MonthAdapter(MONTH,this)
        val layoutManger = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL)
        recycle.layoutManager = layoutManger
    }

    override fun onClickMonth(month: String) {
      Toast.makeText(requireContext(),month, Toast.LENGTH_SHORT).show()
    }


}

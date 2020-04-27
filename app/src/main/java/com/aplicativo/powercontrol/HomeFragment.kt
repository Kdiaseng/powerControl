package com.aplicativo.powercontrol

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.aplicativo.powercontrol.adapter.MonthAdapter
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.utils.ColorTemplate
import kotlinx.android.synthetic.main.card_data.*
import kotlinx.android.synthetic.main.fragment_home.*
import java.util.ArrayList

/**
 * A simple [Fragment] subclass.
 */
class HomeFragment : Fragment(), MonthAdapter.OnMonthListener {

    var navController: NavController? = null

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, YEARS)
        autoCompleteTextView_year.setAdapter(adapter)

        if((activity as MainActivity).supportActionBar!!.isShowing){
            (activity as MainActivity).supportActionBar!!.hide()
        }

        autoCompleteTextView_year.setOnTouchListener { _, _ ->
            autoCompleteTextView_year.showDropDown()
            return@setOnTouchListener false
        }

        loadMonthInRecyclerView(recyclerView_months)
        ploteChart(barChart)
        floatingActionButtonAddOrUpdate.setOnClickListener {
            navController!!.navigate(R.id.action_homeFragment_to_registerCountPowerFragment)
        }

    }

    private fun ploteChart(barChart: BarChart?) {
       val entries = ArrayList<BarEntry>()
        entries.add(BarEntry(8f,0))
        entries.add(BarEntry(2f,1))
        entries.add(BarEntry(5f,2))
        entries.add(BarEntry(20f,3))
        entries.add(BarEntry(15f,4))
        entries.add(BarEntry(19f,5))
        entries.add(BarEntry(20f,6))
        entries.add(BarEntry(55f,7))
        entries.add(BarEntry(54f,8))
        entries.add(BarEntry(32f,9))
        entries.add(BarEntry(24f,10))
        entries.add(BarEntry(18f,11))



        val barDataSet = BarDataSet(entries, "Cells")

        val labels = arrayListOf("2016", "2015", "2014", "2013","2012", "2011", "2010","2009","2008","2007","2006","2005")
        val data = BarData(labels, barDataSet)
        barChart!!.data = data
        barChart.setDescription("Set Bar Chart Description Here")
        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS)
        barChart.animateY(5000)

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

package com.aplicativo.powercontrol

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.aplicativo.powercontrol.R.id.action_homeFragment_to_registerCountPowerFragment
import com.aplicativo.powercontrol.adapter.MonthAdapter
import com.aplicativo.powercontrol.database.AppDataBase
import com.aplicativo.powercontrol.domain.ElectricityBill
import com.aplicativo.powercontrol.dto.MesDto
import com.aplicativo.powercontrol.dto.YearAndMonthNumberArgsDto
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import kotlinx.android.synthetic.main.card_data.*
import kotlinx.android.synthetic.main.fragment_home.*
import java.util.*
import kotlin.collections.ArrayList

/**
 * A simple [Fragment] subclass.
 */
class HomeFragment : Fragment(), MonthAdapter.OnMonthListener {

    var navController: NavController? = null
    private var electricityBill : ElectricityBill? = null


    companion object {
        val YEARS = listOf("2018", "2019", "2020")
        val MONTH = listOf("JAN", "FER", "MAR", "ABR", "MAI", "JUN")
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

        showDataInScreen()

        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, YEARS)
        autoCompleteTextView_year.setAdapter(adapter)


        if ((activity as MainActivity).supportActionBar!!.isShowing) {
            (activity as MainActivity).supportActionBar!!.hide()
        }

        autoCompleteTextView_year.setOnTouchListener { _, _ ->
            autoCompleteTextView_year.showDropDown()
            return@setOnTouchListener false
        }

        loadMonthInRecyclerView(recyclerView_months)
        plotBarChart(barChart)
        plotLineChart(lineChart)
        floatingActionButtonAddOrUpdate.setOnClickListener {
            if (isSave()){
                val action = HomeFragmentDirections.actionHomeFragmentToRegisterCountPowerFragment()
                action.yearAndMonthNumber = YearAndMonthNumberArgsDto(MesDto(4, "ABR"), 2020)
                 Navigation.findNavController(it).navigate(action)
            }
        }

    }

    private fun showDataInScreen() {
       val obj =  AppDataBase(requireActivity()).electricityBillDao().getElectricityBillAllByMonthNumber(4,2020)

    }

    private fun plotLineChart(lineChart: LineChart?) {

        val entries = ArrayList<Entry>()
        entries.add(Entry(8f, 0))
        entries.add(Entry(2f, 1))
        entries.add(Entry(5f, 2))
        entries.add(Entry(20f, 3))
        entries.add(Entry(15f, 4))
        entries.add(Entry(19f, 5))
        entries.add(Entry(20f, 6))
        entries.add(Entry(55f, 7))
        entries.add(Entry(54f, 8))
        entries.add(Entry(32f, 9))
        entries.add(Entry(24f, 10))

        val labels = arrayListOf(
            "JAN",
            "FER",
            "MAR",
            "ABR",
            "MAI",
            "JUN",
            "JUL",
            "AGO",
            "SET",
            "OUT",
            "DEZ"
        )

        val lineDataSet = LineDataSet(entries, "merda")
        val lineData = LineData(labels, lineDataSet )
        lineChart!!.data = lineData
        lineChart.axisLeft.textColor = Color.WHITE
        lineChart.axisRight.textColor = Color.WHITE
        lineDataSet.color = Color.GREEN
        lineDataSet.valueTextColor = Color.WHITE
        lineDataSet.setCircleColor(Color.GREEN)
        lineChart.xAxis.textColor = Color.WHITE
    }


    private fun plotBarChart(barChart: BarChart?) {
        val entries = ArrayList<BarEntry>()
        entries.add(BarEntry(8f, 0))
        entries.add(BarEntry(2f, 1))
        entries.add(BarEntry(5f, 2))
        entries.add(BarEntry(20f, 3))
        entries.add(BarEntry(15f, 4))
        entries.add(BarEntry(19f, 5))
        entries.add(BarEntry(20f, 6))
        entries.add(BarEntry(55f, 7))
        entries.add(BarEntry(54f, 8))
        entries.add(BarEntry(32f, 9))
        entries.add(BarEntry(24f, 10))

        val barDataSet = BarDataSet(entries, "Cells")

        val labels = arrayListOf(
            "JAN",
            "FER",
            "MAR",
            "ABR",
            "MAI",
            "JUN",
            "JUL",
            "AGO",
            "SET",
            "OUT",
            "DEZ"
        )

        val data = BarData(labels, barDataSet)
        barChart!!.data = data
        barChart.setDescription("Set Bar Chart Description Here")
        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS)
        barChart.axisRight.textColor = Color.WHITE
        barChart.axisLeft.textColor = Color.WHITE
        barChart.xAxis.textColor = Color.WHITE
        barDataSet.valueTextColor = Color.WHITE
        barChart.animateY(5000)

    }

    private fun loadMonthInRecyclerView(recycle: RecyclerView?) {
        recycle!!.adapter = MonthAdapter(MONTH, this)
        val layoutManger = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL)
        recycle.layoutManager = layoutManger
    }

    override fun onClickMonth(month: String) {
        Toast.makeText(requireContext(), month, Toast.LENGTH_SHORT).show()
    }

    private fun isSave() = electricityBill == null

}






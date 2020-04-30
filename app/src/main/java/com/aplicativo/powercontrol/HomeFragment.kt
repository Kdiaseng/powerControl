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
import com.aplicativo.powercontrol.adapter.MonthAdapter
import com.aplicativo.powercontrol.database.AppDataBase
import com.aplicativo.powercontrol.domain.ElectricityBill
import com.aplicativo.powercontrol.dto.DateArgsDto
import com.aplicativo.powercontrol.dto.ElectricityBillDto
import com.aplicativo.powercontrol.dto.MesDto
import com.aplicativo.powercontrol.utils.DateFacilitator
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.utils.ColorTemplate
import kotlinx.android.synthetic.main.card_data.view.*
import kotlinx.android.synthetic.main.fragment_home.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * A simple [Fragment] subclass.
 */
class HomeFragment : Fragment(), MonthAdapter.OnMonthListener {

    private var navController: NavController? = null
    private var electricityBill : ElectricityBill? = null
    private var listMonth = ArrayList<MesDto>()
    private var listValues = ArrayList<ElectricityBillDto>()
    private var mesDto: MesDto? = null
    private var yearCurrent: Int = 0
    private var years = ArrayList<Int>()


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

        mesDto = getMesDtoCurrent()
        yearCurrent = getCurrentYear()

        loadYears()
        showDataInScreen(mesDto!!.number, yearCurrent)
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, years)
        autoCompleteTextView_year.setAdapter(adapter)

        loadLists()

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
                action.dateArgs = DateArgsDto(mesDto!!, 2020)
                 Navigation.findNavController(it).navigate(action)
            }
        }

    }

    private fun loadYears() {
        val listDtos =  AppDataBase(requireActivity()).electricityBillDao().getElectricityBillAll()
        val electricityBillYearMin = listDtos.minBy { it.year }
        if (listDtos.isNullOrEmpty()){
           years.add(yearCurrent)
        }else{
            for(ano in electricityBillYearMin!!.year..yearCurrent){
                years.add(ano)
            }
        }
    }

    private fun getCurrentYear(): Int {
        val calendar = Calendar.getInstance()
       return calendar[Calendar.YEAR]
    }

    private fun showDataInScreen(monthNumber: Int, year: Int) {
       val obj =  AppDataBase(requireActivity()).electricityBillDao().getElectricityBillAllByMonthNumber(monthNumber,year)
         if (obj != null){
             card_data_energy.textView_card_read_current.text = getString(R.string.kilowatt_hour, obj.currentReading)
             card_data_energy.textView_card_read_last.text = getString(R.string.kilowatt_hour,0)
             card_data_energy.textView_card_measured_consumption.text = getString(R.string.kilowatt_hour,obj.measuredConsumption)
             card_data_energy.textView_card_billed_consumption.text = getString(R.string.kilowatt_hour,obj.billedConsumption)
             card_data_energy.textView_card_rate.text = obj.rate.toString()
             card_data_energy.textView_card_street_lighting.text = getString(R.string.real,obj.streetLighting)
             textView_consumption_period_value.text = obj.initDate
             textView_consumption_period_value_end.text = obj.endDate
             texView_amount.text = obj.amount.toString()
         }

    }

    private fun plotLineChart(lineChart: LineChart?) {
        val entries = ArrayList<Entry>()
        val labels = listMonth.map { mesDto -> mesDto.name }
        loadListEntry(listValues, entries )

        val lineDataSet = LineDataSet(entries, "LineChart")
        val lineData = LineData(labels, lineDataSet )
        lineChart!!.data = lineData
        lineChart.axisLeft.textColor = Color.WHITE
        lineChart.axisRight.textColor = Color.WHITE
        lineDataSet.color = Color.GREEN
        lineDataSet.valueTextColor = Color.WHITE
        lineDataSet.setCircleColor(Color.GREEN)
        lineChart.xAxis.textColor = Color.WHITE
        lineChart.animateX(5000)
    }

    private fun loadLists(){
        listMonth = DateFacilitator.getMonthsListToCurrentMonth(getMesDtoCurrent().number)
        val list =  AppDataBase(requireActivity()).electricityBillDao().getElectricityBillDtoAll(2020).toTypedArray()
        listValues.addAll(list)
       // labels = listMonth.map { mesDto -> mesDto.name } as ArrayList
    }

    private fun plotBarChart(barChart: BarChart?) {
        val entries = ArrayList<BarEntry>()
        loadListBarEntry(listValues, entries )
        val barDataSet = BarDataSet(entries, "Cells")
        val labels = listMonth.map { mesDto -> mesDto.name }

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

    private fun loadListBarEntry(listValues: List<ElectricityBillDto>, entries: ArrayList<BarEntry>) {
       for ((index,value) in listMonth.withIndex()){
           val find = listValues.find { it.monthNumber == value.number }
           if (find != null){
               entries.add(BarEntry(find.amount.toFloat(), index))
           }else{
               entries.add(BarEntry(0f, index))
           }
       }
    }

    private fun loadListEntry(listValues: List<ElectricityBillDto>, entries: ArrayList<Entry>) {
        for ((index,value) in listMonth.withIndex()){
            val find = listValues.find { it.monthNumber == value.number }
            if (find != null){
                entries.add(Entry(find.amount.toFloat(), index))
            }else entries.add(Entry(0f, index))
        }
    }


    private fun loadMonthInRecyclerView(recycle: RecyclerView?) {
        recycle!!.adapter = MonthAdapter(listMonth, this)
        val layoutManger = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL)
        recycle.layoutManager = layoutManger
    }

    override fun onClickMonth(month: MesDto) {
        mesDto = month
        showDataInScreen(mesDto!!.number, 2020)
        Toast.makeText(requireContext(), month.name, Toast.LENGTH_SHORT).show()
    }

    private fun getMesDtoCurrent(): MesDto{
        val calendar = Calendar.getInstance()
        val df1 = SimpleDateFormat("MM", Locale("pt", "BR"))
        val monthNUmber = df1.format(calendar.time).toInt()
        return MesDto(monthNUmber, DateFacilitator.getMonthByNumber(monthNUmber).name)
    }

    private fun isSave() = electricityBill == null

}






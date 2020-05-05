package com.aplicativo.powercontrol

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
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
    private var electricityBill: ElectricityBill? = null
    private var listMonth = ArrayList<MesDto>()
    private var listValues = ArrayList<ElectricityBillDto>()
    private var mesDto: MesDto? = null
    private var yearSelect: Int = 0
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
        yearSelect = getCurrentYear()

        if (years.isEmpty())
            loadYears()

        showDataInScreen(mesDto!!.number, yearSelect)

        val adapter =
            ArrayAdapter(requireContext(), R.layout.spinner_item, years)
        spinner_years.adapter = adapter

        loadLists(mesDto!!.number)

        if ((activity as MainActivity).supportActionBar!!.isShowing) {
            (activity as MainActivity).supportActionBar!!.hide()
        }

        spinner_years.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            var count = 0
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (count >= 1) {
                    yearSelect = years[position]
                    if (yearSelect == getCurrentYear()){
                        mesDto = getMesDtoCurrent()
                        loadLists(mesDto!!.number)
                    } else {
                        loadLists()
                    }
                    showDataInScreen(mesDto!!.number, yearSelect)
                    loadMonthInRecyclerView(recyclerView_months, mesDto!!.number -1)
                    plotBarChart(barChart)
                    plotLineChart(lineChart)
                }
                count ++
            }
        }

        loadMonthInRecyclerView(recyclerView_months, mesDto!!.number - 1)
        plotBarChart(barChart)
        plotLineChart(lineChart)
        floatingActionButtonAddOrUpdate.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToRegisterCountPowerFragment()
             action.dateArgs = DateArgsDto(mesDto!!, yearSelect)
            if(!isSave())
             action.electricityBillArgs = electricityBill
            Navigation.findNavController(it).navigate(action)
        }

    }

    private fun loadYears() {
        val listDtos = AppDataBase(requireActivity()).electricityBillDao().getElectricityBillAll()
        val electricityBillYearMin = listDtos.minBy { it.year }
        if (listDtos.isNullOrEmpty()) {
            years.add(yearSelect)
        } else {
            for (ano in yearSelect downTo electricityBillYearMin!!.year) {
                years.add(ano)
            }
        }
    }

    private fun getCurrentYear(): Int {
        val calendar = Calendar.getInstance()
        return calendar[Calendar.YEAR]
    }

    private fun showDataInScreen(monthNumber: Int, year: Int) {
       electricityBill = AppDataBase(requireActivity()).electricityBillDao()
            .getElectricityBillAllByMonthNumber(monthNumber, year)

        if (electricityBill != null) {
            card_data_energy.textView_card_read_current.text =
                getString(R.string.kilowatt_hour, electricityBill!!.currentReading)
            card_data_energy.textView_card_read_last.text = getString(R.string.kilowatt_hour, 0)
            card_data_energy.textView_card_measured_consumption.text =
                getString(R.string.kilowatt_hour, electricityBill!!.measuredConsumption)
            card_data_energy.textView_card_billed_consumption.text =
                getString(R.string.kilowatt_hour, electricityBill!!.billedConsumption)
            card_data_energy.textView_card_rate.text = electricityBill!!.rate.toString()
            card_data_energy.textView_card_street_lighting.text =
                getString(R.string.real, electricityBill!!.streetLighting)
            textView_consumption_period_value.text = electricityBill!!.initDate
            textView_consumption_period_value_end.text = electricityBill!!.endDate
            texView_amount.text = electricityBill!!.amount.toString()
        } else {
            clearFields()
        }

    }

    private fun clearFields() {
        card_data_energy.textView_card_read_current.text = getString(R.string.kilowatt_hour, 0)
        card_data_energy.textView_card_read_last.text = getString(R.string.kilowatt_hour, 0)
        card_data_energy.textView_card_measured_consumption.text =
            getString(R.string.kilowatt_hour, 0)
        card_data_energy.textView_card_billed_consumption.text =
            getString(R.string.kilowatt_hour, 0)
        card_data_energy.textView_card_rate.text = "0"
        card_data_energy.textView_card_street_lighting.text = getString(R.string.real, 0.0)
        textView_consumption_period_value.text = getString(R.string.dateNull)
        textView_consumption_period_value_end.text = getString(R.string.dateNull)
        texView_amount.text = "0.0"
    }

    private fun plotLineChart(lineChart: LineChart?) {
        val entries = ArrayList<Entry>()
        val labels = listMonth.map { mesDto -> mesDto.name.substring(0,3) }
        loadListEntry(listValues, entries)
        val lineDataSet = LineDataSet(entries, "LineChart")
        val lineData = LineData(labels, lineDataSet)
        lineChart!!.data = lineData
        lineChart.axisLeft.textColor = Color.WHITE
        lineChart.axisRight.textColor = Color.WHITE
        lineDataSet.color = Color.GREEN
        lineDataSet.valueTextColor = Color.WHITE
        lineDataSet.setCircleColor(Color.GREEN)
        lineChart.xAxis.textColor = Color.WHITE
        lineChart.animateX(5000)
    }

    private fun loadLists(default: Int = 12) {
        listMonth = DateFacilitator.getMonthsListToCurrentMonth(default)
        val list =
            AppDataBase(requireActivity()).electricityBillDao().getElectricityBillDtoAll(yearSelect)
                .toTypedArray()
        if (listValues.isNotEmpty())
            listValues.clear()

        listValues.addAll(list)
    }

    private fun plotBarChart(barChart: BarChart?) {
        val entries = ArrayList<BarEntry>()
        loadListBarEntry(listValues, entries)
        val barDataSet = BarDataSet(entries, "Cells")
        val labels = listMonth.map { mesDto -> mesDto.name.substring(0,3) }

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

    private fun loadListBarEntry(
        listValues: List<ElectricityBillDto>,
        entries: ArrayList<BarEntry>
    ) {
        for ((index, value) in listMonth.withIndex()) {
            val find = listValues.find { it.monthNumber == value.number }
            if (find != null) {
                entries.add(BarEntry(find.amount.toFloat(), index))
            } else {
                entries.add(BarEntry(0f, index))
            }
        }
    }

    private fun loadListEntry(listValues: List<ElectricityBillDto>, entries: ArrayList<Entry>) {
        for ((index, value) in listMonth.withIndex()) {
            val find = listValues.find { it.monthNumber == value.number }
            if (find != null) {
                entries.add(Entry(find.amount.toFloat(), index))
            } else entries.add(Entry(0f, index))
        }
    }


    private fun loadMonthInRecyclerView(recycle: RecyclerView?, position: Int) {
        recycle!!.adapter = MonthAdapter(listMonth, this, mesDto!!.number)
        val layoutManger = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL)
        recycle.layoutManager = layoutManger
        recycle.scrollToPosition(position)
    }

    override fun onClickMonth(month: MesDto) {
        mesDto = month
        loadMonthInRecyclerView(recyclerView_months, mesDto!!.number - 1)
        showDataInScreen(mesDto!!.number, yearSelect)
        setIconFloatingButton()
        Toast.makeText(requireContext(), month.name, Toast.LENGTH_SHORT).show()
    }

    private fun setIconFloatingButton() {
        if (!isSave()){
            floatingActionButtonAddOrUpdate.setImageResource(android.R.drawable.ic_menu_edit)
        }else{
            floatingActionButtonAddOrUpdate.setImageResource(android.R.drawable.ic_input_add)
        }
    }

    private fun getMesDtoCurrent(): MesDto {
        val calendar = Calendar.getInstance()
        val df1 = SimpleDateFormat("MM", Locale("pt", "BR"))
        val monthNUmber = df1.format(calendar.time).toInt()
        return MesDto(monthNUmber, DateFacilitator.getMonthByNumber(monthNUmber).name)
    }

    private fun isSave() = electricityBill == null

}






package com.aplicativo.powercontrol

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.content.FileProvider
import androidx.core.view.get
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
import com.aplicativo.powercontrol.utils.FileFacilitator
import com.aplicativo.powercontrol.utils.MoneyTextWatcher
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.card_data.view.*
import kotlinx.android.synthetic.main.fragment_home.*
import java.io.File
import java.text.NumberFormat
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
    private val JANUARY = 1
    private val DECEMBER: Int = 12


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
        bottom_app_bar.replaceMenu(R.menu.bottomappbar_menu)

        bottom_app_bar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.app_bar_list_doc -> {
                    val action = HomeFragmentDirections.actionHomeFragmentToDocumentListFragment()
                    action.years = years.toIntArray()
                    action.yearSelected = yearSelect
                    Navigation.findNavController(bottom_app_bar).navigate(action)
                    return@setOnMenuItemClickListener true
                }
                R.id.app_bar_view_doc -> {
                    electricityBill?.pathDocument?.let { path ->
                        if (!FileFacilitator.openDocument(path))
                            showMessageSneakBar(getString(R.string.not_found))
                    }
                    return@setOnMenuItemClickListener true
                }
                else -> return@setOnMenuItemClickListener false
            }
        }
    }

    private fun showMessageSneakBar(message: String) {
        Snackbar.make(bottom_app_bar, message, Snackbar.LENGTH_SHORT)
            .show()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        FileFacilitator.init(requireContext())
        mesDto = getMesDtoCurrent()
        yearSelect = getCurrentYear()

        if (years.isEmpty())
            loadYears()

        showDataInScreen(mesDto!!.number, yearSelect)
        setIconFloatingButton()

        val adapter =
            ArrayAdapter(requireContext(), R.layout.spinner_item, years)
        spinner_years.adapter = adapter

        loadLists(mesDto!!.number)
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
                    showDataInScreen(mesDto!!.number, yearSelect)
                    setIconFloatingButton()
                    if (yearSelect == getCurrentYear()) {
                        mesDto = getMesDtoCurrent()
                        loadLists(mesDto!!.number)
                    } else {
                        loadLists()
                    }
                    loadMonthInRecyclerView(recyclerView_months, mesDto!!.number - 1)
                    plotBarChart(barChart)
                    plotLineChart(lineChart)
                }
                count++
            }
        }

        loadMonthInRecyclerView(recyclerView_months, mesDto!!.number - 1)
        plotBarChart(barChart)
        plotLineChart(lineChart)
        floatingActionButtonAddOrUpdate.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToRegisterCountPowerFragment()
            action.dateArgs = DateArgsDto(mesDto!!, yearSelect)
            if (!isSave())
                action.electricityBillArgs = electricityBill
            Navigation.findNavController(it).navigate(action)
        }

    }

    private fun setVisibleMenu(id: Int, enable: Boolean){
        val menu  = bottom_app_bar.menu
        val menuItem = menu.findItem(id)
        menuItem.isVisible =  enable
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

            electricityBill?.apply {
                card_data_energy.textView_card_read_current.text =
                    getString(R.string.kilowatt_hour, currentReading)
                card_data_energy.textView_card_measured_consumption.text =
                    getString(R.string.kilowatt_hour, measuredConsumption)
                card_data_energy.textView_card_billed_consumption.text =
                    getString(R.string.kilowatt_hour, billedConsumption)
                card_data_energy.textView_card_rate.text = rate.toString()
                card_data_energy.textView_card_street_lighting.text =
                   MoneyTextWatcher.formatterToReal(streetLighting)
                textView_consumption_period_value.text = initDate
                textView_consumption_period_value_end.text = endDate
            }

            texView_amount.text = MoneyTextWatcher.formatterToReal(electricityBill!!.amount)
            if (!validateCalculateAmount(electricityBill!!))
                imageView_icon_validation.setImageResource(R.drawable.erroicon)
            else
                imageView_icon_validation.setImageResource(R.drawable.checkyes)
        } else {
            clearFields()
        }
        card_data_energy.textView_card_read_last.text =
            getString(R.string.kilowatt_hour, getLastRead())

    }

    private fun validateCalculateAmount(electricityBill: ElectricityBill): Boolean {
        val consumption = electricityBill.billedConsumption
        val rate = electricityBill.rate
        val streetLighting = electricityBill.streetLighting
        val amountInput = electricityBill.amount
        val total = consumption * rate + streetLighting
        return total == amountInput
    }

    private fun getLastRead(): Int {
        var numberMonth = (mesDto!!.number - 1)
        var year = yearSelect
        if (mesDto!!.number == JANUARY) {
            numberMonth = DECEMBER
            year = yearSelect - 1
        }
        val last =
            AppDataBase(requireActivity()).electricityBillDao().getLastRead(numberMonth, year)
        last?.let {
            return it
        }
        return 0
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
        val labels = listMonth.map { mesDto -> mesDto.name.substring(0, 3) }
        loadListEntry(listValues, entries)

        val lineDataSet = LineDataSet(entries, "LineChart")
        lineDataSet.apply {
            color = Color.GREEN
            valueTextColor = Color.WHITE
            setCircleColor(Color.GREEN)
        }

        val lineData = LineData(labels, lineDataSet)

        lineChart?.apply {
            data = lineData
            axisLeft.textColor = Color.WHITE
            axisRight.textColor = Color.WHITE
            xAxis.textColor = Color.WHITE
            animateX(3000)
        }
    }

    private fun plotBarChart(barChart: BarChart?) {
        val entries = ArrayList<BarEntry>()
        loadListBarEntry(listValues, entries)

        val barDataSet = BarDataSet(entries, "Cells")
        barDataSet.apply {
            setColors(ColorTemplate.COLORFUL_COLORS)
            valueTextColor = Color.WHITE
        }

        val labels = listMonth.map { mesDto -> mesDto.name.substring(0, 3) }

        val dataBar = BarData(labels, barDataSet)

        barChart?.apply {
            data = dataBar
            setDescription("Conta até o mês atual")
            axisRight.textColor = Color.WHITE
            axisLeft.textColor = Color.WHITE
            xAxis.textColor = Color.WHITE
            animateY(3000)
        }
    }

    private fun loadLists(default: Int = DECEMBER) {
        if (listMonth.isNotEmpty()) listMonth.clear()
        listMonth = DateFacilitator.getMonthsListToCurrentMonth(default)
        val list =
            AppDataBase(requireActivity()).electricityBillDao().getElectricityBillDtoAll(yearSelect)
                .toTypedArray()
        if (listValues.isNotEmpty()) listValues.clear()
        val enableMenu = list.isNotEmpty()
        setVisibleMenu(R.id.app_bar_list_doc, enableMenu)
        listValues.addAll(list)
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
        recycle!!.adapter = MonthAdapter(listMonth, this, position)
        val layoutManger = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL)
        recycle.layoutManager = layoutManger
        recycle.scrollToPosition(position)
    }

    override fun onClickMonth(month: MesDto) {
        mesDto = month
        showDataInScreen(mesDto!!.number, yearSelect)
        setIconFloatingButton()
    }

    private fun setIconFloatingButton() {
        if (!isSave()) {
            setVisibleMenu(R.id.app_bar_view_doc, true)
            floatingActionButtonAddOrUpdate.setImageResource(android.R.drawable.ic_menu_edit)
        } else {
            floatingActionButtonAddOrUpdate.setImageResource(android.R.drawable.ic_input_add)
            setVisibleMenu(R.id.app_bar_view_doc, false)
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






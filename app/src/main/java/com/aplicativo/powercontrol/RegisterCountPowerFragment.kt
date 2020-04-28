package com.aplicativo.powercontrol

import android.app.Activity
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.aplicativo.powercontrol.database.AppDataBase
import com.aplicativo.powercontrol.domain.ElectricityBill
import com.aplicativo.powercontrol.dto.YearAndMonthNumberArgsDto
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.fragment_register_count_power.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class RegisterCountPowerFragment : Fragment() {

    private val calendar = Calendar.getInstance()
    private var yearAndMonthNumberArgsDto: YearAndMonthNumberArgsDto? = null
    private var electricityBill: ElectricityBill? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register_count_power, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        arguments?.let {
            yearAndMonthNumberArgsDto =
                RegisterCountPowerFragmentArgs.fromBundle(it).yearAndMonthNumber
        }

        buttonSaveOrUpdate.setOnClickListener {
            electricityBill = buildElectricityBillObject()
            AppDataBase(requireActivity()).electricityBillDao().addElectricityBill(electricityBill!!)
            Toast.makeText(
                activity,
                "CADASTRO FEITO COM SUCESSO!",
                Toast.LENGTH_SHORT
            ).show()
        }

        TxtLayout_date_init.setEndIconOnClickListener {
            DatePickerDialog(
                this.requireContext(),
                DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                    updateDate(year, month, dayOfMonth, textInput_date_init)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        TxtLayout_date_end.setEndIconOnClickListener {
            DatePickerDialog(
                this.requireContext(),
                DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                    updateDate(year, month, dayOfMonth, textInput_date_end)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun buildElectricityBillObject(): ElectricityBill? {
        return ElectricityBill(
            yearAndMonthNumberArgsDto!!.year,
            yearAndMonthNumberArgsDto!!.mesDto.number,
            textInput_read_current.text.toString().toInt(),
            textInput_measured_consumption.text.toString().toInt(),
            textInput_billed_consumption.text.toString().toInt(),
            textInput_street_lighting.text.toString().toDouble(),
            textInput_rate.text.toString().toDouble(),
            textInput_amount.text.toString().toDouble(),
            textInput_date_init.text.toString(),
            textInput_date_end.text.toString()
        )
    }

    private fun updateDate(year: Int, month: Int, dayOfMonth: Int, texField: TextInputEditText) {
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

        val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))
        simpleDateFormat.format(calendar.time)
        texField.setText(simpleDateFormat.format(calendar.time))
    }

}

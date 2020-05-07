package com.aplicativo.powercontrol

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.aplicativo.powercontrol.database.AppDataBase
import com.aplicativo.powercontrol.domain.ElectricityBill
import com.aplicativo.powercontrol.dto.DateArgsDto
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.fragment_register_count_power.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class RegisterCountPowerFragment : Fragment() {

    private val calendar = Calendar.getInstance()
    private var dateArgsDto: DateArgsDto? = null
    private var electricityBill: ElectricityBill? = null
    private var isSave = false
    private val JANUARY = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_register_count_power, container, false)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 2 && resultCode == Activity.RESULT_OK){
            data?.data?.also {
                it.path
                Log.e("RESULT", "Uri: ${it}")
            }

        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        arguments?.let {
            dateArgsDto =
                RegisterCountPowerFragmentArgs.fromBundle(it).dateArgs
            textView_month_selected.text =
                getString(R.string.register_month_label, dateArgsDto!!.mesDto.name)
            if (dateArgsDto!!.mesDto.number == JANUARY && !hasValueLastYear(dateArgsDto!!))
                TxtLayout_read_last.visibility = View.VISIBLE

            electricityBill = RegisterCountPowerFragmentArgs.fromBundle(it).electricityBillArgs
            electricityBill?.let {
                isSave = false
                buttonSaveOrUpdate.text = getString(R.string.update)
                loadDataToUpdate()
            } ?: run {
                buttonSaveOrUpdate.text = getString(R.string.register)
                isSave = true
            }
        }

        button_input_file.setOnClickListener {

            Log.e("DIR", requireContext().getExternalFilesDir("application/prin").toString())

//
//            val documents = "documents/powerControl"
//            val documentFolder = File(myDir, documents)
//            documentFolder.mkdirs()

//            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
//                // Filter to only show results that can be "opened", such as a
//                // file (as opposed to a list of contacts or timezones)
//                addCategory(Intent.CATEGORY_OPENABLE)
//
//                // Filter to show only images, using the image MIME data type.
//                // If one wanted to search for ogg vorbis files, the type would be "audio/ogg".
//                // To search for all documents available via installed storage providers,
//                // it would be "*/*".
//                type = "application/pdf"
//            }
//
//            startActivityForResult(intent, 2)
        }


        buttonSaveOrUpdate.setOnClickListener {
            if (TxtLayout_read_last.visibility == View.VISIBLE
                && validateFieldEmpty(textInput_read_last))
                return@setOnClickListener

            if (validateFieldEmpty(
                    textInput_read_current,
                    textInput_measured_consumption,
                    textInput_billed_consumption,
                    textInput_street_lighting,
                    textInput_rate,
                    textInput_date_init,
                    textInput_date_end
                )
            ) return@setOnClickListener

            electricityBill = buildElectricityBillObject()
            var message = getString(R.string.register_sucess)
            if (isSave) {
                saveCount(electricityBill!!)
                if (TxtLayout_read_last.visibility == View.VISIBLE){
                    saveDecember()
                }
            } else {
                updateCount(electricityBill!!)
                message = getString(R.string.update_sucess)
            }

            Toast.makeText(
                activity,
                message,
                Toast.LENGTH_SHORT
            ).show()
            backHomeScreen(it)

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

    private fun saveDecember() {
        val billDecember = ElectricityBill(
            dateArgsDto!!.year - 1,
            12,
            textInput_read_last.text.toString().toInt(),
            0,
            0,
            0.0,
            0.0,
            0.0,
            "00/00/000",
            "00/00/000"
        )
        saveCount(billDecember)
    }

    private fun hasValueLastYear(date: DateArgsDto): Boolean {
        val result = AppDataBase(requireActivity()).electricityBillDao().getElectricityBillDtoAll(date.year - 1)
        return result.isNotEmpty()
    }

    private fun updateCount(bill: ElectricityBill) {
        AppDataBase(requireActivity()).electricityBillDao()
            .updateElectricityBill(bill)
    }

    private fun saveCount(bill: ElectricityBill) {
        AppDataBase(requireActivity()).electricityBillDao()
            .addElectricityBill(bill)
    }

    private fun validateFieldEmpty(vararg fields: TextInputEditText): Boolean {
        for (field in fields) {
            if (field.text.toString().trim().isEmpty()) {
                field.error = "preencha o campo corretamente"
                field.requestFocus()
                return true
            }
        }
        return false
    }

    private fun backHomeScreen(it: View) {
        Navigation.findNavController(it)
            .navigate(R.id.action_registerCountPowerFragment_to_homeFragment)
    }

    private fun loadDataToUpdate() {
        electricityBill?.let {
            textInput_read_current.setText(electricityBill!!.currentReading.toString())
            textInput_measured_consumption.setText(electricityBill!!.measuredConsumption.toString())
            textInput_billed_consumption.setText(electricityBill!!.billedConsumption.toString())
            textInput_street_lighting.setText(electricityBill!!.streetLighting.toString())
            textInput_rate.setText(electricityBill!!.rate.toString())
            textInput_amount.setText(electricityBill!!.amount.toString())
            textInput_date_init.setText(electricityBill!!.initDate)
            textInput_date_end.setText(electricityBill!!.endDate)
        }

    }

    private fun buildElectricityBillObject(): ElectricityBill? {
        val electricityBillNew = ElectricityBill(
            dateArgsDto!!.year,
            dateArgsDto!!.mesDto.number,
            textInput_read_current.text.toString().toInt(),
            textInput_measured_consumption.text.toString().toInt(),
            textInput_billed_consumption.text.toString().toInt(),
            textInput_street_lighting.text.toString().toDouble(),
            textInput_rate.text.toString().toDouble(),
            textInput_amount.text.toString().toDouble(),
            textInput_date_init.text.toString(),
            textInput_date_end.text.toString()
        )
        if (!isSave) {
            electricityBillNew.id = electricityBill!!.id
        }

        return electricityBillNew
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

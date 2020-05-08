package com.aplicativo.powercontrol

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.aplicativo.powercontrol.database.AppDataBase
import com.aplicativo.powercontrol.domain.ElectricityBill
import com.aplicativo.powercontrol.dto.DateArgsDto
import com.aplicativo.powercontrol.utils.FileFacilitator
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.fragment_register_count_power.*
import java.text.SimpleDateFormat
import java.util.*


class RegisterCountPowerFragment : Fragment() {

    private val calendar = Calendar.getInstance()
    private var dateArgsDto: DateArgsDto? = null
    private var electricityBill: ElectricityBill? = null
    private var pathUri: Uri? = null
    private var isSave = false
    private val JANUARY: Int = 1
    private val REQUEST_CODE: Int = 2

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_register_count_power, container, false)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            data?.data?.also {
                pathUri = it
                val filename = FileFacilitator.getFileNameByUri(it)
                if (filename != null) {
                    button_input_file.text = filename
                }
            }
        }
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        FileFacilitator.init(requireContext())
        super.onActivityCreated(savedInstanceState)
        arguments?.let {
            loadArguments(it)
        }
        button_input_file.setOnClickListener {

            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "application/pdf"
            }
            startActivityForResult(intent, REQUEST_CODE)
        }

        fun hasDocument(): Boolean{
            return  pathUri != null
        }


        buttonSaveOrUpdate.setOnClickListener { it ->
            if (TxtLayout_read_last.visibility == View.VISIBLE
                && validateFieldEmpty(textInput_read_last)
            )
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

            if (!hasDocument()){
                Snackbar.make(it, getString(R.string.add_document_alert), Snackbar.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            electricityBill = buildElectricityBillObject()
            var message = getString(R.string.register_sucess)
            if (isSave) {
                pathUri?.let { uri ->
                    val name = FileFacilitator.getFileNameByUri(uri)
                    FileFacilitator.saveDocumentInDirectory(uri, name!!)
                }
                saveCount(electricityBill!!)
                if (TxtLayout_read_last.visibility == View.VISIBLE) {
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
            openDatePickerDialog(textInput_date_init)
        }

        TxtLayout_date_end.setEndIconOnClickListener {
            openDatePickerDialog(textInput_date_end)
        }
    }

    private fun openDatePickerDialog(textInput: TextInputEditText) {
        DatePickerDialog(
            this.requireContext(),
            DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                updateDate(year, month, dayOfMonth, textInput)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun loadArguments(it: Bundle) {
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
            "00/00/000",
            ""
        )
        saveCount(billDecember)
    }

    private fun getPathDocument(): String? {
        return FileFacilitator.directoryDefault?.path + "/"+ pathUri?.let {
            FileFacilitator.getFileNameByUri(
                it
            )
        }
    }

    private fun hasValueLastYear(date: DateArgsDto): Boolean {
        val result = AppDataBase(requireActivity()).electricityBillDao()
            .getElectricityBillDtoAll(date.year - 1)
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
        val pathDocument = getPathDocument()
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
            textInput_date_end.text.toString(),
            pathDocument!!
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

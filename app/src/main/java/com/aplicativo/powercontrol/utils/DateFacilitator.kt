package com.aplicativo.powercontrol.utils

import com.aplicativo.powercontrol.dto.MesDto

object DateFacilitator {

    private val MONTHS =
        listOf(
            MesDto(1, "JAN"),
            MesDto(2, "FER"),
            MesDto(3, "MAR"),
            MesDto(4, "ABR"),
            MesDto(5, "MAI"),
            MesDto(6, "JUN"),
            MesDto(7, "JUL"),
            MesDto(8, "AGO"),
            MesDto(9, "SET"),
            MesDto(10, "OUT"),
            MesDto(11, "NOV"),
            MesDto(12, "DEZ")
        )

    fun getMonthsListToCurrentMonth(month: Int): ArrayList<MesDto> {
        val listMonths = ArrayList<MesDto>()
        val indexMonth = month - 1
        for (index in 0..indexMonth) {
            listMonths.add(MONTHS[index])
        }
        return listMonths
    }

    fun getMonthByNumber(month: Int): MesDto {
        return MONTHS[month - 1]
    }

}
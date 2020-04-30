package com.aplicativo.powercontrol.utils

import com.aplicativo.powercontrol.dto.MesDto

object DateFacilitator {

    private val MONTHS =
        listOf(
            MesDto(1, "JANEIRO"),
            MesDto(2, "FERVEREIRO"),
            MesDto(3, "MARÇO"),
            MesDto(4, "ABRIL"),
            MesDto(5, "MAIO"),
            MesDto(6, "JUNHO"),
            MesDto(7, "JULHO"),
            MesDto(8, "AGOSTO"),
            MesDto(9, "SETEMBRO"),
            MesDto(10, "OUTUBRO"),
            MesDto(11, "NOVEMBRO"),
            MesDto(12, "DEZEMBRO")
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
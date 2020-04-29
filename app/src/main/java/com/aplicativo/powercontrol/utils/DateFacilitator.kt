package com.aplicativo.powercontrol.utils

object DateFacilitator {

    private val MONTHS =
        listOf("JAN", "FER", "MAR", "ABR", "MAI", "JUN", "JUL", "AGO", "SET", "OUT", "NOV", "DEZ")

    fun getMonthsListToCurrentMonth(month: Int): ArrayList<String> {
        val listMonths = ArrayList<String>()
        val indexMonth = month - 1
        for (index in 0..indexMonth){
            listMonths.add(MONTHS[index])
        }
        return listMonths
    }

    fun getMonthByNumber(month: Int): String{
        return MONTHS[month - 1]
    }

}
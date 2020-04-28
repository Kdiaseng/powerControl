package com.aplicativo.powercontrol.dto

import androidx.room.ColumnInfo

class ElectricityBillDto(
    @ColumnInfo(name = "month_number")
    val monthNumber: Int,


    val amount: Double
) {
}
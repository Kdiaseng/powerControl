package com.aplicativo.powercontrol.dto

import androidx.room.ColumnInfo

class ElectricityBillDto(
    @ColumnInfo(name = "month_number")
    val monthNumber: Int,

    @ColumnInfo(name = "path_document")
    val pathDocument: String,

    val amount: Double
) {
}
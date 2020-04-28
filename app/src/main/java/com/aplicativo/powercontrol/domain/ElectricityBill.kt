package com.aplicativo.powercontrol.domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "electricity_bill")
data class ElectricityBill(

    val year: Int,

    @ColumnInfo(name = "month_number")
    val monthNumber: Int,

    @ColumnInfo(name = "current_reading")
    val currentReading: Int,

    @ColumnInfo(name = "measured_consumption")
    val measuredConsumption : Int,

    @ColumnInfo(name = "billed_consumption")
    val billedConsumption: Int,

    @ColumnInfo(name = "street_lighting")
    val streetLighting: Double,

    val rate: Double,
    val amount: Double,

    @ColumnInfo(name = "init_date")
    val initDate: String,

    @ColumnInfo(name = "end_date")
    val endDate: String


): Serializable{
    @PrimaryKey(autoGenerate = true)
    var id :Long = 0
}

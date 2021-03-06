package com.aplicativo.powercontrol.dao

import androidx.room.*
import com.aplicativo.powercontrol.domain.ElectricityBill
import com.aplicativo.powercontrol.dto.ElectricityBillDto

@Dao
interface ElectricityBillDao {

    @Query("SELECT * FROM electricity_bill")
    fun getElectricityBillAll(): List<ElectricityBill>

    @Query("SELECT * FROM electricity_bill WHERE month_number =:monthNumber AND year =:year ")
    fun getElectricityBillAllByMonthNumber(monthNumber: Int, year: Int): ElectricityBill

    @Query("SELECT current_reading FROM electricity_bill WHERE month_number =:monthNumber AND year =:year")
    fun getLastRead(monthNumber: Int, year: Int): Int?

    @Query("SELECT month_number, amount, path_document FROM electricity_bill WHERE year =:year ORDER BY month_number")
    fun getElectricityBillDtoAll(year: Int): List<ElectricityBillDto>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addElectricityBill(electricityBill: ElectricityBill)

    @Update
    fun updateElectricityBill(electricityBill: ElectricityBill)


    @Delete
    fun deleteElectricityBill(electricityBill: ElectricityBill)
}
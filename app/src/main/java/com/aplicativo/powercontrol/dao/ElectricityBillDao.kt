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

    @Query("SELECT month_number, amount FROM electricity_bill WHERE year =:year")
    fun getElectricityBillDtoAll(year: Int): List<ElectricityBillDto>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addElectricityBill(electricityBill: ElectricityBill)

    @Update
    fun updateElectricityBill(electricityBill: ElectricityBill)


    @Delete
    fun deleteElectricityBill(electricityBill: ElectricityBill)
}
package com.aplicativo.powercontrol.dao

import androidx.room.*
import com.aplicativo.powercontrol.domain.ElectricityBill
import com.aplicativo.powercontrol.dto.ElectricityBillDto

@Dao
interface ElectricityBillDao {

    @Query("SELECT * FROM electricity_bill")
    fun getElectricityBillAll(): List<ElectricityBill>

    @Query("SELECT * FROM electricity_bill WHERE month_number =:monthNumber ")
    fun getElectricityBillAllByMonthNumber(monthNumber: Int): ElectricityBill

    @Query("SELECT month_number, amount FROM electricity_bill")
    fun getElectricityBillDtoAll(): List<ElectricityBillDto>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addElectricityBill(electricityBill: ElectricityBill)

    @Update
    fun updateElectricityBill(electricityBill: ElectricityBill)


    @Delete
    fun deleteElectricityBill(electricityBill: ElectricityBill)
}
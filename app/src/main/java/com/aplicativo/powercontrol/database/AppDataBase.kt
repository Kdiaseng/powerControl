package com.aplicativo.powercontrol.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.aplicativo.powercontrol.dao.ElectricityBillDao
import com.aplicativo.powercontrol.domain.ElectricityBill

@Database(entities = [ElectricityBill::class], version = 1, exportSchema = false)
abstract class AppDataBase : RoomDatabase() {
    abstract fun electricityBillDao(): ElectricityBillDao

    companion object {
        @Volatile
        private var instance: AppDataBase? = null

        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDataBase(context).also {
                instance = it
            }
        }

        private fun buildDataBase(context: Context) = Room.databaseBuilder(
            context.applicationContext,
            AppDataBase::class.java,
            "control-power-database"
        ).allowMainThreadQueries().fallbackToDestructiveMigration().build()
    }
}
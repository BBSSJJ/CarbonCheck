package kr.co.carboncheck.android.carboncheckapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import kr.co.carboncheck.android.carboncheckapp.dao.PlugDao
import kr.co.carboncheck.android.carboncheckapp.entity.Plug

@Database(entities = [Plug::class], version = 2)
abstract class CarbonCheckLocalDatabase : RoomDatabase() {
    abstract fun plugDao(): PlugDao

    companion object {
        @Volatile
        private var instance: CarbonCheckLocalDatabase? = null

        fun getInstance(context: Context): CarbonCheckLocalDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): CarbonCheckLocalDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                CarbonCheckLocalDatabase::class.java,
                "carbon_check_database"
            ).build()
        }
    }

}
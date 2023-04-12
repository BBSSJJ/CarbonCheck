package kr.co.carboncheck.android.carboncheckapp.battery

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHandler(context: Context) :
    SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    companion object {
        private const val DB_NAME = "BatteryLog.db"
        private const val DB_VERSION = 1
        private const val TABLE_NAME = "BatteryLog"
        private const val COL_ID = "id"
        private const val COL_TIMESTAMP = "timestamp"
        private const val COL_STATUS = "status"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = ("CREATE TABLE $TABLE_NAME ($COL_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COL_TIMESTAMP TEXT, $COL_STATUS TEXT)")
        db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun addBatteryHistory(batteryHistory: BatteryHistory): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COL_TIMESTAMP, batteryHistory.timestamp)
        contentValues.put(COL_STATUS, batteryHistory.batteryPercentage)

        return db.insert(TABLE_NAME, null, contentValues)
    }
}
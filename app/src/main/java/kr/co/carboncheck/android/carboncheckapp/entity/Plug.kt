package kr.co.carboncheck.android.carboncheckapp.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "plug")
data class Plug(
    @PrimaryKey @ColumnInfo(name = "plug_id") val plugId: String,
    @ColumnInfo(name = "plug_name") var plugName: String?
)

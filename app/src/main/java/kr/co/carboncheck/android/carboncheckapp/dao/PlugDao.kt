package kr.co.carboncheck.android.carboncheckapp.dao

import androidx.room.*
import kr.co.carboncheck.android.carboncheckapp.entity.Plug

@Dao
interface PlugDao {
    @Query("SELECT * FROM plug")
    fun getAllPlug(): List<Plug>

    @Query("SELECT * FROM plug WHERE plug_id = :plugId")
    fun findById(plugId: String) : Array<Plug>

    @Insert
    fun insertPlug(plug :Plug)

    @Update
    fun updatePlug(plug: Plug)

    @Delete
    fun deletePlug(plug: Plug)
}
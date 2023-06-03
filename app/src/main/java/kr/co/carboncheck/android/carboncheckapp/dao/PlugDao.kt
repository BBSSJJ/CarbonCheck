package kr.co.carboncheck.android.carboncheckapp.dao

import androidx.room.*
import kr.co.carboncheck.android.carboncheckapp.entity.Plug

@Dao
interface PlugDao {
    @Query("SELECT * FROM plug")
    suspend fun getAllPlug(): List<Plug>

    @Query("SELECT * FROM plug WHERE plug_id = :plugId")
    suspend fun findById(plugId: String) : Plug?

    @Insert
    suspend fun insertPlug(plug :Plug)

    @Update
    suspend fun updatePlug(plug: Plug)

    @Delete
    suspend fun deletePlug(plug: Plug)
}
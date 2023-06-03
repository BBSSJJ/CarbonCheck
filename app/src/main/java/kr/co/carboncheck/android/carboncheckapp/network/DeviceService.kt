package kr.co.carboncheck.android.carboncheckapp.network


import androidx.room.Delete
import kr.co.carboncheck.android.carboncheckapp.dto.*
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.Query

interface DeviceService {
    @POST("/homeserver/register")
    fun postRegisterHomeServerRequest(@Body registerHomeServerRequest: RegisterHomeServerRequest): Call<RegisterHomeServerResponse>

    @POST("/homeserver/join")
    fun postJoinHomeServerRequest(@Body joinHomeServerRequest: JoinHomeServerRequest): Call<JoinHomeServerResponse>

    @POST("/plug/register")
    fun postRegisterPlugRequest(@Body registerPlugRequest: RegisterPlugRequest): Call<RegisterPlugResponse>

    @DELETE("/plug/delete")
    fun deletePlugRequest(@Query("plugId") plugId: String): Call<Boolean>
}
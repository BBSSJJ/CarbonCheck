package kr.co.carboncheck.android.carboncheckapp.network


import kr.co.carboncheck.android.carboncheckapp.dto.JoinHomeServerRequest
import kr.co.carboncheck.android.carboncheckapp.dto.JoinHomeServerResponse
import kr.co.carboncheck.android.carboncheckapp.dto.RegisterHomeServerRequest
import kr.co.carboncheck.android.carboncheckapp.dto.RegisterHomeServerResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface DeviceService {
    @POST("/homeserver/register")
    fun postRegisterHomeServerRequest(@Body registerHomeServerRequest: RegisterHomeServerRequest): Call<RegisterHomeServerResponse>

    @POST("/homeserver/join")
    fun postJoinHomeServerRequest(@Body joinHomeServerRequest: JoinHomeServerRequest): Call<JoinHomeServerResponse>
}
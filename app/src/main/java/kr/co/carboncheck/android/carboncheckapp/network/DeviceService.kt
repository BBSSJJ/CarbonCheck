package kr.co.carboncheck.android.carboncheckapp.network


import kr.co.carboncheck.android.carboncheckapp.model.RegisterHomeServerRequest
import kr.co.carboncheck.android.carboncheckapp.model.RegisterHomeServerResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface DeviceService {
    @POST("/register/homeserver")
    fun postRegisterHomeServerRequest(@Body registerHomeServerRequest: RegisterHomeServerRequest): Call<RegisterHomeServerResponse>
}
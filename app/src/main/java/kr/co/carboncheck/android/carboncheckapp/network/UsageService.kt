package kr.co.carboncheck.android.carboncheckapp.network

import kr.co.carboncheck.android.carboncheckapp.dto.GetUsageResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface UsageService {
    @GET("/waterusage/user")
    fun getUserWaterUsageRequest(@Query("userId") userId: String): Call<List<GetUsageResponse>>

    @GET("/electricityusage/user")
    fun getUserElectricityUsageRequest(@Query("userId") userId: String): Call<List<GetUsageResponse>>

    @GET("/waterusage/group")
    fun getGroupWaterUsageRequest(@Query("homeServerId") homeServerId: String): Call<List<GetUsageResponse>>

    @GET("/electricityusage/group")
    fun getGroupElectricityUsageRequest(@Query("homeServerId") homeServerId: String): Call<List<GetUsageResponse>>
}
package kr.co.carboncheck.android.carboncheckapp.network

import com.google.gson.annotations.SerializedName

data class UpdateElectricityUsageDto(
    @SerializedName("plug_id")
    val plugId: String,
    val amount: Float
)

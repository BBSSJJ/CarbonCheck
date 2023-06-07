package kr.co.carboncheck.android.carboncheckapp.network

data class SseDto(
    val msg: String,
    val id: String,
    val success: Boolean
)

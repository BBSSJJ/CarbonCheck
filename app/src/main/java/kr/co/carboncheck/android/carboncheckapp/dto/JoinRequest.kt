package kr.co.carboncheck.android.carboncheckapp.dto

data class JoinRequest(
    val email: String,
    val password: String,
    val name: String,
    val authType: String
)

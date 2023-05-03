package kr.co.carboncheck.android.carboncheckapp.model

data class JoinRequestDTO(
    val email: String,
    val password: String,
    val name: String,
    val authType: String
)

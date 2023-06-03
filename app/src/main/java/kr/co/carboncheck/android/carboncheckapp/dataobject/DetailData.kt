package kr.co.carboncheck.android.carboncheckapp.dataobject

data class DetailData(
    val place: String,
    val placeImage: Int,
    val typeImage: Int,
    val typeUsage: String,
    val carbonUsage: String,
    val Cost: String,
    val Plus: Boolean
)

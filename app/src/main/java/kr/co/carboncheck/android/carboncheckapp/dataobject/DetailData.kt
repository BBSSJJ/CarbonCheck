package kr.co.carboncheck.android.carboncheckapp.dataobject

data class DetailData(
    var place: String,
    val placeImage: Int,
    val typeImage: Int,
    var typeUsage: String,
    val carbonUsage: String,
    val cost: String,
    val plus: Boolean,
    val plugId: String?
)

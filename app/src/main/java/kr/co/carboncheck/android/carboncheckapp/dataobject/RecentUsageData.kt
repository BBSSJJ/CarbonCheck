package kr.co.carboncheck.android.carboncheckapp.dataobject

data class RecentUsageData(
    val date: String,
    val electricityUsage: Float,
    val waterUsage: Float,
    val carbonEmission: Float
)


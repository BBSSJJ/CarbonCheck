package kr.co.carboncheck.android.carboncheckapp.dataobject

// total_usage_fragment Recycler view 의 아이템
data class MemberUsageData (
    val userName: String,
    val targetAmount: Float,
    val currentAmount: Float
)
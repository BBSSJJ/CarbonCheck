package kr.co.carboncheck.android.carboncheckapp.dataobject

// total_usage_fragment Recycler view 의 아이템
data class MemberUsageData (
    val user_name: String,
    val target_usage: Float,
    val current_usage: Float
)
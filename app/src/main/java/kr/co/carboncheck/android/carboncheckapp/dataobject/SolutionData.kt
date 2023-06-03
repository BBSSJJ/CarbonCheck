package kr.co.carboncheck.android.carboncheckapp.dataobject

data class SolutionData (
    val SolutionTitle: String,
    val SolutionMission: String,
    val ExpectedWaterAmount: Float,
    val ExpectedElectricityAmount: Float,
    val ExpectedExpenseAmount: Int,
    val ExpectedCarbonAmount: Float,
    val result: Boolean
)
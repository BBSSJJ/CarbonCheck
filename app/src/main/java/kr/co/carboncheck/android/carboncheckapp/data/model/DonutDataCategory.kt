package kr.co.carboncheck.android.carboncheckapp.data.model

import androidx.annotation.ColorRes
import kr.co.carboncheck.android.carboncheckapp.R

sealed class DonutDataCategory(val name: String, @ColorRes val colorRes: Int)

object ElectricCategory : DonutDataCategory("electric", R.color.electric)
object WaterCategory : DonutDataCategory("water", R.color.water)
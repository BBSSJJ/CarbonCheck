package kr.co.carboncheck.android.carboncheckapp.util

import java.math.RoundingMode
import kotlin.math.roundToInt

class NumberFormat {
    fun toKwh(electricityUsage : Float): Float{
        return (electricityUsage * 0.001).toBigDecimal().setScale(2, RoundingMode.HALF_UP).toFloat()
    }
    fun toLiter(waterUsage: Float): Float{
        return waterUsage.toBigDecimal().setScale(2, RoundingMode.HALF_UP).toFloat()
    }
    fun waterUsageToCarbonUsage(waterUsage: Float): Float{
        return (waterUsage * 0.289).toBigDecimal().setScale(2, RoundingMode.HALF_UP).toFloat()
    }
    fun electricityUsageToCarbonUsage(electricityUsage: Float): Float{  //Wh단위일 때 넣으면 된다
        return (electricityUsage * 0.001 * 424).toBigDecimal().setScale(2, RoundingMode.HALF_UP).toFloat()
    }
    fun waterUsageToPrice(waterUsage:Float): Int{
        return (waterUsage * 0.001  * 470).roundToInt()
    }
    fun electricityToPrice(electricityUsage: Float): Int{ //Wh단위일 때 넣으면 된다
        return (toKwh(electricityUsage) * 151.5).roundToInt()
    }



    fun toKwhString(electricityUsage: Float):String{
        return toKwh(electricityUsage).toString() + " KWh"
    }
    fun toLiterString(waterUsage: Float):String{
        return toLiter(waterUsage).toString() + " L"
    }
    fun waterUsageToCarbonUsageString(waterUsage: Float): String{
        return waterUsageToCarbonUsage(waterUsage).toString() + " g"
    }
    fun electricityUsageToCarbonUsageString(electricityUsage: Float) : String{
        return electricityUsageToCarbonUsage(electricityUsage).toString() + " g"
    }
    fun totalCarbonUsageString(waterUsage: Float, electricityUsage: Float): String{
        return (waterUsageToCarbonUsage(waterUsage) + electricityUsageToCarbonUsage(electricityUsage)).toString() + " g"
    }
    fun waterUsageToPriceString(waterUsage: Float): String{
        return waterUsageToPrice(waterUsage).toString() + " ₩"
    }
    fun electricityToPriceString(electricityUsage: Float): String{
        return electricityToPrice(electricityUsage).toString() + " ₩"
    }
    fun totalPriceString(waterUsage: Float, electricityUsage: Float): String{
        return (waterUsageToPrice(waterUsage) + electricityToPrice(electricityUsage)).toString() + " ₩"
    }
}
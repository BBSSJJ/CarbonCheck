package kr.co.carboncheck.android.carboncheckapp.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    private val userWaterUsage: MutableLiveData<Map<String, Float>> = MutableLiveData()
    private val userElectricityUsage: MutableLiveData<Map<String, Float>> = MutableLiveData()
    private val groupWaterUsage: MutableLiveData<Map<String, Float>> = MutableLiveData()
    private val groupElectricityUsage: MutableLiveData<Map<String, Float>> = MutableLiveData()
    private val groupTargetValue: MutableLiveData<Map<String, Float>> = MutableLiveData()
    private val groupMember: MutableLiveData<List<String>> = MutableLiveData()

    fun setUserWaterUsage(data: Map<String, Float>) {
        userWaterUsage.value = data
    }

    fun setUserElectricityUsage(data: Map<String, Float>) {
        userElectricityUsage.value = data
    }

    fun setGroupWaterUsage(data: Map<String, Float>) {
        groupWaterUsage.value = data
    }

    fun setGroupElectricityUsage(data: Map<String, Float>) {
        groupElectricityUsage.value = data
    }

    fun setGroupTargetValue(data: Map<String, Float>) {
        groupTargetValue.value = data
    }

    fun setGroupMember(data: List<String>) {
        groupMember.value = data
    }

    fun getUserWaterUsage(): MutableLiveData<Map<String, Float>> {
        return this.userWaterUsage
    }

    fun getUserElectricityUsage(): MutableLiveData<Map<String, Float>> {
        return this.userElectricityUsage
    }

    fun getGroupWaterUsage(): MutableLiveData<Map<String, Float>> {
        return this.groupWaterUsage
    }

    fun getGroupElectricityUsage(): MutableLiveData<Map<String, Float>> {
        return this.groupElectricityUsage
    }

    fun getGroupTargetValue(): MutableLiveData<Map<String, Float>> {
        return this.groupTargetValue
    }

    fun groupMember(): MutableLiveData<List<String>> {
        return this.groupMember
    }


}
package kr.co.carboncheck.android.carboncheckapp.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    private var userWaterUsage: MutableLiveData<Map<String, Float>> = MutableLiveData()
    private var userElectricityUsage: MutableLiveData<Map<String, Float>> = MutableLiveData()
    private var groupWaterUsage: MutableLiveData<Map<String, Float>> = MutableLiveData()
    private var groupElectricityUsage: MutableLiveData<Map<String, Float>> = MutableLiveData()
    private var groupTargetValue: MutableLiveData<Map<String, Float>> = MutableLiveData()
    private var groupMember: MutableLiveData<List<String>> = MutableLiveData()
    private var userElectricityUsageName: MutableLiveData<Map<String, Pair<String, Float>>> = MutableLiveData()

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

    fun setUserElectricityUsageName(data: Map<String, Pair<String, Float>>){
        userElectricityUsageName.value = data
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

    fun getUserElectricityUsageName(): MutableLiveData<Map<String, Pair<String, Float>>> {
        return this.userElectricityUsageName
    }



}
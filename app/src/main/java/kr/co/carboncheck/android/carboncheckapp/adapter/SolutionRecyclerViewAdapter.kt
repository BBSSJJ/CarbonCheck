package kr.co.carboncheck.android.carboncheckapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kr.co.carboncheck.android.carboncheckapp.R
import kr.co.carboncheck.android.carboncheckapp.databinding.SolutionListBinding
import kr.co.carboncheck.android.carboncheckapp.dataobject.SolutionData
import kr.co.carboncheck.android.carboncheckapp.view.SolutionFragment

class SolutionRecyclerViewAdapter(val fragment: SolutionFragment) :
    RecyclerView.Adapter<SolutionRecyclerViewAdapter.MyViewHolder>() {

    var datalist = mutableListOf<SolutionData>()

    inner class MyViewHolder(private val binding: SolutionListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(solutionData: SolutionData) {

            binding.solutionResult.setOnCheckedChangeListener { _, isChecked ->
                //가져오기

                if (isChecked) {
                    fragment.addAchieveAmount(
                        solutionData.ExpectedWaterAmount,
                        solutionData.ExpectedElectricityAmount,
                        solutionData.ExpectedExpenseAmount,
                        solutionData.ExpectedCarbonAmount
                    )
                } else {
                    fragment.subAchieveAmount(
                        solutionData.ExpectedWaterAmount,
                        solutionData.ExpectedElectricityAmount,
                        solutionData.ExpectedExpenseAmount,
                        solutionData.ExpectedCarbonAmount
                    )

                }

            }

            binding.solutionTitle.text = solutionData.SolutionTitle
            binding.expectedWaterAmount.text = solutionData.ExpectedWaterAmount.toString()
            binding.expectedElectricityAmount.text =
                solutionData.ExpectedElectricityAmount.toString()
            binding.expectedExpenseAmount.text = solutionData.ExpectedExpenseAmount.toString()
            binding.expectedCarbonAmount.text = solutionData.ExpectedCarbonAmount.toString()
        }
    }

    override fun getItemCount(): Int = datalist.size

    fun getAchievedXP(): ArrayList<Float> {
        var achievedWater = 0f
        var achievedElectricity = 0f
        var achievedExpense = 0f
        var achievedCarbonAmount = 0f
        for (data in datalist) {
            if (data.result) {
                achievedWater += data.ExpectedWaterAmount
                achievedElectricity += data.ExpectedElectricityAmount
                achievedExpense += data.ExpectedExpenseAmount
                achievedCarbonAmount += data.ExpectedCarbonAmount
            }
        }
        return arrayListOf(
            achievedWater,
            achievedElectricity,
            achievedExpense,
            achievedCarbonAmount
        )
    }

    override fun onBindViewHolder(
        holder: SolutionRecyclerViewAdapter.MyViewHolder,
        position: Int
    ) {
        val data = datalist[position]
        holder.bind(data)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SolutionRecyclerViewAdapter.MyViewHolder { // onCreateViewHolder 메서드를 추가
        val binding = SolutionListBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ) // 레이아웃 인플레이션
        return MyViewHolder(binding) // MyViewHolder 생성 및 반환
    }

}
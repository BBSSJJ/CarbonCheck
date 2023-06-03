package kr.co.carboncheck.android.carboncheckapp.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import kr.co.carboncheck.android.carboncheckapp.R
import kr.co.carboncheck.android.carboncheckapp.databinding.RankListBinding
import kr.co.carboncheck.android.carboncheckapp.dataobject.MemberUsageData
import kr.co.carboncheck.android.carboncheckapp.dataobject.RankData

class RankRecyclerViewAdapter : RecyclerView.Adapter<RankRecyclerViewAdapter.MyViewHolder>() {

    var datalist = mutableListOf<RankData>()

    inner class MyViewHolder(private val binding: RankListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val chart = binding.rankUserChart
        var user_xp = 0f
        fun bind(rankData: RankData) {
            binding.rankUserName.text = rankData.rankUserName
            user_xp = rankData.rankXP
            val pictureId = when (rankData.rankProfilePic % 3) {
                0 -> R.drawable.profile0
                1 -> R.drawable.profile1
                2 -> R.drawable.profile2
                else -> R.drawable.profile0
            }
            binding.rankProfilePic.setImageResource(pictureId)
        }
    }

    override fun getItemCount(): Int = datalist.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data = datalist[position]
        holder.chart.data = getBarData(data)
        holder.chart.setTouchEnabled(false)                         // 터치 유무
        holder.chart.description.isEnabled = false                  // 설명 비활성화
        holder.chart.legend.isEnabled = false                       // Legend는 차트의 범례
        holder.chart.xAxis.position = XAxis.XAxisPosition.BOTTOM    // X축 위치는 아래쪽으로 설정

        val xAxis: XAxis = holder.chart.xAxis
        xAxis.setDrawAxisLine(false) // X축 라인 비활성화
        xAxis.setDrawLabels(false) // X축 라인 비활성화
        xAxis.granularity = 1f
        xAxis.textSize = 15f
        xAxis.gridLineWidth = 25f
        xAxis.gridColor = Color.WHITE

        holder.chart.axisLeft.axisMaximum = 100f        // Y축 최대값은 100으로 설정 (퍼센트 단위이므로)
        holder.chart.axisLeft.axisMinimum = 0f          // Y축 최소값은 0으로 설정
        holder.chart.axisLeft.setDrawGridLines(false)   // Y축 그리드 라인 비활성화
        holder.chart.axisLeft.setDrawAxisLine(false)    // Y축 라인 비활성화
        holder.chart.axisLeft.setDrawLabels(false)      // label 삭제

        holder.chart.setExtraOffsets(0f, 0f, 0f, 0f)

        // YAxis(Left) (수평 막대 기준 아래쪽) - 선 유무, 데이터 최솟값/최댓값, label 유무
        val axisLeft: YAxis = holder.chart.axisLeft
        axisLeft.granularity = 1f // 값만큼 라인선 설정

        // YAxis(Right) (수평 막대 기준 위쪽) - 사이즈, 선 유무
        val axisRight: YAxis = holder.chart.axisRight
//        axisRight.textSize = 15f
        axisRight.setDrawLabels(false) // label 삭제
        axisRight.setDrawGridLines(false)
        axisRight.setDrawAxisLine(false)

        holder.bind(data)
        holder.chart.invalidate()
    }

    private fun getBarData(data: RankData): BarData {
        // 막대의 길이를 계산하는 공식 사용
        val barLength = (data.rankXP / 1000f) * 100f    // MAX XP 1000f 임시로
        // BarEntry 객체에 막대의 길이와 인덱스 추가
        val entries = ArrayList<BarEntry>()
        entries.add(BarEntry(barLength, barLength))

        // BarDataSet 객체에 BarEntry 리스트와 레이블 추가
        val dataSet = BarDataSet(entries, "Usage Data")
        dataSet.color = Color.parseColor("#788B55")


        // % 숫자를 보여 주고 싶다면 아래 두 줄을 주석 해제
//        dataSet.valueTextColor = Color.BLACK // 값의 색상 설정
//        dataSet.valueFormatter = PercentFormatter() // 값의 형식을 퍼센트로 설정
        dataSet.setDrawValues(false)
        // BarData 객체에 BarDataSet 객체 추가
        val data = BarData(dataSet)

        data.barWidth = 0.4f // 막대의 너비 설정 (0~1 사이의 값)

        return data // BarData 객체 반환
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = RankListBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return MyViewHolder(binding) // MyViewHolder 생성 및 반환
    }
}
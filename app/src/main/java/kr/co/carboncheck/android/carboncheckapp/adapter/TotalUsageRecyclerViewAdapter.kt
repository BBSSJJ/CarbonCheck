package kr.co.carboncheck.android.carboncheckapp.adapter

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import kr.co.carboncheck.android.carboncheckapp.databinding.HomeUserUsageListBinding
import kr.co.carboncheck.android.carboncheckapp.dataobject.MemberUsageData


class TotalUsageRecyclerViewAdapter :
    RecyclerView.Adapter<TotalUsageRecyclerViewAdapter.MyViewHolder>() {

    var datalist = mutableListOf<MemberUsageData>()

    inner class MyViewHolder(private val binding: HomeUserUsageListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val chart = binding.userChart
        fun bind(memberUsageData: MemberUsageData) {
            //binding.dogPhotoImg.=dogData.dog_img
            binding.userName.text = memberUsageData.user_name
            binding.targetUsage.text = "/" + memberUsageData.target_usage.toString() + " g"
            binding.currentUsage.text = memberUsageData.current_usage.toString()

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            HomeUserUsageListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int = datalist.size


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data = datalist[position]
        holder.chart.data = getBarData(data)

        // HorizontalBarChart에 옵션 설정

        holder.chart.setTouchEnabled(false) // 터치 유무
        holder.chart.description.isEnabled = false // 설명 비활성화
        holder.chart.legend.isEnabled = false // Legend는 차트의 범례
        holder.chart.xAxis.position = XAxis.XAxisPosition.BOTTOM // X축 위치는 아래쪽으로 설정

//        holder.chart.xAxis.setDrawGridLines(false) // X축 그리드 라인 비활성화
        holder.chart.xAxis.setDrawAxisLine(false) // X축 라인 비활성화
//
        holder.chart.axisLeft.axisMaximum = 100f        // Y축 최대값은 100으로 설정 (퍼센트 단위이므로)
        holder.chart.axisLeft.axisMinimum = 0f          // Y축 최소값은 0으로 설정
        holder.chart.axisLeft.setDrawGridLines(false)   // Y축 그리드 라인 비활성화
        holder.chart.axisLeft.setDrawAxisLine(false)    // Y축 라인 비활성화
        holder.chart.axisLeft.setDrawLabels(false)      // label 삭제
//        holder.chart.axisRight.isEnabled = false      // 오른쪽 Y축 비활성화
//
//        // holder.chart.animateXY(2000, 2000) // 애니메이션 효과 적용
////        holder.chart.xAxis.valueFormatter =
////            IndexAxisValueFormatter(getXAxisValues(data)) // X축 레이블을 가져옴



        holder.chart.setExtraOffsets(10f, 0f, 40f, 0f)

        // XAxis (수평 막대 기준 왼쪽) - 선 유무, 사이즈, 색상, 축 위치 설정

        // XAxis (수평 막대 기준 왼쪽) - 선 유무, 사이즈, 색상, 축 위치 설정
        val xAxis: XAxis = holder.chart.xAxis
        xAxis.granularity = 1f
        xAxis.textSize = 15f
        xAxis.gridLineWidth = 25f
        xAxis.gridColor = Color.parseColor("#80E5E5E5")


        // YAxis(Left) (수평 막대 기준 아래쪽) - 선 유무, 데이터 최솟값/최댓값, label 유무

        // YAxis(Left) (수평 막대 기준 아래쪽) - 선 유무, 데이터 최솟값/최댓값, label 유무
        val axisLeft: YAxis = holder.chart.axisLeft

        axisLeft.granularity = 1f // 값만큼 라인선 설정



        // YAxis(Right) (수평 막대 기준 위쪽) - 사이즈, 선 유무
        // YAxis(Right) (수평 막대 기준 위쪽) - 사이즈, 선 유무
        val axisRight: YAxis = holder.chart.axisRight
        axisRight.textSize = 15f
        axisRight.setDrawLabels(false) // label 삭제
        axisRight.setDrawGridLines(false)
        axisRight.setDrawAxisLine(false)


        holder.bind(datalist[position])
        holder.chart.invalidate() // 차트 갱신
    }

    // BarData 객체를 반환하는 함수 정의
    private fun getBarData(data: MemberUsageData): BarData {
        // 막대의 길이를 계산하는 공식 사용
        val barLength = (data.current_usage / data.target_usage) * 100f
        Log.d("BAR 길이: ", barLength.toString());
        // BarEntry 객체에 막대의 길이와 인덱스 추가
        val entries = ArrayList<BarEntry>()
        entries.add(BarEntry(barLength, 0f))

        // BarDataSet 객체에 BarEntry 리스트와 레이블 추가
        val dataSet = BarDataSet(entries, "Usage Data")

        // 막대의 색상을 조건에 따라 설정 (예시로 초록색과 빨간색 사용)
        if (barLength <= 50f) {
            dataSet.color = Color.GREEN // 목표치의 절반 이하면 초록색으로 설정
        } else {
            dataSet.color = Color.RED // 목표치의 절반 이상이면 빨간색으로 설정
        }

        dataSet.valueTextColor = Color.BLACK // 값의 색상 설정
        dataSet.valueFormatter = PercentFormatter() // 값의 형식을 퍼센트로 설정

        // BarData 객체에 BarDataSet 객체 추가
        val data = BarData(dataSet)

        data.barWidth = 0.7f // 막대의 너비 설정 (0~1 사이의 값)

        return data // BarData 객체 반환
    }

    /*
    // X축 레이블을 가져오는 함수 정의
    private fun getXAxisValues(data: MemberUsageData): ArrayList<String> {
        val labels = ArrayList<String>()

        labels.add(data.user_name) // 이름을 가져와서 레이블에 추가

        return labels
    }
    */
     

}
package kr.co.carboncheck.android.carboncheckapp.view


import android.animation.ValueAnimator
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import app.futured.donut.DonutDirection
import kr.co.carboncheck.android.carboncheckapp.databinding.FragmentTotalUsageBinding
import app.futured.donut.DonutSection
import kr.co.carboncheck.android.carboncheckapp.data.model.ElectricCategory
import kr.co.carboncheck.android.carboncheckapp.data.model.WaterCategory
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import app.futured.donut.DonutProgressView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import kr.co.carboncheck.android.carboncheckapp.R
import kr.co.carboncheck.android.carboncheckapp.adapter.TotalUsageRecyclerViewAdapter
import kr.co.carboncheck.android.carboncheckapp.dataobject.MemberUsageData
import kr.co.carboncheck.android.carboncheckapp.dataobject.RecentUsageData

class TotalUsageFragment : Fragment() {
    private var _binding: FragmentTotalUsageBinding? = null
    private val binding get() = _binding!!
    private var electricAmount = 120.4f     // 전기 탄소 배출량
    private var waterAmount = 56.0f        // 수도 탄소 배출량
    private val memberUsageDatas = mutableListOf<MemberUsageData>()


    companion object {
        private val ALL_CATEGORIES = listOf(
            ElectricCategory,
            WaterCategory
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTotalUsageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Donut Chart Setting
        val donutProgressView = binding.donutView
        // updateIndicators()
        setDonut(donutProgressView)
        // initControls()
        Handler().postDelayed({
            fillDonutInitialData(donutProgressView)
            runInitialDonutAnimation()
        }, 1)

        // MemberUsage Setting
        initializeMemberList()      // TODO: 실제 가족 데이터 불러올 것
        initTotalUsageRecyclerView()

        // RecentUsage Setting
        val recentUsageChartView = binding.recentUsageChart
        setRecentUsageBarChart(recentUsageChartView)
    }

    private fun initTotalUsageRecyclerView() {
        val adapter = TotalUsageRecyclerViewAdapter()     // 어댑터 객체
        adapter.datalist =
            memberUsageDatas             // TODO: 실제 가족 데이터 불러올 것 (optional: 코루틴 사용할 것)
        binding.homeUsageRecyclerView.adapter = adapter   // 뷰에 어댑터 결합
        binding.homeUsageRecyclerView.layoutManager = LinearLayoutManager(activity)   // 레이아웃 매니저 결합
    }

    private fun initializeMemberList() {
        with(memberUsageDatas) {
            // TODO: 여기에 실제 데이터 삽입 하시오 ( 가족 이름, 목표치, 사용량)
            add(MemberUsageData("Lee", 4700f, 4600f))
            add(MemberUsageData("GOP", 9750f, 2100f))
            add(MemberUsageData("Sung", 6420f, 3500f))
        }
    }

    private fun runInitialDonutAnimation() {
        ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 10
            interpolator = FastOutSlowInInterpolator()
//            addUpdateListener {
//                donutProgressView.masterProgress = it.animatedValue as Float
//                donutProgressView.alpha = it.animatedValue as Float
//
//                masterProgressSeekbar.progress = (donutProgressView.masterProgress * 100).toInt()
//            }
            start()
        }
    }

    private fun setDonut(donutProgressView: DonutProgressView) {
        val donutProgressView = binding.donutView
        donutProgressView.cap = 1000f      // 목표 설정량
        donutProgressView.masterProgress = 100f     // 파악 안됨
        donutProgressView.gapAngleDegrees = 270f    // 도넛 구멍 방향
        donutProgressView.direction = DonutDirection.CLOCKWISE
//        donutProgressView.
    }

    private fun fillDonutInitialData(donutProgressView: DonutProgressView) {
        val sections = listOf(
            DonutSection(
                ElectricCategory.name,
                ContextCompat.getColor(requireContext(), R.color.electric),
                electricAmount
            ),
            DonutSection(
                WaterCategory.name,
                ContextCompat.getColor(requireContext(), R.color.water),
                waterAmount
            )
        )

        donutProgressView.submitData(sections)
        //updateIndicators()
    }

    private fun setRecentUsageBarChart(barChart: BarChart) {

        // 데이터 리스트를 가져옵니다.
        val dataList = getRecentUsage()

        // 막대 그룹의 개수와 간격을 정의합니다.
        val groupCount = dataList.size
        val groupSpace = 0.4f
        val barSpace = 0f
        val barWidth = 1f-groupSpace


        // 막대 데이터를 저장할 리스트를 생성합니다.
        val barEntries1 = ArrayList<BarEntry>()
        val barEntries2 = ArrayList<BarEntry>()
        val barEntries3 = ArrayList<BarEntry>()

        // TODO: 실제 기준값 삽입할 것 (Function을 쓰거나 클래스 내 변수를 불러 오거나)
        val electricityMax = 4000f      // 424g per 1 Kwh
        val waterMax = 300f             // 0.3g per 1 Liter
        val carbonMax = 1786f           // Sum of both

        // 데이터 리스트에서 각 막대의 값을 가져와서 리스트에 추가합니다.
        for (i in dataList.indices) {
            val data = dataList[i]
            barEntries1.add(BarEntry(i.toFloat(), data.electricityUsage / electricityMax * 100f))
            barEntries2.add(BarEntry(i.toFloat(), data.waterUsage/ waterMax * 100f))
            barEntries3.add(BarEntry(i.toFloat(), data.carbonEmission/ carbonMax * 100f))
        }

        // 각 막대 데이터에 대한 색상과 레이블을 설정합니다.
        val barDataSet1 = BarDataSet(barEntries1, "전기 사용량")
        barDataSet1.color = Color.rgb(244, 210, 70)
        val barDataSet2 = BarDataSet(barEntries2, "수도 사용량")
        barDataSet2.color = Color.rgb(100, 193, 222)
        val barDataSet3 = BarDataSet(barEntries3, "탄소 배출량")
        barDataSet3.color = Color.rgb(80, 146,78)

        // 막대 데이터들을 하나의 데이터 세트로 묶습니다.
        val barData = BarData(barDataSet1, barDataSet2, barDataSet3)

        // 막대 그래프에 데이터를 설정
        barChart.data = barData
        // 막대 그룹의 간격과 너비를 조정합니다.
        barChart.groupBars(0.48f, groupSpace, barSpace)
        barChart.barData.barWidth = barWidth

        // X축에 날짜 레이블을 표시하고 Y축의 범위와 간격을 설정합니다.
        val xAxis = barChart.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(dataList.map { it.date })
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.granularity = 1f
        xAxis.labelCount = dataList.size * 7
        xAxis.setCenterAxisLabels(true)
        xAxis.setDrawAxisLine(false)
        xAxis.setDrawGridLines(false)
        xAxis.setDrawLabels(false)
        xAxis.axisMinimum = 0f
        xAxis.axisMaximum = ( groupCount.toFloat() + groupSpace) * 3f
        // TODO: 최근 7일간의 Date 이름 바꾸기 (Textview 이용)

        val leftAxis = barChart.axisLeft
        leftAxis.axisMinimum = 0f
        leftAxis.axisMaximum = 100f
        leftAxis.setDrawGridLines(false)
        leftAxis.setDrawAxisLine(false)
        leftAxis.setDrawLabels(false)

        val rightAxis = barChart.axisRight
        rightAxis.axisMinimum = 0f
        rightAxis.axisMaximum = 100f
        rightAxis.setDrawGridLines(false)
        rightAxis.setDrawAxisLine(false)
        rightAxis.setDrawLabels(false)

        // 범례와 설명을 표시하고 애니메이션 효과를 줍니다.
        barChart.legend.isEnabled = true
        barChart.legend.isWordWrapEnabled = true

        barChart.description.isEnabled = false
//        barChart.animateY(1000)

        barChart.setExtraOffsets(1f, 1f, 1f, 1f)
        // 터치를 막습니다
        barChart.setTouchEnabled(false)

    }

    private fun getRecentUsage(): ArrayList<RecentUsageData> {
        val recentUsageList = ArrayList<RecentUsageData>()
        // TODO: (Database의 최근 6일 사용량 Query 결과 + 오늘 사용량) 넣을 것.
        // Label, Bar 1, 2, 3 가 된다.
        recentUsageList.add(RecentUsageData("5/27", 1203f, 270f, 591f))
        recentUsageList.add(RecentUsageData("5/28", 2806f, 184f, 1244f))
        recentUsageList.add(RecentUsageData("5/29", 842f, 278f, 440f))
        recentUsageList.add(RecentUsageData("5/30", 772f, 210f, 390f))
        recentUsageList.add(RecentUsageData("5/31", 357f, 232f, 220f))
        recentUsageList.add(RecentUsageData("6/1", 1600f, 67f, 698f))
        recentUsageList.add(RecentUsageData("6/2", 187f, 100f, 109f))

        return recentUsageList
    }

    /*
    private fun updateIndicators() {
        amountCapText.text = getString(R.string.amount_cap, donutProgressView.cap)
        amountTotalText.text = getString(
            R.string.amount_total,
            donutProgressView.getData().sumByFloat { it.amount }
        )

        updateIndicatorAmount(ElectricCategory, binding.electricSectionText)
        updateIndicatorAmount(WaterCategory, binding.waterSectionText)
    }
    private fun updateIndicatorAmount(category: DonutDataCategory, textView: TextView) {
        // 실제 구현시에는 TextView의 값을 업데이트 해야 한다.

        donutProgressView.getData()
            .filter { it.name == category.name }
            .sumByFloat { it.amount }
            .also {
                if (it > 0f) {
                    textView.visible()
                    textView.text = getString(R.string.float_2f, it)
                } else {
                    textView.gone()
                }
            }

    }
*/


}
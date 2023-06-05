package kr.co.carboncheck.android.carboncheckapp.view


import android.animation.ValueAnimator
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.text.InputType.TYPE_CLASS_NUMBER
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.recyclerview.widget.LinearLayoutManager
import app.futured.donut.DonutDirection
import app.futured.donut.DonutProgressView
import app.futured.donut.DonutSection
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import kr.co.carboncheck.android.carboncheckapp.R
import kr.co.carboncheck.android.carboncheckapp.adapter.HomeUsageViewPagerAdapter
import kr.co.carboncheck.android.carboncheckapp.adapter.MyUsageViewPagerAdapter
import kr.co.carboncheck.android.carboncheckapp.adapter.TotalUsageRecyclerViewAdapter
import kr.co.carboncheck.android.carboncheckapp.custom.CustomViewPager
import kr.co.carboncheck.android.carboncheckapp.data.model.ElectricCategory
import kr.co.carboncheck.android.carboncheckapp.data.model.WaterCategory
import kr.co.carboncheck.android.carboncheckapp.databinding.FragmentTotalUsageBinding
import kr.co.carboncheck.android.carboncheckapp.dataobject.MemberUsageData
import kr.co.carboncheck.android.carboncheckapp.dataobject.RecentUsageData
import kr.co.carboncheck.android.carboncheckapp.util.NumberFormat
import kr.co.carboncheck.android.carboncheckapp.util.UserPreference
import kr.co.carboncheck.android.carboncheckapp.viewmodel.SharedViewModel

class TotalUsageFragment : Fragment() {
    private var _binding: FragmentTotalUsageBinding? = null
    private val binding get() = _binding!!

    private val memberUsageData = mutableListOf<MemberUsageData>()
    val memberUsageMap = mutableMapOf<String, Int>()

    private lateinit var myContext: Context
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val numberFormat = NumberFormat()
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var adapter: Any
    private lateinit var myUsageViewPager: CustomViewPager
    private lateinit var homeUsageViewPager: CustomViewPager
    private var myTargetAmount = 0f
    private lateinit var userName: String  //= UserPreference().getPreferences(context!!)?.getString("name","")

    override fun onAttach(context: Context) {
        super.onAttach(context)
        myContext = context
        userName = UserPreference().getPreferences(myContext)?.getString("name", "").toString()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
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
        // NavigateButton onClickListener
        setOnclickListenerOnNavigateButton()
        setOnclickListenerOnTotalAmountText()

        // initTotalUsageRecyclerView() 에서 myTargetAmount(탄소) 설정하기에 제일 먼저 실행
        Handler().postDelayed({
            initTotalUsageRecyclerView()
        }, 1000)

        // Donut Chart Setting
        val donutProgressView = binding.donutView
        setDonut(donutProgressView)
        Handler().postDelayed({
            fillDonutInitialData(donutProgressView)
            runInitialDonutAnimation(donutProgressView)
        }, 1000)


        // MemberUsage Setting
//        initializeMemberList()      // DummyData
//        Handler().postDelayed({
//            initTotalUsageRecyclerView()
//        }, 1000)
        // RecentUsage Setting
        val myRecentUsageChartView = binding.myRecentUsageChart
        val homeRecentUsageChartView = binding.homeRecentUsageChart
        setRecentUsageBarChart(myRecentUsageChartView)
        setHomeRecentUsageBarChart(homeRecentUsageChartView)
        // 1. 커스텀 뷰 페이저 어댑터를 설정하자
        val myUsageViewPagerAdapter = MyUsageViewPagerAdapter()
        val homeMyUsageViewPagerAdapter = HomeUsageViewPagerAdapter()

        myUsageViewPager = binding.myUsageCustomViewPager
        homeUsageViewPager = binding.homeUsageCustomViewPager

        // 스와이프를 막음 , 버튼으로만 조작
        myUsageViewPager.setPagingEnabled(false)
        homeUsageViewPager.setPagingEnabled(false)
//        // 2개로 설정 안하면 아이템을 한번만 볼 수 있고 돌아가면 다시는 못 봄
        myUsageViewPager.offscreenPageLimit = 2
        homeUsageViewPager.offscreenPageLimit = 2

        myUsageViewPager.adapter = myUsageViewPagerAdapter
        homeUsageViewPager.adapter = homeMyUsageViewPagerAdapter
    }

    private fun initTotalUsageRecyclerView() {

//        var adapter = TotalUsageRecyclerViewAdapter()     // 어댑터 객체
        sharedViewModel.getGroupTargetValue().observe(viewLifecycleOwner) {
                if (it.isNotEmpty() && memberUsageData.size != 0) {
                    if (memberUsageData[0].userName.equals("계산중")) {
                        memberUsageData.clear()
                    }
                }
                for ((name, target) in it) if (name != null) {
                    if (name == userName) {
                        myTargetAmount = target     // 유저 타겟값 가져옴  TODO:(도넛 차트에서 사용)
                    }
                    memberUsageMap.put(name, memberUsageData.size)
                    memberUsageData.add(MemberUsageData(name, target, 0f))
                }
            }
        sharedViewModel.getGroupWaterUsage().observe(viewLifecycleOwner) {
            if (it.isNotEmpty() && memberUsageData.size != 0) {
                if (memberUsageData[0].userName.equals("계산중")) {
                    memberUsageData.clear()
                }
            }
            for ((key, value) in it) {
                val index = memberUsageMap[key]
                index?.let {
                    index
                    memberUsageData[index] = MemberUsageData(
                        memberUsageData[index].userName,
                        memberUsageData[index].targetAmount,
                        memberUsageData[index].currentAmount + value * 0.3f
                    )
                }
            }
        }
        sharedViewModel.getGroupElectricityUsage().observe(viewLifecycleOwner) {
            if (it.isNotEmpty() && memberUsageData.size != 0) {
                if (memberUsageData[0].userName.equals("계산중")) {
                    memberUsageData.clear()
                }
            }
            for ((key, value) in it) {
                val index = memberUsageMap[key]
                index?.let {
                    index
                    memberUsageData[index] = MemberUsageData(
                        memberUsageData[index].userName,
                        memberUsageData[index].targetAmount,
                        memberUsageData[index].currentAmount + value * 424f / 1000f
                    )
                }
            }
        }


        adapter = TotalUsageRecyclerViewAdapter()
        (adapter as TotalUsageRecyclerViewAdapter).datalist =
            memberUsageData             // TODO: 실제 가족 데이터 불러올 것 (optional: 코루틴 사용할 것)

        binding.homeUsageRecyclerView.adapter =
            adapter as TotalUsageRecyclerViewAdapter   // 뷰에 어댑터 결합

        layoutManager = LinearLayoutManager(activity)
        binding.homeUsageRecyclerView.layoutManager = layoutManager   // 레이아웃 매니저 결합
        binding.homeUsageRecyclerView

    }

    private fun runInitialDonutAnimation(donutProgressView: DonutProgressView) {
        if (!isAdded) return
        ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 10
            interpolator = FastOutSlowInInterpolator()
            addUpdateListener {
                donutProgressView.masterProgress = it.animatedValue as Float
                donutProgressView.alpha = it.animatedValue as Float
            }
            start()
        }
    }

    private fun setDonut(donutProgressView: DonutProgressView) {
        val userTargetAmount = sharedViewModel.getGroupTargetValue().value?.get(userName)
        
        if (myTargetAmount != 0f) {
            // 유저가 키보드로 입력 해서 값 넣어준 상황
            donutProgressView.cap = myTargetAmount      // TODO: 목표 설정량 변경 가능 하게 하기
        } else {
            // 키보드 입력 없이 프레그 먼트를 새로 열거나 sharedViewModel 사용 하는 상황
            if(userTargetAmount != null){
                donutProgressView.cap = userTargetAmount
            }else{
                // sharedViewModel 에도 없는 상황
                donutProgressView.cap = 1000f // 기본값
            }
        }
        donutProgressView.masterProgress = 100f     // 파악 안됨
        donutProgressView.gapAngleDegrees = 270f    // 도넛 구멍 방향
        donutProgressView.direction = DonutDirection.CLOCKWISE
    }

    private fun fillDonutInitialData(donutProgressView: DonutProgressView) {
        if (!isAdded) return

        val context = myContext
        var electricAmount = 0f
        var waterAmount = 0f
        var electricCarbonAmount: Float
        var waterCarbonAmount: Float
        var totalCarbonAmount = 0f

        val sections = mutableListOf<DonutSection>()

        // Observe the user water usage and electricity usage from the sharedViewModel
        sharedViewModel.getUserElectricityUsage()
            .observe(viewLifecycleOwner) { userElectricityUsage ->
                // Update your UI with the new data
                userElectricityUsage?.let {
                    for ((key, value) in it) {
                        Log.d("TotalUsage elec", value.toString())
                        electricAmount += value
                    }
                }
                electricCarbonAmount =
                    numberFormat.electricityUsageToCarbonUsage(electricAmount / 1000f)   // Kwh 단위로 변환 하고 탄소 배출량 계산
                totalCarbonAmount += electricCarbonAmount
                binding.totalAmountCountText.text = totalCarbonAmount.toString() + "g"
                binding.electricSectionText.text =
                    "전력 사용량 " + numberFormat.toKwhString(electricAmount)
                if (context != null) {
                    sections.add(
                        DonutSection(
                            ElectricCategory.name,
                            ContextCompat.getColor(context, R.color.electric),
                            electricCarbonAmount
                        )
                    )
                    donutProgressView.submitData(sections)
                    runInitialDonutAnimation(donutProgressView)
                }
            }
        sharedViewModel.getUserWaterUsage().observe(viewLifecycleOwner) { userWaterUsage ->
            // Update your UI with the new data

            userWaterUsage?.let {
                for ((key, value) in it) {
                    waterAmount += value
                }
            }
            waterCarbonAmount = numberFormat.waterUsageToCarbonUsage(waterAmount)   // 탄소 배출량 으로 계산
            totalCarbonAmount += waterCarbonAmount
            binding.totalAmountCountText.text = totalCarbonAmount.toString() + "g"
            binding.waterSectionText.text = "수도 사용량 " + numberFormat.toLiterString(waterAmount)

            if (context != null) {
                sections.add(
                    DonutSection(
                        WaterCategory.name,
                        ContextCompat.getColor(context, R.color.water),
                        waterCarbonAmount
                    )
                )
            }
        }
        if (context != null) {
            donutProgressView.submitData(sections)
        }
    }

    private fun setRecentUsageBarChart(barChart: BarChart) {
        if (!isAdded) return
        // 데이터 리스트를 가져옵니다.
        val dataList = getRecentUsage()

        // 막대 그룹의 개수와 간격을 정의합니다.
        val groupCount = dataList.size
        val groupSpace = 0.55f
        val barSpace = 0f
        val barWidth = 1f - groupSpace


        // 막대 데이터를 저장할 리스트를 생성합니다.
        val barEntries1 = ArrayList<BarEntry>()
        val barEntries2 = ArrayList<BarEntry>()
        val barEntries3 = ArrayList<BarEntry>()

        // TODO: 실제 기준값 삽입할 것 (Function을 쓰거나 클래스 내 변수를 불러 오거나)
        val electricityMax = 4000f      // 424g per 1 Kwh
        val waterMax = 300f             // 0.3g per 1 Liter
        val carbonMax = 1786f           // Sum of both
//        val format = SimpleDateFormat("MM/dd")


        // 데이터 리스트에서 각 막대의 값을 가져와서 리스트에 추가합니다.
        for (i in dataList.indices) {
            val data = dataList[i]
            val textView = binding.dateLayout.getChildAt(i) as TextView
            textView.text = data.date
            barEntries1.add(BarEntry(i.toFloat(), data.electricityUsage / electricityMax * 100f))
            barEntries2.add(BarEntry(i.toFloat(), data.waterUsage / waterMax * 100f))
            barEntries3.add(BarEntry(i.toFloat(), data.carbonEmission / carbonMax * 100f))
        }

        // 각 막대 데이터에 대한 색상과 레이블을 설정합니다.
        val barDataSet1 = BarDataSet(barEntries1, "전기 사용량")
        barDataSet1.color = Color.rgb(244, 210, 70)
        val barDataSet2 = BarDataSet(barEntries2, "수도 사용량")
        barDataSet2.color = Color.rgb(100, 193, 222)
        val barDataSet3 = BarDataSet(barEntries3, "탄소 배출량")
        barDataSet3.color = Color.rgb(80, 146, 78)

        // 막대 데이터들을 하나의 데이터 세트로 묶습니다.
        val barData = BarData(barDataSet1, barDataSet2, barDataSet3)
        barData.setDrawValues(false)    // 막대 위 퍼센티지 삭제
        // 막대 그래프에 데이터를 설정
        barChart.data = barData
        // 막대 그룹의 간격과 너비를 조정합니다.
        barChart.barData.barWidth = barWidth
        barChart.groupBars(0f, groupSpace, barSpace)

        // X축에 날짜 레이블을 표시하고 Y축의 범위와 간격을 설정합니다.
        val xAxis = barChart.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(dataList.map { it.date })
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.granularity = 0.01f
        xAxis.labelCount = dataList.size * 7

        xAxis.setCenterAxisLabels(false)
        xAxis.setDrawAxisLine(false)
        xAxis.setDrawGridLines(false)
        xAxis.setDrawLabels(false)
        xAxis.axisMinimum = 0f
        xAxis.axisMaximum = (groupCount.toFloat() + groupSpace) * 3f
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

        // 범례와 설명을 제거 하고 애니메이션 효과를 줍니다.
        barChart.legend.isEnabled = false
        barChart.legend.isWordWrapEnabled = false
        barChart.description.isEnabled = false
        barChart.animateY(1000)
        barChart.setVisibleXRange(0f, dataList.size.toFloat() * 1.9f)
        barChart.setExtraOffsets(1f, 1f, 1f, 1f)
        // 터치를 막습니다
        barChart.setTouchEnabled(false)

    }

    private fun setHomeRecentUsageBarChart(barChart: BarChart) {
        if (!isAdded) return
        // 데이터 리스트를 가져옵니다.
        val dataList = getHomeRecentUsage()
        // 막대 그룹의 개수와 간격을 정의합니다.
        val groupCount = dataList.size
        val groupSpace = 0.55f
        val barSpace = 0f
        val barWidth = 1f - groupSpace


        // 막대 데이터를 저장할 리스트를 생성합니다.
        val barEntries1 = ArrayList<BarEntry>()
        val barEntries2 = ArrayList<BarEntry>()
        val barEntries3 = ArrayList<BarEntry>()

        // TODO: 실제 기준값 삽입할 것 (Function을 쓰거나 클래스 내 변수를 불러 오거나)
        val electricityMax = 4000f * 4f     // 424g per 1 Kwh
        val waterMax = 300f * 4f             // 0.3g per 1 Liter
        val carbonMax = 1786f * 4f          // Sum of both
//        val format = SimpleDateFormat("MM/dd")


        // 데이터 리스트에서 각 막대의 값을 가져와서 리스트에 추가합니다.
        for (i in dataList.indices) {
            val data = dataList[i]
            val textView = binding.dateLayout.getChildAt(i) as TextView
            textView.text = data.date
            barEntries1.add(BarEntry(i.toFloat(), data.electricityUsage / electricityMax * 100f))
            barEntries2.add(BarEntry(i.toFloat(), data.waterUsage / waterMax * 100f))
            barEntries3.add(BarEntry(i.toFloat(), data.carbonEmission / carbonMax * 100f))
        }

        // 각 막대 데이터에 대한 색상과 레이블을 설정합니다.
        val barDataSet1 = BarDataSet(barEntries1, "전기 사용량")
        barDataSet1.color = Color.rgb(244, 210, 70)
        val barDataSet2 = BarDataSet(barEntries2, "수도 사용량")
        barDataSet2.color = Color.rgb(100, 193, 222)
        val barDataSet3 = BarDataSet(barEntries3, "탄소 배출량")
        barDataSet3.color = Color.rgb(80, 146, 78)

        // 막대 데이터들을 하나의 데이터 세트로 묶습니다.
        val barData = BarData(barDataSet1, barDataSet2, barDataSet3)
        barData.setDrawValues(false)    // 막대 위 퍼센티지 삭제
        // 막대 그래프에 데이터를 설정
        barChart.data = barData
        // 막대 그룹의 간격과 너비를 조정합니다.
        barChart.barData.barWidth = barWidth
        barChart.groupBars(0f, groupSpace, barSpace)

        // X축에 날짜 레이블을 표시하고 Y축의 범위와 간격을 설정합니다.
        val xAxis = barChart.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(dataList.map { it.date })
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.granularity = 0.01f
        xAxis.labelCount = dataList.size * 7

        xAxis.setCenterAxisLabels(false)
        xAxis.setDrawAxisLine(false)
        xAxis.setDrawGridLines(false)
        xAxis.setDrawLabels(false)
        xAxis.axisMinimum = 0f
        xAxis.axisMaximum = (groupCount.toFloat() + groupSpace) * 3f
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

        // 범례와 설명을 제거 하고 애니메이션 효과를 줍니다.
        barChart.legend.isEnabled = false
        barChart.legend.isWordWrapEnabled = false
        barChart.description.isEnabled = false
        barChart.animateY(1000)
        barChart.setVisibleXRange(0f, dataList.size.toFloat() * 1.9f)
        barChart.setExtraOffsets(1f, 1f, 1f, 1f)
        // 터치를 막습니다
        barChart.setTouchEnabled(false)

    }

    private fun getRecentUsage(): ArrayList<RecentUsageData> {
        val recentUsageList = ArrayList<RecentUsageData>()
        // TODO: (Database의 최근 6일 사용량 Query 결과 + 오늘 사용량) 넣을 것.
        // Label, Bar 1, 2, 3 가 된다.
        recentUsageList.add(RecentUsageData("05/29", 1003f, 270f, 591f))
        recentUsageList.add(RecentUsageData("05/30", 2106f, 184f, 1244f))
        recentUsageList.add(RecentUsageData("05/31", 2842f, 278f, 1140f))
        recentUsageList.add(RecentUsageData("06/01", 8772f, 210f, 500f))
        recentUsageList.add(RecentUsageData("06/02", 357f, 232f, 220f))
        recentUsageList.add(RecentUsageData("06/03", 1800f, 467f, 898f))
        recentUsageList.add(RecentUsageData("06/04", 187f, 100f, 109f))

        return recentUsageList
    }

    private fun getHomeRecentUsage(): ArrayList<RecentUsageData> {
        val recentUsageList = ArrayList<RecentUsageData>()
        // TODO: (Database의 최근 6일 사용량 Query 결과 + 오늘 사용량) 넣을 것.
        // Label, Bar 1, 2, 3 가 된다.
        recentUsageList.add(RecentUsageData("05/29", 24203f, 670f, 10390.87f))
        recentUsageList.add(RecentUsageData("05/30", 45806f, 484f, 19464.14f))
        recentUsageList.add(RecentUsageData("05/31", 33842f, 978f, 14383.68f))
        recentUsageList.add(RecentUsageData("06/01", 46772f, 710f, 19855.96f))
        recentUsageList.add(RecentUsageData("06/02", 21257f, 832f, 9031.248f))
        recentUsageList.add(RecentUsageData("06/03", 114600f, 247f, 48602.4f))
        recentUsageList.add(RecentUsageData("06/04", 1187f, 700f, 503.048f))

        return recentUsageList
    }

    private fun setOnclickListenerOnTotalAmountText() {
        binding.totalAmountCountText.setOnClickListener {
            val builder = AlertDialog.Builder(myContext)
            builder.setTitle("목표 사용량 입력") // 다이얼로그 제목 설정
            val editText = EditText(myContext)
            editText.inputType = TYPE_CLASS_NUMBER
            builder.setView(editText) // 커스텀 뷰로 EditText를 설정

            builder.setPositiveButton("확인") { _, _ ->
                val value = editText.text.toString().toFloatOrNull() // 입력된 텍스트 가져오기
                if (value != null) {
                    // 유저 타겟 배출량 업데이트 하기
                    val currentNameMap = sharedViewModel.getGroupTargetValue().value?.toMutableMap()
                    currentNameMap?.put(userName, value)
                    sharedViewModel.setGroupTargetValue(currentNameMap?.toMap() ?: emptyMap())
                    // 도넛 뷰 다시 로드하기
                    val donutProgressView = binding.donutView
                    setDonut(donutProgressView)
                    Handler().postDelayed({
                        fillDonutInitialData(donutProgressView)
                        runInitialDonutAnimation(donutProgressView)
                    }, 1000)

                }
            }

            builder.setNegativeButton("취소") { dialog, _ ->
                dialog.dismiss() // 다이얼로그 닫기
            }
            builder.create().show() // 다이얼로그 표시

        }

    }

    private fun setOnclickListenerOnNavigateButton() {
        // myUsageCard
        binding.myUsageCardBeforeButton.setOnClickListener {
            if (myUsageViewPager.currentItem == 0) {
                myUsageViewPager.setCurrentItem(1, true)
                binding.myUsageCardText.setText(R.string.my_weekly_usage)
                binding.myUsageCardIcon.setImageResource(R.drawable.leaderboard_48px)
            } else {
                myUsageViewPager.setCurrentItem(0, true)
                binding.myUsageCardText.setText(R.string.my_usage)
                binding.myUsageCardIcon.setImageResource(R.drawable.person_48px)
            }
        }
        binding.myUsageCardNextButton.setOnClickListener {
            if (myUsageViewPager.currentItem == 0) {
                myUsageViewPager.setCurrentItem(1, true)
                binding.myUsageCardText.setText(R.string.my_weekly_usage)
                binding.myUsageCardIcon.setImageResource(R.drawable.leaderboard_48px)
            } else {
                myUsageViewPager.setCurrentItem(0, true)
                binding.myUsageCardText.setText(R.string.my_usage)
                binding.myUsageCardIcon.setImageResource(R.drawable.person_48px)
            }
        }

        // HomeUsageCard
        binding.homeUsageCardBeforeButton.setOnClickListener {
            if (homeUsageViewPager.currentItem == 0) {
                homeUsageViewPager.setCurrentItem(1, true)
                binding.homeUsageCardText.setText(R.string.home_weekly_usage)
                binding.homeUsageCardIcon.setImageResource(R.drawable.leaderboard_48px)
            } else {
                homeUsageViewPager.setCurrentItem(0, true)
                binding.homeUsageCardText.setText(R.string.home_usage)
                binding.homeUsageCardIcon.setImageResource(R.drawable.group_48px)
            }
        }
        binding.homeUsageCardNextButton.setOnClickListener {
            if (homeUsageViewPager.currentItem == 0) {
                homeUsageViewPager.setCurrentItem(1, true)
                binding.homeUsageCardText.setText(R.string.home_weekly_usage)
                binding.homeUsageCardIcon.setImageResource(R.drawable.leaderboard_48px)
            } else {
                homeUsageViewPager.setCurrentItem(0, true)
                binding.homeUsageCardText.setText(R.string.home_usage)
                binding.homeUsageCardIcon.setImageResource(R.drawable.group_48px)
            }
        }
    }
}
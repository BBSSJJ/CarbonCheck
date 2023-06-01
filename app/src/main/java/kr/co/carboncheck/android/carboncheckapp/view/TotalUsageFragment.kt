package kr.co.carboncheck.android.carboncheckapp.view


import android.animation.ValueAnimator
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
import kr.co.carboncheck.android.carboncheckapp.R
import kr.co.carboncheck.android.carboncheckapp.adapter.TotalUsageRecyclerViewAdapter
import kr.co.carboncheck.android.carboncheckapp.dataobject.MemberUsageData

class TotalUsageFragment : Fragment() {
    private var _binding: FragmentTotalUsageBinding? = null
    private val binding get() = _binding!!
    // private val donutProgressView = binding.donutView
    private var electricAmount = 120.4f     // 전기 탄소 배출량
    private var waterAmount = 56.0f        // 수도 탄소 배출량
    private val memberUsageDatas = mutableListOf<MemberUsageData>()

    /*
    private var RecentUsageList = ArrayList<DataUsage>()
    var weeklyData: MutableList<WeeklyInfo> = mutableListOf(
        WeeklyInfo(0, 0L),
        WeeklyInfo(1, 0L),
        WeeklyInfo(2, 0L),
        WeeklyInfo(3, 0L),
        WeeklyInfo(4, 0L),
        WeeklyInfo(5, 0L),
        WeeklyInfo(6, 0L)
    )
    */
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
        setupDonut(donutProgressView)
        // initControls()
        Handler().postDelayed({
            fillDonutInitialData(donutProgressView)
            runInitialDonutAnimation()
        }, 1)

        // MemberUsage Setting
        initializeMemberList()      // TODO: 실제 가족 데이터 불러올 것
        initTotalUsageRecyclerView()
    }
    private fun initTotalUsageRecyclerView(){
        val adapter=TotalUsageRecyclerViewAdapter()     // 어댑터 객체
        adapter.datalist=memberUsageDatas             // TODO: 실제 가족 데이터 불러올 것 (optional: 코루틴 사용할 것)
        binding.homeUsageRecyclerView.adapter=adapter   // 뷰에 어댑터 결합
        binding.homeUsageRecyclerView.layoutManager=LinearLayoutManager(activity)   // 레이아웃 매니저 결합
    }

    private fun initializeMemberList(){
        with(memberUsageDatas){
            // TODO: 여기에 실제 데이터 삽입 하시오 ( 가족 이름, 목표치, 사용량)
            add(MemberUsageData("Lee", 4700f,4600f))
            add(MemberUsageData("GOP", 9750f,2100f))
            add(MemberUsageData("Sung", 6420f,3500f))
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

    private fun setupDonut(donutProgressView: DonutProgressView) {
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
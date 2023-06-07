package kr.co.carboncheck.android.carboncheckapp.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import kr.co.carboncheck.android.carboncheckapp.R
import kr.co.carboncheck.android.carboncheckapp.adapter.MissionRankViewPagerAdapter
import kr.co.carboncheck.android.carboncheckapp.adapter.RankRecyclerViewAdapter
import kr.co.carboncheck.android.carboncheckapp.adapter.TotalRankViewPagerAdapter
import kr.co.carboncheck.android.carboncheckapp.custom.CustomViewPager
import kr.co.carboncheck.android.carboncheckapp.databinding.FragmentDetailedUsageBinding
import kr.co.carboncheck.android.carboncheckapp.databinding.FragmentMiniGameBinding
import kr.co.carboncheck.android.carboncheckapp.dataobject.MemberUsageData
import kr.co.carboncheck.android.carboncheckapp.dataobject.RankData

class MiniGameFragment : Fragment() {
    private var _binding: FragmentMiniGameBinding? = null
    private val binding get() = _binding!!
    private val rankUserData = mutableListOf<RankData>()
    private lateinit var totalRankViewPager: CustomViewPager
    private lateinit var missionRankViewPager: CustomViewPager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMiniGameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        initializeRankList()
//        initRankRecyclerView()
//        val totalRankChart = binding.totalRankChart
//        val missionRankChart = binding.missionRankChart
        setOnclickListenerOnNavigateButton()
        // Set content of Chart
        val totalRankViewPagerAdapter = TotalRankViewPagerAdapter()
        val missionRankViewPagerAdapter = MissionRankViewPagerAdapter()
        totalRankViewPager = binding.totalRankCustomViewPager
        missionRankViewPager = binding.missionRankCustomViewPager
        // Swipe disable
        totalRankViewPager.setPagingEnabled(false)
        missionRankViewPager.setPagingEnabled(false)
        totalRankViewPager.offscreenPageLimit = 2
        missionRankViewPager.offscreenPageLimit = 2

        totalRankViewPager.adapter = totalRankViewPagerAdapter
        missionRankViewPager.adapter = missionRankViewPagerAdapter


    }

    private fun setOnclickListenerOnNavigateButton() {
        // Total Rank card
        binding.totalRankCardBeforeButton.setOnClickListener {
            if (totalRankViewPager.currentItem == 0) {
                totalRankViewPager.setCurrentItem(1, true)
                binding.totalRankCardText.text = "월간 배출량 랭킹"
//                binding.totalRankCardIcon.setImageResource(R.drawable.leaderboard_48px)
            } else {
                totalRankViewPager.setCurrentItem(0, true)
                binding.totalRankCardText.text = "주간 배출량 랭킹"
            }
        }
        binding.totalRankCardNextButton.setOnClickListener {
            if (totalRankViewPager.currentItem == 0) {
                totalRankViewPager.setCurrentItem(1, true)
                binding.totalRankCardText.text = "월간 배출량 랭킹"
//                binding.totalRankCardIcon.setImageResource(R.drawable.leaderboard_48px)
            } else {
                totalRankViewPager.setCurrentItem(0, true)
                binding.totalRankCardText.text = "주간 배출량 랭킹"
            }
        }
        binding.missionRankCardBeforeButton.setOnClickListener {
            if (missionRankViewPager.currentItem == 0) {
                missionRankViewPager.setCurrentItem(1, true)
                binding.missionRankCardText.text = "월간 미션 달성 랭킹"
            } else {
                missionRankViewPager.setCurrentItem(0, true)
                binding.missionRankCardText.text = "주간 미션 달성 랭킹"
            }
        }
        binding.missionRankCardNextButton.setOnClickListener {
            if (missionRankViewPager.currentItem == 0) {
                missionRankViewPager.setCurrentItem(1, true)
                binding.missionRankCardText.text = "월간 미션 달성 랭킹"
            } else {
                missionRankViewPager.setCurrentItem(0, true)
                binding.missionRankCardText.text = "주간 미션 달성 랭킹"
            }
        }

    }
//    private fun initRankRecyclerView(){
//        val adapter = RankRecyclerViewAdapter()
//        adapter.datalist = rankUserData
//        binding.rankRecyclerView.adapter = adapter
//        binding.rankRecyclerView.layoutManager = LinearLayoutManager(activity)
//    }
//    private fun initializeRankList() {
//        with(rankUserData) {
//            // TODO: 여기에 실제 데이터 삽입 하시오 ( 이름, 목표치, 사용량)
//            add(RankData(1, "카본체크", 900f))
//            add(RankData(2, "체크카본", 870f))
//            add(RankData(5, "카봇체크", 850f))
//            add(RankData(3, "체크체크", 810f))
//            add(RankData(6, "카본카본", 800f))
//            add(RankData(4, "카카크크", 750f))
//            add(RankData(9, "카본카본카본체크", 699f))
//            add(RankData(7, "체크체크체크", 680f))
//            add(RankData(8, "체크셔츠", 630f))
//        }


}
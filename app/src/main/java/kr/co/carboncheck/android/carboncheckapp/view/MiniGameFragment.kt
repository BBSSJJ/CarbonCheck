package kr.co.carboncheck.android.carboncheckapp.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import kr.co.carboncheck.android.carboncheckapp.R
import kr.co.carboncheck.android.carboncheckapp.adapter.RankRecyclerViewAdapter
import kr.co.carboncheck.android.carboncheckapp.databinding.FragmentDetailedUsageBinding
import kr.co.carboncheck.android.carboncheckapp.databinding.FragmentMiniGameBinding
import kr.co.carboncheck.android.carboncheckapp.dataobject.MemberUsageData
import kr.co.carboncheck.android.carboncheckapp.dataobject.RankData

class MiniGameFragment : Fragment() {
    private var _binding: FragmentMiniGameBinding? = null
    private val binding get() = _binding!!
    private val rankUserData = mutableListOf<RankData>()

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
        initializeRankList()
        initRankRecyclerView()
    }
    private fun initRankRecyclerView(){
        val adapter = RankRecyclerViewAdapter()
        adapter.datalist = rankUserData
        binding.rankRecyclerView.adapter = adapter
        binding.rankRecyclerView.layoutManager = LinearLayoutManager(activity)
    }
    private fun initializeRankList() {
        with(rankUserData) {
            // TODO: 여기에 실제 데이터 삽입 하시오 ( 가족 이름, 목표치, 사용량)
            add(RankData(1, "카본체크", 900f))
            add(RankData(2, "체크카본", 870f))
            add(RankData(5, "카봇체크", 850f))
            add(RankData(3, "체크체크", 810f))
            add(RankData(6, "카본카본", 800f))
            add(RankData(4, "카카크크", 750f))
            add(RankData(9, "카본카본카본체크", 699f))
            add(RankData(7, "체크체크체크", 680f))
            add(RankData(8, "체크셔츠", 630f))
        }
    }

}
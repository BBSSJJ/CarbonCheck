package kr.co.carboncheck.android.carboncheckapp.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import kr.co.carboncheck.android.carboncheckapp.adapter.SolutionRecyclerViewAdapter
import kr.co.carboncheck.android.carboncheckapp.databinding.FragmentSolutionBinding
import kr.co.carboncheck.android.carboncheckapp.dataobject.SolutionData

class SolutionFragment : Fragment() {
    private var _binding: FragmentSolutionBinding? = null
    private val binding get() = _binding!!
    private val solutionData = mutableListOf<SolutionData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSolutionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeSolutionList()
        initSolutionListRecyclerView()
    }

    private fun initSolutionListRecyclerView() {
        val adapter = SolutionRecyclerViewAdapter()
        adapter.datalist = solutionData
        binding.solutionRecyclerView.adapter = adapter
        binding.solutionRecyclerView.layoutManager = LinearLayoutManager(activity)
    }

    private fun initializeSolutionList() {
        with(solutionData) {
            // TODO: 여기에 실제 데이터 삽입 하시오 (미션 타이틀, 내용, 수반되는 기대효과 {수도, 전기, 비용, 탄소})
            add(SolutionData("외출시 불 끄기", "작은 실천이 큰 변화를 만듭니다", 30f, 1.2f, 160, 508.8f, false))
            add(SolutionData("냉방 온도 1도 낮추기", "전력 사용량이 줄어듭니다", 0f, 0.31f, 40, 131f, true))
            add(SolutionData("세탁기 절수모드로 사용하기", "수도 사용량을 줄일 수 있습니다", 30f, 0f, 300, 0.007f, true))
            add(SolutionData("샤워시간 5분 줄이기", "수도와 전력 사용량을 동시에 줄일 수 있습니다.", 20f, 0.33f, 2300, 89.08f, false))
            add(SolutionData("스마트폰 과충전 하지 않기", "스마트폰 수명이 늘어납니다.", 0f, 25.4f, 500, 508.8f, false))
        }
    }

}
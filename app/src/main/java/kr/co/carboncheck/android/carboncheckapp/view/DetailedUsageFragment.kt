package kr.co.carboncheck.android.carboncheckapp.view

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.coroutines.launch
import kr.co.carboncheck.android.carboncheckapp.R
import kr.co.carboncheck.android.carboncheckapp.adapter.DetailedRecyclerAdapter
import kr.co.carboncheck.android.carboncheckapp.database.CarbonCheckLocalDatabase
import kr.co.carboncheck.android.carboncheckapp.databinding.FragmentDetailedUsageBinding
import kr.co.carboncheck.android.carboncheckapp.dataobject.DetailData
import kr.co.carboncheck.android.carboncheckapp.network.RetrofitClient
import kr.co.carboncheck.android.carboncheckapp.util.NumberFormat
import kr.co.carboncheck.android.carboncheckapp.viewmodel.SharedViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailedUsageFragment : Fragment() {
    private var _binding: FragmentDetailedUsageBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private lateinit var localDatabase: CarbonCheckLocalDatabase
    val numberFormat = NumberFormat()
    private var detailList = mutableListOf<DetailData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailedUsageBinding.inflate(inflater, container, false)
        localDatabase = CarbonCheckLocalDatabase.getInstance(requireContext())

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        inflateCardView()
        initializeDetailList()
        initDetailListRecyclerView()


    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initDetailListRecyclerView() {
        val adapter = DetailedRecyclerAdapter(requireContext(), sharedViewModel)
        adapter.datalist = detailList
//
//        sharedViewModel.getUserWaterUsage().observe(viewLifecycleOwner) { map ->
//            if (map.isNotEmpty()) {
//                for ((key, value) in map) {
//                    var place = "세면대"
//                    if (key == "FLOW1") place = "세면대"
//                    else if (key == "FLOW2") place = "샤워기"
//                    with(detailList) {
//                        add(
//                            DetailData(
//                                place,
//                                0,
//                                0,
//                                numberFormat.toLiterString(value),
//                                numberFormat.waterUsageToCarbonUsageString(value),
//                                numberFormat.waterUsageToPriceString(value),
//                                false,
//                                null
//                            )
//                        )
//
//                    }
//                }
//            }
//            adapter.datalist = detailList
//        }
//
//        sharedViewModel.getUserElectricityUsageName().observe(viewLifecycleOwner) { map ->
//            for ((key, value) in map) {
//                val plugId = key
//                var plugName = value.first
//                val amount = value.second
//                with(detailList) {
//                    add(
//                        DetailData(
//                            plugName, 1, 1, numberFormat.toKwhString(amount),
//                            numberFormat.electricityUsageToCarbonUsageString(amount),
//                            numberFormat.electricityToPriceString(amount),
//                            false,
//                            plugId
//                        )
//                    )
//                }
//            }
//            with(detailList) {
//                add(DetailData("", 0, 0, "", "", "", true, null))
//            }
//            adapter.datalist = detailList
//        }
        binding.detailListRecyclerview.adapter = adapter
        binding.detailListRecyclerview.layoutManager = GridLayoutManager(activity, 2)
    }

    private fun initializeDetailList() {
        val localDatabase = CarbonCheckLocalDatabase.getInstance(requireContext())
        val plugDao = localDatabase.plugDao()
        val waterUsage = sharedViewModel.getUserWaterUsage().value
        Log.d("testlog", waterUsage.toString())

//        if (waterUsage != null) {
//            if (waterUsage.isEmpty())
//                with(detailList) {
//                    add(DetailData("세면대", 0, 0, "0.0 L", "0.0 g", "0 ₩", false, null))
//                    add(DetailData("샤워기", 0, 0, "0.0 L", "0.0 g", "0 ₩", false, null))
//                }
//        }

        if (waterUsage != null) {
            if (waterUsage.isNotEmpty()) {
                for ((key, value) in waterUsage) {
                    var place = "세면대"
                    if (key == "FLOW1") place = "세면대"
                    else if (key == "FLOW2") place = "샤워기"
                    with(detailList) {
                        add(
                            DetailData(
                                place,
                                0,
                                0,
                                numberFormat.toLiterString(value),
                                numberFormat.waterUsageToCarbonUsageString(value),
                                numberFormat.waterUsageToPriceString(value),
                                false,
                                null
                            )
                        )

                    }
                }
            } else {
                with(detailList) {
                    add(DetailData("세면대", 0, 0, "0.0 L", "0.0 g", "0 ₩", false, null))
                    add(DetailData("샤워기", 0, 0, "0.0 L", "0.0 g", "0 ₩", false, null))
                }
            }
        }

        sharedViewModel.getUserElectricityUsageName().observe(viewLifecycleOwner){map->
            for((key, value) in map){
                val plugId = key
                val plugName = value.first
                val amount = value.second
                with(detailList) {
                    add(
                        DetailData(
                            plugName, 1, 1, numberFormat.toKwhString(amount),
                            numberFormat.electricityUsageToCarbonUsageString(amount),
                            numberFormat.electricityToPriceString(amount),
                            false,
                            plugId
                        )
                    )
                }
            }
            with(detailList) {
                add(DetailData("", 0, 0, "", "", "", true, null))
            }

        }


    }

/*
    private fun cardClickMenu(view: View, plugId: String) {
        val builder = AlertDialog.Builder(context!!)
        builder.setTitle("설정") // 다이얼로그 제목 설정
        val choices = arrayOf("이름 변경", "삭제")
        builder.setItems(choices) { dialog, which ->
            when (which) {
                0 -> {
                    //이름 변경
                    dialog.dismiss() // 다이얼로그 닫기
                    showTextInputDialog(view, plugId) // 텍스트 입력창 띄우기
                }
                1 -> {
                    // 삭제
                    dialog.dismiss() // 다이얼로그 닫기
                    showAlertDialog(view, plugId)
                }
            }
        }
        builder.create().show() // 다이얼로그 표시
    }

    private fun showTextInputDialog(view: View, plugId: String) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("텍스트 입력") // 다이얼로그 제목 설정
        val editText = EditText(context)
        builder.setView(editText) // 커스텀 뷰로 EditText를 설정
        builder.setPositiveButton("확인") { dialog, which ->
            val newName = editText.text.toString() // 입력된 텍스트 가져오기

            lifecycleScope.launch {
                val plugDao = localDatabase.plugDao()
                val plug = plugDao.findById(plugId) // plugId로 저장된 플러그 찾아와서
                plug?.plugName = newName //플러그 이름 변경 후
                plugDao.updatePlug(plug!!) //플러그 다시 저장
            }


            view.findViewById<TextView>(R.id.place_text).text = newName //바로 업데이트
        }
        builder.setNegativeButton("취소") { dialog, which ->
            dialog.dismiss() // 다이얼로그 닫기
        }
        builder.create().show() // 다이얼로그 표시
    }

    private fun showAlertDialog(view: View, plugId: String) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("정말 삭제하시겠습니까?") // 다이얼로그 제목 설정
        builder.setItems(arrayOf("예", "아니오")) { dialog, which ->
            // 선택지를 클릭했을 때 실행되는 코드
            when (which) {
                0 -> {
                    //삭제
                    //서버에 삭제 요청
                    sendDeletePlugRequest(plugId) { result ->
                        if (result == true) {
                            Toast.makeText(context, "삭제 성공", Toast.LENGTH_LONG)
                                .show()
                            //로컬 db에서 삭제
                            lifecycleScope.launch {
                                val plugDao = localDatabase.plugDao()
                                val plug = plugDao.findById(plugId)
                                if (plug != null) {
                                    plugDao.deletePlug(plug)
                                }
                            }

                            val currentNameMap = sharedViewModel.getUserElectricityUsageName().value?.toMutableMap()
                            currentNameMap?.remove(plugId)
                            sharedViewModel.setUserElectricityUsageName(currentNameMap?.toMap() ?: emptyMap())

                            val currentMap = sharedViewModel.getUserElectricityUsage().value?.toMutableMap()
                            currentMap?.remove(plugId)
                            sharedViewModel.setUserElectricityUsage(currentMap?.toMap() ?: emptyMap())
                        }
                        if (result == false) Toast.makeText(context, "삭제 실패", Toast.LENGTH_LONG)
                            .show()
                    }

                    val parentView = view.parent as? ViewGroup //클릭한 뷰에서 부모 뷰 가져오기
                    parentView?.removeView(view)
                }
                1 -> {
                    dialog.dismiss()
                }
            }
        }
        builder.create().show() // 다이얼로그 표시
    }

    private fun sendDeletePlugRequest(plugId: String, callback: (Boolean?) -> Unit) {
        Log.d("testlog", "in sendDeletePlugRequest")
        val call = RetrofitClient.deviceService.deletePlugRequest(plugId)
        call.enqueue(object : Callback<Boolean> {
            override fun onResponse(
                call: Call<Boolean>,
                response: Response<Boolean>
            ) {
                if (response.isSuccessful) {
                    val result = response.body()
                    Log.d("testlog", "플러그 삭제 응답 도착")
                    callback(result)
                } else {
                    Log.d("testlog", "플러그 삭제 실패")
                    callback(null)
                }
            }

            override fun onFailure(call: Call<Boolean>, t: Throwable) {
                Log.e("testlog", "플러그 삭제 요청 실패: " + t.message)
                callback(null)
            }
        })
    }
    */

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}
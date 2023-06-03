package kr.co.carboncheck.android.carboncheckapp.view

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kr.co.carboncheck.android.carboncheckapp.R
import kr.co.carboncheck.android.carboncheckapp.database.CarbonCheckLocalDatabase
import kr.co.carboncheck.android.carboncheckapp.databinding.FragmentDetailedUsageBinding
import kr.co.carboncheck.android.carboncheckapp.entity.Plug
import kr.co.carboncheck.android.carboncheckapp.network.RetrofitClient
import kr.co.carboncheck.android.carboncheckapp.viewmodel.SharedViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kr.co.carboncheck.android.carboncheckapp.util.NumberFormat

class DetailedUsageFragment : Fragment() {
    private var _binding: FragmentDetailedUsageBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private lateinit var localDatabase: CarbonCheckLocalDatabase
    val numberFormat = NumberFormat()

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
        inflateCardView()
    }

    private fun inflateCardView() {
        val localDatabase = CarbonCheckLocalDatabase.getInstance(requireContext())
        val plugDao = localDatabase.plugDao()
        val detailedUsageGridLayout = binding.detailedUsageGridLayout
        val inflater = LayoutInflater.from(context)
        val displayMetrics = resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels
        val scale = resources.displayMetrics.density
        val marginInDp = (8 * scale + 0.5f).toInt() // 16dp를 픽셀로 변환
        val cardViewWidth = screenWidth / 2 - marginInDp

        //물 사용량 카드
        val waterUsage = sharedViewModel.getUserWaterUsage().value
        if (waterUsage != null) {
            if (waterUsage!!.isNotEmpty()) {
                for ((key, value) in waterUsage) {
                    val cardView = inflater.inflate(
                        R.layout.detailed_usage_card_view,
                        detailedUsageGridLayout,
                        false
                    )
                    val params = GridLayout.LayoutParams().apply {
                        width = cardViewWidth
                        height = GridLayout.LayoutParams.WRAP_CONTENT
                    }
                    cardView.layoutParams = params

                    val placeText = cardView.findViewById<TextView>(R.id.place_text)
                    val placeImage = cardView.findViewById<ImageView>(R.id.place_image)
                    val waterOrElectricityImage =
                        cardView.findViewById<ImageView>(R.id.water_or_electricity_image)
                    val waterOrElectricityText =
                        cardView.findViewById<TextView>(R.id.water_or_eletricity_usage_text)
                    val carbonUsageText = cardView.findViewById<TextView>(R.id.carbon_usage_text)
                    val costText = cardView.findViewById<TextView>(R.id.cost_text)

                    placeText.text = key
                    placeImage.setImageResource(R.drawable.faucet)
                    waterOrElectricityImage.setImageResource(R.drawable.water_drop)
                    waterOrElectricityText.text = numberFormat.toLiterString(value)
                    carbonUsageText.text = numberFormat.waterUsageToCarbonUsageString(value)
                    costText.text = numberFormat.waterUsageToPriceString(value)

                    detailedUsageGridLayout.addView(cardView)
                }
            } else {  //수도 사용량은 없을 때에 가져올 수가 없음(left join을 못해서)
                for (i in 1..2) {
                    val cardView = inflater.inflate(
                        R.layout.detailed_usage_card_view,
                        detailedUsageGridLayout,
                        false
                    )
                    val params = GridLayout.LayoutParams().apply {
                        width = cardViewWidth
                        height = GridLayout.LayoutParams.WRAP_CONTENT
                    }
                    cardView.layoutParams = params
                    val placeText = cardView.findViewById<TextView>(R.id.place_text)
                    placeText.text = if (i == 1) "세면대" else "샤워기";

                    detailedUsageGridLayout.addView(cardView)
                }
            }
        }

        //전기 사용량 카드
        val electricityUsage = sharedViewModel.getUserElectricityUsage().value
        Log.d("testlog", "in lifecycle")
        if (electricityUsage != null) {
            if (electricityUsage!!.isNotEmpty()) {
                for ((key, value) in electricityUsage) {
                    val cardView = inflater.inflate(
                        R.layout.detailed_usage_card_view,
                        detailedUsageGridLayout,
                        false
                    )
                    val params = GridLayout.LayoutParams().apply {
                        width = cardViewWidth
                        height = GridLayout.LayoutParams.WRAP_CONTENT
                    }
                    cardView.layoutParams = params

                    val placeText = cardView.findViewById<TextView>(R.id.place_text)
                    val placeImage = cardView.findViewById<ImageView>(R.id.place_image)
                    val waterOrElectricityImage =
                        cardView.findViewById<ImageView>(R.id.water_or_electricity_image)
                    val waterOrElectricityText =
                        cardView.findViewById<TextView>(R.id.water_or_eletricity_usage_text)
                    val carbonUsageText =
                        cardView.findViewById<TextView>(R.id.carbon_usage_text)
                    val costText = cardView.findViewById<TextView>(R.id.cost_text)

                    val plug: Plug?
                    runBlocking {
                        plug = plugDao.findById(key)
                        if (plug == null) {
                            //db에 등록되지 않았음
                            placeText.text = "등록되지 않은 플러그"
                        } else {
                            placeText.text = plug.plugName
                        }
                    }
                    placeImage.setImageResource(R.drawable.power)
                    waterOrElectricityImage.setImageResource(R.drawable.bolt)
                    waterOrElectricityText.text = numberFormat.toKwhString(value)
                    carbonUsageText.text = numberFormat.electricityUsageToCarbonUsageString(value)
                    costText.text = numberFormat.electricityToPriceString(value)

                    //플러그 이름 변경, 삭제를 위한 온클릭이벤트
                    cardView.setOnClickListener {
                        cardClickMenu(cardView, key)
                    }
                    detailedUsageGridLayout.addView(cardView)
                }
            }
        }
        //마지막 추가 버튼
        val cardView = inflater.inflate(
            R.layout.detailed_usage_add_card_view,
            detailedUsageGridLayout,
            false
        )
        val params = GridLayout.LayoutParams().apply {
            width = cardViewWidth
            height = GridLayout.LayoutParams.WRAP_CONTENT
        }
        cardView.layoutParams = params
        val addImage = cardView.findViewById<ImageView>(R.id.add_image)
        addImage.setImageResource(R.drawable.add_circle_60px)
        cardView.setOnClickListener {
            val intent = Intent(activity, QrcodeScanActivity::class.java)
            intent.putExtra("ACTION", "REGISTER_PLUG")
            startActivity(intent)
        }
        detailedUsageGridLayout.addView(cardView)


    }

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
                        if (result == true) Toast.makeText(context, "삭제 성공", Toast.LENGTH_LONG)
                            .show()
                        if (result == false) Toast.makeText(context, "삭제 실패", Toast.LENGTH_LONG)
                            .show()
                    }

                    //로컬 db에서 삭제
                    lifecycleScope.launch {
                        val plugDao = localDatabase.plugDao()
                        val plug = plugDao.findById(plugId) // plugId로 저장된 플러그 찾아와서
                        if (plug == null) {

                        } else {
                            plugDao.deletePlug(plug!!)
                        }
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}
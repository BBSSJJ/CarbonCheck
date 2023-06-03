package kr.co.carboncheck.android.carboncheckapp.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import kr.co.carboncheck.android.carboncheckapp.R
import kr.co.carboncheck.android.carboncheckapp.databinding.FragmentDetailedUsageBinding
import kr.co.carboncheck.android.carboncheckapp.viewmodel.SharedViewModel

class DetailedUsageFragment : Fragment() {
    private var _binding: FragmentDetailedUsageBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: SharedViewModel by activityViewModels()


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

        inflateCardView()


        return binding.root
    }

    @SuppressLint("MissingInflatedId")
    private fun inflateCardView() {
        val detailedUsageGridLayout = binding.detailedUsageGridLayout
        val inflater = LayoutInflater.from(context)
        val displayMetrics = resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels
        val scale = resources.displayMetrics.density
        val marginInDp = (8 * scale + 0.5f).toInt() // 16dp를 픽셀로 변환
        val cardViewWidth = screenWidth / 2 - marginInDp

        //물 사용량 카드
        val waterUsage = sharedViewModel.getUserWaterUsage().value
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
                waterOrElectricityImage.setImageResource(R.drawable.humidity_low)
                waterOrElectricityText.text = value.toString() + " L"
                carbonUsageText.text = value.toString() + " g"
                costText.text = value.toString() + " ₩"

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

        //전기 사용량 카드
        val electricityUsage = sharedViewModel.getUserElectricityUsage().value
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
                val carbonUsageText = cardView.findViewById<TextView>(R.id.carbon_usage_text)
                val costText = cardView.findViewById<TextView>(R.id.cost_text)

                placeText.text = "플러그"
                placeImage.setImageResource(R.drawable.power)
                waterOrElectricityImage.setImageResource(R.drawable.bolt)
                waterOrElectricityText.text = value.toString() + " Wh"
                carbonUsageText.text = value.toString() + " g"
                costText.text = value.toString() + " ₩"

                detailedUsageGridLayout.addView(cardView)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
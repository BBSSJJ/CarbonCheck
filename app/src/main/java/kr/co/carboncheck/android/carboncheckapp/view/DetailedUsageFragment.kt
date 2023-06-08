package kr.co.carboncheck.android.carboncheckapp.view

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
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
        initializeDetailList()
        if (isAdded) {
            if(binding != null){

                binding.progressBar.visibility = View.VISIBLE
            }
        }
        Handler().postDelayed({
            initDetailListRecyclerView()
            if (binding != null) {
                binding.progressBar.visibility = View.INVISIBLE
                binding.totalDetailedUsageCard.visibility = View.VISIBLE
            }
        }, 5000)


    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initDetailListRecyclerView() {
        if (!isAdded) return
        val adapter = DetailedRecyclerAdapter(requireContext(), sharedViewModel)
        adapter.datalist = detailList

        binding.detailListRecyclerview.adapter = adapter
        binding.detailListRecyclerview.layoutManager = GridLayoutManager(activity, 2)
    }

    private fun initializeDetailList() {
        if (!isAdded) return
        val waterUsage = sharedViewModel.getUserWaterUsage().value
        val electricityUsageName = sharedViewModel.getUserElectricityUsageName().value
        var waterUsageSum = 0f
        var electricityUsageSum = 0f

        detailList.clear()

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
                    waterUsageSum += value
                }
            } else {
                with(detailList) {
                    add(DetailData("세면대", 0, 0, "0.0 L", "0.0 g", "0 ₩", false, null))
                    add(DetailData("샤워기", 0, 0, "0.0 L", "0.0 g", "0 ₩", false, null))
                }
            }
        }

        electricityUsageName?.let { map ->
            for ((key, value) in map) {
                val plugId = key
                val plugName = value.first
                val amount = value.second
                detailList.add(
                    DetailData(
                        plugName,
                        1,
                        1,
                        numberFormat.toKwhString(amount),
                        numberFormat.electricityUsageToCarbonUsageString(amount),
                        numberFormat.electricityToPriceString(amount),
                        false,
                        plugId
                    )
                )
                electricityUsageSum += value.second
            }
        }

        // Add an empty item for spacing
        detailList.add(
            DetailData(
                "", 0, 0, "", "", "", true, null
            )
        )

        binding.detailListRecyclerview.adapter?.notifyDataSetChanged()

        changeTotalCardView(waterUsageSum, electricityUsageSum)
    }

    private fun changeTotalCardView(waterUsageSum: Float, electricityUsageSum: Float) {
        binding.totalDetailedWaterUsageText.text = NumberFormat().toLiterString(waterUsageSum)
        binding.totalDetailedElectricityUsageText.text =
            NumberFormat().toKwhString(electricityUsageSum)
        binding.totalDetailedCarbonUsageText.text =
            NumberFormat().totalCarbonUsageString(waterUsageSum, electricityUsageSum)
        binding.totalDetailedCostText.text =
            NumberFormat().totalPriceString(waterUsageSum, electricityUsageSum)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}
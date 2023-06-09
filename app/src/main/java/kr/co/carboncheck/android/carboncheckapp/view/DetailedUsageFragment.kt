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
import androidx.lifecycle.LifecycleOwner
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
        val viewLifecycleObserver = Observer<LifecycleOwner> {
            initDetailListRecyclerView()
            if (binding != null) {
                binding.progressBar.visibility = View.INVISIBLE
                binding.totalDetailedUsageCard.visibility = View.VISIBLE
            }
        }
        viewLifecycleOwnerLiveData.observe(viewLifecycleOwner, viewLifecycleObserver)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeDetailList()
        if (isAdded) {
            if (binding != null) {

                binding.progressBar.visibility = View.VISIBLE
            }
        }
//        Handler().postDelayed({
//            initDetailListRecyclerView()
//            if (binding != null) {
//                binding.progressBar.visibility = View.INVISIBLE
//                binding.totalDetailedUsageCard.visibility = View.VISIBLE
//            }
//        }, 5000)

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
                    with(detailList) {
                        add(
                            DetailData(key, 0, 0, value, false, null)
                        )
                    }
                    waterUsageSum += value
                }
            }
//            else {
//                with(detailList) {
//                    add(DetailData("세면대", 0, 0, 0f, false, null))
//                    add(DetailData("샤워기", 0, 0, 0f, false, null))
//                }
//            }
        }

        electricityUsageName?.let { map ->
            for ((key, value) in map) {
                val plugId = key
                val plugName = value.first
                val amount = value.second
                detailList.add(
                    DetailData(plugName, 1, 1, amount, false, plugId)
                )
                electricityUsageSum += value.second
            }
        }

        // Add an empty item for spacing
        detailList.add(
            DetailData("", 0, 0, 0f, true, null)
        )

        binding.detailListRecyclerview.adapter?.notifyDataSetChanged()

        changeTotalCardView(waterUsageSum, electricityUsageSum)
        sharedViewModel.getUserElectricityUsage()
            .observe(viewLifecycleOwner) { updatedData ->


                electricityUsageSum = 0f
                waterUsageSum = 0f
                if (updatedData != null) {
                    for ((key, value) in updatedData) {
                        electricityUsageSum += value
                    }
                    val waterUsageMap = sharedViewModel.getUserWaterUsage().value
                    for ((key, value) in waterUsageMap!!) {
                        waterUsageSum += value
                    }
                    changeTotalCardView(waterUsageSum, electricityUsageSum)
                }
            }

        sharedViewModel.getUserWaterUsage()
            .observe(viewLifecycleOwner) { updatedData ->
                electricityUsageSum = 0f
                waterUsageSum = 0f
                if (updatedData != null) {
                    val electricityUsageMap = sharedViewModel.getUserElectricityUsage().value
                    for ((key, value) in electricityUsageMap!!) {
                        electricityUsageSum += value
                    }
                    for ((key, value) in updatedData) {
                        waterUsageSum += value
                    }
                    changeTotalCardView(waterUsageSum, electricityUsageSum)
                }
            }
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

        sharedViewModel.getUserElectricityUsage().removeObservers(viewLifecycleOwner)
        sharedViewModel.getUserWaterUsage().removeObservers(viewLifecycleOwner)

        _binding = null
    }
}
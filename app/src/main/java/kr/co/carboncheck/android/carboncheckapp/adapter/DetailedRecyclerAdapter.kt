package kr.co.carboncheck.android.carboncheckapp.adapter

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.utils.Utils.init
import kotlinx.coroutines.*
import kr.co.carboncheck.android.carboncheckapp.R
import kr.co.carboncheck.android.carboncheckapp.database.CarbonCheckLocalDatabase
import kr.co.carboncheck.android.carboncheckapp.databinding.DetailedListBinding
import kr.co.carboncheck.android.carboncheckapp.dataobject.DetailData
import kr.co.carboncheck.android.carboncheckapp.network.RetrofitClient
import kr.co.carboncheck.android.carboncheckapp.view.QrcodeScanActivity
import kr.co.carboncheck.android.carboncheckapp.viewmodel.SharedViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat


class DetailedRecyclerAdapter(var context: Context, private val sharedViewModel: SharedViewModel) :
    RecyclerView.Adapter<DetailedRecyclerAdapter.MyViewHolder>() {
    private lateinit var localDatabase: CarbonCheckLocalDatabase

    var datalist = mutableListOf<DetailData>()
    val numberFormat = kr.co.carboncheck.android.carboncheckapp.util.NumberFormat()

    inner class MyViewHolder(
        private val binding: DetailedListBinding,
    ) :
        RecyclerView.ViewHolder(binding.root) {
        /////////////
        private lateinit var currentData: DetailData // 현재 ViewHolder에 바인딩된 데이터
        private var currentPosition: Int = -1 // 현재 ViewHolder의 위치


        ////////////
        fun bind(detailData: DetailData) {
            /////////////
            currentData = detailData // 현재 데이터 설정
            currentPosition = bindingAdapterPosition

            /////////////
            if (detailData.plus) {
                binding.costImage.setImageDrawable(null)
                binding.carbonUsageImage.setImageDrawable(null)
                binding.placeImage.setImageDrawable(null)
                binding.waterOrElectricityImage.setImageDrawable(null)
                binding.placeText.text = null
                binding.waterOrEletricityUsageText.text = null
                binding.costText.text = null
                binding.carbonUsageText.text = null
                binding.detailedCard.setOnClickListener {
                    val intent = Intent(context, QrcodeScanActivity::class.java)
                    intent.putExtra("ACTION", "REGISTER_PLUG")
                    context.startActivity(intent)
                }

                binding.plusImage.setImageResource(R.drawable.add_circle_60px)


            } else {
                binding.placeText.text = detailData.place
                binding.waterOrEletricityUsageText.text = detailData.typeUsage
                binding.costText.text = detailData.cost
                binding.carbonUsageText.text = detailData.carbonUsage
                binding.costImage.setImageResource(R.drawable.payments)
                binding.carbonUsageImage.setImageResource(R.drawable.co2)
                var place_image = detailData.placeImage
                var type_image = detailData.typeImage


                when (place_image) {
                    0 -> binding.placeImage.setImageResource(R.drawable.faucet)
                    1 -> binding.placeImage.setImageResource(R.drawable.power)
                    else -> binding.placeImage.setImageResource(R.drawable.faucet)
                }
                when (type_image) {
                    0 -> binding.waterOrElectricityImage.setImageResource(R.drawable.water_drop)
                    1 -> binding.waterOrElectricityImage.setImageResource(R.drawable.bolt)
                    else -> binding.waterOrElectricityImage.setImageResource(R.drawable.water_drop)
                }


                if (detailData.typeImage == 1) {
                    binding.detailedCard.setOnClickListener {
                        cardClickMenu(bindingAdapterPosition, detailData.plugId!!)
                    }
                    val color = ContextCompat.getColor(context, R.color.electric)
                    binding.waterOrEletricityUsageText.setTextColor(color)
                    binding.waterOrElectricityImage.setColorFilter(color)



                    //            val handler = Handler(Looper.getMainLooper())

                    sharedViewModel.getUserElectricityUsage()
                        .observe(context as LifecycleOwner) { updatedData ->
                            val updateAmount = updatedData[detailData.plugId]
                            if (updateAmount != null) {
//                                currentData.typeUsage = updateAmount
                                binding.waterOrEletricityUsageText.text = numberFormat.toKwhString(updateAmount)
                                binding.carbonUsageText.text = numberFormat.electricityUsageToCarbonUsageString(updateAmount)
                                binding.costText.text = numberFormat.electricityToPriceString(updateAmount)
                            }
                        }
                }

            }

        }
    }

    override fun onBindViewHolder(
        holder: DetailedRecyclerAdapter.MyViewHolder,
        position: Int
    ) {
        val data = datalist[position]
        holder.bind(data)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DetailedRecyclerAdapter.MyViewHolder {
        val binding =
            DetailedListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int = datalist.size

    private fun removeView(position: Int) {
        datalist.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, datalist.size)
    }


    private fun cardClickMenu(position: Int, plugId: String) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("설정") // 다이얼로그 제목 설정
        val choices = arrayOf("이름 변경", "삭제")
        builder.setItems(choices) { dialog, which ->
            when (which) {
                0 -> {
                    //이름 변경
                    dialog.dismiss() // 다이얼로그 닫기
                    showTextInputDialog(position, plugId) // 텍스트 입력창 띄우기
                }
                1 -> {
                    // 삭제
                    dialog.dismiss() // 다이얼로그 닫기
                    showAlertDialog(position, plugId)
                }
            }
        }
        builder.create().show() // 다이얼로그 표시
    }

    private fun showTextInputDialog(position: Int, plugId: String) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("텍스트 입력") // 다이얼로그 제목 설정
        val editText = EditText(context)
        builder.setView(editText) // 커스텀 뷰로 EditText를 설정
        builder.setPositiveButton("확인") { dialog, which ->
            val newName = editText.text.toString() // 입력된 텍스트 가져오기

            localDatabase = CarbonCheckLocalDatabase.getInstance(context)
            val plugDao = localDatabase.plugDao()

            // 코루틴 스코프 내에서 작업 수행
            CoroutineScope(Dispatchers.Main).launch {
                val plugArray = withContext(Dispatchers.IO) {
                    plugDao.findById(plugId)
                }

                if (plugArray.isNotEmpty()) {
                    plugArray[0].plugName = newName // 플러그 이름 변경 후
                    withContext(Dispatchers.IO) {
                        plugDao.updatePlug(plugArray[0]) // 플러그 다시 저장
                    }
                }
            }

            datalist[position].place = newName
            notifyItemChanged(position)

            val currentNameMap = sharedViewModel.getUserElectricityUsageName().value?.toMutableMap()
            val pair = currentNameMap?.get(plugId)
            val newPair = pair?.copy(first = newName)
            currentNameMap?.put(plugId, newPair!!)
            sharedViewModel.setUserElectricityUsageName(currentNameMap?.toMap() ?: emptyMap())
        }

        builder.setNegativeButton("취소") { dialog, which ->
            dialog.dismiss() // 다이얼로그 닫기
        }
        builder.create().show() // 다이얼로그 표시
    }

    private fun showAlertDialog(position: Int, plugId: String) {
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
                            localDatabase = CarbonCheckLocalDatabase.getInstance(context)
                            val plugDao = localDatabase.plugDao()
                            CoroutineScope(Dispatchers.IO).launch {
                                val plugArray = plugDao.findById(plugId)
                                if (plugArray.isNotEmpty()) {
                                    plugDao.deletePlug(plugArray[0])
                                }
                            }


                            //현재 view에서 삭제
                            removeView(position)


                            //view Model에서 삭제
                            val currentNameMap =
                                sharedViewModel.getUserElectricityUsageName().value?.toMutableMap()
                            currentNameMap?.remove(plugId)
                            sharedViewModel.setUserElectricityUsageName(
                                currentNameMap?.toMap() ?: emptyMap()
                            )

                            val currentMap =
                                sharedViewModel.getUserElectricityUsage().value?.toMutableMap()
                            currentMap?.remove(plugId)
                            sharedViewModel.setUserElectricityUsage(
                                currentMap?.toMap() ?: emptyMap()
                            )


                        }
                        if (result == false) Toast.makeText(context, "삭제 실패", Toast.LENGTH_LONG)
                            .show()
                    }
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

}

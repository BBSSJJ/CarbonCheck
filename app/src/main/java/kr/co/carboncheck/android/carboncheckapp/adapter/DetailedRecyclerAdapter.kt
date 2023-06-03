package kr.co.carboncheck.android.carboncheckapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kr.co.carboncheck.android.carboncheckapp.R
import kr.co.carboncheck.android.carboncheckapp.databinding.DetailedListBinding
import kr.co.carboncheck.android.carboncheckapp.databinding.HomeUserUsageListBinding
import kr.co.carboncheck.android.carboncheckapp.dataobject.DetailData
import kr.co.carboncheck.android.carboncheckapp.dataobject.MemberUsageData


class DetailedRecyclerAdapter:  RecyclerView.Adapter<DetailedRecyclerAdapter.MyViewHolder>() {

    var datalist = mutableListOf<DetailData>()

    inner class MyViewHolder(private val binding: DetailedListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(detailData: DetailData) {
            if(detailData.Plus){

            } else {
                // Text 완료
                binding.placeText.text =detailData.place
                binding.waterOrEletricityUsageText.text = detailData.typeUsage
                binding.costText.text = detailData.Cost
                binding.carbonUsageText.text = detailData.carbonUsage
                var place_image = detailData.placeImage
                var type_image = detailData.typeImage
                when(place_image){
                    0 -> R.drawable.water_drop
                    1 -> R.drawable.bolt
                    else -> R.drawable.water_drop
                }
                when(type_image){
                    0 -> R.drawable.faucet
                    1 -> R.drawable.power
                    else -> R.drawable.faucet
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailedRecyclerAdapter.MyViewHolder {
        val binding =
            DetailedListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }
    override fun getItemCount(): Int = datalist.size


}

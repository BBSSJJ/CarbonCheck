package kr.co.carboncheck.android.carboncheckapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kr.co.carboncheck.android.carboncheckapp.databinding.HomeUserUsageListBinding
import kr.co.carboncheck.android.carboncheckapp.dataobject.MemberUsageData

class TotalUsageRecyclerViewAdapter :
    RecyclerView.Adapter<TotalUsageRecyclerViewAdapter.MyViewHolder>() {

    var datalist = mutableListOf<MemberUsageData>()

    inner class MyViewHolder(private val binding: HomeUserUsageListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(memberUsageData: MemberUsageData) {
            //binding.dogPhotoImg.=dogData.dog_img
            binding.userName.text = memberUsageData.user_name
            binding.targetUsage.text = " /" + memberUsageData.target_usage.toString() + " g"
            binding.currentUsage.text = memberUsageData.current_usage.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            HomeUserUsageListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int = datalist.size


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(datalist[position])
    }

}
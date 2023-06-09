package kr.co.carboncheck.android.carboncheckapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kr.co.carboncheck.android.carboncheckapp.R
import kr.co.carboncheck.android.carboncheckapp.databinding.UserInfoMemberListBinding
import kr.co.carboncheck.android.carboncheckapp.dataobject.MemberData


class UserInfoMemberRecyclerViewAdapter :
    RecyclerView.Adapter<UserInfoMemberRecyclerViewAdapter.MyViewHolder>() {

    var datalist = mutableListOf<MemberData>()

    inner class MyViewHolder(private val binding: UserInfoMemberListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(memberData: MemberData) {
            binding.memberName.text = memberData.memberName
            val pictureId = when (memberData.profilePic % 3) {
                0 -> R.drawable.emblem_t01
                1 -> R.drawable.emblem_t01
                2 -> R.drawable.emblem_t01
                else -> R.drawable.emblem_t01
            }
            binding.profilePic.setImageResource(pictureId)
        }
    }

    override fun getItemCount(): Int = datalist.size

    override fun onBindViewHolder(
        holder: UserInfoMemberRecyclerViewAdapter.MyViewHolder, position: Int
    ) {
        val data = datalist[position]
        holder.bind(data)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): MyViewHolder {
        val binding = UserInfoMemberListBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return MyViewHolder(binding) // MyViewHolder 생성 및 반환
    }
}
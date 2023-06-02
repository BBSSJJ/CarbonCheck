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
            //binding.dogPhotoImg.=dogData.dog_img
            binding.memberName.text = memberData.memberName
            val pictureId = when (memberData.profilePic % 3) {
                0 -> R.drawable.profile0
                1 -> R.drawable.profile1
                2 -> R.drawable.profile2
                else -> R.drawable.profile0
            }
            binding.profilePic.setImageResource(pictureId)
        }
    }

    override fun getItemCount(): Int = datalist.size

    override fun onBindViewHolder(
        holder: UserInfoMemberRecyclerViewAdapter.MyViewHolder,
        position: Int
    ) {
        val data = datalist[position]
        holder.bind(data)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder { // onCreateViewHolder 메서드를 추가
        val binding = UserInfoMemberListBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ) // 레이아웃 인플레이션
        return MyViewHolder(binding) // MyViewHolder 생성 및 반환
    }
}
package kr.co.carboncheck.android.carboncheckapp.view

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import kr.co.carboncheck.android.carboncheckapp.R
import kr.co.carboncheck.android.carboncheckapp.adapter.UserInfoMemberRecyclerViewAdapter
import kr.co.carboncheck.android.carboncheckapp.databinding.FragmentDetailedUsageBinding
import kr.co.carboncheck.android.carboncheckapp.databinding.FragmentUserInfoBinding
import kr.co.carboncheck.android.carboncheckapp.dataobject.MemberData
import kr.co.carboncheck.android.carboncheckapp.dataobject.MemberUsageData
import kr.co.carboncheck.android.carboncheckapp.dto.RegisterFaceRequest
import kr.co.carboncheck.android.carboncheckapp.dto.RegisterFaceResponse
import kr.co.carboncheck.android.carboncheckapp.network.RetrofitClient
import kr.co.carboncheck.android.carboncheckapp.network.SseListener
import kr.co.carboncheck.android.carboncheckapp.util.UserPreference
import kr.co.carboncheck.android.carboncheckapp.viewmodel.SharedViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserInfoFragment : Fragment() {
    private var _binding: FragmentUserInfoBinding? = null
    private val binding get() = _binding!!
    private val memberDatas = mutableListOf<MemberData>()
    private lateinit var progressDialog: ProgressDialog
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private lateinit var myName: String
//    val sseListener = SseListener()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUserInfoBinding.inflate(inflater, container, false)

        val registerHomeServerButton = binding.registerHomeServerButton
        val joinHomeServerButton = binding.joinHomeServerButton
        val registerFaceButton = binding.registerFaceButton
        val logoutButton = binding.logoutButton
        registerHomeServerButton.setOnClickListener {
            val intent = Intent(activity, QrcodeScanActivity::class.java)
            intent.putExtra("ACTION", "REGISTER_HOMESERVER")
            startActivity(intent)
        }

        joinHomeServerButton.setOnClickListener {
            val intent = Intent(activity, QrcodeScanActivity::class.java)
            intent.putExtra("ACTION", "JOIN_HOMESERVER")
            startActivity(intent)
        }

        registerFaceButton.setOnClickListener {
            val preference = getUserDataPreference(requireActivity())
            if (preference["homeServerId"] == "") {
                Toast.makeText(requireActivity(), "홈서버에 가입되지 않았습니다", Toast.LENGTH_SHORT).show()
            } else {
                progressDialog = ProgressDialog(context)
                progressDialog.setMessage("카메라 앞에 서주세요.\n등록이 완료되면 자동으로 닫힙니다.")
                progressDialog.setCancelable(true)
                progressDialog.show()

                sendRegisterFaceRequest(preference["userId"]!!, preference["homeServerId"]!!) {
                    //콜백
                }
            }
        }

        logoutButton.setOnClickListener {
            deletePreferences(requireActivity())

        }

        myName = getUserDataPreference(requireContext())["name"]!!
        binding.userInfoUserName.text = myName


        return binding.root

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeMemberList()      // TODO: 자신을 제외한 멤버 데이터 (번호, 이름) 불러올 것
        initMemberListRecyclerView()
    }

    fun deletePreferences(context: Context?) {
        val userPreference = UserPreference().getPreferences(context!!)
        val editor = userPreference!!.edit()
        editor.clear()
        editor.apply()
        val intent = Intent(requireActivity(), LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        requireActivity().finish()
    }

    private fun getUserDataPreference(context: Context): Map<String, String?> {
        val userPreference = UserPreference().getPreferences(context!!)
        val LoginInfo: MutableMap<String, String?> = HashMap()
        val userId = userPreference!!.getString("userId", "")
        val homeServerId = userPreference!!.getString("homeServerId", "")
        val name = userPreference!!.getString("name", "")
        LoginInfo["userId"] = userId
        LoginInfo["homeServerId"] = homeServerId
        LoginInfo["name"] = name
        return LoginInfo
    }

    private fun sendRegisterFaceRequest(
        userId: String,
        homeServerId: String,
        callback: (Boolean) -> Unit
    ) {

        Log.d("testlog", "회원가입 요청 보냄")

        val request = RegisterFaceRequest(userId, homeServerId)
        val call = RetrofitClient.userService.postRegisterFaceRequest(request)

        call.enqueue(object : Callback<RegisterFaceResponse> {
            override fun onResponse(
                call: Call<RegisterFaceResponse>,
                response: Response<RegisterFaceResponse>
            ) {
                if (response.isSuccessful) {
                    Log.d("testlog", "얼굴 등록 요청 응답 도착")
                    Toast.makeText(
                        requireActivity(),
                        response.body()!!.message,
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    if (response.body()!!.delivered) {
                        Log.d("testlog", "홈서버에 얼굴 등록 요청 성공")
                        callback(true)
                    } else {
                        Log.d("testlog", "홈서버에 얼굴 등록 요청 실패")
                        callback(false)
                    }
                } else {
                    Log.d("testlog", "홈서버에 얼굴 등록 요청 응답 도착 안함")
                    Toast.makeText(
                        requireActivity(),
                        response.body()!!.message,
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    callback(false)
                }
            }

            override fun onFailure(call: Call<RegisterFaceResponse>, t: Throwable) {
                Log.e("testlog", "홈서버에 얼굴 등록 요청 전송 실패" + t.message)
                Toast.makeText(requireActivity(), "Request failed", Toast.LENGTH_SHORT).show()
                callback(false)
            }
        })
    }

    private fun initMemberListRecyclerView() {
        val adapter = UserInfoMemberRecyclerViewAdapter()
        adapter.datalist = memberDatas
        binding.memberInfoRecyclerView.adapter = adapter
        binding.memberInfoRecyclerView.layoutManager = LinearLayoutManager(activity)
    }

    private fun initializeMemberList() {
        val map = sharedViewModel.getGroupTargetValue().value
        if (map != null) {
            var i = 1
            for((key, value) in map){
                if(key == myName) continue
                with(memberDatas) {
                    // TODO: 여기에 실제 데이터 삽입 하시오 ( 가족 이름, 유저 번호)
                    add(MemberData(i, key))
                }
                i += 1
            }
        }


    }
}
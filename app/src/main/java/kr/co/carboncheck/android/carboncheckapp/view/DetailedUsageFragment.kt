package kr.co.carboncheck.android.carboncheckapp.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import kr.co.carboncheck.android.carboncheckapp.databinding.FragmentDetailedUsageBinding
import kr.co.carboncheck.android.carboncheckapp.dto.JoinResponse
import kr.co.carboncheck.android.carboncheckapp.dto.RegisterFaceRequest
import kr.co.carboncheck.android.carboncheckapp.dto.RegisterFaceResponse
import kr.co.carboncheck.android.carboncheckapp.network.RetrofitClient
import kr.co.carboncheck.android.carboncheckapp.util.UserPreference
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailedUsageFragment : Fragment() {
    private var _binding: FragmentDetailedUsageBinding? = null
    private val binding get() = _binding!!


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
        val registerHomeServerButton = binding.registerHomeServerButton
        val joinHomeServerButton = binding.joinHomeServerButton
        val registerFaceButton = binding.registerFaceButton

        // TODO: 이미 가입된 홈서버가 있으면 가입안되도록 수정하자.
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
            }
            else{
                sendRegisterFaceRequest(preference["userId"]!!, preference["homeServerId"]!!){
                    //콜백
                }
            }

        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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

}
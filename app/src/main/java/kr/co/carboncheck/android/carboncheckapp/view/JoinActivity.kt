package kr.co.carboncheck.android.carboncheckapp.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import kr.co.carboncheck.android.carboncheckapp.databinding.ActivityJoinBinding
import kr.co.carboncheck.android.carboncheckapp.model.JoinRequestDTO
import kr.co.carboncheck.android.carboncheckapp.model.JoinResponseDTO
import kr.co.carboncheck.android.carboncheckapp.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class JoinActivity : AppCompatActivity() {
    private lateinit var binding: ActivityJoinBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJoinBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var joinConfirmButton = binding.joinConfirmButton
        joinConfirmButton.setOnClickListener {
            sendJoinRequest(
                binding.joinEmailText.text.toString(),
                binding.joinPasswordText.text.toString(),
                binding.joinNameText.text.toString(),
                "local"
            ){
                result->
                if(result){
                    //회원가입 성공
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                }
                else{
                    //회원가입 실패
                }
            }
        }

    }

    private fun sendJoinRequest(
        email: String,
        password: String,
        name: String,
        authType: String,
        callback: (Boolean) -> Unit
    ) {

        Log.d("testlog", "회원가입 요청 보냄")

        val request = JoinRequestDTO(email, password, name, authType)
        val call = RetrofitClient.service.postJoinRequest(request)

        call.enqueue(object : Callback<JoinResponseDTO> {
            override fun onResponse(
                call: Call<JoinResponseDTO>,
                response: Response<JoinResponseDTO>
            ) {
                if (response.isSuccessful) {
                    Log.d("testlog", "회원가입 요청 응답 도착")
                    Toast.makeText(applicationContext, response.message(), Toast.LENGTH_SHORT)
                        .show()
                    if (response.body()!!.success) {
                        Log.d("testlog", "회원가입 성공")
                        callback(true)
                    } else {
                        Log.d("testlog", "회원가입 실패")
                        callback(false)
                    }
                } else {
                    Log.d("testlog", "회원가입 요청 응답 도착 안함")
                    Toast.makeText(applicationContext, response.message(), Toast.LENGTH_SHORT)
                        .show()
                    callback(false)
                }
            }

            override fun onFailure(call: Call<JoinResponseDTO>, t: Throwable) {
                Log.e("testlog", "회원가입 요청 전송 실패" + t.message)
                Toast.makeText(applicationContext, "Request failed", Toast.LENGTH_SHORT).show()
                callback(false)
            }
        })
    }
}
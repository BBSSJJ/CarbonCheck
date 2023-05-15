package kr.co.carboncheck.android.carboncheckapp.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import kr.co.carboncheck.android.carboncheckapp.databinding.ActivityJoinBinding
import kr.co.carboncheck.android.carboncheckapp.dto.JoinRequest
import kr.co.carboncheck.android.carboncheckapp.dto.JoinResponse
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
            var joinEmailText = binding.joinEmailText.text.toString()
            var joinPasswordText = binding.joinPasswordText.text.toString()
            var joinPasswordCheckText = binding.joinPasswordCheckText.text.toString()
            var joinNameText = binding.joinNameText.text.toString()
            if (joinEmailText == "")
                Toast.makeText(applicationContext, "이메일을 입력해주세요", Toast.LENGTH_SHORT).show()
            else if (joinPasswordText == "")
                Toast.makeText(applicationContext, "비밀번호를 입력해주세요", Toast.LENGTH_SHORT).show()
            else if (joinPasswordText != joinPasswordCheckText)
                Toast.makeText(applicationContext, "비밀번호가 일치하지 않습니다", Toast.LENGTH_SHORT).show()
            else if (joinNameText == "")
                Toast.makeText(applicationContext, "이름을 입력해주세요", Toast.LENGTH_SHORT).show()
            else
                sendJoinRequest(
                    joinEmailText,
                    joinPasswordText,
                    joinNameText,
                    "local"
                ) { result ->
                    if (result) {
                        //회원가입 성공
                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                    } else {
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

        val request = JoinRequest(email, password, name, authType)
        val call = RetrofitClient.userService.postJoinRequest(request)

        call.enqueue(object : Callback<JoinResponse> {
            override fun onResponse(
                call: Call<JoinResponse>,
                response: Response<JoinResponse>
            ) {
                if (response.isSuccessful) {
                    Log.d("testlog", "회원가입 요청 응답 도착")
                    Toast.makeText(
                        applicationContext,
                        response.body()!!.message,
                        Toast.LENGTH_SHORT
                    )
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
                    Toast.makeText(
                        applicationContext,
                        response.body()!!.message,
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    callback(false)
                }
            }

            override fun onFailure(call: Call<JoinResponse>, t: Throwable) {
                Log.e("testlog", "회원가입 요청 전송 실패" + t.message)
                Toast.makeText(applicationContext, "Request failed", Toast.LENGTH_SHORT).show()
                callback(false)
            }
        })
    }
}
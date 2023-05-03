package kr.co.carboncheck.android.carboncheckapp.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import kr.co.carboncheck.android.carboncheckapp.databinding.ActivityLoginBinding
import kr.co.carboncheck.android.carboncheckapp.model.LoginRequestDTO
import kr.co.carboncheck.android.carboncheckapp.model.LoginResponseDTO
import kr.co.carboncheck.android.carboncheckapp.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Boolean.FALSE
import java.lang.Boolean.TRUE

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var localLoginButton = binding.localLoginButton
        localLoginButton.setOnClickListener {
            sendLoginRequest(
                binding.emailText.text.toString(),
                binding.passwordText.text.toString()
            ) { result ->
                if (result) {
                    //로그인 성공
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                } else {
                    //로그인 실패
                }
            }
        }

        var joinButton = binding.joinButton
        joinButton.setOnClickListener {
            val intent = Intent(this, JoinActivity::class.java)
            startActivity(intent)
        }
    }

    private fun sendLoginRequest(
        email: String,
        password: String,
        callback: (Boolean) -> Unit
    ) {
        val request = LoginRequestDTO(email, password)
        val call = RetrofitClient.service.postLoginRequest(request);
        call.enqueue(object : Callback<LoginResponseDTO> {
            override fun onResponse(
                call: Call<LoginResponseDTO>,
                response: Response<LoginResponseDTO>
            ) {
                if (response.isSuccessful) {
                    Log.d("testlog", "로그인 요청 응답 도착")
                    Toast.makeText(applicationContext, response.message(), Toast.LENGTH_SHORT)
                        .show()
                    if (response.body()!!.success) {
                        Log.d("testlog", "로그인 성공" + response.message())
                        callback(true);
                    } else {
                        Log.d("testlog", "로그인 실패" + response.message())
                        callback(false);
                    }

                } else {
                    Log.d("testlog", "로그인 요청 응답 도착 안함")
                    Toast.makeText(applicationContext, "Request failed", Toast.LENGTH_SHORT).show()
                    callback(false);
                }
            }

            override fun onFailure(call: Call<LoginResponseDTO>, t: Throwable) {
                Log.e("testlog", "로그인 요청 전송 실패" + t.message)
                Toast.makeText(applicationContext, "Request failed", Toast.LENGTH_SHORT).show()
                callback(false);
            }
        })
    }
}
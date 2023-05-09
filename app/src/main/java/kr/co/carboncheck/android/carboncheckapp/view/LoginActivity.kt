package kr.co.carboncheck.android.carboncheckapp.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kr.co.carboncheck.android.carboncheckapp.databinding.ActivityLoginBinding
import kr.co.carboncheck.android.carboncheckapp.model.LoginRequestDTO
import kr.co.carboncheck.android.carboncheckapp.model.LoginResponseDTO
import kr.co.carboncheck.android.carboncheckapp.network.RetrofitClient
import kr.co.carboncheck.android.carboncheckapp.util.UserPreference
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lateinit var email: String
        lateinit var password: String

        var localLoginButton = binding.localLoginButton

        val loginInfo: Map<String, String?> = getLoginInfo(this)
        if (loginInfo["email"] != "") {
            Log.d("testlog", "여기는 자동로그인")
            email = loginInfo["email"]!!
            password = loginInfo["password"]!!
            sendLoginRequest(
                email, password
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

        localLoginButton.setOnClickListener {
            Log.d("testlog", "여기는 로그인 클릭")
            email = binding.emailText.text.toString()
            password = binding.passwordText.text.toString()
            sendLoginRequest(email, password) { result ->
                if (result) {
                    //로그인 성공
                    var autoLoginCheckBox = binding.autoLoginCheckBox
                    if (autoLoginCheckBox.isChecked)
                        setLoginInfo(this, email, password)
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

    fun setLoginInfo(context: Context?, email: String?, password: String?) {
        val userPreference = UserPreference().getPreferences(context!!)
        val editor = userPreference!!.edit()
        editor.putString("email", email)
        editor.putString("password", password)
        editor.apply()
    }

    fun getLoginInfo(context: Context): Map<String, String?> {
        val userPreference = UserPreference().getPreferences(context!!)
        val LoginInfo: MutableMap<String, String?> = HashMap()
        val email = userPreference!!.getString("email", "")
        val password = userPreference!!.getString("password", "")
        LoginInfo["email"] = email
        LoginInfo["password"] = password
        return LoginInfo
    }


    // 로그인 요청 메소드
    private fun sendLoginRequest(
        email: String,
        password: String,
        callback: (Boolean) -> Unit
    ) {
        val request = LoginRequestDTO(email, password)
        val call = RetrofitClient.service.postLoginRequest(request);
        call.enqueue(object : Callback<LoginResponseDTO> {
            //전송 성공 시
            override fun onResponse(
                call: Call<LoginResponseDTO>,
                response: Response<LoginResponseDTO>
            ) {
                if (response.isSuccessful) {    //response가 성공적으로 왔을 때

                    Log.d("testlog", "로그인 요청 응답 도착")
                    Toast.makeText(applicationContext, response.body()!!.message, Toast.LENGTH_SHORT)
                        .show()
                    if (response.body()!!.success) {
                        Log.d("testlog", "로그인 성공" + response.body()!!.message)
                        callback(true);
                    } else {
                        Log.d("testlog", "로그인 실패" + response.body()!!.message)
                        callback(false);
                    }

                } else {    //response가 도착하지 않았을 때
                    Log.d("testlog", "로그인 요청 응답 도착 안함")
                    Toast.makeText(applicationContext, "Request failed", Toast.LENGTH_SHORT).show()
                    callback(false);
                }
            }

            //전송 실패했을 때
            override fun onFailure(call: Call<LoginResponseDTO>, t: Throwable) {
                Log.e("testlog", "로그인 요청 전송 실패" + t.message)
                Toast.makeText(applicationContext, "Request failed", Toast.LENGTH_SHORT).show()
                callback(false);
            }
        })
    }
}
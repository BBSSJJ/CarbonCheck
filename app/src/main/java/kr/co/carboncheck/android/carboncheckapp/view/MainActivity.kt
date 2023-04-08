package kr.co.carboncheck.android.carboncheckapp.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import kr.co.carboncheck.android.carboncheckapp.databinding.ActivityMainBinding
import kr.co.carboncheck.android.carboncheckapp.model.MyRequest
import kr.co.carboncheck.android.carboncheckapp.model.MyResponse
import kr.co.carboncheck.android.carboncheckapp.network.RetrofitClient

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val button = binding.sendButton
        Log.d("testlog", "어플 실행")
        button.setOnClickListener {
            Log.d("testlog", "온클릭 리스너 적용")
            Toast.makeText(applicationContext, "clicked!", Toast.LENGTH_SHORT).show()
            getUserData()
        }

    }

    // User data 가져오는 함수
    private fun getUserData() {
        Log.d("testlog", "데이터 보내기 시작")

        // 요청 보낼 객체 생성해서 임의로 아이디 넣어줬어
        val request = MyRequest()
        request.userId = "tasty Lee"


        val call = RetrofitClient.service.postData(request)
        call.enqueue(object : Callback<MyResponse> {
            // 전송 성공
            override fun onResponse(call: Call<MyResponse>, response: Response<MyResponse>) {
                if (response.isSuccessful) {
                    // response는 서버에서 받아온 MyResponse 타입 객체입니다
                    Log.d("testlog", "전송 성공")
                    val userId = response.body()?.userId
                    val usage = response.body()?.usage
                    val time = response.body()?.time
                    Toast.makeText(applicationContext, userId + usage + time, Toast.LENGTH_SHORT)
                        .show()
                } else {
                    Log.d("testlog", "전송 실패")
                    Toast.makeText(applicationContext, "Request failed", Toast.LENGTH_SHORT).show()
                }
            }

            //전송 실패
            override fun onFailure(call: Call<MyResponse>, t: Throwable) {
                Log.e("testlog", "전송 실패: " + t.message)
                Toast.makeText(applicationContext, "Request failed", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
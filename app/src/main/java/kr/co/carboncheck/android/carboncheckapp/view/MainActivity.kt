package kr.co.carboncheck.android.carboncheckapp.view

import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Process
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kr.co.carboncheck.android.carboncheckapp.databinding.ActivityMainBinding
import kr.co.carboncheck.android.carboncheckapp.model.MyRequest
import kr.co.carboncheck.android.carboncheckapp.model.MyResponse
import kr.co.carboncheck.android.carboncheckapp.network.RetrofitClient

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val listPackageInfo: MutableList<PackageInfo> = mutableListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (checkForPermission()) {
            // 권한이 있으면 메인 액티 비티를 시각화 합니다.
            setContentView(binding.root)
            // 설치된 어플 목록을 가져옵니다.
            setPackageInfoList()
        } else {
            // 권한이 존재 하지 않으면 토스트 메세지 출력후 권한 설정 화면 으로 이동 합니다.
            Toast.makeText(
                this, "Check Permission", Toast.LENGTH_LONG
            ).show()
            startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
        }

        // 통신 제대로 되는지 확인, 버튼 누르면 데이터 전송하도록 해둠
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

    private fun checkForPermission(): Boolean {
        // Application 의 패키지 명을 가져 오기 위한 권한이 있는지 확인 하는 함수 입니다.
        val appOps = this.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode =
            appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, Process.myUid(), packageName)
/*
         val mode = appOps.noteOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, Process.myUid(), packageName,
                    null, null)
         으로 사용 할수 있겠 으나 API 30 부터 지원됨
         AttributionTag, RemoteCallback Parameter 를 null 로 설정 하는 예제 이며 이는 "우리는 필요 없어서" 다
*/
        return mode == AppOpsManager.MODE_ALLOWED
    }

    private fun setPackageInfoList() {
        // 설치된 어플 목록을 listPackageInfo 에 담는 코드
        // Overhead 를 줄이기 위해 Coroutine 을 사용 하여 쓰레드 분리
        CoroutineScope(Dispatchers.IO).launch {
            var list: List<PackageInfo> = packageManager.getInstalledPackages(0)
            for (i in list) {
                listPackageInfo.add(i)
            }
        }
    }
}
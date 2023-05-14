package kr.co.carboncheck.android.carboncheckapp.view

import android.Manifest
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.zxing.integration.android.IntentIntegrator
import kr.co.carboncheck.android.carboncheckapp.databinding.ActivityQrcodeScanBinding
import kr.co.carboncheck.android.carboncheckapp.model.RegisterHomeServerRequest
import kr.co.carboncheck.android.carboncheckapp.model.RegisterHomeServerResponse
import kr.co.carboncheck.android.carboncheckapp.network.RetrofitClient
import kr.co.carboncheck.android.carboncheckapp.util.UserPreference
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class QrcodeScanActivity : AppCompatActivity() {
    private lateinit var binding: ActivityQrcodeScanBinding
    private val REQUEST_CAMERA_PERMISSION = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQrcodeScanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //카메라 권한 없을 경우
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                REQUEST_CAMERA_PERMISSION
            )
        } else {
            startScan()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CAMERA_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startScan()
                } else {
                    Toast.makeText(this, "카메라 권한이 거부되었습니다.", Toast.LENGTH_SHORT).show()
                    finish()
                }
                return
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IntentIntegrator.REQUEST_CODE && resultCode == RESULT_OK) {
            val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
            if (result != null && result.contents != null) {
                Log.d("QRScanActivity", "스캔 결과 : ${result.contents}")
                // 스캔 결과를 처리
                val homeServerId = result.contents
                val email = getEmail(this)
                Log.d("homeserverid : ", homeServerId)
                Log.d("email : ", email)
                sendRegisterHomeServerRequest(homeServerId, email) { result ->
                    if (result) {
                        // 등록 성공
                    } else {
                        // 등록 실패
                    }
                }

            } else {
                Toast.makeText(this, "취소됨", Toast.LENGTH_LONG).show()
            }
        }
        finish()
    }

    private fun startScan() {
        val integrator = IntentIntegrator(this)
        integrator.setBeepEnabled(false)
        integrator.setOrientationLocked(true)
        integrator.setPrompt("QR코드를 스캔하세요")
        integrator.setCameraId(0) // 0은 후면 카메라, 1은 전면 카메라
        integrator.initiateScan()
    }

    override fun onBackPressed() {
        finish()
    }

    fun getEmail(context: Context): String {
        val userPreference = UserPreference().getPreferences(context!!)
        val email = userPreference!!.getString("email", "")!!
        return email
    }

    private fun sendRegisterHomeServerRequest(
        homeServerCode: String,
        email: String,
        callback: (Boolean) -> Unit
    ) {
        val request = RegisterHomeServerRequest(homeServerCode, email)
        val call = RetrofitClient.deviceService.postRegisterHomeServerRequest(request);
        call.enqueue(object : Callback<RegisterHomeServerResponse> {
            //전송 성공 시
            override fun onResponse(
                call: Call<RegisterHomeServerResponse>,
                response: Response<RegisterHomeServerResponse>
            ) {
                if (response.isSuccessful) {    //response가 성공적으로 왔을 때

                    Log.d("testlog", "홈서버 등록 응답 도착")
                    Toast.makeText(
                        applicationContext,
                        response.body()!!.message,
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    if (response.body()!!.success) {
                        Log.d("testlog", "홈서버 등록 성공 " + response.body()!!.message)
                        callback(true);
                    } else {
                        Log.d("testlog", "홈서버 등록 실패 " + response.body()!!.message)
                        callback(false);
                    }

                } else {    //response가 도착하지 않았을 때
                    Log.d("testlog", "홈서버 등록 요청 응답 도착 안함")
                    Toast.makeText(applicationContext, "Request failed", Toast.LENGTH_SHORT).show()
                    callback(false);
                }
            }

            //전송 실패했을 때
            override fun onFailure(call: Call<RegisterHomeServerResponse>, t: Throwable) {
                Log.e("testlog", "홈서버 등록 요청 전송 실패" + t.message)
                Toast.makeText(applicationContext, "Request failed", Toast.LENGTH_SHORT).show()
                callback(false);
            }
        })
    }
}

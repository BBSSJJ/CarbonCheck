package kr.co.carboncheck.android.carboncheckapp.view

import android.Manifest
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

class QrcodeScanActivity : AppCompatActivity() {
    private lateinit var binding: ActivityQrcodeScanBinding
    private val REQUEST_CAMERA_PERMISSION = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQrcodeScanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //권한 없을 경우
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
                // 스캔 결과를 처리하는 코드를 작성하세요.
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

}

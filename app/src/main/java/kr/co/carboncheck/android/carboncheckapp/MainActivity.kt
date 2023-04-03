package kr.co.carboncheck.android.carboncheckapp

import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Process
import android.provider.Settings
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private val listPackageInfo: MutableList<PackageInfo> = mutableListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 앱 실행시 필수 권한이 있는지 확인 하는 코드 입니다.
        if (checkForPermission()) {
            // 권한이 있으면 메인 액티 비티를 시각화 합니다.
            setContentView(R.layout.activity_main)
            // 설치된 어플 목록을 가져옵니다.
            setPackageInfoList()
        } else {
            // 권한이 존재 하지 않으면 토스트 메세지 출력후 권한 설정 화면 으로 이동 합니다.
            Toast.makeText(
                this,
                "Check Permission",
                Toast.LENGTH_LONG
            ).show()
            startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
        }
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
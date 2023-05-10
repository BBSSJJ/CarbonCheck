package kr.co.carboncheck.android.carboncheckapp.view

import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.os.Bundle
import android.os.Process
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kr.co.carboncheck.android.carboncheckapp.R
import kr.co.carboncheck.android.carboncheckapp.databinding.ActivityMainBinding
import kr.co.carboncheck.android.carboncheckapp.util.UserPreference


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val listPackageInfo: MutableList<PackageInfo> = mutableListOf()
    private lateinit var bottomNavigationView: BottomNavigationView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //첫 화면 fragment 지정
        val startFragment = TotalUsageFragment()
        supportFragmentManager.beginTransaction().replace(R.id.fragmentContainer, startFragment)
            .commit()

        bottomNavigationView = binding.bottomNavigationView

        //하단 네비게이션 바 클릭 시 해당 fragment로 전환
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            var fragment: Fragment? = null
            when (item.itemId) {
                R.id.total_usage_menu -> fragment = TotalUsageFragment()
                R.id.detailed_usage_menu -> fragment = DetailedUsageFragment()
                R.id.solution_menu -> fragment = SolutionFragment()
                R.id.mini_game_menu -> fragment = MiniGameFragment()
                R.id.user_info_menu -> fragment = UserInfoFragment()

            }
            loadFragment(fragment)
        }



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
    }

    //frame layout 부분을 fragment로 채워넣는 함수
    fun loadFragment(fragment: Fragment?): Boolean {
        fragment?.let {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, it)
                .commit()
            return true
        }
        return false
    }

    fun deletePreferences(context: Context?) {
        val userPreference = UserPreference().getPreferences(context!!)
        val editor = userPreference!!.edit()
        editor.clear()
        editor.apply()
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
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

    // 뒤로가기 두번 눌러 종료하도록 하는 코드
    private var backPressedTime: Long = 0 // 뒤로가기 버튼이 눌린 시간을 저장하는 변수
    private val backPressedInterval = 2000 // 두 번 눌렀을 때의 시간 간격 (밀리초)

    override fun onBackPressed() {
        if (backPressedTime + backPressedInterval > System.currentTimeMillis()) {
            finish()
        } else {
            Toast.makeText(
                applicationContext,
                "한번 더 누르면 종료됩니다.",
                Toast.LENGTH_SHORT
            ).show()
        }
        backPressedTime = System.currentTimeMillis()
    }

}
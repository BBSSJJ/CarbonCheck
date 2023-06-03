package kr.co.carboncheck.android.carboncheckapp.view

import kr.co.carboncheck.android.carboncheckapp.network.SseConnection
import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.os.Bundle
import android.os.Process
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kr.co.carboncheck.android.carboncheckapp.R
import kr.co.carboncheck.android.carboncheckapp.databinding.ActivityMainBinding
import kr.co.carboncheck.android.carboncheckapp.dto.GetGroupTargetAmountResponse
import kr.co.carboncheck.android.carboncheckapp.dto.GetUsageResponse
import kr.co.carboncheck.android.carboncheckapp.dto.GetUserDataResponse
import kr.co.carboncheck.android.carboncheckapp.network.RetrofitClient
import kr.co.carboncheck.android.carboncheckapp.network.SseListener
import kr.co.carboncheck.android.carboncheckapp.util.UserPreference
import kr.co.carboncheck.android.carboncheckapp.viewmodel.SharedViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val listPackageInfo: MutableList<PackageInfo> = mutableListOf()
    private lateinit var bottomNavigationView: BottomNavigationView

    // sseConnection 객체 생성
    private val sseConnection = SseConnection()
    private val sseListener = SseListener()

    private val sharedViewModel: SharedViewModel by viewModels()


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



//        if (checkForPermission()) {
//            // 권한이 있으면 메인 액티 비티를 시각화 합니다.
//            setContentView(binding.root)
//            // 설치된 어플 목록을 가져옵니다.
//            setPackageInfoList()
//        } else {
//            // 권한이 존재 하지 않으면 토스트 메세지 출력후 권한 설정 화면 으로 이동 합니다.
//            Toast.makeText(
//                this, "Check Permission", Toast.LENGTH_LONG
//            ).show()
//            startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
//        }

        //이메일로 유저 데이터들 가져와 preference에 넣는다.
        Log.d("testlog", "get email preference " + getEmailPreference(this))
        getUserData(getEmailPreference(this)) { userData ->
            if (userData != null) {
                setUserDataPreference(this, userData.userId, userData.homeServerId, userData.name)
                getUserDataPreference(this)["userId"]?.let { Log.d("testlog", it) }
                getUserDataPreference(this)["homeServerId"]?.let { Log.d("testlog", it) }
                getUserDataPreference(this)["name"]?.let { Log.d("testlog", it) }
                var userId = userData.userId
                var homeServerId = userData.homeServerId

                lifecycleScope.launch {
                    if (homeServerId != "") {
                        //SSE 연결
                        sseConnection.connect(userData.homeServerId, userData.userId, sseListener)

                        //그룹 물 사용량 가져오기
                        getGroupWaterUsage(homeServerId) { groupWaterUsageList ->
                            if (groupWaterUsageList != null) {
                                val map = groupWaterUsageList.map { groupWaterUsage ->
                                    groupWaterUsage.str to groupWaterUsage.amount
                                }.toMap()
                                sharedViewModel.setGroupWaterUsage(map);
                                for (groupWaterUsage in groupWaterUsageList) {
                                    Log.d(
                                        "testlog",
                                        groupWaterUsage.str + " " + groupWaterUsage.amount
                                    )
                                }
                            }
                        }
                        //그룹 전기 사용량 가져오기
                        getGroupElectricityUsage(homeServerId) { groupElectricityUsageList ->
                            if (groupElectricityUsageList != null) {
                                val map = groupElectricityUsageList.map { groupElectricityUsage ->
                                    groupElectricityUsage.str to groupElectricityUsage.amount
                                }.toMap()
                                sharedViewModel.setGroupElectricityUsage(map);
                                for (groupElectricityUsage in groupElectricityUsageList) {
                                    Log.d(
                                        "testlog",
                                        groupElectricityUsage.str + " " + groupElectricityUsage.amount
                                    )
                                }
                            }
                        }
                        //그룹원 목표치 가져오기
                        getGroupTargetAmount(homeServerId) { groupTargetAmountList ->
                            if (groupTargetAmountList != null) {
                                val map = groupTargetAmountList.map { targetAmount ->
                                    targetAmount.name to targetAmount.targetAmount.toFloat()
                                }.toMap()
                                sharedViewModel.setGroupTargetValue(map);
                                val list = groupTargetAmountList.map { targetAmount ->
                                    targetAmount.name
                                }
                                sharedViewModel.setGroupMember(list)
                                for (targetAmount in groupTargetAmountList) {
                                    Log.d(
                                        "testlog",
                                        targetAmount.name + " " + targetAmount.targetAmount
                                    )
                                }
                            }
                        }
                    }

                }

                lifecycleScope.launch {
                    //유저 물 사용량 가져오기
                    getUserWaterUsage(userId) { userWaterUsageList ->
                        if (userWaterUsageList != null) {
                            val map = userWaterUsageList.map { userWaterUsage ->
                                userWaterUsage.str to userWaterUsage.amount
                            }.toMap()
                            sharedViewModel.setUserWaterUsage(map);
                            for (userWaterUsage in userWaterUsageList) {
                                Log.d("testlog", userWaterUsage.str + " " + userWaterUsage.amount)
                            }
                        }
                    }
                    //유저 전기 사용량 가져오기
                    getUserElectricityUsage(userId) { userElectricityUsageList ->
                        if (userElectricityUsageList != null) {
                            val map = userElectricityUsageList.map { userElectricityUsage ->
                                userElectricityUsage.str to userElectricityUsage.amount
                            }.toMap()
                            sharedViewModel.setUserElectricityUsage(map);
                            for (userElectricityUsage in userElectricityUsageList) {
                                Log.d(
                                    "testlog",
                                    userElectricityUsage.str + " " + userElectricityUsage.amount
                                )
                            }
                        }
                    }
                }
            }
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

/*
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
*/
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


    private fun getUserData(email: String, callback: (GetUserDataResponse?) -> Unit) {
        Log.d("testlog", "in getUserData method")
        val call = RetrofitClient.userService.getUserDataRequest(email)
        call.enqueue(object : Callback<GetUserDataResponse> {
            override fun onResponse(
                call: Call<GetUserDataResponse>,
                response: Response<GetUserDataResponse>
            ) {
                if (response.isSuccessful) {
                    val userData = response.body()
                    Log.d("testlog", "유저 데이터 도착")
                    callback(userData)
                } else {
                    Log.d("testlog", "유저 데이터 도착 안함")
                    callback(null)
                }
            }

            override fun onFailure(call: Call<GetUserDataResponse>, t: Throwable) {
                Log.e("testlog", "유저 데이터 요청 전송 실패: " + t.message)
                callback(null)
            }
        })
    }

    private fun setUserDataPreference(
        context: Context?,
        userId: String?,
        homeServerId: String?,
        name: String?
    ) {
        val userPreference = UserPreference().getPreferences(context!!)
        val editor = userPreference!!.edit()
        editor.putString("userId", userId)
        editor.putString("homeServerId", homeServerId)
        editor.putString("name", name)
        editor.apply()
    }

    private fun getUserDataPreference(context: Context): Map<String, String?> {
        val userPreference = UserPreference().getPreferences(context!!)
        val LoginInfo: MutableMap<String, String?> = HashMap()
        val userId = userPreference!!.getString("userId", "")
        val homeServerId = userPreference!!.getString("homeServerId", "")
        val name = userPreference!!.getString("name", "")
        LoginInfo["userId"] = userId
        LoginInfo["homeServerId"] = homeServerId
        LoginInfo["name"] = name
        return LoginInfo
    }

    private fun getEmailPreference(context: Context): String {
        val userPreference = UserPreference().getPreferences(context!!)
        val email = userPreference!!.getString("email", "")!!
        return email
    }

    private fun getUserWaterUsage(userId: String, callback: (List<GetUsageResponse>?) -> Unit) {
        Log.d("testlog", "in getUserWaterUsage")
        val call = RetrofitClient.usageService.getUserWaterUsageRequest(userId)
        call.enqueue(object : Callback<List<GetUsageResponse>> {
            override fun onResponse(
                call: Call<List<GetUsageResponse>>,
                response: Response<List<GetUsageResponse>>
            ) {
                if (response.isSuccessful) {
                    val userWaterUsageList = response.body()
                    Log.d("testlog", "유저 물 사용량 도착")
                    callback(userWaterUsageList)
                } else {
                    Log.d("testlog", "유저 물 사용량 도착 안함")
                    callback(null)
                }
            }

            override fun onFailure(call: Call<List<GetUsageResponse>>, t: Throwable) {
                Log.d("testlog", "유저 물 사용량 요청 전송 실패" + t.message)
                callback(null)
            }
        })
    }

    private fun getUserElectricityUsage(
        userId: String,
        callback: (List<GetUsageResponse>?) -> Unit
    ) {
        Log.d("testlog", "in getUserElectricityUsage")
        val call = RetrofitClient.usageService.getUserElectricityUsageRequest(userId)
        call.enqueue(object : Callback<List<GetUsageResponse>> {
            override fun onResponse(
                call: Call<List<GetUsageResponse>>,
                response: Response<List<GetUsageResponse>>
            ) {
                if (response.isSuccessful) {
                    val userElectricityUsageList = response.body()
                    Log.d("testlog", "유저 전기 사용량 도착")
                    callback(userElectricityUsageList)
                } else {
                    Log.d("testlog", "유저 전기 사용량 도착 안함")
                    callback(null)
                }
            }

            override fun onFailure(call: Call<List<GetUsageResponse>>, t: Throwable) {
                Log.d("testlog", "유저 전기 사용량 요청 전송 실패" + t.message)
                callback(null)
            }
        })
    }

    private fun getGroupWaterUsage(
        homeServerId: String,
        callback: (List<GetUsageResponse>?) -> Unit
    ) {
        Log.d("testlog", "in getGroupWaterUsage")
        val call = RetrofitClient.usageService.getGroupWaterUsageRequest(homeServerId)
        call.enqueue(object : Callback<List<GetUsageResponse>> {
            override fun onResponse(
                call: Call<List<GetUsageResponse>>,
                response: Response<List<GetUsageResponse>>
            ) {
                if (response.isSuccessful) {
                    val groupWaterUsageList = response.body()
                    Log.d("testlog", "그룹 물 사용량 도착")
                    callback(groupWaterUsageList)
                } else {
                    Log.d("testlog", "그룹 물 사용량 도착 안함")
                    callback(null)
                }
            }

            override fun onFailure(call: Call<List<GetUsageResponse>>, t: Throwable) {
                Log.d("testlog", "그룹 물 사용량 요청 전송 실패" + t.message)
                callback(null)
            }
        })
    }

    private fun getGroupElectricityUsage(
        homeServerId: String,
        callback: (List<GetUsageResponse>?) -> Unit
    ) {
        Log.d("testlog", "in getGroupElectricityUsage")
        val call = RetrofitClient.usageService.getGroupElectricityUsageRequest(homeServerId)
        call.enqueue(object : Callback<List<GetUsageResponse>> {
            override fun onResponse(
                call: Call<List<GetUsageResponse>>,
                response: Response<List<GetUsageResponse>>
            ) {
                if (response.isSuccessful) {
                    val groupElectricityUsageList = response.body()
                    Log.d("testlog", "그룹 전기 사용량 도착")
                    callback(groupElectricityUsageList)
                } else {
                    Log.d("testlog", "그룹 전기 사용량 도착 안함")
                    callback(null)
                }
            }

            override fun onFailure(call: Call<List<GetUsageResponse>>, t: Throwable) {
                Log.d("testlog", "그룹 전기 사용량 요청 전송 실패" + t.message)
                callback(null)
            }
        })
    }

    private fun getGroupTargetAmount(
        homeServerId: String,
        callback: (List<GetGroupTargetAmountResponse>?) -> Unit
    ) {
        Log.d("testlog", "in getGroupTargetAmount")
        val call = RetrofitClient.userService.getGroupTargetAmountRequest(homeServerId)
        call.enqueue(object : Callback<List<GetGroupTargetAmountResponse>> {
            override fun onResponse(
                call: Call<List<GetGroupTargetAmountResponse>>,
                response: Response<List<GetGroupTargetAmountResponse>>
            ) {
                if (response.isSuccessful) {
                    val groupTargetAmountList = response.body()
                    Log.d("testlog", "그룹 목표량 도착")
                    callback(groupTargetAmountList)
                } else {
                    Log.d("testlog", "그룹 목표량 도착 안함")
                    callback(null)
                }
            }

            override fun onFailure(call: Call<List<GetGroupTargetAmountResponse>>, t: Throwable) {
                Log.d("testlog", "그룹 목표량 요청 전송 실패")
                callback(null)
            }
        })
    }


    //어플 종료 시 SSE연결 종료
    override fun onDestroy() {
        super.onDestroy()
        sseConnection.disconnect()
    }
}
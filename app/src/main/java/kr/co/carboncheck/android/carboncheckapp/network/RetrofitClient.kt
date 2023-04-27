package kr.co.carboncheck.android.carboncheckapp.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // 안드로이드 앱, 서버가 같은 pc에서 사용중이면 안드로이드 앱에 서버로 8080포트로 요청 보낼 때 사용하는 URL은
    // http://10.0.2.2:8080 라고 합니다. local host나 127.0.0.1은 에뮬레이터 자체를 가리키기 때문입니다.
    // 10.0.2.2 는 host PC를 가리킨다고 합니다. 에휴

    //goorm server와 통신
//    private const val BASE_URL = "https://carboncheckserver-glzul.run.goorm.site/"

    //local에서 통신
    private const val BASE_URL = "http://10.0.2.2:8080"

    private val retrofit =
        Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create())
            .build()

    // MyService 인터페이스를 가져와서 retrofit 객체를 생성한다
    val service = retrofit.create(MyService::class.java)
}
package kr.co.carboncheck.android.carboncheckapp.network

import kr.co.carboncheck.android.carboncheckapp.model.MyRequest
import kr.co.carboncheck.android.carboncheckapp.model.MyResponse
import retrofit2.Call
import retrofit2.http.*

interface MyService {
    //HTTP POST메소드로 /data에 요청 보낸다
    //파라미터로 들어간 MyRequest객체는 서버로 보낼 객체 MyResponse는 서버에서 받을 객체 타입
    @POST("/data")
    fun postData(@Body request: MyRequest): Call<MyResponse>
}
package kr.co.carboncheck.android.carboncheckapp.network

import kr.co.carboncheck.android.carboncheckapp.dto.*
import retrofit2.Call
import retrofit2.http.*

interface UserService {
    //HTTP POST메소드로 /data에 요청 보낸다
    //파라미터로 들어간 MyRequest객체는 서버로 보낼 객체 MyResponse는 서버에서 받을 객체 타입

    @POST("/join")
    fun postJoinRequest(@Body joinRequest: JoinRequest): Call<JoinResponse>

    @POST("/login")
    fun postLoginRequest(@Body loginRequest: LoginRequest): Call<LoginResponse>

    @GET("/get/user_data")
    fun getUserDataRequest(@Query("email") email: String): Call<GetUserDataResponse>

    @POST("/face/register")
    fun postRegisterFaceRequest(@Body registerFaceRequest: RegisterFaceRequest): Call<RegisterFaceResponse>
}
package kr.co.carboncheck.android.carboncheckapp.network

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.sse.EventSource
import okhttp3.sse.EventSourceListener
import okhttp3.sse.EventSources
import java.util.Base64
import java.util.concurrent.TimeUnit


class SseConnection {
    private var eventSource: EventSource? = null
    private val BASE_URL = "http://10.0.2.2:8080"
//    private val BASE_URL = "https://carboncheckserver-glzul.run.goorm.site/"
//    private val BASE_URL = "http://52.79.136.190:50976/"


    private var homeServerId: String? = null
    private var userId: String? = null
    private var listener: EventSourceListener? = null


    fun connect(homeServerId: String, userId: String, listener: EventSourceListener) {
        this.homeServerId = homeServerId
        this.userId =  userId
        this.listener = listener

        val URL = "$BASE_URL/subscribe/$homeServerId/$userId"

        val client = OkHttpClient.Builder().build()
//            .readTimeout(0, TimeUnit.MILLISECONDS)
//            .writeTimeout(0, TimeUnit.MILLISECONDS)
//            .callTimeout(0, TimeUnit.MILLISECONDS)

        val request = Request.Builder().url(URL).build()

        val eventSourceFactory = EventSources.createFactory(client)
        eventSource = eventSourceFactory.newEventSource(request, listener)
    }

    fun reconnect(){
        disconnect()
        connect(this.homeServerId!!, this.userId!!, this.listener!!)
    }

    fun disconnect() {
        eventSource?.cancel()
    }
}
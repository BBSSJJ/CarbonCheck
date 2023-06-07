package kr.co.carboncheck.android.carboncheckapp.network

import android.app.ProgressDialog
import android.util.Log
import com.google.gson.Gson
import okhttp3.Response
import okhttp3.sse.EventSource
import okhttp3.sse.EventSourceListener

class SseListener : EventSourceListener() {
    private val gson = Gson()
//    private var progressDialog: ProgressDialog = null
    override fun onOpen(eventSource: EventSource, response: Response) {
        super.onOpen(eventSource, response)
        Log.d("testlog", "SSE연결이 열렸습니다")
    }

    override fun onClosed(eventSource: EventSource) {
        super.onClosed(eventSource)
        Log.d("testlog", "SSE연결이 닫혔습니다")

    }

    override fun onEvent(eventSource: EventSource, id: String?, type: String?, data: String) {
        super.onEvent(eventSource, id, type, data)
        Log.d("testlog", data)
        val jsonData = gson.fromJson(data, SseDto::class.java)
        if(jsonData.msg == "register_finish"){
//            progressDialog?.dismiss()
        }
    }

    override fun onFailure(eventSource: EventSource, t: Throwable?, response: Response?) {
        println("SSE연결이 실패했습니다.")
        t?.printStackTrace()
    }
}
package kr.co.carboncheck.android.carboncheckapp.network

import android.util.Log
import okhttp3.Response
import okhttp3.sse.EventSource
import okhttp3.sse.EventSourceListener

class SseListener : EventSourceListener() {
    override fun onOpen(eventSource: EventSource, response: Response) {
        super.onOpen(eventSource, response)
        Log.d("testlog", "SSE연결이 열렸습니다")
    }

    override fun onClosed(eventSource: EventSource) {
        super.onClosed(eventSource)
    }

    override fun onFailure(eventSource: EventSource, t: Throwable?, response: Response?) {
        println("SSE 연결이 실패했습니다.")
        t?.printStackTrace()
    }
}
package com.example.challenge48h

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import okhttp3.*

class MachineWebSocketListener(private val context : Context) : WebSocketListener() {
    override fun onOpen(webSocket: WebSocket, response: Response) {
        Log.d("Debugging app", "Opened connection")
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        Log.d("Debugging app", "Error : " + t.message.toString())
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        Log.d("Debugging app", "Connection closing")
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        Log.d("Debugging app", "Connection closed")
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        val gson = Gson()
        val coffeeStatus = gson.fromJson(text, MachineStatusResponse::class.java).coffeeStatus
        context as MainActivity
        context.onNewStatusReceived(coffeeStatus)
    }
}
package fr.newglace.notedesnazes.WebSocket

import android.util.Log
import com.neovisionaries.ws.client.WebSocket
import com.neovisionaries.ws.client.WebSocketAdapter
import com.neovisionaries.ws.client.WebSocketException
import com.neovisionaries.ws.client.WebSocketFactory
import fr.newglace.notedesnazes.BuildConfig
import java.io.IOException
import java.util.concurrent.ExecutionException
import java.util.concurrent.Executors

class client {
    private var ws: WebSocket? = null
    private var strWS: String? = null
    fun sendMessage(str: String): String? {
        var value: String? = null
        try {
            value = sendMessageWait(str)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

        if (BuildConfig.DEBUG && value == null) {
            error("Assertion failed")
        }
        return value
    }

    @Throws(InterruptedException::class)
    private fun sendMessageWait(str: String): String {
        return if (ws!!.isOpen) {
            ws!!.sendText(str)
            var ticks = 0
            for (i in 0..19) {
                if (strWS == null) {
                    ticks++
                    Thread.sleep(50)
                } else break
            }
            Log.d("/***/ Ticks Number", (20 - ticks).toString() + " ticks")
            if (strWS == null) return "no connection"
            val array = strWS!!.split(" % ".toRegex()).toTypedArray()
            val strEnd: String = strWS as String
            strWS = null
            if (array[0] == "error") {
                if (array.size > 2) Log.d("/***/ WebSocket Error", array[2]) else Log.d("/***/ WebSocket Error", array[1])
                "Error"
            } else {
                if (array.size > 2) Log.d("/***/ WebSocket Success", array[2]) else Log.d("/***/ WebSocket Success", array[1])
                strEnd
            }
        } else "no connection"
    }

    init {
        val factory = WebSocketFactory().setConnectionTimeout(5000)
        try {
            ws = factory.createSocket("ws://45.90.160.218:2408")
            ws?.addListener(object : WebSocketAdapter() {
                @Throws(Exception::class)
                override fun onTextMessage(websocket: WebSocket, message: String) {
                    strWS = message
                }
            })
            val es = Executors.newSingleThreadExecutor()
            val future = ws?.connect(es)
            try {
                future?.get()
            } catch (e: ExecutionException) {
                if (e.cause is WebSocketException) {
                    e.cause
                }
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}

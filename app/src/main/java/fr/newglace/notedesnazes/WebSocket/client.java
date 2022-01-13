package fr.newglace.notedesnazes.WebSocket;

import android.util.Log;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class client {
    private WebSocket ws;
    private String strWS = null;

    public client() {
        WebSocketFactory factory = new WebSocketFactory().setConnectionTimeout(5000);
        try {
            ws = factory.createSocket("ws://45.90.160.218:2408");
            ws.addListener(new WebSocketAdapter() {
                @Override
                public void onTextMessage(WebSocket websocket, String message) throws Exception {
                    strWS = message;
                }
            });

            ExecutorService es = Executors.newSingleThreadExecutor();
            Future<WebSocket> future = ws.connect(es);
            try {
                future.get();
            }
            catch (ExecutionException e) {
                if (e.getCause() instanceof WebSocketException) {
                    e.getCause();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String sendMessage(String str) {
        String value = null;
        try {
            value = sendMessageWait(str);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assert value != null;
        return value;
    }

    private String sendMessageWait(String str) throws InterruptedException {
        if (ws.isOpen()) {
            ws.sendText(str);
            int ticks = 0;
            for (int i = 0; i < 20; i++) {
                if (strWS == null) {
                    ticks++;
                    Thread.sleep(50);
                } else break;
            }

            Log.d("/***/ Ticks Number", ( 20 - ticks)+" ticks");
            if (strWS == null) return "no connection";

            String[] array = strWS.split(" % ");

            String strEnd = strWS;
            strWS = null;

            if (array[0].equals("error")) {
                if (array.length > 2) Log.d("/***/ WebSocket Error", array[2]);
                else Log.d("/***/ WebSocket Error", array[1]);

                return "Error";
            } else {
                if (array.length > 2) Log.d("/***/ WebSocket Success", array[2]);
                else Log.d("/***/ WebSocket Success", array[1]);

                return strEnd;
            }
        } else return "no connection";
    }
}

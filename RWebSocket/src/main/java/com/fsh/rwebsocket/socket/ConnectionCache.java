package com.fsh.rwebsocket.socket;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import okhttp3.WebSocket;

/**
 * 保存WebSocket连接对象
 */
public class ConnectionCache {
    private Map<Integer, WebSocket> webSocketMap;
    private WebSocket currentConnection;//当前最新的连接对象
    public ConnectionCache(){
        webSocketMap = new ConcurrentHashMap<>();
    }

    public void addWebSocket(WebSocket webSocket){
        webSocketMap.put(webSocket.hashCode(),webSocket);
        currentConnection = webSocket;
    }

    public void removeWebSocket(WebSocket webSocket){
        webSocketMap.remove(webSocket.hashCode());
        if(currentConnection == webSocket){
            currentConnection = null;
        }
    }

    public boolean isConnected(){
        return currentConnection != null;
    }

    public WebSocket getCurrentConnection(){
        return currentConnection;
    }

    public void disConnect(){
        currentConnection = null;
        webSocketMap.clear();
    }
}

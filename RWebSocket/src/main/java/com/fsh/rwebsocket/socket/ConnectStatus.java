package com.fsh.rwebsocket.socket;

/**
 * WebSocket的连接状态
 */
public enum  ConnectStatus {
    /**
     * 连接状态
     */
    CONNECTED(0,"连接成功"),
    /**
     * 未连接
     */
    NOT_CONNECTED(1,"没有可用连接"),
    /**
     * 网络正在断开
     */
    CONNECTION_CLOSING(2,"正在断开连接"),
    /**
     * 断开连接
     */
    CONNECTION_CLOSE(3,"连接已经断开"),
    /**
     * 连接失败
     */
    CONNECT_FAILED(4,"连接失败"),
    /**
     * 网络重连
     */
    CONNECT_RETRY(5,"重新连接中"),
    /**
     * 连接中
     */
    CONNECTING(5,"正在连接中"),
    /**
     * WebSocket初始化
     */
    WEBSOCKET_INIT(6,"WebSocket正在初始化"),
    /**
     * 手动关闭连接
     */
    CONNECT_CLOSE_MANUAL(7,"手动关闭连接");
    ;

    private int statusCode;
    private String statusMsg;

    ConnectStatus(int code,String msg){
        statusCode = code;
        statusMsg = msg;
    }

    public int getStatusCode(){
        return statusCode;
    }

    public String getStatusMsg(){
        return statusMsg;
    }
}

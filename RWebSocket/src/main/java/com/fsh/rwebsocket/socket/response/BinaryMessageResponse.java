package com.fsh.rwebsocket.socket.response;

import com.fsh.rwebsocket.socket.WebSocketResponseMsgType;

import okio.ByteString;

/**
 * byte数据类型消息
 * 当WebSocket接收到了byte数据时发送此消息
 */
public class BinaryMessageResponse extends WebSocketResponse {
    private ByteString response;
    public BinaryMessageResponse(ByteString response) {
        super(WebSocketResponseMsgType.MESSAGE_BINARY);
        this.response = response;
    }

   public ByteString getResponse(){
       return response;
   }
}

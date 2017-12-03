# RWebSocket

RWebSocket是一款基于OKHttp和RxJava实现的WebSocket连接工具，支持发送json字符串、普通字符窜
和ByteString三种数据类型消息。其中断线重连部分参考了[WsManager](https://github.com/Rabtman/WsManager)

## 使用方法

1. 初始化WebSocketConnectHandler对象
```
handler = new WebSocketConnectHandler.Builder()
                .url("ws://192.168.1.72:10000")//webSocket连接地址
                .needReConnect(true)//是否在断开连接后自动重连
                .observerOn(AndroidSchedulers.mainThread())//数据回调发生在主线程,基于RxJava实现
                .subscribeOn(Schedulers.io())//数据发生在io线程
                .observer(this)//观察者
                .okClient(client)//OkHttpClient对象
                .build();
```

2. 发送消息

```
//JSON字符串数据，需要竭诚JSONRequest接口
RequestPacket request = new RequestPacket(new TextRequest(input.getText().toString()));
handler.sendMessage(request);//JSON字符串
handler.sendTextMessage("您好");//普通字符串
handler.sendBinaryMsg(ByteString.of("您好".getBytes()));//byteString

```

* 注意

1. 发送JSON字符串数据请求时，需要实现JSONRequest接口

```
public interface JSONRequest {
    String toJsonString();

    JSONObject toJSONObject();
}
```

3. 消息回调

```
public void onNext(@NonNull WebSocketResponse response) {
        Log.d(TAG,"================================================="+Thread.currentThread().getName()+"===================================================================");
        Log.d(TAG,"接收到了消息:"+response.getType().name());
        WebSocketResponseMsgType type = response.getType();
        switch (type) {
            case CONNECTSTATUS://WebSocket连接状态消息
                Log.d(TAG, Thread.currentThread().getName()+"当前连接状态:" + ((ConnectStatusMsg) response).getStatus().getStatusMsg());
                showText.append("接收到了状态消息："+((ConnectStatusMsg) response).getStatus().getStatusMsg()+"\n");
                break;
            case ERROR_MESSAGE://错误消息
                Log.d(TAG, Thread.currentThread().getName()+"消息发送失败:" + ((ErrorResponse) response).getMsg());
                showText.append("接收到消息发送失败："+((ErrorResponse) response).getMsg()+"\n");
                break;
            case MESSAGE_TEXT://字符串数据
                Log.d(TAG, Thread.currentThread().getName()+"接收到服务器端的文本消息:" + ((TextMessageResponse) response).getResponse());
                showText.append("接收到服务器端的文本消息："+((TextMessageResponse) response).getResponse()+"\n");
                break;
            case MESSAGE_BINARY://ByteString数据
                Log.d(TAG, Thread.currentThread().getName()+"接收到服务器端的byte消息:" + ((BinaryMessageResponse) response).getResponse().utf8());
                showText.append("接收到服务器端的byte消息："+((BinaryMessageResponse) response).getResponse().utf8()+"\n");
                break;
        }
    }
```

## 最终结果

![https://github.com/fshlny/RWebScoket/blob/master/shortCut/sample.png]()
package com.fsh.webscoket;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.fsh.rwebsocket.socket.ConnectStatus;
import com.fsh.rwebsocket.socket.WebSocketConnectHandler;
import com.fsh.rwebsocket.socket.WebSocketResponseMsgType;
import com.fsh.rwebsocket.socket.response.BinaryMessageResponse;
import com.fsh.rwebsocket.socket.response.ConnectStatusMsg;
import com.fsh.rwebsocket.socket.response.ErrorResponse;
import com.fsh.rwebsocket.socket.response.TextMessageResponse;
import com.fsh.rwebsocket.socket.response.WebSocketResponse;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;

public class MainActivity extends AppCompatActivity implements Observer<WebSocketResponse>{
    private static final String TAG = MainActivity.class.getSimpleName();
    private EditText input;
    private WebSocketConnectHandler handler;
    private OkHttpClient client;
    private TextView showText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        input = (EditText) findViewById(R.id.input);
        showText = (TextView) findViewById(R.id.msg);
        showText.setText("数据开始初始化：\n");
        client = new OkHttpClient.Builder()
                .retryOnConnectionFailure(true)
                .build();
        handler = new WebSocketConnectHandler.Builder()
                .url("ws://192.168.1.72:10000")
                .needReConnect(true)
                .observerOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .observer(MainActivity.this)
                .reConnectTimeout(3*1000)//断开3秒后自动去重连
                .okClient(client)
                .build();
    }

    public void send(View view){
        if(handler == null || handler.getStatus() != ConnectStatus.CONNECTED
                || TextUtils.isEmpty(input.getText())){
            return;
        }
        RequestPacket request = new RequestPacket(new TextRequest(input.getText().toString()));
        handler.sendMessage(request);
        //handler.sendTextMessage("您好");
        //handler.sendBinaryMsg(ByteString.of("您好".getBytes()));
        input.setText("");
        showText.append("发送消息："+request.toJsonString()+"\n");
    }

    public void disConnect(View view){
        if(handler != null){//连接
            handler.disConnect();
        }
    }



    @Override
    public void onSubscribe(@NonNull Disposable d) {

    }

    @Override
    public void onNext(@NonNull WebSocketResponse response) {
        Log.d(TAG,"================================================="+Thread.currentThread().getName()+"===================================================================");
        Log.d(TAG,"接收到了消息:"+response.getType().name());
        WebSocketResponseMsgType type = response.getType();
        switch (type) {
            case CONNECTSTATUS:
                Log.d(TAG, Thread.currentThread().getName()+"当前连接状态:" + ((ConnectStatusMsg) response).getStatus().getStatusMsg());
                showText.append("接收到了状态消息："+((ConnectStatusMsg) response).getStatus().getStatusMsg()+"\n");
                break;
            case ERROR_MESSAGE:
                Log.d(TAG, Thread.currentThread().getName()+"消息发送失败:" + ((ErrorResponse) response).getMsg());
                showText.append("接收到消息发送失败："+((ErrorResponse) response).getMsg()+"\n");
                break;
            case MESSAGE_TEXT:
                Log.d(TAG, Thread.currentThread().getName()+"接收到服务器端的文本消息:" + ((TextMessageResponse) response).getResponse());
                showText.append("接收到服务器端的文本消息："+((TextMessageResponse) response).getResponse()+"\n");
                break;
            case MESSAGE_BINARY:
                Log.d(TAG, Thread.currentThread().getName()+"接收到服务器端的byte消息:" + ((BinaryMessageResponse) response).getResponse().utf8());
                showText.append("接收到服务器端的byte消息："+((BinaryMessageResponse) response).getResponse().utf8()+"\n");
                break;
        }
    }

    @Override
    public void onError(@NonNull Throwable e) {

    }

    @Override
    public void onComplete() {

    }

    @Override
    protected void onDestroy() {
        if(this.handler != null)
            handler.shutDown();
        super.onDestroy();
    }
}

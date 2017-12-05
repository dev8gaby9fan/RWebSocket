package com.fsh.rwebsocket.socket;

import android.app.AlertDialog;
import android.os.Build;
import android.util.Log;

import com.fsh.rwebsocket.packets.request.JSONRequest;
import com.fsh.rwebsocket.socket.execption.ApiException;
import com.fsh.rwebsocket.socket.response.BinaryMessageResponse;
import com.fsh.rwebsocket.socket.response.ConnectStatusMsg;
import com.fsh.rwebsocket.socket.response.ErrorResponse;
import com.fsh.rwebsocket.socket.response.TextMessageResponse;
import com.fsh.rwebsocket.socket.response.WebSocketResponse;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class WebSocketConnectHandler extends WebSocketListener {
    private static final String TAG = WebSocketConnectHandler.class.getSimpleName();
    /**
     * OkHttpClient
     */
    private OkHttpClient mOkHttpClient;
    /**
     * WebSocket连接请求
     */
    private Request mRequest;
    /**
     * 锁对象
     */
    private ReentrantLock mLock; //重复创建连接
    /**
     * 当前连接对象
     */
    private ConnectStatus cStatus;//当前状态
    /**
     * WebSocket地址
     */
    private String url;
    /**
     * 是否需要重新连接
     */
    private boolean needReConnect;
    /**
     * WebSocket连接对象
     */
    private WebSocket mWebSocket;
    /**
     * webSocket消息发送者（生产者）
     */
    private Observable<WebSocketResponse> webSocketDataStream;
    /**
     * 实际发送对象，Rx Emitter
     */
    private ObservableEmitter<WebSocketResponse> emitter;
    /**
     * 重连次数
     */
    private volatile int reconnectCount;
    /**
     * 认为关闭
     */
    private boolean closeByManual;
    /**
     * 发送消息线程池和连接线程池
     */
    private ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
    /**
     * 重连线程迟
     */
    private ScheduledExecutorService reConnectThreadPool = Executors.newScheduledThreadPool(1);

    private long reconnectTimeout;

    private WebSocketConnectHandler(Builder b) {
        this.cStatus = ConnectStatus.WEBSOCKET_INIT;
        this.url = b.url;
        this.needReConnect = b.needReConnect;
        this.mLock = new ReentrantLock();
        this.reconnectTimeout = b.reConnectTimeout;
        this.mOkHttpClient = b.client;
        webSocketDataStream = Observable.create(new ObservableOnSubscribe<WebSocketResponse>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<WebSocketResponse> e) throws Exception {
                emitter = e;
            }
        });
        webSocketDataStream.subscribeOn(b.subscribeOn)
                            .observeOn(b.observerOn)
                            .subscribe(b.observer);
        connect();
    }

    private void connect() {
        cachedThreadPool.submit(new Runnable() {
            @Override
            public void run() {
                initWebSocketConnect();
            }
        });
    }

    private void initWebSocketConnect() {
        setStatus(ConnectStatus.CONNECTING);
        if (mRequest == null) {
            mRequest = new Request.Builder()
                    .url(this.url).build();
        }
        mOkHttpClient.dispatcher().cancelAll();
        try {
            mLock.lockInterruptibly();
            mOkHttpClient.newWebSocket(mRequest, this);
//            mOkHttpClient.dispatcher().executorService().shutdown();
            Log.d(TAG,Thread.currentThread().getName()+" connect to webSocket");
        } catch (Exception e) {
            Log.e(TAG, "WebSocket newWebSocket error", e);
            setStatus(ConnectStatus.CONNECT_FAILED);
        } finally {
            mLock.unlock();
        }
    }

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        this.mWebSocket = webSocket;
        this.reconnectCount = 0;
        setStatus(ConnectStatus.CONNECTED);
        Log.e(TAG, "connect success");
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        this.emitter.onNext(new TextMessageResponse(text));
    }

    @Override
    public void onClosing(WebSocket webSocket, int code, String reason) {
        setStatus(ConnectStatus.CONNECTION_CLOSE);
    }

    @Override
    public void onMessage(WebSocket webSocket, ByteString bytes) {
        this.emitter.onNext(new BinaryMessageResponse(bytes));
    }

    @Override
    public void onClosed(WebSocket webSocket, int code, String reason) {
        Log.e(TAG, "连接关闭:" + reason);
        setStatus(ConnectStatus.CONNECTION_CLOSE);
        if(code != ConnectStatus.CONNECT_CLOSE_MANUAL.getStatusCode()){
            autoReconnect();
            return;
        }
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        if(cStatus == ConnectStatus.CONNECT_CLOSE_MANUAL){//手动关闭连接
//            setStatus(ConnectStatus.CONNECT_CLOSE_MANUAL);
            this.closeByManual = false;//设置为false，可以继续重新连接
        }else{
            setStatus(ConnectStatus.CONNECTION_CLOSE);
            autoReconnect();
            Log.e(TAG, "连接失败:");
        }
    }

    private void  setStatus(ConnectStatus s) {
        try{
            mLock.lock();
            this.cStatus = s;
            if(emitter != null)
                this.emitter.onNext(new ConnectStatusMsg(this.cStatus));
        }finally {
            mLock.unlock();
        }


    }

    public ConnectStatus getStatus() {
        return this.cStatus;
    }

    public static class Builder {
        private String url;
        private boolean needReConnect;//是否需要重连
        private Scheduler subscribeOn = Schedulers.io();//默认事件在子线程中emit
        private Scheduler observerOn = Schedulers.io();
        private long reConnectTimeout = 8*1000;//默认10断开后8秒重连
        private Observer<WebSocketResponse> observer;
        private OkHttpClient client;

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder needReConnect(boolean needReConnect) {
            this.needReConnect = needReConnect;
            return this;
        }

        public Builder observer(Observer<WebSocketResponse> observer) {
            this.observer = observer;
            return this;
        }

        public Builder subscribeOn(Scheduler scheduler){
            this.subscribeOn = scheduler;
            return this;
        }

        public Builder observerOn(Scheduler scheduler){
            this.observerOn = scheduler;
            return this;
        }

        public Builder reConnectTimeout(long timeOut){
            this.reConnectTimeout = timeOut;
            return this;
        }

        public Builder okClient(OkHttpClient client){
            this.client = client;
            return this;
        }

        public WebSocketConnectHandler build() {
            return new WebSocketConnectHandler(this);
        }
    }

    private boolean checkCurrentStatus() {
        if (cStatus != ConnectStatus.CONNECTED) {
            throw new ApiException(cStatus.getStatusMsg());
        }
        return true;
    }

    /**
     * 发送json字符串数据
     * @param request JSON请求
     */
    public void sendMessage(final JSONRequest request) {
        sendTextMessage(request.toJsonString());
    }

    /**
     * 发送字符串消息
     * @param msg
     */
    public void sendTextMessage(final String msg){
        try {
            if (checkCurrentStatus()) {
                cachedThreadPool.submit(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG,Thread.currentThread().getName()+" send String WebSocket message");
                        mWebSocket.send(msg);
                    }
                });
            }
        } catch (RuntimeException e) {
            this.emitter.onNext(new ErrorResponse(e.getMessage(), e));
        }
    }

    /**
     * 发送ByteString类型的请求
     * @param msg
     */
    public void sendBinaryMsg(final ByteString msg){
        try {
            if (checkCurrentStatus()) {
                cachedThreadPool.submit(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG,Thread.currentThread().getName()+" sendByte WebSocket message");
                        mWebSocket.send(msg);
                    }
                });
            }
        } catch (RuntimeException e) {
            this.emitter.onNext(new ErrorResponse(e.getMessage(), e));
        }
    }

    /**
     * 手动关闭连接
     */
    public synchronized void disConnect() {
        if (this.cStatus != ConnectStatus.CONNECTED) {
            return;
        }

        reConnectThreadPool.submit(new Runnable() {
            @Override
            public void run() {
                closeByManual = true;
                setStatus(ConnectStatus.CONNECTION_CLOSING);
                if (mOkHttpClient != null) {
                    mOkHttpClient.dispatcher().cancelAll();
                }
                if (mWebSocket != null) {
                    Log.d(TAG,Thread.currentThread().getName()+" disconnect");
                    reconnectCount = 0;
                    setStatus(ConnectStatus.CONNECT_CLOSE_MANUAL);
                    boolean isClosed = mWebSocket.close(ConnectStatus.CONNECT_CLOSE_MANUAL.getStatusCode(),
                            ConnectStatus.CONNECT_CLOSE_MANUAL.getStatusMsg());
                    Log.e(TAG,"WebSocket是否关闭成功"+isClosed);
                }

            }
        });
    }

    public void reconnect(){
        if (this.cStatus == ConnectStatus.WEBSOCKET_INIT || this.cStatus == ConnectStatus.CONNECTED) {
            return;
        }
        if (!this.needReConnect || this.closeByManual) {
            return;
        }
        setStatus(ConnectStatus.CONNECT_RETRY);
        connect();
    }

    /**
     * 重连
     */
    private synchronized void autoReconnect() {
        if (this.cStatus == ConnectStatus.WEBSOCKET_INIT || this.cStatus == ConnectStatus.CONNECTED) {
            return;
        }
        if (!this.needReConnect || this.closeByManual) {
            return;
        }
        setStatus(ConnectStatus.CONNECT_RETRY);

        reconnectCount++;
        reConnectThreadPool.schedule(new Runnable() {
            @Override
            public void run() {
                connect();
            }
        },reconnectTimeout,TimeUnit.MILLISECONDS);
    }

    /**
     * 释放资源
     */
    public void shutDown(){
        if(this.cStatus == ConnectStatus.CONNECTED){
            this.disConnect();
        }
        this.cachedThreadPool.shutdown();
        this.reConnectThreadPool.shutdown();
        if(this.emitter != null)
            this.emitter.onComplete();
    }
}

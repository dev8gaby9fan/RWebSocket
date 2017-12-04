package com.fsh.rwebsocket.socket.common;

import java.lang.reflect.Method;

public class MethodFrameContext<T> {
    Method methodReply;
    Class<? extends T> frameReplyClass;

    public MethodFrameContext(Method methodReply,Class<? extends T> frameReplyClass){
        this.methodReply = methodReply;
        this.frameReplyClass = frameReplyClass;
    }

    public Method getMethodReply() {
        return methodReply;
    }

    public Class<? extends T> getFrameReplyClass() {
        return frameReplyClass;
    }
}

package com.fsh.rwebsocket.packets;

/**
 * 描述，连接服务器的版本信息
 */
public class BaseDescription {
    protected int p_ver = 1;
    protected int p_no = 1;

    public int getP_ver() {
        return p_ver;
    }

    public void setP_ver(int p_ver) {
        this.p_ver = p_ver;
    }

    public int getP_no() {
        return p_no;
    }

    public void setP_no(int p_no) {
        this.p_no = p_no;
    }
}

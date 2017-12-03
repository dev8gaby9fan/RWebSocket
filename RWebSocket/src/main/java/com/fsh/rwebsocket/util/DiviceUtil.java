package com.fsh.rwebsocket.util;

import java.util.UUID;

/**
 * Created by Administrator on 2017/9/5.
 */

public class DiviceUtil {
    private static final String TAG = "DeviceIdManager";

    private static final String INVALID_DEVICE_ID = "000000000000000";

    private static final String INVALID_BLUETOOTH_ADDRESS = "02:00:00:00:00:00";

    private static final String INVALID_ANDROID_ID = "9774d56d682e549c";

    private static volatile String sDeviceDigest;

    public static String getDeviceID() {
        // 双重校验锁
        if (sDeviceDigest == null) {
            synchronized (DiviceUtil.class){
                if(sDeviceDigest == null){
                    sDeviceDigest = getUUID();
                }
            }
        }

        return sDeviceDigest;
    }

    private static String getUUID() {
        return UUID.randomUUID().toString();
    }
}

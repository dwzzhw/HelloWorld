package com.loading.deletelater;

import com.loading.modules.data.jumpdata.AppJumpParam;

import java.io.Serializable;

/**
 * 未解锁的道具锁定信息
 */
public class PropLockInfo implements Serializable {
    public String title;
    public String summary;
    public AppJumpParam jumpData;
}

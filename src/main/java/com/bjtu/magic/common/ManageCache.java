package com.bjtu.magic.common;

import java.util.HashMap;
import java.util.Map;

import static com.bjtu.magic.common.Constant.*;
import static com.bjtu.magic.common.Constant.CacheKey.CACHE_KEY_DATAPATH;

/**
 * @Auther: wjx
 * @Date: 2020/4/19 12:49
 * @Description: 缓存数据
 */
public class ManageCache {
    //系统缓存
    private static Map dbCache = new HashMap<String,Object>();
    //数据文件位置
    private static String dataPath;
    //临时变量-当前使用的数据库路径
    public static String tempPath = dataPath;
    //初始化参数
    public static Object getCache(String key){
        return dbCache.get(key);
    }
    public static void putCache(String key,Object value){
        dbCache.put(key,value);
    }

}

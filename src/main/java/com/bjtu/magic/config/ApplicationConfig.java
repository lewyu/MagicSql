package com.bjtu.magic.config;

import com.alibaba.fastjson.JSON;
import com.bjtu.magic.common.Constant;
import com.bjtu.magic.entity.User;
import com.bjtu.magic.execute.Use;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.*;

import static com.bjtu.magic.common.Constant.CacheKey.CACHE_KEY_DATAPATH;
import static com.bjtu.magic.common.Constant.ConfigKey.*;
import static com.bjtu.magic.common.ManageCache.putCache;
import static java.lang.System.exit;
import static java.lang.System.runFinalization;

/**
 * @Auther: wjx
 * @Date: 2020/6/6 22:09
 * @Description:
 */
public class ApplicationConfig {
    //全局配置
    private static final HashMap<String,String> config = new HashMap();
    //登录用户配置
    private static final List<User> loginUsers=new ArrayList();
    //加载所有的数据库
    private static final List<String> databases = new ArrayList<>();
    private static String currentDatabase;
    public static void startApp(){
        try {
            //加载配置
            loadConfig();
            //用户设置的数据目录
            putCache(CACHE_KEY_DATAPATH, config.get(CONFIG_DATA_PATH));
            //加载用户数据
            loadLoginUser();
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("数据库启动异常");
            exit(0);
        }

    }

    private static void loadLoginUser() throws Exception{
        FileInputStream fin = new FileInputStream(config.get(Constant.ConfigKey.CONFIG_USER_PATH));
        ObjectInputStream in = new ObjectInputStream(fin);
        loginUsers.addAll((List)in.readObject());
        in.close();
        fin.close();
    }

    private static void loadConfig() throws Exception {
        Properties properties = new Properties();
        properties.load(new FileInputStream(new File("config/application.properties")));
        Set<String> keys = properties.stringPropertyNames();
        for(String key : keys){
            String value = properties.getProperty(key);
            config.put(key,value);
        }
        System.out.println(config);
        if(!keys.contains(CONFIG_DATA_PATH)||!keys.contains(CONFIG_USER_PATH)){
            throw new RuntimeException(Constant.Error.SQL_CHECK_CONFIG);
        }
    }

    public String get(String key){
        return config.get(key);
    }
    public static boolean isLoginUser(User user){
        for(User login : loginUsers){
            if(user.getUserName().equals(login.getUserName())
                    &&user.getPasswd().equals(login.getPasswd())){
                return true;
            }
        }
        return false;
    }
    public static String getCurrentDatabase(){
        return currentDatabase;
    }

    public static String getDataPath(){
        return config.get(CONFIG_DATA_PATH);
    }
    public static void setCurrentDatabase(String dbName){
        currentDatabase=dbName;
    }
}

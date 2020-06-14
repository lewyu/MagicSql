package com.bjtu.magic.execute;

import com.bjtu.magic.common.Constant;
import com.bjtu.magic.config.ApplicationConfig;
import com.bjtu.magic.entity.User;

import java.util.Scanner;

import static com.bjtu.magic.common.Constant.Error.*;
import static com.bjtu.magic.config.ApplicationConfig.getCurrentDatabase;
import static com.bjtu.magic.config.ApplicationConfig.isLoginUser;
import static java.lang.System.exit;
import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * @Auther: wjx
 * @Date: 2020/6/6 22:45
 * @Description:
 */
public class Check {

    private static boolean isLogin=true;
    private static String currentDatabase;

    public static void login(Scanner in) {
        System.out.print("请输入用户名:");
        String userName = in.nextLine();
        System.out.print("请输入密码:");
        String passwd = in.nextLine();
        if (!isLoginUser(new User(userName, passwd))) {
            System.out.println(SQL_USER_PASSWD);
            exit(0);
        }else{
            isLogin=true;
            System.out.println(Constant.Common.PREFIX+"login successfully.");
        }
    }
    public static void isLogin(){
        if(!isLogin){
            System.out.println(SQL_NOT_LOGIN);
            exit(0);
        }
    }
    public static void isDataBaseUsed(){
        if(isBlank(getCurrentDatabase())){
            throw new RuntimeException(SQL_NO_DB_SELECTED);
        }
    }
}

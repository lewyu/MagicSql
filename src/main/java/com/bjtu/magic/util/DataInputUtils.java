package com.bjtu.magic.util;

import com.bjtu.magic.common.Constant;

import java.util.Scanner;

import static com.bjtu.magic.common.Constant.Common.PREFIX;
import static com.bjtu.magic.common.Constant.Error.SQL_COMMON_ERROR;
import static java.lang.System.exit;

/**
 * @Auther: wjx
 * @Date: 2020/4/19 09:40
 * @Description:
 */
public class DataInputUtils {
    //获取输入的sql
    public static String getInput(Scanner in) {
        StringBuilder sb = new StringBuilder();
        String line;
        boolean isEnd = false;
        while(!isEnd){
            System.out.print(PREFIX);
            line = in.nextLine();
            if(line==null){
                sb.append(SQL_COMMON_ERROR);
                return sb.toString();
            }
            //结束
            if(line.endsWith(";")){
                isEnd=true;
            }
            sb.append(line);
        }
        //退出程序指令
        if(sb.toString().indexOf("exit;")>=0){
            exit(0);
        }
        return sb.toString();
    }
}

package com.bjtu.magic;

import com.alibaba.fastjson.JSON;
import com.bjtu.magic.entity.User;
import com.bjtu.magic.execute.SqlHandler;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

import static com.bjtu.magic.config.ApplicationConfig.startApp;
import static com.bjtu.magic.execute.Check.login;
import static com.bjtu.magic.execute.SqlHandler.analyseInput;
import static com.bjtu.magic.util.DataInputUtils.getInput;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class Main {

    public static void main(String[] args) throws Exception {
//        test1();
        magicMain();
//        loginWrite();
//       loginRead();
//        initApp();
    }

    private static void loginRead() throws Exception {
        FileInputStream fin = new FileInputStream("D:/magicSql/user/user.data");
        ObjectInputStream in = new ObjectInputStream(fin);
        List userList=(List) in.readObject();
        System.out.println(JSON.toJSONString(userList));
        for(Object obj : userList){
            System.out.println(obj);
        }
        in.close();
        fin.close();
    }


    private static void loginWrite() {
        List<User> users = new ArrayList<>();
        users.add(new User("wjx","wjx"));
        users.add(new User("root","root"));
        users.add(new User("magic","magic"));
        try{

            FileOutputStream fout = new FileOutputStream("D:/magicSql/user/user.data");
            ObjectOutputStream out = new ObjectOutputStream(fout);
            out.writeObject(users);
            out.close();
            fout.close();
        }catch(FileNotFoundException e){
            System.out.println("File Not Found!");
        }catch(IOException e){
            System.out.println("I/O Error!");
        }

    }

    private static void test1() throws Exception{
        URL base = Thread.currentThread().getContextClassLoader().getResource("");
        URL resource = Main.class.getClassLoader().getResource("");
        System.out.println(resource.getPath());
        System.out.println("............");
        Properties p = new Properties();
        p.load(new FileInputStream(new File("config/application.properties")));
        System.out.println(p.getProperty("data.path"));
    }

    private static void magicMain() {
        startApp();
        Scanner in =  new Scanner(System.in);
        String input = null;
        String result = null;
        SqlHandler hander =null;
        //非exit命令，循环执行sql
        login(in);
        while(true){
            //获取输入
            input = getInput(in);
            //分析语句
            hander=analyseInput(input);
            //执行语句
            result=hander.executeSql();
            //打印执行结果
            if(isNotBlank(result)){
                System.out.println(result);
            }
        }
    }

}

package com.bjtu.magic.manage;

import com.bjtu.magic.common.Constant;

import java.io.*;
import java.util.List;

import static com.bjtu.magic.common.Constant.Error.SQL_COMMON_ERROR;
import static com.bjtu.magic.config.ApplicationConfig.getDataPath;

/**
 * @Auther: wjx
 * @Date: 2020/4/19 10:14
 * @Description: 文件管理
 */
public class FileManage {
    /**
     * 删除文件or文件夹
     * @param path
     * @param fileName
     */
    public static void dropFile(String path,String fileName){
        File file = new File(path+File.separator+fileName);
        if(file!=null&&file.exists()&&!file.isDirectory()){
            file.delete();
        }else if(file.isDirectory()){
            File[] files = file.listFiles();
            for(File f : files){
                dropFile(path+File.separator+file.getName(),f.getName());
            }
            file.delete();
        } else{
            throw new RuntimeException(SQL_COMMON_ERROR);
        }
    }

    public static void createDir(String path,String dirName) {
        File file = new File(path+File.separator+dirName);
        if(file.exists()&&file.isDirectory()){
            throw new RuntimeException(Constant.Error.SQL_DB_ALREADY_EXIST);
        }
        file.mkdir();
    }
    //如果文件已存在，则替换原来的文件(目录文件不会删除)
    public static void writeObject(String path,String name,Object value){
        FileOutputStream fout = null;
        ObjectOutputStream out = null;
        File file = null;
        try{
            file = new File(path+File.separator+name);
            if(file!=null&&file.exists()&&!file.isDirectory()){
//                file.delete();
                file.deleteOnExit();
            }
            fout = new FileOutputStream(path+File.separator+name);
            out = new ObjectOutputStream(fout);
            out.writeObject(value);
            out.close();
            fout.close();
        }catch (Exception e){
            throw new RuntimeException(Constant.Error.SYSTEM_EXCEPTION);
        }
    }
    //读取失败是否抛异常
    public static Object readObject(String path,String name,boolean throwExeption){
        Object obj =null;
        try {
            obj=readObject(path, name);
        }catch (Exception e){
            if(throwExeption){
                throw e;
            }
        }
        return obj;
    }

    //如果文件不存在，返回null
    public static Object readObject(String path,String name){
        FileInputStream fin = null;
        ObjectInputStream in = null;
        Object result = null;
        File file = null;
        try{
            file = new File(path+File.separator+name);
            if(file==null||!file.exists()||file.getTotalSpace()==0){
                return null;
            }
            fin = new FileInputStream(path+File.separator+name);
            in = new ObjectInputStream(fin);
            result= in.readObject();
            in.close();
            fin.close();
        }catch (Exception e){
            throw new RuntimeException(Constant.Error.SYSTEM_EXCEPTION);
        }
        return result;
    }

}

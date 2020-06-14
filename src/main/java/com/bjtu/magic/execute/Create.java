package com.bjtu.magic.execute;

import com.alibaba.fastjson.JSON;
import com.bjtu.magic.common.Constant;
import com.bjtu.magic.entity.FrmEntity;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.bjtu.magic.common.Constant.Common.TABLE_FRM_SUFFIC;
import static com.bjtu.magic.config.ApplicationConfig.getCurrentDatabase;
import static com.bjtu.magic.config.ApplicationConfig.getDataPath;
import static com.bjtu.magic.manage.FileManage.createDir;
import static com.bjtu.magic.manage.FileManage.writeObject;

/**
 * @Auther: wjx
 * @Date: 2020/5/4 21:47
 * @Description:
 */
public class Create extends SqlHandler{

    @Override
    public String execute() {
        String flag = check(sql.toLowerCase().replace("create", "").trim());
        //创建数据库
        if("0".equals(flag)){
            createDir(getDataPath(),getDBName("create"));
        }else{
            createTable(sql);
        }
        System.out.println(sql+" successfully.");
        return null;
    }
    //创建表
    private void createTable(String sql) {
        String tableName = getName(sql.substring(0, sql.indexOf("(")), "create", "table");
        //获取表结构数据,每个属性对应一个实体
        FrmEntity[] list=getFieldEntity(sql);
        System.out.println("entity is : "+ JSON.toJSONString(list));
        //保存表结构
        writeObject(getDataPath()+File.separator+getCurrentDatabase(),tableName+TABLE_FRM_SUFFIC,list);
    }

    private FrmEntity[] getFieldEntity(String sql) {

        String[] fields = sql.substring(sql.indexOf("(")+1, sql.lastIndexOf(")")).split(",");
        FrmEntity entity = null;
        //主键列,可以为空
        String keyName=null;
        if(fields[fields.length-1].toLowerCase().contains("primary key")){
            keyName = fields[fields.length-1].
                    substring(fields[fields.length-1].indexOf("(") + 1,
                                fields[fields.length-1].lastIndexOf(")"));
        }
        FrmEntity[] frmList = new FrmEntity[keyName==null?fields.length:fields.length-1];
        //解析每一行，即每一个属性
        for (int i = 0; i < fields.length; i++) {
            //主键列
            if(fields[i].toLowerCase().contains("primary")
                    &&fields[i].toLowerCase().contains("key")){
                continue;
            }
            entity = new FrmEntity();
            entity.setOrder(i);
            //解析每一行
            String[] values = fields[i].trim().split(" ");
            for (int j = 0; j < values.length; j++) {
                if(values[0].equalsIgnoreCase("PRIMARY")
                        &&values[1].equalsIgnoreCase("key")){
                    continue;
                }
                if(j==0){
                    if(values[j].equalsIgnoreCase(keyName)){
                        entity.setKey(true);
                        entity.setNull(false);
                    }
                    entity.setName(values[j]);
                }else if(j==1){
                    String type = values[j].substring(0, values[j].indexOf("("));
                    String length = values[j].substring(values[j].indexOf("(")+1, values[j].lastIndexOf(")"));
                    entity.setType(type);
                    entity.setLength(length);
                }else if(j==2){
                    if(values[j].equalsIgnoreCase("not")&&values[j+1].equalsIgnoreCase("null")){
                        entity.setNull(false);
                    }
                }
            }
            frmList[i]=entity;
        }
        return frmList;
    }

}

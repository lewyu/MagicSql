package com.bjtu.magic.execute;

import com.bjtu.magic.common.Constant;
import com.bjtu.magic.entity.BTree;
import com.bjtu.magic.entity.FrmEntity;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import static com.bjtu.magic.config.ApplicationConfig.getCurrentDatabase;
import static com.bjtu.magic.config.ApplicationConfig.getDataPath;
import static com.bjtu.magic.execute.Check.isDataBaseUsed;
import static com.bjtu.magic.manage.FileManage.readObject;
import static com.bjtu.magic.manage.FileManage.writeObject;

/**
 * @Auther: wjx
 * @Date: 2020/6/4 21:48
 * @Description:
 */
public class Update extends SqlHandler{

    @Override
    public String execute() {
        //判断是否选定数据库
        isDataBaseUsed();
        String sqlLow = sql.toLowerCase();
        //表名
        String tableName = sqlLow.substring(sqlLow.indexOf("update") + 6, sqlLow.indexOf("set")).trim();
        //更新的条件属性
        String idName=sqlLow.substring(sqlLow.indexOf("where") + 5, sqlLow.lastIndexOf("=")).trim();
        //更新条件属性的值
        String idValue=sqlLow.substring(sqlLow.lastIndexOf("=")+1).replace("'","").trim();
        //获取表结构
        FrmEntity[] frmEntities = frmMap.get(tableName);
        int keyType = 1;
        for(FrmEntity entity:frmEntities){
            String type = entity.getType().trim().toLowerCase();
            if(entity.isKey()) {
                if(!idName.toLowerCase().equals(entity.getName())){
                    throw new RuntimeException(Constant.Error.SQL_NO_THIS_CONDITION);
                }
                if (type.equals("int") || type.equalsIgnoreCase("integer")) {
                    keyType = 1;
                    break;
                } else if (type.equals("long")) {
                    keyType = 0;
                    break;
                }
            }
        }
        String[] resp =null;
        //查询数据
        BTree tree=(BTree) readObject(getDataPath()+ File.separator+getCurrentDatabase(),tableName+ Constant.Common.TABLE_INDEX_SUFFIC,false);
        if(idValue!=null){
            if(keyType==1){
                resp=(String[])tree.find(Integer.valueOf(idValue));
            }else if(keyType==0){
                resp=(String[])tree.find(Long.valueOf(idValue));
            }
        }
        System.out.println("resp :"+ Arrays.toString(resp));
        //待更新的列
        String[] split = sqlLow.substring(sqlLow.indexOf("set") + 3, sqlLow.indexOf("where") - 1).trim().split(",");
        for(String s : split) {
            for(FrmEntity frm : frmEntities){
                //更新该列
                if(frm.getName().equalsIgnoreCase(s.substring(0, s.indexOf("=")).trim())){
                    resp[frm.getOrder()]=s.substring(s.indexOf("=") + 1).trim().replace("'", "");
                }
            }
        }
        if(idValue!=null){
            if(keyType==1){
                tree.update(split,Integer.valueOf(idValue));
            }else if(keyType==0){
                tree.update(split,Long.valueOf(idValue));
            }
        }
        writeObject(getDataPath()+File.separator+getCurrentDatabase(),tableName+Constant.Common.TABLE_INDEX_SUFFIC,tree);
        return "sql successfully.";
    }
}

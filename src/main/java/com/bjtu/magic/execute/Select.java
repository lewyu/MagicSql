package com.bjtu.magic.execute;

import com.bjtu.magic.common.Constant;
import com.bjtu.magic.entity.BTree;
import com.bjtu.magic.entity.FrmEntity;

import java.io.File;
import java.util.Arrays;

import static com.bjtu.magic.config.ApplicationConfig.getCurrentDatabase;
import static com.bjtu.magic.config.ApplicationConfig.getDataPath;
import static com.bjtu.magic.execute.Check.isDataBaseUsed;
import static com.bjtu.magic.manage.FileManage.readObject;

/**
 * @Auther: wjx
 * @Date: 2020/6/4 21:48
 * @Description:
 */
public class Select extends SqlHandler {

    @Override
    public String execute() {
        //判断是否选定数据库
        isDataBaseUsed();
        //获取tableName
        String trim = sql.toLowerCase().substring(sql.toLowerCase().indexOf("from")+4);
        String tableName = trim.substring(0, trim.indexOf("where")).trim();
        //属性
        String property = trim.substring(trim.indexOf("where") + 5, trim.lastIndexOf("=")).trim();
        //属性值
        Integer key = Integer.valueOf(trim.substring(trim.indexOf("=")+1,trim.length()-1).replace("'","").trim());
        //获取表结构
        FrmEntity[] frmEntities = frmMap.get(tableName);
        int keyType = 1;
        for(FrmEntity entity:frmEntities){
            String type = entity.getType().trim().toLowerCase();
            if(entity.isKey()) {
                if(!property.toLowerCase().equals(entity.getName())){
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
        if(key!=null){
            if(keyType==1){
                resp=(String[])tree.find(key);
            }else if(keyType==0){
                resp=(String[])tree.find(Long.valueOf(key));
            }
        }
        if(resp!=null&&resp.length>0) {
            return Arrays.toString(resp);
        }else{
            return null;
        }
    }
}

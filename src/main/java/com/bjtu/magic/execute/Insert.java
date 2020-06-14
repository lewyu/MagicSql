package com.bjtu.magic.execute;

import com.bjtu.magic.common.Constant;
import com.bjtu.magic.entity.BTree;
import com.bjtu.magic.entity.FrmEntity;

import java.io.File;
import java.util.List;

import static com.bjtu.magic.common.Constant.Error.*;
import static com.bjtu.magic.config.ApplicationConfig.getCurrentDatabase;
import static com.bjtu.magic.config.ApplicationConfig.getDataPath;
import static com.bjtu.magic.execute.Check.isDataBaseUsed;
import static com.bjtu.magic.manage.FileManage.readObject;
import static com.bjtu.magic.manage.FileManage.writeObject;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * @Auther: wjx
 * @Date: 2020/6/4 21:47
 * @Description:
 */
public class Insert extends SqlHandler {

    @Override
    public String execute() {
        //判断是否选定数据库
        isDataBaseUsed();
        //获取表名
        String trim = sql.toLowerCase().replace("insert", "").replace("into", "").trim();
        String tableName = trim.substring(0, trim.toLowerCase().lastIndexOf("values")-1);
        //是否存在对应的表结构文件
        if(!frmMap.containsKey(tableName)){
            throw new RuntimeException(SQL_NO_THIS_TABLE);
        }
        //获取需要插入的所有记录
        String[] allRecord = sql.substring(sql.indexOf("(")+1, sql.lastIndexOf(")")).replace("'","").split("\\)\\s*,\\s*\\(");
        //插入所有的记录
        insertRecode(allRecord,tableName);
        return "insert into "+ tableName+" successfully.";
    }

    private void insertRecode(String[] allRecord,String tableName) {
        //加载索引文件，是否存在索引
        BTree tree=getBTree(tableName);
        //获取表的结构
        FrmEntity[] frmEntities = frmMap.get(tableName);
        for (int i = 0; i < allRecord.length; i++) {
            String record = allRecord[i].replace("'","");
            //所有属性列的值
            String[] allTypeValue = record.trim().substring(0, record.trim().length() - 1).split(",");
            //插入的属性的格式必须和表结构一一对应
            if(frmEntities.length!=allTypeValue.length){
                throw new RuntimeException(SQL_COMMON_ERROR);
            }
            //主键值
            Integer keyValue=null;
            //主键类型数据字典，0-Long，1-Integer
            int keyType=0;
            for (int j = 0; j < allTypeValue.length; j++) {
                if(allTypeValue[j].trim().length()>Integer.valueOf(frmEntities[j].getLength())){
                    throw new RuntimeException(SQL_FIELD_TOO_LONG);
                }
                if(frmEntities[j].isKey()){
                    keyValue=Integer.valueOf(allTypeValue[j]);
                    String type = frmEntities[j].getType().trim().toLowerCase();
                    if(type.equals("int")||type.equalsIgnoreCase("integer")){
                        keyType=1;
                    }else if(type.equals("long")){
                        keyType=0;
                    }
                }
            }
            //插入B+树,默认关键字是Integer类型
            if(keyValue!=null) {
                if(keyType==1){
                    tree.insert(allTypeValue, keyValue);
                }else if(keyType==0){
                    Long value = Long.valueOf(keyValue);
                    tree.insert(allTypeValue, value);
                }
            }
        }
        //持久化
        writeObject(getDataPath()+File.separator+getCurrentDatabase(),tableName+Constant.Common.TABLE_INDEX_SUFFIC,tree);
    }
    //读取B+索引树
    private BTree getBTree(String tableName) {
        BTree tree = null;
        Object object= readObject(getDataPath()+ File.separator+getCurrentDatabase(),tableName+ Constant.Common.TABLE_INDEX_SUFFIC,false);
        if(object==null){
            tree=new BTree(40);
        }else{
            tree=(BTree)object;
        }
        return tree;
    }
}

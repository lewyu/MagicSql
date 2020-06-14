package com.bjtu.magic.execute;

import com.bjtu.magic.common.Constant;
import com.bjtu.magic.entity.BTree;
import com.bjtu.magic.entity.FrmEntity;

import java.io.File;

import static com.bjtu.magic.config.ApplicationConfig.getCurrentDatabase;
import static com.bjtu.magic.config.ApplicationConfig.getDataPath;
import static com.bjtu.magic.execute.Check.isDataBaseUsed;
import static com.bjtu.magic.manage.FileManage.readObject;
import static com.bjtu.magic.manage.FileManage.writeObject;

/**
 * @Auther: wjx
 * @Date: 2020/6/4 21:47
 * @Description:
 */
public class Delete extends SqlHandler {

    @Override
    public String execute() {
        //判断是否选定数据库
        isDataBaseUsed();
        //获取tableName
        String trim = sql.toLowerCase().replace("delete", "").replace("from", "").trim();
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
        //获取索引树并删除
        BTree tree=(BTree) readObject(getDataPath()+ File.separator+getCurrentDatabase(),tableName+ Constant.Common.TABLE_INDEX_SUFFIC,false);
        if(key!=null){
            if(keyType==1){
                tree.delete(key);
            }else if(keyType==0){
                tree.delete(Long.valueOf(key));
            }
        }
        writeObject(getDataPath()+File.separator+getCurrentDatabase(),tableName+Constant.Common.TABLE_INDEX_SUFFIC,tree);
        return "sql successfully.";
    }
}

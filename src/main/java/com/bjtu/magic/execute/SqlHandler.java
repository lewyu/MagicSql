package com.bjtu.magic.execute;

import com.bjtu.magic.common.Constant;
import com.bjtu.magic.entity.BTree;
import com.bjtu.magic.entity.FrmEntity;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.bjtu.magic.common.Constant.Error.SQL_COMMON_ERROR;
import static com.bjtu.magic.common.Constant.Error.SQL_NO_THIS_FUNCTION;
import static com.bjtu.magic.config.ApplicationConfig.getCurrentDatabase;
import static com.bjtu.magic.config.ApplicationConfig.getDataPath;
import static com.bjtu.magic.execute.Check.isLogin;
import static com.bjtu.magic.manage.FileManage.readObject;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * @Auther: wjx
 * @Date: 2020/5/4 21:19
 * @Description:
 */
public class SqlHandler {
    //所有的handler
    private static final Map<String,SqlHandler> handlerMap = new HashMap<>();
    //当前数据库的所有的表结构
    protected static final Map<String, FrmEntity[]> frmMap = new HashMap<>();
    protected String sql;
    static {
        handlerMap.put("create",new Create());
        handlerMap.put("delete",new Delete());
        handlerMap.put("drop",new Drop());
        handlerMap.put("insert",new Insert());
        handlerMap.put("select",new Select());
        handlerMap.put("show",new Show());
        handlerMap.put("update",new Update());
        handlerMap.put("use",new Use());
    }
    //加载当前库的所有的表机构
    protected void loadAllFrm(File dbDir) {
        File[] files = dbDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.toLowerCase().trim().endsWith(Constant.Common.TABLE_FRM_SUFFIC);
            }
        });
        String tableName =null;
        FrmEntity[] frmList = null;
        for(File file : files){
            tableName=file.getName().substring(0,file.getName().lastIndexOf("."));
            frmList = (FrmEntity[])readObject(dbDir.getPath(),file.getName());
            frmMap.put(tableName,frmList);
        }
    }
    //0-数据库，1-表
    protected String check(String sql) {
        if(sql.startsWith("table")&&isBlank(getCurrentDatabase())){
            throw new RuntimeException(Constant.Error.SQL_NO_DB_SELECTED);
        }
        if(!sql.startsWith("table")&&!sql.startsWith("database")){
            throw new RuntimeException(SQL_COMMON_ERROR);
        }
        return sql.startsWith("database")?"0":"1";
    }
    protected String getDBName(String operate){
        return  getName(sql,operate,"database");
    }
    protected String getTableName(String operate){
        return  getName(sql,operate,"table");
    }
    public String getName(String sql,String operate,String tn){
        return sql.toLowerCase().replace(operate, "").replace(tn, "").trim();
    }


    //分析后没有对应的handler，直接执行父类的方法-异常
    public String execute(){
        throw new RuntimeException(SQL_NO_THIS_FUNCTION);
    }

    //封装结果
    public String executeSql(){
        StringBuilder result = new StringBuilder();
        try {
            isLogin();
            //去掉sql后面的分号
            this.sql=sql.replace(";","").trim();
            //执行结果
            String resp = this.execute();
            if(isNotBlank(resp)){
                result.append(resp);
            }
            //result.append("sql执行成功");
        }catch (Exception e){
            //封装异常信息
            result.append(e.getMessage());
        }
        return result.toString();
    }

    //分析输入的sql语句，返回响应的handler
    public static SqlHandler analyseInput(String input) {
        SqlHandler handler = null;
        String sql = input.toLowerCase().trim();
        //分析语句
        for(String operate:handlerMap.keySet()){
            if(sql.startsWith(operate)){
                handler=handlerMap.get(operate);
                break;
            }
        }
        //默认父类
        if(handler==null){
            handler=new SqlHandler();
        }
        handler.sql=input;
        return handler;

    }
}

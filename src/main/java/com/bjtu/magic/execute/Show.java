package com.bjtu.magic.execute;

import com.bjtu.magic.common.Constant;

import java.io.File;
import java.io.FilenameFilter;

import static com.bjtu.magic.config.ApplicationConfig.*;
import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * @Auther: wjx
 * @Date: 2020/6/4 21:24
 * @Description:
 */
public class Show extends SqlHandler{

    @Override
    public String execute() {
        StringBuffer result = new StringBuffer();
        //0-数据库，1-表
        String flag = check(sql.toLowerCase().replace("show","").replace(";","").trim());
        if("0".equals(flag)){
            showDatabases(getDataPath());
        }else{
            showTables(getDataPath());
        }
        return null;
    }

    private void showDatabases(String dataPath) {
        setCurrentDatabase(null);
        File file = new File(dataPath);
        if(!file.exists()||!file.isDirectory()){
            file.mkdir();
        }else{
            File[] dbs = file.listFiles();
            for(File db : dbs){
                System.out.println(db.getName());
            }
        }
    }

    private void showTables(String dataPath) {
        if(isBlank(getCurrentDatabase())){
            throw new RuntimeException(Constant.Error.SQL_NO_DB_SELECTED);
        }
        File file = new File(dataPath+File.separatorChar+getCurrentDatabase());
        File[] tables = file.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(Constant.Common.TABLE_FILE_SUFFIC);
            }
        });
        for(File table : tables){
            String name = table.getName();
            System.out.println(name.substring(0,name.length()-5));
        }
    }
}

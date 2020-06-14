package com.bjtu.magic.execute;

import com.bjtu.magic.common.Constant;
import com.bjtu.magic.entity.FrmEntity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.ObjectInputStream;
import java.util.List;

import static com.bjtu.magic.common.Constant.Error.SQL_NO_THIS_TABLE;
import static com.bjtu.magic.config.ApplicationConfig.*;
import static com.bjtu.magic.execute.Check.isLogin;
import static com.bjtu.magic.manage.FileManage.readObject;
import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * @Auther: wjx
 * @Date: 2020/6/4 21:47
 * @Description:
 */
public class Use extends SqlHandler {

    @Override
    public String execute() {

        String dbName = sql.toLowerCase().replace("use", "").replace(";","").trim();
        File file = new File(getDataPath()+File.separator+dbName);
        if(file==null||isBlank(file.getName())||!file.exists()){
            throw new RuntimeException(SQL_NO_THIS_TABLE);
        }
        setCurrentDatabase(dbName);
        //加载当前数据所有的表结构
        loadAllFrm(file);
        return null;
    }
}

package com.bjtu.magic.execute;

import com.bjtu.magic.common.Constant;
import com.sun.xml.internal.bind.v2.runtime.reflect.opt.Const;

import java.io.File;

import static com.bjtu.magic.common.Constant.Common.TABLE_FRM_SUFFIC;
import static com.bjtu.magic.common.Constant.Error.SQL_COMMON_ERROR;
import static com.bjtu.magic.config.ApplicationConfig.getCurrentDatabase;
import static com.bjtu.magic.config.ApplicationConfig.getDataPath;
import static com.bjtu.magic.manage.FileManage.dropFile;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * @Auther: wjx
 * @Date: 2020/6/4 21:48
 * @Description:
 */
public class Drop extends SqlHandler {

    @Override
    public String execute() {
        String flag = check(sql.toLowerCase().replace("drop", "").replace(";","").trim());
        String name ="0".equals(flag)?
                sql.toLowerCase().replace("drop", "").replace("database", "").trim()
                :sql.toLowerCase().replace("drop", "").replace("table", "").trim()+ TABLE_FRM_SUFFIC;
        String path="0".equals(flag)?getDataPath():getDataPath()+File.separator+getCurrentDatabase();
        //删除
        dropFile(path,name);
        System.out.println(sql+" successfully.");
        return null;
    }

}

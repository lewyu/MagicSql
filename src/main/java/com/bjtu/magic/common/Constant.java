package com.bjtu.magic.common;

/**
 * @Auther: wjx
 * @Date: 2020/4/19 09:31
 * @Description:
 */
//常量
public interface Constant {

    //公共常量
    interface Common{
        String PREFIX = "MagicSql>>";
        String TABLE_FILE_SUFFIC=".data";
        String TABLE_FRM_SUFFIC=".frm";
        String TABLE_INDEX_SUFFIC=".index";
    }
    //配置文件中的key值
    interface ConfigKey{
        String CONFIG_DATA_PATH="data.path";
        String CONFIG_USER_PATH="login.user.file";
    }

    //缓存key
    interface CacheKey{
        String CACHE_KEY_DATAPATH ="dataPath";
    }

    //错误定义
    interface Error{
        String SYSTEM_EXCEPTION="系统异常";

        String SQL_COMMON_ERROR = "sql输入有误！！！";
        String SQL_NOT_LOGIN="未登录，禁止操作！！！";
        String SQL_USER_PASSWD="用户名或密码输入错误";
        String SQL_DB_ALREADY_EXIST = "数据库已存在";
        String SQL_NO_DB_SELECTED="no db was selected";
        String SQL_BAD_INDEX_TYPE = "bad index type";
        String SQL_NO_THIS_FUNCTION="no this function";
        String SQL_CHECK_CONFIG="请检查配置文件";
        String SQL_NO_THIS_TABLE="表不存在";
        String SQL_FIELD_TOO_LONG="属性值超长了！";
        String SQL_NO_THIS_CONDITION="暂不支持的条件属性!";

    }

}

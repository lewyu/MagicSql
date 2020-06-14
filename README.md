# 一、	概述
  本次实践项目，我们用java语言开发实现了一个简单的数据库管理系统，其中包括的功能模块有：数据库的管理、表的管理、配置管理、索引的管理、文件管理、异常管理等功能模块。由于java是基于jvm的一种开发语言，很多底层的操作对java开发人员都是透明的，所以用java实现c 语言的各种对底层的操作、系统调用等都是比较困难的。
  
首先说说索引，我们选择最经典也是最常用的B+树作为索引结构。其实最开始有考虑多种索引结构实现索引功能，包括哈希索引、有序数组索引等，但是查完之后对比发现，哈希索引和有序数组索引有更强的针对性，并不具备普适性，而B+树则能够普适性，这也是有余B+树本身的特性所决定的。
	
SqlHandler这个类是系统的最重要的类，不实现具体的逻辑，只负责任务调度，相当于系统的任务调度中心，这一思想是借鉴了经典的springMVC框架的思想，Sqlhandler的功能类似于Controller的作用。这个类作为用户层和功能实现搭起了一个桥梁，这样做的好处是相互之前隐藏细节，而且sqlhandler还能够对用户输入的sql语句做处理封装等，然后交给具体类去实现，比如说我们这里用户输入的sql语句还有面会有分号，由于分号只是在sql输入的时候做结标识的，并没有实际的业务逻辑，所以sqlhandler会去掉这个分号，所有的具体的实现类拿到的sql语句都没有分号。
     异常处理，由于Sqlhandler是任务调用中心， 当然也可以实现对异常的统一处理。首先后面的具体的实现类的异常都全部抛出，sqlhandler会捕获所有异常，然后统一封装返回给用户。
	由于java语言的限制和个人能力的原因，很多想法未能实现，该系统有借鉴经典的思想，但是很多细节方面还需要优化。
# 二、	总体结构说明
## 1.	环境信息
该系统基于jdk1.8版本开发，使用了流行的maven作为统一的依赖管理。
## 2.	登录相关
为了把时间花在重要的功能实现上，这里只实现了简单的用户登录。我们先把用户的用户名和密码固定，然后反序列化保存在硬盘上，每次启动数据库的时候需要加载这些文件并且解析出用户的信息，启动后获取用户输入的用户名和密码对校验。

## 3.	配置说明
我们在config目录下有一个配置文件application.properties,作为系统的配置文件，其中有2个配置是必须要有的，一个是数据库的数据目录，另一个是反序列化后的用户信息保存的位置。设置这个配置文件的目的有两个，一是便于配置的统一管理，二是作为预留功能，后期有其他需要动态修改的配置可以放到配置文件里面实现，比如我们的数据目录等。
## 4.	处理步骤
 首先用户需要输入密码登录，用户输入sql语句，Sqlhandler会解析语句，并且根据具体的sql和具体的执行sql的类做关联，由不同的handler实现不同的功能，具体的handler处理完成后将结果返回给sqlhandler，由其统一返回。如果有异常抛出，也由Sqlhandler做统一的异常封装，返回给用户。
# 三、	设计与实现
## 1.	登录实现
用户管理也是数据库系统比较重要的一个模块，这个也可以抽离出来一个用户管理模块，但是该系统并没有在这方面花费很多精力，只是做了一个简单的登录，下面的代码是固定了三个用户序列化并且持久化了。loadLoginUser()实现了启动的时候会把配置文件中配置的用户信息的数据读取并解析出来，loginUsers是用来保存解析后的用户信息
private static void loginWrite() 
//加载用户数据
loadLoginUser();

//登录用户配置
private static final List<User> loginUsers=new ArrayList();
## 2.加载配置
在magicMain()方法中首先会调用ApplicationConfig类的方法startApp();方法，用来加载配置文件，最重要的是加载config目录下的application.properties配置文件即loadConfig()的实现，其实是加载用户信息即loadLoginUser();实现的功能。由于这些配置在服务使用过程中被修改，所以会把加载的配置数据放到缓存中，程序运行中可以使用，但是不可以修改。
//加载配置
            loadConfig();
          //加载用户数据
            loadLoginUser();
## 3.	核心处理类SqlHandler
作为系统的统一调度中心，也是系统的核心。

a.protected String sql; 
定义的一个protect类型的变量，即子类有改sql变量的操作权限，但是后面的实现类也咩有改变sql的值，使用过程中会生成临时变量。Sql是读取用户输入的sql语句后去掉后面的分号，没有做过多的解析，也不易做过多的解析，具体的由后面的类去实现

b. private static final Map<String,SqlHandler> handlerMap = new HashMap<>();
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
系统启动的时候先加载静态代码块，会先解析sql语句，然后找到对应的实现类，public static SqlHandler analyseInput(String input)，该方法解析用于输入的sql，然后返回具体handler处理具体的逻辑。如果解析后没有找到具体的handler，会返回SqlHandler，但是sqlhandler的执行方法是直接抛出异常，改sql语句的功能无法支持。即下面的方法。
//分析后没有对应的handler，直接执行父类的方法-异常
    public String execute(){
        throw new RuntimeException(SQL_NO_THIS_FUNCTION);
    }
    
c. protected static final Map<String, FrmEntity[]> frmMap = new HashMap<>();
 改变量是加载当前数据库的所有的表结构，作为一个protected修饰的变量，也是系统具体的handler可以使用，可以临时保存当前数据库的表结构，由于表结构变动较小，为了避免重复从硬盘上读取数据，所以加载到内存中，随取随用。protected void loadAllFrm(File dbDir)实现了加载当前数据的所有表结构的功能。
 
d. protected String getDBName(String operate)
protected String getTableName(String operate)
protected String check(String sql)
 由于开发过程中发现，频繁使用了这个3个功能，即从sql中提取数据库name和表name，第三个是检查对表还是对数据库的操作，故将这3个方法提取出来作为公用方法。避免冗杂的代码。
 
e.核心逻辑调度代码
while(true){
            //获取输入
            input = getInput(in);
            //分析语句
            hander=analyseInput(input);
            //执行语句
            result=hander.executeSql();
            //打印执行结果
            if(isNotBlank(result)){
                System.out.println(result);
            }
        }
## 4.	数据库和表的管理
a.	create类 public class Create extends SqlHandler 继承sqlhandler，也就有了上面提高的sqlhandler定义的一些protected修饰的属性和方法。
createDir(getDataPath(),getDBName("create")); 实现了创建数据库，即创建目录，目录名和数据库name一致。当数据库已经存在的时候抛出数据库已存在的异常。

private void createTable(String sql) 创建表的实现，首先获取表名，
FrmEntity[] list=getFieldEntity(sql); 解析输入的sql语句，从建表语句中提取对应的属性，每一个属性对应个FrmEntity实体类
public static void writeObject(String path,String name,Object value) 将解析后的FrmEntity属性数组序列化后保存在上的当前数据库下面，以表名命名以.frm为后缀存储。

b.	drop类 public class Drop extends SqlHandler
dropFile(path,name); 删除方法，会根据sql提取path和name，一个方法，只不过在解析sql的时候会根据drop表和drop数据库的解析出不同的path和name，删除数据库的时候调用该方法会删除文件夹下的所有文件。

c.	use类 public class Use extends SqlHandler
解析sql获取dbname，判断dbname是否存在，
setCurrentDatabase(dbName); 设置当前use的数据库

d.	show类public class Show extends SqlHandler
第一步解析是show表还是show数据库
private void showDatabases(String dataPath) show数据库，读取数据目录下的所有的文件夹，并展示出来
private void showTables(String dataPath) show表，读取当前数据库目录下的所有的.frm结尾的文件，public boolean accept(File dir, String name) 作为FilenameFilter的实现方法筛选文件。去掉后缀打印出来。
## 5.	对表的增删改查
  isDataBaseUsed();对表的操作是需要选中数据库的，首先会判断是否已经选中了操作的数据库，有才可以进行下一步操作。否则无法继续。
  
a.	insert 类public class Insert extends SqlHandler
获取tablename
if(!frmMap.containsKey(tableName)) 判断是否有对应的表结构文件，有则读取，无说明不存在该表
解析sql，从sql中提取所有的记录
BTree tree=getBTree(tableName);  加载索引文件，没有则新建索引树，索引树存储在当前数据库目录下的以.index为后缀的表名命名的数据文件中
for (int i = 0; i < allRecord.length; i++)遍历从sql中提取出来的数据，拿表结构文件比较，符合条件则插入，否则异常。支持一条sql插入多条数据，如果异常则全部插入失败。
writeObject(getDataPath()+File.separator+getCurrentDatabase(),tableName+Constant.Common.TABLE_INDEX_SUFFIC,tree); 更新索引树

b.	update类 public class Update extends SqlHandler
isDataBaseUsed();  判断是否选定数据库
从sql中提取表名，条件属性&属性值，要跟新的属性和属性值的数组
FrmEntity[] frmEntities = frmMap.get(tableName); 获取表结构，并校验sql语句是否符合表机构
BTree tree=getBTree(tableName);  加载索引文件，并获取到要更新的记录
修改BTree树，然后反序列到磁盘。

c.	delete类 public class Delete extends SqlHandler
 isDataBaseUsed();  判断是否选定数据库
从sql中提取表名，条件属性&属性值
FrmEntity[] frmEntities = frmMap.get(tableName); 获取表结构，并校验sql语句是否符合表机构
BTree tree=getBTree(tableName);  加载索引文件
修改BTree树，删除对应的列，然后反序列化到磁盘。

d.	select类 public class select extends SqlHandler
 isDataBaseUsed();  判断是否选定数据库
从sql中提取表名，条件属性&属性值
FrmEntity[] frmEntities = frmMap.get(tableName); 获取表结构，并校验sql语句是否符合表机构
BTree tree=getBTree(tableName);  加载索引文件，然后从索引树中读取满足条件的数据
  打印结果
## 6.	文件操作
public class FileManage 该类用于管理文件，
public static void dropFile(String path,String fileName) 删除文件或者文件夹，删除文件夹采用递归的方式，先删除文件夹下面的文件，然后删除改文件夹。

File[] files = file.listFiles();
            for(File f : files){
                dropFile(path+File.separator+file.getName(),f.getName());
            }
public static void createDir(String path,String dirName) 创建目录，如果目录已存在则创建失败。public static void writeObject(String path,String name,Object value) 将反序列化的数据保存在磁盘上，如果文件已存在，则替换原来的文件

public static Object readObject(String path,String name,boolean throwExeption) 从磁盘上读取反序列化的数据，throwExeption为false表示读取失败返回nulll，否则抛出异常。
## 7.	常量说明
Constant常量类中把常量分类定义
interface Common 是公共的常量定义
interface ConfigKey //配置文件中的key值常量
interface CacheKey 缓存key常量
interface Error{}这个下面定义是sql的异常定义

## 8.	缓存设计
ManageCache public class ManageCache 用于保存常用的且不会被修改或者不会经常修改的数据，比如说启动的时候加载的配置等。
private static Map dbCache = new HashMap<String,Object>();  存储缓存数据
private static String dataPath; 数据文件位置
public static Object getCache(String key)  根据key获取缓存中的value
public static void putCache(String key,Object value) 将数据放入缓存中
## 9.	索引树的实现
public class BTree <T, K extends Comparable<K>> implements Serializable ，其中T表示数据类型可以变化，K类型必须是Comparable的子类，目的是关键字在比较的时候用compareTo方法比较大小。

a.	内部类

abstract class Node<T, K extends Comparable<K>> implements Serializable 抽象的节点类，叶子节点和非叶子节点继承改抽象类并需要实现抽象类中的增删改查的方法
抽象类的属性：
//父节点
        protected Node<T, K> parent;
        //子节点
        protected Node<T, K>[] childs;
        //子节点数量
        protected Integer amount;
        //关键字数组
        protected Object keys[];
  抽象方法，
abstract T find(K key);
        abstract Node insert(T value, K key);
        abstract LeafNode getNewLeft();
        abstract int delete(K key);
        abstract void update(T value,K key);
class KeyNode<T, K extends Comparable<K>> extends Node<T,K> implements Serializable 非叶子节点，只保存关键字，不保存数据，继承抽象类Node并实现其中的增删改查的抽象方法
class LeafNode<T, K extends Comparable<K>> extends Node<T,K> implements Serializable 叶子节点，既保存关键字，也保存数据，继承抽象类Node并实现其中的增删改查的抽象方法

b.	B+树的属性
//B+树的阶
    private Integer degree;
    //最大节点数
    private Integer maxAmount;
    //根节点
    private Node<T, K> root;
    //最右叶子节点
    private LeafNode<T, K> left;
# 四、	附录
User.data 反序列化后保存的用户信息文件，需要放置在config/application.properties配置的对应的目录下
MagicSql.zip 为项目源码，需要使用maven工具和jdk1.8的环境运行

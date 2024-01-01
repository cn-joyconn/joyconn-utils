package cn.joyconn.utils.dataSource;

import com.alibaba.druid.pool.DruidDataSource;

import java.util.Map;

public class DataSourceContextHolder {
    private static final ThreadLocal<String> contextHolder = new ThreadLocal<String>();

    /**
     *
     * @param dataSourceKeyValue
     */
    public static synchronized void setDBType(DataSourceKeyValue dataSourceKeyValue){
        if(dataSourceKeyValue!=null){

            Map<Object,Object> dataSourceMap = DynamicDataSource.getInstance().getDataSourceMap();
            if(!dataSourceMap.containsKey(dataSourceKeyValue.getKey())){
                DruidDataSource dataSource = initDataSource(dataSourceKeyValue.getValue());
                dataSourceMap.put(dataSourceKeyValue.getKey(),dataSource);
                DynamicDataSource.getInstance().setTargetDataSources(dataSourceMap);
            }
            contextHolder.set(dataSourceKeyValue.getKey());
        }else{
            contextHolder.set("defaultDataSource");
        }
    }
    public static String getDBType(){
        return contextHolder.get();
    }

    public static void clearDBType(){
        contextHolder.remove();
    }


    private static DruidDataSource initDataSource(String source) {

        SourceData sourceData = parse(source);

        DruidDataSource dataSource = new DruidDataSource();

        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl(getDBUrl(sourceData));
        dataSource.setUsername(sourceData.getUser());
        dataSource.setPassword(sourceData.getPsw());
        dataSource.setInitialSize(1);
        dataSource.setMaxActive(10);

        return dataSource;
    }
    public static String getDBUrl(SourceData sourceData){
        String params="useUnicode=true&characterEncoding=UTF-8&useSSL=false&serverTimezone=GMT%2B8"; 
        String url = String.format("jdbc:mysql://%s:%d/%s?%s", 
            sourceData.getServer(),sourceData.getPort(),sourceData.getDatabase(),params);
        return url;
    }
    public static SourceData parse(String source) {

        if (source == null || source.equals("")) {
            return null;
        }

        SourceData sourceData = new SourceData();
        int index = 0;
        String[] arr = source.split(";");
        for (String str : arr) {
            String[] tempA = str.split("=");
            if (tempA.length == 2) {
                if (tempA[0].toLowerCase().contains("server")) {
                    index++;
                    sourceData.setServer(tempA[1]);
                } else if (tempA[0].toLowerCase().contains("port")) {
                    index++;
                    sourceData.setPort(Integer.parseInt(tempA[1]));
                } else if (tempA[0].toLowerCase().contains("uid")) {
                    index++;
                    sourceData.setUser(tempA[1]);
                } else if (tempA[0].toLowerCase().contains("pwd")) {
                    index++;
                    sourceData.setPsw(tempA[1]);
                } else if (tempA[0].toLowerCase().contains("database")) {
                    index++;
                    sourceData.setDatabase(tempA[1]);
                }
            }
        }

        if (index == 5) {
            return sourceData;
        }

        return null;
    }

    public static class SourceData {

        private String server = "";

        private int port = 0;

        private String user = "";

        private String psw = "";

        private String database = "";

        private String company;
        private String project;
        private String room;

        public boolean equals(Object otherObject) {

            if (this == otherObject) return true;   //检测是否是同一个对象

            if (null == otherObject) return false;  //检测对象是否null

            //检查this与otherObject是否属于同一个类
            //情况1：假如每个类都有自己的语义时
            if (this.getClass() != otherObject.getClass()) return false;

            //情况2：如果所有的子类均使用统一的语义(那么此时的equals方法应该置为final)，就使用instanceof检测
            // if( !(this instanceof Person) ) return false;

            //此时，将otherObject转化为相应的类型
            SourceData person = (SourceData) otherObject;
            //然后对所需要比较的域进行比较，使用==比较基本类型，使用equals比较对象域
            return this.server.equals(person.server) && this.port == person.port
                    && this.user.equals(person.user) && this.psw.equals(person.psw) && this.database.equals(person.database);
        }

        public int hashCode() {
            return 1 * server.hashCode() + 3 * new Integer(port).hashCode() + 5 * user.hashCode() + 7 * psw.hashCode() + 9 * database.hashCode();
        }

        public String getServer() {
            return server;
        }

        public void setServer(String server) {
            this.server = server;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }

        public String getPsw() {
            return psw;
        }

        public void setPsw(String psw) {
            this.psw = psw;
        }

        public String getDatabase() {
            return database;
        }

        public void setDatabase(String database) {
            this.database = database;
        }

        public String getCompany() {
            return company;
        }

        public void setCompany(String company) {
            this.company = company;
        }

        public String getProject() {
            return project;
        }

        public void setProject(String project) {
            this.project = project;
        }

        public String getRoom() {
            return room;
        }

        public void setRoom(String room) {
            this.room = room;
        }
    }
}

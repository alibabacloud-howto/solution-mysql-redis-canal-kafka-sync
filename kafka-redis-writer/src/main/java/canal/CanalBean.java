package canal;

import java.util.List;

class User {
	private String id;
	private String username;
	private String password;
	private String addr;
	private String phone;

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getAddr() {
		return addr;
	}
	public void setAddr(String addr) {
		this.addr = addr;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	String nickname;
}

class MysqlType {
    private String id;
    private String username;
    private String password;
    private String addr;
    private String phone;
    private String nickname;
    //getter、setter
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getAddr() {
		return addr;
	}
	public void setAddr(String addr) {
		this.addr = addr;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
}

class SqlType {
	private int id;
	private int username;
	private int password;
	private int addr;
	private int phone;
	private int nickname;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getUsername() {
		return username;
	}
	public void setUsername(int username) {
		this.username = username;
	}
	public int getPassword() {
		return password;
	}
	public void setPassword(int password) {
		this.password = password;
	}
	public int getAddr() {
		return addr;
	}
	public void setAddr(int addr) {
		this.addr = addr;
	}
	public int getPhone() {
		return phone;
	}
	public void setPhone(int phone) {
		this.phone = phone;
	}
	public int getNickname() {
		return nickname;
	}
	public void setNickname(int nickname) {
		this.nickname = nickname;
	}
}

public class CanalBean {
	//data
	private List<User> data;
    //database name
	private String database;
	private long es;
    // auto add from 1
	private int id;
    //DDL?
	private boolean isDdl;
    //type
	private MysqlType mysqlType;
    //UPDATE, old data
	private String old;
    // primary key
	private List<String> pkNames;
    //sql
	private String sql;
	private SqlType sqlType;
    //table name
    private String table;
    private long ts;
    //INSERT、UPDATE、DELETE、ERASE
    private String type;
    //getter、setter
	public List<User> getData() {
		return data;
	}
	public void setData(List<User> data) {
		this.data = data;
	}
	public String getDatabase() {
		return database;
	}
	public void setDatabase(String database) {
		this.database = database;
	}
	public long getEs() {
		return es;
	}
	public void setEs(long es) {
		this.es = es;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public boolean isDdl() {
		return isDdl;
	}
	public void setDdl(boolean isDdl) {
		this.isDdl = isDdl;
	}
	public MysqlType getMysqlType() {
		return mysqlType;
	}
	public void setMysqlType(MysqlType mysqlType) {
		this.mysqlType = mysqlType;
	}
	public String getOld() {
		return old;
	}
	public void setOld(String old) {
		this.old = old;
	}
	public List<String> getPkNames() {
		return pkNames;
	}
	public void setPkNames(List<String> pkNames) {
		this.pkNames = pkNames;
	}
	public String getSql() {
		return sql;
	}
	public void setSql(String sql) {
		this.sql = sql;
	}
	public SqlType getSqlType() {
		return sqlType;
	}
	public void setSqlType(SqlType sqlType) {
		this.sqlType = sqlType;
	}
	public String getTable() {
		return table;
	}
	public void setTable(String table) {
		this.table = table;
	}
	public long getTs() {
		return ts;
	}
	public void setTs(long ts) {
		this.ts = ts;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
}

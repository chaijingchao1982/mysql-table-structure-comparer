package com.cjc.tablecomparer;

/**
 * @author cjc
 * @date Jul 25, 2019
 */
public class Main {

	/** the table name which want to compare */
	private static final String[] TABLE_NAME_ARRAY = { 
			"game_battle_record", 
			"game_client_info", 
			"game_prop",
			"game_user", };

	public static void main(String args[]) {
		MySqlServerInfo db1 = new MySqlServerInfo();
		db1.url = "192.168.30.246:3306";
		db1.dbName = "social";
		db1.setting = "useSSL=false&serverTimezone=UTC";
		db1.usrName = "root";
		db1.password = "root";

		MySqlServerInfo db2 = new MySqlServerInfo();
		db2.url = "192.168.30.246:3306";
		db2.dbName = "social_game";
		db2.setting = "useSSL=false&serverTimezone=UTC";
		db2.usrName = "root";
		db2.password = "root";

		new TableCompareUtil().compare(db1, db2, TABLE_NAME_ARRAY);
	}
}

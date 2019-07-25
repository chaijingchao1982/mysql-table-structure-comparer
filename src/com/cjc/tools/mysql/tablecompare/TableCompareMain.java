package com.cjc.tools.mysql.tablecompare;

/**
 * @author cjc
 * @date Jul 25, 2019
 */
public class TableCompareMain {

	/** 需要比较的表 */
	private static final String[] TABLE_NAME_ARRAY = { 
			"cute_meet_game_battle_record", 
			"cute_meet_game_client_info",
			"cute_meet_game_prop", 
			"cute_meet_game_user",

			"cute_meet_game_block_game_order_201907", 
			"cute_meet_game_bull_game_order_201907",
			"cute_meet_game_crossroad_game_order_201907", 
			"cute_meet_game_jump_game_order_201907",
			"cute_meet_game_knife_game_order_201907", 
			"cute_meet_game_ludo_game_order_201907",
			"cute_meet_game_star_game_order_201907", };

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

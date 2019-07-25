package com.cjc.tablecomparer;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * @author cjc
 * @date Nov 26, 2018
 */
public class TableCompareUtil {

	private static final String TAB = "    ";

	private HashMap<String, HashSet<String>> map1 = new HashMap<>();
	private HashMap<String, HashSet<String>> map2 = new HashMap<>();

	public void compare(MySqlServerInfo db1, MySqlServerInfo db2, String[] tableNameArray) {
		dbInit(db1.url, db1.dbName, db1.setting, db1.usrName, db1.password, tableNameArray, map1);
		dbInit(db2.url, db2.dbName, db2.setting, db2.usrName, db2.password, tableNameArray, map2);

		compareTable(db1.dbName, db2.dbName);
		compareColumn(db1.dbName, db2.dbName);
	}

	private void compareColumn(String dbName1, String dbName2) {
		System.out.println("\n***diff column***");

		for (HashMap.Entry<String, HashSet<String>> entry : map1.entrySet()) {
			String tableName = entry.getKey();
			if (!map2.containsKey(tableName)) {
				continue;
			}

			HashSet<String> set1 = map1.get(tableName);
			HashSet<String> set2 = map2.get(tableName);
			List<String> sameKeys = new ArrayList<>();
			for (String key : set1) {
				if (set2.contains(key)) {
					sameKeys.add(key);
				}
			}

			for (String key : sameKeys) {
				set1.remove(key);
				set2.remove(key);
			}

			if (set1.size() == 0 && set2.size() == 0) {
				continue;
			}

			System.out.println(tableName);
			System.out.println(TAB + dbName1 + ":");
			for (String key : set1) {
				System.out.println(TAB + TAB + key);
			}
			System.out.println(TAB + dbName2 + ":");
			for (String key : set2) {
				System.out.println(TAB + TAB + key);
			}
			System.out.println();
		}
	}

	private void compareTable(String dbName1, String dbName2) {
		List<String> diffTableName1 = new ArrayList<>();
		List<String> diffTableName2 = new ArrayList<>();
		for (HashMap.Entry<String, HashSet<String>> entry : map1.entrySet()) {
			String tableName = entry.getKey();
			if (!map2.containsKey(tableName)) {
				diffTableName1.add(tableName);
			}
		}
		for (HashMap.Entry<String, HashSet<String>> entry : map2.entrySet()) {
			String tableName = entry.getKey();
			if (!map1.containsKey(tableName)) {
				diffTableName2.add(tableName);
			}
		}

		if (diffTableName1.size() == 0 && diffTableName2.size() == 0) {
			return;
		}

		System.out.println("***diff table***");
		if (diffTableName1.size() > 0) {
			System.out.println(dbName1 + ":");
			for (String n : diffTableName1) {
				System.out.println(TAB + n);
			}
		}

		if (diffTableName2.size() > 0) {
			System.out.println();
			System.out.println(dbName2 + ":");
			for (String n : diffTableName2) {
				System.out.println(TAB + n);
			}
		}
	}

	private void dbInit(String url, String dbName, String config, String usrName, String password,
			String[] tableNameArray, HashMap<String, HashSet<String>> map) {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();

			Connection conn = DriverManager.getConnection(
					"jdbc:mysql://" + url + "/" + dbName + "?" + config + "&user=" + usrName + "&password=" + password);
			DatabaseMetaData metaData = conn.getMetaData();
			ResultSet tableRet = metaData.getTables(dbName, "%", "%", new String[] { "TABLE" });

			// get the exist table name
			HashSet<String> allExistTableNames = new HashSet<>();
			while (tableRet.next()) {
				String tableName = tableRet.getString("TABLE_NAME");
				allExistTableNames.add(tableName);
			}

			for (String tableName : tableNameArray) {
				if (!allExistTableNames.contains(tableName)) {
					// filter the no exist table name
					continue;
				}

				// System.out.println(tableName);
				HashSet<String> set = new HashSet<>();
				ResultSet colRet = metaData.getColumns(dbName, "%", tableName, "%");
				while (colRet.next()) {
					String columnName = colRet.getString("COLUMN_NAME");
					String columnType = colRet.getString("TYPE_NAME");
					int dataSize = colRet.getInt("COLUMN_SIZE");
					int digits = colRet.getInt("DECIMAL_DIGITS");
					int nullable = colRet.getInt("NULLABLE");
					final String KEY = columnName + " " + columnType + " " + dataSize + " " + digits + " " + nullable;
					// System.out.println(KEY);
					set.add(KEY);
				}

				map.put(tableName, set);
				// System.out.println("----------------");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

package org.aisen.osc.support.utils;

import com.m.support.sqlite.SqliteUtility;

public class OSCDb {

	public static SqliteUtility getSqlite() {
		return SqliteUtility.getInstanceInApp("aisenforosc", "aisenforosc");
	}
	
}

package com.zeek.services;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.zeek.fbbot.pojo.FBProfile;
import com.zeek.fwk.ZDBConnection;

public class ZeeKServices {
	private static ZeeKServices _instance = null;

	
	//BOT QUERIES
        private static final String insertBotFbUser_template = "INSERT INTO BOT_FB_USER(USER_ID,FB_USER_ID,FNAME,LNAME,GENDER,LOCALE,TIMEZONE,PIC) VALUES (NULL,'FB_SENDER','FB_FNAME','FB_LNAME','FB_GENDER','FB_LOCALE','FB_TZ','FB_PIC')";	
	
	//forcing singleton
	private ZeeKServices() {
		
	}

	public static ZeeKServices getInstance() {
		if (_instance == null) {
			_instance = new ZeeKServices();
		}
		return _instance;
	}
	
	
	/* adds fb profile and returns new user_id */
	public int bot_addNewUser(FBProfile profile, String sender) {
		int user_id = -1;
		ZDBConnection con = ZDBConnection.getInstance();
		String fname = profile.getFirstName();
		String lname = profile.getLastName();
		String gender = profile.getGender();
		String locale = profile.getLocale();
		String tz = profile.getTimezone().toString();
		String pic = profile.getProfilePic();
		
		String query = insertBotFbUser_template;
		
		query = query.replace("FB_SENDER", sender);
		query = query.replace("FB_FNAME", fname);
		query = query.replace("FB_LNAME", lname);
		query = query.replace("FB_GENDER", gender);
		query = query.replace("FB_LOCALE", locale);
		query = query.replace("FB_TZ", tz);
		query = query.replace("FB_PIC", pic);
		
		
		try {
			int r = con.executeUpdate(query);
		}catch(Exception e){
			e.printStackTrace();
			con.close();
			return user_id;
		}
		con.close();
		return bot_getUserId(sender);
	}

	public boolean bot_checkExistingUser(String sender) {
		String query = "SELECT * FROM BOT_FB_USER WHERE FB_USER_ID = '"+sender+"'";
		ZDBConnection con = ZDBConnection.getInstance();
		try{
			ResultSet rs = con.executeQuery(query);
			if (rs.next()){
				return true;
			} else {
				return false;
			}
		}catch(Exception e){
			e.printStackTrace();
			con.close();
			return false;
		}finally {
			con.close();
		}
	}
	
	public int bot_getUserId(String sender){
		int user_id = -1;
		ZDBConnection con = ZDBConnection.getInstance();
		String query = "SELECT USER_ID FROM BOT_FB_USER WHERE FB_USER_ID='"+sender+"'";

		try {
			ResultSet rs = con.executeQuery(query);
			rs.next();
			user_id = rs.getInt(1);
		}catch(Exception e){
			e.printStackTrace();
			con.close();
			return user_id;
		}		
		return user_id;
	}
	
}

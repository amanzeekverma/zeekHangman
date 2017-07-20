package com.zeek.hangman;

import java.util.Date;

public class SavedData {
	String  user_id;
	
	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}
	public String getLastMask() {
		return lastMask;
	}
	public void setLastMask(String lastMask) {
		this.lastMask = lastMask;
	}
	public int getQid() {
		return qid;
	}
	public void setQid(int qid) {
		this.qid = qid;
	}
	public String getHint() {
		return hint;
	}
	public void setHint(String hint) {
		this.hint = hint;
	}
	public boolean isDoubleDown() {
		return doubleDown;
	}
	public void setDoubleDown(boolean doubleDown) {
		this.doubleDown = doubleDown;
	}
	
	int score;
	String lastMask;
	int qid;
	String hint;
	boolean doubleDown;
	Date lastSaved;
	String fname;
	
	
	public String getFname() {
		return fname;
	}
	public void setFname(String fname) {
		this.fname = fname;
	}
	public Date getLastSaved() {
		return lastSaved;
	}
	public void setLastSaved(Date lastSaved) {
		this.lastSaved = lastSaved;
	}
	
	
}

package com.zeek.hangman;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;
import com.zeek.fbbot.pojo.FBProfile;
import com.zeek.fbbot.webhook.ZHangmanWebHook;
import com.zeek.fwk.ZDBConnection;


public class ZHMService {
	private static ZHMService _instance = null;
	public static ZHMService getInstance() {
		if (_instance == null) {
			_instance = new ZHMService();
		}
		return _instance;
	}


        public void unsubscribe(String user_id){
                ZDBConnection con = ZDBConnection.getInstance();
                String query = "UPDATE ZHM_USER SET IS_SUBSCRIBED = 0 WHERE ID = '"+user_id+"'";
                try {
                      con.executeUpdate(query);  
                } catch (Exception e){
                      e.printStackTrace();
                }
                con.close();
        }

	public void persistUserData(SavedData sd) {
		ZDBConnection con = ZDBConnection.getInstance();
		String user_id = sd.getUser_id();
		
		int ddstatus = 0;
		if (sd.isDoubleDown()){
			ddstatus = 1;
		}
		
		int hintStatus = 1;
		if (sd.getHint()== null || sd.getHint().equals("")){
			hintStatus = 0;
		}
		
		String query = "UPDATE ZHM_USER SET IS_SUBSCRIBED = 1, LAST_ACCESS = NOW(), SCORE = "+sd.getScore()+
				", LAST_MASK = '"+sd.getLastMask()+"'"+", CUR_QID = "+sd.getQid()+", DDStatus = " +ddstatus+", HintStatus = " +hintStatus+
						" WHERE " +
						"ID = '"+user_id+"'";
		try {
			con.executeUpdate(query);
		}catch(Exception e){
			e.printStackTrace();
		}
		con.close();
	}
	public void populateUserData(SavedData newsd) {
		ZDBConnection con = ZDBConnection.getInstance();
		String sender = newsd.getUser_id();
		String query = "SELECT * FROM ZHM_USER WHERE ID = '"+sender+"'";
		try {
			ResultSet rs = con.executeQuery(query);
			if (rs.next()){
			    newsd.setScore(rs.getInt("SCORE"));
			    
			    boolean dd_bool = false;
			    int dd = rs.getInt("DDStatus");
			    if (dd == 1) {
			    	dd_bool = true;
			    }
			    newsd.setDoubleDown(dd_bool);
			    
			    newsd.setFname(rs.getString("FNAME"));
			    
			    newsd.setLastMask(rs.getString("LAST_MASK"));
			    
			    newsd.setQid(rs.getInt("CUR_QID"));
			    
			    Timestamp ts = rs.getTimestamp("LAST_ACCESS");
			    Date lastAccess = ts;		
			    newsd.setLastSaved(lastAccess);
			    
			    int hintstatus = rs.getInt("HintStatus");
			    if (hintstatus == 1){
			    	String hint = getHint(newsd.getQid());
			    	newsd.setHint(hint);
			    }
			    
			} else {
				//OH THIS IS A NEW USER .... YAY!
				FBProfile profile = requestFBProfile(sender);
				String fname = profile.getFirstName();
				String lname = profile.getLastName();
				ZDBConnection con1 = ZDBConnection.getInstance();
				String query1 = "INSERT INTO ZHM_USER VALUES ("+
						" '"+sender+"', " +
						" '"+fname+"', " +
						" '"+lname+"', " +
						"0,'',1,0,0, NOW(),1)";
				try {
					con1.executeUpdate(query1);
				}catch(Exception e){
					e.printStackTrace();
				}
				con1.close();
				
				newsd.setFname(fname);
				newsd.setDoubleDown(false);
				newsd.setScore(0);
				newsd.setQid(1);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		ArenaCache.getInstance().saveData(newsd);
		con.close();
		
	}
	
	
	 public String getHint(int qid) {
		 String hint = "LOL!";
			ZDBConnection con = ZDBConnection.getInstance();
			String query = "SELECT HINT FROM ZHM_QUESTIONS WHERE QID = "+qid;
			try {
				ResultSet rs = con.executeQuery(query);
				if (rs.next()){
					hint = rs.getString("HINT");
				}
		    }catch(Exception e){
		    	e.printStackTrace();
		    }
			con.close();
			return hint;
	}
	 
	
	 
	private FBProfile requestFBProfile(String sender){
     	//retrieve FB profile from FB
     	String url = ZHangmanWebHook.fbProfileFetchURL.replace("XXXX", sender);
     	HttpGet http_getFBProfile = new HttpGet(url);
     	System.out.println("Request TO FB = "+url);
     	FBProfile profile = null;
     	try {
     	HttpResponse ack = ZHangmanWebHook.client.execute(http_getFBProfile);
			String result = EntityUtils.toString(ack.getEntity());
			System.out.println("Response from FB = "+result);
			result = result.replaceAll("5.5", "5");
			profile = new Gson().fromJson(result, FBProfile.class );
     	}catch(Exception e){
 			System.out.println(e.getMessage());
 			e.printStackTrace();
 		}
     	return profile;
     }
	public String getQuestion(int cur_qid) {
		String question = "Something bad has happened";
		ZDBConnection con = ZDBConnection.getInstance();
		String query = "SELECT QUESTION FROM ZHM_QUESTIONS WHERE QID = "+cur_qid;
		try {
			ResultSet rs = con.executeQuery(query);
			if (rs.next()){
				question = rs.getString("QUESTION");
			}
	    }catch(Exception e){
	    	e.printStackTrace();
	    }
		con.close();
		return question;
	}
	public String getAnswer(int cur_qid) {
		String answer = "LOL!";
		ZDBConnection con = ZDBConnection.getInstance();
		String query = "SELECT ANSWER FROM ZHM_QUESTIONS WHERE QID = "+cur_qid;
		try {
			ResultSet rs = con.executeQuery(query);
			if (rs.next()){
				answer = rs.getString("ANSWER");
			}
	    }catch(Exception e){
	    	e.printStackTrace();
	    }
		con.close();
		return answer;
	}
	public List<String> getSadUsers() {
		List<String> sadUsers = new ArrayList<String>();
		ZDBConnection con = ZDBConnection.getInstance();
		String query = "SELECT ID FROM ZHM_USER WHERE DATEDIFF(NOW(),LAST_ACCESS) > 4 AND CUR_QID <16 AND CUR_QID > 1";
		try {
			ResultSet rs = con.executeQuery(query);
			while (rs.next()){
				String s = rs.getString("ID");
				sadUsers.add(s);
			}
	    }catch(Exception e){
	    	e.printStackTrace();
	    }
		con.close();
		return sadUsers;
	}
	public List<String> getGoodUsers() {
		List<String> goodUsers = new ArrayList<String>();
		ZDBConnection con = ZDBConnection.getInstance();
		String query = "SELECT ID, FNAME FROM ZHM_USER WHERE ID > 1 AND IS_SUBSCRIBED = 1";
		try {
			ResultSet rs = con.executeQuery(query);
			while (rs.next()){
				String s = rs.getString("ID");
				goodUsers.add(s);
				//System.out.println("New Notification sent to :"+rs.getString("FNAME"));
			}
	    }catch(Exception e){
	    	e.printStackTrace();
	    }
		con.close();
		return goodUsers;
	}
        public String getTop6(){
               String result="";
               ZDBConnection con = ZDBConnection.getInstance();
               String query = "SELECT FNAME, LNAME, SCORE FROM ZHM_USER ORDER BY SCORE DESC LIMIT 6";
               try {
                      ResultSet rs = con.executeQuery(query);
                      int r = 1;
                      while (rs.next()){
                             String row="";
                             String f = rs.getString("FNAME");
                             String l = ""+rs.getString("LNAME").charAt(0);
                             String score = ""+rs.getInt("SCORE");
                             String name = f+","+l;
                             row += (r+" : "+name);
                             int len = row.length();
                             if (len > 20)  {
                                   row = row.substring(0,20);
                                   len = row.length();
                             }
                              
                             row +=(" : "+score);
                             result += row+"\n";
                             r++;
                      }
               }catch(Exception e){
                  e.printStackTrace();
               }
               con.close();
               return result;
        }
        public String getRank(String sender){
              String result = "Your Rank : ";
              ZDBConnection con = ZDBConnection.getInstance();
              String query = "SELECT COUNT(1) FROM ZHM_USER WHERE SCORE > (SELECT SCORE FROM ZHM_USER WHERE ID = '"+sender+"')";
              try {
                     ResultSet rs = con.executeQuery(query);
                     while (rs.next()){
                           int rank = rs.getInt(1)+1;
                           result = result + rank;
                     }
              }catch(Exception e){
                 e.printStackTrace();
              }
              con.close();
              return result;
        }
	
	
}

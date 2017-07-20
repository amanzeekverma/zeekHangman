package com.zeek.hangman;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class ArenaCache {
	
		private static  ArenaCache _instance = null;
		private static HashMap<Integer, String> _questionsMap = new HashMap<Integer, String>();
		private static HashMap<Integer, String> _answerMap = new HashMap<Integer, String>();
		
		private ArenaCache(){
			//cheap singleton!
		}
		public static ArenaCache getInstance(){
			if (_instance == null){
				_instance = new ArenaCache();
			} 
			return _instance;
		}
		
		private static HashMap<String, SavedData> _savedDataCache = new HashMap<String, SavedData>();
		
		public SavedData getSavedData(String user_id){
			try {
				SavedData _sd = null;
				_sd = _savedDataCache.get(user_id);
				return _sd;
			}catch(Exception e){
				return null;
			}
		}
		
		public synchronized void  saveData(SavedData sd){
			String user_id = sd.getUser_id();
			Date d = new Date();
			sd.setLastSaved(d);
			_savedDataCache.put(user_id, sd);
			cleanSavedData(sd);
		}

		private void cleanSavedData(SavedData sd) {
			//If saved data map has more than XXX users. Clean map based on last saved date.
			//TO DO LATER
			ZHMService.getInstance().persistUserData(sd);
		}
		
		public String getQuestion(int cur_qid) {
			String question = _questionsMap.get(cur_qid);
			if (question == null || question.equals("")){
				question = ZHMService.getInstance().getQuestion(cur_qid);
				_questionsMap.put(cur_qid, question);
			}
			return question;
		}
		public String getMask(int cur_qid) {
			String mask = " > ";
			String answer = getAnswer(cur_qid);
			for (int i=0; i<answer.length(); i++){
				mask+="_ ";
			}
			return mask;
		}
		
		public  String getAnswer(int cur_qid) {
			String answer = _answerMap.get(cur_qid);
			if (answer == null || answer.equals("")){
				answer = ZHMService.getInstance().getAnswer(cur_qid);
				_answerMap.put(cur_qid, answer);
			}
			return answer;
		}
		public void clearCache() {
			 _questionsMap = new HashMap<Integer, String>();
			 _answerMap = new HashMap<Integer, String>();
			 _savedDataCache = new HashMap<String, SavedData>();
			 
		}
		
		public void blowHint(String sender) {
			SavedData sd = getSavedData(sender);
			String hint = ZHMService.getInstance().getHint(sd.getQid());
			sd.setHint(hint);
			sd.setScore(sd.getScore() - 3);
			saveData(sd);
		}
		public SavedData getNextQuestion(String sender) {
			SavedData sd = _savedDataCache.get(sender);
			int cur_qid = sd.getQid();
			int next_qid = cur_qid + 1;
			sd.setHint(null);
			sd.setDoubleDown(false);
			sd.setQid(next_qid);
			sd.setLastMask(null);
			saveData(sd);
			return sd;
		}
		public void blowDoubleDown(String sender) {
			SavedData sd = getSavedData(sender);
			sd.setDoubleDown(true);
			saveData(sd);
			
		}
		public List<String> getSadUsers() {
			List<String> sadUsers = ZHMService.getInstance().getSadUsers();
			return sadUsers;
		}
		public List<String> getGoodUsers() {
			List<String> goodUsers = ZHMService.getInstance().getGoodUsers();
			return goodUsers;
		}
                public String getScoreBoard(String sender){
                        String result = "";
                        String top6 = ZHMService.getInstance().getTop6(); //get formatted string
                        String individualRank = ZHMService.getInstance().getRank(sender); //get formatted string.
                        result = top6+"\n"+individualRank; 
			return result;
                }
		
		
		
		
}

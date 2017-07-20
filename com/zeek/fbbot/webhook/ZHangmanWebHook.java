package com.zeek.fbbot.webhook;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;
import com.zeek.fbbot.pojo.FBMessage;
import com.zeek.fbbot.pojo.FBProfile;
import com.zeek.fbbot.pojo.Message;
import com.zeek.fbbot.pojo.Messaging;
import com.zeek.fbbot.pojo.Recipient;
import com.zeek.fbbot.pojo.btnmenu.Button;
import com.zeek.fbbot.pojo.btnmenu.FBButtonMenu;
import com.zeek.fbbot.pojo.btnmenu.Payload;
import com.zeek.fbbot.pojo.btnmenu.QuickReply;
import com.zeek.hangman.ArenaCache;
import com.zeek.hangman.SavedData;
import com.zeek.hangman.ZHMService;
import com.zeek.services.ZeeKServices;

public class ZHangmanWebHook extends HttpServlet{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

        private static int testMode=1;
        private static final ArrayList<String> whiteListTesters = new ArrayList<String>(Arrays.asList("1343946588991496","-1"));

	/************* FB Chat Bot variables *************************/
	public static final String PAGE_TOKEN = "MASKED MASKED MASKED MASKED";
											 
	private static final String VERIFY_TOKEN = "SOMETHING";
	private static final String FB_MSG_URL = "https://graph.facebook.com/v2.8/me/messages?access_token="
			+ PAGE_TOKEN;
	/*************************************************************/

	/******* for making a post call to fb messenger api **********/
	public  static final HttpClient client = HttpClientBuilder.create().build();
	public static final HttpPost httppost = new HttpPost(FB_MSG_URL);
	/*************************************************************/


          /**************************************/
	  //Replace XXXX with right search string.
           public static final String fbProfileFetchURL = "https://graph.facebook.com/v2.8/XXXX?access_token=MASKEDMASKEDMASKED"; 
          /***************************************/

	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}

	/**
	 * get method is used by fb messenger to verify the webhook
	 */
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String queryString = request.getQueryString();
		String msg = "Come on, you cannot hit this !";

		if (queryString != null) {
			String verifyToken = request.getParameter("hub.verify_token");
			String challenge = request.getParameter("hub.challenge");
			// String mode = request.getParameter("hub.mode");
			msg=challenge;
			/*if (StringUtils.equals(VERIFY_TOKEN, verifyToken)
					&& !StringUtils.isEmpty(challenge)) {
				msg = challenge;
			} else {
				msg = "";
			}*/
		} else {
			System.out
					.println("Exception no verify token found in querystring:"
							+ queryString);
		}

		response.getWriter().write(msg);
		response.getWriter().flush();
		response.getWriter().close();
		response.setStatus(HttpServletResponse.SC_OK);
		System.out.println("Good Job on GET");
		return;
	}

	private void processRequest(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
	//System.out.println("POST REQUEST RECEIVED");
	String responseTxt = "ZHM_ARENA>";
	StringBuffer jb = new StringBuffer();
	  String line = null;
	  try {
	    BufferedReader reader = request.getReader();
	    while ((line = reader.readLine()) != null)
	      jb.append(line);
	  } catch (Exception e) { /*report an error*/ }

	  
	  FBMessage fbReq = new Gson().fromJson(jb.toString(), FBMessage.class);
          if (this.testMode == 1){
	     System.out.println(jb.toString()); 
          }
	  if (fbReq == null){
		  System.out.println("empty FB request");
		  response.setStatus(HttpServletResponse.SC_OK);
		  return;
	  }
	  
	  int entrySize = fbReq.getEntry().size();
	  System.out.println("entry size = "+entrySize);
	  for (int i=0; i< entrySize; i++){
		  List<Messaging> messagings = fbReq.getEntry().get(i)
					.getMessaging();
			for (Messaging messaging : messagings) {
				String sender = messaging.getSender().getId();
				if (messaging.getMessage() != null
						&& messaging.getMessage().getText() != null) {    //A USER INPUT.... YAAY!
					String input = messaging.getMessage().getText();
					if (input.equals("zeek clear cache")){
						ArenaCache.getInstance().clearCache();
						response.setStatus(HttpServletResponse.SC_OK);
						return;
					} else if (input.equals("zeek remind users")){
						String text = "Hey there! the challenge will end soon. You still have pending questions...";
						List<String> sadUsers = ArenaCache.getInstance().getSadUsers();
						for (String sadUser : sadUsers){
							//sendResponse(sadUser, text);
                                                        sendNotification(sadUser, text);
						}
						response.setStatus(HttpServletResponse.SC_OK);
						return;
					}  else if (input.equals("zeek send notification")){
						String text = "NEW CHALLENGE IS AVAILABLE NOW!";
						List<String> goodUsers = ArenaCache.getInstance().getGoodUsers();
						for (String user : goodUsers ){
							//sendResponse(user, text);
                                                        sendNotification(user, text);  //F4 
                                                        try { Thread.sleep(300); } catch(Exception e){} //Sleeping 300ms for D7
						}
						response.setStatus(HttpServletResponse.SC_OK);
						return;
					} else if (input.startsWith("zeek testMode")){ //F9
                                                if (this.testMode == 1)
						   this.testMode = 0;
						else 
						   this.testMode = 1;
					        response.setStatus(HttpServletResponse.SC_OK);	
                                                return;
                                        } else if (input.contains("ABOUT GAME")){  //Quick Reply Hack : Fix POJOs
                                           String about = "Hi from ZeeK-Hangman |\nSCORING:\n \u270f 2 for each correct letter guess\n \u270f (-1) for each wrong guess\n \u270f (-3) for HINT\n \u270f You can guess the whole word or single letter\n \u270f Double Down to get twice the point for guess. (Guess 1 right letter for 4 point and whole word for {left letters}*4)\n \u270f FOR MORE http://java-codeperformance.rhcloud.com/zeekhangman.html";
                                           sendResponse(sender, about);        
                                        } else if (input.contains("SCOREBOARD")){
                                           String top6 = "\ud83c\udfc6 TOP 6 \ud83c\udfc6 \n";
                                           String board = ArenaCache.getInstance().getScoreBoard(sender);
                                           sendResponse(sender, top6+board);
                                        } else if (input.contains("LIKE/SHARE")){
                                           String pageDetails = "Please visit/like the FB page below and let your friends know!\n\nhttps://www.facebook.com/zeekHangman/";
                                           sendResponse(sender, pageDetails);
                                        }
					//GO PLAY NOW!
					responseTxt = processInput(input, sender);     
					response.setStatus(HttpServletResponse.SC_OK);
					
				} else if (messaging.getPostback() != null 
						&& messaging.getPostback().getPayload()!=null){
					String inputButton =messaging.getPostback().getPayload();
					responseTxt = processInputButton(inputButton, sender);
					response.setStatus(HttpServletResponse.SC_OK);
				} else {
					System.out.println("Found a request without message..skipping it. I am assuming this is ACK back.");
					//dropping this.
					response.setStatus(HttpServletResponse.SC_OK);
					return;
				}
			}  
	  }
	  System.out.println("Sending HTTP 200 against POST");
	  response.setStatus(HttpServletResponse.SC_OK);
	}

	private String processInputButton(String inputButton, String sender) {
		String responseTxt = "DUMMY RESPONSE"; //Is this being used??
		if (inputButton.equals("hint")){
			ArenaCache.getInstance().blowHint(sender);
			processInput(inputButton, sender);
			
		} else if (inputButton.equals("double down")){
			ArenaCache.getInstance().blowDoubleDown(sender);
			String dd_msg= "***DOUBLE DOWN ACTIVATED***";
			sendResponse(sender, dd_msg);
			processInput(inputButton, sender);

		} else if (inputButton.equals("skip")){
                        processInput("zeek next", sender);

		} else if (inputButton.equals("unsubscribe")){
                        String sorryMessage = "Notifications are stupid! I get it. I will not be sending you any Notifications. \n Please LIKE/SHARE this bot. \nVisit: http://java-codeperformance.rhcloud.com/zeekhangman.html \n You can still come back and play. Once you start playing, we will quietly whitelist you again and send stupid notifications :) but you know how to unsubscribe, its just 1 click!!! \nHave a nice day!";
                        sendResponse(sender, sorryMessage);
                        ZHMService.getInstance().unsubscribe(sender);  

                } else if (inputButton.equals("hi")){
                        String hiMessage = "Hi!, Welcome to ZeeK Hangman. \n Enjoy the ride!"; //Yeah no need to send this to user. Fuck it!
                        processInput(inputButton, sender);

                }
		return responseTxt; //Note: This is found to be useless! Take care later!
	}

	private String processInput(String input, String sender) {
                if (this.testMode == 1){
                   String upgradingMessage = "You caught us with our pants down. The system is down for maintainance. Please visit after some time. Sorry!";
                   if (whiteListTesters.contains(sender)) {
                        String testerMsg = "\n\n***HACKED TEST MODE ACTIVE FOR YOU!***";
                        sendResponse(sender, upgradingMessage+testerMsg); 
                   } else {
			System.out.println("Missed User:"+sender);
                        sendResponse(sender, upgradingMessage);
                        return "";
                   }
                } 
		String responseTxt = "DUMMY RESPONSE";
		boolean showOTM = false;
		boolean showWelcomeBack = false;
		//CHECK IF USER IS PRESENT IN CACHE
		SavedData sd = ArenaCache.getInstance().getSavedData(sender);
		if (sd == null){ //USER NOT IN CACHE
			//CHECK IF USER IS PRESENT IN DB
			sd = new SavedData();
			sd.setUser_id(sender);
			ZHMService.getInstance().populateUserData(sd); //INTERNALLY ADDS NEW USER!
			showWelcomeBack = true;
		}
		String lastMask = sd.getLastMask();

                if (lastMask == null)
				lastMask = "null";

		boolean nextQuestionPlz = false;
		if (input.equals("zeek next") || (lastMask != null && lastMask.length() > 1 && !lastMask.contains("_") && !lastMask.equals("null"))){
			nextQuestionPlz = true;
		}
		if (nextQuestionPlz){
			sd = ArenaCache.getInstance().getNextQuestion(sender);
			lastMask = sd.getLastMask();
		}
		if (sd.getQid() == 16){
			String endMessage = "YOU HAVE SUCCESSFULLY COMPELTED THE CHALLENGE\nYOUR SCORE IS : "+sd.getScore() +"\n\n ** New Challenge will be posted soon, Thank you! **";
			sendResponse(sender, endMessage);
                        String top6 = "\ud83c\udfc6 TOP 6 \ud83c\udfc6 \n";
                        String board = ArenaCache.getInstance().getScoreBoard(sender);
                        sendResponse(sender, top6+board);
			return "";
		}
		if (sd.getQid() == 1 && !lastMask.contains("_")){   //This user has not played his first move yet.
			showOTM = true;
		}
		if (showOTM){
			String oneTimeMsg = "**ONE TIME MESSAGE**\nThis is a 'kind' of hangman i.e., you can guess letter (one at a time).\n OR, To save time, you can guess the word as well" +
					"\nFIRST TIME PLAYERS: do check \"ABOUT GAME\" before playing, to know how scoring works";
			sendResponse(sender, oneTimeMsg);
		}
		if (showWelcomeBack){  //This user is back after 1)A while 2)A hot deployment: Lets load him again and ask to send input again.
			String welcomeBack = sd.getFname()+", We Love You! <3";
			sendResponse(sender, welcomeBack);
		}
		//GAME NOW
		int score = sd.getScore();
		int cur_qid = sd.getQid();
		String question = ArenaCache.getInstance().getQuestion(cur_qid);	
		String answer = ArenaCache.getInstance().getAnswer(cur_qid);
		String hint = sd.getHint();
		String fname = sd.getFname();
		boolean dd = sd.isDoubleDown();		
                
                //D6: HACK
		if (lastMask == null || lastMask.equals("null")){
			lastMask = ArenaCache.getInstance().getMask(cur_qid);
			sd.setLastMask(lastMask);
		}
		//D6 end

                //System.out.println("CHECK PLAY: fname, qid, question, answer, hint, score, dd_indicator: INPUT: LASTMASK");
                System.out.println("CHECK PLAY: "+fname+","+cur_qid+","+question+","+answer+","+hint+","+score+","+dd+":INPUT:"+input+" AGAINST:"+lastMask);
				
		if (input != null && !input.equals("") 
				&& !input.equals("hint") && !input.equals("double down")
				     && !input.equals("zeek next")) {
			if (input.length()==1){
				boolean playover = true;
				String checkChar = input.toUpperCase();
				if (lastMask.contains(input)){
					String lolMessage = "Hey hey, you got points for that";
					sendResponse(sender, lolMessage);
					return "";
				}
				if (answer.contains(checkChar)){
					String hurrayMessage = "Bingo! There you go!";
					sendResponse(sender, hurrayMessage);
					String newMask = " > ";
					int deltaScore = 0;
					for (int i =0; i<answer.length(); i++){
						if (answer.charAt(i)==checkChar.charAt(0)){
							if (!dd)
								deltaScore+=2;
							else 
								deltaScore+=4;
							newMask += (checkChar+" ");
						} else {
							newMask += ("_ ");
						}
					}
                                        //System.out.println("DEBUG:1");
                                        //System.out.println("DEBUG: last mask ="+lastMask);
					//merge new-mask and last-mask
					char[] mask = newMask.toCharArray();
					for (int i=3; i< newMask.length(); i++){   // mask starts with " > "
						if (lastMask.charAt(i)!='_' && lastMask.charAt(i)!=' '){
							mask[i] = lastMask.charAt(i);
						}
					}
					lastMask = String.valueOf(mask);
					if (lastMask.contains("_")){
						playover = false;
					} else {
						playover = true;
					}
					sd.setLastMask(lastMask);
					sd.setScore(score+ deltaScore);
					score = sd.getScore();
					lastMask = sd.getLastMask();
                                        //System.out.println("DEBUG:2");
				} else {
					String ouchMessage = "Sorry! Try Again";
					sendResponse(sender, ouchMessage);
					if (!dd)
						sd.setScore(score - 1);
					else
						sd.setScore(score - 2);
					score = sd.getScore();
					playover = false;
				}
				ArenaCache.getInstance().saveData(sd);
                                //System.out.println("DEBUG:3");
				if (playover){
					//GO TO NEXT ROUND...
					String nextAlertMsg = "Great work "+fname+"! "+answer+" it is.";
					sendResponse(sender, nextAlertMsg);
					processInput("zeek next", sender);
					return "";
				}
			} else if (input.length() == answer.length()){
				input = input.toUpperCase();
				String maskedAnswer = lastMask.replaceAll(" ", "").replaceAll(">", "");
				int leftCharactes = 0;
				for (int i=0; i<maskedAnswer.length(); i++){
					if (maskedAnswer.charAt(i)=='_'){
						leftCharactes++;
					}
				}
				int deltaScore = 0;
				if (answer.equals(input)){
					deltaScore = 2*leftCharactes;
					String ddWow = "";
					if (dd){
						deltaScore = 2*deltaScore;
						ddWow = "You score 2X";
						
					}
					String wowMessage = "That was awesome! "+fname+" "+ddWow+" \n***Cheers!***";
					sendResponse(sender, wowMessage);
					sd.setLastMask(lastMask);
					sd.setScore(score+ deltaScore);
					score = sd.getScore();
					lastMask = sd.getLastMask();
					ArenaCache.getInstance().saveData(sd);
					processInput("zeek next", sender);
					return "";
				} else {
					deltaScore = -1*leftCharactes;
					if (dd){
						deltaScore = 2*deltaScore;
					}
					String ouchMessage = "Oops! thats gonna be negative!";
					sendResponse(sender, ouchMessage);
					sd.setLastMask(lastMask);
					sd.setScore(score+ deltaScore);
					score = sd.getScore();
					lastMask = sd.getLastMask();
					ArenaCache.getInstance().saveData(sd);
				}
			} else {
                                if (!input.contains("SCOREBOARD") && !input.contains("ABOUT GAME") && !input.contains("SHARE")){
				   String ignoredMsg = "FYI, we may ignore your previous input for this game.";
				   sendResponse(sender, ignoredMsg);
                                }
			}
		}
		
		//start displaying below...
		String displayQuestion = fname+", Your Score = "+score +
				"\nGuess a letter or the word" +
				"\nQuestion ["+cur_qid+"/15]: "+question+"\n";
		if (hint != null && hint.length()>1){
			displayQuestion+= "Hint: "+hint+"\n";
		}
		
		if (lastMask !=null && lastMask.length()>1){
			displayQuestion+= (lastMask +"\n");
		} else {
			lastMask = ArenaCache.getInstance().getMask(cur_qid);
			sd.setLastMask(lastMask);
			displayQuestion+= (lastMask +"\n");
		}
		
		responseTxt = displayQuestion;
		
		//sendResponse(sender, responseTxt);
		boolean showHint = true;
		if (sd.getHint()!=null && !sd.getHint().equals(""))
			showHint = false;
		
		boolean showDoubleDown = true;
		if (sd.isDoubleDown()){
			showDoubleDown = false;
		}
		sendButtonMenu(sender, responseTxt, showHint, showDoubleDown);
		return responseTxt;
	}


	private void sendResponse(String sender, String responseTxt) {
		Recipient recipient = new Recipient();
		recipient.setId(sender);
		Messaging reply = new Messaging();
		Message fbRes = new Message();
		fbRes.setText(responseTxt);
		reply.setRecipient(recipient);
		reply.setMessage(fbRes);
		String jsonReply = new Gson().toJson(reply);           
		System.out.println("JSON = "+jsonReply);
		try {
			HttpEntity entity  = new ByteArrayEntity(jsonReply.getBytes("UTF-8"));
			httppost.setHeader("Content-Type", "application/json");
			httppost.setEntity(entity);
			HttpResponse ack = client.execute(httppost);
			String result = EntityUtils.toString(ack.getEntity());
			System.out.println(result);
			
		}catch(Exception e){
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}

        private boolean findService(String text, final String sender){
           boolean ret = false;
           final String text2 = text.toLowerCase();

            if (text2.startsWith("profile")) {
               ret = true;
               //Async
               System.out.println("Sending Async FB Profile req");
               new Thread("fbprofile-th"){
             	  public void run(){
             		 FBProfile profile = requestFBProfile(sender);
             	  }
               }.start();
               System.out.println("FB Profile Async PASS");
            }
            if (text2.startsWith("speak")){
               ret = true;
               //Async
               System.out.println("Sending Async SPEAK msg");
               new Thread("zeektts-th"){
                  public void run(){
                        try{
                        String action = text2.substring(6);
                        String speakURL = "http://java-codeperformance.rhcloud.com/LaunchModule?target=speak&action="+URLEncoder.encode(action, "UTF-8");
                        System.out.println("sending "+speakURL);
                        URL obj = new URL(speakURL);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("GET");

			int responseCode = con.getResponseCode();

			BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
			}
			in.close();
			}catch(Exception e){
                                System.out.println(e.getMessage());
 				e.printStackTrace();
                        }	
                        sendResponse(sender, "action has been submitted");

                  }
               }.start();
               System.out.println("Async SPEAK sent!");
            } 
            if (text2.startsWith("hi")){ //HURRAY SOME ONE CAME IN!!!!
            	ret = true;
            	new Thread("knock-knock-thread"){
               	  public void run(){
               		 FBProfile profile = requestFBProfile(sender);
               		 boolean isExistingUser = ZeeKServices.getInstance().bot_checkExistingUser(sender);
               		 if (isExistingUser){
               			int user_id = ZeeKServices.getInstance().bot_getUserId(sender);
               			String reponseTxt = "Welcome back! : "+user_id;
                    	sendResponse(sender, reponseTxt);
               		 } else {
               			int user_id = ZeeKServices.getInstance().bot_addNewUser(profile, sender);
               			String reponseTxt = "Thank you for using Donna! Ur USER-ID = "+user_id;
                    	sendResponse(sender, reponseTxt);
               		 }
               		 
               		 //SENDING MENU
               		 List<String> _btnList = new ArrayList<String>();
               		 _btnList.add("ADD");
               		 _btnList.add("VIEW");
               		 _btnList.add("DELETE");
               		// sendButtonMenu(sender, _btnList);
               		 
               	  }
                 }.start();
            }

           return ret; 
        }

        
        
        private void sendButtonMenu(String sender, String text, boolean showHint, boolean showDD){
        	Recipient recipient = new Recipient();
    		recipient.setId(sender);
    		com.zeek.fbbot.pojo.btnmenu.Message msg = new com.zeek.fbbot.pojo.btnmenu.Message();
    		com.zeek.fbbot.pojo.btnmenu.Attachment attachment = new com.zeek.fbbot.pojo.btnmenu.Attachment();
    		attachment.setType("template");
    		Payload payload = new Payload();
    		payload.setTemplateType("button");
    		payload.setText(text);
    		Button hintBtn = null;
    		Button ddBtn= null;
    		Button aboutBtn= null;
    		if (showHint){
    			hintBtn = new Button();
    			hintBtn.setTitle("\ud83d\udca1HINT");
    			hintBtn.setType("postback");
    			hintBtn.setPayload("hint");
    		}
    		if (showDD){
    			ddBtn = new Button();
    			ddBtn.setTitle("\ud83d\udcb02xDOWN");
    			ddBtn.setType("postback");
    			ddBtn.setPayload("double down");
    		}
    		aboutBtn= new Button();
    		aboutBtn.setTitle("\ud83d\udca3SKIP");
    		aboutBtn.setType("postback");
    		aboutBtn.setPayload("skip");
    		
    		List<Button> buttons = new ArrayList<Button>();
    		if (hintBtn!=null)
    			buttons.add(hintBtn);
    		if (ddBtn!=null)
    			buttons.add(ddBtn);
    		buttons.add(aboutBtn);
    		
    		payload.setButtons(buttons);
    		attachment.setPayload(payload);
    		
            msg.setAttachment(attachment);
            
            QuickReply qrBtn = new QuickReply();
            qrBtn.setContentType("text");
            qrBtn.setPayload("about game");
            qrBtn.setTitle("\ud83c\udfae ABOUT GAME");
            
            QuickReply qrBtn2 = new QuickReply();
            qrBtn2.setContentType("text");
            qrBtn2.setPayload("scoreboard");
            qrBtn2.setTitle("\ud83c\udfc6SCOREBOARD");
            
            QuickReply qrBtn3 = new QuickReply();
            qrBtn3.setContentType("text");
            qrBtn3.setPayload("likeshare");
            qrBtn3.setTitle("\ud83d\udc98 LIKE/SHARE");  

            List<QuickReply> quickReplyButtons = new ArrayList<QuickReply>();
            quickReplyButtons.add(qrBtn);
            quickReplyButtons.add(qrBtn2);
            quickReplyButtons.add(qrBtn3);
            
            msg.setQuickReplies(quickReplyButtons);
 
    		FBButtonMenu  respMenu = new FBButtonMenu();
    		respMenu.setMessage(msg);
    		respMenu.setRecipient(recipient);
    		String jsonReply = new Gson().toJson(respMenu);
    		System.out.println("JSON = "+jsonReply);
    		try {
    			HttpEntity entity  = new ByteArrayEntity(jsonReply.getBytes("UTF-8"));
    			httppost.setHeader("Content-Type", "application/json");
    			httppost.setEntity(entity);
    			HttpResponse ack = client.execute(httppost);
    			String result = EntityUtils.toString(ack.getEntity());
    			System.out.println(result);
    			
    		}catch(Exception e){
    			System.out.println(e.getMessage());
    			e.printStackTrace();
    		}
        }

//This method is to send message to user with option to UNSUBSCRIBE. which is so bad !
private void sendNotification(String sender, String text){
    Recipient recipient = new Recipient();
    recipient.setId(sender);
    com.zeek.fbbot.pojo.btnmenu.Message msg = new com.zeek.fbbot.pojo.btnmenu.Message();
    com.zeek.fbbot.pojo.btnmenu.Attachment attachment = new com.zeek.fbbot.pojo.btnmenu.Attachment();
    attachment.setType("template");
    Payload payload = new Payload();
    payload.setTemplateType("button");
    payload.setText(text);
    
    Button unSubBtn = null;
    unSubBtn = new Button();
    unSubBtn.setTitle("UNSUBSCRIBE FOREVER!");
    unSubBtn.setType("postback");
    unSubBtn.setPayload("unsubscribe");
    
    Button playBtn = null;
    playBtn = new Button();
    playBtn.setTitle("PLAY NOW");
    playBtn.setType("postback");
    playBtn.setPayload("hi");

    List<Button> buttons = new ArrayList<Button>();
    buttons.add(unSubBtn);
    buttons.add(playBtn);

    payload.setButtons(buttons);
    attachment.setPayload(payload); 
    msg.setAttachment(attachment);

    FBButtonMenu  respMenu = new FBButtonMenu();
    respMenu.setMessage(msg);
    respMenu.setRecipient(recipient);
    String jsonReply = new Gson().toJson(respMenu);
    System.out.println("NOTIFICATION SENT | JSON = "+jsonReply);
    try {
     HttpEntity entity  = new ByteArrayEntity(jsonReply.getBytes("UTF-8"));
     httppost.setHeader("Content-Type", "application/json");
     httppost.setEntity(entity);
     HttpResponse ack = client.execute(httppost);
     String result = EntityUtils.toString(ack.getEntity());
     System.out.println("NOTIFICATION RESULT | JSON ="+result);
    }catch(Exception e){
     System.out.println("NOTIFICATION SEND ERROR : "+e.getMessage());
     e.printStackTrace();
    }
}
        
        
        private FBProfile requestFBProfile(String sender){
        	//retrieve FB profile from FB
        	String url = fbProfileFetchURL.replace("XXXX", sender);
        	HttpGet http_getFBProfile = new HttpGet(url);
        	System.out.println("Request TO FB = "+url);
        	FBProfile profile = null;
        	try {
        	HttpResponse ack = client.execute(http_getFBProfile);
			String result = EntityUtils.toString(ack.getEntity());
			System.out.println("Response from FB = "+result);
			profile = new Gson().fromJson(result, FBProfile.class );
        	}catch(Exception e){
    			System.out.println(e.getMessage());
    			e.printStackTrace();
    		}
        	String reponseTxt = "Hi "+profile.getFirstName()+"!";
        	sendResponse(sender, reponseTxt);
        	return profile;
        }        
}


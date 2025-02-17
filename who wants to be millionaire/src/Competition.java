import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;
import java.util.Random;
import java.util.Scanner;

import enigma.console.TextAttributes;
import enigma.console.TextWindowNotAvailableException;
import enigma.core.Enigma;
import enigma.console.TextAttributes;
import java.awt.Color;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Competition {
	
	File words,stops;
    public Question[] questionPool;
	public String [][] categories, wordCloud;
	public int [][] difficulties;
	public String [][] statisticsQuestions;
	public Participant[] people;
	public String[] dictionary;
	public String[] stop_words;
	public int[] choices;
	public int difficulty, questionCounter, tier, contestantCounter, time;
	boolean doubledip = true; // checks which lifeline has used.
	boolean fiftyfifty = true;
	boolean gameOver = false;
	boolean doubledipflag = true; // this flag controls user has just 2 option after used doubledip
	String ddtext = "Double Dip";
	String fftext = "50%";
	
	
	public static enigma.console.Console cn;
	public KeyListener klis; // listener of keyboard
    public int keypr;   // key pressed?
	public int rkey;    // key   (for press/release)
	
	public static TextAttributes blueColor=new TextAttributes(Color.blue,Color.black);
	public static TextAttributes greenColor=new TextAttributes(Color.green,Color.black);
	public static TextAttributes yellowColor=new TextAttributes(Color.yellow,Color.black);
	public static TextAttributes redColor=new TextAttributes(Color.red,Color.black);
	public static TextAttributes whiteColor=new TextAttributes(Color.white,Color.black);
	public static TextAttributes cyanColor=new TextAttributes(Color.cyan,Color.black);
	
	SimpleAudioPlayer playerMainTheme= new SimpleAudioPlayer("MainTheme.wav");
	SimpleAudioPlayer playerFinalAnswer= new SimpleAudioPlayer("final answer.wav");
	SimpleAudioPlayer playerCorrectAnswer= new SimpleAudioPlayer("correct answer.wav");
	SimpleAudioPlayer playerWrongAnswer= new SimpleAudioPlayer("wrong answer.wav");
	SimpleAudioPlayer playerBreak= new SimpleAudioPlayer("break.wav");
	
	public File file1 = new File("statisticsParticipants.txt");  
    public File file2 = new File("correctAnswers.txt");
    public File file3 = new File("wrongAnswers.txt");
    public File file4 = new File("answeredQuestions.txt");
    public File participantsFile;
   
	public Competition() throws Exception {
		
		playerFinalAnswer.pause();
		playerCorrectAnswer.pause();
		playerWrongAnswer.pause();
		playerBreak.pause();
		cn =Enigma.getConsole("Who Wants to Be a Millionaire", 120,35,13);
		words = new File("dictionary.txt");
		stops = new File("stop_words.txt");
		dictionary = new String[370103];
		stop_words = new String[851];
		wordCloud = new String[5][1000];
		people = new Participant[300];
		categories = new String[100][2];
		difficulties = new int[5][2];
		choices = new int[4];
		questionPool = new Question[300];
		difficulty = 1;
		contestantCounter = 0;
		questionCounter = 0;
		tier = 1;
	    
		
		//////// key listener code //////////////
	      klis=new KeyListener() {
	         public void keyTyped(KeyEvent e) {}
	         public void keyPressed(KeyEvent e) {
	            if(keypr == 0) {
	               keypr = 1;
	               rkey = e.getKeyCode();
	            }
	         }
	         public void keyReleased(KeyEvent e) {}
	      };
	      cn.getTextWindow().addKeyListener(klis);
	     /////////////// ////////////////////// /
	      

		fileOperations();
		start();
	}

	private void start() throws Exception {
		deleteScreen();
		cn.getTextWindow().setCursorPosition(0,0);

		menu();

	    deleteScreen();
	    playerBreak.restart("break.wav");
	    cn.getTextWindow().setCursorPosition(48, 17);
		cn.getTextWindow().output("Contestant:  " + people[contestantCounter].getName()); // new contestant			
		Thread.sleep(5500);
		
		while (true) {
		    deleteScreen();	
			playerBreak.pause();
			time = 0;
			String[] tempWordCloud = printWordCloud(); // selection of words for special word cloud
		    Question nextQuestion = findQuestion(tempWordCloud);/// beyond on this word cloud, it selects a new question with the user
		    keypr = 0;
			String str = "";
		    getChoices(nextQuestion);
			cn.getTextWindow().setCursorPosition(0, 15);
	 		nextQuestion.printQuestion(cn);
		    gameOver = false;
		    cn.getTextWindow().output(("\n Enter your choice (E:Exit): "));
	         while (true) {
	        	  Thread.sleep(50);
	        	  time += 50;
	        	  infoScreen();        	  
	        	  
	        	  cn.getTextWindow().setCursorPosition(29, 24);	 
	        	  if(time == 20000) {
	        		  gameOver = true;
	        		  break;
	        	  }
	        	  if (keypr == 1) {
	        		  char rckey=(char) (rkey + 32);

	        		  if ( str != null && rkey == KeyEvent.VK_BACK_SPACE) {
	        			  str = str.substring(0, str.length()-1);
	        			  cn.getTextWindow().output(str + "            ");
	        		  }
	        		  else if (rkey != KeyEvent.VK_ENTER) {
	        			  str += rckey;   
	        			  cn.getTextWindow().output(str);
		        	  }
	        		  else {	
	        			  cn.getTextWindow().output(29,24,' ');
	        			  if(str.toLowerCase().length() == 1 && ((str.toLowerCase().charAt(0) < 101 && str.toLowerCase().charAt(0) > 96))){
	        				  playerMainTheme.pause();
	        				  playerFinalAnswer.restart("final answer.wav");
	        				  Thread.sleep(tier*3500);
	        				  playerFinalAnswer.pause();
	        				  playerMainTheme.resumeAudio("MainTheme.wav");
	        			  }
	        			  if(str.equalsIgnoreCase(String.valueOf(nextQuestion.getRightChoice()))) 
	    	        	  {
	        				 playerCorrectAnswer.restart("correct answer.wav");
	        				 cn.getTextWindow().setCursorPosition(1, 18 - 96 + str.toLowerCase().charAt(0));
	    	        		 cn.getTextWindow().output(str.toUpperCase() + ") " + nextQuestion.getUserChoiceText(str.toLowerCase().charAt(0)), greenColor);
	    	        		 answeredQuestion(nextQuestion.getQuestionID(), people[contestantCounter].getParticipantID(), true);
	    	        		 correctAnswers(nextQuestion.getCategory());
	    	        		 people[contestantCounter].increaseMoney(difficulty);
	    	        		 
	    	        		 if (difficulty == 2) tier++;
	    	        		 else if(difficulty == 4) tier++;
	    	        		 else if (difficulty == 5) tier++;
	    	        		 difficulty++; 
	    	        		 Thread.sleep(2500);
	    	        		 playerCorrectAnswer.pause();
	    	        		 if (people[contestantCounter].getMoney() == 1000000) {
	    	        			 gameOver = true;
	    	        		 }
	    	        		 break;
	    	        	  }
	    	        	  else if((str.equalsIgnoreCase("j") || str.equalsIgnoreCase("joker")) && (doubledip == true || fiftyfifty == true))
	    					try {
	    						chooseJoker(nextQuestion);
	    						
	    					} catch (InterruptedException e) {
	    						e.printStackTrace();
	    					}
	    	        	  else if (str.toLowerCase().equals("e")) System.exit(0);
	    	        	  else if (str.toLowerCase().equals("r")) {
	    	        		  gameOver = true; 
	    	        		  break;
	    	        	  }
	    	        	  else {
	    	         		if (str.toLowerCase().length() == 1 && ((str.toLowerCase().charAt(0) < 101 && str.toLowerCase().charAt(0) > 96)))// a-b-c-d control 
	    	         		{ 
	    	         			playerWrongAnswer.restart("wrong answer.wav");
		        				 cn.getTextWindow().setCursorPosition(1, 18 - 96 + str.toLowerCase().charAt(0));
		    	        		 cn.getTextWindow().output(str.toUpperCase() + ") " + nextQuestion.getUserChoiceText(str.toLowerCase().charAt(0)), redColor);
	    	         			  if (doubledip == false && doubledipflag == true)
	    	         			 {
	    	         				 Thread.sleep(2000);
	    	         				 doubledipflag = false;
	    	         				 gameOver = false; 
	    	         				 playerWrongAnswer.pause();
	    	         			 }
	    	         			  else if (choices[str.toLowerCase().charAt(0) - 97] == 0 && fiftyfifty == false)	{
	    	         				 playerWrongAnswer.pause();
	    	         				  continue;
	    	         			  }
	    	         		   else 
	    	         			 {
	    	         				 answeredQuestion(nextQuestion.getQuestionID(), people[contestantCounter].getParticipantID(), false);
	    	         				 wrongAnswers(nextQuestion.getCategory());
	    	         				 people[contestantCounter].calculateMoney(tier);
	    	         				 Thread.sleep(2000);
	    	         			     gameOver = true;
	    	         			     playerWrongAnswer.pause();
	    	         			     break;
	    	         			     
	    	         			 }
	    	         			
	    	         	   }
	    	         	}
	        			  str = "";
	    	        	  cn.getTextWindow().output("                ");
	    	         }
	        		}
	        		 keypr = 0;
	        	 } 

	         deleteScreen();
	 		if (gameOver) {
	 			 statisticsParticipants(contestantCounter);
		 		 if (people[contestantCounter].getMoney() != 0) {
					 cn.getTextWindow().setCursorPosition(54,16);
		 			 cn.getTextWindow().output("congratulations!".toUpperCase(Locale.ENGLISH), yellowColor);
		 		 }
				cn.getTextWindow().setCursorPosition(54,17);
				cn.getTextWindow().output("You won: " + String.valueOf(people[contestantCounter].getMoney()));
	 		    Thread.sleep(2000);

				 difficulty = 1;
				 tier = 1;
				 doubledip = true;
				 fiftyfifty = true;
				 doubledipflag = true;
				 ddtext = "Double Dip";
				 fftext = "50%";
	 		    contestantCounter++;

	 		    cn.getTextWindow().setCursorPosition(50,18);
	 		    cn.getTextWindow().output("Next contestant (y/n):  ");
	 			if (gameOver() == false) break;
	 		}
	     }	 
				 
	}
	private boolean gameOver() throws Exception {
 		cn.getTextWindow().setCursorPosition(48,18);
 		cn.getTextWindow().output("Next contestant (y/n):              ");

 		cn.getTextWindow().setCursorPosition(71,18);
 		String userAnswer = cn.readLine();
 		if (userAnswer.toLowerCase().equals("n") || userAnswer.toLowerCase().equals("no")) {
 			start();	
 		}
 		else if (!(userAnswer.toLowerCase().equals("y") || userAnswer.toLowerCase().equals("yes"))) {
 			gameOver();
 		}
 		if (people[contestantCounter] == null) return false;
 		
 		deleteScreen();
	    playerBreak.restart("break.wav");
	    cn.getTextWindow().setCursorPosition(48, 17);
		cn.getTextWindow().output("Contestant:  " + people[contestantCounter].getName()); // new contestant			
		Thread.sleep(5500);	 
		playerBreak.pause();
		return true;
 		
	}

	private void getChoices(Question nextQuestion) {

		choices[0] = Integer.valueOf(nextQuestion.getChoice1().charAt(0));
		choices[1] = Integer.valueOf(nextQuestion.getChoice2().charAt(0));
		choices[2] = Integer.valueOf(nextQuestion.getChoice3().charAt(0));
		choices[3] = Integer.valueOf(nextQuestion.getChoice4().charAt(0));
	}

	private void fileOperations() {
		// file operations of dictionary and stop words
			
				try {
					Scanner in = new Scanner(stops);
					int counter = 0;
					while(in.hasNextLine()) {
						String line = in.nextLine();
						if (line != null) {
							stop_words[counter] = line;
							counter++;
						}
					}
					in.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				
				try {
					Scanner in = new Scanner(words);
					int counter = 0;
					while(in.hasNextLine()) {
						String line = in.nextLine();
						if (line != null) {
							dictionary[counter] = line;
							counter++;
						}
					}
					in.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
	}
	private void infoScreen() {
		
		cn.getTextWindow().setCursorPosition(82, 5);
		cn.getTextWindow().output("Money: " + people[contestantCounter].getMoney());
		cn.getTextWindow().setCursorPosition(82, 7);
		cn.getTextWindow().output("Remaining Time:           ");
		cn.getTextWindow().setCursorPosition(82, 7);
		cn.getTextWindow().output("Remaining Time: " + (20 - time/1000));
		cn.getTextWindow().setCursorPosition(82, 9);
		cn.getTextWindow().output(fftext);
		cn.getTextWindow().setCursorPosition(82, 11);
		cn.getTextWindow().output(ddtext);
		
		cn.getTextWindow().setCursorPosition(78, 3);
		cn.getTextWindow().output("-----------------------------------");
		for (int i = 4; i < 12; i++) {
			cn.getTextWindow().setCursorPosition(78, i);
			cn.getTextWindow().output("|");
			cn.getTextWindow().setCursorPosition(112, i);
			cn.getTextWindow().output("|");
			
		}
		cn.getTextWindow().setCursorPosition(78, 12);
		cn.getTextWindow().output("-----------------------------------");
		
	}
	private void chooseJoker(Question needquestion) throws InterruptedException {
		int px = 81 , py = 9;		// 	Coordinates where lifeliness are
		keypr = 0;
		
		while(true) {
			 Thread.sleep(50);
       	     time += 50;
     		cn.getTextWindow().setCursorPosition(82, 7);
    		cn.getTextWindow().output("Remaining Time:           ");
			cn.getTextWindow().setCursorPosition(82, 7);
			cn.getTextWindow().output("Remaining Time: " + (20 - time/1000));
			cn.getTextWindow().output(px,py,'>');
			
			
			 if(keypr==1) {    // selecting operation
	             if(rkey==KeyEvent.VK_UP && py == 11) { 
	            	 cn.getTextWindow().output(px,py,' ');
	            	 py -= 2;
	        	 }
	             else if(rkey==KeyEvent.VK_DOWN && py == 9 ) { 
	            	 cn.getTextWindow().output(px,py,' ');
	            	 py += 2;
	             }
	             else if(rkey==KeyEvent.VK_ENTER) {
	                if (py == 11 && doubledip == true) {
	                	//doubleDip();
	                	doubledip = false;
	                	ddtext = "-           ";	                	
	                	break;
	        	    }
	                else if (py == 9 && fiftyfifty == true) {
	                	fiftyFifty(needquestion); // parameter means which question is in screen
	                	fiftyfifty = false;
	                	fftext = "-           ";	               
	                	break;
	                }	  
	                
	              }
	             keypr=0; 
			 }
		}
   	 cn.getTextWindow().output(px,py,' ');
	}
	

	private void menu() throws IOException {
		 
		while(true) {
			keypr = 0;                                         
		 cn.getTextWindow().output("\n");                        
		 cn.getTextWindow().output(
				 "           _ __ ___   ___ _ __  _   _ \r\n"
		 		+ "          | '_ ` _ \\ / _ \\ '_ \\| | | |\r\n"
		 		+ "          | | | | | |  __/ | | | |_| |\r\n"
		 		+ "          |_| |_| |_|\\___|_| |_|\\__,_|\n", cyanColor);
		 cn.getTextWindow().output("\n");
		 cn.getTextWindow().output("            _____________________\n", cyanColor);   
		 cn.getTextWindow().output("           /                    /|\n", cyanColor);    
		 cn.getTextWindow().output("          /                    / |\n", cyanColor);
		 cn.getTextWindow().output("         /___________________ /  |\n", cyanColor);
		 cn.getTextWindow().output("         |______WELCOME______|   |\n", cyanColor);	
	     cn.getTextWindow().output("         |1.Load questions   |   |\n", cyanColor);
         cn.getTextWindow().output("         |___________________|   |\n", cyanColor);
		 cn.getTextWindow().output("         |2.Load participants|   |\n", cyanColor);
		 cn.getTextWindow().output("         |___________________|   |\n", cyanColor);
         cn.getTextWindow().output("         |3.Start competition|   |\n", cyanColor);
         cn.getTextWindow().output("         |___________________|   |\n", cyanColor);
		 cn.getTextWindow().output("         |4.Show statistics  |   |\n", cyanColor);
		 cn.getTextWindow().output("         |___________________|   |\n", cyanColor);
		 cn.getTextWindow().output("         |5.Tutorials        |   |\n", cyanColor);
		 cn.getTextWindow().output("         |___________________|  /\n", cyanColor);
		 cn.getTextWindow().output("         |6.Exit             | /\n", cyanColor);
		 cn.getTextWindow().output("         |___________________|/\n", cyanColor);
		 cn.getTextWindow().setCursorPosition(45, 8);
		 cn.getTextWindow().output("      ___           ___           ___           ___     ", blueColor);
		 cn.getTextWindow().setCursorPosition(45, 9);
		 cn.getTextWindow().output("     /\\__\\         /\\__\\         /\\  \\         /\\__\\    ", blueColor);
		 cn.getTextWindow().setCursorPosition(45, 10);
		 cn.getTextWindow().output("    /:/ _/_       /:/ _/_       /::\\  \\       /::|  |   ", blueColor);
		 cn.getTextWindow().setCursorPosition(45, 11);
		 cn.getTextWindow().output("   /:/ /\\__\\     /:/ /\\__\\     /:/\\:\\  \\     /:|:|  |   ", blueColor);
		 cn.getTextWindow().setCursorPosition(45, 12);
		 cn.getTextWindow().output("  /:/ /:/ _/_   /:/ /:/ _/_   /::\\~\\:\\__\\   /:/|:|__|__ ", blueColor);
		 cn.getTextWindow().setCursorPosition(45, 13);
		 cn.getTextWindow().output(" /:/_/:/ /\\__\\ /:/_/:/ /\\__\\ /:/\\:\\ \\:|__| /:/ |::::\\__\\", blueColor);
		 cn.getTextWindow().setCursorPosition(45, 14);
		 cn.getTextWindow().output(" \\:\\/:/ /:/  / \\:\\/:/ /:/  / \\:\\~\\:\\/:/  / \\/__/~~/:/  /", blueColor);
		 cn.getTextWindow().setCursorPosition(45, 15);
		 cn.getTextWindow().output("  \\::/_/:/  /   \\::/_/:/  /   \\:\\ \\::/  /        /:/  / ", blueColor);
		 cn.getTextWindow().setCursorPosition(45, 16);
		 cn.getTextWindow().output("   \\:\\/:/  /     \\:\\/:/  /     \\:\\/:/  /        /:/  /  ", blueColor);
		 cn.getTextWindow().setCursorPosition(45, 17);
		 cn.getTextWindow().output("    \\::/  /       \\::/  /       \\::/__/        /:/  /   ", blueColor);
		 cn.getTextWindow().setCursorPosition(45, 18);
		 cn.getTextWindow().output("     \\/__/         \\/__/         ~~            \\/__/    ", blueColor);
		 
		 
		 boolean run = false;
		 while (true) {
		 if(keypr==1) {    // when key pressed
             if(rkey==KeyEvent.VK_1 || rkey==KeyEvent.VK_NUMPAD1) {
            	  cn.getTextWindow().setCursorPosition(0, 24);
            	  cn.getTextWindow().output("\t\tEnter file name to load:                      ");
            	  cn.getTextWindow().setCursorPosition(32, 24);
            	  File questionsFile = new File(cn.readLine()); 

       			 deleteScreen();
           	     cn.getTextWindow().setCursorPosition(112, 0);
           	     cn.getTextWindow().output("← Back");
            	  loadQuestions(questionsFile);
            	  calculateQuestionData();
        	 }
             else if (rkey==KeyEvent.VK_BACK_SPACE) {
     			 deleteScreen();
     			 keypr = 0;
     			 break;
     			 
             }
             else if(rkey==KeyEvent.VK_2 || rkey==KeyEvent.VK_NUMPAD2) { 
            	 cn.getTextWindow().setCursorPosition(0, 24);
           	     cn.getTextWindow().output("\t\tEnter file name to load:                      ");
           	     cn.getTextWindow().setCursorPosition(32, 24);
            	 participantsFile = new File(cn.readLine()); 
            	 loadParticipants(participantsFile);
            	 cn.getTextWindow().setCursorPosition(0, 25);
                 cn.getTextWindow().output("\n\t\tThe file is loaded. ");

             }
             else if(rkey==KeyEvent.VK_3|| rkey==KeyEvent.VK_NUMPAD3) {
            	 if (questionPool[0] != null && people[0] != null)
            		 {run = true; 
            		  deleteScreen();// if questions and participants are loaded
            		 }
            	 else {
            		 cn.getTextWindow().setCursorPosition(0, 27);
            		 cn.getTextWindow().output("\t\tLoad questions and participants at first.");
            	 }
             }
             else if((rkey==KeyEvent.VK_4 || rkey==KeyEvent.VK_NUMPAD4)&& people[0] != null) {
            	 deleteScreen();
                 printStatistics();   
            	 
           	     cn.getTextWindow().setCursorPosition(112, 0);
           	     cn.getTextWindow().output("← Back");
             }
             else if(rkey==KeyEvent.VK_5 || rkey==KeyEvent.VK_NUMPAD5) {
            	 tutorials();
             }
             else if(rkey==KeyEvent.VK_6 || rkey==KeyEvent.VK_NUMPAD6) {
            	 System.exit(0);
                 
             }
             keypr=0; // reset keypr for next keyboard information
        	}	
		 
		 	if (run) break; // competition start
		 	
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		 }	
			if (run) break; // competition start
		}
} 
	
	private void tutorials() {
		 deleteScreen();
		 cn.getTextWindow().setCursorPosition(0, 1);
    	 cn.getTextWindow().output("        _______ _    _ _______ ____  _____  _____          _       _____ \r\n"
    	 		+ "       |__   __| |  | |__   __/ __ \\|  __ \\|_   _|   /\\   | |     / ____|\r\n"
    	 		+ "          | |  | |  | |  | | | |  | | |__) | | |    /  \\  | |    | (___  \r\n"
    	 		+ "          | |  | |  | |  | | | |  | |  _  /  | |   / /\\ \\ | |     \\___ \\ \r\n"
    	 		+ "          | |  | |__| |  | | | |__| | | \\ \\ _| |_ / ____ \\| |____ ____) |\r\n"
    	 		+ "          |_|   \\____/   |_|  \\____/|_|  \\_\\_____/_/    \\_\\______|_____/ \r\n"
    	 		+ "                                                                         \r\n", blueColor);
    	 cn.getTextWindow().setCursorPosition(6, 9);
   	     cn.getTextWindow().output("> In order to play the game, you must first download the question and contestant files by", greenColor);
   	     cn.getTextWindow().setCursorPosition(6, 10);
	     cn.getTextWindow().output("  clicking the 1 and 2 options from the menu.", greenColor);
	     cn.getTextWindow().setCursorPosition(6, 12);
	     cn.getTextWindow().output("> After uploading the necessary files, you can start the competition by pressing 3 from the menu.", greenColor);
	     cn.getTextWindow().setCursorPosition(6, 14);
	     cn.getTextWindow().output("> To answer the question, simply enter the option you think is correct and press enter.", greenColor);
	     cn.getTextWindow().setCursorPosition(6, 16);
	     cn.getTextWindow().output("> If you want to use a lifeline, you can select the desired joker from the right", greenColor);
	     cn.getTextWindow().setCursorPosition(6, 17);
	     cn.getTextWindow().output("  side by entering j and pressing enter.", greenColor);
	     cn.getTextWindow().setCursorPosition(6, 19);
	     cn.getTextWindow().output("> If you want to withdraw from the competition, you must enter r and press enter.", greenColor);
	     cn.getTextWindow().setCursorPosition(6, 21);
	     cn.getTextWindow().output("> After the game is over, you can continue with an another contestant or go back to the", greenColor);
	     cn.getTextWindow().setCursorPosition(6, 22);
	     cn.getTextWindow().output("  menu and review the stats.", greenColor);
	     cn.getTextWindow().setCursorPosition(6, 24);
	     cn.getTextWindow().output("  Have a good time!", cyanColor);

   	     cn.getTextWindow().setCursorPosition(112, 0);
   	     cn.getTextWindow().output("← Back ");
	}
    private void calculateQuestionData() {
    	
    	// just for print table of questions
    	
    	// calculating category data and print it

  	  cn.getTextWindow().output("\n\tCategory        The number of questions\n");
    	int counter= 0;
    	for (int i = 0; i < questionPool.length; i++) {
    		if (questionPool[i] == null) break;
    		boolean alreadyCounted = false;
    		for (int j = 0; j < categories.length; j++) {
    			if (questionPool[i].getCategory().equals(categories[j][0])) {
    				alreadyCounted = true;
    				break;
    			}
    		}
    		if (alreadyCounted) continue;
    		categories[counter][0] = questionPool[i].getCategory();
    		categories[counter][1] = "0";
    		
    		for (int j = 0; j < questionPool.length; j++) {
    			if (questionPool[j] == null) break;
    			if ( categories[counter][0].equals(questionPool[j].getCategory()))
    				categories[counter][1] = String.valueOf((Integer.parseInt(categories[counter][1])+1));
    		}

    		counter++;
    	}
    	
    	for (int i = 0; i < categories.length; i++) {
    		if (categories[i][0] == null) break;
    		cn.getTextWindow().output("\n\t" + categories[i][0]);
    		for (int j = 0; j < 20-categories[i][0].length(); j++)
    			cn.getTextWindow().output(" ");
    		
    		cn.getTextWindow().output("\t" + categories[i][1]);
    	}
    	
    	// calculating difficulty data and print it
    	 counter= 0;
      	  cn.getTextWindow().output("\n\n\tDifficulty level      The number of questions\n");
    	 
    	for (int i = 0; i < questionPool.length; i++) {
    		if (questionPool[i] == null) break;
    		boolean alreadyCounted = false;
    		for (int j = 0; j < difficulties.length; j++) {
    			if (questionPool[i].getDifficulty() == difficulties[j][0]) {
    				alreadyCounted = true;
    				break;
    			}
    		}
    		if (alreadyCounted) continue;
    		difficulties[counter][0] = questionPool[i].getDifficulty();
    		difficulties[counter][1] = 0;
    		
    		for (int j = 0; j < questionPool.length; j++) {
    			if (questionPool[j] == null) break;
    			if (difficulties[counter][0] == questionPool[j].getDifficulty())
    				difficulties[counter][1] = difficulties[counter][1]+1;
    		}

    		counter++;
    	}
    	   	
    	for (int i = 0; i < difficulties.length; i++) {
    		if (difficulties[i] == null) break;
    		cn.getTextWindow().output("\n\t" + difficulties[i][0]);
    		cn.getTextWindow().output("                      ");
    		cn.getTextWindow().output("\t" +difficulties[i][1] + "\n");
    	}
		 
    }
	private void loadQuestions(File questionsFile) {
		try {
			Scanner in = new Scanner(questionsFile);
			int counter = 0;
			while(in.hasNextLine()) {
				String line = in.nextLine();
				String[] values = line.split("#");
				String category = values[0];
				String text = values[1];
				String choice1 = values[2];
				String choice2 = values[3];
				String choice3 = values[4];
				String choice4 = values[5];
				char rightChoice = values[6].charAt(0);
				int difficulty = Integer.parseInt(values[7]);
				
				if (line != null) {
					questionPool[counter] = new Question(category, text, choice1, choice2, choice3, choice4, rightChoice, difficulty);
					counter++;
				}
			}

			in.close();
			questionCounter = counter;

		    spellCheck(); // spellCheck function
			buildWordCloud(); //  all questions split and a main word cloud is created
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	private void loadParticipants(File participantsFile) throws IOException {
		try {
			Scanner in = new Scanner(participantsFile);
			int counter = 0;
			while(in.hasNextLine()) {
				String line = in.nextLine();
				String[] values = line.split("#");
				String name = values[0];
				String phone = values[2];
				String[] addressData = values[3].split(";");
				String[] dateData = values[1].split("\\.");
				Address address = new Address(addressData[0],addressData[1],addressData[2],addressData[3],addressData[4]);	
				Date birthdate = new Date(Integer.parseInt(dateData[0]), Integer.parseInt(dateData[1]),Integer.parseInt(dateData[2]));
				if (line != null) {
					people[counter] = new Participant(name, birthdate, phone, address);					
					counter++;					
				}
			}
			in.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private void buildWordCloud() {
		int counter1 = 0, counter2 = 0, counter3 = 0, counter4 = 0, counter5 = 0; 
		for(int i = 0; i < questionPool.length; i++) {	
			if (questionPool[i] == null) continue;
			int difficulty = questionPool[i].getDifficulty();	
			String splitted[] = questionPool[i].getText().split("\\W+");   //to convert text to array without punctuation 
	
			for (int j = 0; j < splitted.length; j++) {

				if (!splitted[j].contains("_") && !isStopWord(splitted[j]) && !isExist(splitted[j], difficulty)) {     
					switch(difficulty){  // to keep words in order of difficulty
					case 1:
						wordCloud[difficulty-1][counter1] = splitted[j];
						counter1++;
						break;
					case 2:
						wordCloud[difficulty-1][counter2] = splitted[j];
						counter2++;
						break;
					case 3:	
						wordCloud[difficulty-1][counter3] = splitted[j];
						counter3++;
						break;
					case 4:	
						wordCloud[difficulty-1][counter4] = splitted[j];
						counter4++;
						break;
					case 5:	
						wordCloud[difficulty-1][counter5] = splitted[j];
						counter5++;
						break;
				}
			}
		}
		}
	}
	private String[] printWordCloud() {
		String[] tempWordCloud = new String[15];  //this array holds the words which is in the wordCloud.
		Random rnd = new Random();
		int counter = 0, size = 0;
		for (int i = 0; i < wordCloud[0].length; i++) {
			if (wordCloud[difficulty-1][i] != null) {    //it calculates how full the array is.
				size++;
			}
			else break;
		}
		System.out.println("   \n");
		cn.getTextWindow().setCursorPosition(2, 3);
		cn.getTextWindow().output("-------------------------------------------------------------------------");
		for (int i = 4; i < 14; i++) {
			cn.getTextWindow().setCursorPosition(1, i);
			cn.getTextWindow().output("|");
			cn.getTextWindow().setCursorPosition(74, i);
			cn.getTextWindow().output("|");
			
		}
		cn.getTextWindow().setCursorPosition(2, 14);
		cn.getTextWindow().output("-------------------------------------------------------------------------");
		

		cn.getTextWindow().setCursorPosition(4, 6);
		int placecounter = 8;
		while(true) {
			int number = rnd.nextInt(size);   //it assigns a random number.
			boolean flag = false;
			for (int i = 0; i < tempWordCloud.length; i++) {
				if (tempWordCloud[i] != null && tempWordCloud[i].equals(wordCloud[difficulty-1][number])) {  
					//it prevents typing the same word and spaces in tempWordCloud.
					flag = true;
					break;
				}
			}

			if (flag) continue;  
			if (wordCloud[difficulty-1][number] != null) {
				System.out.print("      " + wordCloud[difficulty-1][number]);
				tempWordCloud[counter] = wordCloud[difficulty-1][number];   //to assign word to tempWordCloud 
				counter++;  
			}

			if (wordCloud[difficulty-1][counter] == null) break;
			if (counter % 4 == 0) {
				for(int i = 2; i <= 73; i++){
					for(int j = 4; j <= 13; j++) {
						if(j == 6 || j == 8 || j == 10 ) {
							cn.getTextWindow().setCursorPosition(72, j);
							System.out.print(" ");
						}
						else {
							cn.getTextWindow().setCursorPosition(i, j);
							System.out.print(" ");
						}
					}
					System.out.println();
				}
				cn.getTextWindow().setCursorPosition(2, placecounter);
				placecounter += 2; 
			}
			if (counter == 15) break;
		}
		return tempWordCloud;
	}
	private Question findQuestion(String[] tempWordCloud) {

		while (true) {
			cn.getTextWindow().setCursorPosition(1, 15);
			cn.getTextWindow().output("> Enter your selection:                       ");
			cn.getTextWindow().setCursorPosition(26, 15);
			String requestedWord = cn.readLine();
			cn.getTextWindow().setCursorPosition(1, 16);
			cn.getTextWindow().output("                                   ");
			for (int i = 0; i < tempWordCloud.length; i++) {
				if (tempWordCloud[i] != null && tempWordCloud[i].equalsIgnoreCase(requestedWord)) {
					for (int j = 0; j < questionPool.length; j++) {
						if(questionPool[j] == null) break;
						if (questionPool[j].getDifficulty() == difficulty && questionPool[j].getText().toLowerCase().contains(requestedWord.toLowerCase())) {
							return questionPool[j];	//a tier based question found which contains user word
						}
					}
				}
			}
			// the loop turn until a valid word be entered
			cn.getTextWindow().setCursorPosition(1, 16);
			cn.getTextWindow().output("> Please enter a valid word."); 
		}
	}
	
		
	private boolean isExist(String word,int difficulty) {
		boolean isExist = false;
        for(int j = 0; j < wordCloud[difficulty-1].length; j++) {
            if(wordCloud[difficulty-1][j] != null && word.equalsIgnoreCase(wordCloud[difficulty-1][j])) {
            	//prevents the same words and spaces from being written on the screen.
            	isExist =  true;
            	break;
            }
        }
        return isExist;
	}
	private boolean isStopWord(String word) {
		boolean isStopWord = false;
        for(int j = 0; j < stop_words.length; j++) {
            if(word.equalsIgnoreCase(stop_words[j])) {  //prevents stop words from being written on the screen.
            	isStopWord = true;
            	break;
            }
        }
        return isStopWord;       
	}
	private boolean isInDictionary(String word) {
		boolean isInDictionary = false;
        for(int j = 0; j < dictionary.length; j++) {
            if(word.equalsIgnoreCase(dictionary[j])) {
            	isInDictionary = true;
            	break;
            }
        }
        return isInDictionary;       
	}
	private void spellCheck() {
			boolean spellingError = false;
			for (int i = 0; i < questionCounter; i++) {
			String[] questionTextWords = questionPool[i].getText().split("\\W+"); // deleting punctuation marks of text words
			String newText =""; // for correct version of question text
			for (int j = 0; j < questionTextWords.length; j++) {
				if(questionTextWords[j] != "" && !questionTextWords[j].contains("_") && !isStopWord(questionTextWords[j]) && !isInDictionary(questionTextWords[j])) {
					int indexOfWord = questionPool[i].getText().indexOf(questionTextWords[j]); // keeping index of the word in the text
					
					String rightWord = spellingError(questionTextWords[j].toLowerCase()); // controlling spelling error
					if (!rightWord.equals(questionTextWords[j].toLowerCase())) { // if its changed new suggestion will be added
						if(indexOfWord == 0) rightWord = rightWord.substring(0, 1).toUpperCase() +  rightWord.substring(1); // if the word is at the beginning
						newText =  questionPool[i].getText().substring(0, indexOfWord)+ rightWord +
								questionPool[i].getText().substring(indexOfWord+ rightWord.length());
						questionPool[i].setText(newText);
						spellingError = true;
					}
						
					if (!spellingError) // if there is no spelling error, it checks if any reversed word exist
						rightWord = reversedWord(questionTextWords[j].toLowerCase()); // controlling reversed word in the function
						if (!rightWord.equals(questionTextWords[j].toLowerCase())) { // // if its changed new suggestion will be added
							if(indexOfWord == 0) rightWord = rightWord.substring(0, 1).toUpperCase() +  rightWord.substring(1);
							 newText =  questionPool[i].getText().substring(0, indexOfWord)+ rightWord +
									 questionPool[i].getText().substring(indexOfWord+ rightWord.length());
					      questionPool[i].setText(newText);
						}
				}				
			}	
			}
			
	}

	private String reversedWord(String word) {
		for (int i = 0; i < dictionary.length; i++) {
			if (dictionary[i] == null) break;
			else if (dictionary[i].equals(word)) break; // if the word is already in the dictionary
			else if(dictionary[i].length() != word.length()) continue; // control only same size words 

			int sameCharCounter = 0;
			for (int j = 0; j < dictionary[i].length(); j++) {
				if (dictionary[i].charAt(j) == word.charAt(j)) sameCharCounter++; 
			}
			
			if (sameCharCounter == word.length()-2) { // all the same except 1 character
				
				for (int j = 0; j < dictionary[i].length()-1; j++) { // control of two swapped chars
					if (dictionary[i].charAt(j) != word.charAt(j) && dictionary[i].charAt(j+1) == word.charAt(j) 
							&&	dictionary[i].charAt(j) == word.charAt(j+1)) {
							word = dictionary[i];
					break;
					}
				}
			}
		}
		return word;
		
	}

	private String spellingError(String word) {
		
		// number of same char control 
		for (int i = 0; i < dictionary.length; i++) {
			if (dictionary[i] == null) break; 
			else if (dictionary[i].equals(word)) break; // if the word is already in the dictionary
			else if(dictionary[i].length() != word.length()) continue; // control only same size words 

			int sameCharCounter = 0;
			for (int j = 0; j < dictionary[i].length(); j++) {
				if (dictionary[i].charAt(j) == word.charAt(j)) sameCharCounter++; 
			}
			
			if (sameCharCounter == word.length()-1) { // all the same except 1 character
				word = dictionary[i];
				break;
			}
		}
		return word;
	}
	
	private void fiftyFifty(Question question) // used this parameter for gain which question information
	{
	
		Random random = new Random();
		char rightchoice = question.getRightChoice();  // according to the right choice of question choosing 2 number beetween 1 and 4
		int rnumber1 = 0;
		int rnumber2 = 0;
		
		if (rightchoice == 'A') 
		{
			while(true) {
			
			 rnumber1 = random.nextInt(4 - 1 + 1) + 1;  
			 rnumber2 = random.nextInt(4 - 1 + 1) + 1;
			if(rnumber1 != rnumber2 && rnumber1 != 1 && rnumber2 != 1) //if the chosen number is equal to the right answer system choose 2 number again
				break;
			}

		}
		else if (rightchoice == 'B') 
		{
			
			while(true) {
			 
				rnumber1 = random.nextInt(4 - 1 + 1) + 1;
				rnumber2 = random.nextInt(4 - 1 + 1) + 1;
			if(rnumber1 != rnumber2 && rnumber1 != 2 && rnumber2 != 2)
				break;
			}
			
		}
		else if (rightchoice == 'C') 
		{
			
			while(true) {
			 rnumber1 = random.nextInt(4 - 1 + 1) + 1;
			 rnumber2 = random.nextInt(4 - 1 + 1) + 1;
			if(rnumber1 != rnumber2 && rnumber1 != 3 && rnumber2 != 3)
				break;
			}
			
		}
		else if (rightchoice == 'D') 
		{
			
			while(true) {
				rnumber1 = random.nextInt(4 - 1 + 1) + 1;
				rnumber2 = random.nextInt(4 - 1 + 1) + 1;
			if(rnumber1 != rnumber2 && (rnumber1 != 4 && rnumber2 != 4))
				break;
			}
		}
		choiceDeleter(rnumber2); 
		choiceDeleter(rnumber1);
		
	}
	private void choiceDeleter(int rnumber) {
		
		cn.getTextWindow().setCursorPosition(3, 18 + rnumber);
		cn.getTextWindow().output("                  ");
		choices[rnumber-1] = 0;
		
		
	}
	
    private void statisticsParticipants(int counter) throws IOException {
			if (!file1.exists()) {    
	            file1.createNewFile();   
	        }
	        // Here we open a new file and store the contestant's name, age and city.
			String name = people[counter].getName();
			int birthYear = people[counter].getBirthdate().getYear();
			String city = people[counter].getAdress().getCity();
			int age = 2022 - birthYear;
			
			String newline = System.lineSeparator();
	        FileWriter fileWriter = new FileWriter(file1, true);         // Here it checks whether there is a previously opened file with the same name.
	        BufferedWriter bWriter = new BufferedWriter(fileWriter);    // İf there is a file before, it does not open a new file and overwrites this file.
	        bWriter.write(name + "#" + city + "#" + age + newline);
	        bWriter.close();   
		}
    
    private void answeredQuestion(int questionID, int contestantID, boolean isAnsweredTrue) throws IOException {
    	 if (!file4.exists()) {
             file4.createNewFile();  
        }
	    // Here we open a file to hold the question ID, participant ID and correctness status of the questions.
	    String newline = System.lineSeparator();
        FileWriter fileWriter = new FileWriter(file4, true);
        BufferedWriter bWriter = new BufferedWriter(fileWriter);
    
        bWriter.write(questionID + "#" + contestantID + "#" + isAnsweredTrue + newline);
        bWriter.close();	 	
    }    
    private String mostSuccessfulContestant() {
        String[][] contestant = new String[1000][2];
        try {
            Scanner in = new Scanner(file4);  
            String[] tempContestant = new String[3];
            while(in.hasNextLine()) { 
                String line = in.nextLine();
                if (line != null) {  // We pull the data from the file in which we have assigned the Participant IDs and throw them into an array.
                    tempContestant = line.split("#");
                    contestant[Integer.parseInt(tempContestant[1]) - 1][0] = people[Integer.parseInt(tempContestant[1]) - 1].getName();
                    String contTrueNumber = contestant[Integer.parseInt(tempContestant[1]) - 1][1];
                    if (contTrueNumber==null) contTrueNumber= "0";
                    if (tempContestant[2].equals("true")) {  
                        contestant[Integer.parseInt(tempContestant[1]) - 1][1] = String.valueOf(Integer.parseInt(contTrueNumber) + 1);
                    } 
                }
            }
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
            int max = 0;
            int numberName = 0;
            String maxName ="";

            for (int i = 0; i < contestant.length; i++) {
                if (contestant[i][1] == null) {
                    break;
                }
                // By counting the correct numbers, we find which competitor is the most successful.
                String name= contestant[i][0];
                numberName = Integer.parseInt(contestant[i][1]);
                if (numberName > max) {
                    max = numberName;
                    maxName = name;
                }
            } 
            return maxName;
     }
    private void ageStatistics() {
        String[] contestant = new String[3];
        double ageUnder18 = 0;
        double ageUnder30 = 0;
        double ageUnder50 = 0;
        double ageOver50 = 0;
        double totalCorrect;
        int age = 0;
        try {  // Here, we calculated the ages of the contestants who answered correctly and created the age statistics.
            Scanner in = new Scanner(file4);
            while(in.hasNextLine()) {
                String line = in.nextLine();
                if (line != null) {
                    contestant = line.split("#");
                    age = 2022 - (people[Integer.valueOf(contestant[1]) - 1].getBirthdate().getYear());
                    if (contestant[2] != "false") {
                        if (age < 18)
                            ageUnder18++;
                        else if (age >= 18 && age < 30)
                            ageUnder30++;
                        else if (age >= 30 && age < 50)
                            ageUnder50++;
                        else
                            ageOver50++;
                    }
                }
            }
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        totalCorrect = ageUnder18 + ageUnder30 + ageUnder50 + ageOver50;
        String newline = System.lineSeparator();
        cn.getTextWindow().output("                                 AGE STATISTICS  " + newline
                                   + " Age < 18  --> % " + (int) ((ageUnder18 / totalCorrect) * 100) + newline
                                   + " 18 <= Age < 30  --> % " + (int) ((ageUnder30 / totalCorrect) * 100) + newline
                                   + " 30 <= Age < 50  --> % " + (int) ((ageUnder50 / totalCorrect) * 100) + newline
                                   + " Age >= 50  --> % " + (int) ((ageOver50 / totalCorrect) * 100));
    }
  
	private void correctAnswers(String category) throws IOException {
		    if (!file2.exists()) {
                 file2.createNewFile();  
            }
		    
		    String newline = System.lineSeparator();
            FileWriter fileWriter = new FileWriter(file2, true);
            BufferedWriter bWriter = new BufferedWriter(fileWriter);
            bWriter.write(category + newline);
            bWriter.close();		
		}    // We opened 2 category files according to the correct and incorrect answers.
	private void wrongAnswers(String category) throws IOException {
		    if (!file3.exists()) {
                file3.createNewFile();  
            }
	    
	        String newline = System.lineSeparator();
            FileWriter fileWriter = new FileWriter(file3, true);
            BufferedWriter bWriter = new BufferedWriter(fileWriter);
            bWriter.write(category + newline);
            bWriter.close();		
	    }   
		
	private void printStatistics() throws TextWindowNotAvailableException, IOException {  
		// Statistics printing function.
	    String newline = System.lineSeparator();
		cn.getTextWindow().output("                             STATISTICS         " + newline
				                  + " The most successful contestant is " + mostSuccessfulContestant() + newline
				                  + " The category with the most correctly answered is " + correctAnswersCalculator() + newline
				                  + " The category with the most badly answered is " + wrongAnswersCalculator() + newline
				                  + " The city with the highest number of participants is " + cityCalculator() + newline);
		cn.getTextWindow().setCursorPosition(0, 6);
		ageStatistics();
	}
	
	
	private String cityCalculator() {
		String[] city = new String[10000];
		try {   // We calculated the city with the highest number of participants by pulling the data from the file where we put the competitor statistics.
			Scanner in = new Scanner(file1);
			int counter = 0;
			String[] tempCity;
			while(in.hasNextLine()) {
				String line = in.nextLine();
				if (line != null) {
					tempCity = line.split("#");
					city[counter] = tempCity[1];
					counter++;
				}
			}
			in.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		    int max = 0;
            int numberCity = 0;   
            String tempCity = "";   
            String maxCity = "";
            
            for (int i = 0; i < city.length; i++) {
            	if (city[i] == null) {
		    		break;
		    	}
            	if (tempCity != city[i]) {
            		tempCity = city[i];
                	for (int j = 0; j < city.length; j++) {
                		if (city[j] == tempCity) 
                			numberCity++;                  		           		
                	}
                	if (numberCity > max) {
                		max = numberCity;
                		maxCity = tempCity;
                	}			
            	}       	
            } 
		    return maxCity;
	}
	
	private String correctAnswersCalculator() {
		String[] category = new String[10000];
		try {  // Here we calculate which category has more correct answers.
			Scanner in = new Scanner(file2);
			int counter = 0;
			while(in.hasNextLine()) {
				String line = in.nextLine();
				if (line != null) {
					category[counter] = line;
					counter++;
				}
			}
			in.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		    int max = 0;
            int count = 1;   
            String word = category[0];   
            String curr = category[0];
		    
		    for (int j = 0; j < category.length; j++) {
		    	if (category[j] == null) {
		    		break;
		    	}
		    	if(curr.equals(category[j])){
		             count++;
		         }
		         else {
		             count = 1;
		             curr = category[j];
		         }
		         if(max < count) {
		             max = count;
		             word = category[j];
		         }
		    }
		    return word;
		
	}
		   
	private String wrongAnswersCalculator() {
		String[] category = new String[10000];
		try {  // Here we calculate which category has more wrong answers.
			Scanner in = new Scanner(file3);
			int counter = 0;
			while(in.hasNextLine()) {
				String line = in.nextLine();
				if (line != null) {
					category[counter] = line;
					counter++;
				}
			}
			in.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		    int max = 0;
            int count = 1;   
            String word = category[0];   
            String curr = category[0];
		    
		    for (int j = 0; j < category.length; j++) {
		    	if (category[j] == null) {
		    		break;
		    	}
		    	if(curr.equals(category[j])){
		             count++;
		         }
		         else {
		             count = 1;
		             curr = category[j];
		         }
		         if(max < count) {
		             max = count;
		             word = category[j];
		         }
		    }
		    return word;
		}
	
	private void deleteScreen() {
		for (int i = 0; i < 120; i++) {
			for (int j = 0; j < 35; j++) {
				cn.getTextWindow().output(i,j, ' ');
			}
		}
		cn.getTextWindow().setCursorPosition(6, 0);
	}
}

public class Question {
    private String category, text, choice1, choice2, choice3, choice4;
    private char rightChoice;
    private int difficulty;
    public static int questionCounter = 0;
    private int questionID;
    
    public Question(String category, String text, String choice1, String choice2, String choice3, String choice4,
            char rightChoice, int difficulty) {
        this.category = category;
        this.text = text;
        this.choice1 = choice1;
        this.choice2 = choice2;
        this.choice3 = choice3;
        this.choice4 = choice4;
        this.rightChoice = rightChoice;
        this.difficulty = difficulty;
        questionCounter++;
        questionID = questionCounter;
    }

	public String getUserChoiceText(char userChoice) {
    	if (userChoice == 'a') return choice1;
    	else if (userChoice == 'b') return choice2;
    	else if (userChoice == 'c') return choice3;
    	else return choice4;
       	
    }
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    
    public static int getQuestionCounter() {
        return questionCounter;
    }

    public static void setQuestionCounter(int questionCounter) {
        Question.questionCounter = questionCounter;
    }

    public int getQuestionID() {
        return questionID;
    }

    public void setQuestionID(int questionID) {
        this.questionID = questionID;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getChoice1() {
        return choice1;
    }

    public void setChoice1(String choice1) {
        this.choice1 = choice1;
    }

    public String getChoice2() {
        return choice2;
    }

    public void setChoice2(String choice2) {
        this.choice2 = choice2;
    }

    public String getChoice3() {
        return choice3;
    }

    public void setChoice3(String choice3) {
        this.choice3 = choice3;
    }

    public String getChoice4() {
        return choice4;
    }

    public void setChoice4(String choice4) {
        this.choice4 = choice4;
    }

    public char getRightChoice() {
        return rightChoice;
    }

    public void setRightChoice(char rightChoice) {
        this.rightChoice = rightChoice;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }
    public void printQuestion(enigma.console.Console cn) {

        cn.getTextWindow().output("\n\n " + text);
        cn.getTextWindow().output("\n\n A) " + choice1);
        cn.getTextWindow().output("\n B) " + choice2);
        cn.getTextWindow().output("\n C) " + choice3);
        cn.getTextWindow().output("\n D) " + choice4 + "\n");
        
    }
    
}


public class Participant {
    private String name;
    private Date birthdate;
    private String phone;
    private Address adress;
    private int money;
    public static int participantCounter = 0;
    private int participantID;

    public Participant(String name, Date birthdate, String phone, Address adress) {
        this.name = name;
        this.birthdate = birthdate;
        this.phone = phone;
        this.adress = adress;
        money = 0;
        participantCounter++;
        participantID = participantCounter;
    }

    public static int getParticipantCounter() {
        return participantCounter;
    }

    public static void setParticipantCounter(int participantCounter) {
        Participant.participantCounter = participantCounter;
    }

    public int getParticipantID() {
        return participantID;
    }

    public void setParticipantID(int participantID) {
        this.participantID = participantID;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public Date getBirthdate() {
        return birthdate;
    }


    public void setBirthdate(Date birthdate) {
        this.birthdate = birthdate;
    }


    public String getPhone() {
        return phone;
    }


    public void setPhone(String phone) {
        this.phone = phone;
    }


    public Address getAdress() {
        return adress;
    }


    public void setAdress(Address adress) {
        this.adress = adress;
    }

    public void increaseMoney(int numberOfQuestion) {
        if (numberOfQuestion == 1) money += 20000;
        else if (numberOfQuestion == 2) money += 80000;
        else if (numberOfQuestion == 3) money += 150000;
        else if (numberOfQuestion == 4) money += 250000;
        else money += 500000;

    }
    public void calculateMoney(int tier) {
        if (tier == 1) money = 0;
        else if (tier == 2) money = 100000;
        else money = 1000000;

    }
    public int getMoney() {
        return money;
    }



}
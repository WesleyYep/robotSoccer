package strategy;

import bot.Robot;
import bot.Robots;

/**
 * Created by Wesley on 21/01/2015.
 */
public class Role {
    private String roleName;
    private CriteriaActionPair[] pairs = {null, null, null, null, null};
    private Robot bot;
    private Robots teamRobots;
    private Robots opponentRobots;
    private double ballX;
    private double ballY;
    private double predictedBallX;
    private double predictedBallY;
    private boolean isSetPlayRole = false;

    public void setRoleName(String value) {
        this.roleName = value;
    }

    public void setPair(Criteria criteria, Action action, int index) {
        if (pairs[index] == null) {
            pairs[index] = new CriteriaActionPair(criteria, action);
        } else {
            pairs[index].setAction(action);
            pairs[index].setCriteria(criteria);
        }
    }

    public Action[] getActions() {
        Action[] actions = {null, null, null, null, null};
        for (int i = 0; i < pairs.length; i++) {
            if (pairs[i] == null) {
                actions[i] = null;
            } else {
                actions[i] = pairs[i].getAction();
            }
        }
        return actions;
    }

    public Criteria[] getCriterias() {
        Criteria[] criterias = {null, null, null, null, null};
        for (int i = 0; i < pairs.length; i++) {
            if (pairs[i] == null) {
                criterias[i] = null;
            } else {
                criterias[i] = pairs[i].getCriteria();
            }
        }
        return criterias;
    }

    public void execute() {
        for (CriteriaActionPair cap : pairs) {
            if (cap == null) { continue; }
            Criteria c = cap.getCriteria();
            if (c == null) { continue; }
            c.addRobot(bot);
            c.setBallPosition(ballX, ballY);
            if (cap.getCriteria().isMet()) {
                Action a = cap.getAction();
                a.addRobot(bot);
                a.addTeamRobots(teamRobots);
                a.addOpponentRobots(opponentRobots);
                a.setBallPosition(ballX, ballY);
                a.setPredBallPosition(predictedBallX, predictedBallY);
                a.execute();
                break; //only have one action at a time!
            }
        }
    }

    protected double squared (double x) {
        return x * x;
    }

    public void addRobot (Robot bot) {
        this.bot = bot;
    }
    
    public void addTeamRobots (Robots team) {
    	this.teamRobots = team;
    }

    public void addOpponentRobots (Robots opponent) {this.opponentRobots = opponent;}

    public void setBallPosition(double x, double y) {
        this.ballX = x;
        this.ballY = y;
    }
    
    public void setPredictedPosition(double x, double y) {
    	this.predictedBallX = x;
    	this.predictedBallY = y;
    }

    @Override
    public String toString() {
        return roleName;
    }


    public boolean isSetPlayRole() {
        return isSetPlayRole;
    }

    public void setIsSetPlayRole(boolean isSetPlayRole) {
        this.isSetPlayRole = isSetPlayRole;
    }
}

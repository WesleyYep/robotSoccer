package strategy;

import bot.Robot;
import bot.Robots;
import data.CriteriaActionTableModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Wesley on 21/01/2015.
 */
public class Role {
    private String roleName;
    private Robot bot;
    private boolean isSetPlayRole = false;
    private List<Action> actionsList = new ArrayList<Action>();

    public void setRoleName(String value) {
        this.roleName = value;
    }

    public void addAction(Action action) {
        actionsList.add(action);
    }

    public void removeAction(Action action) {
        actionsList.remove(action);
    }

//    public void setPair(Criteria criteria, Action action, int index) {
//        if (pairs[index] == null) {
//            pairs[index] = new CriteriaActionPair(criteria, action);
//        } else {
//            pairs[index].setAction(action);
//            pairs[index].setCriteria(criteria);
//        }
//    }
//
//    public Action[] getActions() {
//        Action[] actions = new Action[pairs.length];
//        for (int i = 0; i < pairs.length; i++) {
//            if (pairs[i] == null) {
//                actions[i] = null;
//            } else {
//                actions[i] = pairs[i].getAction();
//            }
//        }
//        return actions;
//    }
//
//    public Criteria[] getCriterias() {
//        Criteria[] criterias = new Criteria[pairs.length];
//        for (int i = 0; i < pairs.length; i++) {
//            if (pairs[i] == null) {
//                criterias[i] = null;
//            } else {
//                criterias[i] = pairs[i].getCriteria();
//            }
//        }
//        return criterias;
//    }

    public void execute() {
        for(Action action : actionsList) {
            action.execute();
        }
//        for (CriteriaActionPair cap : pairs) {
//            if (cap == null) { continue; }
//            Criteria c = cap.getCriteria();
//            if (c == null) { continue; }
//            c.addRobot(bot);
//            c.setBallPosition(ballX, ballY);
//            if (cap.getCriteria().isMet()) {
//                Action a = cap.getAction();
//                a.addRobot(bot);
//                a.addTeamRobots(teamRobots);
//                a.addOpponentRobots(opponentRobots);
//                a.setBallPosition(ballX, ballY);
//                a.setPredBallPosition(predictedBallX, predictedBallY);
//                a.execute();
//                break; //only have one action at a time!
//            }
//        }
    }

    public void addRobot (Robot bot) {
        this.bot = bot;

        // Update robots in each associated action
        for(Action action : actionsList) {
            action.addRobot(bot);
        }
    }
    
//    public void addTeamRobots (Robots team) {
//    	this.teamRobots = team;
//    }
//
//    public void addOpponentRobots (Robots opponent) {this.opponentRobots = opponent;}

//    public void setBallPosition(double x, double y) {
//        this.ballX = x;
//        this.ballY = y;
//    }
//
//    public void setPredictedPosition(double x, double y) {
//    	this.predictedBallX = x;
//    	this.predictedBallY = y;
//    }

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

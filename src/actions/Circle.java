package actions;

import data.Coordinate;
import strategy.Action;

/**
 * Created by chan743 on 5/07/2015.
 */
public class Circle extends Action {

    private double radius = 80;
    private Coordinate centre = new Coordinate(110,90);
    private double t = 0;

    {
        if(!(parameters.containsKey("t"))) {
            //don't bother if already exists
            parameters.put("t", 0);
        }
    }

    @Override
    public void execute() {
        double tToUse = t + parameters.get("t")/100;
        Coordinate spot = new Coordinate(centre.x + radius * Math.cos(tToUse), centre.y + radius*Math.sin(tToUse));
        MoveToSpot.move(bot, spot, 0.8, true);
        t+=0.02;
 //       System.out.println("index: " + bot.getId() + "   - t: " + t + "   -   x: " + spot.x + "   -   y: " + spot.y);
    }


}

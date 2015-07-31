package actions;

import bot.Robot;
import net.sourceforge.jFuzzyLogic.FunctionBlock;
import org.opencv.core.Point;
import strategy.Action;

import java.util.ArrayList;

public class MoveToFacing  extends Action {

    private double error = 2.5;
    private boolean reachFirstSpot = false;
    private boolean reachFinalSpot = false;
    private ArrayList<Point> pointList = new ArrayList<Point>();;
    private int currentPointIndex = 0;
    private boolean once = true;
    private boolean stationary= false;
    private double previousX = 0;
    private double previousY = 0;
    
    double m_VcValue = 2.4500000000000002;
    double m_K_Vc = 0.050000000000000003;
    double m_VmaxMin = 0;
    double m_VmaxMax = 0.8;
    double m_K_Vmax = 23.5;
    double m_dVMin = 0;
    double m_dVMax = 425.0;
    double m_K_dV = 43.119999999999997;
    double m_Ak1 = 12.119999999999999;
    double m_Ak2 = 19.940000000000001;
    double m_Ak3 =3600.0000000000000;
	private double pastLinearVelocity = 0;
	private double pastAngularVelocity = 0;
    
    
    //non-static initialiser block
    {
        parameters.put("fixed point1 x", 140);
        parameters.put("fixed point1 y", 30);
       parameters.put("direction facing", 0);
        //parameters.put("error", 2.5);
    }
    @Override
    public void execute() {
        Robot r = bot;
        //System.out.println("obs");
        int x = parameters.get("fixed point1 x");
        int y = parameters.get("fixed point1 y");
        double angle = parameters.get("direction facing");
        
        if (ballY >= 70 && ballY < 110) {
        	setVelocityToTarget(7,ballY);
        } else if (ballY < 70)  {
        	setVelocityToTarget(7,70);
        } else {
        	setVelocityToTarget(7,110);
        }
        
       
        this.pastAngularVelocity = r.angularVelocity;
        this.pastLinearVelocity = r.linearVelocity;
    }


    public void setVelocityToTarget(double x, double y) {
    	Robot r = bot;
    	Point p = ExecuteMission_MoveTo(4,new Point(x,y), true);
    	
    	
    	
    	if (Math.sqrt(Math.pow((p.x),2) + Math.pow(p.y,2)) < 3.75) {
    		if (ExecuteMission_OrientationTo(4,90, false) < 5) {	
    			r.linearVelocity = 0;
    			r.angularVelocity = 0;
 
    		}
    		
    	} 
    	System.out.println(r.linearVelocity + " " + r.angularVelocity + " " + Math.sqrt(Math.pow((p.x),2) + Math.pow(p.y,2)));
       
    }
    
    private double ExecuteMission_OrientationTo(int VP2M_id, int orientation, boolean bMoveForwardOnly) {
		
    	double targetDist = 0;
    	double targetTheta = orientation;
        double difference = Math.toRadians(targetTheta) - Math.toRadians(bot.getTheta());
        if (difference > Math.PI) {
            difference -= (2 * Math.PI);
        } else if (difference < -Math.PI) {
            difference += (2 * Math.PI);
        }
        difference = Math.toDegrees(difference);
        targetTheta = difference;
        
        boolean bMoveBackward = false;
        
        if (!bMoveForwardOnly && Math.abs(targetTheta) > 90) {
            if (targetTheta < 0) {
                targetTheta = -180 - targetTheta;
            }
            else if (targetTheta > 0) {
                targetTheta = 180 - targetTheta;
            }
        }
        
        GenerateVelocity_VP2(VP2M_id, targetTheta, targetDist, bot);
        
        
        if (bMoveBackward) {
       	 bot.linearVelocity *= -1;
        }
        
        SmoothVelocity(VP2M_id);
		return Math.abs(targetTheta);
	}


	public Point ExecuteMission_MoveTo(int VP2M_id, Point pos, boolean bMoveForwardOnly) {
    	 Robot r = bot;
    	 double tempX = r.getXPosition() + (r.linearVelocity*0.100);
    	 
    	 double  targetDist = Math.sqrt(Math.pow((pos.x-r.getXPosition()),2) + Math.pow((pos.y-r.getYPosition()),2));
         double targetTheta = Math.atan2(r.getYPosition() - pos.y, pos.x - r.getXPosition());
         double difference = targetTheta - Math.toRadians(r.getTheta());
         if (difference > Math.PI) {
             difference -= (2 * Math.PI);
         } else if (difference < -Math.PI) {
             difference += (2 * Math.PI);
         }
         difference = Math.toDegrees(difference);
         targetTheta = difference;
         
         boolean bMoveBackward = false;
         
         if (!bMoveForwardOnly && Math.abs(targetTheta) > 90) {
             if (targetTheta < 0) {
                 targetTheta = -180 - targetTheta;
             }
             else if (targetTheta > 0) {
                 targetTheta = 180 - targetTheta;
             }
         }
         
         GenerateVelocity_VP2(VP2M_id, targetTheta, targetDist, r);
         
         
         if (bMoveBackward) {
        	 r.linearVelocity *= -1;
         }
         
         SmoothVelocity(VP2M_id);
         
         return new Point(pos.x - r.getXPosition(), pos.y - r.getYPosition());
    }

	private void SmoothVelocity(int vP2M_id) {
		Robot r = bot;
		double LinearVelocityMaxAcc  = m_Ak1 * 1.0 / 30.0;
		double LinearVelocityMaxDec  = m_Ak2 * 1.0 / 30.0;
		double AngularVelocityMaxAcc = m_Ak3 * Math.PI / 180.0 * 1.0 / 30.0;
		
		double pre_lv = pastLinearVelocity;
		double pre_av = pastAngularVelocity;

		if( pre_lv * r.linearVelocity < 0 )
		{
			r.linearVelocity = 0;
		}
		else
		{
			double d_lv = r.linearVelocity - pre_lv;
			if( d_lv > 0  )  {
				d_lv = Math.min( d_lv, LinearVelocityMaxAcc );

				//if( r.linearVelocity > 0 )
					r.linearVelocity = pre_lv + d_lv;
				//else
				//	r.linearVelocity = pre_lv - d_lv;
			}
			else 
			{
				d_lv = -Math.min( -d_lv, LinearVelocityMaxDec );

				r.linearVelocity = pre_lv + d_lv;
			}
		}

		if( pre_av * r.angularVelocity < 0 )
		{
			r.angularVelocity = 0;
		}
		else
		{
			double d_av = r.angularVelocity - pre_av;
			if( d_av > 0  ) 
			{
				d_av = Math.min( d_av, AngularVelocityMaxAcc );

				//if( r.angularVelocity > 0 )
					r.angularVelocity = pre_av + d_av;
				//else
				//	r.angularVelocity = pre_av - d_av;
			}
			else 
			{
				d_av = -Math.min( -d_av, AngularVelocityMaxAcc );

				//if( r.angularVelocity > 0 )
					r.angularVelocity = pre_av + d_av;
				//else
				//	r.angularVelocity = pre_av - d_av;
			}
		}
	}

	private void GenerateVelocity_VP2(int vP2M_id, double targetTheta,
			double targetDist, Robot r) {
		
		double expEquation =  -(targetTheta/m_K_dV)*(targetTheta/m_K_dV); 
		double AngularVelocityDegree = (m_dVMin +  (m_dVMax - m_dVMin)*(1.0 - Math.exp(expEquation)));
		
		if (targetTheta < 0 ) AngularVelocityDegree *= -1;
		double expEquation2 = -(targetTheta/m_K_Vmax)*(targetTheta/m_K_Vmax);  
		double Vmax = (m_VmaxMin +  (m_VmaxMax-m_VmaxMin)*(Math.exp(expEquation2)));
		
		double expEquation3 = -(targetDist/m_K_Vc)*(targetDist/m_K_Vc); 
		double V = (Vmax*(1.0 -Math.exp(expEquation3)));
		
		r.angularVelocity = AngularVelocityDegree *Math.PI/180;
		r.linearVelocity = V;
	}
    
}
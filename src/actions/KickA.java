package actions;

import java.util.ArrayList;

import org.opencv.core.Point;

import bot.Robot;
import strategy.Action;
import ui.Field;

public class KickA extends Action{
	
	private String missionText = "kick";
	private boolean moveBackward = false;
	private ArrayList<Point> ballPoints = new ArrayList<Point>();
	//VP2M_id = 2;
	
	private double m_VcValue = 2.8500000000000002;
    private double m_K_Vc = 0.0;
    private double m_VmaxMin = 0.0;
    private double m_VmaxMax = 0.8;
    private double m_K_Vmax = 23.43;
    private double m_dVMin = 0.0;
    private double m_dVMax = 312.0;
    private double m_K_dV = 40.32;
    private double m_Ak1 = 6.38;
    private double m_Ak2 = 5.85;
    private double  m_Ak3 =3600.0000000000000;
    private double pastLinearVelocity = 0.0;
	private double pastAngularVelocity = 0.0;
	private long missionBeginTime = 0;
	private int turnDirection;
	private Point ballVelocity = new Point();
	
	@Override
	public void execute() {
		ballPoints.add(new Point(ballX,ballY));
		if (ballPoints.size() > 7) {
			ballPoints.remove(0);
		}
		calculateBallVelocity();
		Point ballHit = ballSimulationMove(0.2);
		Point A = new Point(bot.getXPosition(),bot.getYPosition());
		Point B;
		
		//if (true) {
			//B = new Point(220.0,90.0);
		//} else {
			double angle = 0;
			double directX = bot.getXPosition() + Math.cos((angle*Math.PI/180)) * 0.20;
			double directY = bot.getYPosition() + Math.sin((angle*Math.PI/180)) * 0.20;
			B = new Point(directX, directY);
		//}
			
		Point targetPos = A;
		double robotSize = 7.5;
		
		if (!missionText.equals("kick.approach.rotate")) {
			
			double disBallToWaitLine = DistanceToLine(A,B,ballHit);
			
			double angleRobotToBall = AngleDegree(ballHit.x-bot.getXPosition(), ballHit.y-bot.getYPosition());
			double angleRobotToB = AngleDegree(B.x-bot.getXPosition(),B.y-bot.getYPosition());
			double angleCheck = angleRobotToBall - angleRobotToB;
			
			while (angleCheck < -180) angleCheck+=360;
			while (angleCheck > 180) angleCheck -= 360;
			
			double orientationError = angleRobotToB - bot.getTheta();
			while (orientationError < -180) orientationError+=360;
			while (orientationError > 180) orientationError -= 360;
			
			if (Math.abs(disBallToWaitLine) < robotSize/2 && Math.abs(angleCheck) < 90 && Math.abs(orientationError) < 10 ) {
				missionText = "kick";
				
				targetPos = B;
				
				double errorPositionStop =7.5;
				if (Distance(ExecuteMission_MoveTo(2,targetPos,false)) < errorPositionStop) {
					//if( mission.behavior.controlType == StrategyGUI_BehaviorControlType::Once )
						//mission.bComplete = true;
				} else {
					//bActiveMission = true;
				}
				
				
			} else {
				//used simulate ball position for normalize in C++
				Point tmpNormalize = normalize(new Point(ballX-B.x,ballY-B.y));
				Point tmp = new Point(ballHit.x+(tmpNormalize.x*robotSize/2),ballHit.y+(tmpNormalize.y*robotSize/2));
				
				double radBallToB = AngleRadian(B.x-ballHit.x,B.y-ballHit.y);
				turnDirection = -1;
				
				if (ccw(new Point(bot.getXPosition(),bot.getYPosition()),ballHit, B) >= 0) {
					turnDirection = 1;
				}
				
				Point posCenter = new Point();
				posCenter.x = tmp.x + Math.cos(radBallToB +turnDirection * Math.PI/2)* robotSize;
				posCenter.y = tmp.y + Math.sin(radBallToB + Math.PI/2)*robotSize;
				
				Point posCenterPlanB = new Point();
				posCenterPlanB.x = tmp.x + Math.cos(radBallToB -turnDirection * Math.PI/2)*robotSize;
				posCenterPlanB.y = tmp.y + Math.sin(radBallToB +turnDirection * Math.PI/2)*robotSize;
				
				if (posCenter.x < robotSize/2 || posCenter.y < robotSize/2 || posCenter.x > Field.OUTER_BOUNDARY_WIDTH -robotSize/2 || posCenter.y > Field.OUTER_BOUNDARY_HEIGHT - robotSize/2) {
					posCenter = posCenterPlanB;
					turnDirection = -turnDirection;
				}
				
				Point missionTmp = posCenter;
				
				double disRobotToCenter = Distance(bot.getXPosition()-posCenter.x,bot.getYPosition()-posCenter.y);
				double radius = robotSize/2;
				
				if (disRobotToCenter > radius) {
					
					double disRobotToTarget = Math.sqrt(disRobotToCenter*disRobotToCenter - radius*radius);
					double radTarget = Math.atan2(disRobotToTarget, radius);
					
					double radCenterToRobot = AngleRadian(bot.getXPosition()-posCenter.x,bot.getYPosition()-posCenter.y);
					
					targetPos.x = posCenter.x + Math.cos(radCenterToRobot + turnDirection *radTarget)*radius;
					targetPos.y = posCenter.y + Math.sin(radCenterToRobot + turnDirection *radTarget)*radius;
				}
				double errorPositionStop = 7.5;
				if (Distance(ExecuteMission_MoveTo(2,targetPos,false))  < errorPositionStop) {
					missionText = "kick.approach.rotate";
					missionBeginTime  = System.currentTimeMillis();	
				} else {
					missionText = "kick.approach";
				}
			}
			
		}
		
		if (missionText.equals("kick.approach.rotate")) {	
			if (missionBeginTime + 500 < System.currentTimeMillis()) {
				missionText = "kick";
				bot.angularVelocity = 0;
				bot.linearVelocity = 0;
			} else {
				if (moveBackward) {
					bot.linearVelocity = -0.3;
				} else  {
					bot.linearVelocity = 0.3;
				}
				bot.angularVelocity = (480*turnDirection) * (Math.PI/180);
				
			}
			
		}	
		
		System.out.println(missionText);
	}

	
	private void calculateBallVelocity() {
		Point mean = new Point(0,0);
		
		for (int i = 0; i<ballPoints.size(); i++) {
			mean.x += ballPoints.get(i).x;
			mean.y += ballPoints.get(i).y;
		}
		
		mean.x /= ballPoints.size();
		mean.y/= ballPoints.size();
		
		double a = 0;
		double b = 0;
		double c = 0;
		
		for (int i = 0; i<ballPoints.size(); i++) {
			
			a += (ballPoints.get(i).x - mean.x)*(ballPoints.get(i).x - mean.x);
			b += 2*  (ballPoints.get(i).x - mean.x) *  (ballPoints.get(i).y - mean.y);
			c +=  (ballPoints.get(i).y - mean.y)  *  (ballPoints.get(i).y - mean.y);
		}
		
		if (a==c || b == 0) {
			ballVelocity.x = 0;
			ballVelocity.y = 0;
		}
		
		double theta = Math.atan2(b, a-c)/2;
		
		Point dis = new Point(ballPoints.get((ballPoints.size()-1)).x-ballPoints.get(0).x,
				ballPoints.get(ballPoints.size()-1).y-ballPoints.get(0).y);
		
		if (Math.abs(theta) < Math.PI/4) {
			if (dis.x < 0) theta+=Math.PI;
		} else {
			if (dis.y > 0) {
				if (theta < 0 ) theta += Math.PI;
			} else {
				if (theta > 0) theta -= Math.PI;
			}
		}
		
		ballVelocity.x = Math.cos(theta)*Distance(dis.x,dis.y) / ballPoints.size() / (1.0/30.0);
		ballVelocity.y = Math.sin(theta)*Distance(dis.x,dis.y) / ballPoints.size() / (1.0/30.0);
	}
	


	private Point ballSimulationMove(double sec) {
		
		double radius = 2.3500000000000000;
		Point ball = new Point(ballX+ballVelocity.x*sec,ballY+ballVelocity.y*sec);
		double BALL_WALL_K = 0.5;
		
		if (ball.x < radius && ballVelocity.x < 0) {
			ball.x = radius - BALL_WALL_K*(ball.x-radius);
			ballVelocity.x *= -BALL_WALL_K;
		}
		
		if (ball.x > Field.OUTER_BOUNDARY_WIDTH-radius && ballVelocity.x > 0) {
			ball.x = (Field.OUTER_BOUNDARY_WIDTH-radius) - BALL_WALL_K*(ball.x-(Field.OUTER_BOUNDARY_WIDTH-radius));
		}
		
		if (ball.y < radius && ballVelocity.y < 0) {
			ball.y = radius - BALL_WALL_K*(ball.y-radius);
			ballVelocity.y *= -BALL_WALL_K;
		}
		
		if (ball.y > Field.OUTER_BOUNDARY_HEIGHT-radius && ballVelocity.y > 0) {
			ball.y = (Field.OUTER_BOUNDARY_HEIGHT-radius) - BALL_WALL_K*(ball.y-(Field.OUTER_BOUNDARY_HEIGHT-radius));
		}
		
		double k = 5;
		double g = 9.8;
		double mass = 0.1;
		
		Point friction = normalize(ballVelocity);
		//ballVelocity.x = ballVelocity.x - friction.x * k * mass * g * sec;
		//ballVelocity.y = ballVelocity.x - friction.x * k * mass * g * sec;
		
		
		return ball;
	}


	private double Distance(Point p) {
		return Distance(p.x,p.y);
	}


	private double DistanceToLine(Point P1, Point P2, Point P) {
		double angle1 = AngleRadian((P1.x-P2.x),(P1.y-P2.y));
		double angle2 = AngleRadian((P.x-P2.x), (P.y-P2.y));
		
		double distance = Distance((P.x-P2.x),(P.y-P2.y))* Math.sin(angle2-angle1);
		
		return distance;
	}
	private double ccw(Point p1, Point p2, Point p3) {
		return p1.x*p2.y + p2.x*p3.y + p3.x*p1.y - p1.x*p3.y - p2.x*p1.y  - p3.x*p2.y;
	}
	
	public Point normalize(Point a) {
		Point result = new Point();
		double dis = Distance(a.x,a.y);
		if (dis!=0) {
			result.x = a.x / dis;
			result.y = a.y / dis;
		}
		
		return result;
		
	}
	
	public double Distance(double x, double y) {
		return Math.sqrt((x*x) + (y*y));
	}
	
	public double AngleRadian(double x, double y) {
		double angle = Math.atan2(y, x);
		while(angle < -Math.PI) angle+=(Math.PI*2);
		while(angle > Math.PI) angle -=(Math.PI*2);
		return angle;
	}
	
	public double AngleDegree(double x, double y) {
		double angle = Math.atan2(y, x) * 180/Math.PI;
		while(angle < -180) angle+=360;
		while(angle > 180) angle -=360;
		return angle;
	}
	

	public Point ExecuteMission_MoveTo(int VP2M_id, Point pos, boolean bMoveForwardOnly) {
    	 Robot r = bot;
    	 
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
         
    	 Point errorPosition = new Point(pos.x-r.getXPosition(),pos.y-r.getYPosition());
    	 targetDist = Distance(errorPosition);
         
         
         boolean bMoveBackward = false;
         
         if (!bMoveForwardOnly && Math.abs(targetTheta) > 90) {
        	 bMoveBackward = true;
             if (targetTheta < 0) {
                 targetTheta = -180 - targetTheta;
             }
             else if (targetTheta > 0) {
                 targetTheta = 180 - targetTheta;
             }
         }
         
         GenerateVelocity_VP2(VP2M_id, targetTheta, targetDist/100.00, r);
         //System.out.println("speed 1: "  + r.linearVelocity + " " + r.angularVelocity);
         
         if (bMoveBackward) {
        	 r.linearVelocity *= -1;
         }
         
         SmoothVelocity(VP2M_id);
         //System.out.println("speed 2: "  + r.linearVelocity + " " + r.angularVelocity);
         moveBackward = bMoveBackward;
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
				d_lv = -1*Math.min( -1*d_lv, LinearVelocityMaxDec );

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
				d_av = -1*Math.min( -d_av, AngularVelocityMaxAcc );

				//if( r.angularVelocity > 0 )
					r.angularVelocity = pre_av + d_av;
				//else
				//	r.angularVelocity = pre_av - d_av;
			}
		}
		pastLinearVelocity = r.linearVelocity;
		pastAngularVelocity = r.angularVelocity;
	}

	private void GenerateVelocity_VP2(int vP2M_id, double targetTheta,
			double targetDist, Robot r) {
		
		double expEquation =  -(targetTheta/m_K_dV)*(targetTheta/m_K_dV); 
		double AngularVelocityDegree = (m_dVMin +  (m_dVMax - m_dVMin)*(1.0 - Math.exp(expEquation)));
		
		if (targetTheta < 0 ) AngularVelocityDegree *= -1;
		double expEquation2 = -(targetTheta/m_K_Vmax)*(targetTheta/m_K_Vmax);  
		double Vmax = (m_VmaxMin +  (m_VmaxMax-m_VmaxMin)*(Math.exp(expEquation2)));
		//System.out.println("Vmax: " + Vmax + " expEquation2:" + expEquation2);
		double partEquation = (targetDist/m_K_Vc);
		double expEquation3 = -((targetDist/m_K_Vc)*(targetDist/m_K_Vc)); 
		double V = (Vmax*(1.0 -Math.exp(expEquation3)));
		//System.out.println("V: " + V + " expEquation3:" + expEquation3 + " partEquation:" + partEquation + " targetDist:" + targetDist);
		r.angularVelocity = AngularVelocityDegree *Math.PI/180;
		r.linearVelocity = V;
	}
}

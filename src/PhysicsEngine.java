import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

//import java.util.*;

public class PhysicsEngine
{
	private final double gravity = 10;
	private final double directionalinfluince = 10;
	private final double restoringforce = 7;
	private final double MaxStrain = 2.5;
	private final double tension = 10;
	private final double DeltaT = 0.05;
	private final double viscosity = 5;
	
	public Level level;
	public Blob blob;
	
	public PhysicsEngine(Level l, Blob b)
	{
		level = l;
		blob = b;
	}
	
	public synchronized void step(boolean LeftPressed, boolean RightPressed, boolean UpPressed, boolean DownPressed, boolean SpacePressed)
	{
		//here, check if you are coliding with special things, like hazards, (other things if there are any), or the winning box
		if(blob.aspectratio!= 1)
		{
			//Droplet is in contact with a wall, which is stuck surface
			//The orientation is already set as well
			double vx = blob.VelocityX;
			double vy = blob.VelocityY;
			double vtan = blob.TangentalVelocity();
			//System.out.println("VTAN "+vtan);
			double vnorm = blob.NormalVelocity();
			Surface s = blob.stuckSurface;
			double strain = blob.aspectratio;
			//Determine all forces on the blob
			double fNormal = 0;
			double fTan = 0;
			double theta = blob.orientation;
			//Note that to be consistant as possible down is positive in this context
			//we would have benifited greatly by standardizing coordinate systems before starting
			//fNormal is positive away from the surface
			//System.out.println("Gravity "+(-gravity * Math.cos(theta)));
			fNormal -= gravity * Math.cos(theta);
			//System.out.println("tension "+( -tension * (1-1/strain)));
			fTan -= gravity*Math.sin(theta);
			fNormal -= tension * (1-1/strain);
			//System.out.println("Restoring "+(strain * restoringforce*(SpacePressed?2:1)));
			fNormal += strain * restoringforce*(SpacePressed?3:1);
			//System.out.println("Viscosity "+(-vnorm * viscosity));
			fNormal -= vnorm * viscosity;//This might be a little much
			//System.out.println(fNormal+"\n");
			vnorm += fNormal*DeltaT;
			//System.out.println(vnorm);
			vtan += fTan*DeltaT;
			//System.out.println("VTAN VNORM"+vtan+" "+vnorm);
			blob.setVelocityTanNorm(vtan, vnorm);
			if(vnorm>0)//determine if the blob will leave the surface
			{
				//System.out.println("Somehow Got Here");
				if(findDistance(blob.center,s)+ vnorm*DeltaT >= blob.unstressedsize/2)
				{
					System.out.println("Blob has left ground");
					//blob has successfully bounced off the ground
					//must leave a little velocity behind to take off
					double tSize = (blob.unstressedsize/2 - findDistance(blob.center, s))/vnorm;
					blob.move(tSize);
					blob.freeFromSurface();
					blob.move(DeltaT-tSize);
					return;
					//finishStep(LeftPressed,RightPressed,UpPressed,DownPressed,SpacePressed,DeltaT-tSize);
				}
			}
			//System.out.println(blob.VelocityX+" "+blob.VelocityY);
			blob.move(DeltaT);			
			double distance = findDistance(blob.center, s);
			blob.aspectratio = blob.unstressedsize/(distance*2);
			if(blob.aspectratio<1 || blob.aspectratio>4)
			{
				System.out.println("Aspect Ratio is bad");
				double tRest = 0;
				while(blob.aspectratio<1 || blob.aspectratio>4)
				{
					blob.move(-DeltaT*.05);
					tRest+=0.05;
					distance = findDistance(blob.center, s);
					blob.aspectratio = blob.unstressedsize/(distance*2);
				}
				blob.setVelocityTanNorm(blob.TangentalVelocity(), 0);
				System.out.println(tRest);
				//finishStep(LeftPressed,RightPressed,UpPressed,DownPressed,SpacePressed,tRest);
			}
			//Should check to see if it rolled off surface or onto another one here.
			//Note that this is still basically undefined behavior
			Surface newS;
			//System.out.println("......");
			if((newS = SurfaceColiding())!=s)
			{
				//System.out.println("In here");
				if(newS == null)
				{
					blob.freeFromSurface();
					//System.out.println("IM FREE!");
				}
				else
				{
					System.out.println("Shifting");
					blob.orientation = newS.getOrientation();
					blob.stuckSurface = newS;
					distance = findDistance(blob.center, newS);
					blob.aspectratio = blob.unstressedsize/(distance*2);
				}
			}
			System.out.println("......");
		}
		else
		{
			double vx = blob.VelocityX;
			double vy = blob.VelocityY;
			vy += gravity*DeltaT;
			if(LeftPressed)
				vx-=directionalinfluince*DeltaT;
			if(RightPressed)
				vx+=directionalinfluince*DeltaT;
			blob.VelocityX = vx;
			blob.VelocityY = vy;
			Point2D before = new Point2D.Double();
			before.setLocation(blob.center);
			blob.move(DeltaT);
			Surface s;
			
			if((s = SurfaceColiding(before))!=null)//Place blob right on the surface
			{
				blob.orientation = s.getOrientation();
				blob.stuckSurface = s;
				double distance = findDistance(blob.center, s);
				//System.out.println(distance);
				double negTime = (distance - blob.unstressedsize/2)/Math.abs(blob.NormalVelocity());
				//System.out.println("negTime "+negTime);
				blob.move(negTime);
				blob.aspectratio=1.0001;
				//finishStep(LeftPressed,RightPressed,UpPressed,DownPressed,SpacePressed,-negTime);
			}
			
		}
		
		
	}
	
	
	
	
	
	//this assumes that the point is actually above the line,
	//this function is only called under these circumstances.
	private double findDistance(Point2D p, Surface s)
	{
		//System.out.println(p);
		//System.out.println(s);
		double ax = p.getX() - s.start.getX();
		double ay = p.getY() - s.start.getY();
		double bx = s.end.getX() - s.start.getX();
		double by = s.end.getY() - s.start.getY();
		return (ax*by - bx*ay)/s.end.distance(s.start);
	}
	private Surface SurfaceColiding(Point2D before)
	{
		Surface best = null;
		double bestNVel = 999;
		//Collision based on current blob location (important for colided movement code)
		for(Surface s:level.surfaces)
		{
			double ax = blob.center.getX() - s.start.getX();
			double ay = blob.center.getY() - s.start.getY();
			double bx = s.end.getX() - s.start.getX();
			double by = s.end.getY() - s.start.getY();
			double cx = blob.center.getX() - s.end.getX();
			double cy = blob.center.getY() - s.end.getY();
			double aCrossB = ax*by - bx*ay;
			if(aCrossB < 0)
				continue;
			if(Math.abs(aCrossB) > blob.unstressedsize * s.end.distance(s.start)/2)
				continue;
			if(ax*bx + ay*by < 0)
				continue;
			if(cx*bx + cy*by > 0)
				continue;
			
			double oldorientation = blob.orientation;
			blob.orientation = s.getOrientation();
			//System.out.println(blob.NormalVelocity());
			if(blob.NormalVelocity() < bestNVel)
			{
				System.out.println("COLIDING SURFACE REALLY");
				best = s;
				bestNVel=blob.NormalVelocity();
			}
			blob.orientation = oldorientation;

		}
		before = new Point2D.Double(before.getX()+blob.VelocityX * blob.unstressedsize/Math.sqrt(blob.VelocityX*blob.VelocityX+blob.VelocityY*blob.VelocityY)/2, 
				                    before.getY()+blob.VelocityY * blob.unstressedsize/Math.sqrt(blob.VelocityX*blob.VelocityX+blob.VelocityY*blob.VelocityY)/2);
		
		Line2D moveVec = new Line2D.Double(before,new Point2D.Double(
				blob.center.getX() + blob.VelocityX * blob.unstressedsize/Math.sqrt(blob.VelocityX*blob.VelocityX+blob.VelocityY*blob.VelocityY)/2, 
				blob.center.getY() + blob.VelocityY * blob.unstressedsize/Math.sqrt(blob.VelocityX*blob.VelocityX+blob.VelocityY*blob.VelocityY)/2));
		
		//This specifically only matches if the blob passes straight through 
		for(Surface s:level.surfaces)
		{
			Line2D surface = s.lineRep;
			if(moveVec.intersectsLine(surface))//This is a valid intersection
			{
				double oldOrientation = blob.orientation;
				blob.orientation = s.getOrientation();
				if(blob.NormalVelocity()<bestNVel)
				{
					bestNVel = blob.NormalVelocity();
					
					//Determine when it collided in deltat sense
					
				}
			}
		}
		return best;
	}
	
	Point2D getIntersection(Line2D l1, Line2D l2)
	{
		double x1 = l1.getX1();
		double y1 = l1.getY1();
		double x2 = l1.getX2();
		double y2 = l1.getY2();
		double x3 = l2.getX1();
		double y3 = l2.getY1();
		double x4 = l2.getX2();
		double y4 = l2.getY2();
		double d = (x1-x2)*(y3-y4) - (y1-y2)*(x3-x4);
	    if (d == 0) return null;
	    double xi = ((x3-x4)*(x1*y2-y1*x2)-(x1-x2)*(x3*y4-y3*x4))/d;
	    double yi = ((y3-y4)*(x1*y2-y1*x2)-(y1-y2)*(x3*y4-y3*x4))/d;
	    return new Point2D.Double(xi,yi);
	}
	
	private Surface SurfaceColiding()//determines what face the blob is colliding with, then sets the angle and returns the surface
	{
        ///////////////////////////////////////////////////////////
		//write this
		//based on proximity and velocity
		//if you switch surfaces make sure you do it correctly
		//////////////////////////////////////////////////////////
		Surface bestsurface = null;
		double maxnormalvelocity = 999;
		//for now do this only
		for(Surface s:level.surfaces)
		{
			double ax = blob.center.getX() - s.start.getX();
			double ay = blob.center.getY() - s.start.getY();
			double bx = s.end.getX() - s.start.getX();
			double by = s.end.getY() - s.start.getY();
			double cx = blob.center.getX() - s.end.getX();
			double cy = blob.center.getY() - s.end.getY();
			double aCrossB = ax*by - bx*ay;
			if(aCrossB < 0)
				continue;
			if(Math.abs(aCrossB) > blob.unstressedsize * s.end.distance(s.start)/2)
				continue;
			if(ax*bx + ay*by < 0)
				continue;
			if(cx*bx + cy*by > 0)
				continue;
			
			double oldorientation = blob.orientation;
			blob.orientation = s.getOrientation();
			//System.out.println(blob.NormalVelocity());
			if(blob.NormalVelocity() < maxnormalvelocity)
			{
				System.out.println("COLIDING SURFACE REALLY");
				bestsurface = s;
				maxnormalvelocity=blob.NormalVelocity();
			}
			blob.orientation = oldorientation;

		}
		return bestsurface;
	}
	
	public boolean haswon()
	{
/////////////////////////////////////////////////////////
		//write this		
		return false;
	}
}

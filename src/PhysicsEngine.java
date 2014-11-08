//import java.util.*;

public class PhysicsEngine
{
	private final double gravity = 10;
	private final double directionalinfluince = 20;
	private final double dampening = 3;
	private final double restoringforce = 20;
	private final double MaxStrain = 2.5;
	private final double tension = 10;
	private final double DeltaT = 0.1;
	
	public Level level;
	public Blob blob;
	
	public PhysicsEngine(Level l, Blob b)
	{
		level = l;
		blob = b;
	}
	
	public void step(boolean LeftPressed, boolean RightPressed, boolean UpPressed, boolean DownPressed, boolean SpacePressed)
	{
		//here, check if you are coliding with special things, like hazards, (other things if there are any), or the winning box
		
		
		double NetForceDown = 0;
		double NetForceRight = 0;
		if(colliding())
		{
			Surface s = SurfaceColiding();
			double Runstressed = blob.unstressedsize/2;
			double Ractual = ((s.end.getY()-s.start.getY())*(blob.center.getX()-s.start.getX()) + (s.end.getX()-s.start.getX())*(blob.center.getY()-s.start.getY()))/Math.sqrt(Math.pow((s.end.getY()-s.start.getY()),2) + Math.pow((s.end.getX()-s.start.getX()),2));
			double strain = Math.min(Runstressed/Ractual,MaxStrain);
			
			double NetForceTangant = 0;
			double NetForceNormal = 0;
			//gravity
			NetForceTangant -= Math.sin(blob.orientation);
			NetForceNormal -= Math.cos(blob.orientation);
			//tension (related to area for philic)
			if(!s.hydrophobic)
				NetForceNormal -= tension*(strain - 0.5);
			//dampening (only if it is philic)
			if((!s.hydrophobic) && blob.TangentalVelocity()!=0)
			{
				NetForceTangant -= blob.TangentalVelocity()/Math.abs(blob.TangentalVelocity())*Math.min(0.5*Math.abs(blob.TangentalVelocity()),1)*dampening;
			}
			//DI
			double KeyboardInputValue = 0;
			if(LeftPressed)
				KeyboardInputValue -= Math.cos(blob.orientation);
			if(RightPressed)
				KeyboardInputValue += Math.cos(blob.orientation);
			if(DownPressed)
				KeyboardInputValue -= Math.sin(blob.orientation);
			if(UpPressed)
				KeyboardInputValue += Math.sin(blob.orientation);
			if(!(KeyboardInputValue==0))
				KeyboardInputValue = KeyboardInputValue/Math.abs(KeyboardInputValue);
			
			NetForceTangant += KeyboardInputValue*0.1*directionalinfluince;
			if(!s.hydrophobic)
				NetForceTangant += KeyboardInputValue*0.9*directionalinfluince;
			//restoring and aspect ratio, springiness
			//need compression (strain) for this
			if(blob.NormalVelocity() >= 0 && SpacePressed)
				NetForceNormal += 2*restoringforce*strain;
			else
				NetForceNormal += restoringforce*strain;
			//normal (not sure how to implement)
				//if it is at max aspect ratio add this to equal net negative normal force
			if((strain == MaxStrain) && (NetForceNormal < 0))
				NetForceNormal = 0;
			
			
			//maximum movement normal and truncate normal velocity
			//normal movement and deformation
			double normalvelocity = blob.NormalVelocity();
			double maxmovementnormal = Ractual - Runstressed/MaxStrain;
			if(blob.NormalVelocity()*DeltaT < maxmovementnormal)
				normalvelocity = maxmovementnormal/DeltaT;
			blob.center.setLocation(blob.center.getX()-normalvelocity*DeltaT*Math.sin(blob.orientation), blob.center.getY()-normalvelocity*DeltaT*Math.cos(blob.orientation));
			blob.VelocityX -= NetForceNormal*DeltaT*Math.sin(blob.orientation);
			blob.VelocityY += NetForceNormal*DeltaT*Math.cos(blob.orientation);
			//bumped into a wall
			//horisontal movement
			double tangentvelocity = blob.TangentalVelocity();
			blob.center.setLocation(blob.center.getX()+tangentvelocity*DeltaT*Math.cos(blob.orientation), blob.center.getY()-tangentvelocity*DeltaT*Math.sin(blob.orientation));
			blob.VelocityX += NetForceTangant*DeltaT*Math.cos(blob.orientation);
			blob.VelocityY -= NetForceTangant*DeltaT*Math.sin(blob.orientation);
			
/////////////////////////////////////////////////////////////////////
			//did it bump in to a wall? you never checked.  do I even want to? hmm...
			
		}
		else
		{
			blob.aspectratio=1;
			NetForceDown += gravity;
			if(LeftPressed)
				NetForceRight += 0.2*directionalinfluince;
			if(RightPressed)
				NetForceRight -= 0.2*directionalinfluince;
			if(DownPressed)
				NetForceDown += 0.1*directionalinfluince;
			if(UpPressed)
				NetForceDown -= 0.1*directionalinfluince;
			
			blob.center.setLocation(blob.VelocityX*DeltaT, blob.VelocityY*DeltaT);
			blob.VelocityX += NetForceRight*DeltaT;
			blob.VelocityY += NetForceDown*DeltaT;
			blob.aspectratio = 1;
		}
	}
	
	private boolean colliding()//checks if the blob is close to a surface, flags collision for step
	{
/////////////////////////////////////////////////////////
		//write this
		for(Surface s:level.surfaces)
		{
			double r = ((s.end.getY()-s.start.getY())*(blob.center.getX()-s.start.getX()) + (s.end.getX()-s.start.getX())*(blob.center.getY()-s.start.getY()))/Math.sqrt(Math.pow((s.end.getY()-s.start.getY()),2) + Math.pow((s.end.getX()-s.start.getX()),2));
			if(r<blob.unstressedsize/2)
				if(blob.center.getY() > (-(s.end.getX()-s.start.getX())/(s.end.getY()-s.start.getY())*(blob.center.getX()-s.start.getX())+s.start.getY()))//above normal through 1
					if(blob.center.getY() < (-(s.end.getX()-s.start.getX())/(s.end.getY()-s.start.getY())*(blob.center.getX()-s.end.getX())+s.end.getY()))//below normal through 2
						if(blob.center.getY() > ((s.end.getY()-s.start.getY())/(s.end.getX()-s.start.getX())*(blob.center.getX()-s.start.getX())+s.start.getY()))//above line
							return true;
		}
		//make rectangles around the surfaces, check if the blob center is in the rectancle
		//ends and corners may not end up being handled well
		return false;
	}
	
	private Surface SurfaceColiding()//determines what face the blob is colliding with, then sets the angle and returns the surface
	{
///////////////////////////////////////////////////////////
		//write this
		//based on proximity and velocity
		//if you switch surfaces make sure you do it correctly
		
		//for now do this only
		for(Surface s:level.surfaces)
		{
			double r = ((s.end.getY()-s.start.getY())*(blob.center.getX()-s.start.getX()) + (s.end.getX()-s.start.getX())*(blob.center.getY()-s.start.getY()))/Math.sqrt(Math.pow((s.end.getY()-s.start.getY()),2) + Math.pow((s.end.getX()-s.start.getX()),2));
			if(r<blob.unstressedsize/2)
				if(blob.center.getY() > (-(s.end.getX()-s.start.getX())/(s.end.getY()-s.start.getY())*(blob.center.getX()-s.start.getX())+s.start.getY()))//above normal through 1
					if(blob.center.getY() < (-(s.end.getX()-s.start.getX())/(s.end.getY()-s.start.getY())*(blob.center.getX()-s.end.getX())+s.end.getY()))//below normal through 2
						if(blob.center.getY() > ((s.end.getY()-s.start.getY())/(s.end.getX()-s.start.getX())*(blob.center.getX()-s.start.getX())+s.start.getY()))//above line
							return s;
		}
		return null;
	}
	
	public boolean haswon()
	{
/////////////////////////////////////////////////////////
		//write this		
		return false;
	}
}

import java.util.*;

public class PhysicsEngine
{
	private final double gravity = 10;
	private final double directionalinfluince = 10;
	private final double dampening = 3;
	private final double restoringforce = 10;
	private final double MaxAspectRatio = 10;
	private final double tension = 2;
	
	public Level level;
	public Blob blob;
	
	public PhysicsEngine(Level l, Blob b)
	{
		level = l;
		blob = b;
	}
	
	public void step(boolean LeftPressed, boolean RightPressed, boolean SpacePressed)
	{
		double NetForceDown = 0;
		double NetForceRight = 0;
		if(colliding())
		{
			Surface s = SurfaceColiding();
			double NetForceTangant = 0;
			double NetForceNormal = 0;
			//gravity
			NetForceTangant -= Math.sin(blob.orientation);
			NetForceNormal -= Math.cos(blob.orientation);
			//tension (related to area for philic)
			if(s.IsHydroPhilic)
				NetForceNormal -= tension*blob.unstressedsize*blob.aspectratio;
			//dampening (only if it is philic)
			if(s.IsHydroPhilic)
			{
				double TangentalVelocity = blob.VelocityX
				NetForceTangant += (tangental velocity)
			}
						//DI
			
			//restoring and aspect ratio, springiness
			
			//normal (not sure how to implement)
		}
		else
		{
			blob.aspectratio=1;
			NetForceDown += gravity;
			if(RightPressed)
			{
				NetForceRight += 0.2*directionalinfluince;
			}
			if(LeftPressed)
			{
				NetForceRight += -0.2*directionalinfluince;
			}
		}
		//handle changing the accelerations
		//handle moving
	}
	
	private boolean colliding()//checks if the blob is close to a surface, then sets the angle and flags collision for step
	{
		return false;
	}
	
	private Surface SurfaceColiding()
	{
		
	}
	
	public boolean haswon()
	{
		
		return false;
	}
}

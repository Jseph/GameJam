import java.util.*;

public class PhysicsEngine
{
	private final int maxspeed = 20;
	private final int gravity = 10;
	private final int directionalinfluince = 10;
	private final int dampening = 3;
	private final int restoringforce = 10;
	
	
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
			
			
		}
		else
		{
			NetForceDown += gravity;
			if()
			NetForceRight += 0.2*directionalinfluince*((int)RightPressed-(int)LeftPressed);
		}
	}
	
	private boolean colliding()//checks if the blob is close to a surface, then sets the angle and flags collision for step
	{
		
		return false;
	}
	
	public boolean haswon()
	{
		
		return false;
	}
}

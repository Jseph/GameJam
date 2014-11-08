import java.util.*;

public class PhysicsEngine
{
	private final int maxspeed = 1;
	private final int gravity = 1;
	private final int directionalinfluince = 1;
	private final int dampening = 1;
	private final int restoringforce = 1;
	
	
	public Level level;
	public Blob blob;
	
	public PhysicsEngine(Level l, Blob b)
	{
		level = l;
		blob = b;
	}
	
	public void step()
	{
		double netforce = 0;
	}
	
	private boolean colliding()//checks if the blob is close to a surface, then sets the angle and flags collision for step
	{
		
	}
	
	public boolean haswon()
	{
		
		return false;
	}
}

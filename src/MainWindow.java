import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import javax.swing.*;


public class MainWindow extends JFrame implements KeyListener
{
	int xSize, ySize;
	boolean done;
	GraphicsEngine ge;
	PhysicsEngine pe;
	DrawingState ds;
	BufferedImage buffer;
	boolean upPressed, downPressed, leftPressed, rightPressed, spacePressed;
	Level l;
	public MainWindow()
	{
		super("DRoP");
		Toolkit tk = Toolkit.getDefaultToolkit();
		xSize = (int)tk.getScreenSize().getWidth();
		ySize = (int)tk.getScreenSize().getHeight();
		buffer = new BufferedImage(xSize, ySize, BufferedImage.TYPE_4BYTE_ABGR);
		l = new Level(0);
		Point2D p = new Point2D.Double();
		p.setLocation(l.startPoint);
		pe = new PhysicsEngine(l,new Blob(1,p));
		GraphicsDevice myDevice = this.getGraphicsConfiguration().getDevice();
		setSize(xSize,ySize);
		setUndecorated(true);
		this.setVisible(true);
		/*try{
			myDevice.setFullScreenWindow(this);
		}catch(Exception e){
			myDevice.setFullScreenWindow(null);
			
		}*/
		ds = new DrawingState(xSize, ySize, 50, 0, 0);
		this.setDefaultCloseOperation(this.EXIT_ON_CLOSE);
		done = false;
		Thread renderThread = new Thread(new Runnable() {
			public void run() {
				while(true)
				{
					//try {Thread.sleep(10);} catch (InterruptedException e) {}
					//repaint();// wasn't working so I put this here...
					paint((Graphics2D) getContentPane().getGraphics());
				}
			}
		});
		Thread soundEffects = new Thread(new Runnable() {
			public void run() {
				int jumpNum = 0;
				while(true)
				{
					try {Thread.sleep(10);} catch (InterruptedException e) {}
					if(pe.justJumped)
					{
						new PlaySound(PlaySound.JUMPSTART+jumpNum++, false);
						jumpNum = jumpNum%PlaySound.NUMJUMPS;
						pe.justJumped = false;
					}
				}
			}
		});
		soundEffects.start();
		addKeyListener(this);
		renderThread.start();
		new PlaySound(PlaySound.MAINLOOP, true);
	}
	public void paint(Graphics2D g)
	{
		//while (!done)
		//{
			//Should probably implement double buffering here...
			//Graphics2D myGraphics = (Graphics2D) this.getContentPane().getGraphics();
		long ts = System.currentTimeMillis();
		Graphics2D myGraphics = (Graphics2D) buffer.getGraphics(); 
		Level l = pe.level;
		Blob b = pe.blob;
		//System.out.println(b.center);
		ds.keepInMiddleBox(b.center);
		//Removing the previous rendered frame
		myGraphics.setColor(Color.black);
		myGraphics.fillRect(0, 0, ds.width, ds.height);
		//Here is some goofy stuff I found online, not sure what it actually does
		myGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	    myGraphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		//Drawing the blob... THIS WILL BE HARD
		//for now it is just a rectangle
		//Translate graphics to the location of the blob
		Point2D blobLoc = ds.mapPoint(b.center);
		myGraphics.translate((int)blobLoc.getX(), (int)blobLoc.getY());
		//Rotate graphics to align with the blob
		myGraphics.rotate(-b.orientation);
		//draw a rectangle representing blob location
		myGraphics.setColor(Color.CYAN);
		myGraphics.fillOval((int)(-ds.zoomLevel*b.aspectratio*b.unstressedsize/2), 
				(int)(-ds.zoomLevel/b.aspectratio*b.unstressedsize/2), 
				(int)(ds.zoomLevel*b.aspectratio*b.unstressedsize), 
				(int)(ds.zoomLevel/b.aspectratio*b.unstressedsize));
		//put the graphics state back to where it was
		//I have no idea if this will work
		myGraphics.rotate(b.orientation);
		System.out.println(blobLoc);
		myGraphics.translate(-(int)blobLoc.getX(), -(int)blobLoc.getY());
		//Draw everything else... or actually enable them to draw themselves
		for(Surface s: l.surfaces)
		{
			s.drawSelf(myGraphics, ds);
		}
		for(Actor a: l.actors)
		{
			a.drawSelf((Graphics2D) myGraphics, ds);
		}
		g.drawImage(buffer, 0, 0, this);
		//public void step(boolean LeftPressed, boolean RightPressed, boolean UpPressed, boolean DownPressed, boolean SpacePressed)

		pe.step(leftPressed, rightPressed, upPressed, downPressed, spacePressed);
		//System.out.println(1000/(System.currentTimeMillis()-ts));
		//}
	}
	public static void main(String[] args) 
	{
		System.out.println((int)Double.NaN);
		MainWindow mw = new MainWindow();
		Blob b = new Blob(2, new Point2D.Double());
		b.orientation = Math.PI/6;
		b.setVelocityTanNorm(1, 1);
		System.out.println(b.VelocityX+" "+b.VelocityY);
		System.out.println(b.TangentalVelocity()+" "+b.NormalVelocity());
	}
	@Override
	public void keyPressed(KeyEvent e) 
	{
		if(e.getKeyCode()==e.VK_UP)
			upPressed = true;
		if(e.getKeyCode()==e.VK_DOWN)
			downPressed = true;
		if(e.getKeyCode()==e.VK_LEFT)
			leftPressed = true;
		if(e.getKeyCode()==e.VK_RIGHT)
			rightPressed = true;
		if(e.getKeyCode()==e.VK_SPACE)
			spacePressed = true;
		if(e.getKeyCode()==e.VK_R)
		{
			Point2D p = new Point2D.Double();
			p.setLocation(l.startPoint);
			pe = new PhysicsEngine(l, new Blob(1, p));
		}
	}
	@Override
	public void keyReleased(KeyEvent e) {
		if(e.getKeyCode()==e.VK_UP)
			upPressed = false;
		if(e.getKeyCode()==e.VK_DOWN)
			downPressed = false;
		if(e.getKeyCode()==e.VK_LEFT)
			leftPressed = false;
		if(e.getKeyCode()==e.VK_RIGHT)
			rightPressed = false;
		if(e.getKeyCode()==e.VK_SPACE)
			spacePressed = false;
	}
	@Override
	public void keyTyped(KeyEvent e) {
		// nothing to see here...
		
	}

}

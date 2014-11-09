import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import javax.swing.*;

enum GameState{MAIN_MENU, LEVEL_SELECT, INFO_SCREEN, PLAY_LEVEL, PAUSE_SCREEN, LEVEL_EDITOR}

public class MainWindow extends JFrame implements KeyListener
{
	public static final double fgbgratio = 4;
	int xSize, ySize;
	boolean done;
	GraphicsEngine ge;
	PhysicsEngine pe;
	DrawingState ds;
	BufferedImage buffer;
	BufferedImage backgroundImage;
	Image bgTile;
	Image lDrop;
	Image Drop;
	Image rDrop;
	int bgTileSize;
	boolean upPressed, downPressed, leftPressed, rightPressed, spacePressed;
	//font levelText
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
		lDrop = new ImageIcon("res/textures/Happy Droplet Left.png").getImage();
		Drop = new ImageIcon("res/textures/Happy Droplet.png").getImage();
		rDrop = new ImageIcon("res/textures/Happy Droplet Right.png").getImage();
		bgTile = new ImageIcon("res/textures/backtile.png").getImage();
		bgTileSize = bgTile.getHeight(this);
		backgroundImage = new BufferedImage(xSize+bgTileSize*2, ySize+bgTileSize*2, BufferedImage.TYPE_3BYTE_BGR);
		Graphics g = backgroundImage.getGraphics();
		for(int i=0; i<xSize/bgTileSize+2; i++)
			for(int j=0; j<ySize/bgTileSize+2; j++)
				g.drawImage(bgTile, i*bgTileSize, j*bgTileSize, bgTileSize, bgTileSize, this);
		//backgroundImage.flush();
		setUndecorated(true);
		this.setVisible(true);
		ds = new DrawingState(xSize, ySize, 50, 0, 0);
		this.setDefaultCloseOperation(this.EXIT_ON_CLOSE);
		done = false;
		//PlaySound woosh = new PlaySound(PlaySound.FAST, true, 0);
		Thread renderThread = new Thread(new Runnable() {
			public void run() {
				while(true)
				{
					paintLevel((Graphics2D) getContentPane().getGraphics());
				}
			}
		});
		Thread soundEffects = new Thread(new Runnable() {
			public void run() {
				int jumpNum = 0;
				double theta = 0;
				while(true)
				{
					try {Thread.sleep(10);} catch (InterruptedException e) {}
					if(pe.justJumped)
					{
						new PlaySound(PlaySound.JUMPSTART+jumpNum++, false);
						jumpNum = jumpNum%PlaySound.NUMJUMPS;
						pe.justJumped = false;
					}
					theta += .05;
					//woosh.volume = Math.abs(Math.cos(theta));
				}
			}
		});
		soundEffects.start();
		addKeyListener(this);
		renderThread.start();
		new PlaySound(PlaySound.MAINLOOP, true);
	}
	public void paintLevel(Graphics2D g)
	{
		long ts = System.currentTimeMillis();
		Graphics2D myGraphics = (Graphics2D) buffer.getGraphics();
		myGraphics.drawImage(backgroundImage, -(((int)(ds.topLeftX*ds.zoomLevel/fgbgratio+Integer.MAX_VALUE/2))%bgTileSize), -(((int)(ds.topLeftY*ds.zoomLevel/fgbgratio + Integer.MAX_VALUE/2))%bgTileSize), this);
		Level l = pe.level;
		Blob b = pe.blob;
		ds.keepInMiddleBox(b.center);
		//Removing the previous rendered frame
		//myGraphics.setColor(Color.black);
		//myGraphics.fillRect(0, 0, ds.width, ds.height);
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
		/*myGraphics.fillOval((int)(-ds.zoomLevel*b.aspectratio*b.unstressedsize/2), 
				(int)(-ds.zoomLevel/b.aspectratio*b.unstressedsize/2), 
				(int)(ds.zoomLevel*b.aspectratio*b.unstressedsize), 
				(int)(ds.zoomLevel/b.aspectratio*b.unstressedsize));*/
		Image cDrop = Drop;
		if(leftPressed && !rightPressed)
			cDrop = lDrop;
		if(rightPressed && !leftPressed)
			cDrop = rDrop;
		myGraphics.drawImage(cDrop,(int)(-ds.zoomLevel*b.aspectratio*b.unstressedsize/2), 
				(int)(-ds.zoomLevel/b.aspectratio*b.unstressedsize/2), 
				(int)(ds.zoomLevel*b.aspectratio*b.unstressedsize), 
				(int)(ds.zoomLevel/b.aspectratio*b.unstressedsize), null);
		//put the graphics state back to where it was
		//I have no idea if this will work
		myGraphics.rotate(b.orientation);
		//System.out.println(blobLoc);
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
		pe.step(leftPressed, rightPressed, upPressed, downPressed, spacePressed);

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

import java.awt.DisplayMode;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.Toolkit;

import javax.swing.*;


public class MainWindow extends JFrame 
{
	int xSize, ySize;
	boolean done;
	GraphicsEngine ge;
	GameState gs;
	public MainWindow()
	{
		super("DRoP");
		
		GraphicsDevice myDevice = this.getGraphicsConfiguration().getDevice();
		DisplayMode newDisplayMode;
		DisplayMode oldDisplayMode = myDevice.getDisplayMode();
		try{
			myDevice.setFullScreenWindow(this);
		}catch(Exception e){
			myDevice.setFullScreenWindow(null);
			
		}
		Toolkit tk = Toolkit.getDefaultToolkit();
		xSize = (int)tk.getScreenSize().getWidth();
		ySize = (int)tk.getScreenSize().getHeight();
		this.setDefaultCloseOperation(this.EXIT_ON_CLOSE);
		done = false;
	}
	public void myRenderingLoop()
	{
		while (!done)
		{
			Graphics myGraphics = this.getContentPane().getGraphics();
			
		}
	}
	public static void main(String[] args) 
	{
		MainWindow mw = new MainWindow();
	}

}

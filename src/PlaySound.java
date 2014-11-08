import java.io.*;
import javax.sound.sampled.*;
public class PlaySound implements Runnable
{
	public static final String[] files = new String[]{"mainLoop.wav"};
	public static final int MAINLOOP = 0;
	boolean loop;
	String fileLocation;
	PlaySound(int soundType, boolean loop)
	{
		Thread t = new Thread(this);
		fileLocation = files[soundType];
		this.loop = loop;
		t.start();
	}
	public void run()
	{
		while(playSound(fileLocation)&&loop);
	}
	private boolean playSound(String fileName)
	{
		File    soundFile = new File(fileName);
        AudioInputStream        audioInputStream = null;
        try
        {
                audioInputStream = AudioSystem.getAudioInputStream(soundFile);
        }
        catch (Exception e)
        {
                e.printStackTrace();
        }
        AudioFormat     audioFormat = audioInputStream.getFormat();
        SourceDataLine  line = null;
        DataLine.Info   info = new DataLine.Info(SourceDataLine.class,audioFormat);
        try
        {
                line = (SourceDataLine) AudioSystem.getLine(info);
                line.open(audioFormat);
        }
        catch (LineUnavailableException e)
        {
                e.printStackTrace();
        }
        catch (Exception e)
        {
                e.printStackTrace();
        }
        line.start();
        int nBytesRead = 0;
        byte[]  abData = new byte[128000];
        while (nBytesRead != -1)
        {
                try
                {
                        nBytesRead = audioInputStream.read(abData, 0, abData.length);
                }
                catch (IOException e)
                {
                        e.printStackTrace();
                        line.drain();
                        line.close();
                        return false;
                }
                if (nBytesRead >= 0)
                {
                        int nBytesWritten = line.write(abData, 0, nBytesRead);
                }
        }
        line.drain();
        line.close();
        return true;
	}
}

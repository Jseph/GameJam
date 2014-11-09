import java.io.*;
//volume control is bad
import javax.sound.sampled.*;
public class PlaySound implements Runnable
{
	public static final String[] files;
	static
	{
		files = new String[40];
		files[0] = "res/underclocked.wav";
		files [1] = "res/start.wav";
		files [2] = "res/fast.wav";
		for(int i=0; i<32; i++)
			files[i+3] = "res/j"+(i+1)+".wav";
	}
	public static final int MAINLOOP = 0;
	public static final int START = 1;
	public static final int FAST = 2;
	public static final int JUMPSTART = 3;
	public static final int NUMJUMPS = 32;
	public static double volume;
	boolean loop;
	String fileLocation;
	PlaySound(int soundType, boolean loop)
	{
		Thread t = new Thread(this);
		fileLocation = files[soundType];
		this.loop = loop;
		volume = 1;
		t.start();
	}
	PlaySound(int soundType, boolean loop, double volume)
	{
		Thread t = new Thread(this);
		fileLocation = files[soundType];
		this.loop = loop;
		this.volume = volume;
		t.start();
	}
	public void run()
	{
		try{while(playSound(fileLocation)&&loop);}catch(Exception e){}
		//playAsClip(fileLocation, loop);
	}
	private boolean playSound(String fileName) throws IOException
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
        int bytesPerSample = 1;
        try
        {
                line = (SourceDataLine) AudioSystem.getLine(info);
                line.open(audioFormat);
                bytesPerSample = audioFormat.getFrameSize()/audioFormat.getChannels();
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
                        int count = 0;
                        for(int i=0; i<nBytesRead; i++)
                        {
                        	if(count==0 && audioFormat.isBigEndian())
                        		abData[i] = (byte) (abData[i]*volume);
                        	else if(count==bytesPerSample-1 && !audioFormat.isBigEndian())
                        		abData[i] = (byte) (abData[i]*volume);
                        	count++;
                        	count = count % bytesPerSample;
                        }
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
	private void playAsClip(String filename, boolean loop)
	{
		try {
			Clip clip = AudioSystem.getClip();
			AudioInputStream is = AudioSystem.getAudioInputStream(new File(filename));
			if(loop) clip.loop(clip.LOOP_CONTINUOUSLY);
			clip.open(is);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
}

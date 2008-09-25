/***************************
 * Brian Capps 
 * 04/27/2008
 * Description
 ****************************/
import javazoom.jlgui.basicplayer.*;
import java.io.File;
import java.io.PrintStream;
import java.util.Map;

public class Music implements BasicPlayerListener
{
	private File song;
	private BasicPlayer player;
	private BasicController control;
	private boolean isPaused, eom;
	private long time, lastUpdate;
	
	public Music()
	{
		//A default player
		
		song = new File("lp.mp3");
		// Instantiate BasicPlayer.
		player = new BasicPlayer();
		// BasicPlayer is a BasicController.
		control = (BasicController) player;
		player.addBasicPlayerListener(this);
		isPaused = false;
		time = 0;
		lastUpdate = 0;
		eom = false;
	}
	
	public Music(String fileName)
	{
		song = new File(fileName);
		// Instantiate BasicPlayer.
		player = new BasicPlayer();
		// BasicPlayer is a BasicController.
		control = (BasicController) player;
		player.addBasicPlayerListener(this);
		isPaused = false;
		time = 0;
		lastUpdate = 0;
		eom = false;
	}
	public boolean load()
	{
		try
		{
			control.open(song);
		}
		catch (BasicPlayerException e)
		{
			return false;
		}
		return true;
	}//end load
	public boolean play()
	{
		if(!isPaused)
		{
			try
			{
				control.play();
				// Set Pan (-1.0 to 1.0).
				if(System.getProperty("os.name").equals("Mac OS X"))
					control.setPan(-1.0); //completely Left - MAC fix.
				else
					control.setPan(0.0);
			}
			catch (BasicPlayerException e)
			{
				return false;
				//e.printStackTrace();
			}
		}//end if notPaused
		else if (isPaused)
		{
			try
			{
				control.resume();
				isPaused=false;
			}
			catch (BasicPlayerException e)
			{
				return false;
				//e.printStackTrace();
			}
		}
		return true;//successful play
	}//end play
	public boolean pause()
	{
		try
		{
			control.pause();
			isPaused = true;
		}
		catch (BasicPlayerException e)
		{
			return false;
			//e.printStackTrace();
		}
		return true;//successful pause
	}//end pause
	public boolean stop()
	{
		try
		{	
			control.stop();
		}
		catch (BasicPlayerException e)
		{
			return false;
			//e.printStackTrace();
		}
		return true;//successful pause
	}//end stop
	public boolean setGain(double g)
	{
		try
		{	
			control.setGain(g);
		}
		catch (BasicPlayerException e)
		{
			return false;
			//e.printStackTrace();
		}
		return true;
	}
	public long getTime()
	{
		if(!isPaused)
		{
			if(lastUpdate>System.currentTimeMillis())
				return time;
			else
				return time +(System.currentTimeMillis()-lastUpdate);
		}
		else
			return time;
	}
	public boolean isEnd()
	{
		return eom;
	}
	//METHODS REQUIRED BY LISTENER
	//CONTAIN POLLING INFO
	
	/**
	 * Open callback, stream is ready to play.
	 *
	 * properties map includes audio format dependant features such as
	 * bitrate, duration, frequency, channels, number of frames, vbr flag,
	 * id3v2/id3v1 (for MP3 only), comments (for Ogg Vorbis), ... 
	 *
	 * @param stream could be File, URL or InputStream
	 * @param properties audio stream properties.
	 */
	public void opened(Object stream, Map properties)
	{
		// Pay attention to properties. It's useful to get duration, 
		// bitrate, channels, even tag such as ID3v2.
		//System.out.println("opened : "+properties.toString());		
	}
		
	/**
	 * Progress callback while playing.
	 * 
	 * This method is called severals time per seconds while playing.
	 * properties map includes audio format features such as
	 * instant bitrate, microseconds position, current frame number, ... 
	 * 
	 * @param bytesread from encoded stream.
	 * @param microseconds elapsed (<b>reseted after a seek !</b>).
	 * @param pcmdata PCM samples.
	 * @param properties audio stream parameters.
	 */
	public void progress(int bytesread, long microseconds, byte[] pcmdata, Map properties)
	{
		// Pay attention to properties. It depends on underlying JavaSound SPI
		// MP3SPI provides mp3.equalizer.
			time = microseconds/1000;
			lastUpdate = System.currentTimeMillis();
	}
	
	/**
	 * Notification callback for basicplayer events such as opened, eom ...
	 *  
	 * @param event
	 */
	public void stateUpdated(BasicPlayerEvent event)
	{
		// Notification of BasicPlayer states (opened, playing, end of media, ...)
		if (event.getCode()==BasicPlayerEvent.EOM)
			eom = true;
	}

	/**
	 * A handle to the BasicPlayer, plugins may control the player through
	 * the controller (play, stop, ...)
	 * @param controller : a handle to the player
	 */	
	public void setController(BasicController controller){}
}//end class

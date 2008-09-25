import net.java.games.input.*;

public class InputListener
{
	private int drumPort;
	private boolean drumsAreConnected;
	private Controller[] ca;
	private boolean[] drumPressed;

	
	public boolean getDrums()
	{	
		drumPort = -1;
		drumsAreConnected = false;
		ca = ControllerEnvironment.getDefaultEnvironment().getControllers();
		
		for(int i=0;i<ca.length;i++) 
		{	
				
				if (ca[i].poll() && ca[i].getName().equals("Harmonix Drum Kit for PlayStation(R)3"))
				{
					drumsAreConnected = true;
					drumPort = i;
				}
		}

		return drumsAreConnected;
	}
	public boolean[] drumsPressed()
	{
		
		if (getDrums() && drumPort != -1)
		{	 
			Component[] components = ca[drumPort].getComponents();
			
			drumPressed = new boolean[21];
			for(int i=0; i<=20; i++)
				drumPressed[i] = false;
			
			for (int i=0; i<13; i++ )
			{
				if (components[i].getPollData()==1.0f)
					drumPressed[i] = true;
				else 
					drumPressed[i] = false;
			}

			if (components[13].getPollData()==Component.POV.UP)
				drumPressed[13] = true;
			
			else if (components[13].getPollData()==Component.POV.UP_RIGHT)
				drumPressed[14] = true;
			
			else if (components[13].getPollData()==Component.POV.RIGHT)
				drumPressed[15] = true;
				
			else if (components[13].getPollData()==Component.POV.DOWN_RIGHT)
				drumPressed[16] = true;
			
			else if (components[13].getPollData()==Component.POV.DOWN)
				drumPressed[17] = true;
				
			else if (components[13].getPollData()==Component.POV.DOWN_LEFT)
				drumPressed[18] = true;
				
			else if (components[13].getPollData()==Component.POV.LEFT)
				drumPressed[19] = true;
				
			else if (components[13].getPollData()==Component.POV.UP_LEFT)
				drumPressed[20] = true;
				
			fixDrums();
				
		return drumPressed;
		}//end if
		else
		{
			drumPressed = new boolean[21];
			for (int i=0; i<=20; i++)
				drumPressed[i] = false;
			return drumPressed;
		}
	}//end drumsPressed()
	public void fixDrums()
	{
		boolean[] tempDrums = new boolean[4];
		
		for(int i =0; i<4; i++)
			tempDrums[i] = drumPressed[i];
		
		drumPressed[0] = tempDrums[1];
		drumPressed[1] = tempDrums[0];
		drumPressed[2] = tempDrums[3];
		drumPressed[3] = tempDrums[2];//dirty fix for drum numbering
	}	
}//end class
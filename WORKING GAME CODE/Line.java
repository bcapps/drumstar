/***************************
 * Brian Capps 
 * 04/28/2008
 * Description
 ****************************/
import java.util.*;

public class Line
{
	private long time; //time in song
	private ArrayList<Note> noteList;
	private boolean inZone;
	
	public Line(long t, String states)
	{
		time = t;
		noteList = new ArrayList<Note>();
		inZone = false;
		
		for(int x=0; x<5; x++)
		{
			noteList.add(new Note());
			if(states.substring(x,x+1).equals("1"))
				noteList.get(x).setIsValid(true);//the note exists
			else
				noteList.get(x).setScored(true);//unused notes are automatically set to "scored"
		}
	}
	public long getTime()
	{
		return time;
	}
	public void setTime(long t)
	{
		time = t;
	}
	public ArrayList<Note> getNotes()
	{
		return noteList;
	}
	
	public void setNotes(ArrayList<Note> n)
	{
		noteList = n;
	}
	public void setInZone(boolean z)
	{
		inZone = z;
	}
	public boolean getInZone()
	{
		return inZone;
	}
}

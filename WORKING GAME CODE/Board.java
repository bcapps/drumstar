/***************************
 * Brian Capps 
 * 04/27/2008
 * Description
 ****************************/
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.*;
import com.sun.opengl.util.j2d.TextRenderer;
import java.awt.*;
import java.awt.Font;
import java.awt.geom.*;
import java.awt.event.*;
import java.io.*;
import java.io.File;
import java.util.*;
import java.util.Scanner;
import com.sun.opengl.util.texture.*;
import com.sun.opengl.util.j2d.Overlay;

public class Board implements KeyListener
{
	private String mp3File, artist, album;//song info
	private ArrayList<Line> lines;//stores all the Lines for the song
	private Music song, missed, boo, cheer;//sound(s)
	private int score, crowdRating;
	private int multiplyer, multiplyerNum;//multiplyer, number of notes hit in a row
	private boolean[] drums, drumsPrevious;//"Previous" arrays store the last keys pressed, in order to
	private boolean[] keys, keysPressed, keysPrevious;//make sure the user is not just holding down keys.
	private boolean[] lineScoreChecked;//if the line was checked yet for scores
	private int redDrumKey, yellowDrumKey, blueDrumKey, 
						greenDrumKey, orangeDrumKey;//the keyboard keycodes for all the drums
	
	private TextRenderer scoreRenderer, multiplyerRenderer, crowdRenderer;
	private Font surroundingFont72, surroundingFont48;//custom font
	
	private int timeIntervalDisplayed; //number of milliseconds on the board.
	private int errorTimeMargin; //number of milliseconds to still accept a note. bidirectional.
	private float correctLine; //location of buttons to press.
	private float boardLength, noteSize;
	float[][] colors;//stores the default colors for each corresponding note .
	
	private int pixelWidth, pixelHeight;//current height/width of window in pixels	
	private long initialEndTime, currentEndTime;
	private boolean songWasOver, songIsStopped;//if initial time for end of song timer, if song is stopped
	//test values///////////////////////
	//private float y1 =1.9000014f, z1=0.35450974f; 
	//private float y1 = 5.050002f, z1= 5.2045116f;
	//private float y1 = 5.550004f, z1 = 3.2045078f;
	//private float y1 = 2.5499997f, z1=1.1045098f;
	private float y1 = 2.35f, z1 =  2.2045088f;
	//2.2 -0.44549027
	
	
	////////////////////////////////////
	
	public Board() 
	{
		Game.frame.addKeyListener(this);//this probably breaks several OOP commandments,
		Game.canvas.addKeyListener(this);//but it's much easier this way :P
		//time = 0;
		lines = new ArrayList<Line>();
		song = null;
		noteSize = 0.4f;
		score = 0;
		multiplyer = 1;
		multiplyerNum = 0;
		missed = null;
		boo = null;
		cheer = null;
		crowdRating = 100;
		songIsStopped = false;
		
		timeIntervalDisplayed = 4000;
		errorTimeMargin = 150;
		correctLine = -5f;
		boardLength = 20f;
		pixelWidth = Game.frame.getWidth();//just the inital values, to be changed below,
		pixelHeight = Game.frame.getHeight();//in setPixel methods
		songWasOver = false;
		initialEndTime = 0;
		currentEndTime = 0;
		
		try//create a font based on a TTF (or OTF renamed to .TTF) file
		{
			surroundingFont72 = Font.createFont(Font.TRUETYPE_FONT,new File("SurroundingBold.ttf"));
			surroundingFont72 = surroundingFont72.deriveFont(Font.PLAIN, 72f);
			surroundingFont48 = surroundingFont72.deriveFont(Font.PLAIN, 48f);
		}catch(Exception e){}
		
		scoreRenderer = new TextRenderer(surroundingFont48, true, true);//new Font("SansSerif", Font.PLAIN, 48)
		multiplyerRenderer = new TextRenderer(surroundingFont72, true, true);
		crowdRenderer = new TextRenderer(surroundingFont48, true, true);
		
		redDrumKey = KeyEvent.VK_V; 
		yellowDrumKey = KeyEvent.VK_G; 
		blueDrumKey = KeyEvent.VK_H; 
		greenDrumKey = KeyEvent.VK_N;
		orangeDrumKey = KeyEvent.VK_SPACE;
		
		colors= new float[][]
									{
									{255f,0f,0f},
									{255f,255f,0f},
									{0f,0f,255f},
									{0f,255f,0f},
									{255f,255f,0f}//filler
									};
		drums = new boolean[21];
		drumsPrevious=InputListener.drumsPressed();
		keysPressed = new boolean[5]; 
		keys = new boolean[5];
		keysPrevious = this.getKeysPressed();
	}
	public void addToMultiplyer()
	{
		multiplyerNum += 1;
		crowdRating +=1*multiplyer;
		
		if(multiplyerNum==10)
			multiplyer = 2;
		else if (multiplyerNum==20)
			multiplyer = 3;
		else if (multiplyerNum==30)
			multiplyer = 4;
			
		if(crowdRating > 100)
			crowdRating = 100;
	}
	public void resetMultiplyer()
	{
		multiplyerNum = 0;
		multiplyer = 1;
		crowdRating -=2;
		
		if(crowdRating < 0)
			crowdRating = 0;
	}
	
	//MUSIC METHODS
	public void loadData(String fileName) 
	{
		try
		{
			Scanner txt = new Scanner(new File(fileName));
			mp3File = txt.nextLine();
			artist = txt.nextLine();
			album = txt.nextLine();
			while(txt.hasNext())
			{
				long noteTime = txt.nextLong();
				String states = txt.next().trim();
				lines.add(new Line(noteTime, states));
			}
			
			for(int i = 0; i<lines.size();i++)//check if there are any duplicate lines with different notes,
				for (int j = 0; j<lines.size();j++)//then condense into one line, eliminating
				 	if(i!=j && lines.get(i).getTime() == lines.get(j).getTime())//the duplicates
					{
						for(int k = 0; k<5; k++)
							if(lines.get(j).getNotes().get(k).getIsValid())
							{
								lines.get(i).getNotes().get(k).setIsValid(true);
								lines.get(i).getNotes().get(k).setScored(false);
							}
						lines.remove(lines.get(j));
					}//end if	
			
			song = new Music("Songs/"+mp3File);//set the song location
			
			lineScoreChecked = new boolean[lines.size()];//array for use in score-checking later on
			for (int i=0; i< lineScoreChecked.length; i++)
				lineScoreChecked[i] = false;
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}//end loadData
	public void loadSounds()
	{
		song.load();
		missed = new Music("missed1.mp3");
		missed.load();
	}
	public void playSong()
	{
		song.play();
	}
	public void pauseSong()
	{
		song.pause();
	}
	public void stopSong()
	{
		song.stop();
	}//END MUSIC METHODS
	
	//DRAW EVERYTHING - where the magic happens
	public void draw(GLAutoDrawable gLDrawable) 
	{		
		drums = InputListener.drumsPressed();
		keys = this.getKeysPressed();
		
		//Start 3d Rendering	
		GL gl = gLDrawable.getGL();
		
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
        
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		
		gl.glLoadIdentity();
		
		GLU glu = new GLU();
		
		/*
		//Code to adjust camera
		if(keys[0])
			z1 -= .05;
		if(keys[1])
			z1 += .05;
		
		if(keys[2])
			y1 -= .05;
		if(keys[3])
			y1 += .05;
		if(keys[4])
		System.out.println(y1+" "+z1);
		*/
		
		glu.gluLookAt(0,y1, z1, 0,0,-3, 0,1,0);
		
		gl.glPushMatrix();
		gl.glEnable(GL.GL_BLEND);
		gl.glBegin(GL.GL_QUADS);
		
		//Draw the Board
		//x goes basically from -1 to 1(camera changed tho, so -3 to 3)
		//y stays same
		//boardLength is -z value
		gl.glColor4f(40/256f,100/256f,150/256f,1f);//R,G,B,A
		gl.glVertex3f(-3f,-4f, 0f);//x,y,z
		
		gl.glColor4f(40/256f,100/256f,150/256f,1f);
		gl.glVertex3f(3f,-4f, 0f);
		
		gl.glColor4f(60/256f,150/256f,200/256f,0f);
		gl.glVertex3f(3f,-4f, -boardLength);
		
		gl.glColor4f(60/256f,150/256f,200/256f,0f);
		gl.glVertex3f(-3f,-4f, -boardLength);
		
		//All y values on top of the Board must have at least
		//0.0001f added for some reason
		//Bottom bar - Orange
		if(drums[4] || keys[4])//if drums or keyboard
			gl.glColor4f(255/255f,255/255f,255/255f, 1f);
		else
			gl.glColor4f(255/255f,150/255f,0/255f, 1f);
		gl.glVertex3f(-3f,-4f+.0001f, correctLine + noteSize/2 + (noteSize/3) );//close left
		gl.glVertex3f(3f,-4f+.0001f, correctLine + noteSize/2 + (noteSize/3) );//close right
		gl.glVertex3f(3f,-4f+.0001f, correctLine - noteSize/2 - (noteSize/3));//far right
		gl.glVertex3f(-3f,-4f+.0001f, correctLine - noteSize/2 - (noteSize/3));//far left
		
		//RedNote
		if(drums[0] || keys[0])
			gl.glColor4f(255/255f,255/255f,255/255f, 1f);
		else
			gl.glColor4f(1f,0f,0f, 1f);
		gl.glVertex3f(-3f,-4f+.001f, correctLine + noteSize/2);
		gl.glVertex3f(-1.5f,-4f+.001f, correctLine + noteSize/2);
		gl.glVertex3f(-1.5f,-4f+.001f, correctLine - noteSize/2);
		gl.glVertex3f(-3f,-4f+.001f, correctLine - noteSize/2);
		
		//YellowNote
		if(drums[1] || keys[1])
			gl.glColor4f(255/255f,255/255f,255/255f, 1f);
		else
			gl.glColor4f(1f,1f,0f, 1f);
		gl.glVertex3f(-1.5f,-4f+.001f, correctLine + noteSize/2);
		gl.glVertex3f(0f,-4f+.001f, correctLine + noteSize/2);
		gl.glVertex3f(0f,-4f+.001f, correctLine - noteSize/2);
		gl.glVertex3f(-1.5f,-4f+.001f, correctLine - noteSize/2);
		//BlueNote
		if(drums[2] || keys[2])
			gl.glColor4f(255/255f,255/255f,255/255f, 1f);
		else
			gl.glColor4f(0f,0f,1f, 1f);
		gl.glVertex3f(0f,-4f+.001f, correctLine + noteSize/2);
		gl.glVertex3f(1.5f,-4f+.001f, correctLine + noteSize/2);
		gl.glVertex3f(1.5f,-4f+.001f, correctLine - noteSize/2);
		gl.glVertex3f(0f,-4f+.001f, correctLine - noteSize/2);
		//GreenNote
		if(drums[3] || keys[3])
			gl.glColor4f(255/255f,255/255f,255/255f, 1f);
		else
			gl.glColor4f(0f,1f,0f, 1f);
		gl.glVertex3f(1.5f,-4f+.001f, correctLine + noteSize/2);
		gl.glVertex3f(3f,-4f+.001f, correctLine + noteSize/2);
		gl.glVertex3f(3f,-4f+.001f, correctLine - noteSize/2);
		gl.glVertex3f(1.5f,-4f+.001f, correctLine - noteSize/2);
		//End Bottom Bar
		
		//Render notes
		boolean notesInZone = false;//whether there are currently any notes in the Zone
		
		for(int i = 0; i< lines.size(); i++)
		{
			if(lines.get(i).getTime()<=song.getTime() + timeIntervalDisplayed* 
				((boardLength+correctLine)/boardLength)&&
				lines.get(i).getTime() >= song.getTime() - timeIntervalDisplayed*
				(-correctLine/boardLength))//if the line is on the board...
			{
								
				if(lines.get(i).getInZone())//if the line is in the score zone
				{
					notesInZone = true;//notes appear in the Zone in this loop
					
					for(int s = 0; s < 5; s++)
					{
						if(((drums[s] && drums[s] != drumsPrevious[s]) ||
							(keys[s] && keys[s] != keysPrevious[s])) && 
							!lines.get(i).getNotes().get(s).getIsValid())//if the pressed notes don't exist
							{	//if the nonexistant notes are pressed...
								resetMultiplyer();
								for(int n=0; n < 5; n++)//then set all the notes in the line to "missed"
									lines.get(i).getNotes().get(n).setMissed(true);
							}//end if (drums || keys)
					}//end for			
				}//end if line is in score zone
				
				this.renderNotes(gLDrawable, i);//draw all the notes in the line
				
			}//end if lines are in board area
		}//end note rendering/for loop
		if(!notesInZone && !song.isEnd())//if notes are hit when no note is in the zone + song isn't over, 
			for(int n = 0; n < 5; n++)//then reset the multiplyer
			{
				if((drums[n] && drums[n] != drumsPrevious[n]) ||
					(keys[n] && keys[n] != keysPrevious[n]))
				{	
					resetMultiplyer();	
				}//end if pressed
			}//end for (int n)	
				
		gl.glEnd();		
		gl.glDisable(GL.GL_BLEND);	
		gl.glPopMatrix();
		
		//Draw HUD
		glEnable2D(gl);
		gl.glBegin(GL.GL_QUADS);
		gl.glColor4f(0f,0f,0f, .6f);
		//draw score box
		gl.glVertex2i(getPixelWidth() - getPixelWidth()/6, getPixelHeight()/6);//top left
		gl.glVertex2i(getPixelWidth(), getPixelHeight()/6);//top right
		gl.glVertex2i(getPixelWidth(), getPixelHeight()/4);
		gl.glVertex2i(getPixelWidth() - getPixelWidth()/6, getPixelHeight()/4);
		//draw crowdRating boxes
		gl.glVertex2f(0f, getPixelHeight()/4f);
		gl.glVertex2f(getPixelWidth()/30f, getPixelHeight()/4f);
		gl.glVertex2f(getPixelWidth()/30f, 3f*getPixelHeight()/4f);
		gl.glVertex2f(0f, 3f*getPixelHeight()/4f);

		if (crowdRating >=80)
			gl.glColor4f(0f,1f,0f, .9f);
		else if (crowdRating <80 && crowdRating>30)
			gl.glColor4f(1f,1f,0f, .9f);
		else 
			gl.glColor4f(1f,0f,0f, .9f);
			
		float meterHeight = (crowdRating/100f) * (3f*getPixelHeight()/4f - getPixelHeight()/4f);
		
		gl.glVertex2f(0f, 3f*getPixelHeight()/4f-meterHeight);
		gl.glVertex2f(getPixelWidth()/30f, 3f*getPixelHeight()/4f-meterHeight);
		gl.glVertex2f(getPixelWidth()/30f, 3f*getPixelHeight()/4f);
		gl.glVertex2f(0f, 3*getPixelHeight()/4f);
		//draw MultiplyerNum boxes
		gl.glColor4f(0f,0f,0f, .6f);
		gl.glVertex2f(getPixelWidth()/2f - 50f, getPixelHeight()/10f+20f);
		gl.glVertex2f(getPixelWidth()/2f+50f, getPixelHeight()/10f+20f);
		gl.glVertex2f(getPixelWidth()/2f+50f, getPixelHeight()/9f+20f);
		gl.glVertex2f(getPixelWidth()/2f - 50f, getPixelHeight()/9f+20f);
		
		gl.glColor4f(246f/255f,242f/255f,11f/255f, .9f);
		float barWidth = 0f;
		if(multiplyerNum== 0)
			 barWidth = 0f;
		else if(multiplyerNum % 10 == 0 || multiplyerNum>=30)
			 barWidth = 100f;
		else
		{
			String floatToParse = ""+multiplyerNum;
			String num = floatToParse.substring(floatToParse.length() - 1);
			float newNum = (float) Integer.parseInt(num);
			barWidth = (newNum /10f)*(100f);
		}
		gl.glVertex2f(getPixelWidth()/2f - 50f, getPixelHeight()/10f+20f);
		gl.glVertex2f(getPixelWidth()/2f - 50f + barWidth, getPixelHeight()/10f+20f);
		gl.glVertex2f(getPixelWidth()/2f - 50f + barWidth, getPixelHeight()/9f+20f);
		gl.glVertex2f(getPixelWidth()/2f - 50f, getPixelHeight()/9f+20f);
		
						
		gl.glEnd();
		
		glDisable2D(gl);
		//end HUD
		
		//draw multiplyer text
		multiplyerRenderer.beginRendering(getPixelWidth(), getPixelHeight());
		multiplyerRenderer.setColor(0.0f, 0.0f, 0.0f, 1.0f);
		multiplyerRenderer.draw("x"+multiplyer, (int) (getPixelWidth()/2 - 
									multiplyerRenderer.getBounds("x"+multiplyer).getWidth()/2),
									(int)getPixelHeight() - getPixelHeight()/10);
		
		multiplyerRenderer.endRendering();
		//draw crowdRating text
		crowdRenderer.beginRendering(getPixelWidth(), getPixelHeight());
		crowdRenderer.setColor(0.0f, 0.0f, 0.0f, 1.0f);
		crowdRenderer.draw(""+crowdRating, (int) getPixelWidth()/19, 
								(int)(getPixelHeight()/4f+meterHeight-
								(crowdRenderer.getBounds(""+crowdRating).getHeight()/2)));
		crowdRenderer.endRendering();
		//draw score text
		scoreRenderer.setColor(1.0f, 1.0f, 1.0f, 1.0f);
		scoreRenderer.beginRendering(getPixelWidth(), getPixelHeight());
		scoreRenderer.draw(""+score, (int) (getPixelWidth() - (getPixelWidth()/6)/2 - 
							scoreRenderer.getBounds(""+score).getWidth()/2), 
							(int) ((getPixelHeight() - getPixelHeight()/4 + getPixelHeight() - 
							getPixelHeight()/6) /2 - scoreRenderer.getBounds(""+score).getHeight()/2));
							//gets the middle of the w/h of the box minus the w/h of the String
		scoreRenderer.endRendering();
		
		//method checks if song is over, and then waits before displaying the final stats, etc.
		if(song.isEnd())
		{
			if(!songWasOver)//if this is the first loop that the song was over
			{
				initialEndTime = System.currentTimeMillis();
				songWasOver = true;
			}
			else
			{
				currentEndTime = System.currentTimeMillis();
				if (currentEndTime >= initialEndTime + timeIntervalDisplayed/2)//if more than _ seconds have passed
				{
					songEndFunctions();
				}
			}
		}//end if (song is over)
		//method checks if song is failed (crowd rating reaches 0)
		if(crowdRating<=0)
		{
			if(!songIsStopped)
			{
				pauseSong();
				initialEndTime = System.currentTimeMillis();
				songIsStopped = true;
			}
			currentEndTime = System.currentTimeMillis();
			songFailedFunctions();
		}
		
		
		keysPrevious = (boolean[]) keys.clone();//set the "previous" arrays to the last configuration at the end
		drumsPrevious = drums;//of the loop.
	}
	public void renderNotes(GLAutoDrawable gLD, int x)
	{
		ArrayList<Note> notes = new ArrayList<Note>();//temporary arraylist
		
		float z = -( ( (lines.get(x).getTime()+(noteSize/2/boardLength*timeIntervalDisplayed)) - song.getTime() + 
						(-correctLine/boardLength*timeIntervalDisplayed) )/ 
						timeIntervalDisplayed   *    boardLength);//ratio of note distance in millis * boardLength
		
		for (int j = 0; j<5; j++)//going to cycle through all the notes to see if/how they should be rendered
		{
			notes.add(lines.get(x).getNotes().get(j));//temp array just to make less writing :P
			if(notes.get(j).getIsValid())//if the note exists
			{
				notes.get(j).setColors(colors[j][0], colors[j][1], colors[j][2]);//set each note to default colors
				
				if(z+noteSize >= correctLine-(errorTimeMargin/timeIntervalDisplayed*boardLength)
						- noteSize/2 && //if the bottom is in area
					z <=correctLine+(errorTimeMargin/timeIntervalDisplayed*boardLength)
						+ noteSize/2 && //and if top is in area
					!notes.get(j).getMissed()  )//and if the note wasnt set to missed
				{
					lines.get(x).setInZone(true);
					
					if( (drums[j] && drums[j] != drumsPrevious[j]) ||//if drums hit note or...if keyboard does
						(keys[j] && keys[j] != keysPrevious[j]) )

					{
						notes.get(j).setScored(true);//scored this note
						
						score += 2*multiplyer;
						addToMultiplyer();
					}//end  if (drums | keyboard)								
				}//end if(z is in the area)
				else
					lines.get(x).setInZone(false);
				//if missed set the notes to Missed = true
				if (z > correctLine+(errorTimeMargin/timeIntervalDisplayed*boardLength) 
						 + noteSize/2 && !notes.get(j).getScored())//if note missed
				{
					if(!notes.get(j).getMissed())//if this the first miss of this note
					{
						resetMultiplyer();
					}
						
					notes.get(j).setMissed(true);
				}
					
				if (!notes.get(j).getScored())//if notes are OK to be drawn...
					if(notes.get(j).getMissed())//if missed note
						if(j<4)
						{
							notes.get(j).setColors(127f, 127f, 127f);
							notes.get(j).draw(gLD, -3f + (1.5f*j), -4f, z, noteSize);
						}
						else
							notes.get(j).drawBar(gLD, z, noteSize, true);
					else //if not missed...
						if(j<4)
							notes.get(j).draw(gLD, -3f + (1.5f*j), -4f, z, noteSize);
						else
							notes.get(j).drawBar(gLD, z, noteSize, false);
			}//end if Valid
		}//end for loop
		
		lines.get(x).setNotes(notes);//exports the temporary "notes" array into the stored Line
		
		if(!lineScoreChecked[x])//if score for this line hasn't already been added
		{
			boolean allScored = true;
			for (int l = 0; l<lines.get(x).getNotes().size(); l++)
			{
				if(!lines.get(x).getNotes().get(l).getScored()) //if any were not scored
					allScored = false;
			}
				
			if(allScored)//if all notes were correctly hit
			{
				score+= 10*multiplyer;
				lineScoreChecked[x] = true;
			}	
		}//end if line scored checked
	}//end renderNotes
	
	
	public void songEndFunctions()
	{
		if(currentEndTime < initialEndTime + timeIntervalDisplayed/2 + 4000)//display text for first 3 seconds
		{
			multiplyerRenderer.setColor(1.0f, 0.0f, 0.0f, 1.0f);
			multiplyerRenderer.beginRendering(getPixelWidth(), getPixelHeight());
			multiplyerRenderer.draw("SONG PASSED!", (int) (getPixelWidth() - getPixelWidth()/2 - 
									(multiplyerRenderer.getBounds("SONG PASSED!").getWidth()/2)),//w center
									(int) (getPixelHeight() - getPixelHeight()/2 - 
									(multiplyerRenderer.getBounds("SONG PASSED!").getHeight()/2)));//h center
			multiplyerRenderer.endRendering();
		}//end if (time< 3 seconds)
		//else...show stats!
	}
	public void songFailedFunctions()
	{
		if(currentEndTime < initialEndTime + timeIntervalDisplayed/2 + 4000)//display text for first 3 seconds
		{
			multiplyerRenderer.setColor(1.0f, 0.0f, 0.0f, 1.0f);
			multiplyerRenderer.beginRendering(getPixelWidth(), getPixelHeight());
			multiplyerRenderer.draw("SONG FAILED!", (int) (getPixelWidth() - getPixelWidth()/2 - 
									(multiplyerRenderer.getBounds("SONG FAILED!").getWidth()/2)),//w center
									(int) (getPixelHeight() - getPixelHeight()/2 - 
									(multiplyerRenderer.getBounds("SONG FAILED!").getHeight()/2)));//h center
			multiplyerRenderer.endRendering();
		}//end if (time< 3 seconds)
		//else ... display some options!!!
	}
	
	//METHODS FOR DRAWING ON A "2D" coord's system
	public void glEnable2D(GL gl)
	{
		int[] vPort = new int[4];
		gl.glGetIntegerv(GL.GL_VIEWPORT, vPort, 0);
		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glPushMatrix();
		gl.glLoadIdentity();
		gl.glOrtho(vPort[0], vPort[0]+vPort[2], vPort[1]+vPort[3], vPort[1], -1, 1);
		gl.glMatrixMode(GL.GL_MODELVIEW);
		gl.glPushMatrix();
		gl.glLoadIdentity();
		gl.glEnable(GL.GL_BLEND);
	}
	public void glDisable2D(GL gl)
	{
		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glPopMatrix();
		gl.glMatrixMode(GL.GL_MODELVIEW);
		gl.glPopMatrix();
	}//END HUD METHODS
	
	//methods for updating coords
	public void setPixelWidth(int w)
	{
		pixelWidth = w;
	}
	public int getPixelWidth()
	{
		return pixelWidth;
	}
	public void setPixelHeight(int h)
	{
		pixelHeight = h;
	}
	public int getPixelHeight()
	{
		return pixelHeight;
	}
	
	//KEYLISTENER METHODS
	public void keyPressed(KeyEvent e)//if the keys are pressed, set them to pressed in the array
    {	
        if(e.getKeyCode() == redDrumKey)
          	keysPressed[0] = true;
        if(e.getKeyCode() == yellowDrumKey)
         	keysPressed[1] = true;
		if(e.getKeyCode() == blueDrumKey)
       		keysPressed[2] = true;
 		if(e.getKeyCode() == greenDrumKey)
            keysPressed[3] = true;     
    	if(e.getKeyCode() == orangeDrumKey)
            keysPressed[4] = true;
    }
    public void keyReleased(KeyEvent e)//if they're released, set them to not pressed
    {
		if(e.getKeyCode() == redDrumKey)
          	keysPressed[0] = false;
        if(e.getKeyCode() == yellowDrumKey)
         	keysPressed[1] = false;
		if(e.getKeyCode() == blueDrumKey)
       		keysPressed[2] = false;
 		if(e.getKeyCode() == greenDrumKey)
            keysPressed[3] = false;     
    	if(e.getKeyCode() == orangeDrumKey)
            keysPressed[4] = false;
	}
    public boolean[] getKeysPressed()
	{
		return keysPressed;	
	} 
    public void keyTyped(KeyEvent e){}
}//end class

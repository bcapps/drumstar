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
import java.awt.Font;
import java.awt.geom.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import com.sun.opengl.util.texture.*;

public class Board implements GLEventListener
{
	private String mp3File, artist, album;
	private long time, timeGap;
	private ArrayList<Line> lines;
	private Music song;
	private boolean songIsPlaying, firstTime;
	private int lowestNoteToProcess, lowestNoteToRender;
	private int score;
	private boolean[] key;
	private long dt_timer;
	
	private int fretDuration; //number of milliseconds on the fret
	private int noteErrorDuration; //number of milliseconds to still accept a note. bidirectional.
	private float targetPos; //location of buttons to press
	private float length;
	float[][] colors;
	Note noteToDraw;
								
	private GLU glu = new GLU();
	private TextRenderer renderer;
	
	//test values///////////////////////
	//private float y =0.8624921f , z=-2.3839877f;
	//1.9000014 0.35450974
	private long previous;
	private float y =1.9000014f, z=0.35450974f; 
	
	public static Input1 input = new Input1();
	private File menuBG = new File (System.getProperty("user.dir")+"/menuBG.png");
  	private Texture texture;
	private float zTest;
	private Note redNote= new Note(255f, 0f, 0f), yellowNote= new Note(255f, 255f, 0f),
					blueNote =  new Note(0f, 0f, 255f), greenNote = new Note(0f, 255f, 0f),
					orangeNote = new Note();
	
	private long oldSongTime = 0;
	private long oldTime = 0;
	private long updateTime = 0;
	////////////////////////////////////
	
	public Board()
	{
		time = 0;
		lines = new ArrayList<Line>();
		zTest = -10f;
		song = null;
		songIsPlaying = false;
		firstTime = true;
		lowestNoteToProcess=0; 
		lowestNoteToRender=0;
		
		fretDuration = 4000;
		noteErrorDuration = 100;
		targetPos = -2.5f;
		length = 10f;
		dt_timer = 0;
		timeGap = 0;
		
		colors= new float[][]
									{
									{255f,0f,0f},
									{255f,255f,0f},
									{0f,0f,255f},
									{0f,255f,0f},
									{255f,255f,0f}//filler
									};
		key = new boolean[21];
	}
	public void loadData(String file)
	{
		try
		{
			Scanner txt = new Scanner(new File(file));
			mp3File = txt.nextLine();
			artist = txt.nextLine();
			album = txt.nextLine();
			while(txt.hasNext())
			{
				long noteTime = txt.nextLong();
				int lengthThrowaway = txt.nextInt();
				String states = txt.next().trim();
				lines.add(new Line(noteTime, states));
			}
			song = new Music(mp3File);
			song.load();
		}
		catch(Exception e) {
			System.out.println(e);
		}
	}//end loadData
	public void playSong()
	{
		song.play();
	}
	public void pauseSong()
	{
		song.pause();
	}
	public void display(GLAutoDrawable gLDrawable) 
	{	
		key = Input.keysPressed();
		
		double dt = (System.currentTimeMillis() - dt_timer )/1000.0;
		dt_timer = System.currentTimeMillis();
		
		//Start 3d Rendering	
		GL gl = gLDrawable.getGL();
		
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
        
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		
		gl.glLoadIdentity();
		
		GLU glu = new GLU();
		
		//Code to adjust camera
		if(input.getKey(KeyEvent.VK_UP))
			z -= .5*dt;
		if(input.getKey(KeyEvent.VK_DOWN))
			z += .5*dt;
		
		if(input.getKey(KeyEvent.VK_LEFT))
			y -= .5*dt;
		if(input.getKey(KeyEvent.VK_RIGHT))
			y += .5*dt;
		if(input.getKey(KeyEvent.VK_SPACE))
		System.out.println(y+" "+z);
		
		
		
		glu.gluLookAt(0,y, z, 0,0,-3, 0,1,0);
		
		//orangeNote.drawBar(gLDrawable, zTest);
		//redNote.draw(gLDrawable, -3f, -4f, zTest);
		//yellowNote.draw(gLDrawable, -1.5f, -4f, zTest);
		//blueNote.draw(gLDrawable, 0f, -4f, zTest);
		//greenNote.draw(gLDrawable, 1.5f, -4f, zTest);
		zTest+= 0.005f;
		if (zTest >-2f)
			zTest=-10f;

		gl.glPushMatrix();
		gl.glEnable(GL.GL_BLEND);
		//gl.glRotatef(70,1,-2,1);
		
		gl.glBegin(GL.GL_QUADS);
		//Draw the Board
		//x goes basically from -1 to 1(camera changed tho)
		//y stays same
		//board length is -z
		gl.glColor4f(40/256f,100/256f,150/256f,1f);//R,G,B,A
		gl.glVertex3f(-3f,-4f, 0f);//x,y,z
		
		gl.glColor4f(40/256f,100/256f,150/256f,1f);
		gl.glVertex3f(3f,-4f, 0f);
		
		gl.glColor4f(60/256f,150/256f,200/256f,0f);
		gl.glVertex3f(3f,-4f, -10f);
		
		gl.glColor4f(60/256f,150/256f,200/256f,0f);
		gl.glVertex3f(-3f,-4f, -10f);
		
		//All y values on top of the Board must have at least
		//0.0001f added for some reason
		//Bottom bar - Orange
		gl.glColor4f(255/256f,165/256f,0/256f, 1f);
		gl.glVertex3f(-3f,-4f+.0001f, -2.15f);//close left
		gl.glVertex3f(3f,-4f+.0001f, -2.15f);//close right
		gl.glVertex3f(3f,-4f+.0001f, -2.85f);//far right
		gl.glVertex3f(-3f,-4f+.0001f, -2.85f);//far left
		//RedNote
		gl.glColor4f(1f,0f,0f, 1f);
		gl.glVertex3f(-3f,-4f+.001f, -2.25f);
		gl.glVertex3f(-1.5f,-4f+.001f, -2.25f);
		gl.glVertex3f(-1.5f,-4f+.001f, -2.75f);
		gl.glVertex3f(-3f,-4f+.001f, -2.75f);
		//YellowNote
		gl.glColor4f(1f,1f,0f, 1f);
		gl.glVertex3f(-1.5f,-4f+.001f, -2.25f);
		gl.glVertex3f(0f,-4f+.001f, -2.25f);
		gl.glVertex3f(0f,-4f+.001f, -2.75f);
		gl.glVertex3f(-1.5f,-4f+.001f, -2.75f);
		//BlueNote
		gl.glColor4f(0f,0f,1f, 1f);
		gl.glVertex3f(0f,-4f+.001f, -2.25f);
		gl.glVertex3f(1.5f,-4f+.001f, -2.25f);
		gl.glVertex3f(1.5f,-4f+.001f, -2.75f);
		gl.glVertex3f(0f,-4f+.001f, -2.75f);
		//GreenNote
		gl.glColor4f(0f,1f,0f, 1f);
		gl.glVertex3f(1.5f,-4f+.001f, -2.25f);
		gl.glVertex3f(3f,-4f+.001f, -2.25f);
		gl.glVertex3f(3f,-4f+.001f, -2.75f);
		gl.glVertex3f(1.5f,-4f+.001f, -2.75f);
		//End Bottom Bar
		
		this.renderNotes(gLDrawable, dt);

		/////////////////////////////////////
		gl.glEnd();		
		
		gl.glDisable(GL.GL_BLEND);	
		gl.glPopMatrix();
		
		try { Thread.sleep(1); } catch (Exception e) {}
	}
	public void renderNotes(GLAutoDrawable gLDrawable, double dt)
	{
		//RENDER NOTES////////////////////////
		
		
		/*  OLD CODE TO CHECK/UPDATE TIME
		long songTime = song.getTime();
		long milliTime = System.currentTimeMillis();
		if(firstTime)
		{
			time = songTime;
			firstTime = false;
		}
		else 
		{
			if(songTime == oldSongTime)
			{
				updateTime += milliTime-oldTime;
				System.out.println("update time: "+updateTime);
					
			}
			else
			{
				if (songTime == oldSongTime + updateTime)
					System.out.println("WINWINWINWIWNWINWIWNWIN");
				else
					System.out.println("Difference: "+(songTime-oldSongTime - updateTime));
						
						
				updateTime = 0;
				System.out.println("New Time: "+time);
			}
			time = songTime + updateTime;
		}//end else
			
			oldSongTime = songTime;
			oldTime = milliTime;*/
		time = song.getTime();	
		for(int i=lowestNoteToProcess; i<lines.size(); i++)
		{
			Line line = lines.get(i);
			if(line.getTime() - noteErrorDuration > time)
				break;
			if(line.getState()==0) //not pressed
			{
				if(time > line.getTime() + noteErrorDuration) //missed line
				{
					//System.out.println("missed line");
					line.setState(3);
					score -= 1;
					lowestNoteToProcess++;
				}
			}							//code below takes care of this
		
		}//end for
		
			//find closest line in bounds to be pressed
			//if a line exists
				//see if correct key combo was pressed
					//do the thing
				//else
					//play a bad line sound
			//if it doesnt exist
				//play a bad line sound
			Line closest = null;
			long closestDistance = 1000000;
			for(int i=lowestNoteToProcess; i<lines.size(); i++)
			{
				Line n = lines.get(i);
				if(n.getTime() - noteErrorDuration > time)
					break;
				if(n.getState() == 1) //user is holding down this line, so it is the only one that can be processed
				{
					closest = n;
					break;
				}
				if(Math.abs(time - n.getTime()) <= closestDistance && time >= n.getTime() - noteErrorDuration && time <= n.getTime() + noteErrorDuration)
				{
					closest = n;
					closestDistance = (long)Math.abs(time - n.getTime());
				}
			}
			if(closest != null)
			{
				if(closest.getState() == 0) //not pressed
				{
					boolean seq = true;
					for(int x=0; x<5; x++)
						if(key[x] != closest.getNotes()[x])
						{ seq = false; break; }
					if(seq)
					{
						//System.out.println("pressed button");
						closest.setState(2); //pressed button
						lowestNoteToProcess++;
					} 
					score += 1;
				} 
				else 
				{
						//play bad line sound
				}
			} 
				/*else if(closest.getState() == 1) 
				{ //holding and strummed, cant do that
					closest.getState() = 2;
					System.out.println("you interrupted the holding");
					lowestNoteToProcess++;
					//play bad line sound
				}*/
			//} 
			else //(if closest == null)
			{
				//play bad line sound
			}
		
		//Part 2
		for(int i=lowestNoteToRender; i<lines.size(); i++)
		{
			Line line = lines.get(i);
			float posz = (line.getTime() + -targetPos/length*fretDuration - time) / fretDuration * length; //head
			if(posz > length) break; //not rendered yet
			float posz2 = (line.getTime()+ -targetPos/length*fretDuration - time) / fretDuration * length; //tail
			if(posz2 <= 1) //will never be rendered again
			{
				lowestNoteToRender++;
				continue;
			}
			if(posz <= length)
				for(int x=0; x<5; x++)
				{
					if(!line.getNotes()[x]) continue;
					if(line.getState() == 2) continue;//pressed
					
					if(line.getState() == 3) //missed
						noteToDraw = new Note(127f, 127f, 127f);
					else
						noteToDraw = new Note(colors[x][0], colors[x][1], colors[x][2]);
					if (x<4)
						noteToDraw.draw(gLDrawable, -3+(1.5f*x), -4, -posz);
					else
						noteToDraw.drawBar(gLDrawable, -posz, false);
				}
		}
	//}//end if songIsPlaying
	}
	public void init(GLAutoDrawable gLDrawable) 
	{
				renderer = new TextRenderer(new Font("SansSerif", Font.BOLD, 36)); //creates textrenderer
		        GL gl = gLDrawable.getGL();
		        gl.glShadeModel(GL.GL_SMOOTH);              // Enable Smooth Shading
		        gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);    // Black Background
		        gl.glClearDepth(1.0f);                      // Depth Buffer Setup
		        gl.glEnable(GL.GL_DEPTH_TEST);              // Enables Depth Testing
		        gl.glDepthFunc(GL.GL_LEQUAL);               // The Type Of Depth Testing To Do
		        // Really Nice Perspective Calculations
		        gl.glHint(GL.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);  
	}
	public void reshape(GLAutoDrawable gLDrawable, int x, int y, int width, 
	            int height) 
	{
	        GL gl = gLDrawable.getGL();
			float h = (float)width / (float)height; 
			gl.glMatrixMode(GL.GL_PROJECTION);
			gl.glLoadIdentity();
			gl.glFrustum(-h, h, -1, 1,  1, 600);
			gl.glMatrixMode(GL.GL_MODELVIEW);
			gl.glLoadIdentity();
			gl.glTranslatef(0.0f, 0.0f, -6f);
	}
	public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {}
}//end class

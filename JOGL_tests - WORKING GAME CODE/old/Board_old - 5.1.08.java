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
	private long time;
	private ArrayList<Line> lines;
	private Music song;
	private int score;
	private boolean[] drums;
	private long dt_timer;
	
	private int timeIntervalDisplayed; //number of milliseconds on the board
	private int errorTimeMargin; //number of milliseconds to still accept a note. bidirectional.
	private float correctLine; //location of buttons to press
	private float boardLength, noteSize;
	float[][] colors;
								
	private GLU glu = new GLU();
	private TextRenderer renderer;
	
	//test values///////////////////////
	//private float y =0.8624921f , z=-2.3839877f;
	//1.9000014 0.35450974
	private float y1 =1.9000014f, z1=0.35450974f; 
	
	public static Input1 input = new Input1();
	private File menuBG = new File (System.getProperty("user.dir")+"/menuBG.png");
  	private Texture texture;
	////////////////////////////////////
	
	public Board()
	{
		time = 0;
		lines = new ArrayList<Line>();
		song = null;
		noteSize = 0.5f;
		dt_timer = 0;
		score = 0;
		
		timeIntervalDisplayed = 4000;
		errorTimeMargin = 100;
		correctLine = -2.5f;
		boardLength = 10f;
		
		colors= new float[][]
									{
									{255f,0f,0f},
									{255f,255f,0f},
									{0f,0f,255f},
									{0f,255f,0f},
									{255f,255f,0f}//filler
									};
		drums = new boolean[21];
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
		drums = InputListener.drumsPressed();
		
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
			z1 -= .5*dt;
		if(input.getKey(KeyEvent.VK_DOWN))
			z1 += .5*dt;
		
		if(input.getKey(KeyEvent.VK_LEFT))
			y1 -= .5*dt;
		if(input.getKey(KeyEvent.VK_RIGHT))
			y1 += .5*dt;
		if(input.getKey(KeyEvent.VK_SPACE))
		System.out.println(y1+" "+z1);
		
		
		
		glu.gluLookAt(0,y1, z1, 0,0,-3, 0,1,0);
		
		gl.glPushMatrix();
		gl.glEnable(GL.GL_BLEND);
		//gl.glRotatef(70,1,-2,1);
		
		gl.glBegin(GL.GL_QUADS);
		//Draw the Board
		//x goes basically from -1 to 1(camera changed tho)
		//y stays same
		//boardLength is -z value
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
		for(int i = 0; i< lines.size(); i++)
		{
			if(lines.get(i).getTime()<=song.getTime() + timeIntervalDisplayed* 
				((boardLength+correctLine)/boardLength) &&
				lines.get(i).getTime() >= song.getTime() - timeIntervalDisplayed*
				(-correctLine/boardLength))//if the line is on the board...
				{
					this.renderNotes(gLDrawable, lines.get(i));
				}
			
		}

		gl.glEnd();		
		
		gl.glDisable(GL.GL_BLEND);	
		gl.glPopMatrix();
		
		try { Thread.sleep(1); } catch (Exception e) {}
	}
	public void renderNotes(GLAutoDrawable gLDrawable, Line l)
	{
		Line line = l;
		float z = -( ( (line.getTime()+(noteSize/2/boardLength*timeIntervalDisplayed)) - song.getTime() + 
						(-correctLine/boardLength*timeIntervalDisplayed) )/ 
						timeIntervalDisplayed   *    boardLength);//ratio of note distance in millis * boardLength
		boolean barMissed = false;
		for (int i = 0; i<line.getNotes().length; i++)
		{
			if(line.getNotes()[i])
			{
				Note note = new Note(colors[i][0], colors[i][1], colors[i][2]);
				
				if(z+noteSize >= correctLine-(errorTimeMargin/timeIntervalDisplayed*boardLength)
						- noteSize/2 && //if the bottom is in area
					z <=correctLine+(errorTimeMargin/timeIntervalDisplayed*boardLength)
						+ noteSize/2)//if top is in area
				{
					note.setColors(200f, 200f, 200f);
					
					if(drums[i])//add keyboard support
					{
						z = correctLine+boardLength;//just to get it off the board
					}
				}
				
				//if missed...barMissed = true and init new Notes with all 127f
				else if (z > correctLine+(errorTimeMargin/timeIntervalDisplayed*boardLength) 
						 + noteSize/2)//if note missed
				{
					note.setColors(127f, 127f, 127f);//make the note black
						
					if (i==4)
						barMissed = true;
				}
					
				if (i<4)
					note.draw(gLDrawable, -3f + ((1+noteSize)*i), -4f, z);
				else
					note.drawBar(gLDrawable, z, barMissed);
			}//end if line.getNotes
		}//end for loop
	}//end renderNotes
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

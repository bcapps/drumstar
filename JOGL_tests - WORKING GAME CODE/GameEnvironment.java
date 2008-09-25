/***************************
 * Brian Capps 
 * 05/01/2008
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

public class GameEnvironment implements GLEventListener
{
	private static String gameState;
	private boolean loadGame, playSong;
	private MainMenuNew menu;
	private Board board;
	private long dt_timer;
	
	public static void setGameState(String s)
	{
		gameState = s;
	}
	
	public void display(GLAutoDrawable gLDrawable) 
	{	
		//Start 3d Rendering	
		GL gl = gLDrawable.getGL();
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		
		gl.glLoadIdentity();
		
		if (gameState.equals("menu"))
		{
			menu.draw(gLDrawable);
			///do main menu!
		}
		else if(gameState.equals("game"))
		{
			if (playSong)
			{
				//try { Thread.sleep(5000); } catch (Exception e) {}
				board.playSong();
				playSong = false;
			}
			if (loadGame)
			{	
				board.loadData(new String("Songs/movealong.txt"));//make this dynamic...
				board.draw(gLDrawable);
				//try { Thread.sleep(2000); } catch (Exception e) {}
				board.loadSounds();
				loadGame = false;
				playSong = true;
			}
			else
				board.draw(gLDrawable);
		}//end if gamestate
		
		gl.glEnd();		
		
		try { Thread.sleep(1); } catch (Exception e) {}
	}
	
	public void init(GLAutoDrawable gLDrawable) 
	{
				gameState = "game";//should be menu, but just for testing
				loadGame = true;
				board = new Board();
				menu = new MainMenuNew();
				dt_timer = 0;
				playSong = false;
				
		        GL gl = gLDrawable.getGL();
		        gl.glShadeModel(GL.GL_SMOOTH);              // Enable Smooth Shading
		        gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);    // White Background
		        gl.glClearDepth(1.0f);                      // Depth Buffer Setup
		        gl.glEnable(GL.GL_DEPTH_TEST);              // Enables Depth Testing
		        gl.glDepthFunc(GL.GL_LEQUAL);               // The Type Of Depth Testing To Do
		        // Really Nice Perspective Calculations
		        gl.glHint(GL.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);  
	}
	public void reshape(GLAutoDrawable gLDrawable, int x, int y, int width, 
	            int height) 
	{
			GLU glu = new GLU();
			board.setPixelWidth(width);//lets the board know when the width or height is changed
			board.setPixelHeight(height);
	        GL gl = gLDrawable.getGL();
			float h = (float)width / (float)height; 
			gl.glMatrixMode(GL.GL_PROJECTION);
			gl.glLoadIdentity();
			glu.gluPerspective(45.0f, h, 0.1f, 100.0f);
			gl.glViewport(0,0,width, height);
			//gl.glFrustum(-h, h, -1, 1,  1, 600);
			gl.glMatrixMode(GL.GL_MODELVIEW);
			gl.glLoadIdentity();
			gl.glTranslatef(0.0f, 0.0f, -6f);
	}
	public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {}
}//end class

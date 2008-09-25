import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;
import com.sun.opengl.util.j2d.TextRenderer;
import java.awt.Font;
import java.awt.geom.*;
import java.awt.event.*;
import java.io.*;
import com.sun.opengl.util.texture.*;

class MenuRenderer implements GLEventListener 
{   
    private GLU glu = new GLU();
	private TextRenderer renderer;
	private boolean[] key, oldKey=Input.keysPressed();
	private int menuOption=1;
	private boolean newTexture = true;
  	private boolean flushTexture;
  	private File menuBG = new File ("/Users/home/Desktop/JOGL_tests/menubg.png");
  	private Texture texture;

	public void display(GLAutoDrawable gLDrawable) 
	{
			key = Input.keysPressed();
				
	        final GL gl = gLDrawable.getGL();
			gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
			gl.glLoadIdentity();
			
			
			if (flushTexture) 
			{
		      flushTexture = false;
		      if (texture != null) 
				{
		        texture.dispose();
		        texture = null;
		      	}
		    }

		    if (newTexture) 
			{
		      newTexture = false;

		      if (texture != null) 
				{
		        texture.dispose();
		        texture = null;
		      	}

		      try 
				{
		        texture = TextureIO.newTexture(menuBG, true);
		      	} 
				catch (IOException e) 
				{
		        e.printStackTrace();
		        ByteArrayOutputStream bos = new ByteArrayOutputStream();
		        e.printStackTrace(new PrintStream(bos));
		        return;
		      	}
		    }

		    texture.enable();
		    texture.bind();
		    gl.glTexEnvi(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE, GL.GL_REPLACE);
		    TextureCoords coords = texture.getImageTexCoords();
			gl.glBegin(GL.GL_QUADS); 
			//Background
			gl.glTexCoord2f(coords.left(), coords.bottom());
	      	gl.glVertex3f(-4.01f, -2.9f, -7f);
	      	gl.glTexCoord2f(coords.right(), coords.bottom());
	      	gl.glVertex3f(4.01f, -2.9f, -7f);
	      	gl.glTexCoord2f(coords.right(), coords.top());
	      	gl.glVertex3f(4.01f, 3.1f, -7f);
	      	gl.glTexCoord2f(coords.left(), coords.top());
	      	gl.glVertex3f(-4.01f, 3.1f, -7f);
			gl.glEnd();
			texture.disable();					// Finish texture/Background
			
			gl.glBegin(GL.GL_QUADS);            // Draw Quads
			
			
			
			if ((key[13] && key[13] != oldKey[13]) || (key[3] && key[3] != oldKey[3]) )//if Up or Blue
				menuOption -= 1;
			if ((key[17] && key[17] != oldKey[17]) || (key[0] && key[0] != oldKey[0]) )//if Down or Yellow
				menuOption += 1;
			if (menuOption >3)
			 	menuOption=1;
			if (menuOption <1)
			 	menuOption=3;
			if (menuOption==1 && key[0])
				Game.gameState = "game";

			//Option1Quad
			if (menuOption ==1)
				gl.glColor3f(1f, 1f, 1f);
			else
				gl.glColor3f(16f/255f, 191f/255f, 78f/255f);  //RBG 16, 191, 78
			gl.glVertex3f(-0.7f, 1.02f, -3.2f);  // Frontmost Right Of The Quad
			gl.glVertex3f(-1.4f, 1.04f, -3.0f);   // Frontmost Left Of The Quad
			gl.glVertex3f(-1.4f, 0.68f, -3.0f);   // Farther Right Of The Quad
			gl.glVertex3f(-0.7f, 0.73f, -3.2f);    // Farther Left Of The Quad
			
			//Option2Quad
			if (menuOption ==2)
				gl.glColor3f(1f, 1f, 1f);
			else
				gl.glColor3f(16f/255f, 191f/255f, 78f/255f);  //RBG 16, 191, 78
			gl.glVertex3f(-0.7f, 0.48f, -3.2f);  // Frontmost Right Of The Quad
			gl.glVertex3f(-1.4f, 0.50f, -3.0f);   // Frontmost Left Of The Quad
			gl.glVertex3f(-1.4f, 0.14f, -3.0f);   // Farther Right Of The Quad
			gl.glVertex3f(-0.7f, 0.19f, -3.2f);    // Farther Left Of The Quad
			
			//Option3Quad
			if (menuOption ==3)
				gl.glColor3f(1f, 1f, 1f);
			else
				gl.glColor3f(16f/255f, 191f/255f, 78f/255f);  //RBG 16, 191, 78
			gl.glVertex3f(-0.7f, 0.14f-.20f, -3.2f);  // Frontmost Right Of The Quad
			gl.glVertex3f(-1.4f, 0.16f-.20f, -3.0f);   // Frontmost Left Of The Quad
			gl.glVertex3f(-1.4f, -0.20f-.20f, -3.0f);   // Farther Right Of The Quad
			gl.glVertex3f(-0.7f, -0.15f-.20f, -3.2f);    // Farther Left Of The Quad
			
			gl.glEnd();                         // Done Drawing The Quads
			gl.glFlush();
			
			renderer.begin3DRendering();
			if(menuOption ==1)
				renderer.setColor(0.0f, 0.0f, 0.0f, 1.0f);
			else
				renderer.setColor(1.0f, 1.0f, 1.0f, 1.0f);
		    renderer.draw3D("Play", -1.2f, 0.8f, -3.0f, 0.003f); //Option1
		
			if(menuOption ==2)
				renderer.setColor(0.0f, 0.0f, 0.0f, 1.0f);
			else
				renderer.setColor(1.0f, 1.0f, 1.0f, 1.0f);
			renderer.draw3D("Options", -1.25f, 0.27f, -3.0f, 0.003f); //Option2

			if(menuOption ==3)
				renderer.setColor(0.0f, 0.0f, 0.0f, 1.0f);
			else
				renderer.setColor(1.0f, 1.0f, 1.0f, 1.0f);
			renderer.draw3D("Quit", -1.2f, -0.24f, -3.0f, 0.003f); //Option3
		    // ... more draw commands, color changes, etc.
		    renderer.end3DRendering();
			oldKey = key;
			try { Thread.sleep(1); } catch (Exception e) {}
	}
	public void displayChanged(GLAutoDrawable gLDrawable, 
            boolean modeChanged, boolean deviceChanged) {}

	public void flushTexture()
	{
	    flushTexture = true;
	}
		
	public void init(GLAutoDrawable gLDrawable) 
	{
				renderer = new TextRenderer(new Font("SansSerif", Font.BOLD, 36)); //creates textrenderer
		        GL gl = gLDrawable.getGL();
		        gl.glShadeModel(GL.GL_SMOOTH);              // Enable Smooth Shading
		        gl.glClearColor(40f/255f, 109f/255f, 154f/255f, 1.0f);    // 40, 109, 154
		        gl.glClearDepth(1.0f);                      // Depth Buffer Setup
		        gl.glEnable(GL.GL_DEPTH_TEST);              // Enables Depth Testing
		        gl.glDepthFunc(GL.GL_LEQUAL);               // The Type Of Depth Testing To Do
		        // Really Nice Perspective Calculations
		        gl.glHint(GL.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);  
	}
	public void reshape(GLAutoDrawable gLDrawable, int x, int y, int width, 
	            int height) 
	{
	        final GL gl = gLDrawable.getGL();

	        if (height <= 0) // avoid a divide by zero error!
	            height = 1;
	        final float h = (float) width / (float) height;
	        gl.glViewport(0, 0, width, height);
	        gl.glMatrixMode(GL.GL_PROJECTION);
	        gl.glLoadIdentity();
	        glu.gluPerspective(45.0f, h, 1.0, 20.0);
	        gl.glMatrixMode(GL.GL_MODELVIEW);
	        gl.glLoadIdentity();
	}
}
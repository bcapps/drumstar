/***************************
 * Brian Capps 
 * 04/22/2008
 * Description
 ****************************/
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

public class MainMenuNew 
{
	private TextRenderer renderer;
	private int menuOption=1;
	private boolean newTexture = true;
  	private boolean flushTexture;
  	private File menuBG = new File (System.getProperty("user.dir")+"/menuBG.png");
  	private Texture texture;
	private boolean[] drums, oldKey=InputListener.drumsPressed();
	
	public MainMenuNew()
	{
		menuOption=1;
		newTexture = true;
		menuBG = new File (System.getProperty("user.dir")+"/menuBG.png");
		renderer = new TextRenderer(new Font("SansSerif", Font.BOLD, 72)); //creates textrenderer
	}
	
	public void draw(GLAutoDrawable gLDrawable)
	{
		drums = InputListener.drumsPressed();//(boolean[]) drumss.clone();
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
      	gl.glVertex3f(-4.01f, -2.9f, -6f);
      	gl.glTexCoord2f(coords.right(), coords.bottom());
      	gl.glVertex3f(4.01f, -2.9f, -6f);
      	gl.glTexCoord2f(coords.right(), coords.top());
      	gl.glVertex3f(4.01f, 3.1f, -6f);
      	gl.glTexCoord2f(coords.left(), coords.top());
      	gl.glVertex3f(-4.01f, 3.1f, -6f);
		gl.glEnd();
		texture.disable();					// Finish texture/Background
		
		gl.glBegin(GL.GL_QUADS);            // Draw Quads
		
		
		
		if ((drums[13] && drums[13] != oldKey[13]) || (drums[3] && drums[3] != oldKey[3]) )//if Up or Blue
			menuOption -= 1;
		if ((drums[17] && drums[17] != oldKey[17]) || (drums[0] && drums[0] != oldKey[0]) )//if Down or Yellow
			menuOption += 1;
		if (menuOption >3)
		 	menuOption=1;
		if (menuOption <1)
		 	menuOption=3;

		//Option1Quad
		if (menuOption ==1)
			gl.glColor3f(1f, 1f, 1f);
		else
			gl.glColor3f(16f/255f, 191f/255f, 78f/255f);  //RBG 16, 191, 78
		gl.glVertex3f(-0.7f, 1.02f, -5.2f);  // Frontmost Right Of The Quad
		gl.glVertex3f(-1.4f, 1.04f, -5.0f);   // Frontmost Left Of The Quad
		gl.glVertex3f(-1.4f, 0.68f, -5.0f);   // Farther Right Of The Quad
		gl.glVertex3f(-0.7f, 0.73f, -5.2f);    // Farther Left Of The Quad
		
		//Option2Quad
		if (menuOption ==2)
			gl.glColor3f(1f, 1f, 1f);
		else
			gl.glColor3f(16f/255f, 191f/255f, 78f/255f);  //RBG 16, 191, 78
		gl.glVertex3f(-0.7f, 0.48f, -5.2f);  // Frontmost Right Of The Quad
		gl.glVertex3f(-1.4f, 0.50f, -5.0f);   // Frontmost Left Of The Quad
		gl.glVertex3f(-1.4f, 0.14f, -5.0f);   // Farther Right Of The Quad
		gl.glVertex3f(-0.7f, 0.19f, -5.2f);    // Farther Left Of The Quad
		
		//Option3Quad
		if (menuOption ==3)
			gl.glColor3f(1f, 1f, 1f);
		else
			gl.glColor3f(16f/255f, 191f/255f, 78f/255f);  //RBG 16, 191, 78
		gl.glVertex3f(-0.7f, 0.14f-.20f, -5.2f);  // Frontmost Right Of The Quad
		gl.glVertex3f(-1.4f, 0.16f-.20f, -5.0f);   // Frontmost Left Of The Quad
		gl.glVertex3f(-1.4f, -0.20f-.20f, -5.0f);   // Farther Right Of The Quad
		gl.glVertex3f(-0.7f, -0.15f-.20f, -5.2f);    // Farther Left Of The Quad
		
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
		oldKey = drums;
		
		if(menuOption==1 && drums[1])
			GameEnvironment.setGameState("game");
			
		try { Thread.sleep(1); } catch (Exception e) {}
	}
}//end class

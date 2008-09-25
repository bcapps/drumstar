import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;

class Renderer implements GLEventListener 
{   
    private float y=0, z=-5, speed=2f;

    private GLU glu = new GLU();

	public void display(GLAutoDrawable gLDrawable) 
	{
			boolean[] key = new boolean[21];
			key = Input.keysPressed();
	        final GL gl = gLDrawable.getGL();
			gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
			gl.glLoadIdentity();
		//	gl.glTranslatef(0.0f, -3.0f, -14.0f);
			gl.glBegin(GL.GL_QUADS);            // Draw A Quad
			
			gl.glColor3f(1.0f, 0.0f, 0.0f);  
			gl.glVertex3f(0.6f, 0.0f, -5.0f);  // Frontmost Right Of The Quad
			
			gl.glColor3f(0.0f, 1.0f, 0.0f);
			gl.glVertex3f(-0.6f, 0.0f, -5.0f);   // Frontmost Left Of The Quad
			
			gl.glColor3f(0.0f, 0.0f, 1.0f);
			gl.glVertex3f(-0.6f, -1.0f, 0.0f);   // Farther Right Of The Quad
			
			gl.glColor3f(1.0f, 0.0f, 1.0f);
			gl.glVertex3f(0.6f, -1.0f, 0.0f);    // Farther Left Of The Quad
			
			//Begin bottom lineup
			if (key[4])
				gl.glColor3f(1.0f, 1.0f, 1.0f);
			else
				gl.glColor3f(1.0f, 0.5f, 0.0f);	
									
			gl.glVertex3f(0.6f, -0.63799536f+0.08f -.014f, -1.8099316f-.4f + .07f);   // Top Right Of The Quad
			gl.glVertex3f(-0.6f, -0.63799536f+0.08f -.014f, -1.8099316f-.4f + .07f);  // Top Left Of The Quad 
			gl.glVertex3f(-0.6f, -0.63799536f+.0001f-.014f, -1.8099316f + .07f);   // Bottom Left Of The Quad
			gl.glVertex3f(0.6f, -0.63799536f+.0001f-.014f, -1.8099316f + .07f);    // Bottom Right Of The Quad
			
			if (key[2])
				gl.glColor3f(1.0f, 1.0f, 1.0f);
			else
				gl.glColor3f(1.0f, 0.0f, 0.0f);	
									
			gl.glVertex3f(-0.3f, -0.63799536f+0.05f, -1.8099316f-0.25f);   // Top Right Of The Quad
			gl.glVertex3f(-0.6f, -0.63799536f+0.05f, -1.8099316f-0.25f);  // Top Left Of The Quad 
			gl.glVertex3f(-0.6f, -0.63799536f+.001f, -1.8099316f);   // Bottom Left Of The Quad 
			gl.glVertex3f(-0.3f, -0.63799536f+.001f, -1.8099316f);    // Bottom Right Of The Quad
			
			if (key[3])
				gl.glColor3f(1.0f, 1.0f, 1.0f);
			else
				gl.glColor3f(1.0f, 1.0f, 0.0f);						
			gl.glVertex3f(-0.0f, -0.63799536f+0.05f, -1.8099316f-0.25f);   // Top Right Of The Quad
			gl.glVertex3f(-0.3f, -0.63799536f+0.05f, -1.8099316f-0.25f);  // Top Left Of The Quad 
			gl.glVertex3f(-0.3f, -0.63799536f+.001f, -1.8099316f);   // Bottom Left Of The Quad
			gl.glVertex3f(-0.0f, -0.63799536f+.001f, -1.8099316f);    // Bottom Right Of The Quad
			
			if (key[0])
				gl.glColor3f(1.0f, 1.0f, 1.0f);
			else
				gl.glColor3f(0.0f, 0.0f, 1.0f);						
			gl.glVertex3f(0.3f, -0.63799536f+0.05f, -1.8099316f-0.25f);   // Top Right Of The Quad
			gl.glVertex3f(0.0f, -0.63799536f+0.05f, -1.8099316f-0.25f);  // Top Left Of The Quad  
			gl.glVertex3f(0.0f, -0.63799536f+.001f, -1.8099316f);   // Bottom Left Of The Quad
			gl.glVertex3f(0.3f, -0.63799536f+.001f, -1.8099316f);    // Bottom Right Of The Quad
			
			if (key[1])
				gl.glColor3f(1.0f, 1.0f, 1.0f);
			else
				gl.glColor3f(0.0f, 1.0f, 0.0f);						
			gl.glVertex3f(0.6f, -0.63799536f+0.05f, -1.8099316f-0.25f);   // Top Right Of The Quad
			gl.glVertex3f(0.3f, -0.63799536f+0.05f, -1.8099316f-0.25f);  // Top Left Of The Quad 
			gl.glVertex3f(0.3f, -0.63799536f+.001f, -1.8099316f);   // Bottom Left Of The Quad
			gl.glVertex3f(0.6f, -0.63799536f+.001f, -1.8099316f);    // Bottom Right Of The Quad
			
			//RedNote
			if (y>= -0.63799536f-0.02f && y<= -0.63799536f+0.03f  && key[2])//margin of passage
				gl.glColor3f(1.0f, 1.0f, 1.0f);
			else
				gl.glColor3f(1.0f, 0.0f, 0.0f);	
			gl.glVertex3f(-0.3f, y+0.05f, z-0.25f);   // Top Right Of The Quad
			gl.glVertex3f(-0.6f, y+0.05f, z-0.25f);  // Top Left Of The Quad 
			gl.glVertex3f(-0.6f, y+0.01f, z);   // Bottom Left Of The Quad
			gl.glVertex3f(-0.3f, y+0.01f, z);    // Bottom Right Of The Quad
			
			y -= speed/1000f;  //speed is the amount of units it moves every thousand loops
			z += (speed*5)/1000f;
			if (y < -0.7f) //just returns the block back to the beginning
			{
				y = 0;
				z = -5;
			}
			gl.glEnd();                         // Done Drawing The Quads
			gl.glFlush();
			try { Thread.sleep(1); } catch (Exception e) {}
	}
	public void displayChanged(GLAutoDrawable gLDrawable, 
            boolean modeChanged, boolean deviceChanged) {}
		
	public void init(GLAutoDrawable gLDrawable) 
	{
		        GL gl = gLDrawable.getGL();
		        gl.glShadeModel(GL.GL_SMOOTH);              // Enable Smooth Shading
		        gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);    // Black Background
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
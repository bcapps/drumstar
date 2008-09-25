/***************************
 * Brian Capps 
 * 04/28/2008
 * Description
 ****************************/
import javax.media.opengl.*;

public class Note
{
	float r, g, b;
	boolean missedNote, scored, isValid;//if was missed, don't draw, got note, if the note exists
	public Note()//if no color specified, values are initialized as 0
	{
		r = 0f;
		g = 0f;
		b = 0f;
		missedNote = false;
		scored = false;
		isValid = false;
	}
	public Note(float red, float green, float blue)//rgb values from 0 to 255
	{
		r = red;
		g = green;
		b = blue;
		missedNote = false;
		scored = false;
		isValid = false;
	}
	public void setColors(float red, float green, float blue)//rgb values from 0 to 255
	{
		r = red;
		g = green;
		b = blue;
	}
	public void draw(GLAutoDrawable gLDrawable, float x, float y, float z, float noteSize)//top left corner coords
	{
		GL gl = gLDrawable.getGL();
		gl.glEnd();
		gl.glBegin(GL.GL_QUADS);
		//BOTTOM BOX
		gl.glColor4f(r/255f,g/255f,b/255f, 1f);
		gl.glVertex3f(x,y+.01f, z+noteSize);
		gl.glVertex3f(x+1.5f,y+.01f, z+noteSize);
		gl.glVertex3f(x+1.5f,y+.01f, z);
		gl.glVertex3f(x,y+.01f, z);
		//Front BOX
		gl.glColor4f(r/255f,g/255f,b/255f, 1f);
		gl.glVertex3f(x,y+.01f, z+noteSize);
		gl.glVertex3f(x+1.5f,y+.01f, z+noteSize);

		gl.glColor4f((r-88)/255f,(g-88)/255f,(b-88)/255f, 1f);
		gl.glVertex3f(x+1.5f,y+0.3f+.01f, z+noteSize);
		gl.glVertex3f(x,y+0.3f+.01f, z+noteSize);
		//TOP
		gl.glColor4f((r-105)/255f,(g-105)/255f,(b-105)/255f, 1f);
		gl.glVertex3f(x,y+0.3f+.01f, z+noteSize);
		gl.glVertex3f(x+1.5f,y+0.3f+.01f, z+noteSize);

		gl.glColor4f((r-147)/255f,(g-147)/255f,(b-147)/255f, 1f);
		gl.glVertex3f(x+1.5f,y+0.3f+.01f, z);
		gl.glVertex3f(x,y+0.3f+.01f, z);
		//Back BOX
		gl.glColor4f((r-105)/255f,(g-105)/255f,(b-105)/255f, 1f);
		gl.glVertex3f(x,y+.01f, z);
		gl.glVertex3f(x+1.5f,y+.01f, z);
		gl.glVertex3f(x+1.5f,y+0.3f+.01f, z);
		gl.glVertex3f(x,y+0.3f+.01f, z);
		//Left BOX
		gl.glColor4f((r-150)/255f,(g-150)/255f,(b-150)/255f, 1f);
		gl.glVertex3f(x,y+.01f, z+noteSize);
		gl.glVertex3f(x,y+.01f, z);
		gl.glVertex3f(x,y+0.3f+.01f, z);
		gl.glVertex3f(x,y+0.3f+.01f, z+noteSize);
		//Right Box
		gl.glColor4f((r-150)/255f,(g-150)/255f,(b-150)/255f, 1f);
		gl.glVertex3f(x+1.5f,y+.01f, z+noteSize);
		gl.glVertex3f(x+1.5f,y+.01f, z);
		gl.glVertex3f(x+1.5f,y+0.3f+.01f, z);
		gl.glVertex3f(x+1.5f,y+0.3f+.01f, z+noteSize);		
		
		gl.glEnd();
	}
	public void drawBar(GLAutoDrawable gLDrawable, float z, float noteSize, boolean missed)
	{
		GL gl = gLDrawable.getGL();
		gl.glEnd();
		
		gl.glBegin(GL.GL_QUADS);
		//One box
		if (missed)
			gl.glColor4f(50/255f,50/255f,50/255f, 1f);
		else
			gl.glColor4f(255/255f,165/255f,0/255f, 1f);
		gl.glVertex3f(-3f,-4f+.008f, z+noteSize  );
		gl.glVertex3f(3f,-4f+.008f, z+noteSize   );
		
		if (missed)
			gl.glColor4f(0/255f,0/255f,0/255f, 1f);
		else
			gl.glColor4f(255/255f,69/255f,0/255f, 1f);
		gl.glVertex3f(3f,-4f+.008f, z        );
		gl.glVertex3f(-3f,-4f+.008f,z        );
		
		gl.glEnd();
	}
	public void setMissed(boolean m)
	{
		missedNote = m;
	}
	public boolean getMissed()
	{
		return missedNote;
	}
	public void setScored(boolean s)
	{
		scored = s;
	}
	public boolean getScored()
	{
		return scored;
	}
	public void setIsValid(boolean v)
	{
		isValid = v;
	}
	public boolean getIsValid()
	{
		return isValid;
	}
}//end class

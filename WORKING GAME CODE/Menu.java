import javax.media.opengl.*;
import java.awt.*;
import java.awt.event.*;
import com.sun.opengl.util.Animator;


public class Menu 
{
    public static void main(String[] args) 
	{
    	Frame frame = new Frame("Menu");
     	GLCanvas canvas = new GLCanvas();
  		final Animator animator = new Animator(canvas);
        canvas.addGLEventListener(new MenuRenderer());
        frame.add(canvas);
        frame.setSize(800, 600);
		frame.setResizable(false);
		
        frame.addWindowListener(new WindowAdapter() {
             public void windowClosing(WindowEvent e) {
                 System.exit(0);
             }
         });
        frame.setVisible(true);
        canvas.requestFocus();
		animator.start();
			
    }
}

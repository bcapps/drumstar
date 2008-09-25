import javax.media.opengl.*;
import java.awt.*;
import java.awt.event.*;
import com.sun.opengl.util.Animator;


public class Game 
{
	public static String gameState;
	public static Frame frame;
	public static GLCanvas canvas;
	public static Board board; 
	
    public static void main(String[] args) 
	{
		init();
  		final Animator animator = new Animator(canvas);
		canvas.addGLEventListener(new GameEnvironment());
		frame.add(canvas);
		frame.setSize(800, 600);	
		
       	frame.addWindowListener(new WindowAdapter() {
       	public void windowClosing(WindowEvent e) {
                 	System.exit(0);
           	}
   		});
       	frame.setVisible(true);
       	canvas.requestFocus();
		animator.start();
    }
	public static void init()
	{
		gameState = "game";
    	frame = new Frame("DrumStar");
     	canvas = new GLCanvas();
	}
}

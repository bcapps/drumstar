import java.awt.event.*;
import java.util.*;

public class Input1 implements MouseListener, KeyListener
{
	private int mouseX = 0;
	private int mouseY = 0;
	private TreeMap<Integer, Boolean> keys = new TreeMap<Integer, Boolean>();
/*
 		keyMap[KEY_LEFT] = KeyEvent.VK_LEFT;
		keyMap[KEY_RIGHT] = KeyEvent.VK_RIGHT;
		keyMap[KEY_UP] = KeyEvent.VK_UP;		
		keyMap[KEY_DOWN] = KeyEvent.VK_DOWN;		
		keyMap[KEY_SPACE] = KeyEvent.VK_SPACE;
		keyMap[KEY_ESC] = KeyEvent.VK_ESCAPE;*/
	public Input1()
	{
		Game.frame.addKeyListener(this);
		Game.canvas.addKeyListener(this);
	}
	public boolean getKey(int key)
	{
//		System.out.println("ask "+key);

		return (keys.get(key) == null) ? false : keys.get(key);
	}
	public void setKey(int key, boolean state)
	{
		keys.put(key, state);
	}
	public int MouseX()
	{
		return mouseX;
	}
	public int MouseY()
	{
		return mouseY;
	}
	
/*	public boolean MouseClickPending()
	{
		return (clickedStack.size() > 0);
	}*/
	
/*	public CCoordinate GetMouseClick()
	{
		if( clickedStack.size() == 0 ) return null;
		CCoordinate c = clickedStack.getFirst();
		CCoordinate n = new CCoordinate(c.x, c.y); //to remove pointer
		clickedStack.removeFirst();
		return n;
	}*/

	public void keyTyped(KeyEvent e) {}

	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();
		keys.put(key, true);
	}

	public void keyReleased(KeyEvent e) {
		int key = e.getKeyCode();
		keys.put(key, false);
	}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	
	public void mousePressed(MouseEvent e) {
		mouseX = e.getX();
		mouseY = e.getY();
	}
	
	public void mouseReleased(MouseEvent e) {
	//	mouseX = e.getX();
	//	mouseY = e.getY();
	}
	
	public void mouseClicked(MouseEvent e)
	{
	//	clickedStack.add(new CCoordinate(e.getX(), e.getY()));
	}
}
import acm.graphics.*;
import java.awt.*;

public class Ball extends GOval {
	/** Attack power of ball*/
	private int ATK = 1; 
	/** Boolean for ball in motion*/
	private boolean ballInMotion = false;
	/** Movement in X (DX) and Y (DY) directions
	 */
	private double DX = 0;
	private double DY = 0;
	private double SPEED_VALUE = 0.75;
	/** Constructor */
	public Ball(int diameter){
		super(diameter, diameter);
		setFilled(true);
		setColor(Color.BLACK);
	}
	/** Getter methods for edges. You want to recalculate here to update values.*/
	public GPoint getLeftEdge(){
		GPoint leftEdge = new GPoint(getX(), getY()+getHeight()/2);
		return leftEdge;
	}
	public GPoint getRightEdge(){
		GPoint rightEdge = new GPoint(getX()+getWidth(), getY()+getHeight()/2);
		return rightEdge;
	}
	public GPoint getTopEdge(){
		GPoint topEdge = new GPoint(getX()+getWidth()/2, getY());
		return topEdge;
	}
	public GPoint getBottomEdge(){
		GPoint bottomEdge  = new GPoint(getX()+getWidth()/2, getY()+getHeight());
		return bottomEdge;
	}
	/** Returns array of all 4 edges*/
	public GPoint[] getAllEdges(){
		return new GPoint[]{getLeftEdge(), getRightEdge(), getTopEdge(), getBottomEdge()};
	}
	/** Returns attack power of ball*/
	public int getATK(){
		return ATK;
	}
	/** Returns movement status of ball*/
	public boolean inMotion(){
		return ballInMotion;
	}
	/** Sets movement status of ball.
	 * <p>
	 * DX and DY are set as percentages of SPEED_VALUE.
	 * <p>
	 * */
	public void startBall(double x, double y){
		DX = x/(x+y)*SPEED_VALUE;
		DY = -(y/(x+y)*SPEED_VALUE); // (-) so it goes towards top
		ballInMotion = true;
		moveBall();
	}
	public void stopBall(){
		DX = 0;
		DY = 0;
		ballInMotion = false;
	}
	public void moveBall(){
		move(DX,DY);
		//System.out.println(""+ DX +" "+ DY);
	}
	public void setDX(double x){
		DX = x;
	}
	public void setDY(double y){
		DY = y;
	}
	public double getDX(){
		return DX;
	}
	public double getDY(){
		return DY;
	}
	public String toString(){
		return "Ball Power: "+ATK+" Ball Motion: "+ballInMotion;
	}
}

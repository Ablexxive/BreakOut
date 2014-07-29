/**
 * @author      Sahil Khanna sahilsan@gmail.com
 * @version     0.2
 * @since       2014-07-26
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class Breakout extends GraphicsProgram {

	/** Width and height of application window in pixels */
	public static final int APPLICATION_WIDTH = 400;
	public static final int APPLICATION_HEIGHT = 600;

	/** Dimensions of game board (usually the same) */
	private static final int WIDTH = APPLICATION_WIDTH;
	private static final int HEIGHT = APPLICATION_HEIGHT;

	/** Dimensions of the paddle */
	private static final int PADDLE_WIDTH = 60;
	private static final int PADDLE_HEIGHT = 10;

	/** Offset of the paddle up from the bottom */
	private static final int PADDLE_Y_OFFSET = 30;

	/** Number of bricks per row */
	private static final int NBRICKS_PER_ROW = 1;

	/** Number of rows of bricks */
	private static final int NBRICK_ROWS = 10;

	/** Separation between bricks */
	private static final int BRICK_SEP = 4;

	/** Width of a brick */
	private static final int BRICK_WIDTH = 36;
	//(WIDTH - (NBRICKS_PER_ROW - 1) * BRICK_SEP) / NBRICKS_PER_ROW;

	/** Height of a brick */
	private static final int BRICK_HEIGHT = 10;//8 - 10 adjusts for collision detection percision :(

	/** Radius of the ball in pixels */
	private static final int BALL_RADIUS = 10;

	/** Offset of the top brick row from the top */
	private static final int BRICK_Y_OFFSET = 70;
	
	/** Offset from left edge **/
	private static final int BRICK_X_OFFSET = 
		(WIDTH - BRICK_WIDTH*NBRICKS_PER_ROW - (NBRICKS_PER_ROW-1)*BRICK_SEP)/2;
 
	/** Number of turns */
	private static int NTURNS = 3;
	
	/** Time Interval (ms)*/
	private static final double TIME = 1;
	/** Brick Array */
	private static Brick brickArray[][] = null;
	private int bricksRemaining = NBRICKS_PER_ROW*NBRICK_ROWS;
	/** Bumper */
	private static GRect paddle = new GRect(PADDLE_WIDTH, PADDLE_HEIGHT);
	/** Ball */
	private Ball ball = new Ball(BALL_RADIUS*2);
	private double startX = 0;//set by clicks
	private double startY = 0;//set by clicks
	
	/** Screen Elements */
	//private GLabel testXYLocations = new GLabel("test"); //just shows xy coordinates on screen for design/debug
	private GLabel score = new GLabel("points");
	private GLabel lives = new GLabel("lives");
	private int points = 0;
	private boolean WIN = false;
	
	public void run() {
		setUp();
		waitForClick();
		play(startX, startY);
	}
	public void setUp(){
		addMouseListeners();
		setSize(APPLICATION_WIDTH,APPLICATION_HEIGHT);
		setBricks();
		setPaddle();
		setBall();
		setScreenElements();
	}
	public void play(double x, double y){
		ball.startBall(x, y);
		//for(int i=0; i<1000; i++){
		while(ball.inMotion()){
			ball.moveBall();
			checkCollision();
			//waitForClick();
			pause(TIME);
			//System.out.println("DX: "+ball.getDX()+" DY: "+ball.getDY());
			if(!ball.inMotion()&NTURNS>0&!WIN){
				play(startX, startY);
			}else{
				if(NTURNS<=0) gameOver();//Java else/if loops?
			}
		}
	}
	public void setScreenElements(){
		//add(testXYLocations, 150, 200);
		score.setLabel("Score = "+ points);
		add(score, 10, 20);
		lives.setLabel("Lives = "+ NTURNS);
		add(lives, 110, 20);
		
	}
	public void gameOver(){
		System.out.println("YOU DEADED");
		GImage endScreen = new GImage("L3.gif", 100, 100);
		double xLocation = WIDTH/2 - endScreen.getWidth()/2;
		double yLocation = HEIGHT/2 - endScreen.getHeight()/2;
		endScreen.setLocation(xLocation, yLocation);
		add(endScreen);
		//Put picture of LoL Defeat here :P
	}
	public void win(){
		ball.stopBall();
		WIN = true;
		GImage endScreen = new GImage("W2.gif", 100, 100);
		double xLocation = WIDTH/2 - endScreen.getWidth()/2;
		double yLocation = HEIGHT/2 - endScreen.getHeight()/2;
		endScreen.setLocation(xLocation, yLocation);
		add(endScreen);
	}
	/** Generates bricks. 
	 */
	private void setBricks(){
		Color color[] = new Color[]{Color.BLUE, Color.RED, Color.ORANGE,
				Color.GREEN, Color.YELLOW};
		brickArray = getBrickArray(NBRICKS_PER_ROW, NBRICK_ROWS);
		int xloc = BRICK_X_OFFSET;
		int yloc = BRICK_Y_OFFSET;
		for(int j=0; j<NBRICK_ROWS; j++){
			for(int i=0; i<NBRICKS_PER_ROW; i++){
				add(brickArray[i][j], xloc, yloc);
				brickArray[i][j].setFilled(true);
				System.out.println(j);
				brickArray[i][j].setColor(color[4-(j%5)]); //%5 = prevents out of bounds for Color[]
				xloc = xloc + BRICK_WIDTH + BRICK_SEP;
			}
			xloc = BRICK_X_OFFSET;
			yloc = yloc + BRICK_HEIGHT + BRICK_SEP;
		}
	}
	/** Creates 2D Brick array. 
	 * @param x length of array
	 * @param y depth of array
	 * @return 2D array of Bricks
	 * */
	private Brick[][] getBrickArray(int x, int y){
		brickArray = new Brick[x][y];
		for(int i=0; i<x; i++){
			for(int j=0; j<y; j++){
				brickArray[i][j]= new Brick(BRICK_WIDTH, BRICK_HEIGHT);
			}
		}
		return brickArray;
	}
	/** Creates Paddle.*/
	private void setPaddle(){
		paddle.setFilled(true);
		paddle.setColor(Color.BLACK);
		add(paddle, WIDTH/2-PADDLE_WIDTH/2, HEIGHT-PADDLE_Y_OFFSET);
	}
	/** Allows mouse to move paddle
	 * <p>
	 *If game has not started (no mouse clicks registered), ball centers on 
	 *paddle and follows paddle.
	 *<p> 
	 * */
	public void mouseMoved(MouseEvent e)
	{
		paddle.setLocation((e.getX()-PADDLE_WIDTH/2), paddle.getY());
		if(!ball.inMotion()){
			ball.setLocation(paddle.getX()+PADDLE_WIDTH/2-ball.getWidth()/2, paddle.getY()-ball.getHeight());
		}
		//testXYLocations.setLabel(""+e.getLocationOnScreen());
	}
	/** Sets values for startX and startY based on "mouse position"
	 * <p>
	 * Ball movement are set when mouseClicked(MouseEvent e) is called
	 * <p>*/ //So the issue w/ original design was, when a loop is called per click, it goes slowly
	public void mouseClicked(MouseEvent e){
		startX = e.getX();
		startY = e.getY();
	}
	private void setBall(){
		add(ball, paddle.getX()+PADDLE_WIDTH/2, paddle.getY()-ball.getHeight());
	}
	/** Checks ball collisions.
	 * T/F values aren't used for anything right now. In the future though!
	 * */
	private boolean checkCollision(){
		System.out.println(ball.getRightEdge());
		System.out.println(ball.getRightEdge().getX());
		boolean hit = false;
		if(ball.getRightEdge().getX()>=WIDTH||ball.getLeftEdge().getX()<=0){
			ball.setDX(-ball.getDX());
			System.out.println(ball.getDX());
			return true;
		}
		if(ball.getTopEdge().getY()-BALL_RADIUS<=0){
			ball.setDY(Math.abs(ball.getDY())); //ABS because we always want it +, can get stuck sometimes
			return true;
		}
		if(paddle.contains(ball.getBottomEdge())||
				paddle.contains(ball.getLeftEdge())||
				paddle.contains(ball.getRightEdge())){
			ball.setDY(-Math.abs(ball.getDY())); //ABS here to prevent it from getting "stuck" in paddle
			return true;
		}
		if(ball.getBottomEdge().getY()>=HEIGHT){
			ball.stopBall();
			System.out.println("YOU DIED");
			NTURNS--;
			lives.setLabel("Lives = "+ NTURNS); //Shows decrease in lives on label
			System.out.println("NTURNS = "+NTURNS);
			pause(500);
			ball.setLocation(paddle.getX()+PADDLE_WIDTH/2, paddle.getY()-ball.getHeight());
			waitForClick(); //Allows you to click and restart ball motion!
			return true;
			//Call a score modifier and a ball resetter?
		}
		hit = checkBrickHit();
		return hit;
	}
	private boolean checkBrickHit(){
		GPoint p[] = ball.getAllEdges();
		for(int i=0; i<brickArray.length; i++){
			for(int j=0; j<brickArray[0].length; j++){
				for(int k=0; k<p.length;k++){
					if(brickArray[i][j].contains(p[k]) & brickArray[i][j].isVisible()){
						if(k<2){
							ball.setDX(-ball.getDX());
						}
						else{
							ball.setDY(-ball.getDY());
						}
						int pointsGained = brickArray[i][j].takeHit(ball.getATK());
						if(!brickArray[i][j].isVisible()){
							bricksRemaining--;
							if(bricksRemaining==0){
								win();
							}
						} 
						points+= pointsGained;
						score.setLabel("Score = "+ points);
					}
				}
			}
		}
		return false;
	}
}
/* Short List:
 * -- Reset on end, take away life (CHECK)
 * -- Tag for displaying lives (CHECK)
 * -- Tag for displaying Points (CHECK)
 * -- Game Over Screen (CHECK)
 */
/* Future work:
 * (1) Bricks - release power-ups?
 * (2) Paddle - move with L/R keys? 
 * (3) Ball - bouce off paddle at angles, not just reversing the DY.
 * Add toString method for bricks/ball
 * Make a TIMESTEP int, counts number of times steps so you can create temporary elements:
 * 	TIMESTEP; create cool effect here; endTime = TIMESTEP + ###(steps till removal); 
 * 	checkStatus(){
 * 			if(TIMESTEP==entTime{
 * 				remove cool effect
 * 			}
 * }
 * checkCollision() returns true, so if you want you, you can have it return to a global boolean
 * that you can use to do something fancy with.
 */
 /* find what % of forward movement is X, vs Y: x/x+y, y/y+x, multiply that by 


*/

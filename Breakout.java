/**
 * @author      Sahil Khanna sahilsan@gmail.com
 * @version     1.0
 * @since       2014-07-26
 */

import acm.graphics.*;
import acm.program.*;
//import acm.util.*;

//import java.applet.*;
import java.awt.*;
import java.awt.event.*;
//import java.util.*;

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
	private static final int NBRICKS_PER_ROW = 10;

	/** Number of rows of bricks */
	private static final int NBRICK_ROWS = 10;

	/** Separation between bricks */
	private static final int BRICK_SEP = 4;

	/** Width of a brick */
	private static final int BRICK_WIDTH = 36;

	/** Height of a brick */
	private static final int BRICK_HEIGHT = 10;//increase for greater collision detection precision

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
	//private int timeStep = 0; //Number of steps that ball is been in motion
	
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
	private GLabel score = new GLabel("points");
	private GLabel lives = new GLabel("lives");
	private GLabel highestScore = new GLabel("highestScore");
	private int points = 0;
	private int highScore = 0;
	private boolean WIN = false;
	
	
	
	/**Run method
	 * Calls setUp() and play(double, double).
	 */
	public void run() {
		setUp();
		waitForClick();
		play(startX, startY);
	}
	
	/**Set up method
	 * Adds mouse listeners and calls methods to generate bricks, paddle, ball, and screen elements.
	 */
	public void setUp(){
		addMouseListeners();
		setSize(APPLICATION_WIDTH,APPLICATION_HEIGHT);
		setBricks();
		setPaddle();
		setBall();
		setScreenElements();
	}
	
	/**
	 * Sets values and adds screen elements:
	 * @param Score
	 * @param lives
	 * @param highestScore
	 */
	public void setScreenElements(){
		if(points>highScore){
			highScore = points;
		}
		points = 0;
		score.setLabel("Score = "+ points);
		add(score, 10, 20);
		lives.setLabel("Lives = "+ NTURNS);
		add(lives, 110, 20);
		highestScore.setLabel("High Score = "+ highScore);
		add(highestScore, 210, 20);
	}
	
	/**
	 * Sets ball in motion.
	 */
	public void play(double x, double y){
		ball.startBall(x, y);
		//timeStep = 0;
		while(ball.inMotion()){
			ball.moveBall();
			checkCollision();
			pause(TIME);
			//timeStep++;
			if(!ball.inMotion()&NTURNS>0&!WIN){
				play(startX, startY);
			}else{
				if(NTURNS<=0) gameOver();
			}
		}
	}
	
	/**
	 * Game over method that displays loss screen. Can replay after clicking. 
	 */
	public void gameOver(){
		GImage endScreen = new GImage("L4.gif", 100, 100);
		double xLocation = WIDTH/2 - endScreen.getWidth()/2;
		double yLocation = HEIGHT/2 - endScreen.getHeight()/2;
		endScreen.setLocation(xLocation, yLocation);
		add(endScreen);
		GImage playAgain = new GImage("L3.gif", 100, 100); 
		add(playAgain,endScreen.getX(), endScreen.getY()+endScreen.getHeight());
		waitForClick();
		playAgain();
	}
	
	/**
	 * Win method displays win screen. Can click to play again.
	 */
	public void win(){
		ball.stopBall();
		WIN = true;
		GImage endScreen = new GImage("W2.gif", 100, 100);
		double xLocation = WIDTH/2 - endScreen.getWidth()/2;
		double yLocation = HEIGHT/2 - endScreen.getHeight()/2;
		endScreen.setLocation(xLocation, yLocation);
		add(endScreen);
		GImage playAgain = new GImage("L3.gif", 100, 100); 
		add(playAgain,endScreen.getX(), endScreen.getY()+endScreen.getHeight());
		waitForClick();
		playAgain();
	}
	
	/**
	 * Play again method resets turns, removes all screen elements, and runs program again.
	 */
	public void playAgain(){
		NTURNS = 3;
		removeAll();
		run();
	}
	
	/** Generates bricks.
	 * 2D square brick array is set on screen and colored.  
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
				brickArray[i][j].setColor(color[4-(j%5)]); //%5 = prevents out of bounds for Color[]
				xloc = xloc + BRICK_WIDTH + BRICK_SEP;
			}
			xloc = BRICK_X_OFFSET;
			yloc = yloc + BRICK_HEIGHT + BRICK_SEP;
		}
		WIN = false; //Win condition tied to presence of bricks. 
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
	
	/** Creates Paddle.
	 * Sets paddle at center of screen.
	 */
	private void setPaddle(){
		paddle.setFilled(true);
		paddle.setColor(Color.BLACK);
		add(paddle, WIDTH/2-PADDLE_WIDTH/2, HEIGHT-PADDLE_Y_OFFSET);
	}
	
	/** Adds ball.
	 * Sets ball over center of paddle. 
	 */
	private void setBall(){
		add(ball, paddle.getX()+PADDLE_WIDTH/2, paddle.getY()-ball.getHeight());
	}
	
	/** Checks ball collisions.
	 * T/F values are currently unused. 
	 * @return boolean
	 */
	private boolean checkCollision(){
		boolean hit = false;
		
		//If ball hits left/right edge of screen, reverse DX
		if(ball.getRightEdge().getX()>=WIDTH||ball.getLeftEdge().getX()<=0){
			ball.setDX(-ball.getDX());
			return true;
		}
		
		//If ball hits top edge of screen, set DY to abs(DY)
		if(ball.getTopEdge().getY()-BALL_RADIUS<=0){
			ball.setDY(Math.abs(ball.getDY())); //ABS because we always want it +
			return true;
		}
		
		//Checks if ball hits paddle. New DX/DY depends on location on paddle.
		if(paddle.contains(ball.getBottomEdge())||
				paddle.contains(ball.getLeftEdge())||
				paddle.contains(ball.getRightEdge())){
			/* 	 A	   B	C
			 * |----|----|----|
			 * 		a	 b
			 * 
			 * Split paddle into three equal zones. If ball hits on:
			 * Zone A: Always move ball to left side (-DX), transfer 
			 * 		   momentum from greater (DY or DX) to lesser
			 * Zone B: Just reverse DY, keep DX same
			 * Zone C: Always move ball to right side (abs(DX)), transfer 
			 * 		   momentum from greater to lesser
			 */
			
			double a = paddle.getX()+paddle.getWidth()/3;
			double b = a+paddle.getWidth()/3;
			double ballHit=ball.getBottomEdge().getX();
			
			if(ballHit<a){
				double dy = Math.abs(ball.getDY());
				double dx = Math.abs(ball.getDX());
				if(ball.getDY() > ball.getDX()){
					dy = dy - (1/4)*dy;
					ball.setDY(-Math.abs(dy));
					dx = dx + (1/4)*dy;
					ball.setDX(-Math.abs(dx));
				}else{
					dx = dx-(1/4)*dx;
					ball.setDX(-Math.abs(dx));
					dy = dy+(1/4)*dx;
					ball.setDY(-Math.abs(dy));
				}
			}
			if(ballHit>a & ballHit<b){
				ball.setDY(-Math.abs(ball.getDY()));
			}
			if(ballHit>b){
				double dy = Math.abs(ball.getDY());
				double dx = Math.abs(ball.getDX());
				if(ball.getDY() > ball.getDX()){
					dy = dy - (1/4)*dy;
					ball.setDY(-Math.abs(dy));
					dx = dx + (1/4)*dy;
					ball.setDX(Math.abs(dx));
				}else{
					dx = dx-(1/4)*dx;
					ball.setDX(Math.abs(dx));
					dy = dy+(1/4)*dx;
					ball.setDY(-Math.abs(dy));
				}
			}
			return true;
		}
		
		//If ball hits bottom of screen, remove a life and reset ball to paddle
		if(ball.getBottomEdge().getY()>=HEIGHT){
			ball.stopBall();
			NTURNS--;
			lives.setLabel("Lives = "+ NTURNS); //Shows decrease in lives on label
			pause(500);
			ball.setLocation(paddle.getX()+PADDLE_WIDTH/2, paddle.getY()-ball.getHeight());
			waitForClick(); //Allows you to click and restart ball motion!
			return true;
		}
		
		//Checks if ball hits brick and accordingly changes bricks and ball direction
		hit = checkBrickHit();
		return hit;
	}
	
	/**
	 * Checks if ball has hit a brick. Boolean return is currently unused.
	 * 1) Generates a GPoint p[] of cardinal edges of ball
	 * 2) Checks each brick against each point in p[] for overlap
	 * 3) If overlap occurs, change direction of ball and reduce the life of a brick.
	 * 4) If brick life <= 0, sets brick.isVisible() as false and reduced bricksRemaining
	 * 5) Calls win() if bricksRemaining == 0 
	 * @return boolean 
	 */
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
								win(); //calls win condition!
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
	
	/** Allows mouse to move paddle
	 * <p>
	 *If game has not started (no mouse clicks registered) or ball is not in motion, ball centers on 
	 *paddle and follows paddle.
	 *<p> 
	 * */
	public void mouseMoved(MouseEvent e)
	{
		paddle.setLocation((e.getX()-PADDLE_WIDTH/2), paddle.getY());
		if(!ball.inMotion()){
			ball.setLocation(paddle.getX()+PADDLE_WIDTH/2-ball.getWidth()/2, paddle.getY()-ball.getHeight());
		}
	}
	
	/** Sets values for startX and startY based on "mouse position"
	 * <p>
	 * Ball movement are set when mouseClicked(MouseEvent e) is called
	 * <p>*/ //So the issue w/ original design was, when a loop is called per click, it goes slowly
	public void mouseClicked(MouseEvent e){
		startX = e.getX();
		startY = e.getY();		
	}
}
/* Short List:
 * -- create Play Again image
 */
/* Future work:
 * (1) Bricks - release power-ups?
 * (2) Paddle - move with L/R keys?
 * (3) Sounds?
 * Make a TIMESTEP int, counts number of times steps so you can create temporary elements:
 * 	TIMESTEP; create cool effect here; endTime = TIMESTEP + ###(steps till removal); 
 * 	checkStatus(){
 * 			if(TIMESTEP==entTime{
 * 				remove cool effect
 * 			}
 * Or maybe a separate class with STATIC values to keep track of temporaray elements that you could 
 * just add to (tempoaray.add(Power up) or .add(Impact explosion) 
 * }
 * checkCollision() returns true, so if you want you, you can have it return to a global boolean
 * that you can use to do something fancy with.
 */

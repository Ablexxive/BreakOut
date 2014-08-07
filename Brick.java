import acm.graphics.GRect;

public class Brick extends GRect {
	/** Point value of Brick
	 * <p>
	 * Player earns value of POINTS every time a brick is hit.
	 * <p>
	 * @param points
	 * */
	private int POINTS = 200;
	
	/** Health of Brick*?
	 * @param HP
	 */
	private int HP = 2;
	
	/**Contructor*/
	public Brick(double x, double y){
		super(x, y);
	}
	/** Get HP*/
	public int getHP(){
		return HP;
	}
	/** Set HP*/
	public void setHP(int x){
		HP = x;
		
	}
	public int takeHit(int ATK){
		int tempHP = HP; 
		HP = HP-ATK;
		if(HP == 1){
			setFilled(false);
		}
		if(HP == 0){
			setVisible(false);
		}
		return (tempHP-HP)*POINTS; //points PER HP lost
		
	}
	/**Get Point Value of Brick*/
	public double getPoints(){
		return POINTS;
	}
	/**Set Point Value of Brick*/
	public void setPoints(int x){
		POINTS = x;
	}
	
	/** To String method
	 * @return String
	 */
	public String toString(){
		return "Brick X: +"+getX()+" Y: "+getY()+" HP: "+HP+" Points: "+POINTS;
	}
}

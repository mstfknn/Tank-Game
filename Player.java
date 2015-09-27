
public class Player {
	private Bullet[] bullet;
	private int life;
	private int fuel;
	private Coordinate coordinate;
	private String direction;
	private static int countofbullet;
	static Player[] tanks = new Player[2];
	
public Player(Coordinate coordinat, int fuel, int life,String direction) {
		
		this.coordinate = coordinat;
		this.fuel = fuel;
		this.life = life;
		this.direction=direction;
		bullet=new Bullet[500];
		countofbullet=0;
	}

	
	

	public Bullet[] getBullet() {
		return bullet;
	}




	public void setBullet(Bullet[] bullet) {
		this.bullet = bullet;
	}




	public static int getCountofbullet() {
		return countofbullet;
	}

	public static void setCountofbullet(int countofbullet) {
		Player.countofbullet = countofbullet;
	}
	public int getLife() {
		return life;
	}

	public void setLife(int life) {
		this.life = life;
	}

	public int getFuel() {
		return fuel;
	}

	public void setFuel(int fuel) {
		this.fuel = fuel;
	}

	public Coordinate getCoordinate() {
		return coordinate;
	}

	public void setCoordinate(Coordinate coordinate) {
		this.coordinate = coordinate;
	}

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}
	public void addBullet(Bullet b){
		bullet[countofbullet]=b;
		countofbullet++;
	}




}

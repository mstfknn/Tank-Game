
public class Enemy {
	private Bullet [] bullet;
	private int life;
	private int fuel;
	private Coordinate coordinate;
	private String direction;
	private int countofbullet;
	
	Enemy(Coordinate coordinate,int fuel,int life,String direction){
		this.life=life;
		this.fuel=fuel;
		this.coordinate=coordinate;
		this.direction=direction;
		bullet=new Bullet[100];
		countofbullet=0;
		
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

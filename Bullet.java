
public class Bullet {

	private Coordinate coordinate;
	private String direction;
	
	Bullet(Coordinate coordinate){
		this.coordinate=coordinate;
		
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

	
	
	
}

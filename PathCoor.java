public class PathCoor {
	private Coordinate coordinate;
	private String direction;
	public PathCoor(Coordinate coordinate, String direction) {
		
		this.coordinate = coordinate;
		this.direction = direction;
	}
	public PathCoor(int x, int y, String string) {
		// TODO Auto-generated constructor stub
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
	public PathCoor(){
		
	}
}
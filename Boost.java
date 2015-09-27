
public class Boost {
	private Coordinate coordinate;
	private String type;
	
	Boost(Coordinate coordinate,String type){
		this.coordinate=coordinate;
		this.type=type;
	}

	public Coordinate getCoordinate() {
		return coordinate;
	}

	public void setCoordinate(Coordinate coordinate) {
		this.coordinate = coordinate;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}

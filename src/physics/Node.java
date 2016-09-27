package physics;

public class Node {
	private double[] coordinate;
	
	public Node(double[] coordinate){
		this.setCoordinate(coordinate);
	}

	/**
	 * @return the coordinate
	 */
	public double[] getCoordinate() {
		return coordinate;
	}

	/**
	 * @param coordinate the coordinate to set
	 */
	public void setCoordinate(double[] coordinate) {
		this.coordinate = coordinate;
	}
}

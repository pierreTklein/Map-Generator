package agents;

import java.util.ArrayList;

import javafx.scene.Group;
import javafx.scene.image.Image;
import physics.Vector;

public class Road extends Sprite{
	/**Contains the length of the road segment and the angle of the segment.**/
	private Vector length;
	/**Intersection of roads on end 1**/
	private ArrayList<Road> intersection1 = new ArrayList<Road>();
	
	/**Intersection of roads on end 2**/
	private ArrayList<Road> intersection2 = new ArrayList<Road>();
	
	/**Type of road**/
	private RoadType roadType;
	
	
	public Road(Image image, double[] location, double[] dimensions, Vector length, RoadType roadType) {
		super(image, location, dimensions);
        getImv().setPreserveRatio(false);
		setLength(length);
		this.setRoadType(roadType);
	}	

	public Vector getLength() {
		return length;
	}

	public void setLength(Vector length) {
		this.length = length;
	}
	
	/**Adds given road to intersection 1, and adds this road to given road's intersection 2. 
	 * Checks that both roads share the correct vertex**/
	public void addRoad1(Road other){
		if(sharesIntersection1(this,other)){
			intersection1.add(other);
			other.addRoad2(this);
		}
		
	}
	/**Adds given road to intersection 2, and adds this road to given road's intersection 1. 
	 * Checks that both roads share the correct vertex**/
	public void addRoad2(Road other){
		if(sharesIntersection2(this,other)){
			intersection2.add(other);
			other.addRoad1(this);
		}
	}
	public ArrayList<Road> getIntersection1(){
		return intersection1;
	}
	public ArrayList<Road> getIntersection2(){
		return intersection1;
	}
	
	public RoadType getRoadType() {
		return roadType;
	}

	public void setRoadType(RoadType roadType) {
		this.roadType = roadType;
	}

	/**Evaluates whether two roads share a vertex.**/
	public static boolean sharesIntersection(Road one, Road two){
		if(sharesIntersection1(one,two) || sharesIntersection2(one,two)){
			return true;
		}
		else{
			return false;
		}
	}
	/**Evaluates whether two roads share vertex 1.**/
	public static boolean sharesIntersection1(Road one, Road two){
		double[] road1V1 = one.getIntersection1Coords();
		double[] road2V1 = two.getIntersection1Coords();
		if((int)road1V1[0] == (int)road2V1[0] && (int)road1V1[1] == (int)road2V1[1]){
			return true;
		}
		else{
			return false;
		}
	}
	/**Evaluates whether two roads share vertex 2.**/
	public static boolean sharesIntersection2(Road one, Road two){
		double[] road1V2 = one.getIntersection2Coords();
		double[] road2V2 = two.getIntersection2Coords();
		if((int)road1V2[0] == (int)road2V2[0] && (int)road1V2[1] == (int)road2V2[1]){
			return true;
		}
		else{
			return false;
		}
	}

	/**Returns the coordinates of vertex 1 (intersection 1)**/
	public double[] getIntersection1Coords(){
		return this.getLocation().getVals();
	}
	/**Returns the coordinates of vertex 2 (intersection 2)**/
	public double[] getIntersection2Coords(){
		return Vector.add(this.getLocation(), this.getLength()).getVals();
	}
	@Override
	public void scaleImv(float scale){
		getImv().setFitWidth(scale * this.length.getMagnitude());
		try {
			getImv().setRotate(length.getAngle());
		} catch (Exception e) {
			System.out.println("length of road is zero");
		}
	}

	@Override
	public void render(double[] UL, double[] LR, double[] canvasSize, float scale, Group group){
		if(group.getChildren().contains(getImv())){
			group.getChildren().remove(getImv());
		}
		this.scaleImv(1/scale);
		float imvX = map((float)getLocation().getX(),(float)UL[0],(float)LR[0],(float)0,(float)canvasSize[0]);
		float imvY = map((float)getLocation().getY(),(float)UL[1],(float)LR[1],(float)0,(float)canvasSize[1]);
		getImv().setLayoutX(imvX);
		getImv().setLayoutY(imvY);
		group.getChildren().add(getImv());
	}
	
	private float map(float value, float low, float high, float newLow, float newHigh){
		float normalized = value - low;
		float domain = high - low;
		float scale = normalized / domain;
		float newValue = (newHigh-newLow) * scale + newLow; //(D-C) * ((X-A)/(B-A)) + C 
		return newValue;
	}
}

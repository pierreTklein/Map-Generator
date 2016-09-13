package agents;

import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import physics.Vector;

public class Sprite implements Comparable<Sprite>{
	/**Location of sprite**/
	private Vector location; //coordinates are in (x,y)
	
	/**Graphics**/
	private Image image;
	private ImageView imv;
	double[] dimensions;
	
	
	public Sprite(Image image, double[] location, double[] dimensions){
		this.setImage(image);
		this.setImv(new ImageView(image));
		this.setLocation(new Vector(location));
		this.setDimensions(dimensions);
		
	}

	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		this.image = image;
	}

	public ImageView getImv() {
		return imv;
	}

	public void setImv(ImageView imv) {
		this.imv = imv;
	}
	
	public void scaleImv(float scale){
        imv.setFitWidth(scale * this.dimensions[0]);
        imv.setPreserveRatio(true);
	}
	
	public void setDimensions(double[] dimensions){
		this.dimensions = dimensions;
	}
	
	public double[] getDimensions(){
		return this.dimensions;
	}
	//the Double[] UL are the coordinates of the upper left corner of the box, and LR are the lower right coords
	public void render(double[] UL, double[] LR, double[] canvasSize, float scale, Group group){
		if(group.getChildren().contains(imv)){
			group.getChildren().remove(imv);
		}
		this.scaleImv(1/scale);
		float imvX = map((float)location.getX(),(float)UL[0],(float)LR[0],(float)0,(float)canvasSize[0]);
		float imvY = map((float)location.getY(),(float)UL[1],(float)LR[1],(float)0,(float)canvasSize[1]);
		imv.setLayoutX(imvX);
		imv.setLayoutY(imvY);
		group.getChildren().add(imv);
	}

	public Vector getLocation() {
		return location;
	}

	public void setLocation(Vector location) {
		this.location = location;
	}	
	public int[] getLowerLeft(){
		int[] lowerLeft = new int[2];
		lowerLeft[0] = (int) (imv.getLayoutX() + imv.getFitWidth());
		lowerLeft[1] = (int) imv.getLayoutY();
		return lowerLeft;
	}
	public int[] getLowerRight(){
		int[] lowerRight = new int[2];
		lowerRight[0] = (int) (imv.getLayoutX());
		lowerRight[1] = (int) (imv.getLayoutY() + imv.getFitHeight());
		return lowerRight;
	}

	
	public int getMaxX(){
		int maxX = (int) (imv.getLayoutX()+imv.getFitWidth());
		return maxX;
	}
	public int getMaxY(){
		int maxY = (int) (imv.getLayoutY()+imv.getFitHeight());
		return maxY;
	}
	public int compareTo(Sprite other) {

		int compareQuantity = ((Sprite) other).getMaxY();

		//ascending order
		return this.getMaxY() - compareQuantity;

		//descending order
		//return compareQuantity - this.quantity;

	}


	
	private float map(float value, float low, float high, float newLow, float newHigh){
		float normalized = value - low;
		float domain = high - low;
		float scale = normalized / domain;
		float newValue = (newHigh-newLow) * scale + newLow; //(D-C) * ((X-A)/(B-A)) + C 
		return newValue;
	}

}

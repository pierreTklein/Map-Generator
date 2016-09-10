package agents;

import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import physics.Vector;

public class Sprite {
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
        imv.setFitWidth(scale);
        imv.setPreserveRatio(true);
	}
	
	public void setDimensions(double[] dimensions){
		this.dimensions = dimensions;
	}
	
	public double[] getDimensions(){
		return this.dimensions;
	}
	//the Double[] UL are the coordinates of the upper left corner of the box, and LR are the lower right coords
	public void render(double[] UL, double[] LR, double[] mapDimensions, float scale, Group group){
		if(group.getChildren().contains(imv)){
			group.getChildren().remove(imv);
		}
		
		scaleImv((float) (dimensions[0] * scale));
		Vector newLayout = new Vector(UL,location.getVals());
		newLayout.normalize();
		//TODO: figure out math to make sure that imv is in the right spot
		if(!((UL[0] >= location.getX() && UL[1] >= location.getY()) || (LR[0] <= location.getX() && LR[1] <= location.getY()))){
			group.getChildren().add(imv);
			imv.setLayoutX(newLayout.getX() * mapDimensions[0]);
			imv.setLayoutY(newLayout.getY() * mapDimensions[1]);
		}
	}

	public Vector getLocation() {
		return location;
	}

	public void setLocation(Vector location) {
		this.location = location;
	}	
}

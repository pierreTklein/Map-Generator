package agents;

import javafx.scene.image.Image;

public class Building extends Sprite{
	private BuildingType type;
	
	public Building(Image image, double[] location, double[] dimensions, BuildingType type) {
		super(image, location, dimensions);
		this.setType(type);
		
	}
	public BuildingType getType() {
		return type;
	}
	public void setType(BuildingType type) {
		this.type = type;
	}


}

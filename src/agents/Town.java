package agents;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import javafx.scene.image.Image;
import mapChooser.Map;
import physics.Vector;
//THANKS TO FREEPIK FOR THEIR GRAPHICS
public class Town {
	/**Graphics, physics**/
	private ArrayList<Building> buildings = new ArrayList<Building>();
	private boolean hasTownHall = false;
	private Vector location;
	/**Variables that have to do with statistics**/
	private int population = 0;
	private String name = "";
	//Towns have 20% office, and 80% residential 
	public Town(Map map){
		
		randomBuildings();
		setTownHall();
		randomName();

	}
	public Town( double[] location) {
		setLocation(new Vector(location));
		randomBuildings();
		setTownHall();
		randomName();
	}
	public void setTownHall(){
		if(!hasTownHall){
			Image image = null;
			BuildingType type = null;
			try {
				FileInputStream path = new FileInputStream("resources/houses/government-1.png");
				image = new Image(path);
				type = BuildingType.Governmental;
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Building b = new Building(image,location.getVals(),new double[] {25,25},type);
			buildings.add(b);
			hasTownHall = true;
		}
	}
	private void setRandomLocation(){
		
	}
	
	
	
	private void randomBuildings(){
		for(int i = 0; i < 50; i++){
			double[] newLocation = location.getVals();
			int neg = 1;
			if(Math.random() < 0.5)
				neg = -1;
			newLocation[0] = location.getX() + i * Math.random() * neg;
			neg = 1;
			if(Math.random() < 0.5)
				neg = -1;
			newLocation[1] = location.getY() + i * Math.random() * neg;
			
			double r = Math.random();
			Image image = null;
			BuildingType type = null;
			if(r < 0.8){
				try {
					FileInputStream path = new FileInputStream("resources/houses/residential-" + ((int)(Math.random() * 6) + 1) + ".png");
					image = new Image(path);
					type = BuildingType.Residential;
					population+=10;
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else{
				try {
					FileInputStream path = new FileInputStream("resources/houses/commercial-" + ((int)(Math.random() * 6) + 1) + ".png");
					image = new Image(path);
					type = BuildingType.Commercial;

				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			
			Building b = new Building(image,newLocation,new double[] {25,25},type);
			buildings.add(b);
		}
	}
	
	public ArrayList<Building> getBuildings() {
		return buildings;
	}
	public void setBuildings(ArrayList<Building> buildings) {
		this.buildings = buildings;
	}
	public Vector getLocation() {
		return location;
	}
	public void setLocation(Vector location) {
		this.location = location;
	}
	public int getPopulation() {
		return population;
	}

	public void randomName(){
		String[] cardinals = {"North ", "South ", "East ", "West "};
		String[] prefixes = {"New ", "Port ", "Fort ", "Isle of "};
		String[][] addons = {cardinals,prefixes};
		String[] firstSyllable = {"York","Man", "Laud", "Cal", "Al", "Ger", "Fan",
								"Fra", "Gil", "Mont"};
		String[] secSyllable = {"chest", "ing", "if","furt","berg","forth","burg",
								"shire", "ert", "es","eman", "uest", "real","ash",
								"berry","cul","polis","ville","ford","burg","borough",
								"brough","field","kirk","bury","stadt"};
		
		String[][] syllables = {firstSyllable,secSyllable};

		name = "";
		for(int i = 0; i < addons.length; i++){
			double r = Math.random();
			if(r < 0.2){
				int rr = (int)(Math.random()*addons[i].length);
				name+=addons[i][rr];
			}

		}
		for(int i = 0; i < syllables.length; i++){
			int r = (int)(Math.random()*syllables[i].length);
			name+=syllables[i][r];
		}
		
	}
	
	public String toString(){
		String s = "Name: " + this.name + '\n' 
				+ "Location:" + location.getX() + "," + location.getY() + '\n' 
				+ "Population: "+this.population;
		
		return s;
	}
}

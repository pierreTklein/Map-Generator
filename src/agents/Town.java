package agents;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.PriorityQueue;

import javafx.scene.image.Image;
import map.Map;
import physics.Vector;
//THANKS TO FREEPIK FOR THEIR GRAPHICS
public class Town {
	/**Graphics, physics**/
	private ArrayList<Building> buildings = new ArrayList<Building>();
	private Road rootRoad; // the root of all of the roads that exist in this town
	private boolean hasTownHall = false;
	private Vector location; //town center
	private Map map;
	/**Variables that have to do with statistics**/
	private int population = 0;
	private String name = "";
	//Towns have 20% office, and 80% residential 
	public Town(Map map){
		this.setMap(map);
		randomBuildings();
		setTownHall();
		randomName();

	}
	public Town(double[] location, Map map) {
		this.setMap(map);
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
				
				path = new FileInputStream("resources/houses/straight-road-cropped.png");
				image = new Image(path);
				rootRoad = new Road(image,location.getVals(),new double[]{image.getWidth(),image.getHeight()}, new Vector(location.getVals(), new double[] {location.getVals()[0]+10,location.getVals()[1]}),RoadType.Small);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Building b = new Building(image,location.getVals(),new double[] {25,25},type);
			buildings.add(b);
			hasTownHall = true;
		}
	}
	public void setRoadAtCoord(double[] coords, Vector length){
		FileInputStream path;
		Image image = null;
		try {
			path = new FileInputStream("resources/houses/straight-road-cropped.png");
			image = new Image(path);
			Road connectingRoad = findRoad(new Vector(coords));
			connectingRoad.addNewRoad2(image, length, RoadType.Small);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**Use A* algorithm to find the path from one point to another using roads**/
	public Road findRoad(Vector end){
		Road start = rootRoad;
	    // The set of nodes already evaluated.
		HashSet<Road> closedSet = new HashSet<Road>();
	    // The set of currently discovered nodes still to be evaluated.
	    // Initially, only the start node is known.
		Comparator<Road> comparator = new Comparator<Road>(){

			@Override
			public int compare(Road o1, Road o2) {
				// TODO Auto-generated method stub
				double score1 = findScore(o1, end);
				double score2 = findScore(o2, end);
				if(score1 > score2){
					return 1;
				}
				else if(score1 < score2){
					return -1;
				}
				else{
					return 0;
				}
			}
		
		};
		PriorityQueue<Road> openSet = new PriorityQueue<Road>(comparator);
		openSet.add(start);

		Road bestRoad = start;
		while(!openSet.isEmpty()){
			Road curRoad = openSet.poll();
			
			double curScore = findScore(curRoad, end);
			if(curScore == 0){
				return curRoad;
			}
			if(curScore < findScore(bestRoad,end)){
				bestRoad = curRoad;
			}
			ArrayList<Road> neighbors = curRoad.getIntersection2();
			for(Road n : neighbors){
				openSet.add(n);
			}
			closedSet.add(curRoad);
		}
		return bestRoad;
	}
	public double findScore(Road curRoad, Vector goal){
		return findScore(new Vector(curRoad.getIntersection2Coords()), goal);
	}
	
	public double findScore(Vector current, Vector goal){
		double score = Vector.sub(current, goal).getMagnitude();
		return score;
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
	/**
	 * @return the map
	 */
	public Map getMap() {
		return map;
	}
	/**
	 * @param map the map to set
	 */
	public void setMap(Map map) {
		this.map = map;
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

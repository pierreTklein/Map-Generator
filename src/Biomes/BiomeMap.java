package Biomes;

import java.util.ArrayList;

import javafx.scene.paint.Color;

public class BiomeMap {
	ArrayList<Biome> biomes = new ArrayList<Biome>();
	public BiomeMap() {
		genericWorld();
	}

	public void genericWorld(){
		biomes.clear();
		//elevation 0:
		double elevation0 = 0;
		//elevation level 1:
		double elevation1 = 0.1;
		Biome water = new Biome(Color.rgb(68, 68, 122),new double[]{elevation0,elevation1}, new double[]{0,1},false, "WATER");
		biomes.add(water);
		//elevation level 2:
		double elevation2 = 0.12;
		Biome beach = new Biome(Color.rgb(160, 144, 120),new double[]{elevation1,elevation2}, new double[]{0,1},false, "BEACH");
		biomes.add(beach);

		//elevation level 3:
		double elevation3 = 0.3;
		Biome tropical_rain_forest = new Biome(Color.rgb(51, 120, 85),new double[]{elevation2,elevation3}, new double[]{0.66,1},false, "TROPICAL RAIN FOREST");
		biomes.add(tropical_rain_forest);
		Biome tropical_seasonal_forest = new Biome(Color.rgb(86, 153, 68),new double[]{elevation2,elevation3}, new double[]{0.33,tropical_rain_forest.getMinMoisture()},false, "TROPICAL SEASONAL FOREST");
		biomes.add(tropical_seasonal_forest);
		Biome grassland = new Biome(Color.rgb(136, 180, 86),new double[]{elevation2,elevation3}, new double[]{0.2,tropical_seasonal_forest.getMinMoisture()},true, "GRASSLAND");
		biomes.add(grassland);
		Biome sub_tropical_desert = new Biome(Color.rgb(210, 185, 138),new double[]{elevation2,elevation3}, new double[]{0,grassland.getMinMoisture()},true, "SUB TROPICAL DESERT");
		biomes.add(sub_tropical_desert);
		
		//elevation level 4:
		double elevation4 = 0.66;
		Biome temperate_rain_forest = new Biome(Color.rgb(68,136,85),new double[]{elevation3,elevation4}, new double[]{0.83,1}, true,"TEMPERATE RAIN FOREST");
		biomes.add(temperate_rain_forest);
		Biome temperate_decidous_forest = new Biome(Color.rgb(104,148,89),new double[]{elevation3,elevation4}, new double[]{0.5,temperate_rain_forest.getMinMoisture()}, true,"TEMPERATE SEASONAL FOREST");
		biomes.add(temperate_decidous_forest);
		Biome prairie = new Biome(Color.rgb(136, 170, 86),new double[]{elevation3,elevation4}, new double[]{0.2,temperate_decidous_forest.getMinMoisture()}, true,"PRAIRIE");
		biomes.add(prairie);
		Biome temperate_desert = new Biome(Color.BURLYWOOD,new double[]{elevation3,elevation4}, new double[]{0,prairie.getMinMoisture()},true, "TEMPERATE DESERT");
		biomes.add(temperate_desert);

		//elevation level 5:
		double elevation5 = 0.8;
		Biome taiga = new Biome(Color.rgb(153,170,119),new double[]{elevation4,elevation5}, new double[]{0.66,1},false, "TAIGA");
		biomes.add(taiga);
		Biome shrubland = new Biome(Color.rgb(136,153,119),new double[]{elevation4,elevation5}, new double[]{0.33,taiga.getMinMoisture()},false, "SHRUBLAND");
		biomes.add(shrubland);
		Biome high_desert = new Biome(Color.BEIGE,new double[]{elevation4,elevation5}, new double[]{0,shrubland.getMinMoisture()},false, "HIGH DESERT");
		biomes.add(high_desert);
		//elevation level 6:
		double elevation6 = 1;
		Biome snow = new Biome(Color.rgb(221,221,227),new double[]{elevation5,elevation6}, new double[]{0.5,1},false, "SNOW");
		biomes.add(snow);
		Biome tundra = new Biome(Color.rgb(187,187,171),new double[]{elevation5,elevation6}, new double[]{0.2,snow.getMinMoisture()},false, "TUNDRA");
		biomes.add(tundra);
		Biome baren = new Biome(Color.rgb(136,136,136),new double[]{elevation5,elevation6}, new double[]{0.1,tundra.getMinMoisture()},false, "BAREN");
		biomes.add(baren);
		Biome scorched = new Biome(Color.rgb(86,86,86),new double[]{elevation5,elevation6}, new double[]{0,baren.getMinMoisture()},false, "SCORCHED");
		biomes.add(scorched);
	}
	
	/**Takes elevation and moisture as parameters and returns the biome.
	NOTE: returns null if the there is no biome that meets the criteria.**/
	public Biome getBiome(double e, double m){
		for(Biome b : biomes){
			double[] eRange = b.getElevationRange();
			double[] mRange = b.getMoistureRange();
			if(isInDomain(eRange, e) && isInDomain(mRange, m)){
				return b;
			}
		}
		return null;
	}
	public ArrayList<Biome> getBiomes(){
		return biomes;
	}
	//e and m need to be between 0 and 1.
	public Color getColor(double e, double m){
		e = e > 1 ? 1 : e;
		e = e < 0 ? 0 : e;
		m = m > 1 ? 1 : m;
		m = m < 0 ? 0 : m;
		
		Biome b = getBiome(e,m);
		if(b != null){
			Color c = getBiome(e,m).getColor();
			return c;
		}
		else{
			System.out.println(e +  " " + m);
			return Color.GREEN;
		}
	}
	private boolean isInDomain(double[] domain, double value){
		if(value >= domain[0] && value <= domain[1]){
			return true;
		}
		else{
			return false;
		}
	}
}

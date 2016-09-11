package physics;

import javafx.scene.shape.Line;

public class Vector {
	private double[] vector = {0,0};
	
	public Vector(){
	}
	public Vector(double[] vector){
		for(int i = 0; i < vector.length; i++){
			this.vector[i] = vector[i];
		}
	}
	
	public Vector(double x, double y){
		double[] v  ={x,y};
		this.vector = v;
	}
	public Vector(double[] start, double[] end){
		for(int i = 0; i < start.length; i++){
			this.vector[i] = end[i]-start[i];
		}
	}
	
	public void setVector(double magnitude, double angle){
		double cos = Math.cos(angle);
    	if(Math.abs(cos) < 0.0001){
    		cos = 0;
    	}
		double sin = Math.sin(angle);

    	if(Math.abs(sin) < 0.0001){
    		sin = 0;
    	}
		vector[0] = cos * magnitude;
		vector[1] = sin * magnitude;
	}
	
	public Vector(double x, double y,double z){
		double[] v  ={x,y,z};
		this.vector = v;
	}
	
	public void setVals(double x, double y, double z){
		this.vector[0] = x;
		this.vector[1] = y;

	}
	public void setVals(double[] vector){
		this.vector = vector;
	}

	//in plane
	public double getAngle() throws Exception{
		double x = this.vector[0];
		double y = this.vector[1];
		double angle = 0;
    	if(x > 0 && y > 0){
    		angle = (Math.toDegrees(Math.atan(y/x)));
    		
    	}
    	else if(x < 0 && y > 0){
    		angle = (180 - Math.toDegrees(Math.atan(Math.abs(y/x))));
    	}
    	else if(x > 0 && y < 0){
    		angle = (360 - Math.toDegrees(Math.atan(Math.abs(y/x))));
    	}
    	else if(x < 0 && y < 0){
    		angle = (Math.toDegrees(Math.atan(Math.abs(y/x)))+180);
    	}
    	else if(x == 0 && y != 0){
    		if(y > 0){
    			angle = (90);
    		}
    		if(y < 0){
    			angle = (270);
    		}
    	}
    	else if(x != 0 && y == 0){
    		if(x > 0){
    			angle = (0);
    		}
    		if(x < 0){
    			angle = (180);
    		}
    	}
    	else {	//if(x == 0 && y == 0){
    		throw new Exception();
    	}
		return angle;


	}

	public void setAngle(double angle){
		angle = angle % 360;
		if(angle < 90){
			vector[0] *= Math.cos(angle);
			vector[1] *=  Math.sin(angle);
		}
		else if(angle < 180){
			vector[0] *= -Math.cos(angle);
			vector[1] *= Math.sin(angle);
		}
		else if(angle < 270){
			vector[0] *= -Math.cos(angle);
			vector[1] *= -Math.sin(angle);
		}
		else{
			vector[0] *= Math.cos(angle);
			vector[1] *= -Math.sin(angle);
		}
	}
	public void add(Vector other){
		for(int i = 0; i < this.vector.length; i++){
			vector[i] += other.getVals()[i];
		}
	}
	public void subtract(Vector other){
		for(int i = 0; i < this.vector.length; i++){
			vector[i] -= other.getVals()[i];
		}
	}

	public void scalarMult(double scalar){
		for(int i = 0; i < this.vector.length; i++){
			vector[i] *= scalar;
		}
	}
	
	public double getMagnitude(){
		double magnitude = Math.sqrt((Math.pow(this.getX(), 2)+(Math.pow(this.getY(), 2))));
		return magnitude;
	}
	public void setMagnitude(double num){
		normalize();
		scalarMult(num);
	}
	
	//in case magnitude = 0;
	public void normalize(){
		double magnitude = this.getMagnitude();
		try{
			for(int i = 0; i < this.vector.length; i++){
				this.vector[i] /=magnitude;
			}
		}
		catch(Exception e){
			for(int i = 0; i < this.vector.length; i++){
				this.vector[i] = 0;
			}
		}
	}
	
	public void limit(double max){
		if(this.getMagnitude() > max){
			this.setMagnitude(max);
		}
	}
	public void random2D(){
		double xNeg = Math.random() > 0.5 ? -1 : 1;
		double yNeg = Math.random() > 0.5 ? -1 : 1;
		boolean complete = false;
		while(!complete){
			this.vector[0] = Math.random() * xNeg;
			this.vector[1] = Math.random() * yNeg;
			try{
				this.normalize();
				complete = true;
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	public double dot(Vector other){
		int a = 0;
		for(int i = 0; i < this.vector.length; i++){
			a+= this.vector[i] * other.getVals()[i];
		}
		return a;
	}

	public double getX(){
		return vector[0];
	}
	public void setX(double x){
		this.vector[0] = x;
	}
	public void setY(double y){
		this.vector[1] = y;
	}
	public double getY(){
		return vector[1];
	}
	public double[] getVals(){
		double[] b = new double[this.vector.length];
		for(int i = 0; i < this.vector.length; i++){
			b[i] = vector[i];
		}
		return b;
	}
	public Line draw(double[] startCoord){
		Line l = new Line(startCoord[0],startCoord[1],this.getX()*10+startCoord[0], this.getY()*10+startCoord[1]);
		return l;
	}
	
	public String toString(){
		return "[ "+this.getX() + " , " + this.getY() + "]";
	}
	
	public static Vector sub(Vector one, Vector two){
		Vector v = new Vector();
		v.setVals(one.getVals());
		v.subtract(two);
		return v;
	}
	public static Vector add(Vector one, Vector two){
		Vector v = new Vector();
		v.setVals(one.getVals());
		v.add(two);
		return v;
	}

}

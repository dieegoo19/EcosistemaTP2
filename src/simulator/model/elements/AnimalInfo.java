package simulator.model.elements;
import simulator.misc.Vector2D;
import simulator.model.Diet;
import simulator.model.JSONable;
import simulator.model.State;


public interface AnimalInfo extends JSONable { // Note that it extends JSONable  
	public State getState();  
	public Vector2D getPosition();  
	public String getGeneticCode();  
	public Diet getDiet();  
	public double getSpeed();  
	public double getSightRange();  
	public double getEnergy();  
	public double getAge();
	public Vector2D getDestination();  
	public boolean isPregnant();  
}
package simulator.model.region;
import simulator.model.*;

import simulator.misc.Utils;
import simulator.model.elements.AnimalInfo;

public class DynamicSupplyRegion extends Region{
	
	double food;
	double growFactor;
	
	public DynamicSupplyRegion(double initialFood, double iniGrowFactor) {
		super();
		this.food = initialFood; this.growFactor = iniGrowFactor;
	}
	
	@Override
	public double getFood(AnimalInfo a, double dt) {
		if(a.getDiet() == Diet.HERBIVORE) {
			double ret = Math.min(food,60.0*Math.exp(-Math.max(0,animalsList.size()-5.0)*2.0)*dt);
			food -= ret;
			return ret;
		}
		else return 0.0;
	}

	@Override
	public void update(double dt) {
		if(Utils.RAND.nextDouble() < 0.5) {
			food *= dt*growFactor;
		}
	}
	
}
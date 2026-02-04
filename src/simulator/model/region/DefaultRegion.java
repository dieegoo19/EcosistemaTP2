package simulator.model.region;
import simulator.model.*;

import simulator.model.elements.AnimalInfo;

public class DefaultRegion extends Region{

	public DefaultRegion() {
		super();
	}

	@Override
	public double getFood(AnimalInfo a, double dt) {
		return a.getDiet() == Diet.HERBIVORE ? 
				60.0*Math.exp(-Math.max(0,super.getHerbivoreAnimalsCount()-5.0)*2.0)*dt
				: 0.0;
	}

	@Override
	public void update(double dt) {}

}

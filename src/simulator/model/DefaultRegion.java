package simulator.model;

public class DefaultRegion extends Region {

	public DefaultRegion() {
		super();
	}

	@Override
	public double getFood(AnimalInfo a, double dt) {
		return a.getDiet() == Animal.Diet.HERBIVORE
				? 60.0 * Math.exp(-Math.max(0, super.getAnimalsCountByDiet(Animal.Diet.HERBIVORE) - 5.0) * 2.0) * dt
				: 0.0;
	}

	@Override
	public void update(double dt) {
	}

	public String toString() {
		return "DefaultRegion";
	}
}

package simulator.model;

import simulator.misc.Utils;

public class DynamicSupplyRegion extends Region {

	final static double FOOD_EAT_RATE_HERBS = 60.0;
	final static double FOOD_SHORTAGE_TH_HERBS = 5.0;
	final static double FOOD_SHORTAGE_EXP_HERBS = 2.0;
	final static double INIT_FOOD = 100.0;
	final static double FACTOR = 2.0;

	private double food;
	private double growFactor;

	public DynamicSupplyRegion(double f, double gFc) {
		super();
		this.food = f;
		this.growFactor = gFc;
	}

	@Override
	public double getFood(AnimalInfo a, double dt) {
		if (a.getDiet().equals(Animal.Diet.CARNIVORE))
			return 0.0;
		else {
			double ret = Math.min(food,
					FOOD_EAT_RATE_HERBS * Math.exp(
							-Math.max(0, super.getAnimalsCountByDiet(Animal.Diet.HERBIVORE) - FOOD_SHORTAGE_TH_HERBS)
									* FOOD_SHORTAGE_EXP_HERBS)
							* dt);
			food -= ret;
			return ret;
		}
	}

	@Override
	public void update(double dt) {
		if (Utils.RAND.nextDouble() < 0.5)
			food += dt * growFactor;
	}

	public String toString() {
		return "DynamicRegion";
	}
}

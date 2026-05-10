package simulator.factories;

import org.json.JSONObject;

import simulator.model.*;

public class DynamicSupplyRegionBuilder extends Builder<Region> {

	final static double INIT_FOOD = 100.0;
	final static double FACTOR = 2.0;

	public DynamicSupplyRegionBuilder() throws IllegalArgumentException {
		super("dynamic", "Crea una region que da comida de forma dinamica.");
	}

	@Override
	protected DynamicSupplyRegion createInstance(JSONObject data) {
		return new DynamicSupplyRegion(data.optDouble("food", INIT_FOOD), data.optDouble("factor", FACTOR));
	}

	@Override
	protected void fillInData(JSONObject o) {
		o.put("factor", "food increase factor (optional, default 2.0)");
		o.put("food", "initial amount of food (optional, default 100.0)");
	}

}

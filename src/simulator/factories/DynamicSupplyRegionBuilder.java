package simulator.factories;

import org.json.JSONObject;

import simulator.model.region.DynamicSupplyRegion;

public class DynamicSupplyRegionBuilder extends Builder<DynamicSupplyRegion>{

	public DynamicSupplyRegionBuilder() throws IllegalArgumentException {
		super("dynamic", "Crea una region que da comida de forma dinamica.");
	}

	@Override
	protected DynamicSupplyRegion createInstance(JSONObject data) {
		return new DynamicSupplyRegion(data.getDouble("food"), data.getDouble("factor"));
	}
	
	 @Override
    protected void fillInData(JSONObject o) {
        o.put("factor", "Factor de crecimiento de la comida en la region.");
        o.put("food", "Cantidad de comida en la region.");
    }
	
}

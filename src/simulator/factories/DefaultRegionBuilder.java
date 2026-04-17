package simulator.factories;

import org.json.JSONObject;

import simulator.model.*;

public class DefaultRegionBuilder extends Builder<Region> {

	public DefaultRegionBuilder() throws IllegalArgumentException {
		super("default", "Crea la region por defecto.");
	}

	@Override
	protected DefaultRegion createInstance(JSONObject data) {
		return new DefaultRegion();
	}

}

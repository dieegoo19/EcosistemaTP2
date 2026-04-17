package simulator.factories;

import org.json.JSONObject;

import simulator.model.*;

public class SelectFirstBuilder extends Builder<SelectionStrategy> {

	public SelectFirstBuilder() throws IllegalArgumentException {
		super("first", "Selecciona el primer animal de la lista.");
	}

	@Override
	protected SelectFirst createInstance(JSONObject data) {
		return new SelectFirst();
	}
}

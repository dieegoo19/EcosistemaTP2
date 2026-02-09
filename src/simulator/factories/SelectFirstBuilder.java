package simulator.factories;

import org.json.JSONObject;

import simulator.model.selection.SelectFirst;

public class SelectFirstBuilder extends Builder<SelectFirst>{

	public SelectFirstBuilder() throws IllegalArgumentException{
		super("first","Selecciona el primer animal de la lista.");
	}

	@Override
	protected SelectFirst createInstance(JSONObject data) {
		return new SelectFirst();
	}
}

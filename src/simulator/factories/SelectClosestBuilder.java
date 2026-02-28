package simulator.factories;

import org.json.JSONObject;

import simulator.model.*;

public class SelectClosestBuilder extends Builder<SelectionStrategy>{

	public SelectClosestBuilder()  throws IllegalArgumentException{
		super("closest","Selecciona el animal mas cercano de la lista.");
	}

	@Override
	protected SelectClosest createInstance(JSONObject data) {
		return new SelectClosest();
	}
}

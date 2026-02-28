package simulator.factories;

import org.json.JSONObject;

import simulator.misc.Vector2D;
import simulator.model.*;

public class WolfBuilder extends AnimalBuilder{
	
	public WolfBuilder(Factory<SelectionStrategy> selectionStrategyFactory) throws IllegalArgumentException {
		super("wolf", "Crea un lobo.", selectionStrategyFactory);
	}

	@Override
	protected Wolf createInstance(JSONObject data) {
		SelectionStrategy mateStr = super.get_strategy(data, "mate_strategy");
        SelectionStrategy dangerStr = super.get_strategy(data, "hunt_strategy");
        Vector2D pos = super.get_pos(data);
        
        return new Wolf(mateStr, dangerStr, pos);
	}
	
	@Override
	protected void fillInData(JSONObject o) {}
}

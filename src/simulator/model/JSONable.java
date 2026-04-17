package simulator.model;

import org.json.*;

public interface JSONable {
	default public JSONObject asJSON() {
		return new JSONObject();
	}

}

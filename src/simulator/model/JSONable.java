package simulator.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.json.*;

public interface JSONable {  
	default public JSONObject asJSON() {  
		return new JSONObject();  
	}
	
}

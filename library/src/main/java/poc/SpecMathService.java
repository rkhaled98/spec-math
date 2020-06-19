package poc;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SpecMathService {
	public String union(String spec1, String spec2, String defaults, ArrayList<String> conflictResolutions) throws UnableToUnionException, IOException {
		// conflictResolution is a JSON object which we need to decode.
		ObjectMapper objectMapper = new ObjectMapper();

		ArrayList<Conflict> conflictResolutionObjects = new ArrayList<Conflict>();

		for (String conflictResolution: conflictResolutions){
			Conflict conflict = objectMapper.readValue(conflictResolution, Conflict.class);
			conflictResolutionObjects.add(conflict);
		}

		// now we need to add these conflicts to the defaults file.
		return "";

	}
	public String unionWithDefaults() throws UnableToUnionException {
		return "";
	}
}

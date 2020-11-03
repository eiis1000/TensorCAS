package tensors.elementoperations;

import show.ezkz.casprzak.core.functions.GeneralFunction;

import java.util.Map;
import java.util.Set;

public class GeneralFunctionWrapper implements ElementAccessor {

	public final GeneralFunction contained;

	public GeneralFunctionWrapper(GeneralFunction contained) {
		this.contained = contained;
	}

	@Override
	public GeneralFunction getValueAt(Map<String, Integer> indexValues, Map<String, GeneralFunction> toSubstitute, int dimension) {
		return contained.substituteVariables(toSubstitute);
	}

	public void getIndices(Set<String> set) {
		// Do nothing
	}


}

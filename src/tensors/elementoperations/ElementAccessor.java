package tensors.elementoperations;

import show.ezkz.casprzak.core.functions.GeneralFunction;

import java.util.Map;
import java.util.Set;

public interface ElementAccessor {

	GeneralFunction getValueAt(Map<String, Integer> indexValues, Map<String, GeneralFunction> toSubstitute, int dimension);

	void getIndices(Set<String> set);


}

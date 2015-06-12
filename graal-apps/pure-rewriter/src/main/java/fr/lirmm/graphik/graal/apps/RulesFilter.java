/**
 * 
 */
package fr.lirmm.graphik.graal.apps;

import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.util.stream.filter.Filter;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
class RulesFilter implements Filter {
	@Override
	public boolean filter(Object o) {
		return o instanceof Rule;
	}
}

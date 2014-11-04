/**
 * 
 */
package fr.lirmm.graphik.graal.homomorphism.checker;

import fr.lirmm.graphik.graal.core.Query;
import fr.lirmm.graphik.graal.core.atomset.ReadOnlyAtomSet;
import fr.lirmm.graphik.graal.homomorphism.Homomorphism;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public interface HomomorphismChecker extends Comparable<HomomorphismChecker> {
	
	/**
	 * 
	 * @param query
	 * @param atomset
	 * @return
	 */
	boolean check(Query query, ReadOnlyAtomSet atomset);
	
	/**
	 * 
	 * @param query
	 * @param atomset
	 * @return
	 */
	Homomorphism<? extends Query, ? extends ReadOnlyAtomSet> getSolver();
	
	/**
	 * 
	 * @return
	 */
	int getPriority();
	
	/**
	 * @param priority
	 */
	void setPriority(int priority);
}

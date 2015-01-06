/**
 * 
 */
package fr.lirmm.graphik.graal.homomorphism;

import java.util.SortedSet;
import java.util.TreeSet;

import fr.lirmm.graphik.graal.core.Query;
import fr.lirmm.graphik.graal.core.atomset.ReadOnlyAtomSet;
import fr.lirmm.graphik.graal.homomorphism.checker.DefaultUnionConjunctiveQueriesChecker;
import fr.lirmm.graphik.graal.homomorphism.checker.RecursiveBacktrackChecker;
import fr.lirmm.graphik.graal.homomorphism.checker.HomomorphismChecker;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 * 
 */
public final class DefaultHomomorphismFactory implements HomomorphismFactory {
	
	private SortedSet<HomomorphismChecker> elements;
	
	private static DefaultHomomorphismFactory instance = null;

	
	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTOR
	// /////////////////////////////////////////////////////////////////////////
	
	private DefaultHomomorphismFactory(){
		this.elements = new TreeSet<HomomorphismChecker>();
		this.elements.add(new RecursiveBacktrackChecker());
		this.elements.add(new DefaultUnionConjunctiveQueriesChecker());
	}
	
	public static synchronized final DefaultHomomorphismFactory getInstance() {
		if(instance == null)
			instance = new DefaultHomomorphismFactory();
		
		return instance;
	}
	
	// /////////////////////////////////////////////////////////////////////////
	// METHODS
	// /////////////////////////////////////////////////////////////////////////
	
	/**
	 * 
	 * @param checker
	 * @return true if this checker is not already added, false otherwise.
	 */
	public boolean addChecker(HomomorphismChecker checker) {
		return this.elements.add(checker);
	}
 	
    @Override
    public Homomorphism<? extends Query, ? extends ReadOnlyAtomSet> getSolver(Query query, ReadOnlyAtomSet atomset) {
    	Homomorphism<? extends Query, ? extends ReadOnlyAtomSet> solver = null;
    	for(HomomorphismChecker e : elements) {
    		if(e.check(query, atomset)) {
    			solver = e.getSolver();
    			break;
    		}
    	}
    	return solver;
    }
    
}

/**
 * 
 */
package fr.lirmm.graphik.kb.atomset.graph;

import java.util.Set;
import java.util.TreeSet;

import fr.lirmm.graphik.kb.core.Predicate;


public class PredicateVertex extends Predicate implements Vertex {

	private static final long serialVersionUID = 1607321754413212182L;
	private Set<Edge> edges = new TreeSet<Edge>();

    // /////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    // /////////////////////////////////////////////////////////////////////////

    /**
     * @param predicate
     */
    public PredicateVertex(Predicate predicate) {
       super(predicate.getLabel(), predicate.getArity());
    }

    // /////////////////////////////////////////////////////////////////////////
    // VERTEX METHODS
    // /////////////////////////////////////////////////////////////////////////

    /*
     * (non-Javadoc)
     * 
     * @see fr.lirmm.graphik.alaska.store.graph.Vertex#getEdges()
     */
    @Override
    public Set<Edge> getEdges() {
        return this.edges;
    }
    
    @Override
    public boolean equals(Object o) {
    	return super.equals(o);
    }
    
    @Override 
    public int hashCode() {
    	return super.hashCode();
    }

}

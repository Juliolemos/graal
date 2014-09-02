/**
 * 
 */
package fr.lirmm.graphik.graal.chase;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.lirmm.graphik.graal.Graal;
import fr.lirmm.graphik.graal.core.DefaultConjunctiveQuery;
import fr.lirmm.graphik.graal.core.DefaultFreeVarGen;
import fr.lirmm.graphik.graal.core.HashMapSubstitution;
import fr.lirmm.graphik.graal.core.Query;
import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.graal.core.Substitution;
import fr.lirmm.graphik.graal.core.SymbolGenerator;
import fr.lirmm.graphik.graal.core.Term;
import fr.lirmm.graphik.graal.core.atomset.AtomSet;
import fr.lirmm.graphik.graal.core.atomset.ReadOnlyAtomSet;
import fr.lirmm.graphik.graal.grd.GraphOfRuleDependencies;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public class ChaseWithGRD extends AbstractChase {
	
	private static final Logger logger = LoggerFactory
			.getLogger(ChaseWithGRD.class);
	
	private ChaseStopCondition stopCondition = new RestrictedChaseStopCondition();
	private SymbolGenerator existentialGen = new DefaultFreeVarGen("E");
	private GraphOfRuleDependencies grd;
	private AtomSet atomSet;
	private Queue<Pair<Rule, Substitution>> queue = new LinkedList<Pair<Rule, Substitution>>();
	
	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTOR
	// /////////////////////////////////////////////////////////////////////////
	
	public ChaseWithGRD(GraphOfRuleDependencies grd, AtomSet atomSet) {
		this.grd = grd;
		this.atomSet = atomSet;
		for(Rule r : grd.getRules()) {			
			this.queue.add(new ImmutablePair<Rule, Substitution>(r, new HashMapSubstitution()));
		}
	}
	
	// /////////////////////////////////////////////////////////////////////////
	// METHODS
	// /////////////////////////////////////////////////////////////////////////

	/* (non-Javadoc)
	 * @see fr.lirmm.graphik.alaska.saturator.Saturator#next()
	 */
	@Override
	public void next() throws ChaseException {
		Rule rule, unifiedRule;
		Substitution unificator;
		Query query;
		try {
			Pair<Rule, Substitution> pair = queue.poll();
			if(pair != null) {
				unificator = pair.getRight();
				rule = pair.getLeft();
				unifiedRule = unificator.getSubstitut(pair.getLeft());
				query = new DefaultConjunctiveQuery(unifiedRule.getBody(), unifiedRule.getFrontier());
				if(logger.isDebugEnabled()) {
					logger.debug("Execute rule: " + rule + " with unificator " + unificator);
					logger.debug("-- Query: " + query);
				}
				
				for (Substitution substitution : Graal.executeQuery(query, atomSet)) {
					if(logger.isDebugEnabled()) {
						logger.debug("-- Found homomorphism: " + substitution );
					}
					Set<Term> fixedTerm = substitution.getValues();
					
					// Generate new existential variables
					for(Term t : unifiedRule.getExistentials()) {
						substitution.put(t, existentialGen.getFreeVar());
					}

					// the atom set producted by the rule application
					ReadOnlyAtomSet deductedAtomSet = Graal.substitute(substitution, unifiedRule.getHead());
	
					if(stopCondition.canIAdd(deductedAtomSet, fixedTerm, this.atomSet)) {
						this.atomSet.addAll(deductedAtomSet);
						for(Rule triggeredRule : this.grd.getOutEdges(rule)) {
							for(Substitution u : this.grd.getUnifiers(rule, triggeredRule)) {
								if(logger.isDebugEnabled()) {
									logger.debug("-- -- Dependency: " + triggeredRule + " with " + u);
									logger.debug("-- -- Unificator: " + u);
								}
								if(u != null) {
									this.queue.add(new ImmutablePair<Rule, Substitution>(triggeredRule, u));
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			throw new ChaseException("An error occur pending saturation step.", e);
		}
	}
	
	@Override
	public boolean hasNext() {
		return !queue.isEmpty();
	}

}

/*
 * Copyright (C) Inria Sophia Antipolis - Méditerranée / LIRMM
 * (Université de Montpellier & CNRS) (2014 - 2017)
 *
 * Contributors :
 *
 * Clément SIPIETER <clement.sipieter@inria.fr>
 * Mélanie KÖNIG
 * Swan ROCHER
 * Jean-François BAGET
 * Michel LECLÈRE
 * Marie-Laure MUGNIER <mugnier@lirmm.fr>
 *
 *
 * This file is part of Graal <https://graphik-team.github.io/graal/>.
 *
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 */
/**
 * 
 */
package fr.lirmm.graphik.graal.backward_chaining.pure;

import fr.lirmm.graphik.graal.api.backward_chaining.QueryRewriterWithCompilation;
import fr.lirmm.graphik.graal.api.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.core.RulesCompilation;
import fr.lirmm.graphik.util.profiler.AbstractProfilable;
import fr.lirmm.graphik.util.profiler.NoProfiler;
import fr.lirmm.graphik.util.stream.CloseableIterable;
import fr.lirmm.graphik.util.stream.CloseableIteratorAdapter;
import fr.lirmm.graphik.util.stream.CloseableIteratorWithoutException;
import fr.lirmm.graphik.util.stream.IterableAdapter;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 * 
 */
public class PureRewriter extends AbstractProfilable implements QueryRewriterWithCompilation {

	private boolean           unfolding = true;
	private RewritingOperator operator;

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// /////////////////////////////////////////////////////////////////////////

	public PureRewriter() {
		this(new AggregSingleRuleOperator());
	}

	public PureRewriter(boolean unfolding) {
		this(new AggregSingleRuleOperator(), unfolding);
	}

	public PureRewriter(RewritingOperator operator) {
		this.operator = operator;
	}

	public PureRewriter(RewritingOperator operator, boolean unfolding) {
		this.operator = operator;
		this.unfolding = unfolding;
	}

	// /////////////////////////////////////////////////////////////////////////
	// METHODS
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public CloseableIteratorWithoutException<ConjunctiveQuery> execute(ConjunctiveQuery query, Iterable<Rule> rules) {
		RewritinCloseableIterator it = new RewritinCloseableIterator(this.unfolding, query, rules, this.operator);
		it.setProfiler(this.getProfiler());
		return it;
	}

	@Override
	public CloseableIteratorWithoutException<ConjunctiveQuery> execute(ConjunctiveQuery query, Iterable<Rule> rules,
	    RulesCompilation compilation) {
		RewritinCloseableIterator it = new RewritinCloseableIterator(this.unfolding, query, rules, compilation, this.operator);
		it.setProfiler(this.getProfiler());
		return it;
	}
	
	public static CloseableIteratorWithoutException<ConjunctiveQuery> unfold(
	    CloseableIterable<ConjunctiveQuery> pivotRewritingSet,
	    RulesCompilation compilation) {
		return new CloseableIteratorAdapter<ConjunctiveQuery>(Utils.unfold(new IterableAdapter(pivotRewritingSet),
		    compilation, NoProfiler.instance()).iterator());
	}

}

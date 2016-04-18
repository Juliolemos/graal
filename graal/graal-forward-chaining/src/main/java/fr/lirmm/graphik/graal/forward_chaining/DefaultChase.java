/*
 * Copyright (C) Inria Sophia Antipolis - Méditerranée / LIRMM
 * (Université de Montpellier & CNRS) (2014 - 2016)
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
package fr.lirmm.graphik.graal.forward_chaining;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.core.InMemoryAtomSet;
import fr.lirmm.graphik.graal.api.core.Predicate;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.api.forward_chaining.AbstractChase;
import fr.lirmm.graphik.graal.api.forward_chaining.ChaseException;
import fr.lirmm.graphik.graal.api.forward_chaining.RuleApplicationHandler;
import fr.lirmm.graphik.graal.core.DefaultRule;
import fr.lirmm.graphik.graal.core.atomset.LinkedListAtomSet;
import fr.lirmm.graphik.graal.core.ruleset.IndexedByBodyPredicatesRuleSet;
import fr.lirmm.graphik.graal.forward_chaining.halting_condition.ChaseStopConditionWithHandler;
import fr.lirmm.graphik.graal.forward_chaining.halting_condition.RestrictedChaseStopCondition;
import fr.lirmm.graphik.graal.forward_chaining.rule_applier.DefaultRuleApplier;
import fr.lirmm.graphik.graal.forward_chaining.rule_applier.TestRuleApplier;
import fr.lirmm.graphik.util.Verbosable;
import fr.lirmm.graphik.util.stream.GIterator;

/**
 * This chase (forward-chaining) algorithm iterates over all rules at each step.
 * It stops if a step does not produce new facts.
 * 
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public class DefaultChase extends AbstractChase implements Verbosable {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultChase.class);

	private IndexedByBodyPredicatesRuleSet ruleSet;
	private AtomSet atomSet;
	private boolean isVerbose = false;

	private Map<Rule, AtomSet> rulesToCheck;
	private Map<Rule, AtomSet> nextRulesToCheck;
	private boolean firstCall = true;

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// /////////////////////////////////////////////////////////////////////////
	
	public DefaultChase(Iterable<Rule> rules, AtomSet atomSet) {
		super(new DefaultRuleApplier<AtomSet>());
		this.atomSet = atomSet;
		this.ruleSet = new IndexedByBodyPredicatesRuleSet();
		init(rules);
	}

	private void init(Iterable<Rule> rules) {
		int i = 0;
		this.nextRulesToCheck = new TreeMap<Rule, AtomSet>();
		for (Rule r : rules) {
			Rule copy = new DefaultRule(r);
			copy.setLabel(Integer.toString(++i));
			this.ruleSet.add(copy);
			this.nextRulesToCheck.put(copy, atomSet);
		}
	}

	// /////////////////////////////////////////////////////////////////////////
	// PUBLICS METHODS
	// /////////////////////////////////////////////////////////////////////////
	
	public void firstCall() {
		super.setRuleApplier(
		    new TestRuleApplier<AtomSet>(atomSet, new ChaseStopConditionWithHandler(new RestrictedChaseStopCondition(),
		                                                                            new Handler())));
	}

	@Override
	public void next() throws ChaseException {
		if (firstCall) {
			this.firstCall();
			this.firstCall = false;
		}
		this.rulesToCheck = this.nextRulesToCheck;
		this.nextRulesToCheck = new TreeMap<Rule, AtomSet>();
		try {
			int i = 0;
			if (!this.rulesToCheck.isEmpty()) {

				if (this.getProfiler().isProfilingEnabled()) {
					this.getProfiler().start("saturationTime");
				}
				for (Entry<Rule, AtomSet> e : this.rulesToCheck.entrySet()) {
					if (LOGGER.isDebugEnabled())
						LOGGER.debug("{}/{}", ++i, this.rulesToCheck.size());

					String key = null;
					Rule rule = e.getKey();
					AtomSet data = e.getValue();

					if (this.isVerbose && this.getProfiler().isProfilingEnabled()) {
						key = "Rule " + rule.getLabel() + " application time";
						this.getProfiler().clear(key);
						this.getProfiler().trace(rule.toString());
						this.getProfiler().start(key);
    				}
					this.getRuleApplier().apply(rule, data);
					if (this.isVerbose && this.getProfiler().isProfilingEnabled()) {
						this.getProfiler().stop(key);
					}
    			}
				if (this.getProfiler().isProfilingEnabled()) {
					this.getProfiler().stop("saturationTime");
				}
    		}
		} catch (Exception e) {
			throw new ChaseException("An error occured during saturation step.", e);
		}
	}
	
	@Override
	public boolean hasNext() {
		return !this.nextRulesToCheck.isEmpty();
	}

	////////////////////////////////////////////////////////////////////////////
	// ABSTRACT METHODS IMPLEMENTATION
	/////////////////////////////////////////////////////// rulesToCheck/////////////////////

	@Override
	public void enableVerbose(boolean enable) {
		this.isVerbose = enable;
	}

	// /////////////////////////////////////////////////////////////////////////
	// PRIVATE CLASS
	// /////////////////////////////////////////////////////////////////////////

	private class Handler implements RuleApplicationHandler {

		Handler() {
		}

		@Override
		public boolean preRuleApplication(Rule rule, Substitution substitution, AtomSet data) {
			return true;
		}

		@Override
		public GIterator<Atom> postRuleApplication(Rule rule, Substitution substitution, AtomSet data,
		    GIterator<Atom> atomsToAdd) {
			InMemoryAtomSet atomset = new LinkedListAtomSet(atomsToAdd);

			for (Atom a : atomset) {
				Predicate p = a.getPredicate();
				for (Rule r : ruleSet.getRulesByBodyPredicate(p)) {
					if (linearRuleCheck(r)) {
						AtomSet set = nextRulesToCheck.get(r);
						if (set == null) {
							set = new LinkedListAtomSet();
							nextRulesToCheck.put(r, set);
						}
						try {
							set.add(a);
						} catch (AtomSetException e) {
							// TODO treat this exception
							e.printStackTrace();
							throw new Error("Untreated exception");
						}
					} else {
						nextRulesToCheck.put(r, atomSet);
					}
				}
			}

			return atomset.iterator();
		}

	}

	private static boolean linearRuleCheck(Rule r) {
		Iterator<Atom> it = r.getBody().iterator();
		if (it.hasNext()) {
			it.next();
		} else {
			return false;
		}
		return !it.hasNext();

	}

}

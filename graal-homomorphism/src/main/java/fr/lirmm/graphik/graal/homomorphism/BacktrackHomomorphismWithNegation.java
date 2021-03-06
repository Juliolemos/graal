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
package fr.lirmm.graphik.graal.homomorphism;

import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.ConjunctiveQueryWithNegation;
import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.api.homomorphism.Homomorphism;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismException;
import fr.lirmm.graphik.graal.core.compilation.NoCompilation;
import fr.lirmm.graphik.graal.homomorphism.backjumping.BackJumping;
import fr.lirmm.graphik.graal.homomorphism.backjumping.NoBackJumping;
import fr.lirmm.graphik.graal.homomorphism.bootstrapper.Bootstrapper;
import fr.lirmm.graphik.graal.homomorphism.bootstrapper.StarBootstrapper;
import fr.lirmm.graphik.graal.homomorphism.forward_checking.ForwardChecking;
import fr.lirmm.graphik.graal.homomorphism.forward_checking.NoForwardChecking;
import fr.lirmm.graphik.graal.homomorphism.not.BacktrackIteratorWithNot;
import fr.lirmm.graphik.util.stream.CloseableIterator;

/**
 * This Backtrack is inspired by the Baget Jean-François Thesis (Chapter 5)
 *
 * see also "Backtracking Through Biconnected Components of a Constraint Graph"
 * (Jean-François Baget, Yannic S. Tognetti IJCAI 2001)
 *
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class BacktrackHomomorphismWithNegation extends AbstractHomomorphism<ConjunctiveQueryWithNegation, AtomSet> implements Homomorphism<ConjunctiveQueryWithNegation, AtomSet> {

	private Scheduler       scheduler;
	private Bootstrapper    bootstrapper;
	private ForwardChecking fc;
	private BackJumping     bj;

	public BacktrackHomomorphismWithNegation() {
		this(DefaultScheduler.instance(), StarBootstrapper.instance(), NoForwardChecking.instance(),
		     NoBackJumping.instance());
	}

	public BacktrackHomomorphismWithNegation(Scheduler s) {
		this(s, StarBootstrapper.instance(), NoForwardChecking.instance(), NoBackJumping.instance());
	}

	public BacktrackHomomorphismWithNegation(ForwardChecking fc) {
		this(DefaultScheduler.instance(), StarBootstrapper.instance(), fc, NoBackJumping.instance());
	}

	public BacktrackHomomorphismWithNegation(BackJumping bj) {
		this(DefaultScheduler.instance(), StarBootstrapper.instance(), NoForwardChecking.instance(), bj);
	}

	public BacktrackHomomorphismWithNegation(Scheduler s, BackJumping bj) {
		this(s, StarBootstrapper.instance(), NoForwardChecking.instance(), bj);
	}

	public BacktrackHomomorphismWithNegation(Scheduler s, Bootstrapper bs, ForwardChecking fc, BackJumping bj) {
		super();
		this.fc = fc;
		this.bj = bj;
		this.scheduler = s;
		this.bootstrapper = bs;
	}

	// /////////////////////////////////////////////////////////////////////////
	// SETTERS
	// /////////////////////////////////////////////////////////////////////////

	/**
	 * @param scheduler
	 *            the scheduler to set
	 */
	public void setScheduler(Scheduler scheduler) {
		this.scheduler = scheduler;
	}

	/**
	 * @param bootstrapper
	 *            the bootstrapper to set
	 */
	public void setBootstrapper(Bootstrapper bootstrapper) {
		this.bootstrapper = bootstrapper;
	}

	/**
	 * @param fc
	 *            the ForwardChecking to set
	 */
	public void setFc(ForwardChecking fc) {
		this.fc = fc;
	}

	/**
	 * @param bj
	 *            the BackJumping to set
	 */
	public void setBj(BackJumping bj) {
		this.bj = bj;
	}

	// /////////////////////////////////////////////////////////////////////////
	// HOMOMORPHISM METHODS
	// /////////////////////////////////////////////////////////////////////////


	public <U1 extends ConjunctiveQueryWithNegation, U2 extends AtomSet> CloseableIterator<Substitution> execute(U1 q, U2 a) throws HomomorphismException {
		return new BacktrackIteratorWithNot(q.getPositiveAtomSet(), q.getNegativeAtomSet(), a, q.getAnswerVariables(),
		                                                            this.scheduler, this.bootstrapper, this.fc,
		                                                            this.bj, NoCompilation.instance());
	}

}

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
package fr.lirmm.graphik.graal.store.jenatdb;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.tdb.TDBFactory;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.Predicate;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.core.DefaultAtom;
import fr.lirmm.graphik.util.stream.AbstractCloseableIterator;

class AtomIterator extends AbstractCloseableIterator<Atom> {

	private Dataset             dataset;
	private ResultSet           rs;
	private QueryExecution      qExec;
	
	private Term subject = null;
	private Predicate predicate = null;
	private Term object = null;


	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTOR
	// /////////////////////////////////////////////////////////////////////////

	public AtomIterator(String directory, String query) {
		this(directory, query, null, null, null);
	}
	
	public AtomIterator(String directory, String query, Term subject, Predicate predicate, Term object) {
		this.dataset = TDBFactory.createDataset(directory);
		this.dataset.begin(ReadWrite.READ);
		this.qExec = QueryExecutionFactory.create(query, this.dataset);
		this.rs = qExec.execSelect();
		
		this.subject = subject;
		this.predicate = predicate;
		this.object = object;
	}


	// /////////////////////////////////////////////////////////////////////////
	// METHODS
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public void close() {
		if (this.qExec != null) {
			this.qExec.close();
		}
		this.dataset.end();
	}

	public void remove() {
		this.rs.remove();
	}

	@Override
	public boolean hasNext() {
		if (this.rs.hasNext()) {
			return true;
		} else {
			this.close();
			return false;
		}
	}

	@Override
	public Atom next() {
		QuerySolution next = this.rs.next();

		Predicate predicate = this.predicate;
		if(predicate == null)
			predicate = Utils.createPredicate(next.get("?p"), 2);
		
		Term subject = this.subject;
		if(subject == null) {
			subject = Utils.createTerm(next.get("?s"));
		} else if(subject.isVariable()) {
			subject = Utils.createTerm(next.get("?" + subject.getLabel()));
		}
		
		Term object = this.object;
		if (object == null) {
			object = Utils.createTerm(next.get("?o"));
		} else if(object.isVariable()) {
			object = Utils.createTerm(next.get("?" + object.getLabel()));
		}

		return new DefaultAtom(predicate, subject, object);
	}

}
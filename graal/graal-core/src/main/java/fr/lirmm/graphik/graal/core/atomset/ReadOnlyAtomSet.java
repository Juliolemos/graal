package fr.lirmm.graphik.graal.core.atomset;

import java.util.Set;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.Predicate;
import fr.lirmm.graphik.graal.core.Term;
import fr.lirmm.graphik.util.stream.ObjectReader;

public interface ReadOnlyAtomSet extends Iterable<Atom> {

	/**
	 * Returns true if this atom set contains the specified atom.
	 * 
	 * @param atom
	 * @return true if this atom set contains the specified atom.
	 * @throws AtomSetException
	 */
	boolean contains(Atom atom) throws AtomSetException;

	/**
	 * Returns an iterator over the atoms in this atom set.
	 * 
	 * @return an iterator over the atoms in this atom set.
	 */
	@Override
	ObjectReader<Atom> iterator();

	/**
	 * 
	 * @return
	 * @throws AtomSetException
	 */
	Iterable<Predicate> getAllPredicates() throws AtomSetException;

	/**
	 * Returns a collection of all terms in this atom set.
	 * 
	 * @return
	 * @throws IAtomSetException
	 */
	Set<Term> getTerms() throws AtomSetException;

	/**
	 * Returns a collection of all terms of the specified type in this atom set.
	 * 
	 * @param type
	 * @return a collection of all terms of the specified type in this atom set.
	 */
	Set<Term> getTerms(Term.Type type);

	/**
	 * Check if all atoms of this AtomSet are also contained in the specified
	 * AtomSet.
	 * 
	 * @param atomset
	 * @return
	 */
	boolean isSubSetOf(AtomSet atomset);

	boolean isEmpty();

};
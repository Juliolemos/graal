/**
 * 
 */
package fr.lirmm.graphik.obda.writer;

import java.io.IOException;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.atomset.ReadOnlyAtomSet;
import fr.lirmm.graphik.util.stream.ObjectWriter;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 * 
 */
public interface AtomWriter extends ObjectWriter<Atom> {
	
	@Override
	void write(Atom object) throws IOException;

	@Override
	void write(Iterable<Atom> objects) throws IOException;
	
	void write(ReadOnlyAtomSet AtomSet) throws IOException;
}

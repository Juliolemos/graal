/**
 * 
 */
package fr.lirmm.graphik;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import fr.lirmm.graphik.graal.Graal;
import fr.lirmm.graphik.graal.core.ConjunctiveQueriesUnion;
import fr.lirmm.graphik.graal.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.core.Query;
import fr.lirmm.graphik.graal.core.atomset.AtomSet;
import fr.lirmm.graphik.graal.io.dlgp.DlgpParser;
import fr.lirmm.graphik.graal.solver.ConjunctiveQueriesUnionSolver;
import fr.lirmm.graphik.graal.solver.Solver;
import fr.lirmm.graphik.graal.solver.SolverFactoryException;
import fr.lirmm.graphik.graal.solver.SqlSolver;
import fr.lirmm.graphik.graal.store.StoreException;
import fr.lirmm.graphik.graal.store.rdbms.DefaultRdbmsStore;
import fr.lirmm.graphik.graal.store.rdbms.driver.SqliteDriver;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 * 
 */
public class SolverFactoryTest {

	@Test
	public void test() throws IOException, StoreException,
			SolverFactoryException {

		File file = new File(TestUtil.DB_TEST);
		file.delete();
		file.createNewFile();

		AtomSet atomSet = new DefaultRdbmsStore(new SqliteDriver(file));

		Query query = DlgpParser.parseQuery("?(X) :- p(X).");
		Solver solver = Graal.getSolverFactory().getSolver(query, atomSet);
		Assert.assertTrue(solver instanceof SqlSolver);
	}

	@Test
	public void testUnionConjunctiveQuery() throws IOException, StoreException,
			SolverFactoryException {

		File file = new File(TestUtil.DB_TEST);
		file.delete();
		file.createNewFile();

		AtomSet atomSet = new DefaultRdbmsStore(new SqliteDriver(file));

		ConjunctiveQuery query1 = DlgpParser.parseQuery("?(X) :- p(X).");
		ConjunctiveQuery query2 = DlgpParser.parseQuery("?(Y) :- q(Y).");
		ConjunctiveQueriesUnion ucq = new ConjunctiveQueriesUnion(query1,
				query2);

		Solver solver = Graal.getSolverFactory().getSolver(ucq, atomSet);
		Assert.assertTrue(solver instanceof ConjunctiveQueriesUnionSolver);
	}
}

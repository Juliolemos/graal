/**
 * 
 */
package fr.lirmm.graphik.graal.store.rdbms;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.lirmm.graphik.graal.core.HashMapSubstitution;
import fr.lirmm.graphik.graal.core.Substitution;
import fr.lirmm.graphik.graal.core.Term;
import fr.lirmm.graphik.graal.core.stream.SubstitutionReader;
import fr.lirmm.graphik.graal.store.StoreException;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 * 
 */
public class ResultSetSubstitutionReader implements SubstitutionReader {

    private static final Logger logger = LoggerFactory
            .getLogger(ResultSetSubstitutionReader.class);
    private ResultSet results;
    private ResultSetMetaData metaData;
    private Statement statement;

    private boolean hasNextCallDone = false;
    private boolean hasNext;
	private boolean isBooleanQuery;
	private RdbmsStore store;

    // /////////////////////////////////////////////////////////////////////////
    //  CONSTRUCTORS
    // /////////////////////////////////////////////////////////////////////////

    /**
     * @param store
     * @param sqlQuery
     * @throws SQLException
     * @throws StoreException 
     */
    public ResultSetSubstitutionReader(RdbmsStore store, String sqlQuery) throws SQLException, StoreException {
    	this.store = store;
		this.statement = store.getDriver().getConnection().createStatement();
        this.results = statement.executeQuery(sqlQuery);
        this.metaData = results.getMetaData();
        this.isBooleanQuery = false;
    }
    
    /**
     * 
     * @param store
     * @param sqlQuery
     * @param isBooleanQuery
     * @throws SQLException
     * @throws StoreException
     */
	public ResultSetSubstitutionReader(RdbmsStore store, String sqlQuery,
			boolean isBooleanQuery) throws SQLException, StoreException {
		this.store = store;
		this.statement = store.getDriver().getConnection().createStatement();
        this.results = statement.executeQuery(sqlQuery);
        this.metaData = results.getMetaData();
		this.isBooleanQuery = isBooleanQuery;
	}

	protected void finalize() {
        this.close();
    }

    // /////////////////////////////////////////////////////////////////////////
    //  METHODS
    // /////////////////////////////////////////////////////////////////////////

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Iterator#remove()
     */
    @Override
    public void remove() {
        // TODO implement this method
        throw new Error("This method isn't implemented");
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.lirmm.graphik.kb.stream.ISubstitutionReader#hasNext()
     */
    @Override
    public boolean hasNext() {
        if (!this.hasNextCallDone) {
            this.hasNextCallDone = true;

            try {
                this.hasNext = this.results.next();
            } catch (SQLException e) {
                logger.error("Error during atom reading", e);
                this.hasNext = false;
            }
        }

        return this.hasNext;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.lirmm.graphik.kb.stream.ISubstitutionReader#next()
     */
    @Override
    public Substitution next() {
        if (!this.hasNextCallDone)
            this.hasNext();

        this.hasNextCallDone = false;

        try {
            Substitution substitution = new HashMapSubstitution();
            if(!isBooleanQuery) {
	            for (int i = 1; i <= this.metaData.getColumnCount(); ++i) {
	                Term term = new Term(this.metaData.getColumnLabel(i),
	                        Term.Type.VARIABLE);
	                Term substitut = this.store.getTerm(this.results.getString(i));
	                substitution.put(term, substitut);
	            }
            }
            return substitution;
        } catch (Exception e) {
        	logger.error("Error while reading the next substitution", e);
            return null;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.lirmm.graphik.kb.stream.ISubstitutionReader#iterator()
     */
    @Override
    public Iterator<Substitution> iterator() {
        return this;
    }

    /* (non-Javadoc)
     * @see fr.lirmm.graphik.kb.stream.ISubstitutionReader#close()
     */
    @Override
    public void close() {
        try {
            this.results.close();
        } catch (SQLException e) {
            logger.warn(e.getMessage(), e);
        }
    }
    
    

}

/* Graal v0.7.4
 * Copyright (c) 2014-2015 Inria Sophia Antipolis - Méditerranée / LIRMM (Université de Montpellier & CNRS)
 * All rights reserved.
 * This file is part of Graal <https://graphik-team.github.io/graal/>.
 *
 * Author(s): Clément SIPIETER
 *            Mélanie KÖNIG
 *            Swan ROCHER
 *            Jean-François BAGET
 *            Michel LECLÈRE
 *            Marie-Laure MUGNIER
 */
 /**
 * 
 */
package fr.lirmm.graphik.graal.io;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public class ParseError extends Error {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6399295619031902779L;

	public ParseError(String msg, Throwable t) {
		super(msg,t);
	}
}

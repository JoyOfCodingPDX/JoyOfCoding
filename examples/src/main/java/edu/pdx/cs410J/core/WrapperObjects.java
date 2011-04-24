package edu.pdx.cs410J.core;

import java.util.*;

/**
 * Demonstrates how wrapper objects can be used to store primitive
 * values in a collection.
 *
 * @author David Whitlock
 * @since Winter 2007
 */
public class WrapperObjects {

	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		Collection c = new ArrayList();
		c.add(new Integer(4));
		c.add(new Double(5.3));
		c.add(new Boolean(false));

		System.out.println(c);
	}

}
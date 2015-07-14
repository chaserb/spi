package com.spi.test.util;

/**
 * Functional interface for functions that throw Throwable exceptions.
 * 
 * @author chase.barrett
 */
@FunctionalInterface
public interface ExceptionThrower {
	
	/**
	 * Instructs the exception thrower to throw its exception.
	 * 
	 * @throws Throwable
	 */
	public void throwException() throws Throwable;

}

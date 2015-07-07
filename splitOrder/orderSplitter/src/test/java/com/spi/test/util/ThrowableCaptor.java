package com.spi.test.util;

/**
 * Test class that will invoke a given {@link ExceptionThrower} and capture
 * its thrown Throwable for later assertions.
 * 
 * @author chase.barrett
 */
public class ThrowableCaptor {

	public static Throwable captureThrowable(ExceptionThrower thrower) {
		try {
			thrower.throwException();
			return null;
		} catch (Throwable t) {
			return t;
		}
	}
}

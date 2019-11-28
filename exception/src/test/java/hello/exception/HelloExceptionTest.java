package hello.exception;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class HelloExceptionTest extends TestCase {

	public HelloExceptionTest() {
		super("Hello Exception Test");
	}
	
	public static Test suite() {
		return new TestSuite(HelloExceptionTest.class);
	}
	
	public void testHelloException() {
		HelloException helloException = new HelloException();
		assertEquals(5,helloException.saySize("Hello"));
		assertEquals(1,helloException.saySize("A"));
	}
}

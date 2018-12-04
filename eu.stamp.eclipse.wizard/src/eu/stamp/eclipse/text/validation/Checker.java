package eu.stamp.eclipse.text.validation;

public abstract class Checker {
		
		private final String message;
		
		public Checker(String message){ this.message = message; }

		public abstract boolean check(String arg);

		public String getMessage() { return message; }		
}

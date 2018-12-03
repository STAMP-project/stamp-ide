package eu.stamp.eclipse.text.validation;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import com.richclientgui.toolbox.validation.validator.IFieldValidator;

public class TextFieldValidatorFactory {
	
	public static final int NONE = 0;
	public static final int ERROR = 1;
	public static final int WARNING = 2;
	
	protected int onlyNumbers;
	protected int onlyAlphaNumerical;
	protected int extra;
	protected int forbiden;
	protected int file;
	protected int pointsFile;
	protected int pointsDirectory;
	protected int noEmpty;
	
	protected char[] extraChars;
	protected char[] forbidenChars;
	
	//protected stringChecker errorChecker;
	
	protected List<Checker> errorCheckers;
	
	//protected stringChecker warningChecker;
	
	protected List<Checker> warningCheckers;
	
	protected TextFieldValidatorFactory() {
		errorCheckers = new LinkedList<Checker>();
		warningCheckers = new LinkedList<Checker>();
	}
	
	public static TextFieldValidatorFactory getFactory() {
		return new TextFieldValidatorFactory();
	}
	
	public TextFieldValidatorFactory onlyNumbers(int code) {
        onlyNumbers = code;
	    return this;
	}
	
	public TextFieldValidatorFactory onlyAlphaNumerical(int code) {
        onlyAlphaNumerical = code; 
	    return this;
	}
	
	public TextFieldValidatorFactory setExtraCharactersAllowed(
			char[] extraCharactersAllowed, int code) {
        extraChars = extraCharactersAllowed;
        extra = code;
		return this;
	}
	
	public TextFieldValidatorFactory setExtraForbidenCharacters(
			char[] extraForbidenCharacters,int code) {
        forbiden = code;
        forbidenChars = extraForbidenCharacters;
		return this;
	}
	
	public TextFieldValidatorFactory fileMustExist(int code) {
        file = code;
		return this;
	}
	
	public TextFieldValidatorFactory pointsToFile(int code) {
        pointsFile = code;
		return this;
	}
	
	public TextFieldValidatorFactory pointsToDirectory(int code) {
        pointsDirectory = code;
		return this;
	}
	
	public TextFieldValidatorFactory notEmpty(int code) {
        noEmpty = code;
		return this;
	}
	
	public IFieldValidator<String> getValidator(){
		return generateValidator();
	}
	
	protected MyStringValidator generateValidator(){
		
		if(noEmpty != NONE) {
			Checker checker = new Checker("can not be empty") {
				@Override
				public boolean check(String arg) {
					if(arg == null) return false;
					if(arg.isEmpty()) return false;
					return true;
				}
			};
			add(noEmpty, checker);
		}
		if(onlyNumbers != NONE) {
			Checker checker = new Checker("Only numbers") {
				@Override
				public boolean check(String arg) {
                  String sr = arg.replaceAll("[0-9]","");
				  return sr.isEmpty();
				}	
			};
		  add(onlyNumbers,checker);
		}
		if(onlyAlphaNumerical != NONE) {
			Checker checker = new Checker("Only AlphaNumerical characters") {
				@Override
				public boolean check(String arg) {
					String sr = arg.replaceAll("[0-9a-zA-Z]","");
					return sr.isEmpty();
				}	
			};
		  add(onlyAlphaNumerical,checker);
		}
        if(file != NONE) {
        	Checker checker = new Checker("doesn't match an existing file") {
				@Override
				public boolean check(String arg) {
					File f = new File(arg);
					return f.exists();
				}	
        	};
        	add(file,checker);
        }
        if(pointsFile != NONE) {
        	Checker checker = new Checker("is not a file") {
				@Override
				public boolean check(String arg) {
					File f = new File(arg);
					if(!f.exists()) return false;
					return f.isFile();
				}
        	};
        	add(pointsFile,checker);
        }
        if(pointsDirectory != NONE) {
        	Checker checker = new Checker("is not a directory") {
				@Override
				public boolean check(String arg) {
					File f = new File(arg);
					if(!f.exists()) return false;
					return f.isDirectory();
				}
        	};
        	add(pointsDirectory,checker);
        }
        if(forbiden != NONE) {
        	Checker checker = new CharChecker(
        			"contains non allowed characters ",forbidenChars); 
        	add(forbiden,checker);
        }
		return new MyStringValidator(errorCheckers,warningCheckers,
				extraChars);
	}
	
	public void add(int place, Checker checker) {
		if(checker == null) return;
		if(place == ERROR) {
			errorCheckers.add(checker);
			return;
		}
		if(place == WARNING)
			warningCheckers.add(checker);
	}
	
	public void applyTo(IObjectWithTextValidation target) {
		MyStringValidator val = generateValidator();
		val.setName(target.getName());
		target.setFieldValidator(val);
	}
	
	protected class CharChecker extends Checker {

		final char[] chars;
		
		CharChecker(String message,char[] chars) {
			super(message);
			this.chars = chars;
		}

		@Override
		public boolean check(String arg) {
			for(char ch : chars)if(arg.indexOf(ch) > -1)
				return false;
			return true;
		}
	}
	
	protected class MyStringValidator implements IFieldValidator<String> {
     
		final List<Checker> errorCheckers;
		
		final List<Checker> warningCheckers;
		
		final char[] allowed;
		
		String name;
		
		String message;
		
		MyStringValidator(List<Checker> errorCheckers, List<Checker> warningCheckers,
				char[] allowed){
			this.errorCheckers = errorCheckers;
			this.warningCheckers = warningCheckers;
			this.allowed = allowed;
			message = "";
		}
		@Override
		public String getErrorMessage() { 
			if(name != null) return name + " " + message;
			return message; 
			}
		
		@Override
		public String getWarningMessage() { 
			if(name != null) return name + " " + message;
			return message; 
			}
		
		@Override
		public boolean isValid(String arg) { 
			if(allowed != null)if(allowed.length > 0)
				for(char ch : allowed)
					arg = removeChar(ch,arg);
		
		     for(Checker checker : errorCheckers)
		    	   if(!checker.check(arg)) {
		    		   message = checker.getMessage();
		    		   return false;
		    	   }
		     return true;
		}
		
		@Override
		public boolean warningExist(String arg) { 
			for(Checker checker : warningCheckers)
				if(!checker.check(arg)) {
					message = checker.getMessage();
					return true;
				}
			return false;
			}	
		
		void setName(String name) { this.name = name; }
		
		String removeChar(char ch, String arg) {
			int n = arg.indexOf(ch);
			if(n < 0) return arg;
			if(n == 0) {
				if(arg.length() > 1) return removeChar(ch,arg.substring(1));
				return "";
			}
			if(arg.length() > n) {
				String result = arg.substring(0,n-1) + arg.substring(n+1);
			    if(result.indexOf(ch) > -1) result = removeChar(ch,result);
			    return result;
			}
			return "";
		}
	}
	
}

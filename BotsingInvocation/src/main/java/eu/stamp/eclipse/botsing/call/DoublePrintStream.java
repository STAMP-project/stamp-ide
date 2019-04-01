package eu.stamp.eclipse.botsing.call;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Locale;

public class DoublePrintStream extends PrintStream {
	
	private final PrintStream consoleStream;
	
	public DoublePrintStream(PrintStream consoleStream,File file) throws FileNotFoundException{
		super(file);
		this.consoleStream = consoleStream;
	}
	@Override
	public void flush() {
		super.flush();
		consoleStream.flush();
	}

	@Override
	public void close() {
        consoleStream.close();
		super.close();
	}

	@Override
	public void write(int b) {
        consoleStream.write(b);
		super.write(b);
	}

	@Override
	public void write(byte[] buf, int off, int len) {
        consoleStream.write(buf, off, len);
		super.write(buf, off, len);
	}

	@Override
	public void print(boolean b) {
        consoleStream.print(b);
		super.print(b);
	}

	@Override
	public void print(char c) {
		consoleStream.println(c);
		super.print(c);
	}

	@Override
	public void print(int i) {
		consoleStream.println(i);
		super.print(i);
	}

	@Override
	public void print(long l) {
		consoleStream.println(l);
		super.print(l);
	}

	@Override
	public void print(float f) {
		consoleStream.println(f);
		super.print(f);
	}

	@Override
	public void print(double d) {
		consoleStream.println(d);
		super.print(d);
	}

	@Override
	public void print(char[] s) {
		consoleStream.println(s);
		super.print(s);
	}

	@Override
	public void print(String s) {
		consoleStream.print(s);
		super.print(s);
	}

	@Override
	public void print(Object obj) {
	    consoleStream.print(obj);
		super.print(obj);
	}

	@Override
	public void println() {
		consoleStream.println();
		super.println();
	}

	@Override
	public void println(boolean x) {
		consoleStream.println(x);
		super.println(x);
	}

	@Override
	public void println(char x) {
		consoleStream.println(x);
		super.println(x);
	}

	@Override
	public void println(int x) {
		consoleStream.println(x);
		super.println(x);
	}

	@Override
	public void println(long x) {
		consoleStream.println(x);
		super.println(x);
	}

	@Override
	public void println(float x) {
		consoleStream.println(x);
		super.println(x);
	}

	@Override
	public void println(double x) {
		consoleStream.println(x);
		super.println(x);
	}

	@Override
	public void println(char[] x) {
		consoleStream.println(x);
		super.println(x);
	}

	@Override
	public void println(String x) {
		consoleStream.println(x);
		super.println(x);
	}

	@Override
	public void println(Object x) {
		consoleStream.println(x);
		super.println(x);
	}

	@Override
	public PrintStream printf(String format, Object... args) {
		consoleStream.printf(format, args);
		return super.printf(format, args);
	}

	@Override
	public PrintStream printf(Locale l, String format, Object... args) {
		consoleStream.printf(l,format, args);
		return super.printf(l, format, args);
	}

	@Override
	public PrintStream format(String format, Object... args) {
		consoleStream.format(format, args);
		return super.format(format, args);
	}

	@Override
	public PrintStream format(Locale l, String format, Object... args) {
		consoleStream.format(l, format, args);
		return super.format(l, format, args);
	}

	@Override
	public PrintStream append(CharSequence csq) {
		consoleStream.append(csq);
		return super.append(csq);
	}

	@Override
	public PrintStream append(CharSequence csq, int start, int end) {
		consoleStream.append(csq,start,end);
		return super.append(csq, start, end);
	}

	@Override
	public PrintStream append(char c) {
		consoleStream.append(c);
		return super.append(c);
	}

	@Override
	public void write(byte[] b) throws IOException {
		consoleStream.write(b);
		super.write(b);
	}
	
}


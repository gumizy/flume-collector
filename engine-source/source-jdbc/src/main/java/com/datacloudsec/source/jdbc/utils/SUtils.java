
package com.datacloudsec.source.jdbc.utils;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * String utilities
 * @author daniellanzagarcia
 *
 */
public class SUtils {
	
	public static final Pattern EMPTY_LINE_PATTERN = Pattern.compile("^\\s*$");
	public static final Pattern PROPERTY_PATTERN = Pattern.compile("^([A-z,0-9]+)[ ]+=[ ]+(.+)");
	
	public final static String EOL = System.getProperty("line.separator");
	
	public static List<String> toLines(String in) {
		return Arrays.asList(in.split("\n"));
	}

	public static List<String> grep(List<String> lines, String regex) {
		return grep(lines, Pattern.compile(".*" + regex + ".*"));
	}

	public static List<String> grep(List<String> lines, Pattern pattern) {
		List<String> returnLines = new LinkedList<>();
		
		for (String line : lines)
			if(pattern.matcher(line).matches())
				returnLines.add(line);
		
		return returnLines;
	}

	public static List<String> linesFromTo(List<String> lines, Pattern patternStart, Pattern patternEnd) {
		List<String> returnLines = new LinkedList<>();
		
		Iterator<String> it = lines.iterator();
		
		while (it.hasNext()){
			String line = it.next();
			
			if(patternStart.matcher(line).matches()){
				returnLines.add(line);
				
				break;
			}
		}
		
		while (it.hasNext()){
			String line = it.next();
			
			returnLines.add(line);
			
			if(patternEnd.matcher(line).matches())
				break;
		}
		
		return returnLines;	
	}

	public static String join(List<String> lines, char delim) {
		StringBuilder sb = new StringBuilder();
		
		for (String string : lines) {
			sb.append(string);
			sb.append(delim);
		}
		
		return sb.toString();
	}

	public static List<String> linesFrom(List<String> linesClone, Pattern pattern) {
		List<String> returnLines = new LinkedList<>();
		
		Iterator<String> it = linesClone.iterator();
		
		while (it.hasNext()){
			String line = it.next();
			
			if(pattern.matcher(line).matches()){
				returnLines.add(line);
				break;
			}
		}
		
		while (it.hasNext()){
			String line = it.next();
			
			returnLines.add(line);
		}
		
		return returnLines;	
	}

	public static List<String> linesBefore(List<String> lines, Pattern pattern, int number) {
		LimitedQueue<String> returnLines = new LimitedQueue<String>(number);

		Iterator<String> it = lines.iterator();

		while (it.hasNext()){
			String line = it.next();

			if(pattern.matcher(line).matches())
				break;

			returnLines.add(line);
		}

		return returnLines;
	}

	public static String getFirstLine(String text) {
		int index_eol = text.indexOf(EOL);

		if(index_eol > 0)
			return text.substring(0, index_eol);
		else
			return text;
	}
	
}

package server;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextExtraction {

	public static String useRegex(final String input, final String regex) {
		// Compile regular expression
		final Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		// Match regex against input
		final Matcher matcher = pattern.matcher(input);
		// Use results...
		if (matcher.find()) {
			return matcher.group();
		}
		return "";
	}

	public String extractReqFileDir(final String input) {
		return useRegex(input, "(?<=(GET /))[^ ]+");
	}

	public String extractExt(final String input) {
		return useRegex(input, "(?<=(\\.))[^ ]+");
	}

	public String extractHost(final String input) {
		return useRegex(input, "(?<=(Host: ))[^ ]+");
	}

}

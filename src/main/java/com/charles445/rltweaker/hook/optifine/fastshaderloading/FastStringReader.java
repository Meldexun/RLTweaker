package com.charles445.rltweaker.hook.optifine.fastshaderloading;

class FastStringReader {

	private final String input;
	private int pos;
	private final int len;

	FastStringReader(String input) {
		this.input = input;
		this.len = input.length();
	}

	boolean read(char expected) {
		return pos < len && input.charAt(pos++) == expected;
	}

	boolean read(CharSequence expected) {
		for (int i = 0; i < expected.length(); i++) {
			if (pos >= len || input.charAt(pos++) != expected.charAt(i)) {
				return false;
			}
		}
		return true;
	}

	boolean readOptional(char expected) {
		if (pos < len && input.charAt(pos) == expected) {
			pos++;
			return true;
		}
		return false;
	}

	boolean readOptional(CharSequence expected) {
		if (expected.length() > len - pos) {
			return false;
		}
		for (int i = 0; i < expected.length(); i++) {
			if (input.charAt(pos + i) != expected.charAt(i)) {
				return false;
			}
		}
		pos += expected.length();
		return true;
	}

	int readWhile(CharPredicate predicate) {
		int start = pos;
		while (pos < len && predicate.test(input.charAt(pos))) {
			pos++;
		}
		return pos - start;
	}

	String readMatching(CharPredicate predicate) {
		int start = pos;
		while (pos < len && predicate.test(input.charAt(pos))) {
			pos++;
		}
		return input.substring(start, pos);
	}

	String readAll() {
		String result = input.substring(pos, len);
		pos = len;
		return result;
	}

	boolean endReached() {
		return pos >= len;
	}

	static boolean isWhitespace(char c) {
		return c == ' ' || c == '\t';
	}

	static boolean isWord(char c) {
		return ('a' <= c && c <= 'z') || ('A' <= c && c <= 'Z') || ('0' <= c && c <= '9') || c == '_' || c == '.' || c == '-';
	}

}

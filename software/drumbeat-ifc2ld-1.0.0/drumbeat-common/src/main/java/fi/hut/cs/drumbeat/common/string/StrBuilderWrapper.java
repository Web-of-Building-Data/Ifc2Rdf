package fi.hut.cs.drumbeat.common.string;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.text.StrBuilder;
import org.apache.commons.lang3.text.StrMatcher;

public class StrBuilderWrapper {
	
	public static final Pattern INTEGER					= Pattern.compile("^[+-]?\\p{Digit}+");			 					// or \\p{Digit}+
	public static final Pattern DOUBLE						= Pattern.compile("^[+-]?\\p{Digit}+(\\.)?\\p{Digit}*([eE][+-]?\\p{Digit}+)?");
	public static final Pattern STRING_BETWEEN_APOSTROPHES = Pattern.compile("^\\'([^\\']|(\\'\\'))*\\'");
	public static final Pattern IDENTIFIER_NAME			= Pattern.compile("^\\p{Alpha}\\p{Alnum}*");

	InternalStrBuilderEx internal;
	
	public StrBuilderWrapper(String str) {
		internal = new InternalStrBuilderEx(str);
	}
	
	public char charAt(int pos) {
		return internal.charAt(internal.currentPos + pos);
	}
	
	public boolean isEmpty() {
		return internal.currentPos == internal.internalSize;
	}
	
	public StrBuilderWrapper trim() {
		internal.trimLeft();
		internal.trimRight();
		return this;
	}
	
	public StrBuilderWrapper trimLeft() {
		internal.trimLeft();
		return this;
	}
	
	public StrBuilderWrapper trimRight() {
		internal.trimRight();
		return this;
	}
	
	public String substring(int startIndex, int endIndex) {
		return internal.substring(startIndex - internal.currentPos, endIndex - internal.currentPos);
	}	

	public String getFirstMatch(Pattern pattern) {
		return internal.getFirstMatch(pattern);
	}
	
	public String getFirstMatch(String regex) {
		return internal.getFirstMatch(Pattern.compile(regex));
	}
	
	public long getLong() throws NumberFormatException {
		String s = internal.getFirstMatch(INTEGER);		
		return Long.parseLong(s);
	}
	
	public double getDouble() {
		String s = internal.getFirstMatch(DOUBLE); 
		return Double.parseDouble(s);		
	}
	
	public StrBuilderWrapper skip(int number) {
		if (internal.currentPos + number > internal.internalSize) {
			throw new StringIndexOutOfBoundsException();
		}
		internal.currentPos += number;
		return this;
	}
	
	public String getStringBetweenBrackets(char openingBracketChar, char closingBracketChar) {
		return internal.getStringBetweenBrackets(openingBracketChar, closingBracketChar);
	}

	public String getStringBetweenRoundBrackets() {
		return internal.getStringBetweenBrackets(StringUtils.OPENING_ROUND_BRACKET_CHAR, StringUtils.CLOSING_ROUND_BRACKET_CHAR);
	}
	
	public String getStringBetweenCurlyBrackets() {
		return internal.getStringBetweenBrackets(StringUtils.OPENING_CURLY_BRACKET_CHAR, StringUtils.CLOSING_CURLY_BRACKET_CHAR);
	}
	
	public String getStringBetweenSquareBrackets() {
		return internal.getStringBetweenBrackets(StringUtils.OPENING_SQUARE_BRACKET_CHAR, StringUtils.CLOSING_SQUARE_BRACKET_CHAR);
	}	

	public String getStringBetweenSingleQuotes() {
		String result = internal.getFirstMatch(STRING_BETWEEN_APOSTROPHES);
		if (result != null) {
			return result.substring(1, result.length()-1);
		}
		return null;
	}

	public String getStringBetweenSimilarCharacters(char ch) {
		return internal.getStringBetweenSimilarCharacters(ch);
	}
	
	public String getIdentifierName() {
		return internal.getFirstMatch(IDENTIFIER_NAME);
	}
	
	public int indexOf(char ch, int startIndex) {
		int index = internal.indexOf(ch, internal.currentPos + startIndex);
		return index != -1 ? index - internal.currentPos : -1;
	}

	public int indexOf(String s, int startIndex) {
		int index = internal.indexOf(s, internal.currentPos + startIndex);
		return index != -1 ? index - internal.currentPos : -1;
	}
	
	public String getFirstMatch(StrMatcher strMatcher) {
		return internal.getFirstMatch(strMatcher);
	}
	
	@Override
	public String toString() {
		return internal.toString();
	}	
	
	
	/**
	 * Class InternalStrBuilderEx
	 * @author Nam
	 *
	 */
	private class InternalStrBuilderEx extends StrBuilder {		
		
		private static final long serialVersionUID = 1L;
		
		public int currentPos = 0;
		public int internalSize;
		
		public InternalStrBuilderEx(String str) {
			super(str);
			internalSize = size;
		}
		
		public void trimLeft() {
			for (; currentPos < internalSize; ++currentPos) {
				if (buffer[currentPos] > ' ') {
					break;
				}
			}
		}
		
		public void trimRight() {
			for (; internalSize > currentPos; --internalSize) {
				if (buffer[internalSize - 1] > ' ') {
					break;
				}
			}
		}

		public String getFirstMatch(Pattern pattern) {
			if (currentPos == internalSize) {
				return null;
			}
			Matcher matcher = pattern.matcher(subSequence(currentPos, internalSize));
			if (matcher.find() && matcher.start() == 0) {
				int start = currentPos;
				currentPos += matcher.end();
				return substring(start, currentPos);
			}
			return null;
		}
		
		public String getStringBetweenBrackets(char openingBracketChar, char closingBracketChar) {
			trimLeft();
			if (currentPos == internalSize || buffer[currentPos] != openingBracketChar) {
				return null;
			}
			
			int balanceOfBrackets = 1;
			
			for (int i = currentPos + 1; i < internalSize; ++i) {	
				
				if (buffer[i] == openingBracketChar) {
					++balanceOfBrackets;
				} else if (buffer[i] == closingBracketChar) {
					--balanceOfBrackets;
					
					if (balanceOfBrackets == 0) {
						
						String result = substring(currentPos + 1, i);
						currentPos = i + 1;
						return result;
					}
				}
			}
			
			return null;
		}
		
		public String getStringBetweenSimilarCharacters(char ch) {
			if (currentPos == internalSize || buffer[currentPos] != ch) {
				return null;
			}
			for (int i = currentPos + 1; i < internalSize; ++i) {
				if (buffer[i] == ch) {
					String result = substring(currentPos + 1, i);
					currentPos = i + 1;
					return result;				
				}
			}
			return null;
		}
		
		public String getFirstMatch(StrMatcher strMatcher) {
			int length = strMatcher.isMatch(buffer, currentPos);
			if (length > 0) {
				String result = substring(currentPos, currentPos + length);
				currentPos += length;
				return result;				
			}

			return null;
		}
		
		@Override
		public String toString() {
			return substring(currentPos);
		}
		
		
	}
	
}

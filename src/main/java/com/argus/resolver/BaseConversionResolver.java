package com.argus.resolver;

import java.util.ArrayList;
import java.util.List;

import java.util.regex.Pattern;

import com.argus.Query;
import com.argus.QueryResult;
import com.argus.SingleQueryResult;

/**
 * Resolve base/radix conversions, e.g. converting from decimal to
 * binary.
 *
 * @todo We assume all numbers are decimal unless they are obviously
 * not (e.g. 0xFE). If a number is almost certainly binary, e.g.
 * 1010111 then we should provide a conversion from binary.
 * @todo If the user types "1011 to dec" then we should make the
 * assumption the input number is binary not decimal.
 * @todo Support conversion to/from arbitrary bases.
 */
public class BaseConversionResolver implements Resolver
{
    // No need to check uppercase letters as we have normalised the query
    // string.
    private static Pattern hexDigitsPattern = Pattern.compile("[a-f]+");
    
    private static class Number {
        public long number;

        public Number(long number) {
            this.number = number;
        }
        
        /** Convert the number to the given base. */
        public String convertTo(int base) {
            if (base == 2) {
                return "0b" + Long.toBinaryString(number);
            }
            if (base == 8) {
                return "0o" + Long.toOctalString(number);
            }
            if (base == 10) {
                return Long.toString(number);
            }
            if (base == 16) {
                return "0x" + Long.toHexString(number).toUpperCase();
            }

            return null;
        }

        /**
         * Parse a given string into a number. 
         *
         * @param base Pass the base if known or null if not known.
         * In some cases it's possible to determine the base from
         * the string, e.g. "0b101" is binary and "F0" is probably hex.
         */
        public static Number parse(final String number, Integer base) {
            try {
                if (number.startsWith("0x")) {
                    if (base != null && base != 16) {
                        return null;
                    }

                    return new Number(Long.parseLong(number.substring(2), 16));
                }

                // @todo Support octal prefixex:
                // https://en.wikipedia.org/wiki/Octal
                
                else if (number.startsWith("0b")) {
                    if (base != null && base != 2) {
                        return null;
                    }
                    
                    return new Number(Long.parseLong(number.substring(2), 2));
                }
                else if (hexDigitsPattern.matcher(number).matches()) {
                    if (base != null && base != 16) {
                        return null;
                    }                                

                    return new Number(Long.parseLong(number, 16));
                }
                else if (base != null) {
                    if (base >= Character.MIN_RADIX &&
                        base <= Character.MAX_RADIX) {
                        return new Number(Long.parseLong(number, base));
                    }
                    
                    return null;
                }
                else {
                    // @todo We assume all other numbers are decimal, however
                    // if the number only has 0s and 1s we should convert it as
                    // both a binary and a decimal number.
                    // @todo Perhaps if there are x digits (e.g. 10) we can safely
                    // assume it's probably binary?
                    return new Number(Long.parseLong(number));
                }                
            }
            catch (NumberFormatException exception) {
                return null;
            }
        }

        public static Integer parseBase(final String word) {
            if (word.equals("bin") || word.equals("binary")) {
                return 2;
            }
            if (word.equals("oct") || word.equals("octal")) {
                return 8;
            }            
            if (word.equals("dec") || word.equals("decimal")) {
                return 10;
            }
            if (word.equals("hex") || word.equals("hexadecimal")) {
                return 16;
            }

            return null;
        }
        
        public static Number parse(List<String> words) {
            if (words.size() == 1) {
                return parse(words.get(0), null);
            }
            else if (words.size() == 2) {
                Integer base = parseBase(words.get(0));
                String numberString;
                
                if (base == null) {
                    numberString = words.get(0);
                    base = parseBase(words.get(1));
                }
                else {
                    numberString = words.get(1);
                }

                return parse(numberString, base);
            }
            else {
                return null;
            }
        }
    }
    
    public @Override QueryResult tryResolve(final Query query) {
        final List<String> words = query.getNormalisedWordList();
        if (words.size() < 3) return null;

        if (words.get(0).equals("convert")) {
            words.remove(0);
        }

        // @todo Also "whats" and "what is".

        int indexOfIn = words.indexOf("in");
        if (indexOfIn < 0) {
            indexOfIn = words.indexOf("to");             
        }
        
        if (indexOfIn <= 0) {
            return null;
        }


        final Integer base = Number.parseBase(words.get(words.size() - 1));
        if (base == null) return null;
        
        final Number source =
            Number.parse(new ArrayList<>(words.subList(0, indexOfIn)));
        if (source == null) return null;
        
        // @todo We should write out context here.
        return new SingleQueryResult(query, source.convertTo(base));
    }
}

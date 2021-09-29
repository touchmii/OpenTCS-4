package com.lvsrobot.vehicleqian;

import com.google.common.primitives.Ints;

import javax.annotation.Nullable;

public class Parsers {
    /**
     * Prevents instantiation of this utility class.
     */
    private Parsers() {
    }

    /**
     * Parses a String to an int. If <code>toParse</code> could not be parsed successfully then the
     * default value <code>retOnFail</code> is returned.
     *
     * @param toParse the <code>String</code> to be parsed.
     * @param retOnFail default value that is returned if the <code>String</code> could not be parsed.
     * @return if the <code>String</code> could be parsed then the int value of the
     * <code>String</code> is returned, else retOnFail
     */
    public static int tryParseString(@Nullable String toParse, int retOnFail) {

        if (toParse == null) {
            return retOnFail;
        }
        Integer parseTry = Ints.tryParse(toParse);
        if (parseTry == null) {
            return retOnFail;
        }
        return parseTry;

    }
}

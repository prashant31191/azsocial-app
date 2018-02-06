package com.azsocial.utils;

/**
 * Created by prashant.chovatiya on 2/6/2018.
 */

public class StringUtils {

    public static boolean isValidString(String string)
    {
        if(string !=null && string.length() > 0)
            return  true;
        else
            return  false;
    }

    public static String setString(String string)
    {
        if(string !=null && string.length() > 0)
            return  string;
        else
            return  "";
    }
}

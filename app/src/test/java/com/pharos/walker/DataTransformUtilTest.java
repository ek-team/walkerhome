package com.pharos.walker;


import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;


public class DataTransformUtilTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void stringToList() {
        String test = "[123,456,789]";
        stringToList(test);
    }
    public static List<String> stringToList(String string){
        List<String> stringList = new ArrayList<>();
        String dealResult = trimBothEndsChars(string,"\\[","\\]");
        String [] strings = dealResult.split(",");
        for (int i = 0; i < strings.length; i++){
            stringList.add(i,strings[i]);
            System.out.println(strings[i]);
        }
        return stringList;
    }
    /**
     * 去除字符串首尾两端指定的字符
     * */
    public static String trimBothEndsChars(String srcStr, String startSp,String endSp) {
        String regex = "^" + startSp + "*|" + endSp + "*$";
        return srcStr.replaceAll(regex, "");
    }
}
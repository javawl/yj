package com.yj.util;

import java.util.ArrayList;
import java.util.List;

public class Validate {
    public static boolean checkValueInOneZero(List<String> input){
        for (int i = 0; i < input.size(); i++){
            //不等于0并且不等于1
            if (!input.get(i).equals("0") && !input.get(i).equals("1")){
                return false;
            }
        }
        return true;
    }

    public static boolean checkValueInOneTwoZero(List<String> input){
        for (int i = 0; i < input.size(); i++){
            //不等于0并且不等于1
            if (!input.get(i).equals("0") && !input.get(i).equals("1") && !input.get(i).equals("2")){
                return false;
            }
        }
        return true;
    }
}

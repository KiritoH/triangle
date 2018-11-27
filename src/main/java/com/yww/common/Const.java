package com.yww.common;


import com.google.common.collect.Sets;

import java.util.Set;

public class Const {

    public enum TriangleTypeEnum{

        NO_TRIANGLE(0,"不是三角形"),
        ISOSCELES_TRIANGLE(10,"等腰三角形"),
        EQUILATERAL_TRIANGLE(20,"等边三角形"),
        ISOSCELES_RIGHT_TRIANGLE(30,"等腰直角三角形"),
        RIGHT_TRIANGLE(40,"直角三角形"),
        ACUTE_TRIANGLE(50,"锐角三角形"),
        OBTUSE_TRIANGLE(60,"钝角三角形");

        TriangleTypeEnum(int code, String value){
            this.code = code;
            this.value = value;
        }

        private String value;
        private int code;
        public String getValue(){
            return value;
        }

        public int getCode(){
            return code;
        }

        //用于防止没有对应枚举时报错
        public static TriangleTypeEnum codeOf(int code){
            for (TriangleTypeEnum triangleTypeEnum : values()){
                if(triangleTypeEnum.getCode() == code){
                    return triangleTypeEnum;
                }
            }
            throw new RuntimeException("没有找到对应的枚举");
        }
    }


    //利用接口来进行分组
    public interface Result{
        int PASS = 0; //通过
        int FAIL = 1; //失败

    }


}

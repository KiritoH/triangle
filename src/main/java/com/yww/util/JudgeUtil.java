package com.yww.util;

import com.yww.common.Const;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Arrays;


public class JudgeUtil {
    //判断三角形类型
    public int judge(Double a, Double b, Double c){

        //调用库函数，使得side_C为最大边
        double arr [] = {a,b,c};
        Arrays.sort(arr);
        double side_A = arr[0];
        double side_B = arr[1];
        double side_C = arr[2];


        //判断是否为三角形
        if (side_C < side_A + side_B){
            //判断等边
            System.out.println(side_A+" "+side_B+" "+side_C+"");

            if (side_A == side_B && side_B == side_C){
                System.out.println("等边三角形");
                return Const.TriangleTypeEnum.EQUILATERAL_TRIANGLE.getCode();
            }
            //判断等腰
            if (side_A == side_B || side_B == side_C || side_A == side_C){
                //判断直角,由于excel表传值的过程会有精度损失，且并没有开根号数字的近似类
                //而等腰直角三角形必定会有一个数是存在根号的
                //所以这里牺牲一些精度来进行判断，以近似结果代替真实结果
                //所以如果遇到十分近似值的时候可能会得到错误的结果，所以精度将会在使用前进行说明。
                BigDecimal   tem_C   =   new BigDecimal(side_C*side_C);
                //进行四舍五入然后保留两位。
                double C = tem_C.setScale(2,   RoundingMode.HALF_UP).doubleValue();
                BigDecimal   tem_AB   =   new BigDecimal(side_A*side_A+side_B*side_B);
                double AB = tem_AB.setScale(2,   RoundingMode.HALF_UP).doubleValue();
                if (AB == C){
                    System.out.println("等腰直角三角形");
                    return Const.TriangleTypeEnum.ISOSCELES_RIGHT_TRIANGLE.getCode();
                }
                System.out.println("等腰三角形");
                return Const.TriangleTypeEnum.ISOSCELES_TRIANGLE.getCode();
            }
            //判断直角
            if (side_C*side_C == side_A*side_A+side_B*side_B){
                System.out.println("直角三角形");
                return Const.TriangleTypeEnum.RIGHT_TRIANGLE.getCode();
            }
            //判断锐角
            if (side_C*side_C < side_A*side_A+side_B*side_B){
                System.out.println("锐角三角形");
                return Const.TriangleTypeEnum.ACUTE_TRIANGLE.getCode();
            }else {
                System.out.println("钝角三角形");
                return Const.TriangleTypeEnum.OBTUSE_TRIANGLE.getCode();
            }
        }else {
            System.out.println("无法构成三角形");
            return Const.TriangleTypeEnum.NO_TRIANGLE.getCode();
        }

    }
}

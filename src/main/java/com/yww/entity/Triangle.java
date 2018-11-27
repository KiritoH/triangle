package com.yww.entity;

import com.yww.util.JudgeUtil;

public class Triangle {
    private Double side_A;
    private Double side_B;
    private Double side_C;

    public Triangle(Double side_A, Double side_B, Double side_C) {
        JudgeUtil judgeUtil = new JudgeUtil();
        int type = judgeUtil.judge(side_A,side_B,side_C);
        if (type == 0){
            System.out.println("无法构成三角形");
            return;
        }
        this.side_A = side_A;
        this.side_B = side_B;
        this.side_C = side_C;
    }

    public Double getSide_A() {
        return side_A;
    }

    public void setSide_A(double side_A) {
        this.side_A = side_A;
    }

    public Double getSide_B() {
        return side_B;
    }

    public void setSide_B(double side_B) {
        this.side_B = side_B;
    }

    public Double getSide_C() {
        return side_C;
    }

    public void setSide_C(double side_C) {
        this.side_C = side_C;
    }
}

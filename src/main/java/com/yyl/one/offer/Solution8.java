package com.yyl.one.offer;

/**
 * 题目描述
 * 一只青蛙一次可以跳上1级台阶，也可以跳上2级。求该青蛙跳上一个n级的台阶总共有多少种跳法（先后次序不同算不同的结果）。
 */
public class Solution8 {
    public static int jumpFloor(int target) {
        if(target==0){
            return -1;
        }else if(target==1){
            return 1;
        }else if(target==2){
            return 2;
        }else{
            return jumpFloor(target-1)+jumpFloor(target-2);
        }
    }

    public static void main(String[] args) {
        System.out.println(jumpFloor(8));
    }
}

package com.assignment.mahjong.models.backend.Player;

public class Point {
    private int totalPoints; // 总分
    private int basePoints;  // 基础分数
    private double multiplier; // 总倍率，用于计算总分

    // 构造函数，初始化分数和倍率
    public Point() {
        this.totalPoints = 0;  // 初始总分为0
        this.basePoints = 0;   // 初始基础分为0
        this.multiplier = 1.0; // 初始倍率设为1
    }

    // 设置基础分数，并更新总分
    public void setBasePoints(int points) {
        basePoints = points;  // 设置基础分数
        updateTotalPoints();  // 更新总分
    }

    // 增加倍率，并更新总分
    public void addMultiplier(double increment) {
        multiplier += increment; // 增加倍率
        updateTotalPoints();     // 更新总分
    }

    // 重置倍率为1，并更新总分
    public void resetMultiplier() {
        multiplier = 1.0;    // 重置倍率
        updateTotalPoints(); // 更新总分
    }

    // 更新总分的方法，根据基础分和倍率计算
    private void updateTotalPoints() {
        totalPoints = (int) (basePoints * multiplier); // 总分 = 基础分 * 倍率
    }

    // 获取当前的总分
    public int getTotalPoints() {
        return totalPoints;
    }

    // 获取分数详情的字符串描述
    public String getScoreDetails() {
        return "Base Points: " + basePoints +
                ", Multiplier: " + multiplier +
                ", Total Points: " + totalPoints;
    }
}

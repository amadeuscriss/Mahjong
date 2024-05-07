package models.backend.Player;

public class Point {
    private int totalPoints;

    public Point() {
        this.totalPoints = 0;
    }

    // 添加分数
    public void addPoints(int points) {
        totalPoints += points;
    }

    // 减去分数
    public void deductPoints(int points) {
        totalPoints -= points;
        if (totalPoints < 0) {
            totalPoints = 0;
        }
    }

    // 获取当前总分
    public int getTotalPoints() {
        return totalPoints;
    }
}

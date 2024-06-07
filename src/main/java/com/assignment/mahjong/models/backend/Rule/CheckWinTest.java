package com.assignment.mahjong.models.backend.Rule;

import com.assignment.mahjong.models.backend.Player.Point;
import com.assignment.mahjong.models.backend.Tile.TileInterface;

import java.util.ArrayList;
import java.util.List;

public class CheckWinTest {

    public static void main(String[] args) {
        Point points = new Point();
        CheckWin checkWin = new CheckWin(points);

        List<List<TileInterface>> testCases = new ArrayList<>();

        // 测试用例1: 标准胡牌 (11 22 33 444 555 666 777)
        List<TileInterface> testCase1 = new ArrayList<>();
        testCase1.add(new ConcreteTile("Character", "1"));
        testCase1.add(new ConcreteTile("Character", "1"));
        testCase1.add(new ConcreteTile("Character", "2"));
        testCase1.add(new ConcreteTile("Character", "2"));
        testCase1.add(new ConcreteTile("Character", "2"));
        testCase1.add(new ConcreteTile("Character", "3"));
        testCase1.add(new ConcreteTile("Dot", "4"));
        testCase1.add(new ConcreteTile("Dot", "5"));
        testCase1.add(new ConcreteTile("Dot", "6"));
        testCase1.add(new ConcreteTile("Dot", "7"));
        testCase1.add(new ConcreteTile("Dot", "8"));
        testCase1.add(new ConcreteTile("Dot", "7"));
        testCase1.add(new ConcreteTile("Dot", "7"));
        testCase1.add(new ConcreteTile("Dot", "7"));
        testCases.add(testCase1);

        // 测试用例2: 七对子 (1122 3344 5566 7788 99)
        List<TileInterface> testCase2 = new ArrayList<>();
        testCase2.add(new ConcreteTile("Character", "1"));
        testCase2.add(new ConcreteTile("Character", "1"));
        testCase2.add(new ConcreteTile("Character", "2"));
        testCase2.add(new ConcreteTile("Character", "2"));
        testCase2.add(new ConcreteTile("Dot", "3"));
        testCase2.add(new ConcreteTile("Dot", "3"));
        testCase2.add(new ConcreteTile("Dot", "4"));
        testCase2.add(new ConcreteTile("Dot", "4"));
        testCase2.add(new ConcreteTile("Bamboo", "6"));
        testCase2.add(new ConcreteTile("Bamboo", "6"));
        testCase2.add(new ConcreteTile("Bamboo", "7"));
        testCase2.add(new ConcreteTile("Bamboo", "7"));
        testCase2.add(new ConcreteTile("Dot", "8"));
        testCase2.add(new ConcreteTile("Dot", "8"));
        testCases.add(testCase2);

        // 测试用例3: 清一色 (111 222 333 444 555 66)
        List<TileInterface> testCase3 = new ArrayList<>();
        testCase3.add(new ConcreteTile("Character", "1"));
        testCase3.add(new ConcreteTile("Character", "1"));
        testCase3.add(new ConcreteTile("Character", "1"));
        testCase3.add(new ConcreteTile("Character", "2"));
        testCase3.add(new ConcreteTile("Character", "2"));
        testCase3.add(new ConcreteTile("Character", "2"));
        testCase3.add(new ConcreteTile("Character", "3"));
        testCase3.add(new ConcreteTile("Character", "3"));
        testCase3.add(new ConcreteTile("Character", "4"));
        testCase3.add(new ConcreteTile("Character", "4"));
        testCase3.add(new ConcreteTile("Character", "4"));
        testCase3.add(new ConcreteTile("Character", "5"));
        testCase3.add(new ConcreteTile("Character", "5"));
        testCase3.add(new ConcreteTile("Character", "5"));
        testCases.add(testCase3);

        // 测试用例4: 龙 (123 456 789 112 33)
        List<TileInterface> testCase4 = new ArrayList<>();
        testCase4.add(new ConcreteTile("Character", "1"));
        testCase4.add(new ConcreteTile("Character", "2"));
        testCase4.add(new ConcreteTile("Character", "3"));
        testCase4.add(new ConcreteTile("Character", "4"));
        testCase4.add(new ConcreteTile("Character", "5"));
        testCase4.add(new ConcreteTile("Character", "6"));
        testCase4.add(new ConcreteTile("Character", "7"));
        testCase4.add(new ConcreteTile("Character", "8"));
        testCase4.add(new ConcreteTile("Character", "9"));
        testCase4.add(new ConcreteTile("Character", "1"));
        testCase4.add(new ConcreteTile("Character", "1"));
        testCase4.add(new ConcreteTile("Character", "2"));
        testCase4.add(new ConcreteTile("Character", "2"));
        testCases.add(testCase4);

        // 测试用例5: 大四喜 (东东东 东南南 西西西 北北北)
        List<TileInterface> testCase5 = new ArrayList<>();
        testCase5.add(new ConcreteTile("Wind", "East"));
        testCase5.add(new ConcreteTile("Wind", "East"));
        testCase5.add(new ConcreteTile("Wind", "East"));
        testCase5.add(new ConcreteTile("Wind", "South"));
        testCase5.add(new ConcreteTile("Wind", "South"));
        testCase5.add(new ConcreteTile("Wind", "South"));
        testCase5.add(new ConcreteTile("Wind", "West"));
        testCase5.add(new ConcreteTile("Wind", "West"));
        testCase5.add(new ConcreteTile("Wind", "West"));
        testCase5.add(new ConcreteTile("Wind", "North"));
        testCase5.add(new ConcreteTile("Wind", "North"));
        testCase5.add(new ConcreteTile("Wind", "North"));
        testCase5.add(new ConcreteTile("Character", "1"));
        testCase5.add(new ConcreteTile("Character", "1"));
        testCases.add(testCase5);

        // 测试用例6: 大三元 (红红红 绿绿绿 白白白 11 22)
        List<TileInterface> testCase6 = new ArrayList<>();
        testCase6.add(new ConcreteTile("Dragon", "Red"));
        testCase6.add(new ConcreteTile("Dragon", "Red"));
        testCase6.add(new ConcreteTile("Dragon", "Red"));
        testCase6.add(new ConcreteTile("Dragon", "Green"));
        testCase6.add(new ConcreteTile("Dragon", "Green"));
        testCase6.add(new ConcreteTile("Dragon", "Green"));
        testCase6.add(new ConcreteTile("Dragon", "White"));
        testCase6.add(new ConcreteTile("Dragon", "White"));
        testCase6.add(new ConcreteTile("Dragon", "White"));
        testCase6.add(new ConcreteTile("Character", "1"));
        testCase6.add(new ConcreteTile("Character", "1"));
        testCase6.add(new ConcreteTile("Character", "2"));
        testCase6.add(new ConcreteTile("Character", "2"));
        testCases.add(testCase6);

        // 测试用例7: 九莲宝灯 (1112345678999)
        List<TileInterface> testCase7 = new ArrayList<>();
        testCase7.add(new ConcreteTile("Character", "1"));
        testCase7.add(new ConcreteTile("Character", "1"));
        testCase7.add(new ConcreteTile("Character", "1"));
        testCase7.add(new ConcreteTile("Character", "2"));
        testCase7.add(new ConcreteTile("Character", "3"));
        testCase7.add(new ConcreteTile("Character", "4"));
        testCase7.add(new ConcreteTile("Character", "5"));
        testCase7.add(new ConcreteTile("Character", "6"));
        testCase7.add(new ConcreteTile("Character", "7"));
        testCase7.add(new ConcreteTile("Character", "8"));
        testCase7.add(new ConcreteTile("Character", "9"));
        testCase7.add(new ConcreteTile("Character", "9"));
        testCase7.add(new ConcreteTile("Character", "9"));
        testCase7.add(new ConcreteTile("Character", "1"));
        testCases.add(testCase7);

        // 测试用例8: 十三幺 (19万 19条 19筒 东南西北中发白)
        List<TileInterface> testCase8 = new ArrayList<>();
        testCase8.add(new ConcreteTile("Character", "1"));
        testCase8.add(new ConcreteTile("Character", "9"));
        testCase8.add(new ConcreteTile("Bamboo", "1"));
        testCase8.add(new ConcreteTile("Bamboo", "9"));
        testCase8.add(new ConcreteTile("Dot", "1"));
        testCase8.add(new ConcreteTile("Dot", "9"));
        testCase8.add(new ConcreteTile("Wind", "East"));
        testCase8.add(new ConcreteTile("Wind", "South"));
        testCase8.add(new ConcreteTile("Wind", "West"));
        testCase8.add(new ConcreteTile("Wind", "North"));
        testCase8.add(new ConcreteTile("Dragon", "Red"));
        testCase8.add(new ConcreteTile("Dragon", "Green"));
        testCase8.add(new ConcreteTile("Dragon", "White"));
        testCase8.add(new ConcreteTile("Character", "1"));
        testCases.add(testCase8);

        // 测试用例9: 小四喜 (东东东 南南南 西西 西北北北)
        List<TileInterface> testCase9 = new ArrayList<>();
        testCase9.add(new ConcreteTile("Wind", "East"));
        testCase9.add(new ConcreteTile("Wind", "East"));
        testCase9.add(new ConcreteTile("Wind", "East"));
        testCase9.add(new ConcreteTile("Wind", "South"));
        testCase9.add(new ConcreteTile("Wind", "South"));
        testCase9.add(new ConcreteTile("Wind", "South"));
        testCase9.add(new ConcreteTile("Wind", "West"));
        testCase9.add(new ConcreteTile("Wind", "West"));
        testCase9.add(new ConcreteTile("Wind", "West"));
        testCase9.add(new ConcreteTile("Wind", "North"));
        testCase9.add(new ConcreteTile("Wind", "North"));
        testCase9.add(new ConcreteTile("Wind", "North"));
        testCase9.add(new ConcreteTile("Character", "1"));
        testCase9.add(new ConcreteTile("Character", "1"));
        testCases.add(testCase9);

        // 测试用例10: 全带幺 (123 123 123 123 111)
        List<TileInterface> testCase10 = new ArrayList<>();
        testCase10.add(new ConcreteTile("Character", "1"));
        testCase10.add(new ConcreteTile("Character", "2"));
        testCase10.add(new ConcreteTile("Character", "3"));
        testCase10.add(new ConcreteTile("Character", "1"));
        testCase10.add(new ConcreteTile("Character", "2"));
        testCase10.add(new ConcreteTile("Character", "3"));
        testCase10.add(new ConcreteTile("Dot", "1"));
        testCase10.add(new ConcreteTile("Dot", "2"));
        testCase10.add(new ConcreteTile("Dot", "3"));
        testCase10.add(new ConcreteTile("Dot", "1"));
        testCase10.add(new ConcreteTile("Dot", "2"));
        testCase10.add(new ConcreteTile("Dot", "3"));
        testCase10.add(new ConcreteTile("Character", "1"));
        testCase10.add(new ConcreteTile("Character", "1"));
        testCases.add(testCase10);

        // 运行测试用例
        for (int i = 0; i < testCases.size(); i++) {
            List<TileInterface> testCase = testCases.get(i);
            boolean result = checkWin.checkIfWin(testCase, false, false, false, false);
            System.out.println("Test case " + (i + 1) + ": " + (result ? "Win" : "Not Win"));
        }
    }
}
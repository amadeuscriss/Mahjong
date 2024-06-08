package com.assignment.mahjong.models.backend.Rule;

import com.assignment.mahjong.models.backend.Tile.TileInterface;
import com.assignment.mahjong.models.backend.Player.Point;

import java.util.*;
import java.util.stream.Collectors;

public class CheckWin {
    private Point points;  // Point object for recording player scores

    public CheckWin(Point points) {
        this.points = points;
    }

    // Method to determine if adding a specific tile results in a win
    public static boolean canWin(List<TileInterface> handTiles, TileInterface candidateTile) {
        List<TileInterface> testHand = new ArrayList<>(handTiles);
        testHand.add(candidateTile);
        return isStandardWin(testHand) || isSevenPairs(testHand) || isGreatMajority(testHand) || isAllOneSuit(testHand)
                || isFiveGateWin(testHand) || isDragon(testHand) || isSelfDraw(testHand);
    }

    public boolean checkIfWin(List<TileInterface> handTiles, boolean isSelfDrawn, boolean isWinByDiscard, boolean isKongFlowerWin, boolean isLastTileWin) {
        boolean won = false;
        points.setBasePoints(1);  // 设置基础分为1分

        if (isStandardWin(handTiles)) {
            secondcheck(handTiles);
            won = true;
        }

        if (isSelfDrawn) {
            points.addMultiplier(1.5);  // 自摸胡 1.5倍
        } else if (isWinByDiscard) {
            points.addMultiplier(2.0);  // 点炮胡 2倍
        }

        if (won) {
            System.out.println("Player wins with total points: " + points.getTotalPoints());
            System.out.println(points.getScoreDetails());
        }
        return won;
    }

    private static boolean isStandardWin(List<TileInterface> handTiles) {
        if (handTiles.size() % 3 != 2) return false;

        int[] cards = new int[34];
        for (TileInterface tile : handTiles) {
            int index = getTileIndex(tile);
            cards[index]++;
        }

        return get_hu_info(cards, -1, -1);  // 调用split类中的get_hu_info方法
    }

    private static int getTileIndex(TileInterface tile) {
        String value = tile.getValueAsString();
        int num = extractNumber(value);
        String type = tile.getType();

        if (type.equals("Bamboo")) {
            return num - 1;  // 0-8 对应 Bamboo 1-9
        } else if (type.equals("Dot")) {
            return 9 + num - 1;  // 9-17 对应 Dot 1-9
        } else if (type.equals("Character")) {
            return 18 + num - 1;  // 18-26 对应 Character 1-9
        } else {
            // 假设其余类型为字牌
            switch (value) {
                case "East":
                    return 27;
                case "South":
                    return 28;
                case "West":
                    return 29;
                case "North":
                    return 30;
                case "Red":
                    return 31;
                case "Green":
                    return 32;
                case "White":
                    return 33;
                default:
                    throw new IllegalArgumentException("Invalid tile type or value");
            }
        }
    }

    private static int extractNumber(String str) {
        String num = str.replaceAll("[^\\d]", "");
        if (num.isEmpty()) {
            return -1; // 标识为非数字牌
        }
        return Integer.parseInt(num);
    }

    private static boolean canFormMelds(List<TileInterface> tiles) {
        if (tiles.isEmpty()) return true;

        int[] counts = new int[34];
        for (TileInterface tile : tiles) {
            int index = getTileIndex(tile);
            counts[index]++;
        }

        // 尝试将所有的牌分组，判断是否可以胡牌
        return canFormMeldsHelper(counts, 0);
    }

    private static boolean canFormMeldsHelper(int[] counts, int index) {
        if (index >= counts.length) return true;

        // 如果当前牌的数量为0，检查下一张牌
        if (counts[index] == 0) return canFormMeldsHelper(counts, index + 1);

        // 尝试组成刻子
        if (counts[index] >= 3) {
            counts[index] -= 3;
            if (canFormMeldsHelper(counts, index)) return true;
            counts[index] += 3;
        }

        // 尝试组成顺子
        if (index < 27 && index % 9 < 7 && counts[index] > 0 && counts[index + 1] > 0 && counts[index + 2] > 0) {
            counts[index]--;
            counts[index + 1]--;
            counts[index + 2]--;
            if (canFormMeldsHelper(counts, index)) return true;
            counts[index]++;
            counts[index + 1]++;
            counts[index + 2]++;
        }

        return false;
    }


    private static int countMatches(List<TileInterface> tiles, String value) {
        return (int) tiles.stream().filter(t -> t.getValueAsString().equals(value)).count();
    }

    private static boolean isSevenPairs(List<TileInterface> handTiles) {
        if (handTiles.size() != 14) return false;
        Map<String, Integer> countMap = new HashMap<>();
        for (TileInterface tile : handTiles) {
            countMap.merge(tile.getValueAsString(), 1, Integer::sum);
        }
        return countMap.values().stream().allMatch(count -> count == 2);
    }

    private static boolean isGreatMajority(List<TileInterface> handTiles) {
        return handTiles.size() == 13 && countMatches(handTiles, "single") == 1;
    }

    private static boolean isFiveGateWin(List<TileInterface> handTiles) {
        return handTiles.size() == 14 && countMatches(handTiles, "5 Wan") == 1;
    }

    private static boolean isLastTileWin(List<TileInterface> handTiles) {
        // Assuming last tile logic is handled externally and provided as boolean parameter
        return false;
    }

    private static boolean isDragon(List<TileInterface> handTiles) {
        Set<String> sequence = new HashSet<>(Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9"));
        for (TileInterface tile : handTiles) {
            sequence.remove(tile.getValueAsString());
        }
        return sequence.isEmpty();
    }

    private static boolean isAllOneSuit(List<TileInterface> handTiles) {
        if (handTiles.isEmpty()) return false;
        String suit = handTiles.get(0).getType();
        return handTiles.stream().allMatch(tile -> tile.getType().equals(suit));
    }

    private static boolean isSelfDraw(List<TileInterface> handTiles) {
        // Logic for self-draw check
        return false;
    }

    public static boolean get_hu_info(int[] hand_cards, int curCard, int gui_index) {
        int[] cards = new int[34];
        System.arraycopy(hand_cards, 0, cards, 0, 34);

        if (curCard < 34&& curCard >= 0) {
            cards[curCard]++;
        }
        int gui_num = 0;
        if (gui_index < 34&& gui_index >= 0) {
            gui_num = cards[gui_index];
            cards[gui_index] = 0;
        }

        int[] eye_tbl = new int[34];
        int eye_num = 0;
        int empty = -1;
        for (int i = 0; i < 34; ++i) {
            // 优化手段，三不靠的牌，必做将
            int min = (i / 9) * 9;
            int max = min + 8;
            if (max == 35) max = 33;
            if (cards[i] == 1 &&
                    (i - 2 < min || cards[i - 2] == 0) &&
                    (i - 1 < min || cards[i - 1] == 0) &&
                    (i + 1 > max || cards[i + 1] == 0) &&
                    (i + 2 > max || cards[i + 2] == 0)) {
                if (gui_num < 0) {
                    return false;
                }
                eye_num = 1;
                eye_tbl[0] = i;
                empty = -1;
                break;
            }
            if (empty == -1 && cards[i] == 0) empty = i;
            if (cards[i] > 0 && cards[i] + gui_num >= 2) {
                eye_tbl[eye_num++] = i;
            }
        }
        if (empty > 0) {
            eye_tbl[eye_num++] = empty;
        }

        boolean hu = false;
        int[] cache = {0, 0, 0, 0};
        for (int i = 0; i < eye_num; i++) {
            int eye = eye_tbl[i];
            if (eye == empty) {
                hu = foreach_eye(cards, gui_num - 2, gui_num, 1000, cache);
            } else {
                int n = cards[eye];
                if (n == 1) {
                    cards[eye] = 0;
                    hu = foreach_eye(cards, gui_num - 1, gui_num, eye / 9, cache);
                } else {
                    cards[eye] -= 2;
                    hu = foreach_eye(cards, gui_num, gui_num, eye / 9, cache);
                }
                cards[eye] = n;
            }
            if (hu) {
                break;
            }
        }

        if (gui_num > 0) {
            cards[gui_index] = gui_num;
        }
        return hu;
    }

    public static boolean foreach_eye(int[] cards, int gui_num, int max_gui, int eye_color, int[] cache) {
        int left_gui = gui_num;
        for (int i = 0; i < 3; i++) {
            int cache_index = -1;
            if (eye_color != i) cache_index = i;
            int need_gui = check_normal(cards, i * 9, i * 9 + 8, max_gui, cache_index, cache);
            if (cache_index > 0) {
                cache[i] = need_gui + 1;
            }
            left_gui -= need_gui;
            if (left_gui < 0) {
                return false;
            }
        }

        int cache_index = -1;
        if (eye_color != 3) cache_index = 3;
        int need_gui = check_zi(cards, max_gui, cache_index, cache);
        if (cache_index > 0) {
            cache[3] = need_gui + 1;
        }
        return left_gui >= need_gui;
    }

    public static int check_normal(int[] cards, int from, int to, int max_gui, int cache_index, int[] cache) {
        if (cache_index >= 0) {
            int n = cache[cache_index];
            if (n > 0) return n - 1;
        }

        int n = 0;
        for (int i = from; i <= to; i++) {
            n = n * 10 + cards[i];
        }

        if (n == 0) return 0;

        boolean n3 = false;
        for (int i = 0; i <= max_gui; i++) {
            if ((n + i) % 3 == 0) {
                n3 = true;
                break;
            }
        }

        if (!n3) {
            return max_gui + 1;
        }

        return next_split(n, 0, max_gui);
    }

    public static int next_split(int n, int need_gui, int max_gui) {
        int c = 0;
        while (true) {
            if (n == 0) return need_gui;

            while (n > 0) {
                c = n % 10;
                n = n / 10;
                if (c != 0) break;
            }
            if (c == 1 || c == 4) {
                return one(n, need_gui, max_gui);
            } else if (c == 2) {
                return two(n, need_gui, max_gui);
            }
        }
    }

    public static int one(int n, int need_gui, int max_gui) {
        int c1 = n % 10;
        int c2 = (n % 100) / 10;

        if (c1 == 0) ++need_gui;
        else n -= 1;

        if (c2 == 0) ++need_gui;
        else n -= 10;

        if (n == 0) return need_gui;

        if (need_gui > max_gui) return need_gui;

        return next_split(n, need_gui, max_gui);
    }

    public static int two(int n, int need_gui, int max_gui) {
        int c1 = n % 10;
        int c2 = (n % 100) / 10;
        int c3 = (n % 1000) / 100;
        int c4 = (n % 10000) / 1000;

        boolean choose_ke = true;
        if (c1 != 0) {
            if (c1 == 1) {
                // 刻子
                if (c2 != 0 && c2 != 1) {
                    if (c2 == 2) {
                        if (c3 == 2) {
                            if (c4 == 2) choose_ke = false;
                        } else if (c3 == 3) {
                            if (c4 != 2) choose_ke = false;
                        } else {
                            choose_ke = false;
                        }
                    } else if (c2 == 3) {
                        if (c3 != 3) {
                            choose_ke = false;
                        }
                    } else if (c2 == 4) {
                        if (c3 == 2) {
                            if (c4 == 2 || c4 == 3 || c4 == 4) choose_ke = false;
                        }
                        if (c3 == 3) {
                            choose_ke = false;
                        }
                    }
                }
            } else if (c1 == 2) {
                choose_ke = false;
            } else if (c1 == 3) {
                if (c2 == 2) {
                    if (c3 == 1 || c3 == 4) {
                        choose_ke = false;
                    } else if (c3 == 2) {
                        if (c4 != 2) choose_ke = false;
                    }
                }
                if (c2 == 3) {
                    choose_ke = false;
                } else if (c2 == 4) {
                    if (c3 == 2) {
                        choose_ke = false;
                    }
                }
            } else if (c1 == 4) {
                if (c2 == 2 && c3 != 2) {
                    choose_ke = false;
                } else if (c2 == 3) {
                    if (c3 == 0 || c3 == 1 || c3 == 2) {
                        choose_ke = false;
                    }
                } else if (c2 == 4) {
                    if (c3 == 2) choose_ke = false;
                }
            }
        }  // c1 == 0 全拆刻子


        if (choose_ke) {
            need_gui += 1;
        } else {
            if (c1 < 2) {
                need_gui += (2 - c1);
                n -= c1;
            } else {
                n -= 2;
            }

            if (c2 < 2) {
                need_gui += (2 - c2);
                n -= c2;
            } else {
                n -= 20;
            }
        }

        if (n == 0) return need_gui;

        if (need_gui > max_gui) return need_gui;

        return next_split(n, need_gui, max_gui);
    }

    public static int check_zi(int[] cards, int max_gui, int cache_index, int[] cache) {
        if (cache_index >= 0) {
            int n = cache[cache_index];
            if (n > 0) return n - 1;
        }

        int need_gui = 0;
        for (int i = 27; i < 34; i++) {
            int c = cards[i];
            if (c == 0) continue;
            if (c == 1 || c == 4) {
                need_gui = need_gui + 2;
            } else if (c == 2) {
                need_gui = need_gui + 1;
            }
            if (need_gui > max_gui) return need_gui;
        }

        return need_gui;
    }

    public void secondcheck(List<TileInterface> handTiles){
        if (isGreatMajority(handTiles)) {
            points.addMultiplier(2.0);  // 大钓 2番
            System.out.println(2);
        }
        if (isFiveGateWin(handTiles)) {
            points.addMultiplier(2.0);  // 捉五魁 2番
            System.out.println(3);
        }
        if (isLastTileWin(handTiles)) {
            points.addMultiplier(2.0);  // 海底捞月 2番
            System.out.println(4);
        }
        if (isDragon(handTiles)) {
            points.addMultiplier(3.0);  // 一条龙 3番
            System.out.println(5);
        }
        if (isSevenPairs(handTiles)) {
            points.addMultiplier(4.0);  // 七对子 4番
            System.out.println(6);
        }
        if (isSelfDraw(handTiles)) {
            points.addMultiplier(5.0);  // 杠上开花 5番
            System.out.println(6);
        }
        if (isAllOneSuit(handTiles)) {
            points.addMultiplier(6.0);  // 清一色 6番
            System.out.println(7);
        }
    }
}

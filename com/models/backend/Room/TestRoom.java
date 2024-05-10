package models.backend.Room;

public class TestRoom {
    public static void main(String[] args) {
        Room room = new Room();
        System.out.println("Testing Room Class");

        // 测试添加玩家
        Player alice = new Player("Alice");
        room.addPlayer(alice);
        System.out.println("Expected: Alice added, Actual: " + (room.getPlayers().contains(alice) ? "Alice added" : "Alice not added"));

        Player bob = new Player("Bob");
        room.addPlayer(bob);

        Player charlie = new Player("Charlie");
        room.addPlayer(charlie);

        Player david = new Player("David");
        room.addPlayer(david);

        // 测试添加超过最大玩家数
        Player eve = new Player("Eve");
        room.addPlayer(eve);

        // 测试设置玩家准备状态
        alice.setReady(true);
        bob.setReady(true);
        charlie.setReady(true);
        david.setReady(true);
        System.out.println("Expected: Game started, Actual: Game started? " + (room.getPlayers().size() == 4 && room.getPlayers().stream().allMatch(Player::isReady)));

        // 测试删除玩家
        room.removePlayerByName("Bob");
        System.out.println("Expected: Bob removed, Actual: Bob removed? " + (!room.getPlayers().contains(bob)));

        // 测试重置房间
        room.resetRoom();
        System.out.println("Expected: Room reset, Actual: Room reset? " + (room.getPlayers().stream().noneMatch(Player::isReady) && !room.getPlayers().isEmpty()));

        // 测试再次设置玩家准备后是否能重新开始游戏
        alice.setReady(true);
        charlie.setReady(true);
        david.setReady(true);
        room.checkIfGameCanStart();
        System.out.println("Expected: Game should not start without enough players, Actual: Game started? " + room.getPlayers().size());
    }
}


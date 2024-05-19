package com.assignment.mahjong.models.backend.Room;
/**
public class TestRoom {
    public static void main(String[] args) {
        RoomManager roomManager = new RoomManager();
        String roomCode = roomManager.createRoom();  // 创建一个房间并获取房间号

        System.out.println("Testing Room Class");

        // 测试加入玩家
        Player alice = new Player("Alice");
        boolean aliceJoined = roomManager.joinRoom(roomCode, alice);
        System.out.println("Expected: Alice added, Actual: " + (aliceJoined ? "Alice added" : "Alice not added"));

        Player bob = new Player("Bob");
        boolean bobJoined = roomManager.joinRoom(roomCode, bob);
        System.out.println("Expected: Bob added, Actual: " + (bobJoined ? "Bob added" : "Bob not added"));

        Player charlie = new Player("Charlie");
        boolean charlieJoined = roomManager.joinRoom(roomCode, charlie);
        System.out.println("Expected: Charlie added, Actual: " + (charlieJoined ? "Charlie added" : "Charlie not added"));

        Player david = new Player("David");
        boolean davidJoined = roomManager.joinRoom(roomCode, david);
        System.out.println("Expected: David added, Actual: " + (davidJoined ? "David added" : "David not added"));

        // 测试添加超过最大玩家数
        Player eve = new Player("Eve");
        boolean eveJoined = roomManager.joinRoom(roomCode, eve);
        System.out.println("Expected: Room full, Actual: " + (!eveJoined ? "Room overflow" : "Room not overflow"));

        // 获取房间状态
        Room room = roomManager.getRoom(roomCode);

        // 设置玩家准备状态
        if (room != null) {
            alice.setReady(true);
            bob.setReady(true);
            charlie.setReady(true);
            david.setReady(true);
            System.out.println("Expected: Game started, Actual: Game started? " + (room.getPlayers().size() == 4 && room.getPlayers().stream().allMatch(Player::isReady)));
        } else {
            System.out.println("Room was not found.");
        }

        // 测试重置房间
        if (room != null) {
            room.resetRoom();
            System.out.println("Expected: Room reset, Actual: Room reset? " + (room.getPlayers().stream().noneMatch(Player::isReady) && !room.isGameStarted()));
        }
    }
}
**/
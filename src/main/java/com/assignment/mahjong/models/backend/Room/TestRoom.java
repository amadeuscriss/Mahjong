package com.assignment.mahjong.models.backend.Room;

/**
 * Test class for the RoomManager and Player classes.
 */
public class TestRoom {
    public static void main(String[] args) {
        RoomManager roomManager = new RoomManager();
        String roomCode = roomManager.createRoom("lilisi");

        System.out.println("Testing Room Class");

        // Test join players
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

        // Test add more than the maximum number of players
        Player eve = new Player("Eve");
        boolean eveJoined = roomManager.joinRoom(roomCode, eve);
        System.out.println("Expected: Room full, Actual: " + (!eveJoined ? "Room overflow" : "Room not overflow"));

        Room room = roomManager.getRoom(roomCode);

        // Set player readiness
        if (room != null) {
            alice.setReady(true);
            bob.setReady(true);
            charlie.setReady(true);
            david.setReady(true);
            System.out.println("Expected: Game started, Actual: Game started? " + (room.getPlayers().size() == 4 && room.getPlayers().stream().allMatch(Player::isReady)));
        } else {
            System.out.println("Room was not found.");
        }

        // Test reset room
        if (room != null) {
            room.resetRoom();
            System.out.println("Expected: Room reset, Actual: Room reset? " + (room.getPlayers().stream().noneMatch(Player::isReady) && !room.isGameStarted()));
        }
    }
}

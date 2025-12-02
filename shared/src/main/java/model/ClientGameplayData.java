package model;

public record ClientGameplayData(String username, Role role, int gameID) {

    public enum Role {
        WHITE,
        BLACK,
        OBSERVER
    }
}

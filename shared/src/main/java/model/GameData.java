package model;

import chess.ChessGame;

public record GameData(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {
    @Override
    public String toString() {
        return "Game Information:" + '\n' +
                "gameID = " + gameID + '\n' +
                "whiteUsername = " + whiteUsername + '\n' +
                "blackUsername = " + blackUsername + '\n' +
                "gameName = " + gameName + '\n' +
                "game = " + game + '\n'
                ;
    }
}

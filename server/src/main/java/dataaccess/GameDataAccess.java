package dataaccess;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import model.CreateGameRequest;
import model.CreateGameResult;
import model.GameData;
import model.JoinGameRequest;

import java.util.ArrayList;
import java.util.List;

public interface GameDataAccess {
    void clear();

    ArrayList<GameData> listGames();

    CreateGameResult createGame(String gameName);

    void joinGame(JoinGameRequest req, String username);

    boolean verifyGameID(int gameID);

    ChessGame makeMove(int gameID, ChessMove move) throws InvalidMoveException;

    void finishGame(int gameID);

    boolean checkFinishedGame(int gameID);
}

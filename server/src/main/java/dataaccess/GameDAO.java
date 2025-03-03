package dataaccess;

import java.util.ArrayList;

import chess.ChessGame;
import model.GameData;

public interface GameDAO {
    ArrayList<GameData> listGames();
    GameData createGame(String gameName);
    GameData getGame(Integer gameID);
    void updateGame(GameData gameData, Integer gameID);
    void clear();
}

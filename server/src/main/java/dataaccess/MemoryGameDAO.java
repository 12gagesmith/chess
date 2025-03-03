package dataaccess;

import model.GameData;
import java.util.ArrayList;

public class MemoryGameDAO implements GameDAO{
    @Override
    public ArrayList<GameData> listGames() {
        return null;
    }

    @Override
    public GameData createGame(String gameName) {
        return null;
    }

    @Override
    public GameData getGame(Integer gameID) {
        return null;
    }

    @Override
    public void updateGame(GameData gameData, Integer gameID) {
    }

    @Override
    public void clear() {
    }
}

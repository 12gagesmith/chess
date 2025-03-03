package dataaccess;

import java.util.ArrayList;
import java.util.Map;

public class MemoryGameDAO implements GameDAO{
    @Override
    public ArrayList<Map> listGames() {
        return null;
    }

    @Override
    public Map createGame(String gameName) {
        return Map.of();
    }

    @Override
    public Map getGame(String gameID) {
        return Map.of();
    }

    @Override
    public void updateGame(Map game, String gameID) {
    }

    @Override
    public void clear() {
    }
}

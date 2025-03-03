package dataaccess;

import service.Game;
import java.util.ArrayList;

public class MemoryGameDAO implements GameDAO{
    @Override
    public ArrayList<Game> listGames() {
        return null;
    }

    @Override
    public Game createGame(String gameName) {
        return null;
    }

    @Override
    public Game getGame(String gameID) {
        return null;
    }

    @Override
    public void updateGame(Game game, String gameID) {

    }

    @Override
    public void clear() {
    }
}

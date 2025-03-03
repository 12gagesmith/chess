package service;

public class Game {

    private final String gameID;
    private String whiteUsername;
    private String blackUsername;
    private String gameName;

    public Game(String gameID, String whiteUsername, String blackUsername, String gameName) {
        this.gameID = gameID;
        this.whiteUsername = whiteUsername;
        this.blackUsername = blackUsername;
        this.gameName = gameName;
    }

    public String getGameID() {
        return this.gameID;
    }

    public String getWhiteUsername() {
        return this.whiteUsername;
    }

    public String getBlackUsername() {
        return this.blackUsername;
    }

    public String getGameName() {
        return this.gameName;
    }

    public void setWhiteUsername(String whiteUsername) {
        this.whiteUsername = whiteUsername;
    }

    public void setBlackUsername(String blackUsername) {
        this.blackUsername = blackUsername;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

}

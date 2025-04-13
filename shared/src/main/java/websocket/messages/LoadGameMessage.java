package websocket.messages;

import model.GameData;

public class LoadGameMessage extends ServerMessage {
    public final GameData gameData;
    public final String playerColor;
    public LoadGameMessage(GameData gameData, String playerColor) {
        super(ServerMessageType.LOAD_GAME);
        this.gameData = gameData;
        this.playerColor = playerColor;
    }
}

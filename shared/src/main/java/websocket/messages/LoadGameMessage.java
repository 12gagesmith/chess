package websocket.messages;

import model.GameData;

public class LoadGameMessage extends ServerMessage {
    public final GameData gameData;
    public LoadGameMessage(GameData gameData) {
        super(ServerMessageType.LOAD_GAME);
        this.gameData = gameData;
    }
}

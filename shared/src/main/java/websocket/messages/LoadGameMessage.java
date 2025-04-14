package websocket.messages;

import model.GameData;

public class LoadGameMessage extends ServerMessage {
    public final GameData game;
    public final String playerColor;
    public LoadGameMessage(GameData game, String playerColor) {
        super(ServerMessageType.LOAD_GAME);
        this.game = game;
        this.playerColor = playerColor;
    }
}

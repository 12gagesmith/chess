package websocket.messages;

import chess.ChessPosition;
import model.GameData;

import java.util.Collection;

public class LoadGameMessage extends ServerMessage {
    public final GameData game;
    public final Collection<ChessPosition> positions;
    public LoadGameMessage(GameData game, Collection<ChessPosition> positions) {
        super(ServerMessageType.LOAD_GAME);
        this.game = game;
        this.positions = positions;
    }
}

package ui;

import ui.websocket.NotificationHandler;
import websocket.messages.*;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Repl implements NotificationHandler {
    private final Client client;

    public Repl(String serverUrl) {
        client = new Client(serverUrl, this);
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        String result = "";
        System.out.print(client.help());
        while (!result.equals(SET_TEXT_COLOR_BLUE + "Thanks for playing!" + RESET_TEXT_COLOR)) {
            printPrompt();
            String line = scanner.nextLine();
            try {
                result = client.eval(line);
                System.out.print(result);
            } catch (Throwable e) {
                String message = e.toString();
                System.out.print(message);
            }
        }
    }

    private void printPrompt() {
        System.out.print("\n" + ">>> ");
    }

    @Override
    public void notify(ServerMessage message) {
        if (message instanceof NotificationMessage) {
            System.out.println(SET_TEXT_COLOR_YELLOW + ((NotificationMessage) message).message + RESET_TEXT_COLOR);
        } else if (message instanceof LoadGameMessage) {
            client.printBoard(((LoadGameMessage) message).game.game(), ((LoadGameMessage) message).playerColor);
        } else if (message instanceof ErrorMessage) {
            System.out.println(SET_TEXT_COLOR_RED + ((ErrorMessage) message).errorMessage + RESET_TEXT_COLOR);
        }
        printPrompt();
    }
}

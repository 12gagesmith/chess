package ui;

import java.util.Scanner;

import static ui.EscapeSequences.RESET_TEXT_COLOR;
import static ui.EscapeSequences.SET_TEXT_COLOR_BLUE;

public class Repl {
    private final Client client;

    public Repl(String serverUrl) {
        client = new Client(serverUrl);
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
}

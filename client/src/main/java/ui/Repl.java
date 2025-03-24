package ui;

import java.util.Scanner;

public class Repl {
    private final Client client;

    public Repl(String serverUrl) {
        client = new Client(serverUrl);
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        String result = "";
        while (!result.equals("quit")) {
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

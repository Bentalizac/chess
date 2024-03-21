package UserInterface;
import server.ServerFacade;
import ui.EscapeSequences;

import java.util.Scanner;
public class repl {
    public static void main(String[] args) {
        EscapeSequences ui = new EscapeSequences();

        ServerFacade serverFacade = new ServerFacade(8080);
        Scanner scanner = new Scanner(System.in);
        System.out.print(EscapeSequences.WHITE_KING + " Welcome to this 240 Chess nonsense " + EscapeSequences.BLACK_KING + "\n");
        while (true) {
            // Prompt user for input
            System.out.print("Type Help to get started: ");
            String userInput = scanner.nextLine();

            System.out.println("Input was: " + userInput);

            if (userInput.equals("quit")) {
                break;
            }

        }

        scanner.close();
    }

    private String parseInput(String userInput) { // Break user input by spaces to be able to handle usernames and registration and such
        return null;
    }

}

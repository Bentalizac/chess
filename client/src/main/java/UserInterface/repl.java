package UserInterface;
import model.AuthData;
import server.ServerFacade;
import ui.EscapeSequences;

import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;


public class repl {

    AuthData authData = null;
    EscapeSequences ui = new EscapeSequences();
    ServerFacade serverFacade = new ServerFacade(8080);

    public void runREPL(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print(EscapeSequences.WHITE_KING + " Welcome to this 240 Chess nonsense " + EscapeSequences.BLACK_KING + "\n");
        while (true) {
            // Prompt user for input
            System.out.print("Type Help to get started: ");
            String userInput = scanner.nextLine();
            String response = parseInput(userInput);
            if (Objects.equals(response, "quit")) {
                break;
            }
            System.out.print(response);
        }
        scanner.close();
    }
    private String parseInput(String userInput) { // Break user input by spaces to be able to handle usernames and registration and such
        String command = userInput.split(" ")[0].toLowerCase();
        String[] body = userInput.split(" ");
        if (command.equals("quit")) {
            return "quit";
        }
        if (Objects.equals(command, "help")) {
            return helpCommand();
        }

        else if (command.equals("register")) {
            System.out.print("Registering user with username " + body[1] + "\n");
        }
        else if (command.equals("login")) {
            System.out.print("Logging in user: " + body[1]+ "\n");
        }
        else if (command.equals("list")) {
            System.out.print("Fetching all games"+ "\n");
        }
        else if (command.equals("join")) {
            System.out.print("Joining game " + body[1] + " with the following information:" + Arrays.toString(body)+ "\n");
        }
        else if (command.equals("observe")) {
            System.out.print("Spectating game " + body[1]+ "\n");
        }
        else if (command.equals("logout")) {
            System.out.print("Logging out"+ "\n");
            this.authData = null;
        }
        else{
            System.out.print("Command not recognized, type HELP to view valid commands \n");
        }

        return null;
    }

    private String helpCommand() { // Returns the help string
        if (this.authData == null) {
            return """
                    AVAILABLE COMMANDS:\s
                       Register <username> <password> <email> | Register a new user, you'll still need to login after registeringLogin <username> <password> | Login as an existing user\s
                       Quit | Exit this program. Why would you want to do that?\s
                       Help | Return to this ✨beautifully✨ written help menu\s
                    """;
        }
        else{
            return """
                    AVAILABLE COMMANDS:\s
                        Create <name> | Creates a new game with the chosen name. Very fun, all our reviewers say so\s
                        List | Displays a list of all the games, make sure to remember the ID of the game you're looking for\s
                        Join <ID> OR <gameName> <[WHITE/BLACK]> <username> | Join a game by it's name or ID as a player by including a username and color\s
                        Observe <ID> OR <gameName> | Stalk your friends that actually wanted to play a game\s
                        Logout | Logs out the current user, which is probably you\s
                        Quit | Exit this program. Why would you want to do that?\s
                        Help | Return to this ✨beautifully✨ written help menu\s
                    """;
        }
    }

}

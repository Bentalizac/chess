package UserInterface;
import model.AuthData;
import model.UserData;
import server.ServerFacade;
import ui.EscapeSequences;

import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;


public class repl {

    AuthData authData = null;
    EscapeSequences ui = new EscapeSequences();
    ServerFacade serverFacade = new ServerFacade("HTTP://localhost:8080");

    public void runREPL(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print(EscapeSequences.WHITE_KING + " Welcome to this 240 Chess Nonsense " + EscapeSequences.BLACK_KING + "\n");
        while (true) {
            // Prompt user for input
            System.out.print("\nType Help to get started: ");
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
        switch (command) {
            case "help" -> {
                return helpCommand();
            }
            case "register" -> {
                return this.register(body);
            }
            case "login" -> {

                return this.login(body);
            }
            case "list" -> {
                return ("Fetching all games" + "\n");
            }
            case "join" -> {
                return ("Joining game " + body[1] + " with the following information:" + Arrays.toString(body) + "\n");
            }
            case "observe" -> {
                return ("Spectating game " + body[1] + "\n");
            }
            case "logout" -> {
                this.authData = null;
                return ("Logging out" + "\n");
            }
            default -> {
                return ("Command not recognized, type HELP to view valid commands \n");
            }
        }
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

    private String register(String[] body) {
        if (body.length < 4) {
            return "You're missing one or more fields, you need a username, password, and email";
        }
        var response = serverFacade.register(new UserData(body[1], body[2], body[3]));

        if (response.getClass() == UserData.class) {
            return "Successfully registered user :" + ((UserData) response).username();
        }
        return response.toString();
    }

    private String login(String[] body) {
        if (body.length < 3) {
            return "You're missing one or more fields, you need a username and password";
        }

        var response = serverFacade.login(new UserData(body[1], body[2], null));

        if (response.getClass() == AuthData.class) {
            this.authData = new AuthData(((AuthData) response).authToken(), ((AuthData) response).username());
            return "Successfully logged in user :" + ((AuthData) response).username();
        }
        return response.toString();
    }

    private String logout() {
        var response = serverFacade.logout(authData);
        return response.toString();
    }

}

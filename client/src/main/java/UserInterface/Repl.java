package UserInterface;
import Facades.ServerFacade;
import Facades.WebSocketFacade;
import chess.BoardPainter;
import chess.ChessBoard;
import chess.ChessGame;

import exception.ResponseException;
import model.AuthData;
import model.GameData;
import model.JoinGameRequest;
import model.UserData;
import Facades.NotificationHandler;
import ui.EscapeSequences;

import java.util.*;

import static chess.EscapeSequences.*;


public class Repl {

    AuthData authData = null;
    final static String DECOROW = (SET_BG_COLOR_WHITE + EMPTY) + (SET_BG_COLOR_BLACK + EMPTY) +(SET_BG_COLOR_WHITE + EMPTY) + (SET_BG_COLOR_BLACK + EMPTY) + (SET_BG_COLOR_WHITE + EMPTY) + (SET_BG_COLOR_BLACK + EMPTY) + (SET_BG_COLOR_WHITE + EMPTY) + (SET_BG_COLOR_BLACK + EMPTY) + SET_BG_COLOR_DARK_GREY;
    final static String DECOROW2 = (SET_BG_COLOR_BLACK + EMPTY) +(SET_BG_COLOR_WHITE + EMPTY) + (SET_BG_COLOR_BLACK + EMPTY) + (SET_BG_COLOR_WHITE + EMPTY) + (SET_BG_COLOR_BLACK + EMPTY) + (SET_BG_COLOR_WHITE + EMPTY) + (SET_BG_COLOR_BLACK + EMPTY) + (SET_BG_COLOR_WHITE + EMPTY) + SET_BG_COLOR_DARK_GREY;

    ServerFacade serverFacade = new ServerFacade(8080);
    WebSocketFacade webSocketFacade;

    public void runREPL(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("   " + EscapeSequences.WHITE_KING + " Welcome to this 240 Chess Nonsense " + EscapeSequences.BLACK_KING + "\n");
        while (true) {
            // Prompt user for input
            System.out.print(DECOROW+DECOROW  +"\n" + DECOROW2+DECOROW2  + "\nType Help to get started \n "+ SET_TEXT_BLINKING+ ">>>" + RESET_TEXT_BLINKING);
            String userInput = scanner.nextLine();
            String response = parseInput(userInput);
            if (Objects.equals(response, "quit")) {
                break;
            }
            System.out.print(SET_BG_COLOR_DARK_GREY+ response + "\n");

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
                return this.listGames();
            }
            case "join", "observe" -> {
                return joinGame(body);
            }
            case "logout" -> {
                //this.authData = null;
                return logout();
            }
            case "create" -> {
                return createGame(body);
            }
            case "detailedhelp" ->
            {
                return detailedHelp();
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
                       DetailedHelp | A slightly more formal, far more boring help menu.\s
                    """;
        }
        else{
            return """
                    AVAILABLE COMMANDS:\s
                        Create <name> | Creates a new game with the chosen name. Very fun, all our reviewers say so\s
                        List | Displays a list of all the games, make sure to remember the ID of the game you're looking for\s
                        Join <ID> OR <gameName> <[WHITE/BLACK]> | Join a game by its name or ID. Please specify which color, grey isn't an option.\s
                        Observe <ID> OR <gameName> | Stalk your friends that actually wanted to play a game\s
                        Logout | Logs out the current user, which is probably you\s
                        Quit | Exit this program. Why would you want to do that? I mean, I guess it's easier to leave this than VIM\s
                        Help | Return to this ✨beautifully✨ written help menu\s
                        DetailedHelp | A slightly more formal, far more boring help menu.\s
                    """;
        }
    }

    private String detailedHelp() { // Returns the help string
        if (this.authData == null) {
            return """
                    All commands are non-case-sensitive.
                    Each must be entered as a single line similar to standard terminal prompting
                    The order of items does matter. A lot.
                    
                    AVAILABLE COMMANDS:\s
                       Register <username> <password> <email> | Register a new user, you'll still need to login after registering\s
                       Login <username> <password> | Login as an existing user\s
                       Quit | Exit\s
                       Help | Return to the fun help menu\s
                       DetailedHelp | Return to this help menu.\s
                    """;
        }
        else{
            return """
                    All commands are non-case-sensitive.
                    Each must be entered as a single line similar to standard terminal prompting
                    The order of items does matter. A lot.
                    
                    AVAILABLE COMMANDS:\s
                        Create <name> | Creates a new game with the chosen name.\s
                        List | Displays a list of all the games, as well as current players if applicable\s
                        Join <ID> <"WHITE" OR "BLACK"> | Join a game by its ID. The second option must be either black or white\s
                        Observe <ID> | Spectate a game\s
                        Logout | Logs out the current user\s
                        Quit | Exit\s
                        Help | Return to the fun help menu\s
                        DetailedHelp | Return to this help menu.\s
                    """;
        }
    }



    private String register(String[] body) {
        if (body.length < 4) {
            return "You're missing one or more fields, you need a username, password, and email";
        }
        var response = serverFacade.register(new UserData(body[1], body[2], body[3]));

        if (response.getClass() == AuthData.class) {
            this.authData = ((AuthData) response);
            return "Successfully registered user :" + ((AuthData) response).username() + "welcome to the most mediocre chess program you'll likely use.";
        }

        else{
            return "That username's already being used. Come up with something more original";
        }
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
        if (loginGate()) {
            return "ACCESS DENIED.";
        }
        serverFacade.logout(authData);
        this.authData = null;
        return "Logged out? Why? Why would you leave us?";
    }

    private String listGames() {
        if (loginGate()) {
            return "ACCESS DENIED.";
        }
        String output = "Here's all the games we've got, any catch your eye?\n";
        GameData[] response = serverFacade.listGames(authData);
        if(response.length == 0) {
            output += "... Well that's a bit awkward. There's no games going. Maybe start your own? \n";
            return output;
        }
        for (GameData data : response) {
            output += "[Game number: " + data.gameID() + " Name: " + data.gameName() + "] ";
            if (data.whiteUsername() != null) {
                output += ", has " + data.whiteUsername() + " playing white.";
            }
            if (data.blackUsername() != null) {
                output +=  " " + data.blackUsername() + " is playing black.";
            }
            if ((Objects.equals(data.whiteUsername(), data.blackUsername())) && (data.blackUsername() != null)) {
                output += ".. Wait, is that fella playing himself?";
            }
            output += "\n";
        }
        output += "\nIf you want to join a game, make sure to keep track of the game number. \n";
        return output;
    }

    private String joinGame(String[] body) {
        if (loginGate()) {
            return "ACCESS DENIED.";
        }
        String response = "";
        String output = "That game doesn't exist, gimme a different number. \n";
        if (body.length < 2) {
            return "You're missing one or more pieces of info, I need a game number at least, and a color would be nice";
        }

        try{ // Checks to see if the correct part is a number
            int x = Integer.parseInt(body[1]);
        }
        catch(NumberFormatException ex){
            return "Gimme a number, I can't get you in a game by name.";
        }
        if (body.length == 2) {
            response = serverFacade.joingame(new JoinGameRequest(null, Integer.parseInt(body[1])), authData);
            if(gameExists(response)) {

                try {
                    webSocketFacade = new WebSocketFacade(8080, new NotificationHandler());
                    webSocketFacade.spectate(authData, Integer.parseInt(body[1]));
                    PlayMenu game = new PlayMenu(Integer.parseInt(body[1]), webSocketFacade, authData, webSocketFacade.game);
                    game.run();
                }
                catch(ResponseException ex) {
                    return "Musta been a connection issue, this things says \n" + ex.getMessage() + "\n";
                }
                output = "How was the show? Hope you're into chess if you're here to watch people play of all things...";
            }
        }
        else {
            response = serverFacade.joingame(new JoinGameRequest(body[2].toUpperCase(), Integer.parseInt(body[1])), authData);
            if(gameExists(response)) {
                ChessGame.TeamColor color = null;
                if (body[2].equalsIgnoreCase("WHITE")) {
                    color = ChessGame.TeamColor.WHITE;
                }
                else{
                    color = ChessGame.TeamColor.BLACK;
                }
                try {
                    webSocketFacade = new WebSocketFacade(8080, new NotificationHandler());
                    webSocketFacade.join(authData, color, Integer.parseInt(body[1]));
                    PlayMenu game = new PlayMenu(Integer.parseInt(body[1]), webSocketFacade, authData, webSocketFacade.game);
                    game.run();
                }
                catch(ResponseException ex) {
                    return "Musta been a connection issue, this things says \n" + ex.getMessage() + "\n";
                }

                output = "Welcome to the game! May luck be on your side!";

            }
        }
        return output;
    }

    private boolean gameExists(String response) {
        String[] list = response.split(" ");
        for (String word : list) {
            if(Objects.equals(word, "400")) {
                return false;
            }
        }
        //drawBoards();
        return true;
    }
    private String createGame(String[] body) {
        if (loginGate()) {
            return "ACCESS DENIED.";
        }

        if (body.length < 2) {
            return "All you need to make a game is a name, you can give me that much at least. \nBe better.";
        }

        GameData data = new GameData(0, null, null, body[1], null, null);
        var response = serverFacade.createGame(data, authData);
        return "";
    }
    private boolean loginGate() {
        return this.authData == null;
    }

}

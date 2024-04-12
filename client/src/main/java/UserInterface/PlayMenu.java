package UserInterface;

import Facades.WebSocketFacade;
import chess.BoardPainter;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import exception.ResponseException;
import model.AuthData;
import Facades.NotificationHandler;
import ui.EscapeSequences;

import java.util.Objects;
import java.util.Scanner;

import static chess.EscapeSequences.*;
import static java.lang.Integer.parseInt;

public class PlayMenu {

    final static String DECOROW = (SET_BG_COLOR_WHITE + EMPTY) + (SET_BG_COLOR_BLACK + EMPTY) +(SET_BG_COLOR_WHITE + EMPTY) + (SET_BG_COLOR_BLACK + EMPTY) + (SET_BG_COLOR_WHITE + EMPTY) + (SET_BG_COLOR_BLACK + EMPTY) + (SET_BG_COLOR_WHITE + EMPTY) + (SET_BG_COLOR_BLACK + EMPTY) + SET_BG_COLOR_DARK_GREY;
    final static String DECOROW2 = (SET_BG_COLOR_BLACK + EMPTY) +(SET_BG_COLOR_WHITE + EMPTY) + (SET_BG_COLOR_BLACK + EMPTY) + (SET_BG_COLOR_WHITE + EMPTY) + (SET_BG_COLOR_BLACK + EMPTY) + (SET_BG_COLOR_WHITE + EMPTY) + (SET_BG_COLOR_BLACK + EMPTY) + (SET_BG_COLOR_WHITE + EMPTY) + SET_BG_COLOR_DARK_GREY;
    int gameID;

    ChessGame game;
    WebSocketFacade webSocketFacade;
    AuthData authData;

    //Facades.WebSocketFacade socketFacade;

    public PlayMenu(int gameID, WebSocketFacade webSocketFacade, AuthData auth, ChessGame game) {this.gameID = gameID;
    this.game = game;
    this.webSocketFacade = webSocketFacade;
    authData = auth;
    }

    public void run() {
        Scanner playScanner = new Scanner(System.in);
        System.out.print("   " + EscapeSequences.WHITE_KING + " Chess do be Chessin' " + EscapeSequences.BLACK_KING + "\n");
        while (true) {
            // Prompt user for input
            System.out.print(DECOROW+DECOROW  +"\n" + DECOROW2+DECOROW2  + "\nType Help to get started \n "+ SET_TEXT_BLINKING+ ">>>" + RESET_TEXT_BLINKING);
            String userInput = playScanner.nextLine();
            String response = parseInput(userInput);
            if (Objects.equals(response, "quit")) {
                try {
                    webSocketFacade.leave(authData, gameID);
                }
                catch (ResponseException ex) {
                    System.out.print("Musta been a connection issue, this things says \n" + ex.getMessage() + "\nTry that again in a bit. Or ALT+F4");
                }
                break;
            }
            System.out.print(SET_BG_COLOR_DARK_GREY+ response + "\n");
        }
        //playScanner.close();
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
            case "move" -> {
                return makeMove(body);
            }
            case "redraw" -> {
                redraw();
                return "";
            }
            case "highlight" -> highlight(body);
        }
        return "Just ask for help if you don't know what else to say";
    }

    private String helpCommand() {
        return """
                    AVAILABLE COMMANDS:\s
                       Redraw | Redraw the pretty board and clear the screen \s
                       Leave | Get up from the table, but saves your spot in the game\s
                       Move <starting coordinate> <ending coordinate> | move the piece from the start to the end, if it can. Coordinates are a1, b6, h4, etc.\s
                       Resign | Give up and go home. You really sure you want to do that?.\s
                       Highlight | If you need someone to tell you what moves you can make, just use this instead.\s
                       Help | Have me tell you all of this again.\s
                    """;
    }
    private ChessPosition stringToCoord(String input) throws InvalidCoordinatesError{
        String[] pieces = input.split("");
        int row;
        int col;
        try {
            col = parseInt(pieces[1]);
        }
        catch(NumberFormatException ex) {
            throw new InvalidCoordinatesError("Coordinates need to be formatted as a letter followed by a number, like a1, or C8");
        }
        switch (pieces[0].toLowerCase()) {
            case "a" -> row = 1;
            case "b" -> row = 2;
            case "c" -> row = 3;
            case "d" -> row = 4;
            case "e" -> row = 5;
            case "f" -> row = 6;
            case "g" -> row = 7;
            case "h" -> row = 8;
            default -> throw new InvalidCoordinatesError(pieces[0] + " is an invalid column label");
        }
        if(col >= 9 || col <1) {
            throw new InvalidCoordinatesError(pieces[1] + " is an invalid row position");
        }
        return new ChessPosition(row, col);
    }
    private String makeMove(String[] body) {
        if (body.length < 3) {
            return "You're missing one or more fields, you need a starting coordinate, and an ending coordinate";
        }
        String start = body[1];
        String end = body[2];
        ChessPosition startTile;
        ChessPosition endTile;
        if (start.length() != 2 || end.length() != 2){
            return "Those coordinates are invalid, try again please.";
        }
        try{
            startTile = stringToCoord(start);
            endTile = stringToCoord(end);
        }
        catch(InvalidCoordinatesError ex) {
            return ex.getMessage();
        }
        ChessMove move = new ChessMove(startTile, endTile);

        try {
            webSocketFacade.move(authData, move, gameID);
        }
        catch(ResponseException ex) {
            return "Musta been a connection issue, this things says \n" + ex.getMessage() + "\n";
        }

        return "";
    }

    public void redraw(){
        ChessGame game = webSocketFacade.game;
        BoardPainter painter = new BoardPainter(game.getBoard());
        if (game.getTeamTurn() == ChessGame.TeamColor.BLACK) {
            painter.drawBlackDown();
        }
        else {
            painter.drawWhiteDown();
        }
    }

    public void highlight(String [] body) {
        if (body.length < 2) {
            return;
        }
        String start = body[1];
        ChessPosition startTile;
        if (start.length() != 2){
            return;
        }
        try{
            startTile = stringToCoord(start);
        }
        catch(InvalidCoordinatesError ex) {
           System.out.print(ex.getMessage());
            return;
        }
        BoardPainter painter = new BoardPainter(webSocketFacade.game.getBoard());
        painter.highlight(startTile, game);
    }

}

package server;

import com.google.gson.Gson;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import model.JoinGameRequest;
import model.UserData;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

public class ServerFacade {
    //private final String serverUrl;
    private final String serverUrl;
    private int statusCode;



    public ServerFacade(int port) {
        serverUrl = "HTTP://localhost:" + port;
    }

    public void clear(){
        var path = "/db";
        try{
            this.makeRequest("DELETE", path, null, UserData.class, null, null);
        }
        catch(ResponseException ex){
            ex.getMessage();
        }
    }

    public Object register(UserData data) {
        var path = "/user";
        try{
            return this.makeRequest("POST", path, data, UserData.class, null, null);
        }
        catch(ResponseException ex){
            return ex.getMessage();
        }
    }

    public Object login(UserData data) {
        var path = "/session";
        try{
            return this.makeRequest("POST", path, data, AuthData.class, null, null);
        }
        catch(ResponseException ex){
            return ex.getMessage();
        }
    }

    public Object logout(AuthData data) {
        var path = "/session";
        try{
            return this.makeRequest("DELETE", path, data, AuthData.class, "authorization", data.authToken());
        }
        catch(ResponseException ex){
            return ex.getMessage();
        }
    }

    public GameData[] listGames(AuthData data) {
        var path = "/game";
        try{
            record listGames(GameData[] games) {};
            listGames response = this.makeRequest("GET", path, null, listGames.class, "authorization", data.authToken());
            return response.games;
        }
        catch(ResponseException ex){
            return null;
        }
    }

    public String joingame(JoinGameRequest data, AuthData auth) {
        var path = "/game";
        try{
            JoinGameRequest response = this.makeRequest("PUT", path, data, JoinGameRequest.class, "authorization", auth.authToken());
            return response.toString();
        }
        catch(ResponseException ex){
            return ex.toString();
        }
    }

    public int createGame(GameData data, AuthData auth) {
        var path = "/game";

        try{
            int response = this.makeRequest("POST", path, data, int.class, "authorization", auth.authToken());
            return response;
        }
        catch(ResponseException ex) {
            return ex.statusCode();
        }
    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass, String headerName, String headerValue) throws ResponseException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);

            http.setDoOutput(true);

            // Set the optional header if provided
            if (headerName != null && headerValue != null) {
                http.setRequestProperty(headerName, headerValue);
            }

            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ResponseException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            throw new ResponseException(status, "failure: " + status);
        }
    }
    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                String output = readString(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(output, responseClass); // Add the function
                }
            }
        }
        return response;
    }

    protected static String readString(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        InputStreamReader sr = new InputStreamReader(is);
        char[] buf = new char[1024];
        int len;
        while ((len = sr.read(buf)) > 0) {
            sb.append(buf, 0, len);
        }
        return sb.toString();
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }



}

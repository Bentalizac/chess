
// THIS WHOLE FILE MAY BE UNNECESSARY

import com.google.gson.Gson;
import exception.ResponseException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dataAccess.MemoryDataAccess;

import server.Server;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import model.UserData;
import model.AuthData;
import model.GameData;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class serverTests {
    static private Server testServer;
    //Petshop code has a facade, here, what is that and do I need it here?
    //static ServerFacade server;

    private static String serverUrl;

    @BeforeAll
    static void startServer() throws Exception{
        testServer = new Server();
        testServer.run(0);
        var url = "http://localhost:" + testServer.port();
        serverUrl = url;
        //server = new ServerFacade(url);
    }

    @AfterAll
    static void stopServer() {testServer.stop();}

    @Test
    void addUser() {
        var testUser = new UserData("JuanPablo", "password", "yee@haw.com");

        String path = "/addUser";
    }


    // The following is copied from the petshop's server facade, which seems to be used to generate http requests from a few parameters and an object

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass) throws ResponseException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

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
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }


    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}




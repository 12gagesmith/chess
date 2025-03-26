package serverfacade;

import chess.ChessGame;
import com.google.gson.Gson;
import serverfacade.records.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.*;

public class ServerFacade {

    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public RegisterResult register(String username, String password, String email) throws DataAccessException {
        String path = "/user";
        RegisterRequest registerRequest = new RegisterRequest(username, password, email);
        return this.makeRequest("POST", path, registerRequest, RegisterResult.class, "");
    }

    public LoginResult login(String username, String password) throws DataAccessException {
        String path = "/session";
        LoginRequest loginRequest = new LoginRequest(username, password);
        return this.makeRequest("POST", path, loginRequest, LoginResult.class, "");
    }

    public void logout(String authToken) throws DataAccessException {
        String path = "/session";
        LogoutRequest logoutRequest = new LogoutRequest(authToken);
        this.makeRequest("DELETE", path, logoutRequest, null, authToken);
    }

    public ListResult listGames(String authToken) throws DataAccessException {
        String path = "/game";
        ListRequest listRequest = new ListRequest(authToken);
        return this.makeRequest("GET", path, listRequest, ListResult.class, authToken);
    }

    public CreateResult createGame(String authToken, String gameName) throws DataAccessException {
        String path = "/game";
        CreateRequest createRequest = new CreateRequest(gameName);
        return this.makeRequest("POST", path, createRequest, CreateResult.class, authToken);
    }

    public void joinGame(String playerColor, int gameID, String authToken) throws DataAccessException {
        String path = "/game";
        JoinRequest joinRequest;
        if (playerColor.equals("BLACK")) {
            joinRequest = new JoinRequest(ChessGame.TeamColor.BLACK, gameID);
        } else if (playerColor.equals("WHITE")) {
            joinRequest = new JoinRequest(ChessGame.TeamColor.WHITE, gameID);
        } else {
            throw new DataAccessException(403, "Error: Invalid Player Color");
        }
        this.makeRequest("PUT", path, joinRequest, null, authToken);
    }

    public void clear() throws DataAccessException {
        String path = "/db";
        this.makeRequest("DELETE", path, null, null, "");
    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass, String authToken) throws DataAccessException {
        try {
            URL url = new URI(serverUrl + path).toURL(); // Set url with path
            HttpURLConnection http = (HttpURLConnection) url.openConnection(); // Make connection to that url
            http.setRequestMethod(method); // Set type of request (POST, GET, etc.)
            http.setDoOutput(true); // Needs to be set to true
            writeBody(request, http, authToken); // If the request has a body, write to it
            http.connect(); // Sends request to server
            throwIfNotSuccessful(http); // Self-explanatory
            return readBody(http, responseClass); // Find result body, and return it
        } catch (Exception ex) {
            throw new DataAccessException(500, ex.getMessage());
        }
    }

    private static void writeBody(Object request, HttpURLConnection http, String authToken) throws IOException {
        if (request != null) {
            if (request instanceof LogoutRequest || request instanceof ListRequest || request instanceof CreateRequest
                    || request instanceof JoinRequest) {
                http.addRequestProperty("authorization", authToken);
            }
            if (!(request instanceof LogoutRequest || request instanceof ListRequest)) {
                http.addRequestProperty("Content-Type", "application/json");
                String requestData = new Gson().toJson(request);
                try (OutputStream requestBody = http.getOutputStream()) {
                    requestBody.write(requestData.getBytes());
                }
            }
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws DataAccessException, IOException {
        int status = http.getResponseCode();
        if (status / 100 != 2) {
            throw new DataAccessException(status, "failure: " + status);
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream responseBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(responseBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }
}

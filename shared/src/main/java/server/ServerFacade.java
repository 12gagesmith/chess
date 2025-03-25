package server;

import com.google.gson.Gson;
import server.records.*;

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
        return this.makeRequest("POST", path, registerRequest, RegisterResult.class);
    }

    public LoginResult login(String username, String password) throws DataAccessException {
        String path = "/session";
        LoginRequest loginRequest = new LoginRequest(username, password);
        return this.makeRequest("POST", path, loginRequest, LoginResult.class);
    }

    public void logout(String authToken) throws DataAccessException {
        String path = "/session";
        LogoutRequest logoutRequest = new LogoutRequest(authToken);
        this.makeRequest("DELETE", path, logoutRequest, null);
    }

    public void listGames() throws DataAccessException {
        String path = "/game";
        this.makeRequest("GET", path, null, null);
    }

    public void createGame() throws DataAccessException {
        String path = "/game";
        this.makeRequest("POST", path, null, null);
    }

    public void joinGame() throws DataAccessException {
        String path = "/game";
        this.makeRequest("PUT", path, null, null);
    }

    public void clear() throws DataAccessException {
        String path = "/db";
        this.makeRequest("DELETE", path, null, null);
    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass) throws DataAccessException {
        try {
            URL url = new URI(serverUrl + path).toURL(); // Set url with path
            HttpURLConnection http = (HttpURLConnection) url.openConnection(); // Make connection to that url
            http.setRequestMethod(method); // Set type of request (POST, GET, etc.)
            http.setDoOutput(true); // Needs to be set to true
            writeBody(request, http); // If the request has a body, write to it
            http.connect(); // Sends request to server
            throwIfNotSuccessful(http); // Self-explanatory
            return readBody(http, responseClass); // Find result body, and return it
        } catch (Exception ex) {
            throw new DataAccessException(500, ex.getMessage());
        }
    }

    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            if (request instanceof LogoutRequest) {
                http.addRequestProperty("authorization", (((LogoutRequest) request).authToken()));
            }
            http.addRequestProperty("Content-Type", "application/json");
            String requestData = new Gson().toJson(request);
            try (OutputStream requestBody = http.getOutputStream()) {
                requestBody.write(requestData.getBytes());
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

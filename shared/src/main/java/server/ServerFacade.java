package server;

public class ServerFacade {

    public void register() {
        String path = "/user";
        String method = "POST";
    }

    public void login() {
        String path = "/session";
        String method = "POST";
    }

    public void logout() {
        String path = "/session";
        String method = "DELETE";
    }

    public void listGames() {
        String path = "/game";
        String method = "GET";
    }

    public void createGame() {
        String path = "/game";
        String method = "POST";
    }

    public void joinGame() {
        String path = "/game";
        String method = "PUT";
    }

    public void clear() {
        String path = "/db";
        String method = "DELETE";
    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass) {
        return null;
    }
}

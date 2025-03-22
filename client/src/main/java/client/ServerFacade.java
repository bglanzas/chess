package client;

import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class ServerFacade {
    private final String serverUrl;
    private final Gson gson = new Gson();

    public ServerFacade(int port) {
        this.serverUrl = "http://localhost:" + port;
    }

    private String sendRequest(String endpoint, String method, String jsonBody, String authToken) throws IOException {
        URL url = new URL(serverUrl + endpoint);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(method);
        connection.setRequestProperty("Content-Type", "application/json");
        if (authToken != null) {
            connection.setRequestProperty("Authorization", authToken);
        }

        if (jsonBody != null) {
            connection.setDoOutput(true);
            try (OutputStream os = connection.getOutputStream()) {
                os.write(jsonBody.getBytes());
            }
        }

        try (InputStreamReader reader = new InputStreamReader(connection.getInputStream());
             BufferedReader br = new BufferedReader(reader)) {
            return br.readLine();
        } catch (IOException e) {
            try (InputStreamReader errorReader = new InputStreamReader(connection.getErrorStream());
                 BufferedReader br = new BufferedReader(errorReader)) {
                return br.readLine();
            }
        }
    }

    public AuthData register(String username, String password, String email)throws Exception{
        String json = gson.toJson(new UserData(username, password, email));
        String response = sendRequest("/user", "POST", json, null);
        return gson.fromJson(response, AuthData.class);
    }

    public AuthData login(String username, String password)throws Exception{
        String json = gson.toJson(new UserData(username, password, null));
        String response = sendRequest("/session", "POST", json, null);
        return gson.fromJson(response, AuthData.class);
    }

    public void logout(String authToken) throws Exception{
        sendRequest("/session", "DELETE", null, authToken);
    }

    public List<GameData> listGames(String authToken)throws Exception{
        String response = sendRequest("/game", "GET", null, authToken);
        Map<String, List<GameData>> gameMap = gson.fromJson(response, Map.class);
        return gameMap.get("games");
    }

    public GameData create(String authToken, String gameName)throws Exception{
        String json = gson.toJson(Map.of("gameName", gameName));
        String response = sendRequest("/game", "POST", json, authToken);
        return gson.fromJson(response, GameData.class);
    }

    public void joinGame(String authToken, int gameID, String playerColor)throws Exception{
        String json = gson.toJson(Map.of("gameID", gameID, "playerColor", playerColor));
        sendRequest("/game", "PUT", json, authToken);
    }

    public void clearDatabase() throws Exception{
        sendRequest("/db", "DELETE", null, null);
    }
 }


package client;

import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import model.UserData;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.lang.reflect.Type;
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

    public AuthData register(String username, String password, String email) throws Exception {
        String json = gson.toJson(new UserData(username, password, email));
        String response = sendRequest("/user", "POST", json, null);

        if (response.contains("Error")) {
            throw new Exception("Registration failed: " + response);
        }

        return gson.fromJson(response, AuthData.class);
    }


    public AuthData login(String username, String password) throws Exception {
        String json = gson.toJson(new UserData(username, password, null));
        String response = sendRequest("/session", "POST", json, null);

        if (response.contains("Error")) {
            throw new Exception("Login failed: " + response);
        }

        return gson.fromJson(response, AuthData.class);
    }


    public void logout(String authToken) throws Exception {
        String response = sendRequest("/session", "DELETE", null, authToken);

        if (response != null && response.contains("Error")) {
            throw new Exception("Logout failed: " + response);
        }
    }

    public List<GameData> listGames(String authToken) throws Exception {
        String response = sendRequest("/game", "GET", null, authToken);

        if (response.contains("Error")) {
            throw new Exception("Unable to list games: " + response);
        }

        Type responseType = new TypeToken<Map<String, List<GameData>>>(){}.getType();
        Map<String, List<GameData>> parsed = gson.fromJson(response, responseType);
        return parsed.get("games");
    }


    public GameData createGame(String authToken, String gameName) throws Exception {
        String json = gson.toJson(Map.of("gameName", gameName));
        String response = sendRequest("/game", "POST", json, authToken);

        if (response.contains("Error")) {
            throw new Exception("Create game failed: " + response);
        }

        Map<String, Object> responseMap = gson.fromJson(response, Map.class);
        Integer gameID = ((Double) responseMap.get("gameID")).intValue();

        return new GameData(gameID, null, null, gameName, null);
    }



    public void joinGame(String authToken, int gameID, String playerColor) throws Exception {
        var request = Map.of(
                "gameID", gameID,
                "playerColor", playerColor.toUpperCase()
        );

        String json = gson.toJson(request);
        String response = sendRequest("/game", "PUT", json, authToken);

        if (response.contains("Error")) {
            if (response.toLowerCase().contains("already taken")) {
                throw new Exception("Game is full.");
            } else {
                throw new Exception("Join game failed: " + response);
            }
        }
    }

    public GameData observeGame(String authToken, int gameID, boolean whitePerspective) throws Exception {
        var request = Map.of(
                "gameID", gameID,
                "playerColor", "OBSERVER"
        );

        String json = gson.toJson(request);
        String response = sendRequest("/game", "PUT", json, authToken);

        if (response.contains("Error")) {
            throw new Exception("Observe game failed: " + response);
        }

        return gson.fromJson(response, GameData.class);
    }






    public void clearDatabase() throws Exception{
        sendRequest("/db", "DELETE", null, null);
    }
 }


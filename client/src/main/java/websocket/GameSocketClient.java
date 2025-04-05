package websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import websocket.messages.ServerMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ErrorMessage;
import ui.GameplayUI;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.Future;

@WebSocket
public class GameSocketClient {
    private final Gson gson = new Gson();
    private final String uri;
    private Session session;
    private final GameplayUI gameplayUI;

    public GameSocketClient(String uri, GameplayUI gameplayUI) {
        this.uri = uri;
        this.gameplayUI = gameplayUI;
    }

    public void connect() {
        try {
            WebSocketClient client = new WebSocketClient();
            client.start();
            Future<Session> fut = client.connect(this, URI.create(uri));
            session = fut.get();
        } catch (Exception e) {
            throw new RuntimeException("WebSocket connection failed", e);
        }
    }

    public void send(Object message) {
        try {
            String json = gson.toJson(message);
            session.getRemote().sendString(json);
        } catch (IOException e) {
            System.out.println("Failed to send message: " + e.getMessage());
        }
    }

    @OnWebSocketMessage
    public void onMessage(String msg) {
        try {
            ServerMessage baseMessage = gson.fromJson(msg, ServerMessage.class);
            switch (baseMessage.getServerMessageType()) {
                case LOAD_GAME -> {
                    LoadGameMessage m = gson.fromJson(msg, LoadGameMessage.class);
                    gameplayUI.onMessage(m);
                }
                case NOTIFICATION -> {
                    NotificationMessage m = gson.fromJson(msg, NotificationMessage.class);
                    gameplayUI.onMessage(m);
                }
                case ERROR -> {
                    ErrorMessage m = gson.fromJson(msg, ErrorMessage.class);
                    gameplayUI.onMessage(m);
                }
                default -> System.out.println("Unknown message type received");
            }
        } catch (Exception e) {
            System.out.println("Failed to process message: " + e.getMessage());
        }
    }

    @OnWebSocketError
    public void onError(Throwable t) {
        System.out.println("WebSocket error: " + t.getMessage());
    }

    @OnWebSocketClose
    public void onClose(int statusCode, String reason) {
        System.out.println("WebSocket closed: " + reason);
    }
}


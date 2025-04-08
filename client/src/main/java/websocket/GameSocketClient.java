package websocket;

import client.GameplayUI;
import com.google.gson.Gson;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;

@ClientEndpoint
public class GameSocketClient {
    private final Gson gson = new Gson();
    private final String uri;
    private final GameplayUI gameplayUI;
    private Session session;

    public GameSocketClient(String uri, GameplayUI gameplayUI) {
        this.uri = uri;
        this.gameplayUI = gameplayUI;
    }

    public void connect() {
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.connectToServer(this, URI.create(uri));
        } catch (Exception e) {
            throw new RuntimeException("WebSocket connection failed", e);
        }
    }

    public void send(Object message) {
        try {
            String json = gson.toJson(message);
            session.getBasicRemote().sendText(json);
        } catch (IOException e) {
            System.out.println("Failed to send message: " + e.getMessage());
        }
    }

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        System.out.println("WebSocket connection established.");
    }

    @OnMessage
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

}



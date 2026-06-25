package lk.techmart.web.websocket;

import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@ServerEndpoint("/inventory-sync")
public class InventorySyncWebSocket {

    // දැනට සයිට් එකේ ලොග් වෙලා ඉන්න හැම බ්‍රවුසර් සෙෂන් (Clients) එකක්ම මේ ලිස්ට් එකට සේව් වෙනවා
    private static final Set<Session> clients = Collections.synchronizedSet(new HashSet<>());

    @OnOpen
    public void onOpen(Session session) {
        clients.add(session);
        System.out.println("🚀 WebSocket Connected: " + session.getId());
    }

    @OnClose
    public void onClose(Session session) {
        clients.remove(session);
        System.out.println("❌ WebSocket Disconnected: " + session.getId());
    }

    @OnError
    public void onError(Throwable throwable) {
        System.out.println("⚠️ WebSocket Error: " + throwable.getMessage());
    }

    // 🎯 අන්න අර OrderBean එකෙන් කෝල් කරන මැජික් මෙතඩ් එක මේකයි!
    public static void broadcastStockUpdate(String productId, int newStock) {
        // බ්‍රවුසර් එකේ ජාවාස්ක්‍රිප්ට් එකට කියවන්න ලේසි වෙන්න JSON පේලෝඩ් එකක් හදනවා
        String jsonPayload = "{\"productId\":\"" + productId + "\", \"newStock\":" + newStock + "}";

        synchronized (clients) {
            // දැනට සයිට් එක බලන් ඉන්න හැමෝගෙම බ්‍රවුසර් එකට මේ මැසේජ් එක තල්ලු (Push) කරනවා
            for (Session client : clients) {
                if (client.isOpen()) {
                    try {
                        client.getBasicRemote().sendText(jsonPayload);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    // 🎯 ActiveMQ Listener එකෙන් එන මැසේජ් එක හැම බ්‍රවුසර් එකකටම යවන මෙතඩ් එක
    public static void broadcastNotification(String message) {
        // ෆ්‍රන්ට් එන්ඩ් එකේ ජාවාස්ක්‍රිප්ට් එකට ස්ටොක් එකයි නොටිෆිකේෂන් එකයි පටලවගන්නේ නැතුව
        // ලේසියෙන්ම වෙන් කරගන්න පුළුවන් වෙන්න JSON එකක් හදමු
        String jsonPayload = "{\"type\":\"NOTIFICATION\", \"message\":\"" + message + "\"}";

        synchronized (clients) {
            for (Session client : clients) {
                if (client.isOpen()) {
                    try {
                        client.getBasicRemote().sendText(jsonPayload);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
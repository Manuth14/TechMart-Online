package lk.techmart.ejb.bean;

import jakarta.ejb.Asynchronous;
import jakarta.ejb.Stateless;
import jakarta.ejb.AsyncResult;
import lk.techmart.core.service.InvoiceService;
import java.util.concurrent.Future;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Stateless
public class InvoiceBean implements InvoiceService {

    @Override
    @Asynchronous
    public Future<Boolean> generateInvoiceAndEmail(String orderId, String customerEmail) {
        HttpClient client = HttpClient.newHttpClient();

        try {
            // 🏢 1. Invoice එක හදන්න පටන් ගත්තා කියලා UI එකට කියනවා
            sendUiNotification(client, "⏳ [Async] Generating Invoice PDF for Order: " + orderId);
            Thread.sleep(3000); // Heavy task mock

            // 🏢 2. Invoice එක හැදිලා ඉවරයි කියලා UI එකට කියනවා
            sendUiNotification(client, "📄 [Async] Invoice PDF successfully generated for " + orderId);

            // ✉️ 3. ඊමේල් එක යවන්න පටන් ගත්තා කියලා UI එකට කියනවා
            sendUiNotification(client, "⏳ [Async] Sending receipt email to " + customerEmail);
            Thread.sleep(2000); // Heavy task mock

            // ✉️ 4. ඔක්කොම ඉවරයි කියලා UI එකට කියනවා
            sendUiNotification(client, "✉️ [Async] Order processing 100% complete. Email dispatched!");

            return new AsyncResult<>(true);

        } catch (Exception e) {
            sendUiNotification(client, "❌ [Async Error] Failed processing order: " + e.getMessage());
            return new AsyncResult<>(false);
        }
    }

    // 🔥 සර්ව්ලට් එක හරහා වෙබ්සොකට් එකට මැසේජ් එක තල්ලු කරන Helper මෙතඩ් එක
    private void sendUiNotification(HttpClient client, String message) {
        try {
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/techmart-web/sync-stock-trigger?notification="
                            + URLEncoder.encode(message, StandardCharsets.UTF_8)))
                    .GET()
                    .build();
            // Async හින්දා බ්ලොක් වෙන්නේ නෑ
            client.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
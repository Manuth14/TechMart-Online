package lk.techmart.ejb.notification;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.jms.*;
import org.apache.activemq.ActiveMQConnectionFactory;

// 🎯 Assignment Requirement: Singleton & Startup Bean Optimization
@Singleton
@Startup // සර්වර් එක ස්ටාර්ට් වෙද්දීම මේක බැක්ග්‍රවුන්ඩ් එකෙන් ලයිව් ස්ටාර්ට් වෙනවා
public class ActiveMQListener implements MessageListener {

    private Connection connection;
    private Session session;
    private MessageConsumer consumer;

    @PostConstruct
    public void startListening() {
        String brokerURL = "tcp://localhost:61616";
        try {
            ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(brokerURL);
            this.connection = factory.createConnection();
            this.connection.start();

            this.session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Queue queue = session.createQueue("OrderNotificationQueue");

            // 🎯 කෙලින්ම ActiveMQ කියු එකට Listener එකක් සෙට් කරනවා
            this.consumer = session.createConsumer(queue);
            this.consumer.setMessageListener(this); // onMessage මෙතඩ් එකට ලින්ක් කිරීම

            System.out.println("🚀 [ActiveMQ Listener] Background Worker Started Successfully & Listening to Queue...");

        } catch (JMSException e) {
            System.err.println("❌ [ActiveMQ Listener] Failed to start: " + e.getMessage());
        }
    }

    @Override
    public void onMessage(Message message) {
        try {
            if (message instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) message;
                String body = textMessage.getText();

                System.out.println("🔔 [ActiveMQ Listener] Got Message: " + body);

                // 🚀 වෙබ්සොකට් එක ට්‍රිගර් කරන සර්ව්ලට් එකට මැසේජ් එක යැවීම
                java.net.http.HttpClient client = java.net.http.HttpClient.newHttpClient();
                java.net.http.HttpRequest httpRequest = java.net.http.HttpRequest.newBuilder()
                        .uri(java.net.URI.create("http://localhost:8080/techmart-web/sync-stock-trigger?notification="
                                + java.net.URLEncoder.encode(body, "UTF-8")))
                        .GET()
                        .build();

                client.sendAsync(httpRequest, java.net.http.HttpResponse.BodyHandlers.ofString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @PreDestroy
    public void stopListening() {
        try {
            if (consumer != null) consumer.close();
            if (session != null) session.close();
            if (connection != null) connection.close();
            System.out.println("🛑 [ActiveMQ Listener] Background Worker Stopped.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
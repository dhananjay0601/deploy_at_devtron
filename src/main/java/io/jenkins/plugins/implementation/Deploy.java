package io.jenkins.plugins.implementation;

import hudson.Extension;
import hudson.model.AbstractBuild;
import hudson.model.listeners.RunListener;
import hudson.model.AbstractProject;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.Builder;
import java.io.*;
import jenkins.tasks.SimpleBuildStep;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import io.jenkins.plugins.implementation.Deploy;

public class Deploy extends Builder implements SimpleBuildStep {

    public static void sendPayload(String dockerImageName, String webhookUrl, TaskListener listener, String apiToken) throws IOException {

        String requestBody = "{\"dockerImage\":\"" + dockerImageName + "\"\",\"digest\":\" test1 \",\"dataSource\":\" ext \",\"materialType\":\" git \"}";
        performCurlRequest(webhookUrl, apiToken, requestBody, listener);

    }

    public static void performCurlRequest(String url, String apiToken, String requestBody, TaskListener listener) throws IOException {

        // Create the connection
        HttpURLConnection connection = null;
        try {
            URL requestUrl = new URL(url);
            connection = (HttpURLConnection) requestUrl.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("api-token", apiToken);
            listener.getLogger().println(connection);

            // Write the request body to the connection's output stream
            try (OutputStream outputStream = connection.getOutputStream()) {
                byte[] requestBodyBytes = requestBody.getBytes(StandardCharsets.UTF_8);
                outputStream.write(requestBodyBytes);
                outputStream.flush();
            }

            // Write the payload
            connection.getOutputStream().write(requestBody.getBytes());
            connection.getOutputStream().flush();

            // Get the response from the server
            int responseCode = connection.getResponseCode();
            listener.getLogger().println(requestBody);
            listener.getLogger().println(responseCode);
            if (responseCode == HttpURLConnection.HTTP_OK) {

                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    String line;
                    StringBuilder response = new StringBuilder();
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    listener.getLogger().println("Response: " + response);
                }
            } else {
                listener.getLogger().println("Failed to send cURL request. Response code: " + responseCode);
            }
        } finally {

            // Close the connection
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}

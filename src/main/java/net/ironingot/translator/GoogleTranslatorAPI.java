package net.ironingot.translator;

import net.ironingot.kanachat.KanaChat;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;

public class GoogleTranslatorAPI {
    private static final String baseURL = "https://inputtools.google.com/request";

    private static String makeURLString(String text) {
        try {
            String encodedText = URLEncoder.encode(text, StandardCharsets.UTF_8);
            return baseURL + "?text=" + encodedText + "&itc=ja-t-i0-und&num=1&ie=utf-8&oe=utf-8";
        } catch (Exception e) {
            KanaChat.logger.log(Level.SEVERE, "URL encoding error:", e);
            return baseURL;
        }
    }

    public static String translate(String text) {
        String result = text;
        try {
            String response = callWebAPI(makeURLString(text));
            String candidate = pickupFirstCandidate(response);
            if (!candidate.isEmpty()) {
                result = candidate;
            }
        } catch (Exception e) {
            KanaChat.logger.log(Level.SEVERE, "translate error:", e);
        }
        return result;
    }

    private static String pickupFirstCandidate(String response) {
        StringBuilder stringBuilder = new StringBuilder();
        JSONParser parser = new JSONParser();

        try {
            JSONArray rootArray = (JSONArray) parser.parse(response);

            if (rootArray.size() > 1 && "SUCCESS".equals(rootArray.get(0))) {
                JSONArray responseArray = (JSONArray) rootArray.get(1);

                for (Object o : responseArray) {
                    try {
                        JSONArray partArray = (JSONArray) o;
                        JSONArray candidates = (JSONArray) partArray.get(1);
                        if (!candidates.isEmpty()) {
                            stringBuilder.append((String) candidates.get(0));
                        }
                    } catch (IndexOutOfBoundsException e) {
                        KanaChat.logger.log(Level.SEVERE, "candidate parse error:", e);
                    }
                }
            }
        } catch (ParseException e) {
            KanaChat.logger.log(Level.SEVERE, "pickup parse error:", e);
        }

        return stringBuilder.toString();
    }

    private static String callWebAPI(String urlString) {
        HttpURLConnection connection = null;
        BufferedReader bufferedReader = null;
        StringBuilder stringBuilder = new StringBuilder();

        try {
            URL url = URI.create(urlString).toURL();
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");
            connection.connect();

            bufferedReader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8)
            );

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            KanaChat.logger.log(Level.SEVERE, "REST API Error", e);
        } finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (IOException ignored) {
            }

            if (connection != null) {
                connection.disconnect();
            }
        }

        return stringBuilder.toString();
    }
}

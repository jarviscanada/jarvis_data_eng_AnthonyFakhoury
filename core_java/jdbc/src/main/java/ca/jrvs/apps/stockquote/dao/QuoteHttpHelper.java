package ca.jrvs.apps.stockquote.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.sql.Timestamp;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QuoteHttpHelper {
  final Logger infoLogger = LoggerFactory.getLogger("infoLogger");
  final Logger errorLogger = LoggerFactory.getLogger("errorLogger");

  private String apiKey;
  private OkHttpClient client;

  public QuoteHttpHelper(String apiKey, OkHttpClient client) {
    this.apiKey = apiKey;
    this.client = client;
  }

  /**
   * Fetch latest quote data from Alpha Vantage endpoint
   * @param symbol
   * @return Quote with latest data
   * @throws IllegalArgumentException - if no data was found for the given symbol
   */
  public Quote fetchQuoteInfo(String symbol) throws IllegalArgumentException {
    Quote quote = new Quote();
    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create("https://alpha-vantage.p.rapidapi.com/query?function=GLOBAL_QUOTE&symbol="+symbol+"&datatype=json"))
        .header("X-RapidAPI-Key", apiKey)
        .header("X-RapidAPI-Host", "alpha-vantage.p.rapidapi.com")
        .method("GET", BodyPublishers.noBody())
        .build();
    try {
      HttpResponse<String> response = HttpClient.newHttpClient().send(request, BodyHandlers.ofString());
      ObjectMapper objectMapper = new ObjectMapper();
      JsonNode jsonNode = objectMapper.readTree(response.body());
      JsonNode globalQuoteNode = jsonNode.get("Global Quote");
      if (globalQuoteNode == null || globalQuoteNode.isEmpty()) {
        infoLogger.warn("Global Quote is null or empty.");
        return null;
      }
      quote = objectMapper.convertValue(globalQuoteNode, Quote.class);
      quote.setTimestamp(new Timestamp(System.currentTimeMillis()));
      infoLogger.info("QuoteHttpHelper: Quote sucessfully retrieved from Alpha Vantage API");
      return quote;
    } catch (InterruptedException e) {
      errorLogger.error("QuoteHttpHelper: InterruptedException error: {}", e.getMessage());
    } catch (JsonMappingException e) {
      errorLogger.error("QuoteHttpHelper: JsonMappingException error: {}", e.getMessage());
    } catch (JsonProcessingException e) {
      errorLogger.error("QuoteHttpHelper: JsonProcessingException error: {}", e.getMessage());
    } catch (IOException e) {
      errorLogger.error("QuoteHttpHelper: IOException error: {}", e.getMessage());
    } catch (IllegalArgumentException e) {
      errorLogger.error("QuoteHttpHelper: IllegalArgumentException error: {}", e.getMessage());
    }
    return quote;
  }

}

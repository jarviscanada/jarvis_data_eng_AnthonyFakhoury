package ca.jrvs.apps.stockquote.dao;

import static org.junit.jupiter.api.Assertions.*;

import okhttp3.OkHttpClient;
import org.junit.jupiter.api.Test;

public class QuoteHttpHelperTest {

  @Test
  void fetchQuoteInfo() {
    OkHttpClient client = new OkHttpClient();
    String apiKey = "baa2240413msh2e60e8817c8fcaap115d3djsn4db12cb6f9f6";
    String symbol = "MSFT";
    QuoteHttpHelper quoteHttpHelper = new QuoteHttpHelper(apiKey, client);
    Quote quote = quoteHttpHelper.fetchQuoteInfo(symbol);
    assertEquals("MSFT", quote.getTicker());
  }
}
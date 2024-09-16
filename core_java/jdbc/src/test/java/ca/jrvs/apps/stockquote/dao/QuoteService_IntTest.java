package ca.jrvs.apps.stockquote.dao;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Optional;
import okhttp3.OkHttpClient;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class QuoteService_IntTest {
  private static final String apiKey = "baa2240413msh2e60e8817c8fcaap115d3djsn4db12cb6f9f6";
  private static Connection connection;
  private static QuoteService quoteService;
  private static String ticker = "MSFT";
  private static String invalidTicker = "TEST_NO";


  @BeforeAll
  static void setup() throws SQLException {
    DatabaseConnectionManager dcm = new DatabaseConnectionManager("localhost",
        "stock_quote", "postgres", "password");
    connection = dcm.getConnection();
    QuoteHttpHelper httpHelper = new QuoteHttpHelper(apiKey, new OkHttpClient());
    QuoteDao quoteDao = new QuoteDao(connection);
    quoteService = new QuoteService(quoteDao, httpHelper);
  }

  @AfterAll
  public static void tearDown() throws SQLException {
    connection.close();
  }

  @Test
  void fetchQuoteDataFromAPI_Pass() {
    Optional<Quote> fetchedQuote = quoteService.fetchQuoteDataFromAPI(ticker);
    assertTrue(fetchedQuote.isPresent());
    assertEquals(ticker, fetchedQuote.get().getTicker());
  }

  @Test
  void fetchQuoteDataFromAPI_Fail() {
    try {
      Optional<Quote> fetchedQuote = quoteService.fetchQuoteDataFromAPI(invalidTicker);
      assertFalse(fetchedQuote.isPresent());
    } catch (IllegalArgumentException e){
      assertEquals("Invalid ticker please type a valid quote ticker!", e.getMessage());
    }
  }
}
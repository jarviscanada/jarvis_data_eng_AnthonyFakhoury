package ca.jrvs.apps.stockquote.dao;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.SQLException;
import okhttp3.OkHttpClient;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class PositionService_IntTest {
  private static final String apiKey = "baa2240413msh2e60e8817c8fcaap115d3djsn4db12cb6f9f6";
  private static Connection connection;
  private static PositionService positionService;
  private static QuoteService quoteService;


  @BeforeAll
  static void setup() throws SQLException {
    DatabaseConnectionManager dcm = new DatabaseConnectionManager("localhost",
        "stock_quote", "postgres", "password");
    connection = dcm.getConnection();
    QuoteHttpHelper httpHelper = new QuoteHttpHelper(apiKey, new OkHttpClient());
    PositionDao positionDao = new PositionDao(connection);
    QuoteDao quoteDao = new QuoteDao(connection);
    quoteService = new QuoteService(quoteDao, httpHelper);
    positionService = new PositionService(positionDao, quoteService);

//    Quote quote = new Quote();
//    quote.setTicker(ticker);
//    quote.setOpen(181.74);
//    quote.setHigh(182.43);
//    quote.setLow(180.0);
//    quote.setPrice(181.56);
//    quote.setVolume(49402449);
//    quote.setLatestTradingDay(new Date(124, 9, 10));
//    quote.setPreviousClose(182.31);
//    quote.setChange(-0.75);
//    quote.setChangePercent("-0.4114%");
//    quote.setTimestamp(new Timestamp(System.currentTimeMillis()));
//    quoteDao.save(quote);
  }

  @AfterAll
  public static void tearDown() throws SQLException {
    connection.close();
  }

  @Test
  void buy_Pass() {
    String ticker = "MSFT";
    int numberOfShares = 100;
    double price = 150.0;
    Position result = positionService.buy(ticker, numberOfShares, numberOfShares*price);
    assertNotNull(result);
    assertEquals(ticker, result.getTicker());
  }

  @Test
  void buy_Fail() {
    String ticker = "MSFT";
    int numberOfShares = 999999999;
    double price = 150.0;
    try {
      Position result = positionService.buy(ticker, numberOfShares, numberOfShares*price);
      assertNull(result);
    } catch (IllegalArgumentException e){
      assertEquals("Cannot buy more shares than available volume", e.getMessage());
    }
  }

  @Test
  void sell_Pass() {
    String ticker = "MSFT";
    positionService.sell(ticker);
  }

  @Test
  void sell_Fail() {
    String ticker = "AAPL";
    try {
      positionService.sell(ticker);
    } catch (IllegalArgumentException e){
      assertEquals("Cannot sell stock shares you do not own", e.getMessage());
    }
  }
}
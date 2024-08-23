package ca.jrvs.apps.stockquote.dao;

import static org.junit.jupiter.api.Assertions.*;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Optional;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class QuoteDaoTest {

  private static Connection c;
  private static QuoteDao quoteDao;
  private static DatabaseConnectionManager dcm;
  private static Quote testQuote1;
  private static Quote testQuote2;
  private static String ticker1;
  private static String ticker2;
  private static Timestamp testTimestamp;

  @BeforeAll
  public static void setUp() throws SQLException {
    dcm = new DatabaseConnectionManager("localhost",
        "stock_quote", "postgres", "password");
    c = dcm.getConnection();
    quoteDao = new QuoteDao(c);

    ticker1 = "TESTSTOCK1";
    ticker2 = "TESTSTOCK2";
    testTimestamp = new Timestamp(System.currentTimeMillis());

    //populate quote table with dummy data
    testQuote1 = new Quote();
    testQuote1.setTicker(ticker1);
    testQuote1.setOpen(181.74);
    testQuote1.setHigh(182.43);
    testQuote1.setLow(180.0);
    testQuote1.setPrice(181.56);
    testQuote1.setVolume(49402449);
    testQuote1.setLatestTradingDay(new Date(124, 9, 10));
    testQuote1.setPreviousClose(182.31);
    testQuote1.setChange(-0.75);
    testQuote1.setChangePercent("-0.4114%");
    testQuote1.setTimestamp(testTimestamp);
    quoteDao.save(testQuote1);

    testQuote2 = new Quote();
    testQuote2.setTicker(ticker2);
    testQuote2.setOpen(281.74);
    testQuote2.setHigh(282.43);
    testQuote2.setLow(280.0);
    testQuote2.setPrice(281.56);
    testQuote2.setVolume(49402449);
    testQuote2.setLatestTradingDay(new Date(124, 9, 10));
    testQuote2.setPreviousClose(282.31);
    testQuote2.setChange(-0.75);
    testQuote2.setChangePercent("-0.4114%");
    testQuote2.setTimestamp(testTimestamp);
    quoteDao.save(testQuote2);
  }

  @AfterAll
  public static void tearDown() throws SQLException {
    quoteDao.deleteById(ticker1);
    quoteDao.deleteById(ticker2);
    c.close();
  }

  @Test
  void save() {
    Quote savedQuote = quoteDao.save(testQuote1);
    assertEquals(savedQuote, testQuote1);
    assertEquals(ticker1, testQuote1.getTicker());
    assertEquals(181.74, testQuote1.getOpen(), 0.001);
    assertEquals(182.43, testQuote1.getHigh(), 0.001);
    assertEquals(180.0, testQuote1.getLow(), 0.001);
    assertEquals(181.56, testQuote1.getPrice(), 0.001);
    assertEquals(49402449, testQuote1.getVolume(), 0.001);
    assertEquals(new Date(124, 9, 10), testQuote1.getLatestTradingDay());
    assertEquals(182.31, testQuote1.getPreviousClose(), 0.001);
    assertEquals(-0.75, testQuote1.getChange(), 0.001);
    assertEquals("-0.4114%", testQuote1.getChangePercent());
    assertEquals(testTimestamp, testQuote1.getTimestamp());

    testQuote1.setOpen(182.74);
    savedQuote = quoteDao.save(testQuote1);
    assertEquals(savedQuote, testQuote1);
    assertEquals(182.74, testQuote1.getOpen(), 0.001);

    testQuote1.setOpen(181.74);
    savedQuote = quoteDao.save(testQuote1);
    assertEquals(savedQuote, testQuote1);
    assertEquals(181.74, testQuote1.getOpen(), 0.001);
  }

  @Test
  void findById() {
    Optional<Quote> foundQuote = quoteDao.findById(ticker1);
    assertTrue(foundQuote.isPresent());
    assertEquals(ticker1, foundQuote.get().getTicker());
    assertEquals(181.74, foundQuote.get().getOpen(), 0.001);
    assertEquals(182.43, foundQuote.get().getHigh(), 0.001);
    assertEquals(180.0, foundQuote.get().getLow(), 0.001);
    assertEquals(181.56, foundQuote.get().getPrice(), 0.001);
    assertEquals(49402449, foundQuote.get().getVolume(), 0.001);
    assertEquals(new Date(124, 9, 10), foundQuote.get().getLatestTradingDay());
    assertEquals(182.31, foundQuote.get().getPreviousClose(), 0.001);
    assertEquals(-0.75, foundQuote.get().getChange(), 0.001);
    assertEquals("-0.4114%", foundQuote.get().getChangePercent());
    assertEquals(testTimestamp, foundQuote.get().getTimestamp());
  }

  @Test
  void findAll() {
    Iterable<Quote> quotes = quoteDao.findAll();
    for (Quote q : quotes) {
      if (q.getTicker().equals(ticker1)) {
        assertEquals(ticker1, q.getTicker());
      }else if (q.getTicker().equals(ticker2)){
        assertEquals(ticker2, q.getTicker());
      }
    }
  }

  @Test
  void deleteById() {
    quoteDao.deleteById(ticker1);
    Optional<Quote> deletedQuote = quoteDao.findById(ticker1);
    assertFalse(deletedQuote.isPresent());
  }

  @Test
  void deleteAll() {
    quoteDao.deleteAll();
    Optional<Quote> deletedQuote1 = quoteDao.findById(ticker1);
    Optional<Quote> deletedQuote2 = quoteDao.findById(ticker2);
    assertFalse(deletedQuote1.isPresent());
    assertFalse(deletedQuote2.isPresent());
  }
}
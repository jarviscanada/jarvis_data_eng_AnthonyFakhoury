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

class PositionDaoTest {

  private static Connection c;
  private static DatabaseConnectionManager dcm;
  private static QuoteDao quoteDao;
  private static PositionDao positionDao;
  private static Quote testQuote1;
  private static Quote testQuote2;
  private static Position testPosition1;
  private static Position testPosition2;
  private static String ticker1;
  private static String ticker2;
  private static Timestamp testTimestamp;

  @BeforeAll
  public static void setUp() throws SQLException {
    dcm = new DatabaseConnectionManager("localhost",
        "stock_quote", "postgres", "password");
    c = dcm.getConnection();
    quoteDao = new QuoteDao(c);
    positionDao = new PositionDao(c);

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

    testPosition1 = new Position();
    testPosition1.setTicker(ticker1);
    testPosition1.setNumOfShares(10);
    testPosition1.setValuePaid(20.50);
    positionDao.save(testPosition1);

    testPosition2 = new Position();
    testPosition2.setTicker(ticker2);
    testPosition2.setNumOfShares(30);
    testPosition2.setValuePaid(40.50);
    positionDao.save(testPosition2);
  }

  @AfterAll
  public static void tearDown() throws SQLException {
    positionDao.deleteById(ticker1);
    positionDao.deleteById(ticker2);
    quoteDao.deleteById(ticker1);
    quoteDao.deleteById(ticker2);
    c.close();
  }

  @Test
  void save() {
    Position savedPosition = positionDao.save(testPosition1);
    assertEquals(savedPosition, testPosition1);
    assertEquals(ticker1, testPosition1.getTicker());
    assertEquals(10, testPosition1.getNumOfShares());
    assertEquals(20.50, testPosition1.getValuePaid(), 0.001);

    testPosition1.setNumOfShares(11);
    savedPosition = positionDao.save(testPosition1);
    assertEquals(savedPosition, testPosition1);
    assertEquals(11, testPosition1.getNumOfShares(), 0.001);

    testPosition1.setNumOfShares(10);
    savedPosition = positionDao.save(testPosition1);
    assertEquals(savedPosition, testPosition1);
    assertEquals(10, testPosition1.getNumOfShares(), 0.001);
  }

  @Test
  void findById() {
    Optional<Position> foundPosition = positionDao.findById(ticker1);
    assertTrue(foundPosition.isPresent());
    assertEquals(ticker1, foundPosition.get().getTicker());
    assertEquals(10, foundPosition.get().getNumOfShares());
    assertEquals(20.50, foundPosition.get().getValuePaid(), 0.001);
  }

  @Test
  void findAll() {
    Iterable<Position> positions = positionDao.findAll();
    for (Position p : positions) {
      if (p.getTicker().equals(ticker1)) {
        assertEquals(ticker1, p.getTicker());
      }else if (p.getTicker().equals(ticker2)){
        assertEquals(ticker2, p.getTicker());
      }
    }
  }

  @Test
  void deleteById() {
    positionDao.deleteById(ticker1);
    Optional<Position> deletedPosition = positionDao.findById(ticker1);
    assertFalse(deletedPosition.isPresent());
  }

  @Test
  void deleteAll() {
    positionDao.deleteAll();
    Optional<Position> deletedPosition1 = positionDao.findById(ticker1);
    Optional<Position> deletedPosition2 = positionDao.findById(ticker2);
    assertFalse(deletedPosition1.isPresent());
    assertFalse(deletedPosition2.isPresent());
  }
}
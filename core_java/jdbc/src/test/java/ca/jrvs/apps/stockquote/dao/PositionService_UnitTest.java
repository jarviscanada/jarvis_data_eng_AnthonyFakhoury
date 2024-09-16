package ca.jrvs.apps.stockquote.dao;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PositionService_UnitTest {
  @Mock
  private PositionDao mockPositionDao;
  @Mock
  private QuoteService mockQuoteService;
  @InjectMocks
  private PositionService positionService;

  @BeforeEach
  void setup() {
    positionService = new PositionService(mockPositionDao, mockQuoteService);
  }

  @Test
  void buy_Pass() {
    String ticker = "TEST";
    int numberOfShares = 100;
    double price = 150.0;
    Quote mockQuote = new Quote();
    mockQuote.setTicker(ticker);
    mockQuote.setOpen(181.74);
    mockQuote.setHigh(182.43);
    mockQuote.setLow(180.0);
    mockQuote.setPrice(181.56);
    mockQuote.setVolume(49402449);
    mockQuote.setLatestTradingDay(new Date(124, 9, 10));
    mockQuote.setPreviousClose(182.31);
    mockQuote.setChange(-0.75);
    mockQuote.setChangePercent("-0.4114%");
    mockQuote.setTimestamp(new Timestamp(System.currentTimeMillis()));
    Position mockPosition = new Position();
    mockPosition.setTicker(ticker);
    mockPosition.setNumOfShares(numberOfShares);
    mockPosition.setValuePaid(numberOfShares*price);

    when(mockQuoteService.fetchQuoteDataFromAPI(ticker)).thenReturn(Optional.of(mockQuote));
    when(mockPositionDao.findById(ticker)).thenReturn(Optional.empty());
    when(mockPositionDao.save(any(Position.class))).thenReturn(mockPosition);

    Position result = positionService.buy(ticker, numberOfShares, numberOfShares*price);
    assertNotNull(result);
    assertEquals(mockPosition, result);
    verify(mockQuoteService).fetchQuoteDataFromAPI(ticker);
    verify(mockPositionDao).findById(ticker);
    verify(mockPositionDao).save(any(Position.class));
  }

  @Test
  void buy_Fail(){
    String ticker = "TEST";
    int numberOfShares = 100;
    double price = 150.0;
    Quote mockQuote = new Quote();
    mockQuote.setTicker(ticker);
    mockQuote.setOpen(181.74);
    mockQuote.setHigh(182.43);
    mockQuote.setLow(180.0);
    mockQuote.setPrice(181.56);
    mockQuote.setVolume(50);
    mockQuote.setLatestTradingDay(new Date(124, 9, 10));
    mockQuote.setPreviousClose(182.31);
    mockQuote.setChange(-0.75);
    mockQuote.setChangePercent("-0.4114%");
    mockQuote.setTimestamp(new Timestamp(System.currentTimeMillis()));

    when(mockQuoteService.fetchQuoteDataFromAPI(ticker)).thenReturn(Optional.of(mockQuote));

    try {
      Position result = positionService.buy(ticker, numberOfShares, numberOfShares*price);
      assertNull(result);
    } catch (IllegalArgumentException e){
      assertEquals("Cannot buy more shares than available volume", e.getMessage());
    }
    verify(mockQuoteService).fetchQuoteDataFromAPI(ticker);
    verify(mockPositionDao, never()).findById(ticker);
    verify(mockPositionDao, never()).save(any(Position.class));
  }

  @Test
  void sell_Pass() {
    String ticker = "TEST";
    int numberOfShares = 100;
    double price = 150.0;
    Position mockPosition = new Position();
    mockPosition.setTicker(ticker);
    mockPosition.setNumOfShares(numberOfShares);
    mockPosition.setValuePaid(numberOfShares*price);

    when(mockPositionDao.findById(ticker)).thenReturn(Optional.of(mockPosition));
    doNothing().when(mockPositionDao).deleteById(ticker);

    positionService.sell(ticker);

    verify(mockPositionDao).findById(ticker);
    verify(mockPositionDao).deleteById(ticker);
  }

  @Test
  void sell_Fail() {
    String ticker = "TEST";

    when(mockPositionDao.findById(ticker)).thenReturn(Optional.empty());

    try {
      positionService.sell(ticker);
    } catch (IllegalArgumentException e){
      assertEquals("Cannot sell stock shares you do not own", e.getMessage());
    }

    verify(mockPositionDao).findById(ticker);
    verify(mockPositionDao, times(0)).deleteById(ticker);
  }
}
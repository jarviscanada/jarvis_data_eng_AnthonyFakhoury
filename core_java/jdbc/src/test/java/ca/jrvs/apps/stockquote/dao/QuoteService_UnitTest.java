package ca.jrvs.apps.stockquote.dao;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
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
class QuoteService_UnitTest {
  @Mock
  private QuoteDao mockQuoteDao;
  @Mock
  private QuoteHttpHelper mockQuoteHttpHelper;
  @InjectMocks
  private QuoteService quoteService;

  @BeforeEach
  void setup() {
    quoteService = new QuoteService(mockQuoteDao, mockQuoteHttpHelper);
  }

  @Test
  void fetchQuoteDataFromAPI_Pass() {
    String ticker = "TEST_YES";
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

    when(mockQuoteHttpHelper.fetchQuoteInfo(ticker)).thenReturn(mockQuote);
    when(mockQuoteDao.findById(ticker)).thenReturn(Optional.empty());
    when(mockQuoteDao.save(mockQuote)).thenReturn(mockQuote);

    Optional<Quote> result = quoteService.fetchQuoteDataFromAPI(ticker);
    assertTrue(result.isPresent());
    assertEquals(mockQuote, result.get());
    verify(mockQuoteHttpHelper).fetchQuoteInfo(ticker);
    verify(mockQuoteDao).findById(ticker);
    verify(mockQuoteDao).save(mockQuote);
  }

  @Test
  void fetchQuoteDataFromAPI_Fail(){
    String ticker = "TEST_NO";

    when(mockQuoteHttpHelper.fetchQuoteInfo(ticker)).thenReturn(null);

    try {
      Optional<Quote> result = quoteService.fetchQuoteDataFromAPI(ticker);
      assertFalse(result.isPresent());
    } catch (IllegalArgumentException e){
      assertEquals("Invalid ticker please type a valid quote ticker!", e.getMessage());
    }
    verify(mockQuoteHttpHelper).fetchQuoteInfo(ticker);
    verify(mockQuoteDao, never()).findById(any(String.class));
    verify(mockQuoteDao, never()).save(any(Quote.class));
  }
}
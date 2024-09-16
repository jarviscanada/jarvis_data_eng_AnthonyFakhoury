package ca.jrvs.apps.stockquote.dao;

import java.util.NoSuchElementException;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QuoteService {
  final Logger infoLogger = LoggerFactory.getLogger("infoLogger");
  final Logger errorLogger = LoggerFactory.getLogger("errorLogger");

  private QuoteDao dao;
  private QuoteHttpHelper httpHelper;

  public QuoteService(QuoteDao dao, QuoteHttpHelper httpHelper) {
    this.dao = dao;
    this.httpHelper = httpHelper;
  }

  /**
   * Fetches latest quote data from endpoint
   * @param ticker
   * @return Latest quote information or empty optional if ticker symbol not found
   */
  public Optional<Quote> fetchQuoteDataFromAPI(String ticker) {
    Optional<Quote> quote = Optional.of(new Quote());
    try {
      infoLogger.info("Quote service: Fetching quote from API");
      quote = Optional.ofNullable(httpHelper.fetchQuoteInfo(ticker));
      if (quote.isEmpty()) {
        errorLogger.error("Quote service: Invalid ticker please type a valid quote ticker!");
        throw new IllegalArgumentException("Invalid ticker please type a valid quote ticker!");
      }
      if (dao.findById(ticker).isEmpty()){
        infoLogger.info("Quote service: Quote doesn't currently exist in table, saving to table.");
        dao.save(quote.get());
      }else{
        infoLogger.info("Quote service: Successfully fetched quote from api");
      }
    }catch (NoSuchElementException e) {
      errorLogger.error("Quote service: NoSuchElementException, Ticker doesn't exist{}",
          e.getMessage());
    }
    return quote;
  }
}
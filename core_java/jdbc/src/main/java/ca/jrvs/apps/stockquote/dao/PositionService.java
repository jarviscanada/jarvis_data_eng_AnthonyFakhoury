package ca.jrvs.apps.stockquote.dao;

import java.util.NoSuchElementException;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PositionService {
  final Logger infoLogger = LoggerFactory.getLogger("infoLogger");
  final Logger errorLogger = LoggerFactory.getLogger("errorLogger");

  private PositionDao dao;
  private QuoteService quoteService;

  public PositionService(PositionDao dao, QuoteService quoteService) {
    this.dao = dao;
    this.quoteService = quoteService;
  }

  /**
   * Processes a buy order and updates the database accordingly
   * @param ticker
   * @param numberOfShares
   * @param price
   * @return The position in our database after processing the buy
   */
  public Position buy(String ticker, int numberOfShares, double price) {
    Optional<Position> position = Optional.of(new Position());
    Position newPosition = new Position();
    try {
      Optional<Quote> quote = quoteService.fetchQuoteDataFromAPI(ticker);
      infoLogger.info("Position service: Buying {} shares of {} at price {}", numberOfShares, ticker, price);
      if (quote.isEmpty()) {
        errorLogger.error("Position service: Can not find quote for ticker {}", ticker);
        throw new IllegalArgumentException("Quote not found for ticker: " + ticker);
      }
      if (numberOfShares > quote.get().getVolume()) {
        errorLogger.error("Position service: Trying to buy more shares than exists!");
        throw new IllegalArgumentException("Cannot buy more shares than available volume");
      }
      position = dao.findById(ticker);
      if (position.isPresent()){
        newPosition.setTicker(ticker);
        newPosition.setNumOfShares(position.get().getNumOfShares() + numberOfShares);
        newPosition.setValuePaid(position.get().getValuePaid() + (numberOfShares*price));
        infoLogger.info("Position service: Updated existing position: {}", newPosition);
      }else{
        newPosition.setTicker(ticker);
        newPosition.setNumOfShares(numberOfShares);
        newPosition.setValuePaid(price);
        infoLogger.info("Position service: Created new position: {}", newPosition);
      }
      infoLogger.info("Position service: Has successfully purchased stock");
    }catch (NoSuchElementException e) {
      errorLogger.error("Position service: NoSuchElementException, Ticker doesn't exist{}",
          e.getMessage());
    }
    return dao.save(newPosition);
  }

  /**
   * Sells all shares of the given ticker symbol
   * @param ticker
   */
  public void sell(String ticker) {
    infoLogger.info("Position service: Selling all shares of {}", ticker);
    Optional<Position> position = dao.findById(ticker);
    if (position.isEmpty()) {
      errorLogger.error("Position service: You do not own this stock");
      throw new IllegalArgumentException("Cannot sell stock shares you do not own");
    }else{
      dao.deleteById(ticker);
      infoLogger.info("Position service: Stock has been deleted");
    }
  }

  public Iterable<Position> view() {
    infoLogger.info("Position service: Showing all positions: ");
    return dao.findAll();
  }
}

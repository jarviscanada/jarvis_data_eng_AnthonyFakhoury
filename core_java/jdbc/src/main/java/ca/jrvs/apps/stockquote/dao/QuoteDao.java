package ca.jrvs.apps.stockquote.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.sql.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QuoteDao implements CrudDao<Quote, String> {
  final Logger infoLogger = LoggerFactory.getLogger("infoLogger");
  final Logger errorLogger = LoggerFactory.getLogger("errorLogger");

  private Connection c;

  public QuoteDao(Connection c) {
    this.c = c;
  }

  private static final String INSERT = "INSERT INTO quote (symbol, open, "
      + "high, low, price, volume, latest_trading_day, previous_close, change, "
      + "change_percent, timestamp) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
  private static final String UPDATE = "UPDATE quote SET open = ?, high = ?, "
      + "low = ?, price = ?, volume = ?, latest_trading_day = ?, "
      + "previous_close = ?, change = ?, change_percent = ?, timestamp = ? "
      + "WHERE symbol = ?";
  private static final String GET_ONE = "SELECT symbol, open, high, low, price, "
      + "volume, latest_trading_day, previous_close, change, change_percent, "
      + "timestamp FROM quote WHERE symbol = ?";
  private static final String GET_ALL = "SELECT * FROM quote";
  private static final String DELETE_ONE = "DELETE FROM quote WHERE symbol = ?";
  private static final String DELETE_ALL = "DELETE FROM quote";

  @Override
  public Quote save(Quote entity) throws IllegalArgumentException {
    if (entity == null){
      errorLogger.error("QuoteDao: Illegal Argument Exception entity must not be null");
      throw new IllegalArgumentException();
    }
    if (this.findById(entity.getTicker()).isEmpty()) {
      try(PreparedStatement statement = this.c.prepareStatement(INSERT)){
        statement.setString(1, entity.getTicker());
        statement.setDouble(2, entity.getOpen());
        statement.setDouble(3, entity.getHigh());
        statement.setDouble(4, entity.getLow());
        statement.setDouble(5, entity.getPrice());
        statement.setInt(6, entity.getVolume());
        statement.setDate(7, entity.getLatestTradingDay());
        statement.setDouble(8, entity.getPreviousClose());
        statement.setDouble(9, entity.getChange());
        statement.setString(10, entity.getChangePercent());
        statement.setTimestamp(11, entity.getTimestamp());
        statement.execute();
        infoLogger.info("QuoteDao: Quote has been saved {}", entity);
        return entity;
      }catch(SQLException e){
        errorLogger.error("QuoteDao: SQLException! Failed to save quote {}", e.getMessage());
        throw new RuntimeException(e);
      }
    } else {
      try(PreparedStatement statement = this.c.prepareStatement(UPDATE)){
        statement.setDouble(1, entity.getOpen());
        statement.setDouble(2, entity.getHigh());
        statement.setDouble(3, entity.getLow());
        statement.setDouble(4, entity.getPrice());
        statement.setInt(5, entity.getVolume());
        statement.setDate(6, entity.getLatestTradingDay());
        statement.setDouble(7, entity.getPreviousClose());
        statement.setDouble(8, entity.getChange());
        statement.setString(9, entity.getChangePercent());
        statement.setTimestamp(10, entity.getTimestamp());
        statement.setString(11, entity.getTicker());
        statement.execute();
        infoLogger.info("QuoteDao: Quote has been updated {}", entity);
        return entity;
      }catch(SQLException e){
        errorLogger.error("QuoteDao: SQLException! Failed to update quote {}", e.getMessage());
        throw new RuntimeException(e);
      }
    }
  }

  @Override
  public Optional<Quote> findById(String s) throws IllegalArgumentException {
    if (s == null){
      errorLogger.error("QuoteDao: Illegal Argument Exception s must not be null in findById");
      throw new IllegalArgumentException();
    }
    Quote quote = new Quote();
    try(PreparedStatement statement = this.c.prepareStatement(GET_ONE)){
      statement.setString(1, s);
      ResultSet rs = statement.executeQuery();
      if(rs.next()){
        quote.setTicker(rs.getString("symbol"));
        quote.setOpen(rs.getDouble("open"));
        quote.setHigh(rs.getDouble("high"));
        quote.setLow(rs.getDouble("low"));
        quote.setPrice(rs.getDouble("price"));
        quote.setVolume(rs.getInt("volume"));
        quote.setLatestTradingDay(rs.getDate("latest_trading_day"));
        quote.setPreviousClose(rs.getDouble("previous_close"));
        quote.setChange(rs.getDouble("change"));
        quote.setChangePercent(rs.getString("change_percent"));
        quote.setTimestamp(rs.getTimestamp("timestamp"));
        infoLogger.info("QuoteDao: Quote has been found {}", quote);
        return Optional.of(quote);
      }else{
        infoLogger.info("QuoteDao: No Quote found with symbol {}", s);
        return Optional.empty();
      }
    }catch (SQLException e){
      errorLogger.error("QuoteDao: Failed to find quote {}", e.getMessage());
      throw new RuntimeException(e);
    }
  }

  @Override
  public Iterable<Quote> findAll() {
    List<Quote> quotes = new ArrayList<>();
    try (PreparedStatement statement = this.c.prepareStatement(GET_ALL)) {
      ResultSet rs = statement.executeQuery();
      while (rs.next()) {
        Quote quote = new Quote();
        quote.setTicker(rs.getString("symbol"));
        quote.setOpen(rs.getDouble("open"));
        quote.setHigh(rs.getDouble("high"));
        quote.setLow(rs.getDouble("low"));
        quote.setPrice(rs.getDouble("price"));
        quote.setVolume(rs.getInt("volume"));
        quote.setLatestTradingDay(rs.getDate("latest_trading_day"));
        quote.setPreviousClose(rs.getDouble("previous_close"));
        quote.setChange(rs.getDouble("change"));
        quote.setChangePercent(rs.getString("change_percent"));
        quote.setTimestamp(rs.getTimestamp("timestamp"));
        quotes.add(quote);
      }
      infoLogger.info("QuoteDao: Quotes have been found {}", quotes);
    } catch (SQLException e) {
      errorLogger.error("QuoteDao: Failed to find all quotes {}", e.getMessage());
    }
    return quotes;
  }

  @Override
  public void deleteById(String s) throws IllegalArgumentException {
    if (s == null){
      errorLogger.error("QuoteDao: Illegal Argument Exception s must not be null in deleteById");
      throw new IllegalArgumentException();
    }
    try(PreparedStatement statement = this.c.prepareStatement(DELETE_ONE)){
      statement.setString(1, s);
      statement.execute();
      infoLogger.info("QuoteDao: Quote has been deleted {}", s);
    }catch (SQLException e){
      errorLogger.error("QuoteDao: Failed to delete quote {}", e.getMessage());
      throw new RuntimeException(e);
    }
  }

  @Override
  public void deleteAll() {
    try (PreparedStatement statement = this.c.prepareStatement(DELETE_ALL)) {
      statement.execute();
      infoLogger.info("QuoteDao: Quotes have been deleted");
    } catch (SQLException e) {
      errorLogger.error("QuoteDao: Failed to delete all quotes {}", e.getMessage());
    }
  }
}

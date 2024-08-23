package ca.jrvs.apps.stockquote.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PositionDao implements CrudDao<Position, String>{
  final Logger infoLogger = LoggerFactory.getLogger("infoLogger");
  final Logger errorLogger = LoggerFactory.getLogger("errorLogger");

  private Connection c;

  public PositionDao(Connection c) {
    this.c = c;
  }

  private static final String INSERT = "INSERT INTO position (symbol, "
      + "number_of_shares, value_paid) VALUES (?, ?, ?)";
  private static final String UPDATE = "UPDATE position SET number_of_shares = ?, "
      + "value_paid = ? WHERE symbol = ?";
  private static final String GET_ONE = "SELECT symbol, number_of_shares, "
      + "value_paid FROM position WHERE symbol = ?";
  private static final String GET_ALL = "SELECT * FROM position";
  private static final String DELETE_ONE = "DELETE FROM position WHERE symbol = ?";
  private static final String DELETE_ALL = "DELETE FROM position";

  @Override
  public Position save(Position entity) throws IllegalArgumentException {
    if (entity == null){
      errorLogger.error("PositionDao: Illegal Argument Exception entity must not be null");
      throw new IllegalArgumentException();
    }
    if (this.findById(entity.getTicker()).isEmpty()) {
      try(PreparedStatement statement = this.c.prepareStatement(INSERT)){
        statement.setString(1, entity.getTicker());
        statement.setDouble(2, entity.getNumOfShares());
        statement.setDouble(3, entity.getValuePaid());
        statement.execute();
        infoLogger.info("PositionDao: Position has been saved {}", entity);
        return entity;
      }catch(SQLException e){
        errorLogger.error("PositionDao: SQLException! Failed to save position {}", e.getMessage());
        throw new RuntimeException(e);
      }
    } else {
      try(PreparedStatement statement = this.c.prepareStatement(UPDATE)){
        statement.setDouble(1, entity.getNumOfShares());
        statement.setDouble(2, entity.getValuePaid());
        statement.setString(3, entity.getTicker());
        statement.execute();
        infoLogger.info("PositionDao: Position has been updated {}", entity);
        return entity;
      }catch(SQLException e){
        errorLogger.error("PositionDao: SQLException! Failed to update position {}", e.getMessage());
        throw new RuntimeException(e);
      }
    }
  }

  @Override
  public Optional<Position> findById(String s) throws IllegalArgumentException {
    if (s == null){
      errorLogger.error("PositionDao: Illegal Argument Exception s must not be null in findById");
      throw new IllegalArgumentException();
    }
    Position position = new Position();
    try(PreparedStatement statement = this.c.prepareStatement(GET_ONE)){
      statement.setString(1, s);
      ResultSet rs = statement.executeQuery();
      if(rs.next()){
        position.setTicker(rs.getString("symbol"));
        position.setNumOfShares(rs.getInt("number_of_shares"));
        position.setValuePaid(rs.getDouble("value_paid"));
        infoLogger.info("PositionDao: Position has been found {}", position);
        return Optional.of(position);
      }else{
        infoLogger.info("QuoteDao: No Quote found with symbol {}", s);
        return Optional.empty();
      }
    }catch (SQLException e){
      errorLogger.error("PositionDao: Failed to find position {}", e.getMessage());
      throw new RuntimeException(e);
    }
  }

  @Override
  public Iterable<Position> findAll() {
    List<Position> positions = new ArrayList<>();
    try (PreparedStatement statement = this.c.prepareStatement(GET_ALL)) {
      ResultSet rs = statement.executeQuery();
      while (rs.next()) {
        Position position = new Position();
        position.setTicker(rs.getString("symbol"));
        position.setNumOfShares(rs.getInt("number_of_shares"));
        position.setValuePaid(rs.getDouble("value_paid"));
        positions.add(position);
      }
      infoLogger.info("PositionDao: Position have been found {}", positions);
    } catch (SQLException e) {
      errorLogger.error("PositionDao: Failed to find all positions {}", e.getMessage());
    }
    return positions;
  }

  @Override
  public void deleteById(String s) throws IllegalArgumentException {
    if (s == null){
      errorLogger.error("PositionDao: Illegal Argument Exception s must not be null in deleteById");
      throw new IllegalArgumentException();
    }
    try(PreparedStatement statement = this.c.prepareStatement(DELETE_ONE)){
      statement.setString(1, s);
      statement.execute();
      infoLogger.info("PositionDao: Position has been deleted {}", s);
    }catch (SQLException e){
      errorLogger.error("PositionDao: Failed to delete position {}", e.getMessage());
      throw new RuntimeException(e);
    }
  }

  @Override
  public void deleteAll() {
    try (PreparedStatement statement = this.c.prepareStatement(DELETE_ALL)) {
      statement.execute();
      infoLogger.info("PositionDao: Positions have been deleted");
    } catch (SQLException e) {
      errorLogger.error("PositionDao: Failed to delete all positions {}", e.getMessage());
    }
  }
}

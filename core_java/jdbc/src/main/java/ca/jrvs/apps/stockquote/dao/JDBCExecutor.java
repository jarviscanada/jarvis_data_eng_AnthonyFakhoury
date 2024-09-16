package ca.jrvs.apps.stockquote.dao;

import ca.jrvs.apps.jdbc.Customer;
import ca.jrvs.apps.jdbc.CustomerDAO;
import ca.jrvs.apps.jdbc.DatabaseConnectionManager;
import ca.jrvs.apps.jdbc.Order;
import ca.jrvs.apps.jdbc.OrderDAO;
import java.sql.Connection;
import java.sql.SQLException;
import okhttp3.OkHttpClient;

public class JDBCExecutor {
  private static final String apiKey = "baa2240413msh2e60e8817c8fcaap115d3djsn4db12cb6f9f6";
  private static Connection connection;
  private static PositionService positionService;
  private static QuoteService quoteService;

  public static void main(String[] args) {
    DatabaseConnectionManager dcm = new DatabaseConnectionManager("localhost",
        "stock_quote", "postgres", "password");
    try{
      Connection connection = dcm.getConnection();
      QuoteHttpHelper httpHelper = new QuoteHttpHelper(apiKey, new OkHttpClient());
      PositionDao positionDao = new PositionDao(connection);
      QuoteDao quoteDao = new QuoteDao(connection);
      quoteService = new QuoteService(quoteDao, httpHelper);
      positionService = new PositionService(positionDao, quoteService);
    }catch(SQLException e){
      e.printStackTrace();
    }
  }
}

package ca.jrvs.apps.jdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class JDBCExecutor {

  public static void main(String[] args) {
    DatabaseConnectionManager dcm = new DatabaseConnectionManager("localhost",
        "hplussport", "postgres", "password");
    try{
      Connection connection = dcm.getConnection();
      CustomerDAO customerDAO = new CustomerDAO(connection);
      Customer customer = new Customer();
      customer.setFirstName("John");
      customer.setLastName("Adams");
      customer.setEmail("jadams.wh.gov");
      customer.setAddress("1234 Main St");
      customer.setCity("Arlington");
      customer.setState("VA");
      customer.setPhone("(555) 555-2845");
      customer.setZipCode("01234");

      Customer dbCustomer = customerDAO.create(customer);
      System.out.println(dbCustomer);
      dbCustomer = customerDAO.findByID(dbCustomer.getId());
      System.out.println(dbCustomer);
      dbCustomer.setEmail("john.adams@wh.gov");
      dbCustomer = customerDAO.update(dbCustomer);
      System.out.println(dbCustomer);
      customerDAO.delete(dbCustomer.getId());

      OrderDAO orderDAO = new OrderDAO(connection);
      Order order = orderDAO.findByID(1000);
      System.out.println(order);
    }catch(SQLException e){
      e.printStackTrace();
    }
  }
}

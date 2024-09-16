package ca.jrvs.apps.stockquote.dao;

import java.util.InputMismatchException;
import java.util.Optional;
import java.util.Scanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StockQuoteController {
  final Logger infoLogger = LoggerFactory.getLogger("infoLogger");
  final Logger errorLogger = LoggerFactory.getLogger("errorLogger");

  private QuoteService quoteService;
  private PositionService positionService;

  public StockQuoteController(QuoteService quoteService, PositionService positionService) {
    this.quoteService = quoteService;
    this.positionService = positionService;
  }

  /**
   * User interface for our application
   */
  public void initClient() {
    Scanner scanner = new Scanner(System.in);
    int choice = 0;

    while (true) {
      System.out.println();
      displayMenu();
      try {
        if (scanner.hasNextInt()) {
          choice = scanner.nextInt();
          System.out.println();
          switch (choice) {
            case 1:
              infoLogger.info("StockQuoteController: View stock quote selected");
              handleViewStockQuote(scanner);
              break;
            case 2:
              infoLogger.info("StockQuoteController: Buy stock selected");
              handleBuyStock(scanner);
              break;
            case 3:
              infoLogger.info("StockQuoteController: Sell stock selected");
              handleSellStock(scanner);
              break;
            case 4:
              infoLogger.info("StockQuoteController: View positions selected");
              handleViewPositions();
              break;
            case 5:
              System.out.println("Exiting the program...");
              scanner.close();
              infoLogger.info("StockQuoteController: Application Exited");
              return;
            default:
              System.out.println("Invalid choice. Please enter a number between 1 and 5.");
          }
        }else{
          System.out.println("Invalid choice. Please enter a number between 1 and 5.");
        }
        scanner.nextLine();
      } catch (InputMismatchException e) {
        errorLogger.error("StockQuoteController: Invalid input {}", e.getMessage());
        System.out.println("Invalid input. Please enter a valid integer.");
        scanner.nextLine(); // Clear the invalid input
      }
    }
  }

  private void displayMenu() {
    infoLogger.info("Stock Quote App Menu");
    System.out.println("1. View stock quote");
    System.out.println("2. Buy stock");
    System.out.println("3. Sell stock");
    System.out.println("4. View positions");
    System.out.println("5. Exit");
    System.out.print("Please type the corresponding number to select an option: ");
  }

  private void handleViewStockQuote(Scanner scanner) {
    System.out.println("Enter stock symbol you want to view: ");
    String ticker = scanner.next().toUpperCase();
    try {
      Optional<Quote> quoteOptional = quoteService.fetchQuoteDataFromAPI(ticker);
      quoteOptional.ifPresent(quote -> System.out.println(quote.toString()));
    }catch (IllegalArgumentException e){
      System.out.println("You entered non existing stock quote ticker");
    }
  }

  private void handleBuyStock(Scanner scanner) {
    System.out.println("Enter stock symbol you want to buy: ");
    String ticker = scanner.next().toUpperCase();
    try{
      Optional<Quote> quoteOptional = quoteService.fetchQuoteDataFromAPI(ticker);
      if (quoteOptional.isPresent()) {
        double price = quoteOptional.get().getPrice();
        System.out.println("The price for one share of " + ticker + " is " + price);
        System.out.println("How many shares would you like to purchase? ");
        int num = scanner.nextInt();
        System.out.println(
            "Your order for " + num + " shares of " + ticker + " costs " + price * num);
        positionService.buy(ticker.toUpperCase(), num, price);
        System.out.println("Thank you for your purchase!");
      }
    } catch (IllegalArgumentException e) {
      System.out.println("You entered non existing stock quote ticker or incorrect shares number");
    }
  }

  private void handleSellStock(Scanner scanner) {
    System.out.println("Enter stock symbol you want to sell: ");
    String ticker = scanner.next().toUpperCase();
    try{
      positionService.sell(ticker);
      System.out.println("You have successfully sold all your shares of " + ticker);
    } catch (IllegalArgumentException e) {
      System.out.println("You do not have any positions on stock " + ticker);
    }
  }

  private void handleViewPositions() {
    System.out.println("Showing all your positions: ");
    Iterable<Position> positions = positionService.view();
    if (!positions.iterator().hasNext()) {
      System.out.println("You have no positions.");
      return;
    }
    positions.forEach(System.out::println);
  }
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.inventorysystemjava;

/**
 *
 * @author Jilianne
 */
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class InventorySystemJava {
    static class Stock {
        String dateEntered, stockLabel, brand, engineNumber, status;

        Stock(String dateEntered, String stockLabel, String brand, String engineNumber, String status) {
            this.dateEntered = dateEntered;
            this.stockLabel = stockLabel;
            this.brand = brand;
            this.engineNumber = engineNumber;
            this.status = status;
        }
    }

    private List<Stock> stockList = new ArrayList<>();

    public void readCSV(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length < 5) continue;
                stockList.add(new Stock(values[0], values[1], values[2], values[3], values[4]));
            }
        } catch (IOException e) {
            System.err.println("Error reading CSV file: " + e.getMessage());
        }
    }

    public void writeCSV(String filename) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            for (Stock stock : stockList) {
                bw.write(stock.dateEntered + "," + stock.stockLabel + "," + stock.brand + "," + stock.engineNumber + "," + stock.status);
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error writing to CSV file: " + e.getMessage());
        }
    }

    public void addStock(String brand, String engineNumber) {
        for (Stock stock : stockList) {
            if (stock.engineNumber.equals(engineNumber)) {
                System.out.println("Error: Engine Number already exists.");
                return;
            }
        }
        String dateEntered = LocalDate.now().format(DateTimeFormatter.ofPattern("M/d/yyyy"));
        stockList.add(new Stock(dateEntered, "New", brand, engineNumber, "On-hand"));
        writeCSV("MotorPH_Inventory.csv");
        System.out.println("Stock added successfully.");
    }

    public void deleteStock(String engineNumber) {
        Iterator<Stock> iterator = stockList.iterator();
        while (iterator.hasNext()) {
            Stock stock = iterator.next();
            if (stock.engineNumber.equals(engineNumber)) {
                if (stock.stockLabel.equals("Old") || stock.status.equals("Sold")) {
                    System.out.println("Error: Cannot delete 'Old' or 'Sold' stock.");
                    return;
                }
                iterator.remove();
                writeCSV("MotorPH_Inventory.csv");
                System.out.println("Stock deleted successfully.");
                return;
            }
        }
        System.out.println("Error: Stock not found.");
    }

    public void searchStock(String engineNumber) {
        for (Stock stock : stockList) {
            if (stock.engineNumber.equals(engineNumber)) {
                System.out.println("Stock Found: " + stock.dateEntered + ", " + stock.stockLabel + ", " + stock.brand + ", " + stock.engineNumber + ", " + stock.status);
                return;
            }
        }
        System.out.println("No items found.");
    }

    public void sortStockByBrand() {
        stockList.sort(Comparator.comparing(stock -> stock.brand));
        System.out.println("Stock sorted by brand:");
        for (Stock stock : stockList) {
            System.out.println(stock.dateEntered + ", " + stock.stockLabel + ", " + stock.brand + ", " + stock.engineNumber + ", " + stock.status);
        }
    }

    public static void main(String[] args) {
        InventorySystemJava inventory = new InventorySystemJava();
        inventory.readCSV("MotorPH_Inventory.csv");

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\nMotorPH Stock Management System");
            System.out.println("1. Add Stock");
            System.out.println("2. Delete Stock");
            System.out.println("3. Search Stock");
            System.out.println("4. Sort Stock by Brand");
            System.out.println("5. Exit");
            System.out.print("Enter your choice: ");
            
            int choice = scanner.nextInt();
            scanner.nextLine();
            
            switch (choice) {
                case 1:
                    System.out.print("Enter Brand: ");
                    String brand = scanner.nextLine();
                    System.out.print("Enter Engine Number: ");
                    String engineNumber = scanner.nextLine();
                    inventory.addStock(brand, engineNumber);
                    break;
                case 2:
                    System.out.print("Enter Engine Number to delete: ");
                    String deleteEngine = scanner.nextLine();
                    inventory.deleteStock(deleteEngine);
                    break;
                case 3:
                    System.out.print("Enter Engine Number to search: ");
                    String searchEngine = scanner.nextLine();
                    inventory.searchStock(searchEngine);
                    break;
                case 4:
                    inventory.sortStockByBrand();
                    break;
                case 5:
                    System.out.println("Exiting system...");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
}

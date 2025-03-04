/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.inventorysystem.java;

/**
 *
 * @author Jilianne
 */
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class invStockBST2 {
    static class treeNode {
        String date, stockLabel, brand, engineNumber, status;
        int key;
        treeNode left, right;

        treeNode(String date, String stockLabel, String brand, String engineNumber, String status, int key) {
            this.date = date;
            this.stockLabel = stockLabel;
            this.brand = brand;
            this.engineNumber = engineNumber;
            this.status = status;
            this.key = key;
            this.left = this.right = null;
        }
    }

    private treeNode root;

    public int convertKey(String engineNumber) {
        return engineNumber.hashCode();
    }

    public void loadFromCSV(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            int lineCount = 0;

            while ((line = br.readLine()) != null) {
                lineCount++;
                if (lineCount <= 2) continue;
                String[] values = line.split(",");
                if (values.length < 5) continue;

                addItemCSV(values[0].trim(), values[1].trim(), values[2].trim(), values[3].trim(), values[4].trim());
            }
        } catch (IOException e) {
            System.err.println("Error reading CSV file: " + e.getMessage());
        }
    }

    public void addItemCSV(String date, String stockLabel, String brand, String engineNumber, String status) {
        if (searchEngineNumber(root, engineNumber) != null) return;
        int key = convertKey(engineNumber);
        treeNode newNode = new treeNode(date, stockLabel, brand, engineNumber, status, key);
        if (root == null) root = newNode;
        else addNode(root, newNode);
    }

    public void addItem(String brand, String engineNumber) {
        if (searchEngineNumber(root, engineNumber) != null) {
            System.out.println("Error: Engine Number already exists.");
            return;
        }
        
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("M/d/yyyy"));
        String stockLabel = "New";
        String status = "On-hand";
        
        int key = convertKey(engineNumber);
        treeNode newNode = new treeNode(date, stockLabel, brand, engineNumber, status, key);
        
        if (root == null) root = newNode;
        else addNode(root, newNode);
        
        System.out.println("Stock added successfully.");
    }

    private treeNode searchEngineNumber(treeNode node, String engineNumber) {
        if (node == null) return null;
        if (node.engineNumber.equals(engineNumber)) return node;
        return searchEngineNumber(node.key > convertKey(engineNumber) ? node.left : node.right, engineNumber);
    }

    public void deleteItem(String engineNumber) {
        treeNode node = searchEngineNumber(root, engineNumber);
        if (node == null) {
            System.out.println("Error: Stock not found.");
            return;
        }
        
        if (node.stockLabel.equals("Old") || node.status.equals("Sold")) {
            System.out.println("Error: Cannot delete 'Old' or 'Sold' stock.");
            return;
        }
        
        root = deleteRec(root, convertKey(engineNumber));
        System.out.println("Stock deleted successfully.");
    }

    private treeNode deleteRec(treeNode root, int key) {
        if (root == null) return null;
        
        if (key < root.key) root.left = deleteRec(root.left, key);
        else if (key > root.key) root.right = deleteRec(root.right, key);
        else {
            if (root.left == null) return root.right;
            if (root.right == null) return root.left;
            
            treeNode minNode = findMin(root.right);
            root.date = minNode.date;
            root.stockLabel = minNode.stockLabel;
            root.brand = minNode.brand;
            root.engineNumber = minNode.engineNumber;
            root.status = minNode.status;
            root.key = minNode.key;
            root.right = deleteRec(root.right, minNode.key);
        }
        return root;
    }

    private treeNode findMin(treeNode node) {
        while (node.left != null) node = node.left;
        return node;
    }
    
    public void displayInventory() {
        System.out.println("Current Inventory:");
        displayOrdered(root);
    }

    private void displayOrdered(treeNode node) {
        if (node != null) {
            displayOrdered(node.left);
            System.out.println(node.date + ", " + node.stockLabel + ", " + node.brand + ", " + node.engineNumber + ", " + node.status);
            displayOrdered(node.right);
        }
    }

    public void writeToCSV(String filename) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            writeInventoryToCSV(bw, root);
        } catch (IOException e) {
            System.err.println("Error writing to CSV file: " + e.getMessage());
        }
    }

    private void writeInventoryToCSV(BufferedWriter bw, treeNode node) throws IOException {
        if (node != null) {
            writeInventoryToCSV(bw, node.left);
            bw.write(node.date + "," + node.stockLabel + "," + node.brand + "," + node.engineNumber + "," + node.status);
            bw.newLine();
            writeInventoryToCSV(bw, node.right);
        }
    }
}

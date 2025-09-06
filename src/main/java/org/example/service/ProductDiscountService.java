package org.example.service;

import org.example.model.DiscountResponse;
import org.example.model.Product;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductDiscountService {

    public DiscountResponse applyDiscounts(List<Product> products) {
        // ✅ Validate input
        products.forEach(p -> {
            if (p.getPrice() <= 0 || p.getQuantity() <= 0) {
                throw new IllegalArgumentException("Price and Quantity must be > 0");
            }
        });

        // ✅ Aggregate totals by category using streams
        double electronicsTotalPrice = products.stream()
                .filter(p -> p.getCategory().equalsIgnoreCase("electronics"))
                .mapToDouble(p -> p.getPrice() * p.getQuantity())
                .sum();

        int electronicsTotalQuantity = products.stream()
                .filter(p -> p.getCategory().equalsIgnoreCase("electronics"))
                .mapToInt(Product::getQuantity)
                .sum();

        double clothingTotalPrice = products.stream()
                .filter(p -> p.getCategory().equalsIgnoreCase("clothing"))
                .mapToDouble(p -> p.getPrice() * p.getQuantity())
                .sum();

        int clothingTotalQuantity = products.stream()
                .filter(p -> p.getCategory().equalsIgnoreCase("clothing"))
                .mapToInt(Product::getQuantity)
                .sum();

        double groceryTotalPrice = products.stream()
                .filter(p -> p.getCategory().equalsIgnoreCase("grocery"))
                .mapToDouble(p -> p.getPrice() * p.getQuantity())
                .sum();

        int groceryTotalQuantity = products.stream()
                .filter(p -> p.getCategory().equalsIgnoreCase("grocery"))
                .mapToInt(Product::getQuantity)
                .sum();

        double otherTotalPrice = products.stream()
                .filter(p -> {
                    String cat = p.getCategory().toLowerCase();
                    return !cat.equals("electronics") && !cat.equals("clothing") && !cat.equals("grocery");
                })
                .mapToDouble(p -> p.getPrice() * p.getQuantity())
                .sum();

        // ✅ Calculate discounts
        double totalSaving = 0.0;

        // Electronics: 10% discount if total price >= 20,000
        if (electronicsTotalPrice >= 20000) {
            totalSaving += electronicsTotalPrice * 0.10;
        }

        // Clothing logic
        int freeItems = clothingTotalQuantity / 3;
        if (freeItems > 0 && clothingTotalQuantity > 0) {
            double avgClothingPrice = clothingTotalPrice / clothingTotalQuantity;
            totalSaving += freeItems * avgClothingPrice;
        }

        // Grocery: 5% discount if quantity >= 10
        if (groceryTotalQuantity >= 10) {
            totalSaving += groceryTotalPrice * 0.05;
        }

        // ✅ Final Bill Calculation
        double totalBillWithoutDiscount = electronicsTotalPrice + clothingTotalPrice + groceryTotalPrice + otherTotalPrice;
        double finalBill = totalBillWithoutDiscount - totalSaving;

        return new DiscountResponse(products, totalSaving, finalBill);
    }

}



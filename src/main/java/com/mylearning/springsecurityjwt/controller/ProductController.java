package com.mylearning.springsecurityjwt.controller;

import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    //boilerplate for the product class with customization checks for null
    private record Product(Integer productId, String productName, double productPrice) {
        public Product {
            if (productId == null || productName == null) {
                throw new IllegalArgumentException("productName and productPrice cannot be null");
            }
        }
    }

    //List.of(...):
    //Creates an immutable list — you cannot add, remove, or modify elements.
    //If you try to modify the list, it throws an UnsupportedOperationException.

    //new ArrayList<>(...):
    //Creates a mutable copy of the immutable list.
    //Since it's wrapped in an ArrayList, you can add, remove, or update elements.

    // Modifying the list
    //        products.add(new Product(4, "Notebook", 10.0));
    //        products.remove(0);

    List<Product> products = new ArrayList<>(
            List.of(new Product(1, "Pen", 5.0),
                    new Product(2, "Pencil", 2.0),
                    new Product(3, "Eraser", 1.0))
    );

    @GetMapping
    public List<Product> getProducts() {
        return products;
    }

    @PostMapping
    public Product addProduct(@RequestBody  Product product) {
        products.add(product);
        System.out.println("Product added successfully");
        return product;
    }

}

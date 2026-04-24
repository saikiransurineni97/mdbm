package com.project.mdbm.controller;

import com.project.mdbm.dto.GenericAPIResponse;
import com.project.mdbm.dto.ProductRequest;
import com.project.mdbm.dto.ProductResponse;
import com.project.mdbm.service.ProductService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping("/save")
    public ResponseEntity<GenericAPIResponse> saveProduct(
            @Valid @RequestBody ProductRequest product,
            HttpSession session) {
        return ResponseEntity.ok(productService.saveProduct(product, session));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<GenericAPIResponse> updateProduct(
            @PathVariable String id,
            @Valid @RequestBody ProductRequest product,
            HttpSession session) {
        return ResponseEntity.ok(productService.updateProduct(id, product, session));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<GenericAPIResponse> deleteProduct(
            @PathVariable String id,
            HttpSession session) {
        return ResponseEntity.ok(productService.deleteProduct(id, session));
    }

    @GetMapping("/all")
    public ResponseEntity<List<ProductResponse>> getAllProducts(HttpSession session) {
        return ResponseEntity.ok(productService.getAllProducts(session));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(
            @PathVariable String id,
            HttpSession session) {
        return ResponseEntity.ok(productService.getProductById(id, session));
    }
}
package com.project.mdbm.service;

import com.project.mdbm.config.DataSourceContextHolder;
import com.project.mdbm.config.DynamicMongoManager;
import com.project.mdbm.dto.GenericAPIResponse;
import com.project.mdbm.dto.ProductRequest;
import com.project.mdbm.dto.ProductResponse;
import com.project.mdbm.entity.ProductJPA;
import com.project.mdbm.entity.ProductMongo;
import com.project.mdbm.mapper.ProductMapper;
import com.project.mdbm.repository.ProductRepository;
import com.project.mdbm.utils.ResponseUtils;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private static final String SESSION_CONNECTION_NAME = "connectionName";
    private static final String SESSION_DB_TYPE = "dbType";
    private static final String NON_RELATIONAL = "Non-Relational";

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final DynamicMongoManager dynamicMongoManager;

    public GenericAPIResponse saveProduct(ProductRequest request, HttpSession session) {
        GenericAPIResponse response = ResponseUtils.getResponseObject();

        String connectionName = (String) session.getAttribute(SESSION_CONNECTION_NAME);
        String dbType = (String) session.getAttribute(SESSION_DB_TYPE);

        if (!StringUtils.hasText(connectionName) || !StringUtils.hasText(dbType)) {
            response.setCode(400);
            response.setMessage("No database connected. Please call /api/db/connect first");
            return response;
        }

        try {
            if (NON_RELATIONAL.equalsIgnoreCase(dbType)) {
                saveProductToMongo(request, connectionName);
            } else {
                saveProductToJpa(request, connectionName);
            }

            response.setCode(200);
            response.setMessage("Product saved successfully in " + connectionName);
            return response;

        } catch (DataIntegrityViolationException e) {
            response.setCode(409);
            response.setMessage("Product with this product code already exists");
            return response;

        } catch (Exception e) {
            response.setCode(500);
            response.setMessage("Failed to save product: " + e.getMessage());
            return response;

        } finally {
            DataSourceContextHolder.clear();
        }
    }

    public GenericAPIResponse updateProduct(String id, ProductRequest request, HttpSession session) {
        GenericAPIResponse response = ResponseUtils.getResponseObject();

        try {
            String connectionName = (String) session.getAttribute(SESSION_CONNECTION_NAME);
            String dbType = (String) session.getAttribute(SESSION_DB_TYPE);

            if (!StringUtils.hasText(connectionName) || !StringUtils.hasText(dbType)) {
                response.setCode(400);
                response.setMessage("No database connected. Please call /api/db/connect first");
                return response;
            }

            if (NON_RELATIONAL.equalsIgnoreCase(dbType)) {
                MongoTemplate mongoTemplate = dynamicMongoManager.getMongoTemplate(connectionName);
                ProductMongo existingProduct = mongoTemplate.findById(id, ProductMongo.class, "product");

                if (existingProduct == null) {
                    response.setCode(404);
                    response.setMessage("Product not found with id: " + id);
                    return response;
                }

                existingProduct.setProductCode(request.getProductCode());
                existingProduct.setProductName(request.getProductName());
                existingProduct.setCategory(request.getCategory());
                existingProduct.setDepartment(request.getDepartment());
                existingProduct.setPrice(request.getPrice());

                mongoTemplate.save(existingProduct, "product");

            } else {
                DataSourceContextHolder.setDataSourceKey(connectionName);

                Long productId = Long.valueOf(id);
                Optional<ProductJPA> optionalProduct = productRepository.findById(productId);

                if (optionalProduct.isEmpty()) {
                    response.setCode(404);
                    response.setMessage("Product not found with id: " + id);
                    return response;
                }

                ProductJPA existingProduct = optionalProduct.get();
                existingProduct.setProductCode(request.getProductCode());
                existingProduct.setProductName(request.getProductName());
                existingProduct.setCategory(request.getCategory());
                existingProduct.setDepartment(request.getDepartment());
                existingProduct.setPrice(request.getPrice());

                productRepository.save(existingProduct);
            }

            response.setCode(200);
            response.setMessage("Product updated successfully in " + connectionName);
            return response;

        } catch (NumberFormatException e) {
            response.setCode(400);
            response.setMessage("Invalid product id: " + id);
            return response;

        } catch (DataIntegrityViolationException e) {
            response.setCode(409);
            response.setMessage("Product with this product code already exists");
            return response;

        } catch (Exception e) {
            response.setCode(500);
            response.setMessage("Failed to update product: " + e.getMessage());
            return response;

        } finally {
            DataSourceContextHolder.clear();
        }
    }

    public GenericAPIResponse deleteProduct(String id, HttpSession session) {
        GenericAPIResponse response = ResponseUtils.getResponseObject();

        try {
            String connectionName = (String) session.getAttribute(SESSION_CONNECTION_NAME);
            String dbType = (String) session.getAttribute(SESSION_DB_TYPE);

            if (!StringUtils.hasText(connectionName) || !StringUtils.hasText(dbType)) {
                response.setCode(400);
                response.setMessage("No database connected. Please call /api/db/connect first");
                return response;
            }

            if (NON_RELATIONAL.equalsIgnoreCase(dbType)) {
                MongoTemplate mongoTemplate = dynamicMongoManager.getMongoTemplate(connectionName);
                ProductMongo existingProduct = mongoTemplate.findById(id, ProductMongo.class, "product");

                if (existingProduct == null) {
                    response.setCode(404);
                    response.setMessage("Product not found with id: " + id);
                    return response;
                }

                mongoTemplate.remove(existingProduct, "product");
            } else {
                DataSourceContextHolder.setDataSourceKey(connectionName);

                Long productId = Long.valueOf(id);
                if (!productRepository.existsById(productId)) {
                    response.setCode(404);
                    response.setMessage("Product not found with id: " + id);
                    return response;
                }

                productRepository.deleteById(productId);
            }

            response.setCode(200);
            response.setMessage("Product deleted successfully from " + connectionName);
            return response;

        } catch (NumberFormatException e) {
            response.setCode(400);
            response.setMessage("Invalid product id: " + id);
            return response;

        } catch (Exception e) {
            response.setCode(500);
            response.setMessage("Failed to delete product: " + e.getMessage());
            return response;

        } finally {
            DataSourceContextHolder.clear();
        }
    }

    public List<ProductResponse> getAllProducts(HttpSession session) {
        String connectionName = (String) session.getAttribute(SESSION_CONNECTION_NAME);
        String dbType = (String) session.getAttribute(SESSION_DB_TYPE);

        try {
            if (!StringUtils.hasText(connectionName) || !StringUtils.hasText(dbType)) {
                return Collections.emptyList();
            }

            if (NON_RELATIONAL.equalsIgnoreCase(dbType)) {
                MongoTemplate mongoTemplate = dynamicMongoManager.getMongoTemplate(connectionName);
                List<ProductMongo> products = mongoTemplate.findAll(ProductMongo.class, "product");

                return products.stream()
                        .map(productMapper::toResponse)
                        .toList();
            }

            DataSourceContextHolder.setDataSourceKey(connectionName);
            List<ProductJPA> products = productRepository.findAll();

            return products.stream()
                    .map(productMapper::toResponse)
                    .toList();

        } finally {
            DataSourceContextHolder.clear();
        }
    }

    public ProductResponse getProductById(String id, HttpSession session) {
        String connectionName = (String) session.getAttribute(SESSION_CONNECTION_NAME);
        String dbType = (String) session.getAttribute(SESSION_DB_TYPE);

        try {
            if (!StringUtils.hasText(connectionName) || !StringUtils.hasText(dbType)) {
                return new ProductResponse();
            }

            if (NON_RELATIONAL.equalsIgnoreCase(dbType)) {
                MongoTemplate mongoTemplate = dynamicMongoManager.getMongoTemplate(connectionName);
                ProductMongo product = mongoTemplate.findById(id, ProductMongo.class, "product");
                return product != null ? productMapper.toResponse(product) : new ProductResponse();
            }

            DataSourceContextHolder.setDataSourceKey(connectionName);
            Long productId = Long.valueOf(id);

            return productRepository.findById(productId)
                    .map(productMapper::toResponse)
                    .orElse(new ProductResponse());

        } catch (NumberFormatException e) {
            return new ProductResponse();

        } finally {
            DataSourceContextHolder.clear();
        }
    }

    private void saveProductToMongo(ProductRequest request, String connectionName) {
        MongoTemplate mongoTemplate = dynamicMongoManager.getMongoTemplate(connectionName);
        ProductMongo productMongo = productMapper.toMongo(request);
        mongoTemplate.save(productMongo, "product");
    }

    private void saveProductToJpa(ProductRequest request, String connectionName) {
        DataSourceContextHolder.setDataSourceKey(connectionName);
        ProductJPA productJPA = productMapper.toJpa(request);
        productRepository.save(productJPA);
    }
}
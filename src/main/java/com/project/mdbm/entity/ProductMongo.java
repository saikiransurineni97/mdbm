package com.project.mdbm.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "product")
public class ProductMongo {

    @Id
    private String id;

    @Indexed(unique = true)
    private String productCode;

    private String productName;
    private String category;
    private String department;
    private Long price;
}

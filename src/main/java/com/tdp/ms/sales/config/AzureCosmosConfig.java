package com.tdp.ms.sales.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.spring.data.cosmosdb.CosmosDbFactory;
import com.microsoft.azure.spring.data.cosmosdb.config.CosmosDBConfig;
import com.microsoft.azure.spring.data.cosmosdb.core.ReactiveCosmosTemplate;
import com.microsoft.azure.spring.data.cosmosdb.core.convert.MappingCosmosConverter;
import com.microsoft.azure.spring.data.cosmosdb.core.convert.ObjectMapperFactory;
import com.microsoft.azure.spring.data.cosmosdb.core.mapping.CosmosMappingContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AzureCosmosConfig {

    @Value("${azure.cosmosdb.uri}")
    private String uri;
    @Value("${azure.cosmosdb.key}")
    private String key;
    @Value("${azure.cosmosdb.database}")
    private String database;

    public CosmosDbFactory cosmosDbFactory() {
        return new CosmosDbFactory(
                CosmosDBConfig.defaultBuilder()
                        .uri(uri).key(key)
                        .database(database)
                        .build()
        );
    }

    public MappingCosmosConverter mappingCosmosConverter() {
        CosmosMappingContext cosmosMappingContext = new CosmosMappingContext();
        ObjectMapper objectMapper = ObjectMapperFactory.getObjectMapper();
        return new MappingCosmosConverter(cosmosMappingContext, objectMapper);
    }

    @Bean
    public ReactiveCosmosTemplate reactiveCosmosTemplate() {
        return new ReactiveCosmosTemplate(cosmosDbFactory(), mappingCosmosConverter(), database);
    }

}

package com.jfecm.bankaccountmanagement.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * This class is a configuration class for the ModelMapper bean. It defines a Spring bean
 * for creating and configuring a ModelMapper instance, which can be used for mapping data
 * between different Java objects.
 *
 * <p>The ModelMapper is a powerful library that simplifies the process of mapping data
 * between objects with different structures, making it easier to work with DTOs (Data
 * Transfer Objects) and entities in Spring applications.
 */
@Component
public class ModelMapperConfig {

    /**
     * This method defines a Spring bean for creating and configuring a ModelMapper instance.
     *
     * @return A configured ModelMapper instance that can be used for object mapping.
     */
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

}

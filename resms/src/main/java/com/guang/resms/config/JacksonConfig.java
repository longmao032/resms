package com.guang.resms.config;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Configuration
public class JacksonConfig {
    
    @Bean
    public Jackson2ObjectMapperBuilder jackson2ObjectMapperBuilder() {
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();

        // 配置 Java Time API 的序列化和反序列化格式
        JavaTimeModule javaTimeModule = new JavaTimeModule();

        // 定义日期时间格式
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // LocalDateTime 序列化器和反序列化器
        javaTimeModule.addSerializer(LocalDateTime.class,
            new LocalDateTimeSerializer(dateTimeFormatter));
        javaTimeModule.addDeserializer(LocalDateTime.class,
            new LocalDateTimeDeserializer(dateTimeFormatter));

        // LocalDate 序列化器和反序列化器
        javaTimeModule.addSerializer(LocalDate.class,
            new LocalDateSerializer(dateFormatter));
        javaTimeModule.addDeserializer(LocalDate.class,
            new LocalDateDeserializer(dateFormatter));

        builder.modules(javaTimeModule);

        return builder;
    }
}
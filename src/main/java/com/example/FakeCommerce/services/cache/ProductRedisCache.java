package com.example.FakeCommerce.services.cache;

import java.time.Duration;
import java.util.Optional;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.example.FakeCommerce.dtos.GetProductResponseDto;
//import com.fasterxml.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service 
@RequiredArgsConstructor 
@Slf4j 
public class ProductRedisCache {
   private static final Duration CACHE_TTL = Duration.ofMinutes(1);
   private static final String KEY_SUMMARY = "product:summary:";
    private final StringRedisTemplate stringRedisTemplate;
   private final ObjectMapper objectMapper;

   public Optional<GetProductResponseDto> getSummary(Long id){
        String responseJson = stringRedisTemplate.opsForValue().get(KEY_SUMMARY + id);
      
        if(responseJson == null){
            log.info("Cache miss for product id {}", id);
            return Optional.empty();//cache miss    
        } 

        //cache hit
        
        try {
            GetProductResponseDto response = objectMapper.readValue(responseJson, GetProductResponseDto.class);
            log.info("cache hit for product id {}",id);
            return Optional.of(response);
        } catch (Exception e) {
            // TODO: handle exception
            log.error("Error parsing product summary from cache {}",e.getMessage());
           return  Optional.empty();
        }
   }

   public void putSummary(Long id, GetProductResponseDto response){
        try {
            stringRedisTemplate.opsForValue().set(KEY_SUMMARY + id,objectMapper.writeValueAsString(response),CACHE_TTL);

        } catch (Exception e) {
            // TODO: handle exception
            throw new RuntimeException("Error serailiazing key value to cache "+e.getMessage());

        }
   }
}

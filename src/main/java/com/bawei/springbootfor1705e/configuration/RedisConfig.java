package com.bawei.springbootfor1705e.configuration;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;

import java.nio.charset.StandardCharsets;

@Configuration
public class RedisConfig {

    @Value("${spring.redis.hostName}")
    private String hostName;
    @Value("${spring.redis.port}")
    private Integer port;
    @Value("${spring.redis.password}")
    private String password;
    @Value("${spring.redis.testOnBorrow}")
    private Boolean testOnBorrow;
    @Value("${spring.redis.maxIdle}")
    private Integer maxIdle;
    @Value("${spring.redis.maxTotal}")
    private Integer maxTotal;
    @Value("${spring.redis.maxWaitMillis}")
    private Long maxWaitMillis;
    @Value("${spring.redis.testWhileIdle}")
    private Boolean testWhileIdle;
    @Value("${spring.redis.minEvictableIdleTimeMillis}")
    private Long minEvictableIdleTimeMillis;
    @Value("${spring.redis.numTestsPerEvictionRun}")
    private Integer numTestsPerEvictionRun;
    @Value("${spring.redis.timeBetweenEvictionRunsMillis}")
    private Long timeBetweenEvictionRunsMillis;

    @Bean
    public JedisClientConfiguration jedisClientConfiguration() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        //取出值之前要检查
        jedisPoolConfig.setTestOnBorrow(true);
        //最大空闲线程数
        jedisPoolConfig.setMaxIdle(maxIdle);
        //最大连接数
        jedisPoolConfig.setMaxTotal(maxTotal);
        //连接redis最大等待时间
        jedisPoolConfig.setMaxWaitMillis(maxWaitMillis);
        //连接池空闲时，进行redis有效性检查
        jedisPoolConfig.setTestWhileIdle(testWhileIdle);
        //检查redis连接的最小空闲时间
        jedisPoolConfig.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
        //运行检查的线程数，如果不启用检查，为-1
        jedisPoolConfig.setNumTestsPerEvictionRun(numTestsPerEvictionRun);
        //运行检查线程启动的时间间隔，默认-1
        jedisPoolConfig.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);

        JedisClientConfiguration.JedisPoolingClientConfigurationBuilder builder = (JedisClientConfiguration.JedisPoolingClientConfigurationBuilder)JedisClientConfiguration.builder();

        JedisClientConfiguration jcf = builder.poolConfig(jedisPoolConfig).build();
        return jcf;
    }

    @Bean
    public JedisConnectionFactory redisConnectionFactory(JedisClientConfiguration jedisClientConfiguration) {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(hostName);
        config.setPort(port);
        config.setPassword(password);
        return new JedisConnectionFactory(config,jedisClientConfiguration);
    }

    @Bean
    public RedisTemplate<String,Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        //设置v的序列化
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(objectMapper);
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);

        //设置key的序列化
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer(StandardCharsets.UTF_8);
        redisTemplate.setKeySerializer(stringRedisSerializer);
        redisTemplate.setHashKeySerializer(stringRedisSerializer);

        redisTemplate.afterPropertiesSet();

        return redisTemplate;

    }
}

package com.lujieni.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.scripting.support.ResourceScriptSource;

/**
 * 位置redis多数据源(主从)
 */
@Configuration
public class RedisConfig {

    /**
     * 配置lettuce连接池参数
     *
     * @return
     */
    @Bean
    @ConfigurationProperties(prefix = "spring.redis.lettuce.pool")
    public GenericObjectPoolConfig redisPool() {
        return new GenericObjectPoolConfig<>();
    }

    /**
     * 配置第一个数据源的连接配置
     *
     * @return
     */
    @Bean
    @ConfigurationProperties(prefix = "spring.redis")
    public RedisStandaloneConfiguration redisMasterConfig() {
        return new RedisStandaloneConfiguration();
    }

    /**
     * 配置第二个数据源的连接配置
     *
     * @return
     */
    @Bean
    @ConfigurationProperties(prefix = "spring.redis2")
    public RedisStandaloneConfiguration redisSlaveConfig() {
        return new RedisStandaloneConfiguration();
    }


    /**
     *
     * @param config  连接池配置
     * @param redisMasterConfig  连接配置
     * @return
     */
    @Bean
    @Primary
    public LettuceConnectionFactory masterFactory(GenericObjectPoolConfig config, RedisStandaloneConfiguration redisMasterConfig) {
        LettuceClientConfiguration clientConfiguration = LettucePoolingClientConfiguration.builder().poolConfig(config).build();
        return new LettuceConnectionFactory(redisMasterConfig, clientConfiguration);
    }

    @Bean
    public LettuceConnectionFactory slaveFactory(GenericObjectPoolConfig config, RedisStandaloneConfiguration redisSlaveConfig) {
        LettuceClientConfiguration clientConfiguration = LettucePoolingClientConfiguration.builder().poolConfig(config).build();
        return new LettuceConnectionFactory(redisSlaveConfig, clientConfiguration);
    }


    /**
     * 自定义RedisTemplate类,不使用默认的RedisTemplate,
     * 注意这里方法名一定要是redisTemplate或者使用@Bean
     * 来指定("redisTemplate"),这样才会覆盖默认的配置;
     *
     * 使用@Primary来指定默认情况下即使用@Autowried标签
     * 来装配时使用哪一个bean
     *
     * spring默认会按照类名+参数名去寻找符合条件的bean,
     * 但当没有符合条件的bean的时候会按照类型去寻找
     *
     * redisTemplate 默认序列化使用的jdkSerializeable,
     * 存储二进制字节码, 所以自定义序列化类
     *
     * 原本使用入参名来区别同一个类的不同实例是可行的,
     * 但是在redisAutoConfiguration中还配置了stringRedisTemplate,
     * 它没有按照入参名来区分,所以我们只能使用@Primary注解LettuceConnectionFactory
     * @param
     * @return
     */
    @Bean("redisTemplate")
    public RedisTemplate<Object, Object> redisMasterTemplate(RedisConnectionFactory masterFactory) {
        RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(masterFactory);

        Jackson2JsonRedisSerializer <Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(objectMapper);

        redisTemplate.setEnableTransactionSupport(true);

        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.afterPropertiesSet();

        return redisTemplate;
    }


    @Bean
    public RedisTemplate<Object, Object> redisSlaveTemplate(RedisConnectionFactory slaveFactory) {
        RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(slaveFactory);

        Jackson2JsonRedisSerializer <Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(objectMapper);

        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.afterPropertiesSet();

        return redisTemplate;
    }

    /**
     * springboot调用redis脚本配置
     * @return
     */
    @Bean
    public DefaultRedisScript<Long> redisScript() {
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("syn.lua")));
        redisScript.setResultType(Long.class);//返回值类型
        return redisScript;
    }
}

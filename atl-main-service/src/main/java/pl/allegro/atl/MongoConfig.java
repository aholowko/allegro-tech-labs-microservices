package pl.allegro.atl;

import com.mongodb.MongoClientOptions;
import com.mongodb.management.JMXConnectionPoolListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MongoConfig {

    @Bean
    public MongoClientOptions mongoClientOptions() {
        return MongoClientOptions.builder()
                .alwaysUseMBeans(true)
                .connectTimeout(20)
//                .socketTimeout(200)
                .socketTimeout(30_000)  // #5.1
                .addConnectionPoolListener(new JMXConnectionPoolListener())
                .build();
    }
}

package pl.allegro.atl;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import pl.allegro.atl.infrastructure.metrics.ThreadPoolMetrics;

@SpringBootApplication
@EnableMongoRepositories
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableAsync(proxyTargetClass = true)
public class AtlServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AtlServiceApplication.class, args);
    }

    @Bean
    public TimedAspect timedAspect(MeterRegistry registry) {
        return new TimedAspect(registry);
    }

    @Bean
    public Executor myThreadPool(MeterRegistry registry) {
        final ArrayBlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(1000);
        final ThreadPoolExecutor executor = new ThreadPoolExecutor(
                20, 20,
                1, TimeUnit.MINUTES,
                queue
        );
        ThreadPoolMetrics.createGauges(registry, "myThreadPool", executor, queue);
        return executor;
    }

}

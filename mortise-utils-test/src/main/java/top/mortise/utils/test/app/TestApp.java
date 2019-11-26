package top.mortise.utils.test.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ImportResource;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.Resource;
import java.math.BigInteger;

@ImportResource("classpath:config/*.xml")
@SpringBootApplication
@EnableScheduling
public class TestApp extends SpringBootServletInitializer {
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(TestApp.class);

    }
    public static void main(String[] args) {
        SpringApplication.run(TestApp.class,args);


    }
}

package com.aryak.springai;

import com.aryak.springai.rag.HrPolicyLoader;
import com.aryak.springai.rag.RandomDataLoader;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SpringAiApplication {

    static void main(String[] args) {
        SpringApplication.run(SpringAiApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(RandomDataLoader dataLoader) {
        return args -> {
            // do something at startup
            //dataLoader.loadDataInVectorStore();
        };
    }

    @Bean
    public CommandLineRunner commandLineRunner1(HrPolicyLoader hrPolicyLoader) {
        return args -> {
            // do something at startup
            hrPolicyLoader.loadHrPolicyPdfDataInVectorStore();
        };
    }

}

package com.yodigi.quiplash;

import com.yodigi.quiplash.utils.RepoUtil;
import com.yodigi.quiplash.utils.GeneralUtil;
import com.yodigi.quiplash.utils.RetrieveQuestionsUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:application.properties")
public class SpringConfig {

    @Value("${max_contenders}")
    private Integer maxContenders;

    @Bean
    public RepoUtil gameRepoUtil() {
        return new RepoUtil();
    }

    @Bean
    public RetrieveQuestionsUtil retrieveQuestionsUtil() {
        return new RetrieveQuestionsUtil();
    }

    @Bean
    public GeneralUtil generalUtil() {
        return new GeneralUtil();
    }

}

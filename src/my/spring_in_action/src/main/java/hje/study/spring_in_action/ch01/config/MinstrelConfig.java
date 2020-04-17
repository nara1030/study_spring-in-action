package hje.study.spring_in_action.ch01.config;

import hje.study.spring_in_action.ch01.logging.Minstrel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableAspectJAutoProxy
public class MinstrelConfig {
    @Bean(name = "minstrel")
    public Minstrel minstrel() {
        return new Minstrel(System.out);
    }
}

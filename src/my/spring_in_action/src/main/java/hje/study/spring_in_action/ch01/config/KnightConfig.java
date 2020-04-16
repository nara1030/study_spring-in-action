package hje.study.spring_in_action.ch01.config;

import hje.study.spring_in_action.ch01.knight.BraveKnight;
import hje.study.spring_in_action.ch01.knight.Knight;
import hje.study.spring_in_action.ch01.quest.Quest;
import hje.study.spring_in_action.ch01.quest.SlayDragonQuest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KnightConfig {
    @Bean
    public Knight knight() {
        return new BraveKnight(quest());
    }

    @Bean
    public Quest quest() {
        return new SlayDragonQuest(System.out);
    }
}

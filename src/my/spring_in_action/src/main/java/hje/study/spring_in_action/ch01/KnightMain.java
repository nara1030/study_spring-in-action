package hje.study.spring_in_action.ch01;

import hje.study.spring_in_action.ch01.config.KnightConfig;
import hje.study.spring_in_action.ch01.knight.Knight;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class KnightMain {
    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(KnightConfig.class);
        Knight knight = context.getBean(Knight.class);
        knight.embarkOnQuest();
    }
}

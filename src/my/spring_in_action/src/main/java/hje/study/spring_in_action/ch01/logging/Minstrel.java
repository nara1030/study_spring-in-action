package hje.study.spring_in_action.ch01.logging;

import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

import java.io.PrintStream;

@Aspect
public class Minstrel {
    private PrintStream stream;

    public Minstrel(PrintStream stream) {
        this.stream = stream;
    }

    @Pointcut("execution(* *.embarkOnQuest(..))")
    public void onPointCut() {
    }

    @Before("onPointCut()")
    public void singBeforeQuest() {
        stream.println("Fa la la, the knight is so brave!");
    }

    @After("onPointCut()")
    public void singAfterQuest() {
        stream.println("Tee hee hee, the brave knight " + "did embark  on a quest");
    }
}

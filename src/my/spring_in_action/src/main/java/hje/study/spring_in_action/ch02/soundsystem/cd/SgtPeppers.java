package hje.study.spring_in_action.ch02.soundsystem.cd;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SgtPeppers implements CompactDisc {
    private static final Logger LOGGER = LoggerFactory.getLogger(SgtPeppers.class);

    private String title = "Sgt. Pepper's Lonely Hearts Club Band";
    private String artist = "The Beatles";

    @Override
    public void play() {
        LOGGER.info("Playing {} by {}", title, artist);
    }
}

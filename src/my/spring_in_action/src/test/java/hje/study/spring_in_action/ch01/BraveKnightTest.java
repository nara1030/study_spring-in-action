package hje.study.spring_in_action.ch01;

import static org.mockito.Mockito.*;

import hje.study.spring_in_action.ch01.knight.BraveKnight;
import hje.study.spring_in_action.ch01.quest.Quest;
import org.junit.Test;

public class BraveKnightTest {
    @Test
    public void knightShouldEmbarkOnQuest() {
        Quest mockQuest = mock(Quest.class);    // 모의 Quest 생성
        BraveKnight knight = new BraveKnight(mockQuest);    // 모의 Quest 주입
        knight.embarkOnQuest();
        verify(mockQuest, times(1)).embark();
    }
}

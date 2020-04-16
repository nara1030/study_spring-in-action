package hje.study.spring_in_action.ch01.knight;

import hje.study.spring_in_action.ch01.quest.Quest;

public class BraveKnight implements Knight {
    private Quest quest;

    public BraveKnight(Quest quest) {  // Quest 주입
        this.quest = quest;
    }

    @Override
    public void embarkOnQuest() {
        quest.embark();
    }
}

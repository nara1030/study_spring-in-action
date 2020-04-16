package hje.study.spring_in_action.ch01.knight;

import hje.study.spring_in_action.ch01.quest.RescueDamselQuest;

public class DamselRescuingKnight implements Knight {
    private RescueDamselQuest quest;

    public DamselRescuingKnight() {
        this.quest = new RescueDamselQuest();   // RescueDamselQuest에 강하게 결합
    }

    @Override
    public void embarkOnQuest() {
        quest.embark();
    }
}

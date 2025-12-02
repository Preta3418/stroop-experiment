package com._rdTem.stroop_experiment;

import java.util.List;

public class ExperimentData {
    private String participantId;
    private List<Trial> trials;

    public String getParticipantId() {
        return participantId;
    }

    public void setParticipantId(String participantId) {
        this.participantId = participantId;
    }

    public List<Trial> getTrials() {
        return trials;
    }

    public void setTrials(List<Trial> trials) {
        this.trials = trials;
    }
}

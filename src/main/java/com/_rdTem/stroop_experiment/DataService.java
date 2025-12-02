package com._rdTem.stroop_experiment;

import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class DataService {

    private final Path path = Paths.get("data");

    public void saveExperiment(ExperimentData data) {
        String participantId = data.getParticipantId();
        List<Trial> trials = data.getTrials();

        StringBuilder csv = new StringBuilder();




    }

}

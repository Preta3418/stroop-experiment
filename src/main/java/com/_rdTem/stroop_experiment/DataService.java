package com._rdTem.stroop_experiment;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class DataService {

    private final Path dataDir = Paths.get("data");

    public void saveExperiment(String participantId, String csvContent) throws IOException {
        Path filePath = dataDir.resolve("stroop_" + participantId + ".csv");
        Files.write(filePath, csvContent.getBytes(StandardCharsets.UTF_8));
    }

    public List<Map<String, String>> getAllParticipants() throws IOException {
        if (!Files.exists(dataDir)) {
            return new ArrayList<>();
        }

        try (Stream<Path> paths = Files.list(dataDir)) {
            return paths
                    .filter(path -> path.toString().endsWith(".csv"))
                    .map(path -> {
                        Map<String, String> info = new HashMap<>();
                        String filename = path.getFileName().toString();
                        String id = filename.replace("stroop_", "").replace(".csv", "");
                        info.put("participantId", id);

                        try {
                            LocalDateTime time = LocalDateTime.ofInstant(
                                    Files.getLastModifiedTime(path).toInstant(),
                                    ZoneId.systemDefault()
                            );
                            info.put("savedAt", time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
                        } catch (IOException e) {
                            info.put("savedAt", "");
                        }
                        return info;
                    })
                    .collect(Collectors.toList());
        }
    }

    public Map<String, Object> getParticipant(String id) throws IOException {
        Path filePath = dataDir.resolve("stroop_" + id + ".csv");

        if (!Files.exists(filePath)) {
            return null;
        }

        List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
        List<Map<String, Object>> trials = new ArrayList<>();

        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.trim().isEmpty()) continue;

            String[] parts = line.split(",");
            if (parts.length >= 10) {
                Map<String, Object> trial = new HashMap<>();
                trial.put("phase", parts[1]);
                trial.put("trialNumber", Integer.parseInt(parts[2]));
                trial.put("word", parts[3]);
                trial.put("wordType", parts[4]);
                trial.put("color", parts[5]);
                trial.put("response", parts[6]);
                trial.put("correct", Boolean.parseBoolean(parts[7]));
                trial.put("rt", Long.parseLong(parts[8]));
                trials.add(trial);
            }
        }

        return Map.of("trials", trials);
    }

    public byte[] getFileContent(String id) throws IOException {
        Path filePath = dataDir.resolve("stroop_" + id + ".csv");
        if (!Files.exists(filePath)) {
            return null;
        }
        return Files.readAllBytes(filePath);
    }

    public void deleteParticipant(String id) throws IOException {
        Path filePath = dataDir.resolve("stroop_" + id + ".csv");
        Files.deleteIfExists(filePath);
    }

    public byte[] getMasterCsv() throws IOException {
        if (!Files.exists(dataDir)) {
            return null;
        }

        List<Path> csvFiles;
        try (Stream<Path> paths = Files.list(dataDir)) {
            csvFiles = paths
                    .filter(path -> path.toString().endsWith(".csv"))
                    .collect(Collectors.toList());
        }

        if (csvFiles.isEmpty()) {
            return null;
        }

        StringBuilder masterCsv = new StringBuilder();
        boolean headerAdded = false;

        for (Path file : csvFiles) {
            List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);

            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                if (line.trim().isEmpty()) continue;

                if (i == 0) {
                    if (!headerAdded) {
                        masterCsv.append(line).append("\n");
                        headerAdded = true;
                    }
                } else {
                    masterCsv.append(line).append("\n");
                }
            }
        }

        // Add BOM for Excel
        byte[] bom = new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};
        byte[] content = masterCsv.toString().getBytes(StandardCharsets.UTF_8);
        byte[] fileContent = new byte[bom.length + content.length];
        System.arraycopy(bom, 0, fileContent, 0, bom.length);
        System.arraycopy(content, 0, fileContent, bom.length, content.length);

        return fileContent;
    }
}

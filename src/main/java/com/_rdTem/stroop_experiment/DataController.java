package com._rdTem.stroop_experiment;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class DataController {

    private final DataService dataService;

    public DataController(DataService dataService) {
        this.dataService = dataService;
    }

    @PostMapping("/save")
    public ResponseEntity<?> save(@RequestParam String participantId, @RequestBody String csvContent) {
        try {
            dataService.saveExperiment(participantId, csvContent);
            return ResponseEntity.ok().build();
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("저장 실패");
        }
    }

    @GetMapping("/participants")
    public ResponseEntity<List<Map<String, String>>> getParticipants() {
        try {
            return ResponseEntity.ok(dataService.getAllParticipants());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/participant/{id}")
    public ResponseEntity<?> getParticipant(@PathVariable String id) {
        try {
            Map<String, Object> data = dataService.getParticipant(id);
            if (data == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(data);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/participant/{id}/download")
    public ResponseEntity<byte[]> download(@PathVariable String id) {
        try {
            byte[] content = dataService.getFileContent(id);
            if (content == null) {
                return ResponseEntity.notFound().build();
            }
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("text/csv; charset=UTF-8"));
            headers.setContentDispositionFormData("attachment", "stroop_data_" + id + ".csv");
            return new ResponseEntity<>(content, headers, HttpStatus.OK);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/participant/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        try {
            dataService.deleteParticipant(id);
            return ResponseEntity.ok().build();
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/master-csv")
    public ResponseEntity<byte[]> masterCsv() {
        try {
            byte[] content = dataService.getMasterCsv();
            if (content == null) {
                return ResponseEntity.notFound().build();
            }
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("text/csv; charset=UTF-8"));
            String filename = "stroop_master_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmm")) + ".csv";
            headers.setContentDispositionFormData("attachment", filename);
            return new ResponseEntity<>(content, headers, HttpStatus.OK);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

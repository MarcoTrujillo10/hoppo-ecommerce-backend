package com.example.HPPO_Backend.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.*;
import java.util.*;
import java.util.UUID;

@RestController
@RequestMapping("/uploads")
public class UploadController {

    private static final String UPLOAD_DIR = "uploads";
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB en bytes

    @PostMapping
    public ResponseEntity<Map<String, Object>> upload(@RequestParam("files") MultipartFile[] files) {
        try {
            Files.createDirectories(Paths.get(UPLOAD_DIR));

            List<String> urls = new ArrayList<>();
            List<String> errors = new ArrayList<>();
            
            for (MultipartFile file : files) {
                if (file.isEmpty()) continue;

                // Validar tamaño del archivo (10MB máximo)
                if (file.getSize() > MAX_FILE_SIZE) {
                    errors.add("El archivo " + file.getOriginalFilename() + " excede el tamaño máximo de 10MB");
                    continue;
                }

                // Validar que sea una imagen
                String contentType = file.getContentType();
                if (contentType == null || !contentType.startsWith("image/")) {
                    errors.add("El archivo " + file.getOriginalFilename() + " no es una imagen válida");
                    continue;
                }

                String ext = Optional.ofNullable(file.getOriginalFilename())
                        .filter(fn -> fn.contains("."))
                        .map(fn -> fn.substring(fn.lastIndexOf(".")))
                        .orElse("");

                String filename = UUID.randomUUID().toString().replace("-", "") + ext;
                Path target = Paths.get(UPLOAD_DIR, filename);
                Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

                String publicUrl = "/uploads/" + filename;
                urls.add(publicUrl);
            }

            Map<String, Object> body = new HashMap<>();
            body.put("urls", urls);
            
            if (!errors.isEmpty()) {
                body.put("errors", errors);
                body.put("warning", "Algunos archivos no se pudieron subir");
            }
            
            if (urls.isEmpty() && !errors.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
            }
            
            return ResponseEntity.ok(body);

        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error subiendo archivos: " + ex.getMessage()));
        }
    }
}
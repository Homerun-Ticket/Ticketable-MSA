package com.example.modulegame.domain.stadium.controller;


import com.example.modulegame.domain.stadium.dto.request.SectionCreateRequest;
import com.example.modulegame.domain.stadium.dto.request.SectionUpdateRequest;
import com.example.modulegame.domain.stadium.dto.response.SectionCreateResponse;
import com.example.modulegame.domain.stadium.dto.response.SectionUpdateResponse;
import com.example.modulegame.domain.stadium.service.SectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class SectionController {
    private final SectionService sectionService;

    @PostMapping("/v1/admin/stadiums/{stadiumId}/sections")
    public ResponseEntity<SectionCreateResponse> createSection(
            @PathVariable Long stadiumId,
            @RequestBody SectionCreateRequest request
    ) {
        return ResponseEntity.ok(sectionService.createSection(stadiumId, request));
    }

    @PutMapping("/v1/admin/sections/{sectionId}")
    public ResponseEntity<SectionUpdateResponse> updateSection(
            @PathVariable Long sectionId,
            @RequestBody SectionUpdateRequest request
    ) {
        return ResponseEntity.ok(sectionService.updateSection(sectionId, request));
    }

    @DeleteMapping("/v1/admin/sections/{sectionId}")
    public ResponseEntity<Void> deleteSection(
            @PathVariable Long sectionId
    ) {
        sectionService.delete(sectionId);
        return ResponseEntity.ok().build();
    }
}

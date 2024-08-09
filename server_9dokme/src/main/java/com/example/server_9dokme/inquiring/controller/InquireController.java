package com.example.server_9dokme.inquiring.controller;

import com.example.server_9dokme.inquiring.dto.request.InquireRequestDto;
import com.example.server_9dokme.inquiring.entity.Inquire;
import com.example.server_9dokme.inquiring.service.InquireService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api")
public class InquireController {
    @Autowired
    private InquireService inquireService;

    @PostMapping
    public ResponseEntity<Inquire> createInquire(@RequestBody InquireRequestDto inquireRequest) {
        Inquire createdInquire = inquireService.createInquire(inquireRequest);
        return new ResponseEntity<>(createdInquire, HttpStatus.CREATED);
    }
}

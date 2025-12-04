package org.example.koudynamicpricingbackend.contollers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.koudynamicpricingbackend.requests.AddSpecialDayRequest;
import org.example.koudynamicpricingbackend.responses.AddSpecialDayResponse;
import org.example.koudynamicpricingbackend.responses.SpecialDayResponse;
import org.example.koudynamicpricingbackend.services.SpecialDayService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/special-days")
@RequiredArgsConstructor
public class AdminSpecialDayController {

    private final SpecialDayService specialDayService;

    @PostMapping("/add")
    public ResponseEntity<AddSpecialDayResponse> addSpecialDay(@Valid @RequestBody AddSpecialDayRequest request) {

        AddSpecialDayResponse response = specialDayService.addSpecialDay(request);

        return ResponseEntity
                .status(HttpStatus.CREATED) // 201 Created
                .body(response);
    }


    @GetMapping
    public ResponseEntity<List<SpecialDayResponse>> getAllSpecialDays() {

        return ResponseEntity.ok(specialDayService.getAllSpecialDays());
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSpecialDay(@PathVariable Long id) {
        specialDayService.deleteSpecialDay(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }



}
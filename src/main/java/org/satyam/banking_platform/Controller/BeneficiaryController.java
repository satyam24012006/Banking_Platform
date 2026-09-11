package org.satyam.banking_platform.Controller;

import org.satyam.banking_platform.DTO.AddBeneficiaryRequest;
import org.satyam.banking_platform.DTO.BeneficiaryResponse;
import org.satyam.banking_platform.Service.BeneficiaryService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/beneficiaries")
public class BeneficiaryController {

    private final BeneficiaryService beneficiaryService;

    public BeneficiaryController(
            BeneficiaryService beneficiaryService) {

        this.beneficiaryService = beneficiaryService;
    }


    // Add Beneficiary
    @PostMapping("/{userId}")
    public ResponseEntity<BeneficiaryResponse> addBeneficiary(
            @PathVariable Long userId,
            @Valid @RequestBody AddBeneficiaryRequest request) {

        BeneficiaryResponse response =
                beneficiaryService.addBeneficiary(request, userId);

        return ResponseEntity.ok(response);
    }


    // Get Beneficiary By ID
    @GetMapping("/{id}")
    public ResponseEntity<BeneficiaryResponse> getBeneficiaryById(
            @PathVariable Long id) {

        BeneficiaryResponse response =
                beneficiaryService.getBeneficiaryById(id);

        return ResponseEntity.ok(response);
    }


    // Get All Beneficiaries
    @GetMapping
    public ResponseEntity<List<BeneficiaryResponse>>
    getAllBeneficiaries() {

        return ResponseEntity.ok(
                beneficiaryService.getAllBeneficiaries()
        );
    }


    // Delete Beneficiary
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBeneficiary(
            @PathVariable Long id) {

        beneficiaryService.deleteBeneficiary(id);

        return ResponseEntity.noContent().build();
    }
}
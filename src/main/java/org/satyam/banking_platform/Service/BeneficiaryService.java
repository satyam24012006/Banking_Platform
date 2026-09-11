package org.satyam.banking_platform.Service;

import org.satyam.banking_platform.DTO.AddBeneficiaryRequest;
import org.satyam.banking_platform.DTO.BeneficiaryResponse;
import org.satyam.banking_platform.Model.Beneficiary;
import org.satyam.banking_platform.Model.User;
import org.satyam.banking_platform.Repository.BeneficiaryRepository;
import org.satyam.banking_platform.Repository.UserRepository;
import org.satyam.banking_platform.Exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BeneficiaryService {

    private final BeneficiaryRepository beneficiaryRepository;
    private final UserRepository userRepository;

    public BeneficiaryService(
            BeneficiaryRepository beneficiaryRepository,
            UserRepository userRepository) {

        this.beneficiaryRepository = beneficiaryRepository;
        this.userRepository = userRepository;
    }


    // Add Beneficiary
    public BeneficiaryResponse addBeneficiary(
            AddBeneficiaryRequest request,
            Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        Beneficiary beneficiary = new Beneficiary();

        beneficiary.setName(request.getName());
        beneficiary.setAccountNumber(request.getAccountNumber());
        beneficiary.setBankName(request.getBankName());
        beneficiary.setUser(user);

        Beneficiary savedBeneficiary =
                beneficiaryRepository.save(beneficiary);

        return convertToResponse(savedBeneficiary);
    }


    // Get Beneficiary By ID
    public BeneficiaryResponse getBeneficiaryById(Long id) {

        Beneficiary beneficiary = beneficiaryRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Beneficiary not found"));

        return convertToResponse(beneficiary);
    }


    // Get All Beneficiaries
    public List<BeneficiaryResponse> getAllBeneficiaries() {

        return beneficiaryRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }


    // Delete Beneficiary
    public void deleteBeneficiary(Long id) {

        Beneficiary beneficiary = beneficiaryRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Beneficiary not found"));

        beneficiaryRepository.delete(beneficiary);
    }


    // Convert Entity → Response
    private BeneficiaryResponse convertToResponse(
            Beneficiary beneficiary) {

        BeneficiaryResponse response =
                new BeneficiaryResponse();

        response.setId(beneficiary.getId());
        response.setName(beneficiary.getName());
        response.setAccountNumber(
                beneficiary.getAccountNumber());
        response.setBankName(
                beneficiary.getBankName());

        return response;
    }
}
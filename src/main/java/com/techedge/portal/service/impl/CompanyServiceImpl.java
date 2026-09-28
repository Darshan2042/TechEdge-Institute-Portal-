package com.techedge.portal.service.impl;

import com.techedge.portal.dto.request.CompanyRequest;
import com.techedge.portal.dto.response.CompanyResponse;
import com.techedge.portal.entity.Company;
import com.techedge.portal.exception.BusinessRuleException;
import com.techedge.portal.repository.CompanyRepository;
import com.techedge.portal.service.CompanyService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;

    public CompanyServiceImpl(
            CompanyRepository companyRepository
    ) {
        this.companyRepository = companyRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompanyResponse> getCompanies() {

        return companyRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public CompanyResponse createCompany(
            CompanyRequest request
    ) {

        if (companyRepository.existsByName(request.name())) {
            throw new BusinessRuleException(
                    "Company with this name already exists"
            );
        }

        Company company = new Company();

        company.setName(request.name());
        company.setIndustry(request.industry());
        company.setWebsite(request.website());
        company.setLogoUrl(request.logoUrl());

        Company savedCompany =
                companyRepository.save(company);

        return toResponse(savedCompany);
    }

    private CompanyResponse toResponse(
            Company company
    ) {
        return new CompanyResponse(
                company.getId(),
                company.getName(),
                company.getIndustry(),
                company.getWebsite(),
                company.getLogoUrl()
        );
    }
}
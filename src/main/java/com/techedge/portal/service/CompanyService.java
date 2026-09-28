package com.techedge.portal.service;

import com.techedge.portal.dto.request.CompanyRequest;
import com.techedge.portal.dto.response.CompanyResponse;

import java.util.List;

public interface CompanyService {

    List<CompanyResponse> getCompanies();

    CompanyResponse createCompany(
            CompanyRequest request
    );
}
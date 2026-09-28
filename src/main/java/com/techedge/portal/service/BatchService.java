package com.techedge.portal.service;

import com.techedge.portal.dto.request.BatchRequest;
import com.techedge.portal.dto.response.BatchResponse;
import com.techedge.portal.entity.enums.BatchMode;
import com.techedge.portal.entity.enums.BatchStatus;

import java.util.List;

public interface BatchService {

    List<BatchResponse> getBatches(
            BatchStatus status,
            BatchMode mode
    );

    List<BatchResponse> getBatchesByCourse(Long courseId);

    BatchResponse createBatch(BatchRequest request);

    BatchResponse updateBatch(
            Long batchId,
            BatchRequest request
    );
}
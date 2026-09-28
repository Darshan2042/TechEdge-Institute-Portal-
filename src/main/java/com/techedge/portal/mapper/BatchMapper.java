package com.techedge.portal.mapper;

import com.techedge.portal.dto.request.BatchRequest;
import com.techedge.portal.dto.response.BatchResponse;
import com.techedge.portal.entity.Batch;

public class BatchMapper {

    private BatchMapper() {
    }

    public static Batch toEntity(BatchRequest request) {
        Batch batch = new Batch();

        batch.setBatchCode(request.batchCode());
        batch.setStartDate(request.startDate());
        batch.setEndDate(request.endDate());
        batch.setTiming(request.timing());
        batch.setMode(request.mode());
        batch.setTrainerName(request.trainerName());
        batch.setTotalSeats(request.totalSeats());
        batch.setAvailableSeats(request.availableSeats());
        batch.setStatus(request.status());

        return batch;
    }

    public static void updateEntity(Batch batch, BatchRequest request) {
        batch.setBatchCode(request.batchCode());
        batch.setStartDate(request.startDate());
        batch.setEndDate(request.endDate());
        batch.setTiming(request.timing());
        batch.setMode(request.mode());
        batch.setTrainerName(request.trainerName());
        batch.setTotalSeats(request.totalSeats());
        batch.setAvailableSeats(request.availableSeats());
        batch.setStatus(request.status());
    }

    public static BatchResponse toResponse(Batch batch) {
        return new BatchResponse(
                batch.getId(),
                batch.getBatchCode(),
                batch.getStartDate(),
                batch.getEndDate(),
                batch.getTiming(),
                batch.getMode(),
                batch.getTrainerName(),
                batch.getAvailableSeats(),
                batch.getTotalSeats(),
                batch.getStatus()
        );
    }
}
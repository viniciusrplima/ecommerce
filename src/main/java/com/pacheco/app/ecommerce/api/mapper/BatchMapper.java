package com.pacheco.app.ecommerce.api.mapper;

import com.pacheco.app.ecommerce.api.model.input.BatchInput;
import com.pacheco.app.ecommerce.api.model.output.BatchLiteModel;
import com.pacheco.app.ecommerce.api.model.output.BatchModel;
import com.pacheco.app.ecommerce.domain.model.Batch;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class BatchMapper {

    @Autowired
    private ModelMapper modelMapper;

    public BatchModel toRepresentation(Batch batch) {
        return modelMapper.map(batch, BatchModel.class);
    }

    public BatchLiteModel toLite(Batch batch) {
        return modelMapper.map(batch,  BatchLiteModel.class);
    }

    public List<BatchLiteModel> toLiteList(List<Batch> batches) {
        return batches.stream()
                .map(batch -> toLite(batch))
                .collect(Collectors.toList());
    }

    public Batch toModel(BatchInput batchInput) {
        return modelMapper.map(batchInput, Batch.class);
    }
}

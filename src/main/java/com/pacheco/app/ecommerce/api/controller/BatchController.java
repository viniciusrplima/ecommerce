package com.pacheco.app.ecommerce.api.controller;

import com.pacheco.app.ecommerce.api.mapper.BatchMapper;
import com.pacheco.app.ecommerce.api.model.input.BatchInput;
import com.pacheco.app.ecommerce.api.model.output.BatchLiteModel;
import com.pacheco.app.ecommerce.api.model.output.BatchModel;
import com.pacheco.app.ecommerce.domain.repository.BatchRepository;
import com.pacheco.app.ecommerce.domain.service.BatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(Routes.BATCHES)
public class BatchController {

    @Autowired private BatchMapper batchMapper;
    @Autowired private BatchRepository batchRepository;
    @Autowired private BatchService batchService;

    @GetMapping
    private List<BatchLiteModel> getBatches() {
        return batchMapper.toLiteList(batchRepository.findAll());
    }

    @GetMapping("/{batchId}")
    private BatchModel getBatch(@PathVariable Long batchId) {
        return batchMapper.toRepresentation(batchService.findById(batchId));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    private BatchModel saveBatch(@RequestBody @Valid BatchInput batchInput) {
        return batchMapper.toRepresentation(
                batchService.saveBatch(batchMapper.toModel(batchInput)));
    }

}

package io.everyonecodes.cryptolog.service;

import io.everyonecodes.cryptolog.repository.AssetsAllocationRepository;
import org.springframework.stereotype.Service;

@Service
public class AssetsAllocationService {

    private final AssetsAllocationRepository assetsAllocationRepository;

    public AssetsAllocationService(AssetsAllocationRepository assetsAllocationRepository) {
        this.assetsAllocationRepository = assetsAllocationRepository;
    }

}

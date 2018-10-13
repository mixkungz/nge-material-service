package com.sit.cloudnative.MaterialService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MaterialService {
    @Autowired
    private MaterialRepository materialRepository;

    public Material addMaterial(Material material) {
        return materialRepository.save(material);
    }
}

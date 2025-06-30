package com.dudoji.spring.service;

import com.dudoji.spring.models.dao.PinSkinDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PinSkinService {
    @Autowired
    private PinSkinDao pinSkinDao;


}

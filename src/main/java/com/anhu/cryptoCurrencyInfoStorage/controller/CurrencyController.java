package com.anhu.cryptoCurrencyInfoStorage.controller;

import com.anhu.cryptoCurrencyInfoStorage.repository.CurrencyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class CurrencyController {

    @Autowired
    CurrencyRepository currencyRepository;

}

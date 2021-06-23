package com.anhu.cryptoCurrencyInfoStorage.repository;

import com.anhu.cryptoCurrencyInfoStorage.model.Currency;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CurrencyRepository  extends JpaRepository<Currency, Long> {
}

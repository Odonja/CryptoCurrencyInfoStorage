package com.anhu.cryptoCurrencyInfoStorage.repository;

import com.anhu.cryptoCurrencyInfoStorage.model.Currency;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * The CurrencyRepository holds Currency information and is responsible for executing queries on this information.
 */
public interface CurrencyRepository extends JpaRepository<Currency, String> {
}

package br.com.joaomatos.hubfinanceiro;

import java.math.BigDecimal;
import java.util.Date;

public record Transaction(
        Integer type,
        Date date,
        BigDecimal value,
        Long cpf,
        String card,
        String hour,
        String storeOwner,
        String storeName) {

}

package br.com.joaomatos.hubfinanceiro;

import java.math.BigDecimal;

public record TransactionCNAB(
        Integer type,
        String date,
        BigDecimal value,
        Long cpf,
        String card,
        String hour,
        String storeOwner,
        String storeName) {

}

package com.coffee.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO per erogazione bevanda.
 * Campi: tipoBevanda (obbligatorio), zuccheri, conBiscotto (opzionali).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErogazioneRequest {

    private String tipoBevanda;
    private Integer zuccheri;
    private Boolean conBiscotto;
}

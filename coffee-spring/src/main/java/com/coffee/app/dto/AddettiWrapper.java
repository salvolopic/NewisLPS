package com.coffee.app.dto;

import com.coffee.app.entity.Addetto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Wrapper per XML - JAXB richiede un root element per serializzare liste.
 */
@XmlRootElement(name = "addetti")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddettiWrapper {

    @XmlElement(name = "addetto")
    private List<Addetto> addetti;
}

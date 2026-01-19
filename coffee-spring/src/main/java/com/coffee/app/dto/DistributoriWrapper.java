package com.coffee.app.dto;

import com.coffee.app.entity.Distributore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Wrapper per XML - JAXB richiede un root element per serializzare liste.
 */
@XmlRootElement(name = "distributori")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DistributoriWrapper {

    @XmlElement(name = "distributore")
    private List<Distributore> distributori;
}

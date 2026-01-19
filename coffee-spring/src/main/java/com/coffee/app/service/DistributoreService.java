package com.coffee.app.service;

import com.coffee.app.entity.Cliente;
import com.coffee.app.entity.Distributore;
import com.coffee.app.entity.StatoDistributore;
import com.coffee.app.repository.ClienteRepository;
import com.coffee.app.repository.DistributoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DistributoreService {

    private final DistributoreRepository distributoreRepository;
    private final ClienteRepository clienteRepository;


    public List<Distributore> findAll() {
        return distributoreRepository.findAll();
    }


    public Optional<Distributore> findById(String id) {
        return distributoreRepository.findById(id);
    }


    public List<Distributore> findAttivi() {
        return distributoreRepository.findByStato(StatoDistributore.ATTIVO);
    }


    public List<Distributore> findConLivelliBassi(Integer soglia) {
        return distributoreRepository.findByLivelliBassi(soglia);
    }


    public Distributore create(Distributore distributore) {
        if (distributoreRepository.existsById(distributore.getId())) {
            throw new RuntimeException("Distributore con ID " + distributore.getId() + " già esistente");
        }
        return distributoreRepository.save(distributore);
    }


    public Distributore update(Distributore distributore) {
        return distributoreRepository.save(distributore);
    }


    public void delete(String id) {
        distributoreRepository.deleteById(id);
    }

    public Distributore ripristinaLivelli(String id) {
        Distributore dist = distributoreRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Distributore non trovato: " + id));

        dist.setLivelloCaffe(100);
        dist.setLivelloLatte(100);
        dist.setLivelloZucchero(100);
        dist.setLivelloCioccolato(100);
        dist.setLivelloTe(100);
        dist.setLivelloBicchieri(200);
        dist.setLivelloBiscotti(150);
        dist.setUltimaManutenzione(LocalDateTime.now());

        return distributoreRepository.save(dist);
    }


    public Distributore cambiaStato(String id, StatoDistributore nuovoStato) {
        Distributore dist = distributoreRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Distributore non trovato: " + id));

        dist.setStato(nuovoStato);
        return distributoreRepository.save(dist);
    }


    public void decrementaLivelli(String id, String tipoBevanda, int zuccheri, boolean conBiscotto) {
        Distributore dist = distributoreRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Distributore non trovato: " + id));

        switch (tipoBevanda.toLowerCase()) {
            case "espresso":
                dist.setLivelloCaffe(dist.getLivelloCaffe() - 2);
                dist.setLivelloBicchieri(dist.getLivelloBicchieri() - 1);
                break;

            case "caffe_lungo":
            case "lungo":
                dist.setLivelloCaffe(dist.getLivelloCaffe() - 3);
                dist.setLivelloBicchieri(dist.getLivelloBicchieri() - 1);
                break;

            case "caffe_ristretto":
            case "ristretto":
                dist.setLivelloCaffe(dist.getLivelloCaffe() - 1);
                dist.setLivelloBicchieri(dist.getLivelloBicchieri() - 1);
                break;

            case "caffe_americano":
            case "americano":
                dist.setLivelloCaffe(dist.getLivelloCaffe() - 2);
                dist.setLivelloBicchieri(dist.getLivelloBicchieri() - 1);
                break;

            case "macchiato":
                dist.setLivelloCaffe(dist.getLivelloCaffe() - 2);
                dist.setLivelloLatte(dist.getLivelloLatte() - 1);
                dist.setLivelloBicchieri(dist.getLivelloBicchieri() - 1);
                break;

            case "doppio_espresso":
            case "doppio":
                dist.setLivelloCaffe(dist.getLivelloCaffe() - 4);
                dist.setLivelloBicchieri(dist.getLivelloBicchieri() - 1);
                break;

            case "cappuccino":
                dist.setLivelloCaffe(dist.getLivelloCaffe() - 2);
                dist.setLivelloLatte(dist.getLivelloLatte() - 5);
                dist.setLivelloBicchieri(dist.getLivelloBicchieri() - 1);
                break;

            case "latte_macchiato":
                dist.setLivelloCaffe(dist.getLivelloCaffe() - 1);
                dist.setLivelloLatte(dist.getLivelloLatte() - 7);
                dist.setLivelloBicchieri(dist.getLivelloBicchieri() - 1);
                break;

            case "caffe_latte":
                dist.setLivelloCaffe(dist.getLivelloCaffe() - 2);
                dist.setLivelloLatte(dist.getLivelloLatte() - 6);
                dist.setLivelloBicchieri(dist.getLivelloBicchieri() - 1);
                break;

            case "cioccolata":
            case "cioccolata_calda":
                dist.setLivelloCioccolato(dist.getLivelloCioccolato() - 5);
                dist.setLivelloLatte(dist.getLivelloLatte() - 3);
                dist.setLivelloBicchieri(dist.getLivelloBicchieri() - 1);
                break;

            case "mocaccino":
            case "mochaccino":
                dist.setLivelloCaffe(dist.getLivelloCaffe() - 2);
                dist.setLivelloCioccolato(dist.getLivelloCioccolato() - 3);
                dist.setLivelloLatte(dist.getLivelloLatte() - 4);
                dist.setLivelloBicchieri(dist.getLivelloBicchieri() - 1);
                break;

            case "te":
            case "te_nero":
                dist.setLivelloTe(dist.getLivelloTe() - 1);
                dist.setLivelloBicchieri(dist.getLivelloBicchieri() - 1);
                break;

            case "te_al_latte":
                dist.setLivelloTe(dist.getLivelloTe() - 1);
                dist.setLivelloLatte(dist.getLivelloLatte() - 3);
                dist.setLivelloBicchieri(dist.getLivelloBicchieri() - 1);
                break;

            case "te_verde":
                dist.setLivelloTe(dist.getLivelloTe() - 1);
                dist.setLivelloBicchieri(dist.getLivelloBicchieri() - 1);
                break;

            case "cappuccino_intense":
            case "cappuccino intense":
                dist.setLivelloCaffe(dist.getLivelloCaffe() - 3);
                dist.setLivelloLatte(dist.getLivelloLatte() - 5);
                dist.setLivelloBicchieri(dist.getLivelloBicchieri() - 1);
                break;

            case "coffee_barley":
            case "coffee barley":
                dist.setLivelloCaffe(dist.getLivelloCaffe() - 2);
                dist.setLivelloBicchieri(dist.getLivelloBicchieri() - 1);
                break;

            case "biscotti":
                dist.setLivelloBiscotti(dist.getLivelloBiscotti() - 2);
                break;

            default:
                throw new RuntimeException("Tipo bevanda non supportato: " + tipoBevanda);
        }

        if (zuccheri > 0) {
            dist.setLivelloZucchero(dist.getLivelloZucchero() - zuccheri);
        }

        if (conBiscotto) {
            dist.setLivelloBiscotti(dist.getLivelloBiscotti() - 2);
        }

        distributoreRepository.save(dist);
    }


    public void decrementaLivelli(String id, String tipoBevanda, int zuccheri) {
        decrementaLivelli(id, tipoBevanda, zuccheri, false);
    }

    @Transactional
    public Distributore erogaBevanda(
            String distributoreId,
            String tipoBevanda,
            int zuccheri,
            boolean conBiscotto) {

        Distributore distributore = distributoreRepository.findById(distributoreId)
            .orElseThrow(() -> new RuntimeException("Distributore non trovato: " + distributoreId));

        if (distributore.getStato() != StatoDistributore.ATTIVO) {
            throw new RuntimeException("Distributore non attivo. Stato: " + distributore.getStato());
        }

        Optional<Cliente> clienteOpt = clienteRepository.findByDistributoreConnessoId(distributoreId);
        if (clienteOpt.isEmpty()) {
            throw new RuntimeException("Nessun cliente connesso al distributore: " + distributoreId);
        }

        Cliente cliente = clienteOpt.get();

        BigDecimal prezzoBevanda = calcolaPrezzoBevanda(tipoBevanda, conBiscotto);

        if (cliente.getCredito().compareTo(prezzoBevanda) < 0) {
            throw new RuntimeException(
                String.format("Credito insufficiente. Richiesto: %.2f €, Disponibile: %.2f €",
                    prezzoBevanda.doubleValue(),
                    cliente.getCredito().doubleValue())
            );
        }

        BigDecimal nuovoCredito = cliente.getCredito().subtract(prezzoBevanda);
        cliente.setCredito(nuovoCredito);
        clienteRepository.save(cliente);

        decrementaLivelli(distributoreId, tipoBevanda, zuccheri, conBiscotto);

        return distributoreRepository.findById(distributoreId)
            .orElseThrow(() -> new RuntimeException("Errore nel recupero distributore"));
    }

    private BigDecimal calcolaPrezzoBevanda(String tipoBevanda, boolean conBiscotto) {
        BigDecimal prezzo;

        switch (tipoBevanda.toLowerCase()) {
            case "espresso":
            case "caffe_lungo":
            case "lungo":
            case "caffe_ristretto":
            case "ristretto":
            case "caffe_americano":
            case "americano":
                prezzo = new BigDecimal("1.00");
                break;

            case "macchiato":
                prezzo = new BigDecimal("1.20");
                break;

            case "doppio_espresso":
            case "doppio":
                prezzo = new BigDecimal("1.50");
                break;

            case "cappuccino":
                prezzo = new BigDecimal("1.50");
                break;

            case "caffe_latte":
                prezzo = new BigDecimal("1.60");
                break;

            case "latte_macchiato":
                prezzo = new BigDecimal("1.70");
                break;

            case "cioccolata":
            case "cioccolata_calda":
                prezzo = new BigDecimal("1.80");
                break;

            case "mocaccino":
            case "mochaccino":
                prezzo = new BigDecimal("2.00");
                break;

            case "te":
            case "te_nero":
            case "te_verde":
                prezzo = new BigDecimal("1.00");
                break;

            case "te_al_latte":
                prezzo = new BigDecimal("1.30");
                break;

            case "cappuccino_intense":
            case "cappuccino intense":
                prezzo = new BigDecimal("1.70");
                break;

            case "coffee_barley":
            case "coffee barley":
                prezzo = new BigDecimal("1.00");
                break;

            case "biscotti":
                prezzo = new BigDecimal("0.50");
                break;

            default:
                throw new RuntimeException("Tipo bevanda non supportato: " + tipoBevanda);
        }

        if (conBiscotto) {
            prezzo = prezzo.add(new BigDecimal("0.50"));
        }

        return prezzo;
    }
}

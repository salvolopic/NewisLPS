document.addEventListener("DOMContentLoaded", () => {
    caricaManutentoreIniziale();

    const btn = document.getElementById("btn-carica");
    if (btn) btn.addEventListener("click", carica);
});

function caricaManutentoreIniziale() {
    // Legge dati manutentore da localStorage (salvati dal login)
    const manutentoreId = localStorage.getItem('manutentoreId');

    if (!manutentoreId) {
        window.location.href = 'manutenzione-login.html';
        return;
    }

    // Chiama API per ottenere dati aggiornati del manutentore
    fetch(`http://localhost:8080/api/manutentori/${manutentoreId}`)
        .then(response => {
            if (response.ok) {
                return response.json();
            } else {
                throw new Error('Manutentore non trovato');
            }
        })
        .then(manutentore => {
            const nomeEl = document.getElementById('nome-manutentore');
            nomeEl.textContent = 'Addetto: ' + manutentore.nome + ' ' + manutentore.cognome;
        })
        .catch(err => {
            console.error('Errore nel caricamento dei dati manutentore:', err);
            // Fallback: usa dati da localStorage
            const nome = localStorage.getItem('manutentoreNome') || 'Manutentore';
            const nomeEl = document.getElementById('nome-manutentore');
            nomeEl.textContent = 'Addetto: ' + nome;
        });
}

function carica() {
    const idInput = document.getElementById("cercaId");
    const id = idInput.value.trim().toUpperCase();

    if (!id) {
        return;
    }

    fetch(`http://localhost:8080/api/distributori/${id}`)
        .then(response => {
            if (response.ok) {
                return response.json();
            } else if( response.status === 404) {
                throw new Error("Distributore non trovato");
            } else {
                throw new Error("Errore nel caricamento ");
            }
        })
        .then(distributore => {
            mostraStatoDistributore(distributore);
        })
        .catch(err => {
            console.error("Errore API: ", err);
            const ris = document.getElementById("risultato");
            ris.style.display = "none";
            ris.innerHTML = "";
        });
    }


//legge file json dello stato del distributore 

function mostraStatoDistributore(dist) {
    const risultato = document.getElementById("risultato");

    //dist è un oggetto json con le info del distributore
    const nome = dist.nome || "";
    const loc = dist.ubicazione || "";
    const stato = dist.stato || "ATTIVO";

    //Accedi ai livelli direttamente dall'oggetto dist
    const livelloCaffe = dist.livelloCaffe || 0;
    const livelloLatte = dist.livelloLatte || 0;
    const livelloZucchero = dist.livelloZucchero || 0;
    const livelloBicchieri = dist.livelloBicchieri || 0;

    //Controlla se ha guasti attivi 
    const haGuasti = dist.guasti && dist.guasti.length > 0 &&
                     dist.guasti.some(g => !g.risolto);


    let html = `
    <div class="status-card">

        <div style="display:flex; justify-content:space-between; margin-bottom:15px;">
            <div class="status-title">${nome}</div>
            <div class="machine-status ${stato === 'ATTIVO' ? 'active' : 'inactive'}">${stato === 'ATTIVO' ? 'Attivo' : 'Disattivo'}</div>
        </div>

        <div style="margin-bottom:15px;">
            <div style="font-size:14px; color:#888;">Ubicazione</div>
            <div style="font-size:16px;">${loc}</div>
        </div>

        <div style="font-size:16px; font-weight:600; margin-bottom:10px;">Livelli Forniture</div>

        <div class="status-info">
            ${livelloHtmlJSON("Caffè", livelloCaffe)}
            ${livelloHtmlJSON("Latte", livelloLatte)}
            ${livelloHtmlJSON("Zucchero", livelloZucchero)}
            ${livelloHtmlJSON("Bicchieri", livelloBicchieri)}
        </div>
    `;

   if (haGuasti) {
        html += `
        <div style="margin-top:20px; padding:15px; background:rgba(220,53,69,0.1); border-left:3px solid #dc3545;">
            <div style="font-weight:600; color:#ff6b7a;">🔴 Guasti rilevati</div>
            ${dist.guasti.filter(g => !g.risolto).map(g => `<div>${g.descrizione}</div>`).join("")}
        </div>`;
    } else {
        html += `
        <div style="margin-top:20px; padding:15px; background:rgba(0,204,127,0.1); border-left:3px solid #00cc7f;">
            <div style="font-weight:600; color:#00cc7f;">✓ Nessun guasto</div>
        </div>`;
    }

    html += `
        <div style="margin-top:20px; display:flex; gap:10px;">
            <button class="btn" onclick="ripristinaLivelli('${dist.id}')">Ripristina Livelli</button>
            <button class="btn btn-secondary" onclick="cambiaStato('${dist.id}', '${stato === 'ATTIVO' ? 'MANUTENZIONE' : 'ATTIVO'}')">${stato === 'ATTIVO' ? 'Metti in Manutenzione' : 'Attiva'}</button>
        </div>
    `;

    html += "</div>";

    risultato.innerHTML = html;
    risultato.style.display = "block";
}

function ripristinaLivelli(distributoreId) {
    if (!confirm(`Ripristinare tutti i livelli del distributore ${distributoreId}?`)) {
        return;
    }

    fetch(`http://localhost:8080/api/distributori/${distributoreId}/ripristina-livelli`, {
        method: 'POST'
    })
    .then(response => {
        if (response.ok) {
            return response.json();
        } else {
            throw new Error('Errore durante il ripristino');
        }
    })
    .then(distributore => {
        mostraStatoDistributore(distributore);
    })
    .catch(err => {
        console.error('Errore:', err);
    });
}

function cambiaStato(distributoreId, nuovoStato) {
    const statoLabel = nuovoStato === 'ATTIVO' ? 'attivo' : 'in manutenzione';

    if (!confirm(`Cambiare lo stato del distributore ${distributoreId} in ${statoLabel}?`)) {
        return;
    }

    fetch(`http://localhost:8080/api/distributori/${distributoreId}/stato/${nuovoStato}`, {
        method: 'PUT'
    })
    .then(response => {
        if (response.ok) {
            return response.json();
        } else {
            throw new Error('Errore durante il cambio stato');
        }
    })
    .then(distributore => {
        mostraStatoDistributore(distributore);
    })
    .catch(err => {
        console.error('Errore:', err);
    });
}

function livelloHtmlJSON(label, percentuale) {
    const p = parseInt(percentuale, 10);
    let colore = "#999";

    if(p >= 50) colore = "#00cc7f";       // Verde
    else if(p >= 20) colore = "#ffd700";
    else colore = "#ff6b7a";              // Rosso
    
    return `
        <div class="status-item">
            <div class="status-label">${label}</div>
            <div class="status-value" style="color: ${colore};">${percentuale}%</div>
        </div>`;
}

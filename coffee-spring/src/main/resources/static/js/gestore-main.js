let distributoriXML = [];

document.addEventListener("DOMContentLoaded", () => {
    caricaGestoreIniziale();

    const btnFiltra = document.getElementById("btn-filtra");
    const btnReset  = document.getElementById("btn-reset");

    if (btnFiltra) {
        btnFiltra.addEventListener("click", filtraPerId);
    }
    if (btnReset) {
        btnReset.addEventListener("click", mostraTutti);
    }

    caricaTutteLeMacchine();
});

function caricaGestoreIniziale() {
    // Legge dati gestore da localStorage (salvati dal login)
    const gestoreId = localStorage.getItem('gestoreId');

    if (!gestoreId) {
        window.location.href = 'gestore-login.html';
        return;
    }

    // Chiama API per ottenere dati aggiornati del gestore
    fetch(`http://localhost:8080/api/gestori/${gestoreId}`)
        .then(response => {
            if (response.ok) {
                return response.json();
            } else {
                throw new Error('Gestore non trovato');
            }
        })
        .then(gestore => {
            const nomeEl = document.getElementById('nome-gestore');
            nomeEl.textContent = gestore.ruolo + ': ' + gestore.nome + ' ' + gestore.cognome;
        })
        .catch(err => {
            console.error('Errore nel caricamento dei dati gestore:', err);
            // Fallback: usa dati da localStorage
            const nome = localStorage.getItem('gestoreNome') || 'Gestore';
            const ruolo = localStorage.getItem('gestoreRuolo') || 'Admin';
            const nomeEl = document.getElementById('nome-gestore');
            nomeEl.textContent = ruolo + ': ' + nome;
        });
}

function caricaTutteLeMacchine() {
    fetch("http://localhost:8080/api/distributori")
        .then(response => {
            if (response.ok) {
                return response.json();  // ← JSON invece di XML
            } else {
                throw new Error('Errore nel caricamento distributori');
            }
        })
        .then(distributoriArray => {
            // distributoriArray è un array di oggetti JSON
            distributoriXML = distributoriArray;  // Rinomina variabile se vuoi
            mostraLista(distributoriXML);
            setMessaggio("");
        })
        .catch(err => {
            console.error("Errore API gestore:", err);
            setMessaggio("Impossibile caricare lo stato dei distributori.");
        });
}

function mostraLista(listaNodi) {
    const container = document.getElementById("machine-list");
    container.innerHTML = "";

    if (!listaNodi || listaNodi.length === 0) {
        container.innerHTML = "<p style='color:#aaa;'>Nessun distributore trovato.</p>";
        return;
    }

    listaNodi.forEach(nodo => {
        const cardHtml = creaCardDaNodo(nodo);
        container.insertAdjacentHTML("beforeend", cardHtml);
    });

    inizializzaPulsantiAzione();
}

function filtraPerId() {
    const input = document.getElementById("filtro-id");
    const valore = input.value.trim().toUpperCase();

    if (!valore) {
        mostraTutti();
        return;
    }

    const filtrati = distributoriXML.filter(dist => {
        const id = dist.id || "";
        return id.toUpperCase() === valore;
    });

    if (filtrati.length === 0) {
        mostraLista([]);
        setMessaggio("Nessun distributore trovato con ID " + valore);
    } else {
        mostraLista(filtrati);
        setMessaggio("Mostro risultati per ID " + valore);
    }
}

function mostraTutti() {
    document.getElementById("filtro-id").value = "";
    mostraLista(distributoriXML);
    setMessaggio("");
}

function setMessaggio(testo) {
    const el = document.getElementById("messaggio-ricerca");
    if (!el) return;
    el.textContent = testo;
}

function creaCardDaNodo(dist) {
    const id   = dist.id || "";
    const nome = dist.nome || "";
    const ubic = dist.ubicazione || "";
    const stato = dist.stato || "ATTIVO";

    const caffe = dist.livelloCaffe || 0;
    const latte = dist.livelloLatte || 0;
    const bevande = "-";
    
    const haGuasto = dist.guasti && dist.guasti.length > 0 && dist.guasti.some (g => !g.risolto);

    const statoLabel = haGuasto ? "✗ Guasto" : "✓ OK";
    const statoColor = haGuasto ? "#ff6b7a" : "#00cc7f";

    const isAttivo = stato === "ATTIVO";

    return `
    <div class="machine-card" data-id="${id}" data-stato="${stato}" ${haGuasto ? 'style="border-left-color:#dc3545;"' : ""}>
        <div class="machine-header">
            <div class="machine-id">${id} - ${nome}</div>
            <div class="machine-status ${isAttivo ? "active" : "inactive"}">
                ${isAttivo ? "Attivo" : "Disattivo"}
            </div>
        </div>
        <div style="font-size: 14px; color: #999; margin-bottom: 10px;">${ubic}</div>

        <div class="status-info">
            <div class="status-item">
                <div class="status-label">Caffè</div>
                <div class="status-value" style="color: ${colorePercentuale(caffe)};">${caffe}%</div>
            </div>
            <div class="status-item">
                <div class="status-label">Latte</div>
                <div class="status-value" style="color: ${colorePercentuale(latte)};">${latte}%</div>
            </div>
            <div class="status-item">
                <div class="status-label">Bevande oggi</div>
                <div class="status-value">${bevande}</div>
            </div>
            <div class="status-item">
                <div class="status-label">Stato</div>
                <div class="status-value" style="color: ${statoColor};">${statoLabel}</div>
            </div>
        </div>

        <div class="machine-actions">
            <button class="btn btn-dettagli">Dettagli</button>
            ${isAttivo
                ? '<button class="btn btn-danger btn-disattiva">Disattiva</button>'
                : '<button class="btn btn-attiva">Attiva</button>'}
        </div>
    </div>`;
}

function colorePercentuale(val) {
    const p = parseInt(val, 10);
    if (isNaN(p)) return "#999";
    if (p < 20) return "#ff6b7a";
    if (p < 50) return "#ffd700";
    return "#00cc7f";
}

function inizializzaPulsantiAzione() {
    const btnsDettagli = document.querySelectorAll(".btn-dettagli");
    btnsDettagli.forEach(btn => {
        btn.addEventListener("click", () => {
            const card = btn.closest(".machine-card");
            const id = card?.dataset.id || "(sconosciuto)";
        });
    });

    const btnsDisattiva = document.querySelectorAll(".btn-disattiva");
    btnsDisattiva.forEach(btn => {
        btn.addEventListener("click", () => {
            const card = btn.closest(".machine-card");
            const id = card?.dataset.id || "(sconosciuto)";

            if (confirm("Vuoi disattivare il distributore " + id + "?")) {
                // Chiama API per disattivare nel database
                fetch(`http://localhost:8080/api/distributori/${id}/stato/DISATTIVO`, {
                    method: 'PUT'
                })
                .then(response => {
                    if (response.ok) {
                        return response.json();
                    } else {
                        throw new Error("Errore durante la disattivazione");
                    }
                })
                .then(distributore => {
                    // Aggiorna UI
                    card.dataset.stato = "DISATTIVO";
                    const statusBadge = card.querySelector(".machine-status");
                    statusBadge.classList.remove("active");
                    statusBadge.classList.add("inactive");
                    statusBadge.textContent = "Disattivo";

                    btn.outerHTML = '<button class="btn btn-attiva">Attiva</button>';
                    inizializzaPulsantiAzione();
                })
                .catch(err => {
                    console.error("Errore disattivazione:", err);
                });
            }
        });
    });

    const btnsAttiva = document.querySelectorAll(".btn-attiva");
    btnsAttiva.forEach(btn => {
        btn.addEventListener("click", () => {
            const card = btn.closest(".machine-card");
            const id = card?.dataset.id || "(sconosciuto)";

            if (confirm("Vuoi attivare il distributore " + id + "?")) {
                // Chiama API per attivare nel database
                fetch(`http://localhost:8080/api/distributori/${id}/stato/ATTIVO`, {
                    method: 'PUT'
                })
                .then(response => {
                    if (response.ok) {
                        return response.json();
                    } else {
                        throw new Error("Errore durante l'attivazione");
                    }
                })
                .then(distributore => {
                    // Aggiorna UI
                    card.dataset.stato = "ATTIVO";
                    const statusBadge = card.querySelector(".machine-status");
                    statusBadge.classList.remove("inactive");
                    statusBadge.classList.add("active");
                    statusBadge.textContent = "Attivo";

                    btn.outerHTML = '<button class="btn btn-danger btn-disattiva">Disattiva</button>';
                    inizializzaPulsantiAzione();
                })
                .catch(err => {
                    console.error("Errore attivazione:", err);
                });
            }
        });
    });
}

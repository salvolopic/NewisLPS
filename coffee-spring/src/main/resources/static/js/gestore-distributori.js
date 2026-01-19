document.addEventListener("DOMContentLoaded", () => {
    caricaDistributoriDaAPI();
    inizializzaFormAggiunta();
});

function caricaDistributoriDaAPI() {
    fetch("http://localhost:8080/api/distributori")
        .then(response => {
            if (response.ok) {
                return response.json();
            } else {
                throw new Error('Errore nel caricamento distributori');
            }
        })
        .then(distributoriArray => {
            const container = document.querySelector(".machine-list");
            if (!container) return;

            container.innerHTML = "";

            if (distributoriArray.length === 0) {
                container.innerHTML = "<p style='color:#aaa;text-align:center;padding:20px;'>Nessun distributore presente.</p>";
                return;
            }

            distributoriArray.forEach(dist => {
                const cardHtml = creaCardDistributoreJSON(dist);
                container.insertAdjacentHTML("beforeend", cardHtml);
            });

            inizializzaBottoniRimuovi();
        })
        .catch(err => {
            console.error("Errore caricamento distributori:", err);
            const container = document.querySelector(".machine-list");
            if (container) {
                container.innerHTML = "<p style='color:#ff6b7a;text-align:center;padding:20px;'>Errore nel caricamento dei distributori.</p>";
            }
        });
}

// Crea card da oggetto JSON (dal database)
function creaCardDistributoreJSON(dist) {
    const id = dist.id || "";
    const nome = dist.nome || "";
    const ubicazione = dist.ubicazione || "";
    const stato = dist.stato || "ATTIVO";

    const isAttivo = stato === "ATTIVO";

    return `
    <div class="machine-card ${isAttivo ? "" : "inactive"}" data-id="${id}">
        <div class="machine-header">
            <div class="machine-id">${id} - ${nome}</div>
            <div class="machine-status ${isAttivo ? "active" : "inactive"}">
                ${isAttivo ? "Attivo" : stato === "MANUTENZIONE" ? "Manutenzione" : "Disattivo"}
            </div>
        </div>
        <div style="font-size: 14px; color: #999; margin-bottom: 10px;">${ubicazione}</div>
        <div style="font-size: 14px; margin-bottom: 10px;">
            <span style="color: #999;">Caffè:</span> ${dist.livelloCaffe || 0}% |
            <span style="color: #999;">Latte:</span> ${dist.livelloLatte || 0}%
        </div>
        <div style="font-size: 14px; margin-bottom: 15px;">
            <span style="color: #999;">Guasti attivi:</span> ${dist.guasti && dist.guasti.length > 0 ? dist.guasti.filter(g => !g.risolto).length : 0}
        </div>

        <div class="machine-actions">
            <button class="btn btn-secondary btn-modifica">Modifica</button>
            <button class="btn btn-danger btn-rimuovi-distributore">Rimuovi</button>
        </div>
    </div>`;
}

// Funzione legacy per XML (manteniamo per compatibilità)
function creaCardDistributore(nodo) {
    const id = testoTag(nodo, "id");
    const nome = testoTag(nodo, "nome");
    const ubicazione = testoTag(nodo, "ubicazione");
    const stato = testoTag(nodo, "stato") || "attivo";

    const isAttivo = stato.toLowerCase() === "attivo";

    return `
    <div class="machine-card ${isAttivo ? "" : "inactive"}" data-id="${id}">
        <div class="machine-header">
            <div class="machine-id">${id} - ${nome}</div>
            <div class="machine-status ${isAttivo ? "active" : "inactive"}">
                ${isAttivo ? "Attivo" : "Disattivo"}
            </div>
        </div>
        <div style="font-size: 14px; color: #999; margin-bottom: 10px;">${ubicazione}</div>
        <div style="font-size: 14px; margin-bottom: 10px;">
            <span style="color: #999;">Modello:</span> Maestro Pro
        </div>
        <div style="font-size: 14px; margin-bottom: 15px;">
            <span style="color: #999;">Installato:</span> 15/03/2024
        </div>

        <div class="machine-actions">
            <button class="btn btn-secondary btn-modifica">Modifica</button>
            <button class="btn btn-danger btn-rimuovi-distributore">Rimuovi</button>
        </div>
    </div>`;
}

function testoTag(parent, tag) {
    const el = parent.getElementsByTagName(tag)[0];
    return el ? el.textContent : "";
}

function inizializzaFormAggiunta() {
    const form = document.querySelector("form");
    if (!form) return;

    form.addEventListener("submit", (e) => {
        e.preventDefault();

        const id = form.querySelector("input[placeholder*='UNIPA']")?.value || "";
        const nome = form.querySelectorAll("input[type='text']")[1]?.value || "";
        const ubicazione = form.querySelectorAll("input[type='text']")[2]?.value || "";

        if (!id || !nome || !ubicazione) {
            return;
        }

        // Crea oggetto distributore con valori di default
        const nuovoDistributore = {
            id: id.toUpperCase(),
            nome: nome,
            ubicazione: ubicazione,
            stato: "ATTIVO",
            livelloCaffe: 100,
            livelloLatte: 100,
            livelloZucchero: 100,
            livelloBicchieri: 200,
            livelloCioccolato: 100,
            livelloTe: 100,
            livelloBiscotti: 150
        };

        // Chiama API per salvare nel database
        fetch('http://localhost:8080/api/distributori', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(nuovoDistributore)
        })
        .then(response => {
            if (response.ok) {
                return response.json();
            } else if (response.status === 400) {
                // Leggi il messaggio di errore dal server
                return response.json().then(errData => {
                    throw new Error(errData.errore || 'Distributore già esistente o dati non validi');
                });
            } else {
                throw new Error('Errore durante il salvataggio');
            }
        })
        .then(distributore => {
            form.reset();
            caricaDistributoriDaAPI();
        })
        .catch(err => {
            console.error('Errore aggiunta distributore:', err);
        });
    });
}

function inizializzaBottoniRimuovi() {
    const buttons = document.querySelectorAll(".btn-rimuovi-distributore");
    buttons.forEach(btn => {
        btn.addEventListener("click", () => {
            const card = btn.closest(".machine-card");
            const id = card?.dataset.id || "(sconosciuto)";

            if (confirm("Vuoi rimuovere il distributore " + id + "?\nQuesta operazione eliminerà il distributore dal database.")) {
                // Chiama API DELETE per rimuovere dal database
                fetch(`http://localhost:8080/api/distributori/${id}`, {
                    method: 'DELETE'
                })
                .then(response => {
                    if (response.ok || response.status === 204) {
                        card.remove();
                    } else if (response.status === 404) {
                        throw new Error("Distributore non trovato");
                    } else {
                        throw new Error("Errore durante la rimozione");
                    }
                })
                .catch(err => {
                    console.error("Errore rimozione distributore:", err);
                });
            }
        });
    });

    const btnsModifica = document.querySelectorAll(".btn-modifica");
    btnsModifica.forEach(btn => {
        btn.addEventListener("click", () => {
            // Funzionalità di modifica non implementata
        });
    });
}


document.addEventListener("DOMContentLoaded", () => {
    caricaAddettiDaAPI();
    inizializzaFormAggiunta();
});

function caricaAddettiDaAPI() {
    fetch("http://localhost:8080/api/manutentori")
        .then(response => {
            if (response.ok) {
                return response.json();
            } else {
                throw new Error('Errore nel caricamento addetti');
            }
        })
        .then(addettiArray => {
            const container = document.getElementById("addetti-list");
            if (!container) return;

            container.innerHTML = "";

            if (addettiArray.length === 0) {
                container.innerHTML = "<p style='color:#aaa;text-align:center;padding:20px;'>Nessun addetto presente.</p>";
                return;
            }

            addettiArray.forEach(addetto => {
                const cardHtml = creaCardAddettoJSON(addetto);
                container.insertAdjacentHTML("beforeend", cardHtml);
            });

            inizializzaBottoniRimuovi();
        })
        .catch(err => {
            console.error("Errore caricamento addetti:", err);
            const container = document.getElementById("addetti-list");
            if (container) {
                container.innerHTML = "<p style='color:#ff6b7a;text-align:center;padding:20px;'>Errore nel caricamento degli addetti.</p>";
            }
        });
}

// Crea card da oggetto JSON (dal database)
function creaCardAddettoJSON(addetto) {
    const id = addetto.id || "";
    const nome = addetto.nome || "";
    const cognome = addetto.cognome || "";
    const username = addetto.username || "";
    const email = addetto.email || "";
    const telefono = addetto.telefono || "";
    const zona = addetto.zona || "Non specificata";
    const stato = addetto.stato || "ATTIVO";

    const isAttivo = stato === "ATTIVO";

    return `
    <div class="machine-card ${isAttivo ? "" : "inactive"}" data-id="${id}" data-username="${username}">
        <div class="machine-header">
            <div class="machine-id">${nome} ${cognome}</div>
            <div class="machine-status ${isAttivo ? "active" : "inactive"}">
                ${isAttivo ? "Attivo" : "Disattivo"}
            </div>
        </div>
        <div style="font-size: 14px; color: #999; margin-bottom: 5px;">Username: ${username}</div>
        <div style="font-size: 14px; color: #999; margin-bottom: 5px;">Email: ${email}</div>
        <div style="font-size: 14px; color: #999; margin-bottom: 5px;">Telefono: ${telefono || 'Non specificato'}</div>
        <div style="font-size: 14px; margin-bottom: 10px;">
            <span style="color: #999;">Zona:</span> ${zona}
        </div>

        <div class="machine-actions">
            <button class="btn btn-secondary">Modifica</button>
            <button class="btn btn-danger btn-rimuovi-addetto">Rimuovi</button>
        </div>
    </div>`;
}

function inizializzaBottoniRimuovi() {
    const buttons = document.querySelectorAll(".btn-rimuovi-addetto");
    buttons.forEach(btn => {
        btn.addEventListener("click", () => {
            const card = btn.closest(".machine-card");
            const id = card?.dataset.id || null;
            const username = card?.dataset.username || "(sconosciuto)";

            if (!id) {
                return;
            }

            if (confirm("Vuoi rimuovere l'addetto " + username + "?\nQuesta operazione eliminerà l'addetto dal database.")) {
                // Chiama API DELETE per rimuovere dal database
                fetch(`http://localhost:8080/api/manutentori/${id}`, {
                    method: 'DELETE'
                })
                .then(response => {
                    if (response.ok || response.status === 204) {
                        card.remove();
                    } else if (response.status === 404) {
                        throw new Error("Addetto non trovato");
                    } else {
                        throw new Error("Errore durante la rimozione");
                    }
                })
                .catch(err => {
                    console.error("Errore rimozione addetto:", err);
                });
            }
        });
    });
}

function inizializzaFormAggiunta() {
    const form = document.getElementById("form-nuovo-addetto");
    if (!form) return;

    form.addEventListener("submit", (e) => {
        e.preventDefault();

        const nome = document.getElementById("nome")?.value || "";
        const cognome = document.getElementById("cognome")?.value || "";
        const email = document.getElementById("email")?.value || "";
        const username = document.getElementById("username")?.value || "";
        const password = document.getElementById("password")?.value || "password123"; // Password di default
        const telefono = document.getElementById("telefono")?.value || "";
        const zona = document.getElementById("zona")?.value || "";

        if (!nome || !cognome || !email || !username) {
            return;
        }

        // Crea oggetto addetto/manutentore
        const nuovoAddetto = {
            nome: nome,
            cognome: cognome,
            username: username,
            password: password,
            email: email,
            telefono: telefono,
            zona: zona,
            stato: "ATTIVO"
        };

        // Chiama API per salvare nel database
        fetch('http://localhost:8080/api/manutentori', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(nuovoAddetto)
        })
        .then(response => {
            if (response.ok) {
                return response.json();
            } else if (response.status === 400) {
                // Leggi il messaggio di errore dal server
                return response.json().then(errData => {
                    throw new Error(errData.errore || 'Addetto già esistente o dati non validi');
                });
            } else {
                throw new Error('Errore durante il salvataggio');
            }
        })
        .then(addetto => {
            form.reset();
            caricaAddettiDaAPI();
        })
        .catch(err => {
            console.error('Errore aggiunta addetto:', err);
        });
    });
}

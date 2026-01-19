let bevandaSel = null;
let prezzoSel = 0;
let zuccheroSel = 0;
let credito = 0;

const DISTRIBUTORE_ID = "DIST001";

let clienteConnessoId = null;
let clientiDisponibili = [];

document.addEventListener("DOMContentLoaded", () => {
    inizializzaProdotti();
    inizializzaZucchero();
    inizializzaEroga();
    inizializzaTabs();
    inizializzaPopupBadge();

    inviaHeartbeat();
    setInterval(inviaHeartbeat, 60000);
});

function inizializzaPopupBadge() {
    const modal = document.getElementById('modal-badge');
    const btnConnetti = document.getElementById('btn-connetti-badge');
    const btnAnnulla = document.getElementById('btn-annulla-badge');
    const btnDisconnetti = document.getElementById('btn-disconnetti-badge');

    caricaClientiIniziali();
    mostraPopupBadge();

    btnConnetti.addEventListener('click', connettiClienteBadge);
    btnAnnulla.addEventListener('click', () => {
        nascondiPopupBadge();
    });
    btnDisconnetti.addEventListener('click', disconnettiBadge);
}

function caricaClientiIniziali() {
    fetch('http://localhost:8080/api/clienti')
        .then(response => {
            if (response.ok) {
                return response.json();
            } else {
                throw new Error('Errore caricamento clienti');
            }
        })
        .then(clienti => {
            clientiDisponibili = clienti;
            const select = document.getElementById('select-cliente');
            select.innerHTML = '<option value="">-- Seleziona un cliente --</option>';

            clienti.forEach(cliente => {
                const option = document.createElement('option');
                option.value = cliente.id;
                option.textContent = `${cliente.nome} ${cliente.cognome} (${cliente.email})`;
                select.appendChild(option);
            });
        })
        .catch(err => {
            console.error('Errore caricamento clienti:', err);
        });
}

function mostraPopupBadge() {
    const modal = document.getElementById('modal-badge');
    modal.style.display = 'flex';
}

function nascondiPopupBadge() {
    const modal = document.getElementById('modal-badge');
    modal.style.display = 'none';
}

function connettiClienteBadge() {
    const selectCliente = document.getElementById('select-cliente');
    const inputPassword = document.getElementById('input-password-badge');

    const clienteId = selectCliente.value;
    const password = inputPassword.value.trim();

    if (!clienteId) {
        return;
    }

    if (!password) {
        return;
    }

    const cliente = clientiDisponibili.find(c => c.id == clienteId);
    if (!cliente) {
        return;
    }

    fetch('http://localhost:8080/api/clienti/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
            email: cliente.email,
            password: password
        })
    })
    .then(response => {
        if (response.ok) {
            return response.json();
        } else {
            throw new Error('Password errata');
        }
    })
    .then(clienteAutenticato => {
        return fetch(`http://localhost:8080/api/clienti/${clienteAutenticato.id}/connetti`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ distributoreId: DISTRIBUTORE_ID })
        });
    })
    .then(response => {
        if (response.ok) {
            return response.json();
        } else {
            return response.json().then(errData => {
                throw new Error(errData.errore || 'Errore nella connessione');
            });
        }
    })
    .then(clienteConnesso => {
        clienteConnessoId = clienteConnesso.id;

        document.getElementById('user-name').textContent = clienteConnesso.nome + ' ' + clienteConnesso.cognome;
        credito = parseFloat(clienteConnesso.credito);
        document.getElementById('credit-display').textContent = '€ ' + credito.toFixed(2);

        document.getElementById('btn-disconnetti-badge').style.display = 'block';

        nascondiPopupBadge();
        document.getElementById('input-password-badge').value = '';

        if (window.pollingInterval) {
            clearInterval(window.pollingInterval);
        }
        window.pollingInterval = setInterval(aggiornaUtente, 3000);
    })
    .catch(err => {
        console.error('Errore connessione badge:', err);
    });
}

function disconnettiBadge() {
    if (!clienteConnessoId) {
        return;
    }

    if (!confirm('Vuoi rimuovere il badge dal distributore?')) {
        return;
    }

    fetch(`http://localhost:8080/api/clienti/${clienteConnessoId}/disconnetti`, {
        method: 'POST'
    })
    .then(response => {
        if (response.ok) {
            return response.json();
        } else {
            throw new Error('Errore durante la disconnessione');
        }
    })
    .then(() => {
        clienteConnessoId = null;
        credito = 0;

        document.getElementById('user-name').textContent = 'Nessun cliente connesso';
        document.getElementById('credit-display').textContent = '€ 0.00';
        document.getElementById('btn-disconnetti-badge').style.display = 'none';

        if (window.pollingInterval) {
            clearInterval(window.pollingInterval);
        }

        mostraPopupBadge();
    })
    .catch(err => {
        console.error('Errore disconnessione badge:', err);
    });
}

function aggiornaUtente() {
    if (!clienteConnessoId) {
        return;
    }

    fetch(`http://localhost:8080/api/clienti/distributore/${DISTRIBUTORE_ID}`)
        .then(response => {
            if (response.ok) {
                return response.json();
            } else if (response.status === 404) {
                throw new Error('Nessun cliente connesso');
            } else {
                throw new Error('Errore nel recupero dati cliente');
            }
        })
        .then(cliente => {
            credito = parseFloat(cliente.credito);
            document.getElementById('credit-display').textContent = '€ ' + credito.toFixed(2);
            document.getElementById('user-name').textContent = cliente.nome + ' ' + cliente.cognome;
        })
        .catch(err => {
            console.error('Errore polling utente:', err);
        });
}

function inizializzaProdotti() {
    document.querySelectorAll(".product-card").forEach(card => {
        card.addEventListener("click", () => selezionaProdotto(card));
    });
}

function selezionaProdotto(card) {
    if (getComputedStyle(card).display === "none") return;

    bevandaSel = card.dataset.name;
    prezzoSel = parseFloat(card.dataset.price);

    document.querySelectorAll(".product-card").forEach(c => c.classList.remove("selected"));
    card.classList.add("selected");
}

function inizializzaZucchero() {
    document.querySelectorAll(".sugar-btn").forEach(btn => {
        btn.addEventListener("click", () => selezionaZucchero(btn));
    });
}

function selezionaZucchero(btn) {
    zuccheroSel = parseFloat(btn.dataset.sugar);
    document.querySelectorAll(".sugar-btn").forEach(b => b.classList.remove("selected"));
    btn.classList.add("selected");
}

function inizializzaTabs() {
    const tabs = document.querySelectorAll(".tabs .tab");
    tabs.forEach(tab => tab.addEventListener("click", () => attivaTab(tab)));
    attivaTab(document.querySelector(".tab.active"));
}

function attivaTab(tab) {
    document.querySelectorAll(".tabs .tab").forEach(t => t.classList.remove("active"));
    tab.classList.add("active");

    const filtro = tab.dataset.tab;
    const cards = document.querySelectorAll(".product-card");

    cards.forEach(card => {
        const tipo = card.dataset.type;
        let show = filtro === "favourites" ||
                   (filtro === "coffee" && tipo === "coffee") ||
                   (filtro === "snacks" && tipo === "snack") ||
                   (filtro === "other" && tipo === "other");

        card.style.display = show ? "" : "none";
        if (!show) card.classList.remove("selected");
    });

    const sugarBox = document.querySelector(".sugar-selector");
    if (filtro === "snacks") {
        sugarBox.style.display = "none";
        zuccheroSel = 0;
        document.querySelectorAll(".sugar-btn").forEach(b => b.classList.remove("selected"));
    } else {
        sugarBox.style.display = "";
    }

    bevandaSel = null;
    prezzoSel = 0;
}

function inizializzaEroga() {
    document.getElementById("eroga-btn").addEventListener("click", eroga);
}

function eroga() {
    if (!bevandaSel) {
        return;
    }

    if (!clienteConnessoId) {
        mostraPopupBadge();
        return;
    }

    const vecchioCredito = credito;

    fetch(`http://localhost:8080/api/distributori/${DISTRIBUTORE_ID}/eroga`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
            tipoBevanda: bevandaSel,
            zuccheri: Math.floor(zuccheroSel),
            conBiscotto: false
        })
    })
    .then(response => {
        if (response.ok) {
            return response.json();
        } else {
            return response.json().then(errData => {
                throw new Error(errData.errore || 'Errore durante l\'erogazione');
            });
        }
    })
    .then(distributore => {
        return fetch(`http://localhost:8080/api/clienti/distributore/${DISTRIBUTORE_ID}`);
    })
    .then(response => {
        if (response.ok) {
            return response.json();
        } else {
            throw new Error('Errore aggiornamento credito');
        }
    })
    .then(cliente => {
        credito = parseFloat(cliente.credito);
        const creditoScalato = vecchioCredito - credito;

        alert(
            `✅ Erogazione completata!\n\n` +
            `Bevanda: ${bevandaSel}\n` +
            `Zucchero: ${zuccheroSel}\n` +
            `Costo: € ${creditoScalato.toFixed(2)}\n\n` +
            `Credito precedente: € ${vecchioCredito.toFixed(2)}\n` +
            `Credito attuale: € ${credito.toFixed(2)}`
        );

        document.getElementById("credit-display").textContent = "€ " + credito.toFixed(2);

        document.querySelectorAll(".product-card").forEach(c => c.classList.remove("selected"));
        document.querySelectorAll(".sugar-btn").forEach(b => b.classList.remove("selected"));

        bevandaSel = null;
        prezzoSel = 0;
        zuccheroSel = 0;
    })
    .catch(err => {
        console.error("Errore erogazione:", err);
    });
}

function inviaHeartbeat() {
    const url = `http://localhost:8081/api/monitoring/heartbeat/${DISTRIBUTORE_ID}`;

    fetch(url, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        }
    })
    .then(response => {
        if (response.ok) {
            console.log(`✓ Heartbeat inviato per distributore ${DISTRIBUTORE_ID}`);
        } else {
            console.warn(`⚠ Heartbeat fallito: HTTP ${response.status}`);
        }
    })
    .catch(err => {
        console.error(`✗ Errore invio heartbeat:`, err);
    });
}

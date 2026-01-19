let credito = 0.00;
let distributoreId = null;

document.addEventListener('DOMContentLoaded', () => {
    inizializzaBottoneConnetti();
    inizializzaBottoneDisconnetti();
    inizializzaBottoneRicarica();
    caricaUtenteIniziale();

    document.addEventListener('keydown', (e) => {
        if (e.ctrlKey && e.key === 'l') {
            localStorage.clear();
        }
    });
});

function caricaUtenteIniziale() {
    // Legge dati cliente da localStorage (salvati dal login)
    const clienteId = localStorage.getItem('clienteId');

    if (!clienteId) {
        window.location.href = 'cliente-login.html';
        return;
    }

    // Chiama API per ottenere dati aggiornati del cliente
    fetch(`http://localhost:8080/api/clienti/${clienteId}`)
        .then(response => {
            if (response.ok) {
                return response.json();
            } else {
                throw new Error('Cliente non trovato');
            }
        })
        .then(cliente => {
            const nomeEl = document.querySelector('.user-name');
            const creditoEl = document.querySelector('.credit');
            nomeEl.textContent = cliente.nome + ' ' + cliente.cognome;
            credito = parseFloat(cliente.credito);
            creditoEl.textContent = '€ ' + credito.toFixed(2);

            // Aggiorna localStorage con dati freschi
            localStorage.setItem('clienteCredito', cliente.credito);
        })
        .catch(err => {
            console.error('Errore nel caricamento dei dati utente:', err);
            // Fallback: usa dati da localStorage
            const nome = localStorage.getItem('clienteNome') || 'Utente';
            const creditoSalvato = localStorage.getItem('clienteCredito') || '0';

            const nomeEl = document.querySelector('.user-name');
            const creditoEl = document.querySelector('.credit');
            nomeEl.textContent = nome;
            credito = parseFloat(creditoSalvato);
            creditoEl.textContent = '€ ' + credito.toFixed(2);
        });
}

function inizializzaBottoneConnetti() {
    const btn = document.getElementById('btn-connetti');
    if (!btn) return;
    btn.addEventListener('click', connetti);
}


function connetti() {
    const input = document.getElementById('idDist');
    const id = input.value.trim().toUpperCase();

    if (id === '') {
        return;
    }

    const clienteId = localStorage.getItem('clienteId');
    if (!clienteId) {
        window.location.href = 'cliente-login.html';
        return;
    }

    fetch(`http://localhost:8080/api/clienti/${clienteId}/connetti`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ distributoreId: id })
    })
    .then(response => {
        if (response.ok) {
            return response.json();
        } else if (response.status === 400) {
            return response.json().then(errData => {
                throw new Error(errData.errore || 'Errore nella connessione');
            });
        } else {
            throw new Error('Errore nella connessione al distributore');
        }
    })
    .then(cliente => {
        distributoreId = id;
        alert('Connesso al distributore: ' + id);

        const badge = document.querySelector('.disconnected-badge');
        if (badge) {
            badge.textContent = 'Connesso a ' + id;
            badge.style.background = "#28a745";
        }
    })
    .catch(err => {
        console.error('Errore connessione:', err);
    });
}

function inizializzaBottoneDisconnetti() {
    const btn = document.getElementById('btn-disconnetti');
    if (!btn) return;
    btn.addEventListener('click', disconnetti);
}

function disconnetti() {
    const clienteId = localStorage.getItem('clienteId');
    if (!clienteId) {
        window.location.href = 'cliente-login.html';
        return;
    }

    if (!distributoreId) {
        return;
    }

    fetch(`http://localhost:8080/api/clienti/${clienteId}/disconnetti`, {
        method: 'POST'
    })
    .then(response => {
        if (response.ok) {
            return response.json();
        } else {
            throw new Error('Errore durante la disconnessione');
        }
    })
    .then(cliente => {
        alert('Disconnesso dal distributore: ' + distributoreId);
        distributoreId = null;

        const badge = document.querySelector('.disconnected-badge');
        if (badge) {
            badge.textContent = 'Non connesso';
            badge.style.background = "#dc3545";
        }
    })
    .catch(err => {
        console.error('Errore disconnessione:', err);
    });
}

function inizializzaBottoneRicarica() {
    const btn = document.getElementById('btn-ricarica');
    if (!btn) return;
    btn.addEventListener('click', ricarica);
}

function ricarica() {
    const select = document.getElementById('importo');
    const importo = parseFloat(select.value);

    if (isNaN(importo) || importo <= 0) {
        return;
    }

    const clienteId = localStorage.getItem('clienteId');
    if (!clienteId) {
        window.location.href = 'cliente-login.html';
        return;
    }

    const vecchio = credito;

    fetch(`http://localhost:8080/api/clienti/${clienteId}/ricarica`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ importo: importo })
    })
    .then(response => {
        if (response.ok) {
            return response.json();
        } else if (response.status === 400) {
            return response.json().then(errData => {
                throw new Error(errData.errore || 'Errore durante la ricarica');
            });
        } else {
            throw new Error('Errore durante la ricarica');
        }
    })
    .then(cliente => {
        credito = parseFloat(cliente.credito);

        alert(
            `Ricarica di € ${importo.toFixed(2)} effettuata!\n` +
            `Credito precedente: € ${vecchio.toFixed(2)}\n` +
            `Nuovo credito: € ${credito.toFixed(2)}`
        );

        const creditoEl = document.querySelector('.credit');
        if (creditoEl) {
            creditoEl.textContent = '€ ' + credito.toFixed(2);
        }

        localStorage.setItem('clienteCredito', cliente.credito);
    })
    .catch(err => {
        console.error('Errore ricarica:', err);
    });
}

document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('cliente-register-form');

    form.addEventListener('submit', (e) => {
        e.preventDefault();
        effettuaRegistrazione();
    });
});

function effettuaRegistrazione() {
    const nome = document.getElementById('nome').value;
    const cognome = document.getElementById('cognome').value;
    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;
    const confirmPassword = document.getElementById('confirm-password').value;

    if (password !== confirmPassword) {
        return;
    }

    // Crea oggetto cliente
    const nuovoCliente = {
        nome: nome,
        cognome: cognome,
        email: email,
        password: password,
        credito: 0  // Credito iniziale a 0
    };

    // Chiama API di registrazione
    fetch('http://localhost:8080/api/clienti/registra', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(nuovoCliente)
    })
    .then(response => {
        if (response.ok) {
            return response.json();
        } else if (response.status === 400) {
            throw new Error('Email già registrata o dati non validi');
        } else {
            throw new Error('Errore durante la registrazione');
        }
    })
    .then(cliente => {
        window.location.href = 'cliente-login.html';
    })
    .catch(err => {
        console.error('Errore registrazione:', err);
    });
}

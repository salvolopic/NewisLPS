document.addEventListener("DOMContentLoaded", () => {
    const form = document.getElementById("gestore-login-form");
    if (!form) return;

    form.addEventListener("submit", (e) => {
        e.preventDefault();
        effettuaLogin();
    });
});

function effettuaLogin() {
    const username = document.getElementById("username").value;
    const password = document.getElementById("password").value;

    // Effettua la richiesta di login al server
    fetch('http://localhost:8080/api/gestori/login', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ username, password })
    })
    .then(response => {
        if (response.ok) {
            return response.json();
        } else {
            throw new Error('Login fallito');
        }
    })
    .then(gestore => {
        // Salva ID gestore in localStorage per usarlo in gestore-main.html
        localStorage.setItem('gestoreId', gestore.id);
        localStorage.setItem('gestoreNome', gestore.nome + ' ' + gestore.cognome);
        localStorage.setItem('gestoreRuolo', gestore.ruolo);

        window.location.href = 'gestore-main.html';
    })
    .catch(err => {
        console.error('Errore login:', err);
    });
}

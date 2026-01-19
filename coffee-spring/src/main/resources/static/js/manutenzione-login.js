document.addEventListener("DOMContentLoaded", () => {
    const form = document.getElementById("manutenzione-login-form");
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
    fetch('http://localhost:8080/api/manutentori/login', {
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
    .then(manutentore => {
        // Salva ID manutentore in localStorage per usarlo in manutenzione-main.html
        localStorage.setItem('manutentoreId', manutentore.id);
        localStorage.setItem('manutentoreNome', manutentore.nome + ' ' + manutentore.cognome);

        window.location.href = 'manutenzione-main.html';
    })
    .catch(err => {
        console.error('Errore login:', err);
    });
}
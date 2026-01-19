document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('cliente-login-form');
    form.addEventListener('submit', (e) => {
        e.preventDefault();
        effettuaLogin();
    });
});

function effettuaLogin() {
    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;

    // Effettua la richiesta di login al server 
    fetch('http://localhost:8080/api/clienti/login', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ email, password })
    })
    .then(response => {
        if (response.ok) {
            return response.json();
        } else {
            throw new Error('Login fallito');
        }
    })
    .then(cliente => {
        
        // Salva ID cliente in localStorage per usarlo in cliente-main.html
        localStorage.setItem('clienteId', cliente.id);
        localStorage.setItem('clienteNome', cliente.nome + ' ' + cliente.cognome);
        localStorage.setItem('clienteCredito', cliente.credito);

        window.location.href = 'cliente-main.html';
    })
    .catch(err => {
        console.error('Errore login:', err);
    });
}

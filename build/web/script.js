window.addEventListener('DOMContentLoaded', () => {
    if (window.location.pathname.includes("index.html")) {
        console.log("Welcome to CraveRush!");
    }

    const navLinks = document.querySelectorAll("nav a");
    navLinks.forEach(link => {
        if (link.href === window.location.href) {
            link.style.backgroundColor = "#a1887f";
            link.style.color = "white";
        }
    });

    const passwordInput = document.querySelector('input[type="password"]');
    if (passwordInput) {
        const toggle = document.createElement('button');
        toggle.textContent = 'Show';
        toggle.type = 'button';
        toggle.style.marginLeft = '10px';
        toggle.onclick = () => {
            passwordInput.type = passwordInput.type === 'password' ? 'text' : 'password';
            toggle.textContent = passwordInput.type === 'password' ? 'Show' : 'Hide';
        };
        passwordInput.parentNode.appendChild(toggle);
    }
});

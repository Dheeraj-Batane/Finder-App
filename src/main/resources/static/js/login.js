document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('loginForm');
    const messageBox = document.getElementById('messageBox');
    const loginBtn = document.getElementById('loginBtn');

    form.addEventListener('submit', async (e) => {
        e.preventDefault();

        const payload = {
            email: document.getElementById('email').value,
            password: document.getElementById('password').value
        };

        loginBtn.innerText = "Authenticating...";
        loginBtn.disabled = true;
        messageBox.style.display = 'none';
        messageBox.className = '';

        try {
            // 1. Call Login API
            const loginRes = await fetch('/api/auth/login', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });

            const loginData = await loginRes.json();

            if (loginRes.ok && loginData.responseCode === "00000000") {
                // Save user details to Local Storage
                localStorage.setItem('userId', loginData.userId);
                localStorage.setItem('userRole', loginData.userRole);

                loginBtn.innerText = "Fetching Dashboard...";

                // 2. Fetch Service Categories immediately after successful login
                const servicesRes = await fetch('/services');
                if (servicesRes.ok) {
                    const servicesData = await servicesRes.json();

                    if (servicesData.responseCode === "00000000") {
                        // Store the categories array as a JSON string in Local Storage
                        localStorage.setItem('categories', JSON.stringify(servicesData.data));
                    }
                }

                // 3. Redirect based on role (or default to user dashboard)
                if (loginData.userRole === "PROVIDER") {
                    window.location.href = '/provider-dashboard'; // You can build this later
                } else {
                    window.location.href = '/user-dashboard';
                }

            } else {
                messageBox.innerText = loginData.responseMessage || "Invalid email or password.";
                messageBox.classList.add('error-msg');
                messageBox.style.display = 'block';
                loginBtn.innerText = "Login";
                loginBtn.disabled = false;
            }

        } catch (error) {
            messageBox.innerText = "A network error occurred. Please try again.";
            messageBox.classList.add('error-msg');
            messageBox.style.display = 'block';
            loginBtn.innerText = "Login";
            loginBtn.disabled = false;
        }
    });
});
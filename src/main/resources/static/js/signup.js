document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('userSignUpForm');
    const messageBox = document.getElementById('messageBox');
    const submitBtn = document.getElementById('submitBtn');

    form.addEventListener('submit', async (e) => {
        e.preventDefault();

        // 1. Build the payload
        const payload = {
            firstName: document.getElementById('firstName').value,
            lastName: document.getElementById('lastName').value,
            email: document.getElementById('email').value,
            cellPhone: document.getElementById('cellPhone').value,
            password: document.getElementById('password').value,
            role: "USER", // Standard user role
            address: {
                addressLine1: document.getElementById('addressLine1').value,
                addressLine2: document.getElementById('addressLine2').value,
                city: document.getElementById('city').value,
                state: document.getElementById('state').value,
                zipCode: document.getElementById('zipCode').value,
                addressType: document.getElementById('addressType').value
            }
        };

        // 2. Prepare UI
        submitBtn.innerText = "Creating Account...";
        submitBtn.disabled = true;
        messageBox.style.display = 'none';
        messageBox.className = '';

        try {
            // 3. Call the API
            const response = await fetch('/api/auth/signup', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(payload)
            });

            const data = await response.json();

            // 4. Handle Response
            if (response.ok && data.responseCode === "00000000") {
                messageBox.innerText = "Account created successfully! Redirecting to login...";
                messageBox.classList.add('success-msg');
                messageBox.style.display = 'block';
                form.reset();

                // Redirect directly to login page
                setTimeout(() => {
                    window.location.href = '/login';
                }, 2000);

            } else {
                messageBox.innerText = "Error: " + (data.responseMessage || "Failed to sign up.");
                messageBox.classList.add('error-msg');
                messageBox.style.display = 'block';
            }

        } catch (error) {
            console.error('Fetch error:', error);
            messageBox.innerText = "A network error occurred. Please try again later.";
            messageBox.classList.add('error-msg');
            messageBox.style.display = 'block';
        } finally {
            submitBtn.innerText = "Create Account";
            submitBtn.disabled = false;
        }
    });
});
document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('providerSignUpForm');
    const messageBox = document.getElementById('messageBox');
    const submitBtn = document.getElementById('submitBtn');

    form.addEventListener('submit', async (e) => {
        e.preventDefault(); // Prevent standard HTML form submission

        // 1. Build the payload matching the backend DTOs
        const payload = {
            firstName: document.getElementById('firstName').value,
            lastName: document.getElementById('lastName').value,
            email: document.getElementById('email').value,
            cellPhone: document.getElementById('cellPhone').value,
            password: document.getElementById('password').value,
            role: "PROVIDER", // Hardcoded since this is the Provider sign-up page
            address: {
                addressLine1: document.getElementById('addressLine1').value,
                addressLine2: document.getElementById('addressLine2').value,
                city: document.getElementById('city').value,
                state: document.getElementById('state').value,
                zipCode: document.getElementById('zipCode').value,
                addressType: document.getElementById('addressType').value
            }
        };

        // 2. Prepare UI for loading
        submitBtn.innerText = "Processing...";
        submitBtn.disabled = true;
        messageBox.style.display = 'none';
        messageBox.className = '';

        try {
            // 3. Make the API Call
            const response = await fetch('/api/auth/signup', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(payload)
            });
            // Parse the new JSON response format
            const data = await response.json();

            // 4. Handle the Response
           // Check for the custom responseCode "00000000"
           if (response.ok && data.responseCode === "00000000") {
                           messageBox.innerText = "Account created successfully! Redirecting to step 2...";
                           messageBox.classList.add('success-msg');
                           messageBox.style.display = 'block';

                           // Store the userId in localStorage for the next page to use
                           localStorage.setItem('onboardingUserId', data.userId);

                           setTimeout(() => {
                               window.location.href = '/provider-onboarding'; // Redirect to the new page
                           }, 1500);

           } else {
                           // Handle backend validation or custom errors
                           messageBox.innerText = "Error: " + (data.responseMessage || "Failed to sign up.");
                           messageBox.classList.add('error-msg');
                           messageBox.style.display = 'block';
                       }

        } catch (error) {
            // Network or server down error
            console.error('Fetch error:', error);
            messageBox.innerText = "A network error occurred. Please try again later.";
            messageBox.classList.add('error-msg');
            messageBox.style.display = 'block';
        } finally {
            // Restore button state
            submitBtn.innerText = "Sign Up as Provider";
            submitBtn.disabled = false;
        }
    });
});
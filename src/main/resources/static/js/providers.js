document.addEventListener('DOMContentLoaded', async () => {

    // Security Check
    const userId = localStorage.getItem('userId');
    if (!userId) {
        window.location.href = '/login';
        return;
    }

    // Extract ONLY the categoryId from URL Parameters
        const urlParams = new URLSearchParams(window.location.search);
        const categoryId = urlParams.get('categoryId');

        if (!categoryId) {
            alert("No service category selected.");
            window.location.href = '/user-dashboard';
            return;
        }

        // Fetch the category name from Local Storage
        const storedCategories = localStorage.getItem('categories');
        let categoryName = "Selected Service"; // Default fallback name

        if (storedCategories) {
            const categories = JSON.parse(storedCategories);
            // Use '==' instead of '===' in case the URL param is a string but stored ID is a number
            const foundCategory = categories.find(cat => cat.id == categoryId);

            if (foundCategory) {
                categoryName = foundCategory.name;
            }
        }

        // Update the Header Title
        document.getElementById('pageTitleHeader').innerText = `Top Rated Professionals for ${categoryName}`;
    const loadingMsg = document.getElementById('loadingMsg');
    const providersGrid = document.getElementById('providersGrid');

    try {
        // Fetch Providers from API
        // Note: Make sure your backend mapping is updated to match this path
        const response = await fetch(`/services/category/${categoryId}`);
        const data = await response.json();

        loadingMsg.style.display = 'none';
        providersGrid.style.display = 'grid';

        if (response.ok && data.responseCode === "00000000" && data.data.length > 0) {
            renderProviders(data.data, categoryId, categoryName);
        } else {
            providersGrid.innerHTML = `
                <div class="empty-state">
                    <h3>No professionals found!</h3>
                    <p>There are currently no verified providers available for ${categoryName}. Please check back later.</p>
                </div>`;
        }
    } catch (error) {
        console.error("Error fetching providers:", error);
        loadingMsg.innerHTML = `<span style="color: red;">Failed to load providers. Please try again later.</span>`;
    }

    // Logout handling
    document.getElementById('logoutBtn').addEventListener('click', (e) => {
        e.preventDefault();
        localStorage.clear();
        window.location.href = '/login';
    });
});

function renderProviders(providers, categoryId, categoryName) {
    const providersGrid = document.getElementById('providersGrid');
    providersGrid.innerHTML = '';

    providers.forEach(provider => {
        // Format the schedule array into HTML list items
        let scheduleHtml = '';
        if (provider.schedules && provider.schedules.length > 0) {
            provider.schedules.forEach(sched => {
                // Format time to remove seconds if they exist (e.g., "09:00:00" -> "09:00")
                const start = sched.startTime.substring(0, 5);
                const end = sched.endTime.substring(0, 5);
                // Capitalize first letter of the day, lowercase the rest
                const day = sched.dayOfWeek.charAt(0).toUpperCase() + sched.dayOfWeek.slice(1).toLowerCase();

                scheduleHtml += `<li><strong>${day}:</strong> ${start} - ${end}</li>`;
            });
        } else {
            scheduleHtml = `<li>Schedule not provided</li>`;
        }

        const card = document.createElement('div');
        card.className = 'provider-card';
        card.innerHTML = `
            <div class="provider-header">
                <div>
                    <div class="provider-name">${provider.firstName} ${provider.lastName}</div>
                    <div class="provider-stats">${provider.experienceYears} Years Experience</div>
                </div>
                <div class="rate-badge">₹${provider.hourlyRate}/hr</div>
            </div>

            <div class="provider-bio">
                ${provider.bio}
            </div>

            <div class="schedule-box">
                <div class="schedule-title">Availability:</div>
                <ul class="schedule-list">
                    ${scheduleHtml}
                </ul>
            </div>

            <button class="book-btn" onclick="initiateBooking(${provider.providerId}, '${provider.firstName} ${provider.lastName}', ${categoryId}, '${categoryName}')">
                Book Appointment
            </button>
        `;

        providersGrid.appendChild(card);
    });
}

// Global function to handle clicking the Book button
// --- Modal and Booking Logic ---

// Variables to hold the selected data
let selectedProviderId = null;
let selectedCategoryId = null;

const modal = document.getElementById('bookingModal');
const closeModalBtn = document.getElementById('closeModalBtn');
const bookingForm = document.getElementById('bookingForm');
const bookingMessageBox = document.getElementById('bookingMessageBox');
const confirmBookingBtn = document.getElementById('confirmBookingBtn');

// 1. Function called when clicking a "Book Appointment" button on a card
window.initiateBooking = function(providerId, providerName, categoryId, categoryName) {
    selectedProviderId = providerId;
    selectedCategoryId = categoryId;

    // Update modal text
    document.getElementById('modalTitle').innerText = `Book ${providerName}`;
    document.getElementById('modalSubtitle').innerText = `Service: ${categoryName}`;

    // Prevent booking in the past by setting the 'min' attribute to today
    const today = new Date().toISOString().split('T')[0];
    document.getElementById('appointmentDate').setAttribute('min', today);

    // Reset form and messages
    bookingForm.reset();
    bookingMessageBox.style.display = 'none';

    // Show the modal
    modal.style.display = 'flex';
};

// 2. Close modal when clicking the 'X' or clicking outside the box
closeModalBtn.addEventListener('click', () => {
    modal.style.display = 'none';
});

window.addEventListener('click', (event) => {
    if (event.target === modal) {
        modal.style.display = 'none';
    }
});

// 3. Handle the actual booking submission
bookingForm.addEventListener('submit', async (e) => {
    e.preventDefault();

    const userId = localStorage.getItem('userId');
    if (!userId) {
        alert("Session expired. Please log in again.");
        window.location.href = '/login';
        return;
    }

    // HTML time input returns "HH:mm". We append ":00" to match your backend's LocalTime format
    const timeValue = document.getElementById('appointmentTime').value + ":00";
    const dateValue = document.getElementById('appointmentDate').value;

    const payload = {
        userId: parseInt(userId),
        providerId: selectedProviderId,
        categoryId: parseInt(selectedCategoryId),
        appointmentDate: dateValue,
        appointmentTime: timeValue
    };

    confirmBookingBtn.innerText = "Processing...";
    confirmBookingBtn.disabled = true;
    bookingMessageBox.style.display = 'none';
    bookingMessageBox.className = '';

    try {
        const response = await fetch('/api/bookings', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });

        // Your backend returns 201 CREATED for success
        if (response.status === 201) {
            bookingMessageBox.innerText = "Booking Confirmed! Redirecting...";
            bookingMessageBox.style.backgroundColor = '#d4edda';
            bookingMessageBox.style.color = '#155724';
            bookingMessageBox.style.border = '1px solid #c3e6cb';
            bookingMessageBox.style.display = 'block';

            // Redirect to My Bookings page after 1.5 seconds
            setTimeout(() => {
                window.location.href = '/my-bookings';
            }, 15000);

        } else {
            // Handle error messages from backend (like "Time slot already booked")
            const errorText = await response.text();
            bookingMessageBox.innerText = "Error: " + errorText;
            bookingMessageBox.style.backgroundColor = '#f8d7da';
            bookingMessageBox.style.color = '#721c24';
            bookingMessageBox.style.border = '1px solid #f5c6cb';
            bookingMessageBox.style.display = 'block';
        }

    } catch (error) {
        console.error("Booking error:", error);
        bookingMessageBox.innerText = "Network error. Please try again.";
        bookingMessageBox.style.backgroundColor = '#f8d7da';
        bookingMessageBox.style.color = '#721c24';
        bookingMessageBox.style.display = 'block';
    } finally {
        confirmBookingBtn.innerText = "Confirm Booking";
        confirmBookingBtn.disabled = false;
    }
});
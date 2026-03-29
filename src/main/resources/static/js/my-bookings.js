document.addEventListener('DOMContentLoaded', async () => {

    // Security Check
    const userId = localStorage.getItem('userId');
    if (!userId) {
        window.location.href = '/login';
        return;
    }

    const loadingMsg = document.getElementById('loadingMsg');
    const bookingsGrid = document.getElementById('bookingsGrid');

    try {
        // Fetch User's Bookings
        const response = await fetch(`/api/bookings/user/${userId}`);
        const data = await response.json();

        loadingMsg.style.display = 'none';
        bookingsGrid.style.display = 'grid';

        if (response.ok && data.responseCode === "00000000" && data.data.length > 0) {
            renderBookings(data.data);
        } else {
            bookingsGrid.innerHTML = `
                <div class="empty-state">
                    <h3>No Bookings Found</h3>
                    <p>You haven't booked any services yet. Go to your dashboard to find professionals near you.</p>
                    <a href="/user-dashboard" class="search-btn" style="display: inline-block; margin-top: 15px; text-decoration: none;">Explore Services</a>
                </div>`;
        }
    } catch (error) {
        console.error("Error fetching bookings:", error);
        loadingMsg.innerHTML = `<span style="color: red;">Failed to load your bookings. Please try again later.</span>`;
    }

    // Logout handling
    document.getElementById('logoutBtn').addEventListener('click', (e) => {
        e.preventDefault();
        localStorage.clear();
        window.location.href = '/login';
    });
});

function renderBookings(bookings) {
    const bookingsGrid = document.getElementById('bookingsGrid');
    bookingsGrid.innerHTML = '';

    bookings.forEach(booking => {

        // Format the date nicely (e.g., "2026-03-27" -> "Mar 27, 2026")
        const dateObj = new Date(booking.appointmentDate);
        const formattedDate = dateObj.toLocaleDateString('en-US', {
            month: 'short', day: 'numeric', year: 'numeric'
        });

        // Convert 24h time to 12h format (e.g., "14:00" -> "2:00 PM")
        const timeParts = booking.appointmentTime.split(':');
        let hours = parseInt(timeParts[0]);
        const minutes = timeParts[1];
        const ampm = hours >= 12 ? 'PM' : 'AM';
        hours = hours % 12;
        hours = hours ? hours : 12; // the hour '0' should be '12'
        const formattedTime = `${hours}:${minutes} ${ampm}`;

        const card = document.createElement('div');
        card.className = 'booking-card';

        card.innerHTML = `
            <div class="booking-info">
                <div class="service-title">${booking.categoryName}</div>
                <div class="provider-name">Professional: ${booking.providerName}</div>

                <div class="datetime-box">
                    <div>📅 <span>${formattedDate}</span></div>
                    <div>⏰ <span>${formattedTime}</span></div>
                </div>
            </div>

            <div class="status-badge status-${booking.status}">
                ${booking.status}
            </div>
        `;

        bookingsGrid.appendChild(card);
    });
}
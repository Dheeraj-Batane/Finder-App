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
        // Format the date and time
        const dateObj = new Date(booking.appointmentDate);
        const formattedDate = dateObj.toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' });

        const timeParts = booking.appointmentTime.split(':');
        let hours = parseInt(timeParts[0]);
        const minutes = timeParts[1];
        const ampm = hours >= 12 ? 'PM' : 'AM';
        hours = hours % 12;
        hours = hours ? hours : 12;
        const formattedTime = `${hours}:${minutes} ${ampm}`;

        // --- NEW: Review Display Logic ---
        let reviewSectionHtml = '';

        if (booking.status === 'COMPLETED') {
            if (booking.reviewStars) {
                // User has already reviewed this booking. Generate static stars.
                const filledStars = '★'.repeat(booking.reviewStars);
                const emptyStars = '☆'.repeat(5 - booking.reviewStars);

                reviewSectionHtml = `
                    <div class="submitted-review">
                        <div class="static-stars">${filledStars}${emptyStars}</div>
                        <div class="review-text">"${booking.reviewComments}"</div>
                    </div>
                `;
            } else {
                // Booking is completed, but no review exists yet. Show the button.
                reviewSectionHtml = `<button onclick="openReviewModal(${booking.bookingId}, '${booking.providerName}')" style="background: #ffc107; color: #333; border: none; padding: 8px 15px; border-radius: 5px; font-weight: bold; cursor: pointer; margin-top: 15px; width: 100%;">Leave a Review</button>`;
            }
        }

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

                ${reviewSectionHtml} </div>

            <div class="status-badge status-${booking.status}">
                ${booking.status}
            </div>
        `;

        bookingsGrid.appendChild(card);
    });
}

// --- Review Modal Logic ---
const reviewModal = document.getElementById('reviewModal');
const closeReviewModalBtn = document.getElementById('closeReviewModalBtn');
const reviewForm = document.getElementById('reviewForm');
const reviewMessageBox = document.getElementById('reviewMessageBox');
const submitReviewBtn = document.getElementById('submitReviewBtn');

window.openReviewModal = function(bookingId, providerName) {
    document.getElementById('reviewBookingId').value = bookingId;
    document.getElementById('reviewProviderName').innerText = `Rate your experience with ${providerName}`;
    reviewForm.reset();
    reviewMessageBox.style.display = 'none';
    reviewModal.style.display = 'flex';
};

closeReviewModalBtn.addEventListener('click', () => { reviewModal.style.display = 'none'; });
window.addEventListener('click', (event) => { if (event.target === reviewModal) reviewModal.style.display = 'none'; });

reviewForm.addEventListener('submit', async (e) => {
    e.preventDefault();

    // Get selected star rating
    const selectedStar = document.querySelector('input[name="stars"]:checked');
    if (!selectedStar) {
        alert("Please select a star rating.");
        return;
    }

    const payload = {
        bookingId: parseInt(document.getElementById('reviewBookingId').value),
        stars: parseInt(selectedStar.value),
        comments: document.getElementById('reviewComments').value
    };

    submitReviewBtn.innerText = "Submitting...";
    submitReviewBtn.disabled = true;

    try {
        const response = await fetch('/api/bookings/review', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });

        const data = await response.json();

        if (response.status === 201) {
            reviewMessageBox.innerText = "Review submitted successfully!";
            reviewMessageBox.style.backgroundColor = '#d4edda';
            reviewMessageBox.style.color = '#155724';
            reviewMessageBox.style.border = '1px solid #c3e6cb';
            reviewMessageBox.style.display = 'block';

            // Reload page to reflect changes
            setTimeout(() => { window.location.reload(); }, 1500);
        } else {
            reviewMessageBox.innerText = "Error: " + (data.responseMessage || "Could not submit review.");
            reviewMessageBox.style.backgroundColor = '#f8d7da';
            reviewMessageBox.style.color = '#721c24';
            reviewMessageBox.style.border = '1px solid #f5c6cb';
            reviewMessageBox.style.display = 'block';
        }
    } catch (error) {
        reviewMessageBox.innerText = "Network error. Please try again.";
        reviewMessageBox.style.backgroundColor = '#f8d7da';
        reviewMessageBox.style.color = '#721c24';
        reviewMessageBox.style.display = 'block';
    } finally {
        submitReviewBtn.innerText = "Submit Review";
        submitReviewBtn.disabled = false;
    }
});
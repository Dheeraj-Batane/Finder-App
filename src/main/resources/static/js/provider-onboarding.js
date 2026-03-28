document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('onboardingForm');
    const messageBox = document.getElementById('messageBox');
    const submitBtn = document.getElementById('submitBtn');
    const addScheduleBtn = document.getElementById('addScheduleBtn');
    const scheduleContainer = document.getElementById('scheduleContainer');

    // 1. Verify User ID exists. If not, they bypassed step 1.
    const userId = localStorage.getItem('onboardingUserId');
    if (!userId) {
        alert("Session expired or invalid. Please sign up first.");
        window.location.href = '/provider-signup';
        return;
    }

    // 2. Logic to dynamically add schedule rows
    addScheduleBtn.addEventListener('click', () => {
        const row = document.createElement('div');
        row.className = 'schedule-row';
        row.innerHTML = `
            <div class="form-group">
                <label>Day</label>
                <select class="day-input" required>
                    <option value="MONDAY">Monday</option>
                    <option value="TUESDAY">Tuesday</option>
                    <option value="WEDNESDAY">Wednesday</option>
                    <option value="THURSDAY">Thursday</option>
                    <option value="FRIDAY">Friday</option>
                    <option value="SATURDAY">Saturday</option>
                    <option value="SUNDAY">Sunday</option>
                </select>
            </div>
            <div class="form-group">
                <label>Start Time</label>
                <input type="time" class="start-time-input" required>
            </div>
            <div class="form-group">
                <label>End Time</label>
                <input type="time" class="end-time-input" required>
            </div>
            <button type="button" class="remove-btn" onclick="this.parentElement.remove()">X</button>
        `;
        scheduleContainer.appendChild(row);
    });

    // 3. Handle Form Submission
    form.addEventListener('submit', async (e) => {
        e.preventDefault();

        // Get selected categories
        const checkedCategories = Array.from(document.querySelectorAll('input[name="category"]:checked'))
                                       .map(cb => parseInt(cb.value));

        if (checkedCategories.length === 0) {
            alert("Please select at least one service category.");
            return;
        }

        // Get schedules
        const scheduleRows = document.querySelectorAll('.schedule-row');
        const schedules = [];

        scheduleRows.forEach(row => {
            const day = row.querySelector('.day-input').value;
            // HTML time inputs return "HH:mm". We append ":00" to ensure it matches LocalTime expectations
            const start = row.querySelector('.start-time-input').value + ":00";
            const end = row.querySelector('.end-time-input').value + ":00";

            schedules.push({ dayOfWeek: day, startTime: start, endTime: end });
        });

        // Build Payload
        const payload = {
            userId: parseInt(userId),
            categoryIds: checkedCategories,
            experienceYears: parseInt(document.getElementById('experienceYears').value),
            hourlyRate: parseFloat(document.getElementById('hourlyRate').value),
            bio: document.getElementById('bio').value,
            schedules: schedules
        };

        submitBtn.innerText = "Saving Profile...";
        submitBtn.disabled = true;
        messageBox.style.display = 'none';
        messageBox.className = '';

        try {
            const response = await fetch('/api/auth/provider/onboard', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });

            if (response.status === 201 || response.ok) {
                messageBox.innerText = "Profile complete! Awaiting admin verification. Redirecting to login...";
                messageBox.classList.add('success-msg');
                messageBox.style.display = 'block';

                // Clear the ID from memory for security
                localStorage.removeItem('onboardingUserId');

                setTimeout(() => {
                    window.location.href = '/login';
                }, 3000);

            } else {
                const errorText = await response.text();
                messageBox.innerText = "Error: " + errorText;
                messageBox.classList.add('error-msg');
                messageBox.style.display = 'block';
            }

        } catch (error) {
            messageBox.innerText = "A network error occurred. Please try again later.";
            messageBox.classList.add('error-msg');
            messageBox.style.display = 'block';
        } finally {
            submitBtn.innerText = "Complete Onboarding";
            submitBtn.disabled = false;
        }
    });
});
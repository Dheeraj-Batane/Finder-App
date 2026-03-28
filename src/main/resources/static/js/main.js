document.addEventListener('DOMContentLoaded', () => {
    // Simple logic to handle the search button click
    const searchBtn = document.getElementById('homeSearchBtn');

    if(searchBtn) {
        searchBtn.addEventListener('click', () => {
            const service = document.getElementById('serviceSearch').value;
            const location = document.getElementById('locationSearch').value;

            if(!service && !location) {
                alert("Please enter a service or location to search.");
                return;
            }

            // Later, this will redirect to: /search?service=X&location=Y
            console.log(`Searching for ${service} in ${location}`);
        });
    }
});


document.addEventListener('DOMContentLoaded', () => {

    // 1. Security Check: Ensure user is actually logged in
    const userId = localStorage.getItem('userId');
    if (!userId) {
        window.location.href = '/login';
        return;
    }

    const servicesGrid = document.getElementById('servicesGrid');

    // 2. Load categories from Local Storage
    const storedCategories = localStorage.getItem('categories');

    if (storedCategories) {
        const categories = JSON.parse(storedCategories);
        renderCategories(categories);
    } else {
        servicesGrid.innerHTML = `<p class="empty-state">No services available right now. Please check back later.</p>`;
    }

    // 3. Function to dynamically generate category cards
    function renderCategories(categories) {
        servicesGrid.innerHTML = ''; // Clear loading text

        categories.forEach(category => {
            // Create the card element
            const card = document.createElement('div');
            card.className = 'service-card';

            // We store the ID inside a data-attribute so it is hidden from the UI but accessible to JS
            card.setAttribute('data-category-id', category.id);

            card.innerHTML = `
                <h3>${category.name}</h3>
                <p>${category.description}</p>
            `;

            // Add click listener to fetch providers for this specific category
            card.addEventListener('click', () => {
                const catId = card.getAttribute('data-category-id');
                const catName = category.name;

                // For now, we just log it.
                // Next step: You will likely redirect to a new page or open a modal to show providers.
                console.log(`User clicked to view providers for Category ID: ${catId} (${catName})`);

                // Example of next step redirect:
                // window.location.href = `/providers?categoryId=${catId}`;
            });

            servicesGrid.appendChild(card);
        });
    }

    // 4. Handle Logout functionality
    document.getElementById('logoutBtn').addEventListener('click', (e) => {
        e.preventDefault();
        localStorage.clear(); // Clear all user data and categories
        window.location.href = '/login';
    });
});
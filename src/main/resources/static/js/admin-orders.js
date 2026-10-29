/**
 * Admin Orders Page - Search and Filter Functionality
 * Provides client-side filtering for the orders table
 */

(function() {
    'use strict';

    // DOM Elements
    const searchInput = document.getElementById('searchOrders');
    const statusFilter = document.getElementById('statusFilter');
    const ordersTable = document.getElementById('ordersTable');
    const tableBody = ordersTable ? ordersTable.querySelector('tbody') : null;

    // No table, no functionality needed
    if (!tableBody) return;

    const rows = Array.from(tableBody.querySelectorAll('tr[data-status]'));

    // Debounce function for search
    function debounce(func, wait) {
        let timeout;
        return function executedFunction(...args) {
            const later = () => {
                clearTimeout(timeout);
                func(...args);
            };
            clearTimeout(timeout);
            timeout = setTimeout(later, wait);
        };
    }

    // Filter rows based on search term and status filter
    function filterRows() {
        const searchTerm = searchInput.value.toLowerCase().trim();
        const statusValue = statusFilter.value;

        let visibleCount = 0;

        rows.forEach(row => {
            const orderId = row.cells[0]?.textContent?.toLowerCase() || '';
            const customerName = row.cells[1]?.textContent?.toLowerCase() || '';
            const customerEmail = row.cells[1]?.querySelector('.customer-email')?.textContent?.toLowerCase() || '';
            const province = row.cells[2]?.textContent?.toLowerCase() || '';
            const total = row.cells[3]?.textContent?.toLowerCase() || '';
            const status = row.dataset.status || '';

            // Check search term
            const matchesSearch = !searchTerm ||
                orderId.includes(searchTerm) ||
                customerName.includes(searchTerm) ||
                customerEmail.includes(searchTerm) ||
                province.includes(searchTerm) ||
                total.includes(searchTerm);

            // Check status filter
            const matchesStatus = !statusValue || status === statusValue;

            const isVisible = matchesSearch && matchesStatus;

            if (isVisible) {
                row.style.display = '';
                row.style.animation = 'fadeIn 0.2s ease-out';
                visibleCount++;
            } else {
                row.style.display = 'none';
            }
        });

        // Update visible count in footer
        updateVisibleCount(visibleCount);

        // Show/hide no results message
        toggleNoResults(visibleCount === 0 && rows.length > 0);
    }

    // Update visible count in card footer
    function updateVisibleCount(count) {
        const footer = ordersTable.closest('.card')?.querySelector('.card-footer .text-muted strong');
        if (footer) {
            footer.textContent = count;
        }
    }

    // Toggle no results message
    function toggleNoResults(show) {
        let noResultsRow = tableBody.querySelector('.no-results-row');

        if (show && !noResultsRow) {
            noResultsRow = document.createElement('tr');
            noResultsRow.className = 'no-results-row';
            noResultsRow.innerHTML = `
                <td colspan="6" style="padding: 48px 24px; text-align: center;">
                    <div class="empty-state">
                        <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true" style="margin-bottom: 12px; opacity: 0.4;">
                            <circle cx="11" cy="11" r="8"></circle>
                            <line x1="21" y1="21" x2="16.65" y2="16.65"></line>
                        </svg>
                        <h4 style="margin: 0 0 4px; font-size: 16px; font-weight: 600;">No matching orders</h4>
                        <p class="text-muted" style="margin: 0; font-size: 13px;">Try adjusting your search or filter criteria.</p>
                    </div>
                </td>
            `;
            tableBody.appendChild(noResultsRow);
        } else if (!show && noResultsRow) {
            noResultsRow.remove();
        }
    }

    // Event listeners with debounced search
    const debouncedFilter = debounce(filterRows, 200);

    searchInput.addEventListener('input', debouncedFilter);
    statusFilter.addEventListener('change', filterRows);

    // Clear search on Escape key
    searchInput.addEventListener('keydown', (e) => {
        if (e.key === 'Escape') {
            searchInput.value = '';
            searchInput.blur();
            filterRows();
        }
    });

    // Initial filter in case URL has parameters
    filterRows();

    // Add CSS animation for fade in
    const style = document.createElement('style');
    style.textContent = `
        @keyframes fadeIn {
            from { opacity: 0; transform: translateY(-4px); }
            to { opacity: 1; transform: translateY(0); }
        }
        .no-results-row td {
            border: none !important;
        }
    `;
    document.head.appendChild(style);
})();
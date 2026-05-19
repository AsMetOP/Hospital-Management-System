/**
 * CUSTOM JAVASCRIPT — app.js
 * File: src/main/resources/static/js/app.js
 * JD REQUIREMENT: "responsive user interfaces using ... JavaScript"
 *
 * This file handles:
 *   1. Auto-dismiss flash alerts after 4 seconds
 *   2. Form submit loading state (prevent double submit)
 *   3. Confirm dialogs for destructive actions
 *   4. Set today's date as minimum for appointment date inputs
 */

document.addEventListener('DOMContentLoaded', function () {

    // ── 1. AUTO-DISMISS ALERTS ──────────────────────────────
    // Bootstrap 5 alerts with auto-close after 4 seconds
    const alerts = document.querySelectorAll('.alert.alert-dismissible');
    alerts.forEach(function (alert) {
        setTimeout(function () {
            // Use Bootstrap's Alert API to fade out
            const bsAlert = new bootstrap.Alert(alert);
            bsAlert.close();
        }, 4000);
    });

    // ── 2. FORM LOADING STATE (prevent double submit) ────────
    // When any form is submitted, disable the submit button
    // to prevent accidental duplicate submissions (F5 or double click)
    const forms = document.querySelectorAll('form');
    forms.forEach(function (form) {
        form.addEventListener('submit', function () {
            const submitBtn = form.querySelector('button[type="submit"]');
            if (submitBtn) {
                submitBtn.disabled = true;
                submitBtn.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>Processing...';
            }
        });
    });

    // ── 3. SET MIN DATE FOR APPOINTMENT DATE INPUT ───────────
    // Prevents booking appointments in the past
    const dateInputs = document.querySelectorAll('input[type="date"]');
    const today = new Date().toISOString().split('T')[0]; // "yyyy-MM-dd"
    dateInputs.forEach(function (input) {
        if (!input.hasAttribute('min')) {
            input.setAttribute('min', today);
        }
    });

    // ── 4. ACTIVE NAV LINK HIGHLIGHT ────────────────────────
    // Adds 'active' class to the current page's nav link
    const currentPath = window.location.pathname;
    const navLinks = document.querySelectorAll('.nav-link');
    navLinks.forEach(function (link) {
        const href = link.getAttribute('href');
        if (href && currentPath.startsWith(href) && href !== '/') {
            link.classList.add('active');
        }
    });

    // ── 5. TABLE ROW CLICK → NAVIGATE (optional UX) ─────────
    // Makes entire table row clickable if it has data-href attribute
    const clickableRows = document.querySelectorAll('tr[data-href]');
    clickableRows.forEach(function (row) {
        row.style.cursor = 'pointer';
        row.addEventListener('click', function () {
            window.location.href = row.getAttribute('data-href');
        });
    });

    console.log('✅ MediCare HMS — JavaScript loaded successfully');
});

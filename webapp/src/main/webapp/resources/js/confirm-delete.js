// Global variables to store the current deletion context
let currentFormId = '';
let currentDeleteType = '';


function openDeleteModal(formId, deleteType) {
    // Store the current context
    currentFormId = formId;
    currentDeleteType = deleteType;

    // Set the appropriate title and warning based on the delete type
    const modalTitle = document.getElementById('delete-modal-title');
    const modalWarning = document.getElementById('delete-modal-warning');

    // Get the appropriate i18n keys based on the delete type
    let titleKey, warningKey;

    switch(deleteType) {
        case 'event':
            titleKey = 'event.confirmDelete';
            warningKey = 'event.deleteWarning';
            break;
        case 'eventResponse':
            titleKey = 'eventResponse.confirmDelete';
            warningKey = 'eventResponse.deleteWarning';
            break;
        case 'journey':
            titleKey = 'journey.confirmDelete';
            warningKey = 'journey.deleteWarning';
            break;
        case 'journeyResponse':
            titleKey = 'journeyResponse.confirmDelete';
            warningKey = 'journeyResponse.deleteWarning';
            break;
        default:
            titleKey = 'item.confirmDelete';
            warningKey = 'item.deleteWarning';
    }

    // Get the messages from the server-rendered data attributes
    modalTitle.textContent = document.getElementById('i18n-' + titleKey).getAttribute('data-message');
    modalWarning.textContent = document.getElementById('i18n-' + warningKey).getAttribute('data-message');

    // Move the form to the modal
    const form = document.getElementById(formId);
    const formContainer = document.getElementById('delete-form-container');

    // Clear the container
    formContainer.innerHTML = '';

    // Clone the form and move it to the modal
    const formClone = form.cloneNode(true);
    formClone.style.display = 'block';
    formContainer.appendChild(formClone);

    // Show the modal
    document.getElementById('delete-modal').classList.add('active');
}

function closeDeleteModal() {
    document.getElementById('delete-modal').classList.remove('active');
}

function submitDelete() {
    // Get the form inside the modal
    const formContainer = document.getElementById('delete-form-container');
    const form = formContainer.querySelector('form');

    if (!form) {
        console.error('Form not found in modal');
        return;
    }

    // Submit the form
    form.submit();

    // Close the modal
    closeDeleteModal();
}
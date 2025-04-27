function openDeleteModal() {
    document.getElementById('delete-modal').classList.add('active') ;
}

function closeDeleteModal() {
    document.getElementById('delete-modal').classList.remove('active') ;
}

function submitDelete() {
    document.getElementById('delete-form').submit();
}
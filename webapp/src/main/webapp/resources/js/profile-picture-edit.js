// Initialize file upload component
document.addEventListener("DOMContentLoaded", () => {
    try {
        window.eventFileUpload = FileUpload.init({
            fileInputId: "eventFile",
            previewContainerId: "filePreview",
            previewImageId: "previewImage",
            fileNameId: "fileName",
            removeButtonId: "removeFile",
            maxSizeMB: 5,
            sizeExceededMessage: "File size exceeds 5MB limit",
        })
        console.log("File upload component initialized")
    } catch (error) {
        console.error("Failed to initialize file upload component:", error)
    }
})
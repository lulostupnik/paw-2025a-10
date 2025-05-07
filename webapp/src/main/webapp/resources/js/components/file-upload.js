/**
 * File Upload Component
 *
 * Handles file selection, preview, and validation
 */
let FileUpload = (() => {
    /**
     * Initialize file upload component
     * @param {Object} options Configuration options
     */
    function init(options = {}) {
        // Default configuration
        const config = {
            fileInputId: "profilePicture",
            previewContainerId: "filePreview",
            previewImageId: "previewImage",
            fileNameId: "fileName",
            removeButtonId: "removeFile",
            // maxSizeMB: 5,
            // sizeExceededMessage: "File size exceeds limit",
            onFileSelected: null,
            onFileRemoved: null,
            ...options,
        }

        // Get DOM elements
        const fileInput = document.getElementById(config.fileInputId)
        const previewContainer = document.getElementById(config.previewContainerId)
        const previewImage = document.getElementById(config.previewImageId)
        const fileName = document.getElementById(config.fileNameId)
        const removeButton = document.getElementById(config.removeButtonId)

        // Validate required elements
        if (!fileInput) {
            console.error("FileUpload: File input not found:", config.fileInputId)
            return null
        }

        if (!previewContainer) {
            console.error("FileUpload: Preview container not found:", config.previewContainerId)
            return null
        }

        if (!previewImage) {
            console.error("FileUpload: Preview image not found:", config.previewImageId)
            return null
        }

        if (!fileName) {
            console.error("FileUpload: File name element not found:", config.fileNameId)
            return null
        }

        if (!removeButton) {
            console.error("FileUpload: Remove button not found:", config.removeButtonId)
            return null
        }

        console.log("FileUpload: All elements found, initializing...")

        // Handle file selection
        fileInput.addEventListener("change", function (event) {
            // Check if files were selected (user might cancel the dialog)
            if (this.files && this.files.length > 0) {
                const file = this.files[0]
                console.log("FileUpload: File selected:", file.name, "Size:", file.size)

                // Update file name
                fileName.textContent = file.name

                // Create preview image
                const reader = new FileReader()

                reader.onload = (e) => {
                    try {
                        previewImage.src = e.target.result
                        previewContainer.style.display = "flex"
                        console.log("FileUpload: Preview generated successfully")

                        // Call onFileSelected callback if provided
                        if (typeof config.onFileSelected === "function") {
                            config.onFileSelected(file, e.target.result)
                        }
                    } catch (error) {
                        console.error("FileUpload: Error displaying preview", error)
                    }
                }

                /*reader.onerror = (error) => {
                    console.error("FileUpload: Error reading file", error)
                    alert("Error reading file. Please try again.")
                }*/

                reader.readAsDataURL(file)
            } else {
                console.log("FileUpload: No file selected or selection canceled")
            }
        })

        // Handle file removal
        removeButton.addEventListener("click", () => {
            console.log("FileUpload: Removing file")

            // Store the current file for the callback
            const currentFile = fileInput.files && fileInput.files[0] ? fileInput.files[0] : null

            // Clear the file input
            fileInput.value = ""
            previewContainer.style.display = "none"
            previewImage.src = "#"
            fileName.textContent = ""

            // Call onFileRemoved callback if provided
            if (typeof config.onFileRemoved === "function" && currentFile) {
                config.onFileRemoved(currentFile)
            }
        })

        // Public methods for this instance
        const publicMethods = {
            getFile: () => (fileInput.files && fileInput.files[0] ? fileInput.files[0] : null),

            clear: () => {
                console.log("FileUpload: Clearing file via API")
                fileInput.value = ""
                previewContainer.style.display = "none"
                previewImage.src = "#"
                fileName.textContent = ""
            },

            setFile: (file) => {
                if (file instanceof File) {
                    try {
                        // Create a DataTransfer object to set the file input value
                        const dataTransfer = new DataTransfer()
                        dataTransfer.items.add(file)
                        fileInput.files = dataTransfer.files

                        // Update file name
                        fileName.textContent = file.name

                        // Create preview image
                        const reader = new FileReader()
                        reader.onload = (e) => {
                            previewImage.src = e.target.result
                            previewContainer.style.display = "flex"
                        }
                        reader.readAsDataURL(file)
                        return true
                    } catch (error) {
                        console.error("FileUpload: Failed to set file programmatically", error)
                        return false
                    }
                }
                return false
            },

            hasFile: () => !!(fileInput.files && fileInput.files[0]),
        }

        // Store instance methods on the element for future reference
        fileInput._fileUpload = publicMethods

        console.log("FileUpload: Component initialized successfully")
        return publicMethods
    }

    // Public API
    return {
        init: init,
    }
})()

// Make available globally
window.FileUpload = FileUpload

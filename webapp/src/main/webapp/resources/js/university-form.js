document.addEventListener("DOMContentLoaded", () => {
    const universityForm = document.getElementById("universityForm");
    const nameInput = document.getElementById("name");
    const abbreviationInput = document.getElementById("abbreviation");
    const cityInput = document.getElementById("city");
    const citySearch = document.getElementById("citySearch");
    const cityDropdown = document.getElementById("cityDropdown");
    const selectedCity = document.getElementById("selectedCity");
    const cityItems = document.querySelectorAll("#cityDropdown .autocomplete-item");

    // Focus on the first field when the page loads
    nameInput.focus();

    // Auto-capitalize first letter of each word
    function capitalizeFirstLetter(input) {
        input.addEventListener("blur", function () {
            if (this.value) {
                this.value = this.value
                    .split(" ")
                    .map((word) => word.charAt(0).toUpperCase() + word.slice(1))
                    .join(" ");
            }
        });
    }

    capitalizeFirstLetter(nameInput);
    capitalizeFirstLetter(citySearch);

    const emptyMessage = document.getElementById("i18n-cities-none")
        ? document.getElementById("i18n-cities-none").value
        : "No cities selected"
    try {
        window.univesityCityAutocomplete = SingleOptionAutocomplete.init({
            selectId: "city",
            searchId: "citySearch",
            dropdownId: "cityDropdown",
            selectedContainerId: "selectedCity",
            apiEndpoint: `${apiBaseUrl}cities`,
            selectedValue: universitySelectedCity,
            minChars: 2,
            debounceTime: 300,
            emptyMessage: emptyMessage,
            multiSelect: false, // Single-select mode
            error: document.getElementById("city.errors") !== null,
        })
        console.log("City autocomplete component initialized")
    } catch (error) {
        console.error("Failed to initialize city autocomplete component:", error)
    }

});
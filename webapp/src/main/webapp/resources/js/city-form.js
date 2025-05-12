document.addEventListener("DOMContentLoaded", () => {
    const nameInput = document.getElementById("name");
    const countrySearch = document.getElementById("countrySearch");

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
    capitalizeFirstLetter(countrySearch);
    const emptyMessage = document.getElementById("country.emptyMessage") !== null
        ? document.getElementById("country.emptyMessage").value
        : "No countries found";

    window.countryAutocomplete = SingleOptionAutocomplete.init({
        selectId: "country",
        searchId: "countrySearch",
        dropdownId: "countryDropdown",
        selectedContainerId: "selectedCountry",
        minChars: 2,
        debounceTime: 300,
        emptyMessage: emptyMessage,
        multiSelect: false, // Single-select mode
        error: document.getElementById("city.errors") !== null,
    })

});
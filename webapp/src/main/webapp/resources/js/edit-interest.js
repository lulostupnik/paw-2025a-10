document.addEventListener("DOMContentLoaded", () => {
    try {
        const emptyMessage = document.getElementById("i18n-interests-none")
            ? document.getElementById("i18n-interests-none").value
            : "No interests selected"

        window.interestsAutocomplete = ListAutocomplete.init({
            selectId: "interestsSelect",
            searchId: "interestSearch",
            dropdownId: "interestDropdown",
            selectedContainerId: "selectedInterests",
            apiEndpoint: `${apiBaseUrl}interests`,
            selectedValue: previousInterests,
            minChars: 2,
            debounceTime: 300,
            emptyMessage: emptyMessage,
            multiSelect: true, // Keep multi-select mode for interests
            useId: true,
            error: document.getElementById("interests.errors") !== null,
        })
        console.log("Interests list autocomplete component initialized")
    } catch (error) {
        console.error("Failed to initialize interests list autocomplete component:", error)
    }
});

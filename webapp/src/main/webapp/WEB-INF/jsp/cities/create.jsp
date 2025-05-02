<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>

<html>
<head>
  <title>
    <c:choose>
      <c:when test="${isUpdate}">
        <spring:message code="editCity.title" text="Edit City"/>
      </c:when>
      <c:otherwise>
        <spring:message code="createCity.title" text="Create City"/>
      </c:otherwise>
    </c:choose>
  </title>
  <!-- Include custom CSS -->
  <link rel="stylesheet" href="<c:url value='/resources/css/main.css'/>" />
  <link rel="stylesheet" href="<c:url value='/resources/css/auth.css'/>" />
  <link rel="stylesheet" href="<c:url value='/resources/css/form-enhancements.css'/>" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <link rel="icon" type="image/svg+xml" href="<c:url value='/resources/images/favicon.svg'/>" />
  <link rel="alternate icon" href="<c:url value='/resources/images/favicon.ico'/>" type="image/x-icon" />
</head>
<body>
<jsp:include page="../components/navbar.jsp"/>
<jsp:include page="../components/i18n-hidden-inputs.jsp"/>

<div class="auth-container">
  <div class="auth-card">
    <div class="auth-header">
      <div class="auth-logo">
        <svg xmlns="http://www.w3.org/2000/svg" class="auth-logo-img" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4" />
        </svg>
      </div>
      <h1 class="auth-title">
        <c:choose>
          <c:when test="${isUpdate}">
            <spring:message code="editCity.title" text="Edit City"/>
          </c:when>
          <c:otherwise>
            <spring:message code="createCity.title" text="Create City"/>
          </c:otherwise>
        </c:choose>
      </h1>
      <p class="auth-subtitle">
        <c:choose>
          <c:when test="${isUpdate}">
            <spring:message code="editCity.subtitle" text="Update city information"/>
          </c:when>
          <c:otherwise>
            <spring:message code="createCity.subtitle" text="Add a new city to the system"/>
          </c:otherwise>
        </c:choose>
      </p>
    </div>

    <c:choose>
      <c:when test="${isUpdate}">
        <c:url var="formAction" value="/cities/${cityId}/edit"/>
      </c:when>
      <c:otherwise>
        <c:url var="formAction" value="/cities/create"/>
      </c:otherwise>
    </c:choose>

    <form:form modelAttribute="createCityForm" action="${formAction}" method="post" class="auth-form" id="cityForm" novalidate="true">
      <!-- City Name Field -->
      <div class="form-group">
        <form:label path="name" cssClass="form-label required-field">
          <spring:message code="createCity.name" text="City Name"/>
        </form:label>
        <form:input path="name" id="name" type="text" cssClass="form-input ${not empty errors.getFieldError('name') ? 'error' : ''}" />
        <form:errors path="name" cssClass="error-message" />
      </div>

      <!-- Country Field with Enhanced Autocomplete -->
      <div class="form-group">
        <form:label path="country" cssClass="form-label required-field">
          <spring:message code="createCity.country" text="Country"/>
        </form:label>
        <div class="autocomplete-wrapper">
          <form:select path="country" id="country" cssClass="form-select ${not empty errors.getFieldError('country') ? 'error' : ''}" style="display: none;">
            <form:option value=""><spring:message code="createCity.country.select" text="Select a country"/></form:option>
            <c:forEach var="item" items="${country}">
              <form:option value="${item.name}"><c:out value="${item.name}"/></form:option>
            </c:forEach>
          </form:select>
          <input type="text" id="countrySearch" class="autocomplete-input" placeholder="<spring:message code="createCity.country.search" text="Type to search country..."/>" />
          <div id="countryDropdown" class="autocomplete-dropdown" style="display: none;">
            <c:forEach var="item" items="${country}">
              <div class="autocomplete-item" data-value="<c:out value="${item.name}"/>">
                <c:out value="${item.name}"/>
              </div>
            </c:forEach>
          </div>
          <!-- Container for selected country tag -->
          <div id="selectedCountry" class="selected-tags"></div>
        </div>
        <form:errors path="country" cssClass="error-message" />
      </div>

      <button type="submit" class="form-button">
        <c:choose>
          <c:when test="${isUpdate}">
            <spring:message code="editCity.submit" text="Update City"/>
          </c:when>
          <c:otherwise>
            <spring:message code="createCity.submit" text="Create City"/>
          </c:otherwise>
        </c:choose>
      </button>
    </form:form>

    <div class="auth-footer">
      <button type="button" name="back" class="auth-link" onClick="history.back()">
        <spring:message code="city.back" text="Back to cities"/>
      </button>
    </div>
  </div>
</div>

<!-- Include JavaScript files -->
<script>
  document.addEventListener("DOMContentLoaded", () => {
    const cityForm = document.getElementById("cityForm");
    const nameInput = document.getElementById("name");
    const countryInput = document.getElementById("country");
    const countrySearch = document.getElementById("countrySearch");
    const countryDropdown = document.getElementById("countryDropdown");
    const selectedCountry = document.getElementById("selectedCountry");
    const countryItems = document.querySelectorAll("#countryDropdown .autocomplete-item");

    // Check if we're in update mode
    const isUpdateMode = ${isUpdate != null && isUpdate ? 'true' : 'false'};

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

    // Initialize country autocomplete
    function initCountryAutocomplete() {
      // If there's a selected country (especially in update mode), show it
      if (countryInput.value) {
        displaySelectedCountry(countryInput.value);
      }

      // Show dropdown when focusing on search input
      countrySearch.addEventListener("focus", function() {
        countryDropdown.style.display = "block";
      });

      // Filter countries as user types
      countrySearch.addEventListener("input", function() {
        const searchValue = this.value.toLowerCase();
        let hasVisibleItems = false;

        countryItems.forEach(item => {
          const countryName = item.getAttribute("data-value").toLowerCase();
          if (countryName.includes(searchValue)) {
            item.style.display = "block";
            hasVisibleItems = true;
          } else {
            item.style.display = "none";
          }
        });

        countryDropdown.style.display = hasVisibleItems ? "block" : "none";
      });

      // Handle country selection
      countryItems.forEach(item => {
        item.addEventListener("click", function() {
          const selectedValue = this.getAttribute("data-value");
          countryInput.value = selectedValue;
          countrySearch.value = "";
          countryDropdown.style.display = "none";
          displaySelectedCountry(selectedValue);
        });
      });

      // Close dropdown when clicking outside
      document.addEventListener("click", function(e) {
        if (!countrySearch.contains(e.target) && !countryDropdown.contains(e.target)) {
          countryDropdown.style.display = "none";
        }
      });
    }

    // Display selected country as a tag
    function displaySelectedCountry(countryName) {
      selectedCountry.innerHTML = "";

      if (countryName) {
        const tag = document.createElement("div");
        tag.className = "selected-tag";

        const tagText = document.createElement("span");
        tagText.textContent = countryName;

        const removeBtn = document.createElement("button");
        removeBtn.type = "button";
        removeBtn.className = "remove-tag";
        removeBtn.innerHTML = "×";
        removeBtn.addEventListener("click", function() {
          countryInput.value = "";
          selectedCountry.innerHTML = "";
        });

        tag.appendChild(tagText);
        tag.appendChild(removeBtn);
        selectedCountry.appendChild(tag);
      }
    }

    // Initialize autocomplete
    initCountryAutocomplete();

    // Form validation
    cityForm.addEventListener("submit", (event) => {
      let isValid = true;

      // Validate name
      if (!nameInput.value.trim()) {
        const errorElement = document.createElement("div");
        errorElement.className = "error-message";
        errorElement.textContent = "City name is required";

        const existingError = nameInput.parentNode.querySelector(".error-message");
        if (!existingError) {
          nameInput.parentNode.appendChild(errorElement);
        }

        nameInput.classList.add("error");
        isValid = false;
      } else {
        nameInput.classList.remove("error");
        const existingError = nameInput.parentNode.querySelector(".error-message");
        if (existingError) {
          existingError.remove();
        }
      }

      // Validate country
      if (!countryInput.value.trim()) {
        const errorElement = document.createElement("div");
        errorElement.className = "error-message";
        errorElement.textContent = "Country is required";

        const existingError = countryInput.parentNode.parentNode.querySelector(".error-message");
        if (!existingError) {
          countryInput.parentNode.parentNode.appendChild(errorElement);
        }

        countrySearch.classList.add("error");
        isValid = false;
      } else {
        countrySearch.classList.remove("error");
        const existingError = countryInput.parentNode.parentNode.querySelector(".error-message");
        if (existingError) {
          existingError.remove();
        }
      }

      if (!isValid) {
        event.preventDefault();
      }
    });
  });
</script>

</body>
</html>
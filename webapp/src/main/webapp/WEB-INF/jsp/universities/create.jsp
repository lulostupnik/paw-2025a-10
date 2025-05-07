<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<html lang="${pageContext.response.locale}">
<head>
  <title>
    <c:choose>
      <c:when test="${isUpdate}">
        <spring:message code="updateUniversity.title"/>
      </c:when>
      <c:otherwise>
        <spring:message code="createUniversity.title"/>
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
            <spring:message code="updateUniversity.title" text="Update University"/>
          </c:when>
          <c:otherwise>
            <spring:message code="createUniversity.title"/>
          </c:otherwise>
        </c:choose>
      </h1>
      <p class="auth-subtitle">
        <c:choose>
          <c:when test="${isUpdate}">
            <spring:message code="updateUniversity.subtitle" text="Update university information"/>
          </c:when>
          <c:otherwise>
            <spring:message code="createUniversity.subtitle" text="Add a new university to the system"/>
          </c:otherwise>
        </c:choose>
      </p>
    </div>

    <c:choose>
      <c:when test="${isUpdate}">
        <c:url var="formAction" value="/universities/${universityId}/edit"/>
      </c:when>
      <c:otherwise>
        <c:url var="formAction" value="/universities/create"/>
      </c:otherwise>
    </c:choose>

    <form:form modelAttribute="createUniversityForm" action="${formAction}" method="post" class="auth-form" id="universityForm" novalidate="true">
      <!-- Name Field -->
      <div class="form-group">
        <form:label path="name" cssClass="form-label required-field">
          <spring:message code="createUniversity.name"/>
        </form:label>
        <form:input path="name" id="name" type="text" cssClass="form-input ${not empty errors.getFieldError('name') ? 'error' : ''}" />
        <form:errors path="name" cssClass="error-message" />
      </div>

      <!-- Abbreviation Field -->
      <div class="form-group">
        <form:label path="abbreviation" cssClass="form-label required-field">
          <spring:message code="createUniversity.abbreviation"/>
        </form:label>
        <form:input path="abbreviation" id="abbreviation" type="text" cssClass="form-input ${not empty errors.getFieldError('abbreviation') ? 'error' : ''}" />
        <form:errors path="abbreviation" cssClass="error-message" />
      </div>

      <!-- City Field with Enhanced Autocomplete -->
      <div class="form-group">
        <form:label path="city" cssClass="form-label required-field">
          <spring:message code="createUniversity.city"/>
        </form:label>
        <div class="autocomplete-wrapper">
          <form:select path="city" id="city" cssClass="form-select ${not empty errors.getFieldError('city') ? 'error' : ''}" style="display: none;">
            <form:option value=""><spring:message code="createUniversity.city.select"/></form:option>
            <c:forEach var="item" items="${cities}">
              <form:option value="${item.name}"><c:out value="${item.name}"/></form:option>
            </c:forEach>
          </form:select>
          <input type="text" id="citySearch" class="form-input autocomplete-input" placeholder="<spring:message code="createUniversity.city.search" text="Type to search city..."/>" />
          <div id="cityDropdown" class="autocomplete-dropdown" style="display: none;">
            <c:forEach var="item" items="${cities}">
              <div class="autocomplete-item" data-value="<c:out value="${item.name}"/>">
                <c:out value="${item.name}"/>
              </div>
            </c:forEach>
          </div>
          <!-- Container for selected city tag -->
          <div id="selectedCity" class="selected-tags"></div>
        </div>
        <form:errors path="city" cssClass="error-message" />
      </div>

      <button type="submit" class="form-button">
        <c:choose>
          <c:when test="${isUpdate}">
            <spring:message code="updateUniversity.submit" text="Update University"/>
          </c:when>
          <c:otherwise>
            <spring:message code="createUniversity.submit"/>
          </c:otherwise>
        </c:choose>
      </button>
    </form:form>

    <div class="auth-footer">
      <button type="button" name="back" class="auth-link" onClick="history.back()">
        <spring:message code="university.back" text="Back to universities"/>
      </button>
    </div>
  </div>
</div>

<!-- Include JavaScript files -->
<script>
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

    // Initialize city autocomplete
    function initCityAutocomplete() {
      // If there's a selected city value, display it
      if (cityInput.value && cityInput.value.trim() !== "") {
        displaySelectedCity(cityInput.value);
      }

      // Show dropdown when focusing on search input
      citySearch.addEventListener("focus", function() {
        cityDropdown.style.display = "block";
      });

      // Filter cities as user types
      citySearch.addEventListener("input", function() {
        const searchValue = this.value.toLowerCase();
        let hasVisibleItems = false;

        cityItems.forEach(item => {
          const cityName = item.getAttribute("data-value").toLowerCase();
          if (cityName.includes(searchValue)) {
            item.style.display = "block";
            hasVisibleItems = true;
          } else {
            item.style.display = "none";
          }
        });

        cityDropdown.style.display = hasVisibleItems ? "block" : "none";
      });

      // Handle city selection
      cityItems.forEach(item => {
        item.addEventListener("click", function() {
          const selectedValue = this.getAttribute("data-value");
          cityInput.value = selectedValue;
          citySearch.value = "";
          cityDropdown.style.display = "none";
          displaySelectedCity(selectedValue);
        });
      });

      // Close dropdown when clicking outside
      document.addEventListener("click", function(e) {
        if (!citySearch.contains(e.target) && !cityDropdown.contains(e.target) && !selectedCity.contains(e.target)) {
          cityDropdown.style.display = "none";
        }
      });
    }

    // Display selected city as a tag
    function displaySelectedCity(cityName) {
      selectedCity.innerHTML = "";

      if (cityName) {
        const tag = document.createElement("div");
        tag.className = "selected-tag";

        const tagText = document.createElement("span");
        tagText.textContent = cityName;

        const removeBtn = document.createElement("button");
        removeBtn.type = "button";
        removeBtn.className = "remove-tag";
        removeBtn.innerHTML = "×";
        removeBtn.addEventListener("click", function(e) {
          e.preventDefault();
          e.stopPropagation();
          cityInput.value = "";
          selectedCity.innerHTML = "";
        });

        tag.appendChild(tagText);
        tag.appendChild(removeBtn);
        selectedCity.appendChild(tag);
      }
    }

    // Initialize autocomplete
    initCityAutocomplete();

    // Form validation
    universityForm.addEventListener("submit", (event) => {
      let isValid = true;

      // Validate name
      if (!nameInput.value.trim()) {
        const errorElement = document.createElement("div");
        errorElement.className = "error-message";
        errorElement.textContent = "University name is required";

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

      // Validate abbreviation
      if (!abbreviationInput.value.trim()) {
        const errorElement = document.createElement("div");
        errorElement.className = "error-message";
        errorElement.textContent = "Abbreviation is required";

        const existingError = abbreviationInput.parentNode.querySelector(".error-message");
        if (!existingError) {
          abbreviationInput.parentNode.appendChild(errorElement);
        }

        abbreviationInput.classList.add("error");
        isValid = false;
      } else {
        abbreviationInput.classList.remove("error");
        const existingError = abbreviationInput.parentNode.querySelector(".error-message");
        if (existingError) {
          existingError.remove();
        }
      }

      // Validate city
      if (!cityInput.value.trim()) {
        const errorElement = document.createElement("div");
        errorElement.className = "error-message";
        errorElement.textContent = "City is required";

        const existingError = document.querySelector(".form-group:nth-child(3) .error-message");
        if (!existingError) {
          document.querySelector(".form-group:nth-child(3)").appendChild(errorElement);
        }

        citySearch.classList.add("error");
        isValid = false;
      } else {
        citySearch.classList.remove("error");
        const existingError = document.querySelector(".form-group:nth-child(3) .error-message");
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
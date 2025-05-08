<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<!DOCTYPE html>
<html lang="${pageContext.response.locale}">
<head>
  <title><spring:message code="event.page.title"/></title>
  <link rel="stylesheet" href="<c:url value="/resources/css/main.css"/>" />
  <link rel="icon" type="image/svg+xml" href="<c:url value='/resources/images/favicon.svg'/>" />
  <link rel="alternate icon" href="<c:url value='/resources/images/favicon.ico'/>" type="image/x-icon" />
  <link rel="stylesheet" href="<c:url value='/resources/css/auth.css'/>" />
</head>
<body>
<jsp:include page="../components/i18n-hidden-inputs.jsp"/>
<c:set var="searchUrl" value="/events" scope="request" />
<c:set var="searchPlaceholderCode" value="events.search.event" scope="request" />

<div class="layout-container">
  <!-- Main Content -->
  <div class="main-content">
    <jsp:include page="../components/navbar.jsp" />
    <div class="content-container">
      <div class="header-container">
        <h2 class="page-title">
          <spring:message code="event.list.title"/>
        </h2>
        <div class="journeys-actions">
          <form action="<c:url value='${searchUrl}'/>" method="get" class="search-form">
            <input type="text" name="search" class="search-input"
                   placeholder="<spring:message code='${searchPlaceholderCode}' />"
                   value="<c:out value="${param.search}"/>">
            <input type="hidden" name="page" value="1">
            <input type="hidden" name="pageSize" value="${param.pageSize != null ? param.pageSize : 10}">

            <!-- Preserve sort parameters -->
            <c:if test="${not empty param.sort}">
              <input type="hidden" name="sort" value="<c:out value="${param.sort}"/>">
            </c:if>
            <c:if test="${not empty param.direction}">
              <input type="hidden" name="direction" value="<c:out value="${param.direction}"/>">
            </c:if>

            <!-- Preserve filter parameters - Fixed field names to match the filter form -->
            <c:if test="${not empty param.destination}">
              <input type="hidden" name="destination" value="<c:out value="${param.destination}"/>">
            </c:if>
            <c:if test="${not empty param.cityName}">
              <input type="hidden" name="cityName" value="<c:out value="${param.cityName}"/>">
            </c:if>
            <c:if test="${not empty param.startDate}">
              <input type="hidden" name="startDate" value="<c:out value="${param.startDate}"/>">
            </c:if>
            <c:if test="${not empty param.endDate}">
              <input type="hidden" name="endDate" value="<c:out value="${param.endDate}"/>">
            </c:if>
            <c:if test="${not empty param.interests}">
              <input type="hidden" name="interests" value="<c:out value="${param.interests}"/>">
            </c:if>
            <c:if test="${not empty param.interestName}">
              <input type="hidden" name="interestName" value="<c:out value="${param.interestName}"/>">
            </c:if>

            <button type="submit" class="btn-secondary" aria-label="<spring:message code="admin.search.button" />">
              <img src="<c:url value='/resources/icons/search.svg'/>" alt="<spring:message code="admin.search.button" />" class="search-icon" />
            </button>
          </form>
          <button id="filterToggleBtn" class="btn-secondary btn-with-icon">
            <img src="<c:url value='/resources/icons/filter.svg'/>" alt="<spring:message code="event.filter.toggle"/>" class="btn-icon filter-icon" />
            <img src="<c:url value='/resources/icons/x.svg'/>" alt="<spring:message code="event.filter.close"/>" class="btn-icon close-icon" style="display: none;" />
            <spring:message code="event.filter.toggle"/>
          </button>
          <div class="sort-dropdown">
            <button id="sortToggleBtn" class="btn-secondary btn-with-icon">
              <img src="<c:url value='/resources/icons/sort.svg'/>" alt="<spring:message code="event.sort.toggle"/>" class="btn-icon" />
              <spring:message code="event.sort.toggle"/>
            </button>
            <div id="sortDropdown" class="dropdown-content" style="display: none;">
              <!-- Updated sort links to use the correct parameter names -->
              <a href="<c:url value="/events?sort=date&direction=asc${not empty param.search ? '&search='.concat(param.search) : ''}${not empty param.destination ? '&destination='.concat(param.destination) : ''}${not empty param.cityName ? '&cityName='.concat(param.cityName) : ''}${not empty param.startDate ? '&startDate='.concat(param.startDate) : ''}${not empty param.endDate ? '&endDate='.concat(param.endDate) : ''}${not empty param.interests ? '&interests='.concat(param.interests) : ''}${not empty param.interestName ? '&interestName='.concat(param.interestName) : ''}${not empty param.page ? '&page='.concat(param.page) : ''}${not empty param.pageSize ? '&pageSize='.concat(param.pageSize) : ''}"/>" class="${param.sort == 'date' && param.direction == 'asc' ? 'active' : ''}">
                <spring:message code="event.sort.date.asc"/>
              </a>
              <a href="<c:url value="/events?sort=date&direction=desc${not empty param.search ? '&search='.concat(param.search) : ''}${not empty param.destination ? '&destination='.concat(param.destination) : ''}${not empty param.cityName ? '&cityName='.concat(param.cityName) : ''}${not empty param.startDate ? '&startDate='.concat(param.startDate) : ''}${not empty param.endDate ? '&endDate='.concat(param.endDate) : ''}${not empty param.interests ? '&interests='.concat(param.interests) : ''}${not empty param.interestName ? '&interestName='.concat(param.interestName) : ''}${not empty param.page ? '&page='.concat(param.page) : ''}${not empty param.pageSize ? '&pageSize='.concat(param.pageSize) : ''}"/>" class="${param.sort == 'date' && param.direction == 'desc' ? 'active' : ''}">
                <spring:message code="event.sort.date.desc"/>
              </a>
              <a href="<c:url value="/events?sort=attendees&direction=desc${not empty param.search ? '&search='.concat(param.search) : ''}${not empty param.destination ? '&destination='.concat(param.destination) : ''}${not empty param.cityName ? '&cityName='.concat(param.cityName) : ''}${not empty param.startDate ? '&startDate='.concat(param.startDate) : ''}${not empty param.endDate ? '&endDate='.concat(param.endDate) : ''}${not empty param.interests ? '&interests='.concat(param.interests) : ''}${not empty param.interestName ? '&interestName='.concat(param.interestName) : ''}${not empty param.page ? '&page='.concat(param.page) : ''}${not empty param.pageSize ? '&pageSize='.concat(param.pageSize) : ''}"/>" class="${param.sort == 'attendees' && param.direction == 'desc' ? 'active' : ''}">
                <spring:message code="event.sort.attendees"/>
              </a>
              <a href="<c:url value="/events?sort=city&direction=asc${not empty param.search ? '&search='.concat(param.search) : ''}${not empty param.destination ? '&destination='.concat(param.destination) : ''}${not empty param.cityName ? '&cityName='.concat(param.cityName) : ''}${not empty param.startDate ? '&startDate='.concat(param.startDate) : ''}${not empty param.endDate ? '&endDate='.concat(param.endDate) : ''}${not empty param.interests ? '&interests='.concat(param.interests) : ''}${not empty param.interestName ? '&interestName='.concat(param.interestName) : ''}${not empty param.page ? '&page='.concat(param.page) : ''}${not empty param.pageSize ? '&pageSize='.concat(param.pageSize) : ''}"/>" class="${param.sort == 'city' && param.direction == 'asc' ? 'active' : ''}">
                <spring:message code="event.sort.city"/>
              </a>
              <a href="<c:url value="/events?sort=interest&direction=asc${not empty param.search ? '&search='.concat(param.search) : ''}${not empty param.destination ? '&destination='.concat(param.destination) : ''}${not empty param.cityName ? '&cityName='.concat(param.cityName) : ''}${not empty param.startDate ? '&startDate='.concat(param.startDate) : ''}${not empty param.endDate ? '&endDate='.concat(param.endDate) : ''}${not empty param.interests ? '&interests='.concat(param.interests) : ''}${not empty param.interestName ? '&interestName='.concat(param.interestName) : ''}${not empty param.page ? '&page='.concat(param.page) : ''}${not empty param.pageSize ? '&pageSize='.concat(param.pageSize) : ''}"/>" class="${param.sort == 'interest' && param.direction == 'asc' ? 'active' : ''}">
                <spring:message code="event.sort.interest"/>
              </a>
            </div>
          </div>
          <a href="<c:url value="/events/create"/>" class="btn btn-primary btn-with-icon">
            <img src="<c:url value='/resources/icons/plus.svg'/>" alt="<spring:message code="journey.create.button"/>" class="btn-icon" />
            <spring:message code="event.create.button"/>
          </a>
        </div>
      </div>

      <!-- Filter Section - Initially Hidden -->
      <div id="filterSection" class="filter-section hidden">
        <h3 class="filter-title">
          <spring:message code="event.filter.title"/>
        </h3>
        <c:set var="actionGet"><c:url value="/events"/></c:set>
        <form:form action="${actionGet}" method="GET"
                   modelAttribute="filterEventForm"
                   class="filter-form" id="eventFilterForm">

          <div class="filter-grid">
            <!-- City filter with autocomplete -->
            <div class="filter-item">
              <c:set var="cityLabel"><spring:message code="createJourney.city"/></c:set>
              <form:label for="citySearch" class="form-label" path="destination">${cityLabel}</form:label>
              <div class="autocomplete-wrapper">
                <input type="text" id="citySearch" class="autocomplete-input"
                       placeholder="<spring:message code='event.filter.city.placeholder'/>"
                       value="${param.cityName}" />
                <form:select id="city" name="destination" class="hidden-select" path="destination" style="display: none;">
                  <option value=""></option>
                  <c:forEach var="city" items="${cities}">
                    <option value="${city.id}" ${param.destination == city.id ? 'selected' : ''}><c:out value="${city.name}"/></option>
                  </c:forEach>
                </form:select>
                <div id="cityDropdown" class="autocomplete-dropdown" style="display: none;">
                  <c:forEach var="city" items="${cities}">
                    <div class="autocomplete-item" data-value="${city.id}"><c:out value="${city.name}"/></div>
                  </c:forEach>
                </div>
                <div id="citySelectedContainer" class="selected-tags"></div>
              </div>
              <form:errors path="destination" cssClass="error-message" />
            </div>

            <div class="filter-item">
              <c:set var="afterDateFilter"><spring:message code="event.filter.afterDate"/></c:set>
              <jsp:include page="../components/date-field.jsp">
                <jsp:param name="path" value="startDate"/>
                <jsp:param name="label" value="${afterDateFilter}"/>
              </jsp:include>
            </div>

            <div class="filter-item">
              <c:set var="beforeDateFilter"><spring:message code="event.filter.beforeDate"/></c:set>
              <jsp:include page="../components/date-field.jsp">
                <jsp:param name="path" value="endDate"/>
                <jsp:param name="label" value="${beforeDateFilter}"/>
              </jsp:include>
              <form:errors path="" cssClass="error-message" />
            </div>

            <!-- Interest filter with autocomplete -->
            <div class="filter-item">
              <c:set var="interestsLabel"><spring:message code="event.filter.interest"/></c:set>
              <form:label for="interest-search" class="form-label" path="interests">${interestsLabel}</form:label>
              <div class="autocomplete-wrapper">
                <input type="text" id="interest-search" class="autocomplete-input"
                       placeholder="<spring:message code='event.filter.interest.placeholder'/>"
                       value="${param.interestName}" />
                <form:select path="interests" id="interest-select" name="interests" class="hidden-select" style="display: none;">
                  <option value=""></option>
                  <c:forEach var="interest" items="${interests}">
                    <option value="${interest.id}" ${param.interests == interest.id ? 'selected' : ''}><c:out value="${interest.name}"/></option>
                  </c:forEach>
                </form:select>
                <div id="interest-dropdown" class="autocomplete-dropdown" style="display: none;">
                  <c:forEach var="interest" items="${interests}">
                    <div class="autocomplete-item" data-value="${interest.id}"><c:out value="${interest.name}"/></div>
                  </c:forEach>
                </div>
                <div id="interestSelectedContainer" class="selected-tags"></div>
              </div>
              <form:errors path="interests" cssClass="error-message" />
            </div>
          </div>

          <!-- Preserve search parameter -->
          <c:if test="${not empty param.search}">
            <input type="hidden" name="search" value="<c:out value="${param.search}"/>">
          </c:if>

          <!-- Preserve sort parameters -->
          <c:if test="${not empty param.sort}">
            <input type="hidden" name="sort" value="<c:out value="${param.sort}"/>">
          </c:if>
          <c:if test="${not empty param.direction}">
            <input type="hidden" name="direction" value="<c:out value="${param.direction}"/>">
          </c:if>

          <!-- Preserve pagination parameters -->
          <input type="hidden" name="page" value="1">
          <c:if test="${not empty param.pageSize}">
            <input type="hidden" name="pageSize" value="<c:out value="${param.pageSize}"/>">
          </c:if>

          <div class="filter-actions">
            <button type="button" id="resetFiltersBtn" class="btn-danger btn-with-icon">
              <img src="<c:url value='/resources/icons/x.svg'/>" alt="<spring:message code="event.filter.reset"/>" class="btn-icon" />
              <spring:message code="event.filter.reset"/>
            </button>
            <button type="submit" class="btn-secondary btn-with-icon">
              <img src="<c:url value='/resources/icons/filter.svg'/>" alt="<spring:message code="event.filter.button"/>" class="btn-icon" />
              <spring:message code="event.filter.button"/>
            </button>
          </div>
        </form:form>
      </div>

      <!-- Events Grid -->
      <div class="events-container">
        <div class="events-grid">
          <c:if test="${empty eventsWithAttendance}">
            <div class="empty-state">
              <p class="empty-message"><spring:message code="event.no.events"/></p>
            </div>
          </c:if>
          <c:forEach items="${eventsWithAttendance}" var="eventAttendance">
            <jsp:include page="event-card.jsp">
              <jsp:param name="eventId" value="${eventAttendance.event.id}" />
              <jsp:param name="city" value="${eventAttendance.event.eventCity.name}" />
              <jsp:param name="date" value="${eventAttendance.event.date}" />
              <jsp:param name="username" value="${eventAttendance.event.user.username}"/>
              <jsp:param name="description" value="${eventAttendance.event.description}" />
              <jsp:param name="flyerImageId" value="${eventAttendance.event.flyerImageId}" />
              <jsp:param name="attend" value="${eventAttendance.attending}" />
              <jsp:param name="firstname" value="${eventAttendance.event.user.firstname}" />
              <jsp:param name="lastname" value="${eventAttendance.event.user.lastname}"/>
              <jsp:param name="title" value="${eventAttendance.event.title}"/>
              <jsp:param name="isFull" value="${eventAttendance.event.attendeesLimit.isPresent() && eventAttendance.event.attendeesLimit.get() <= eventAttendance.event.attendeesCount}"/>
            </jsp:include>
          </c:forEach>
        </div>
        <jsp:include page="/WEB-INF/jsp/components/pagination-with-page-number.jsp">
          <jsp:param name="pageObjectTotalPages" value="${eventsPage.totalPages}" />
          <jsp:param name="currentPage" value="${currentPage}" />
          <jsp:param name="pageSize" value="${pageSize}" />
          <jsp:param name="baseUrl" value="/events" />
        </jsp:include>
      </div>
    </div>
  </div>
</div>

<script src="<c:url value='/resources/js/components/list-autocomplete.js'/>"></script>
<script>
  document.addEventListener('DOMContentLoaded', function() {
    // Initialize filter toggle
    const filterToggleBtn = document.getElementById('filterToggleBtn');
    const filterSection = document.getElementById('filterSection');
    const filterForm = document.getElementById('eventFilterForm');
    const filterIcon = filterToggleBtn.querySelector('.filter-icon');
    const closeIcon = filterToggleBtn.querySelector('.close-icon');

    // Check if there are any filter parameters in the URL
    const urlParams = new URLSearchParams(window.location.search);
    if (urlParams.has('destination') || urlParams.has('startDate') ||
            urlParams.has('endDate') || urlParams.has('interests')) {
      // Show filter section if filters are applied
      filterSection.classList.remove('hidden');
      // Update icons
      filterIcon.style.display = 'none';
      closeIcon.style.display = 'inline';
    }

    // Toggle filter section visibility
    filterToggleBtn.addEventListener('click', function() {
      filterSection.classList.toggle('hidden');

      // Toggle icons
      if (filterSection.classList.contains('hidden')) {
        filterIcon.style.display = 'inline';
        closeIcon.style.display = 'none';
      } else {
        filterIcon.style.display = 'none';
        closeIcon.style.display = 'inline';
      }

      // Optional: Animate the toggle button
      this.classList.toggle('active');
    });

    // Sort dropdown toggle
    const sortToggleBtn = document.getElementById('sortToggleBtn');
    const sortDropdown = document.getElementById('sortDropdown');

    sortToggleBtn.addEventListener('click', function(e) {
      e.stopPropagation();
      sortDropdown.style.display = sortDropdown.style.display === 'block' ? 'none' : 'block';
    });

    // Close dropdown when clicking outside
    document.addEventListener('click', function(e) {
      if (!sortToggleBtn.contains(e.target) && !sortDropdown.contains(e.target)) {
        sortDropdown.style.display = 'none';
      }
    });

    // City Autocomplete
    initAutocomplete('citySearch', 'cityDropdown', 'city', 'citySelectedContainer', false);

    // Interest Autocomplete
    initAutocomplete('interest-search', 'interest-dropdown', 'interest-select', 'interestSelectedContainer', false);

    // Initialize with any pre-selected values
    initializeSelectedValues();

    // Reset button functionality
    const resetFiltersBtn = document.getElementById('resetFiltersBtn');
    if (resetFiltersBtn) {
      resetFiltersBtn.addEventListener('click', function(e) {
        e.preventDefault(); // Prevent default button behavior

        // Build URL with only sort parameters preserved
        let resetUrl = '<c:url value="/events"/>';
        const sortParam = urlParams.get('sort');
        const directionParam = urlParams.get('direction');
        const searchParam = urlParams.get('search');
        const pageSizeParam = urlParams.get('pageSize');

        // Start with question mark if we have parameters
        let hasParam = false;

        if (sortParam) {
          resetUrl += (hasParam ? '&' : '?') + 'sort=' + sortParam;
          hasParam = true;
        }

        if (directionParam) {
          resetUrl += (hasParam ? '&' : '?') + 'direction=' + directionParam;
          hasParam = true;
        }

        if (searchParam) {
          resetUrl += (hasParam ? '&' : '?') + 'search=' + searchParam;
          hasParam = true;
        }

        if (pageSizeParam) {
          resetUrl += (hasParam ? '&' : '?') + 'pageSize=' + pageSizeParam;
          hasParam = true;
        }

        // Always reset to page 1
        resetUrl += (hasParam ? '&' : '?') + 'page=1';

        // Navigate to the reset URL
        window.location.href = resetUrl;
      });
    }

    // Function to initialize autocomplete
    function initAutocomplete(inputId, dropdownId, selectId, containerid, multiSelect) {
      const input = document.getElementById(inputId);
      const dropdown = document.getElementById(dropdownId);
      const select = document.getElementById(selectId);
      const selectedContainer = document.getElementById(containerid);
      const options = dropdown.querySelectorAll('.autocomplete-item');

      // Show dropdown on input focus
      input.addEventListener('focus', function() {
        dropdown.style.display = 'block';
        filterOptions(this.value);
      });

      // Show dropdown when clicking on input
      input.addEventListener('click', function(e) {
        e.stopPropagation();
        dropdown.style.display = 'block';
        filterOptions(this.value);
      });

      // Hide dropdown when clicking outside
      document.addEventListener('click', function(e) {
        if (!input.contains(e.target) && !dropdown.contains(e.target)) {
          dropdown.style.display = 'none';
        }
      });

      // Filter options as user types
      input.addEventListener('input', function() {
        filterOptions(this.value);
        dropdown.style.display = 'block';
      });

      // Handle option selection
      options.forEach(option => {
        option.addEventListener('click', function() {
          const value = this.dataset.value;
          const text = this.textContent.trim();

          // For single select, clear previous selection
          if (!multiSelect) {
            // Clear all options
            Array.from(select.options).forEach(opt => {
              opt.selected = false;
            });

            // Clear selected container
            selectedContainer.innerHTML = '';
          }

          // Find and select the option
          Array.from(select.options).forEach(opt => {
            if (opt.value === value) {
              opt.selected = true;
            }
          });

          // Update input and selected container
          input.value = '';

          // Create selected tag
          const tag = document.createElement('div');
          tag.className = 'selected-tag';
          tag.innerHTML = text;

          // Add remove button for tag
          const removeBtn = document.createElement('button');
          removeBtn.type = 'button';
          removeBtn.className = 'tag-remove';
          removeBtn.innerHTML = '<img src="<c:url value='/resources/icons/x.svg'/>" width="12" height="12"/>';
          removeBtn.addEventListener('click', function() {
            // Deselect the option
            Array.from(select.options).forEach(opt => {
              if (opt.value === value) {
                opt.selected = false;
              }
            });

            // Remove the tag
            tag.remove();
          });

          tag.appendChild(removeBtn);
          selectedContainer.appendChild(tag);

          // Hide dropdown
          dropdown.style.display = 'none';
        });
      });

      // Filter dropdown options based on search text
      function filterOptions(searchText) {
        const filter = searchText.toLowerCase();
        let hasResults = false;

        options.forEach(option => {
          const text = option.textContent.toLowerCase();
          if (text.includes(filter)) {
            option.style.display = '';
            hasResults = true;
          } else {
            option.style.display = 'none';
          }
        });

        const noResultsTxt = document.getElementById("i18n-results-match-none")
                ? document.getElementById("i18n-results-match-none").value
                : "No matching results found"

        // Show no results message if needed
        let noResultsMsg = dropdown.querySelector('.no-results');
        if (!hasResults) {
          if (!noResultsMsg) {
            noResultsMsg = document.createElement('div');
            noResultsMsg.className = 'autocomplete-item no-results';
            noResultsMsg.textContent = noResultsTxt;
            dropdown.appendChild(noResultsMsg);
          }
          noResultsMsg.style.display = '';
        } else if (noResultsMsg) {
          noResultsMsg.style.display = 'none';
        }
      }
    }

    // Initialize selected values from URL parameters
    function initializeSelectedValues() {
      // City
      const citySelect = document.getElementById('city');
      const citySelectedContainer = document.getElementById('citySelectedContainer');

      if (citySelect.value) {
        const selectedOption = Array.from(citySelect.options).find(opt => opt.selected);
        if (selectedOption) {
          const tag = document.createElement('div');
          tag.className = 'selected-tag';
          tag.innerHTML = selectedOption.textContent;

          const removeBtn = document.createElement('button');
          removeBtn.type = 'button';
          removeBtn.className = 'tag-remove';
          removeBtn.innerHTML = '<img src="<c:url value='/resources/icons/x.svg'/>"/>';
          removeBtn.addEventListener('click', function() {
            selectedOption.selected = false;
            tag.remove();
          });

          tag.appendChild(removeBtn);
          citySelectedContainer.appendChild(tag);
        }
      }

      // Interest
      const interestSelect = document.getElementById('interest-select');
      const interestSelectedContainer = document.getElementById('interestSelectedContainer');

      if (interestSelect.value) {
        const selectedOption = Array.from(interestSelect.options).find(opt => opt.selected);
        if (selectedOption) {
          const tag = document.createElement('div');
          tag.className = 'selected-tag';
          tag.innerHTML = selectedOption.textContent;

          const removeBtn = document.createElement('button');
          removeBtn.type = 'button';
          removeBtn.className = 'tag-remove';
          removeBtn.innerHTML = '<img src="<c:url value='/resources/icons/x.svg'/>"/>';
          removeBtn.addEventListener('click', function() {
            selectedOption.selected = false;
            tag.remove();
          });

          tag.appendChild(removeBtn);
          interestSelectedContainer.appendChild(tag);
        }
      }
    }
  });
</script>

<style>
  /* Sort dropdown styles */
  .sort-dropdown {
    position: relative;
    display: inline-block;
  }

  .dropdown-content {
    position: absolute;
    right: 0;
    background-color: white;
    min-width: 200px;
    box-shadow: 0 8px 16px rgba(0,0,0,0.1);
    z-index: 1;
    border-radius: 0.5rem;
    overflow: hidden;
  }

  .dropdown-content a {
    color: #333;
    padding: 12px 16px;
    text-decoration: none;
    display: block;
    transition: background-color 0.2s;
  }

  .dropdown-content a:hover {
    background-color: #f1f1f1;
  }

  .dropdown-content a.active {
    background-color: #e5e7eb;
    font-weight: 500;
  }
</style>
</body>
</html>

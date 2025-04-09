<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


<%--
Parameters:
- path: Form path for the select
- label: Label text
- items: Collection of items to display (passed as request attribute)
- searchPlaceholder: Placeholder text for search input
- helpText: Help text to display below the component
--%>

<div class="form-group">
  <form:label path="${param.path}" cssClass="form-label">
    ${param.label}
  </form:label>

  <!-- Hidden select that will hold the actual form data -->
  <form:select path="${param.path}" multiple="true" id="interestsSelect" style="display: none;">
    <c:forEach var="item" items="${requestScope[param.items]}">
      <option value="${item.name}"><c:out value="${item.name}"/></option>
    </c:forEach>
  </form:select>

  <!-- Custom UI for interests selection -->
  <div class="interests-container">
    <!-- Search input -->
    <input type="text" id="interestSearch" class="interests-search"
           placeholder="${param.searchPlaceholder}" />

    <!-- Dropdown for search results -->
    <div id="interestDropdown" class="interests-dropdown hidden">
      <ul>
        <c:forEach var="item" items="${requestScope[param.items]}">
          <li class="interest-option" data-value="${item.name}">
            <c:out value="${item.name}"/>
          </li>
        </c:forEach>
      </ul>
    </div>
  </div>

  <!-- Selected interests will appear here as tags -->
  <div id="selectedInterests" class="selected-interests"></div>

  <div class="interests-help-text">
    ${param.helpText}
  </div>

  <form:errors path="${param.path}" cssClass="error-message" />
</div>

<!-- JavaScript for the interests autocomplete -->
<script>
  document.addEventListener('DOMContentLoaded', function() {
    const interestsSelect = document.getElementById('interestsSelect');
    const interestSearch = document.getElementById('interestSearch');
    const interestDropdown = document.getElementById('interestDropdown');
    const selectedInterests = document.getElementById('selectedInterests');
    const interestOptions = document.querySelectorAll('.interest-option');

    // Store selected values as objects with value and text properties
    let selectedValues = [];

    // Initialize with any pre-selected values (for edit forms)
    initializeSelectedValues();
    updateSelectedTags();

    // Toggle dropdown on input focus
    interestSearch.addEventListener('focus', function() {
      interestDropdown.classList.remove('hidden');
      filterOptions(this.value);
    });

    // Hide dropdown when clicking outside
    document.addEventListener('click', function(e) {
      if (!interestSearch.contains(e.target) && !interestDropdown.contains(e.target)) {
        interestDropdown.classList.add('hidden');
      }
    });

    // Filter options as user types
    interestSearch.addEventListener('input', function() {
      filterOptions(this.value);
      interestDropdown.classList.remove('hidden');
    });

    // Handle option selection
    interestOptions.forEach(option => {
      option.addEventListener('click', function() {
        const value = this.dataset.value;
        const text = this.textContent.trim();

        // Check if already selected
        const exists = selectedValues.some(item => item.value === value);

        // Toggle selection
        if (!exists) {
          // Add to selected values
          selectedValues.push({ value: value, text: text });

          // Update the hidden select
          updateSelectElement();
        }

        // Update the UI
        updateSelectedTags();
        interestSearch.value = '';
        interestDropdown.classList.add('hidden');
      });
    });

    // Filter dropdown options based on search text
    function filterOptions(searchText) {
      const filter = searchText.toLowerCase();
      interestOptions.forEach(option => {
        const text = option.textContent.toLowerCase();
        if (text.includes(filter)) {
          option.style.display = '';
        } else {
          option.style.display = 'none';
        }
      });
    }

    // Initialize selected values from the select element
    function initializeSelectedValues() {
      // Get all option elements
      const options = interestsSelect.querySelectorAll('option');

      // Check which ones are selected
      options.forEach(option => {
        if (option.selected) {
          selectedValues.push({
            value: option.value,
            text: option.textContent.trim()
          });
        }
      });
    }

    // Update the select element based on selectedValues array
    function updateSelectElement() {
      const options = interestsSelect.querySelectorAll('option');
      const selectedValueIds = selectedValues.map(item => item.value);

      options.forEach(option => {
        option.selected = selectedValueIds.includes(option.value);
      });
    }

    // Update the selected tags UI
    function updateSelectedTags() {
      // Clear existing tags
      selectedInterests.innerHTML = '';

      // Create tags for each selected value
      selectedValues.forEach(item => {
        // Create tag container
        const tag = document.createElement('div');
        tag.className = 'interest-tag';

        // Add text node
        const textNode = document.createTextNode(item.text);
        tag.appendChild(textNode);

        // Create remove button
        const removeBtn = document.createElement('button');
        removeBtn.type = 'button';
        removeBtn.className = 'interest-tag-remove';
        removeBtn.dataset.value = item.value;

        // Create SVG for the remove button
        const svg = document.createElementNS('http://www.w3.org/2000/svg', 'svg');
        svg.setAttribute('width', '12');
        svg.setAttribute('height', '12');
        svg.setAttribute('fill', 'currentColor');
        svg.setAttribute('viewBox', '0 0 16 16');

        const path = document.createElementNS('http://www.w3.org/2000/svg', 'path');
        path.setAttribute('d', 'M8 8.707l3.646 3.647a.5.5 0 0 0 .708-.708L8.707 8l3.647-3.646a.5.5 0 0 0-.708-.708L8 7.293 4.354 3.646a.5.5 0 1 0-.708.708L7.293 8l-3.647 3.646a.5.5 0 0 0 .708.708L8 8.707z');

        svg.appendChild(path);
        removeBtn.appendChild(svg);

        // Add remove button functionality
        removeBtn.addEventListener('click', function() {
          const valueToRemove = this.dataset.value;
          selectedValues = selectedValues.filter(item => item.value !== valueToRemove);
          updateSelectElement();
          updateSelectedTags();
        });

        // Add button to tag
        tag.appendChild(removeBtn);

        // Add tag to container
        selectedInterests.appendChild(tag);
      });
    }
  });
</script>
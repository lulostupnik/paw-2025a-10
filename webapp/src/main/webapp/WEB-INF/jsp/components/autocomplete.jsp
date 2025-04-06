<%--
  Reusable Spring Form Autocomplete Component

  Parameters:
  - path: The form:input path attribute (required)
  - label: Label text (required)
  - placeholder: Placeholder text (optional)
  - hint: Hint text below input (optional)
  - icon: SVG icon HTML (optional)
  - listVar: JavaScript variable name containing the options array (required)
  - required: Whether the field is required (optional, default: false)
--%>

<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<%-- Include styles only once per page using a flag --%>
<c:if test="${empty autocompleteStylesIncluded}">
  <c:set var="autocompleteStylesIncluded" value="true" scope="request" />
  <style>
    /* Awesomplete dropdown styling */
    .awesomplete > ul {
      border-radius: 0.5rem;
      border: 1px solid #e5e7eb;
      box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.1), 0 4px 6px -2px rgba(0, 0, 0, 0.05);
      padding: 0.5rem 0;
      background: white;
      width: 100%;
      max-height: 20rem;
      overflow-y: auto;
      z-index: 100;
    }

    .awesomplete > ul > li {
      padding: 0.75rem 1rem;
      color: #374151;
      cursor: pointer;
      transition: all 0.2s ease;
      font-size: 0.875rem;
      border-left: 3px solid transparent;
    }

    .awesomplete > ul > li:hover,
    .awesomplete > ul > li[aria-selected="true"] {
      background-color: #F9FAFB;
      color: #2563EB;
      border-left: 3px solid #2563EB;
    }

    /* Fix the width of the Awesomplete container */
    .awesomplete {
      display: block;
      width: 100%;
    }

    /* Custom scrollbar for the dropdown */
    .awesomplete > ul::-webkit-scrollbar {
      width: 6px;
    }

    .awesomplete > ul::-webkit-scrollbar-track {
      background: #f1f1f1;
      border-radius: 10px;
    }

    .awesomplete > ul::-webkit-scrollbar-thumb {
      background: #d1d5db;
      border-radius: 10px;
    }

    .awesomplete > ul::-webkit-scrollbar-thumb:hover {
      background: #9ca3af;
    }

    /* Custom match styling */
    .match-text {
      font-weight: 600;
    }

    /* Invalid input styling */
    .autocomplete-invalid {
      border-color: #ef4444 !important;
      animation: shake 0.5s;
    }

    @keyframes shake {
      0%, 100% { transform: translateX(0); }
      10%, 30%, 50%, 70%, 90% { transform: translateX(-5px); }
      20%, 40%, 60%, 80% { transform: translateX(5px); }
    }
  </style>
</c:if>

<div class="mb-4 sm:mb-8">
  <form:label path="${param.path}" class="block mb-2 text-sm font-medium">
    <c:choose>
      <c:when test="${not empty param.messagePrefix}">
        <spring:message code="${param.messagePrefix}.${param.label}" />
      </c:when>
      <c:otherwise>
        <spring:message code="${param.label}" text="${param.label}" />
      </c:otherwise>
    </c:choose>
    <c:if test="${param.required == 'true'}">
      <span class="text-red-500">*</span>
    </c:if>
  </form:label>

  <div class="relative w-full">
    <form:input path="${param.path}"
                id="${param.path}"
                class="py-2.5 sm:py-3 px-4 block w-full border border-gray-200 rounded-lg sm:text-sm focus:border-blue-500 focus:ring-blue-500"
                placeholder="${param.placeholder}"
                autocomplete="off" />

    <c:if test="${not empty param.icon}">
      <div class="absolute inset-y-0 right-0 flex items-center pr-3 pointer-events-none text-gray-400">
          ${param.icon}
      </div>
    </c:if>
  </div>

  <c:if test="${not empty param.hint}">
    <div class="text-xs text-gray-500 mt-1">${param.hint}</div>
  </c:if>

  <form:errors path="${param.path}" cssClass="text-red-500 text-sm mt-1" element="p" />

  <script>
    document.addEventListener("DOMContentLoaded", function() {
      var input = document.getElementById("${param.path}");

      // Check if the options array exists
      if (typeof ${param.listVar} !== 'undefined') {
        // Initialize Awesomplete with custom item rendering
        var awesomplete = new Awesomplete(input, {
          list: ${param.listVar},
          minChars: 1,
          maxItems: 10,
          autoFirst: true,
          filter: function(text, input) {
            return Awesomplete.FILTER_CONTAINS(text, input.match(/[^,]*$/)[0]);
          },
          // Custom item renderer to avoid the default highlighting
          item: function(text, input) {
            // Create the list item
            var item = document.createElement("li");

            // Get the input value
            var inputValue = input.match(/[^,]*$/)[0].trim().toLowerCase();

            // If there's no input, just show the text
            if (!inputValue) {
              item.textContent = text;
              return item;
            }

            // Find the position of the match
            var index = text.toLowerCase().indexOf(inputValue);

            // If no match is found, just show the text
            if (index === -1) {
              item.textContent = text;
              return item;
            }

            // Split the text into three parts: before match, match, after match
            var before = text.substring(0, index);
            var match = text.substring(index, index + inputValue.length);
            var after = text.substring(index + inputValue.length);

            // Create the HTML content
            if (before) {
              var beforeSpan = document.createElement("span");
              beforeSpan.textContent = before;
              item.appendChild(beforeSpan);
            }

            var matchSpan = document.createElement("span");
            matchSpan.className = "match-text";
            matchSpan.textContent = match;
            item.appendChild(matchSpan);

            if (after) {
              var afterSpan = document.createElement("span");
              afterSpan.textContent = after;
              item.appendChild(afterSpan);
            }

            return item;
          }
        });

        // Improve dropdown positioning
        input.addEventListener('awesomplete-open', function() {
          var dropdown = input.parentNode.querySelector('ul');
          if (dropdown) {
            dropdown.style.width = input.offsetWidth + 'px';
          }
        });

        // Validation: ensure only items from the list are accepted
        input.addEventListener('blur', function() {
          var currentValue = input.value.trim();
          var isValid = ${param.listVar}.includes(currentValue);

          // If not valid and not empty, clear the field
          if (!isValid && currentValue !== '') {
            input.value = '';
            // Add a visual indicator that the value was invalid
            input.classList.add('autocomplete-invalid');
            setTimeout(function() {
              input.classList.remove('autocomplete-invalid');
            }, 1000);
          }
        });

        // Form submission validation
        if (input.form) {
          input.form.addEventListener('submit', function(e) {
            var currentValue = input.value.trim();
            var isValid = ${param.listVar}.includes(currentValue) || currentValue === '';

            if (!isValid) {
              e.preventDefault();
              input.value = '';
              input.focus();
              // Add a visual indicator that the value was invalid
              input.classList.add('autocomplete-invalid');
              setTimeout(function() {
                input.classList.remove('autocomplete-invalid');
              }, 1000);
            }
          });
        }

        // When an item is selected, mark as valid
        input.addEventListener('awesomplete-selectcomplete', function() {
          // You could add a visual indicator that the selection is valid
          input.classList.add('border-green-500');
          setTimeout(function() {
            input.classList.remove('border-green-500');
          }, 1000);
        });
      } else {
        console.error("Autocomplete options array '${param.listVar}' is not defined");
      }
    });
  </script>
</div>
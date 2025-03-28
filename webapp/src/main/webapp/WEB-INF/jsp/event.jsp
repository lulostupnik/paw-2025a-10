<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Create an Event</title>
  <script src="https://cdn.tailwindcss.com"></script>
  <script>
    tailwind.config = {
      theme: {
        extend: {
          colors: {
            'custom-blue': '#3490dc',
          }
        }
      }
    }
  </script>
</head>
<body class="bg-gray-100">
<!-- Contact Us -->
<div class="max-w-[85rem] px-4 py-10 sm:px-6 lg:px-8 lg:py-14 mx-auto">
  <div class="max-w-2xl lg:max-w-5xl mx-auto">
    <div class="text-center">
      <h1 class="text-3xl font-bold text-gray-800 sm:text-4xl dark:text-white">
        Create an Event
      </h1>
      <p class="mt-1 text-gray-600 dark:text-neutral-400">
        Embrace a new journey.
      </p>
    </div>

    <div class="mt-12 grid items-center lg:grid-cols-2 gap-6 lg:gap-16">
      <!-- Card -->
      <div class="flex flex-col border border-gray-200 rounded-xl p-4 sm:p-6 lg:p-8 dark:border-neutral-700">
        <h2 class="mb-8 text-xl font-semibold text-gray-800 dark:text-neutral-200">
          Fill in the form
        </h2>

        <form action="/create-event" method="post" enctype="multipart/form-data">
          <div class="grid gap-4">
            <!-- Grid -->
            <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <div>
                <label for="city" class="block text-sm font-medium text-gray-700 mb-2">City</label>
                <input type="text" name="city" id="city" class="py-2.5 sm:py-3 px-4 block w-full border-gray-200 rounded-lg text-sm focus:border-blue-500 focus:ring-blue-500" placeholder="Enter City">
              </div>

              <div>
                <label for="date" class="block text-sm font-medium text-gray-700 mb-2">Date</label>
                <input type="date" name="date" id="date" class="py-2.5 sm:py-3 px-4 block w-full border-gray-200 rounded-lg text-sm focus:border-blue-500 focus:ring-blue-500">
              </div>

              <div class="sm:col-span-2">
                <label for="flyer" class="block text-sm font-medium text-gray-700 mb-2">Event Flyer</label>
                <input type="file" name="flyer" id="flyer" class="block w-full border border-gray-200 shadow-sm rounded-lg text-sm focus:z-10 focus:border-blue-500 focus:ring-blue-500
                  file:bg-gray-50 file:border-0
                  file:me-4
                  file:py-2.5 file:px-4
                  dark:file:bg-neutral-700
                  dark:file:text-neutral-400">
              </div>

              <div class="sm:col-span-2">
                <label for="description" class="block text-sm font-medium text-gray-700 mb-2">Description</label>
                <textarea id="description" name="description" rows="4" class="py-2.5 sm:py-3 px-4 block w-full border-gray-200 rounded-lg text-sm focus:border-blue-500 focus:ring-blue-500" placeholder="Event details"></textarea>
              </div>
            </div>

            <div class="mt-4">
              <button type="submit" class="w-full py-3 px-4 inline-flex justify-center items-center gap-x-2 text-sm font-semibold rounded-lg border border-transparent bg-blue-600 text-white hover:bg-blue-700 disabled:opacity-50 disabled:pointer-events-none">
                Create Event
              </button>
            </div>
          </div>
        </form>
      </div>

      <!-- Additional Content -->
      <div class="hidden lg:block">
        <img src="/api/placeholder/500/600" alt="Event Creation" class="rounded-xl">
      </div>
    </div>
  </div>
</div>
</body>
</html>
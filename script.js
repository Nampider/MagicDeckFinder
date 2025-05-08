const BASE_URL = "http://localhost:8000";

function getCardList() {
  const text = document.getElementById("cardInput").value;
  return text
    .split("\n")
    .map(name => name.trim())
    .filter(name => name)
    .map(name => ({ name }));
}

function showLoading(state) {
  document.getElementById("loading").classList.toggle("hidden", !state);
}

function displayResults(content, isError = false) {
  const resultsDiv = document.getElementById("results");
  resultsDiv.innerHTML = "";

  if (Array.isArray(content)) {
    content.forEach(res => {
      const el = document.createElement("div");
      if (res.error) {
        el.textContent = `❌ Error for ${res.card || "Unknown"}: ${res.error}`;
        el.className = "result-error";
      } else {
        el.textContent = `✅ ${JSON.stringify(res)}`;
        el.className = "result-success";
      }
      resultsDiv.appendChild(el);
    });
  } else if (content.errors) {
    const heading = document.createElement("div");
    heading.textContent = `❌ ${content.detail}`;
    heading.className = "result-error";
    resultsDiv.appendChild(heading);

    content.errors.forEach(errorMsg => {
      const el = document.createElement("div");
      el.textContent = `- ${errorMsg}`;
      el.className = "result-error";
      resultsDiv.appendChild(el);
    });
  } else {
    const el = document.createElement("pre");
    el.textContent = JSON.stringify(content, null, 2);
    resultsDiv.appendChild(el);
  }
}

async function scrapeCards() {
  showLoading(true);
  try {
    const response = await fetch(`${BASE_URL}/scrape`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify(getCardList())
    });

    const data = await response.json();
    displayResults(data);
  } catch (err) {
    displayResults([{ error: "Scrape failed: " + err.message }], true);
  } finally {
    showLoading(false);
  }
}

async function mockScrape() {
  showLoading(true);
  try {
    const response = await fetch(`${BASE_URL}/mockScrape`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify(getCardList())
    });

    if (!response.ok) {
      const errorData = await response.json();
      displayResults(errorData.detail, true);
    } else {
      const data = await response.json();
      displayResults(data);
    }
  } catch (err) {
    displayResults([{ error: "Mock scrape failed: " + err.message }], true);
  } finally {
    showLoading(false);
  }
}

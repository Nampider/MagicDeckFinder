from selenium import webdriver
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.common.by import By
from selenium.webdriver.common.keys import Keys
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.chrome.options import Options
from webdriver_manager.chrome import ChromeDriverManager

# Setup Chrome driver
options = Options()
options.add_argument("--start-maximized")

driver = webdriver.Chrome(service=Service(ChromeDriverManager().install()), options=options)

def web_scrape(driver: webdriver.Chrome, search_term: str = "Swamp"):
    wait = WebDriverWait(driver, 10) 
    driver.get("https://darksidegames.com/")

    # Wait and input the search term
    search_input = wait.until(
        EC.presence_of_element_located((By.CLASS_NAME, "search-bar__input"))
    )
    search_input.clear()
    search_input.send_keys(search_term + Keys.ENTER)

    card_info = []

    while True:
        # Wait for products to load
        wait.until(EC.presence_of_all_elements_located((By.CLASS_NAME, "productCard__title")))

        # Grab product info
        prices = driver.find_elements(By.CLASS_NAME, "productCard__price")
        titles = driver.find_elements(By.CLASS_NAME, "productCard__title")
        stocks = driver.find_elements(By.CLASS_NAME, "productChip__active")
        # sets = driver.find_elements(By.CLASS_NAME, "productCard__setName")
    
        # Safeguard for inconsistent list lengths
        count = min(len(prices), len(titles), len(stocks))

        for i in range(count):
            title_text = titles[i].text.strip()
            if search_term.strip().lower() in title_text.lower():
                card_info.append({
                    "title": title_text,
                    "price": prices[i].text.strip(),
                    "stock": stocks[i].text.strip(),
                    # "set": sets[i].text.strip()
                })

        # Try to go to the next page
        try:
            next_button = wait.until(EC.element_to_be_clickable((By.CLASS_NAME, "pagination__next")))

            # Check if it's disabled
            if 'disabled' in next_button.get_attribute('class'):
                break

            # Wait for old products to become stale before clicking
            first_card = titles[0]
            next_button.click()
            wait.until(EC.staleness_of(first_card))  # wait until content refreshes
        except:
            break

    return card_info

# Call the function
results = web_scrape(driver, "Swamp")

print("\nCollected card data:")
for card in results:
    print(card)

driver.quit()

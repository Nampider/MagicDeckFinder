from selenium import webdriver
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.common.by import By
from selenium.webdriver.common.keys import Keys
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.chrome.options import Options
from webdriver_manager.chrome import ChromeDriverManager
import time

# Setup Chrome driver
options = Options()
options.add_argument("--start-maximized")

driver = webdriver.Chrome(service=Service(ChromeDriverManager().install()), options=options)

def web_scrape(driver: webdriver, search_term: str = "Sol Ring"):
    driver.get("https://darksidegames.com/")
    WebDriverWait(driver, 5).until(
        EC.presence_of_element_located((By.CLASS_NAME, "search-bar__input"))
    )

    input_element = driver.find_element(By.CLASS_NAME, "search-bar__input")
    input_element.clear()
    input_element.send_keys(search_term + Keys.ENTER)

    WebDriverWait(driver, 5).until(
        EC.presence_of_element_located((By.CLASS_NAME, "productCard__title"))
    )

    card_info = []
    
    while True:
        time.sleep(1)  # Helps if the page is JS-heavy

        prices = driver.find_elements(By.CLASS_NAME, "productCard__price")
        titles = driver.find_elements(By.CLASS_NAME, "productCard__title")
        stocks = driver.find_elements(By.CLASS_NAME, "productChip__active")
        sets = driver.find_elements(By.CLASS_NAME, "productCard__setName")

        for price, title, stock, set_name in zip(prices, titles, stocks, sets):
            if title.text.strip().lower() == search_term.strip().lower():
                card_info.append({
                    "title": title.text,
                    "price": price.text,
                    "stock": stock.text,
                    "set": set_name.text
                })

        # Try to move to the next page if it exists
        try:
            next_button = WebDriverWait(driver, 3).until(
                EC.element_to_be_clickable((By.CLASS_NAME, "pagination__next"))
            )
            if 'disabled' in next_button.get_attribute('class'):
                break
            next_button.click()
        except:
            break  # No more pages

    return card_info

# Call the function and print results
results = web_scrape(driver, "Sol Ring")

print("\nCollected card data:")
for card in results:
    print(card)

driver.quit()

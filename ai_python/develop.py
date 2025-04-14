from selenium import webdriver
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.common.by import By
from selenium.webdriver.common.keys import Keys
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.chrome.options import Options
from webdriver_manager.chrome import ChromeDriverManager


def web_scrape(search_term: str):
    options = Options()
    options.add_argument("--start-maximized")
    options.add_argument('headless')
    driver = webdriver.Chrome(service=Service(ChromeDriverManager().install()), options=options)
    wait = WebDriverWait(driver, 10) 
    driver.get("https://darksidegames.com/")

    # Search
    search_input = wait.until(
        EC.presence_of_element_located((By.CLASS_NAME, "search-bar__input"))
    )
    search_input.clear()
    search_input.send_keys(search_term + Keys.ENTER)

    card_info = []

    while True:
        wait.until(EC.presence_of_all_elements_located((By.CLASS_NAME, "productCard__title")))
        prices = driver.find_elements(By.CLASS_NAME, "productCard__price")
        titles = driver.find_elements(By.CLASS_NAME, "productCard__title")
        stocks = driver.find_elements(By.CLASS_NAME, "productChip__active")
        min_price_cards = []
        count = min(len(prices), len(titles), len(stocks))
        for i in range(count):
            title_text = titles[i].text.strip()
            if search_term.strip().lower() in title_text.lower() and "art card" not in title_text.lower() and stocks[i].text.strip()!= "":
                card_info.append({
                    "title": title_text,
                    "price": prices[i].text.strip(),
                    # "stock": stocks[i].text.strip() if stocks[i].text.strip() else 0
                    "stock": stocks[i].text.strip()
                })

        min_price_cards.append(min(card_info, key=lambda card: float(card["price"].replace("$", "").replace(" USD", ""))))
        print("This is the min list")
        print(min_price_cards)
                

        try:
            next_button = wait.until(EC.element_to_be_clickable((By.PARTIAL_LINK_TEXT, "Next")))
            driver.execute_script("arguments[0].scrollIntoView(true);", next_button)
            driver.execute_script("arguments[0].click();", next_button)
            wait.until(EC.staleness_of(titles[0]))
        except:
            break

    driver.quit()
    return min_price_cards[0]

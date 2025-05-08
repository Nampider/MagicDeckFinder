from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.common.keys import Keys
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.chrome.options import Options
from selenium_stealth import stealth
import time


def web_scrape(search_term: str):
    options = Options()
    options.add_argument("--headless=new")
    options.add_argument("--disable-gpu")
    options.add_argument("--disable-extensions")
    options.add_argument("--blink-settings=imagesEnabled=false")
    options.add_argument("--disable-dev-shm-usage")
    options.add_argument("--no-sandbox")
    options.add_argument("--window-size=1920,1080")
    options.add_argument("--ignore-certificate-errors")

    driver = webdriver.Chrome(options=options)
    stealth(driver,
        languages=["en-US", "en"],
        vendor="Google Inc.",
        platform="Win32",
        webgl_vendor="Intel Inc.",
        renderer="Intel Iris OpenGL Engine",
        fix_hairline=True,
    )

    wait = WebDriverWait(driver, 20)
    card_info = []

    try:
        driver.get("https://darksidegames.com/")

        try:
            search_input = wait.until(EC.presence_of_element_located((By.CLASS_NAME, "search-bar__input")))
            search_input.clear()
            search_input.send_keys(search_term + Keys.ENTER)
        except:
            # print(f"[ERROR] Search input not found for: {search_term}")
            return {"error": "Search input not found", "card": search_term}

        while True:
            try:
                wait.until(EC.presence_of_all_elements_located((By.CLASS_NAME, "productCard__title")))
                prices = driver.find_elements(By.CLASS_NAME, "productCard__price")
                titles = driver.find_elements(By.CLASS_NAME, "productCard__title")
                stocks = driver.find_elements(By.CLASS_NAME, "productChip__active")

                count = min(len(prices), len(titles), len(stocks))
                for i in range(count):
                    title_text = titles[i].text.strip()
                    if (
                        search_term.strip().lower() in title_text.lower()
                        and "art card" not in title_text.lower()
                        and stocks[i].text.strip() != ""
                    ):
                        card_info.append({
                            "title": title_text,
                            "price": prices[i].text.strip(),
                            "stock": stocks[i].text.strip()
                        })

                next_button = wait.until(EC.element_to_be_clickable((By.PARTIAL_LINK_TEXT, "Next")))
                driver.execute_script("arguments[0].scrollIntoView(true);", next_button)
                driver.execute_script("arguments[0].click();", next_button)
                wait.until(EC.staleness_of(titles[0]))
            except:
                break
    except Exception as e:
        # print(f"[ERROR] Unexpected error scraping '{search_term}': {e}")
        return {"error": str(e), "card": search_term}
    finally:
        driver.quit()

    if card_info:
        return min(card_info, key=lambda card: float(card["price"].replace("$", "").replace(" USD", "")))
    else:
        return {"message": "No matching cards found", "card": search_term}

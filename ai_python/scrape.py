#Actual selenium scraping here

import selenium.webdriver as webdriver
from selenium.webdriver.chrome.service import Service
import time

#Grab content from a website using selenium
def scrape_website(website):
    print("Launching Chrome Browser...")
    
    chrome_driver_path = "./chromedriver.exe" #For an application that allows us to control chrome
    options = webdriver.ChromeOptions() #specify how the chrome web driver operates   we can run i headless mode etc
    driver = webdriver.Chrome(service = Service(chrome_driver_path), options=options)   #setup the actual driver (can use firefox whatever browser)
    
    try:
        driver.get(website)
        print("Page Loaded")
        html = driver.page_source
        time.sleep(10)
        return html
    finally:
        driver.quit()
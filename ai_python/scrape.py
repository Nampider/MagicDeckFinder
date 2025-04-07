#Actual selenium scraping here

import selenium.webdriver as webdriver
from selenium.webdriver.chrome.service import Service
import time
from bs4 import BeautifulSoup

#Grab content from a website using selenium
def scrape_website(website):
    print("Launching Chrome Browser...")
    
    chrome_driver_path = "./chromedriver" #For an application that allows us to control chrome
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

def extract_body_content(html_content):
    soup = BeautifulSoup(html_content, "html.parser")
    body_content = soup.body
    if body_content:
        return str(body_content)
    return ""

def clean_body_content(body_content):
    soup = BeautifulSoup(body_content, "html.parser")

    for script_or_style in soup(["script", "style"]):   #looks inside of the code for any scripts or style and remove them from the code
        script_or_style.extract()
    
    cleaned_content = soup.get_text(separator="\n") #basically says get all of the text and separate with a newline
    cleaned_content = "\n".join(
        line.strip() for line in cleaned_content.splitlines() if line.strip()
    )
    return cleaned_content

#split the cleaned content into batches. To feed this into the LLM.
def split_dom_content(dom_content, max_length=6000):
    return [    #create batches of 6000 characters.
        dom_content[i: i+max_length] for i in range(0, len(dom_content), max_length)
    ]
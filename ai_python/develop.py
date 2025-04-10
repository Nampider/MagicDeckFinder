#This is an example file for webscraping

from selenium import webdriver
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.common.by import By #use this to search for elements in html
from selenium.webdriver.common.keys import Keys
from selenium.webdriver.support.ui import WebDriverWait #for waiing for web to load fully.
from selenium.webdriver.support import expected_conditions as EC
import time

#setup the chrome driver service
#we have to create a webdriver. Automation tool to control google chrome. Chrome is the most populat

service = Service(executable_path="./chromedriver")
driver = webdriver.Chrome(service=service)

driver.get("https://darksidegames.com/")

def web_scrape(driver:webdriver):
    WebDriverWait(driver, 5).until(EC.presence_of_element_located((By.CLASS_NAME, "search-bar__input")))  #basically the above code says to wait until you can find the class with gLFyf

    input_element = driver.find_element(By.CLASS_NAME, "search-bar__input")  #look for first element on the page with this class and return it.
    input_element.clear()
    input_element.send_keys("The Mouth of Sauron" + Keys.ENTER)

    WebDriverWait(driver, 5).until(EC.presence_of_element_located((By.PARTIAL_LINK_TEXT, "The Mouth of Sauron"))) 

    card_price = driver.find_elements(By.CLASS_NAME, "productCard__price")
    card_title = driver.find_elements(By.CLASS_NAME, "productCard__title ")
    card_stock = driver.find_elements(By.CLASS_NAME, "productChip__active")
    card_set = driver.find_elements(By.CLASS_NAME, "productCard__setName")
    card_info = []
    for i,j,k,l in zip(card_price, card_title, card_stock, card_set):
        if j.text == "The Mouth of Sauron":
            card_info.append((i.text, j.text, l.text, k.text))

    print("Lets go through the card_information list that I gathered")
    for f in card_info:
        print(f)

web_scrape(driver)



# link = driver.find_element(By.PARTIAL_LINK_TEXT, "Tech With Tim")   #find element is first thing on page. find_elements with a s returns an array of everything.
# link.click()

driver.quit()

""""
These are the classes I will need from darkside games.
 productCard__price
productCard__title -> href\
productCard__img
productChip__active 
"""

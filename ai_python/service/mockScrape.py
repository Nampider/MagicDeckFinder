#This is the file that will handle the mock data request
from models.mockResponse import Item
import requests
import json
import random

def mockDataForm(cardName):
    response = requests.get(f"https://api.scryfall.com/cards/named?exact={cardName}")
    dataImg = json.loads(response.content.decode()).get("image_uris").get("normal")
    price = str(round(random.uniform(1, 10), 2))
    stock = str(round(random.uniform(1, 10)))

    print(dataImg)

    item = Item(name=cardName, price = f"${price} USD", img_url=dataImg, stock=f"{stock} left", store="darksides")
    # print("Inside of the mockDataForm")
    # print(item)
    return item


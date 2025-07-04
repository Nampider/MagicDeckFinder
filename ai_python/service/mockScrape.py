#This is the file that will handle the mock data request
from models.mockResponse import Item
import requests
import json
import random
import uuid

def mockDataForm(cardName):
    response = requests.get(f"https://api.scryfall.com/cards/named?exact={cardName}")
    dataImg = json.loads(response.content.decode()).get("image_uris").get("normal")
    price = str(round(random.uniform(1, 10), 2))
    stock = str(round(random.uniform(1, 10)))
    foil = random.choice([True, False])
    condition = random.choice(["Mint", "Near-Mint", "Used"])
    id = uuid.uuid4()

    print(dataImg)

    item = Item(id = id, name=cardName, price = f"${price} USD", img_url=dataImg, stock=f"{stock} left", store="darksides", foil=foil, condition=condition)
    # print("Inside of the mockDataForm")
    # print(item)
    return item


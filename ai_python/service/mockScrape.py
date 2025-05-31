#This is the file that will handle the mock data request
from models.mockResponse import Item

def mockDataForm(cardName):
    item = Item(name=cardName, price = "$9.55 USD")

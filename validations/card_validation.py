#This file will be used for validating that the cards returned were valid.
from mtgsdk import Card
from mtgsdk import Set
from mtgsdk import Type
from mtgsdk import Supertype
from mtgsdk import Subtype
from mtgsdk import Changelog

def validate_card_name(cardName: str) -> str:
    cleanName = str(cardName).strip()[6:-1]
    cardExist = Card.where(name=cleanName).all()
    # print("This is the cardExist")
    # print(len(cardExist))
    if len(cardExist) == 0:
        # print(cleanName)
        # print("This is the invalid card name")
        return "Invalid Card Name"
    else:
        return "Valid Card Name"

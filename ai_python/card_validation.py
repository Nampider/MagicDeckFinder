#This file will be used for validating that the cards returned were valid.
from mtgsdk import Card
from mtgsdk import Set
from mtgsdk import Type
from mtgsdk import Supertype
from mtgsdk import Subtype
from mtgsdk import Changelog
import requests
import json
import urllib.parse
import pkg_resources
from symspellpy import SymSpell, Verbosity
from itertools import islice
import re

def normalize(text):
    return re.sub(r'[^\w\s]', '', text).lower()

def spellCheck(cardName: str) -> str:
    sym_spell = SymSpell(max_dictionary_edit_distance=10, prefix_length=11)

    dictionary_path = "./misc/correct_words.txt"
    sym_spell.load_dictionary(dictionary_path, 0, 1, separator="$")

    # Debug: Show some loaded words
    # print(list(islice(sym_spell.words.items(), 5)))

    # Lookup entire phrase as a single token
    suggestions = sym_spell.lookup(normalize(cardName), Verbosity.CLOSEST, max_edit_distance=10)

    # if suggestions:
    #     print(suggestions[0].term)
    # else:
    #     print("No suggestion found.")
    return suggestions[0].term


def validate_card_name(cardName: str) -> list[str]:
    cleanName = str(cardName).strip()[6:-1]
    fixed_name = spellCheck(cleanName)
    # print("Returning fixed_nam")
    # print(fixed_name)
    response = requests.get(f"https://api.scryfall.com/cards/named?exact={cleanName}")
    data = json.loads(response.content.decode()).get("name")
    
    # for key, value in data.items():
    #     print(key, value)
    # cleanName = str(cardName).strip()[6:-1]
    cardExist = Card.where(name=cleanName).all()
    # print("This is the cardExist")
    # print(len(cardExist))
    if len(cardExist) == 0:
        # print("Returning fixed_nam")
        # print(fixed_name)
        # print(cleanName)
        # print("This is the invalid card name")
        return [fixed_name, cleanName]
    else:
        return ["Valid Card Name", cleanName]

#This is the dto class for mock response


from pydantic import BaseModel
from typing import List, Optional

class Item(BaseModel):
    name: str
    price: str
    img_url: str
    stock: str
    store: str
    condition: str
    foil: bool

class ErrorResponse(BaseModel):
    detail: str
    errors: List[str]  # List of error messages (e.g., "Card not found for ...")

class ScrapeRequest(BaseModel):
    quantity: int
    name: str  # This could be things like 'titles', 'links', etc.
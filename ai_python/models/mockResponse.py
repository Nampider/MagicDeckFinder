#This is the dto class for mock response


from pydantic import BaseModel
from typing import List, Optional
from uuid import UUID

class Item(BaseModel):
    id: UUID
    name: str
    price: str
    img_url: str
    stock: str
    store: str
    condition: str
    foil: bool
    setName: str

class ErrorResponse(BaseModel):
    detail: str
    errors: List[str]  # List of error messages (e.g., "Card not found for ...")

class ScrapeRequest(BaseModel):
    quantity: int
    name: str  # This could be things like 'titles', 'links', etc.
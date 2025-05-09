#This is the file that stores all the RESTAPI models that we need to use for the program
# Define a request model to capture the input from the frontend

from pydantic import BaseModel
from typing import List, Optional

class ScrapeRequest(BaseModel):
    quantity: int
    name: str  # This could be things like 'titles', 'links', etc.

class ErrorResponse(BaseModel):
    detail: str
    errors: List[str]  # List of error messages (e.g., "Card not found for ...")

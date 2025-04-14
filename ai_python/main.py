#This is the code for fasapi endpoint exposure that serves to connect the webscraping portion of the code with the restapi frontend

from fastapi import FastAPI
from pydantic import BaseModel
from typing import List
import requests
from develop import web_scrape
from models import ScrapeRequest
from typing import List

app = FastAPI()

@app.post("/scrape")
async def scrape_data(scrape_request: List[ScrapeRequest]):
    all_results = []
    for req in scrape_request:
        result = web_scrape(req.name)
        all_results.append(result)
    return all_results
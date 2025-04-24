#This is the code for fasapi endpoint exposure that serves to connect the webscraping portion of the code with the restapi frontend

from fastapi import FastAPI
from pydantic import BaseModel
from typing import List
import requests
from develop import web_scrape
from models import ScrapeRequest
from typing import List
from fastapi import FastAPI
from fastapi.concurrency import run_in_threadpool
from typing import List
from pydantic import BaseModel
import asyncio
import concurrent.futures
from datetime import datetime

app = FastAPI()

# @app.post("/scrape")
# async def scrape_data(scrape_request: List[ScrapeRequest]):
#     all_results = []
#     for req in scrape_request:
#         result = web_scrape(req.name)
#         all_results.append(result)
#     return all_results


# Max number of concurrent Selenium scrapess
MAX_CONCURRENT = 30
semaphore = asyncio.Semaphore(MAX_CONCURRENT)

def web_scrape_wrapper(search_term: str):
    # Your existing web_scrape logic goes here
    from develop import web_scrape
    return web_scrape(search_term)

async def limited_scrape(name: str):
    async with semaphore:
        return await run_in_threadpool(web_scrape_wrapper, name)

@app.post("/scrape")
async def scrape_data(scrape_request: List[ScrapeRequest]):
    start = datetime.now()
    tasks = [limited_scrape(req.name) for req in scrape_request]
    results = await asyncio.gather(*tasks)
    
    end = datetime.now()
    print(end - start)
    return results
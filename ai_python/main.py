
from fastapi import FastAPI, HTTPException, status
from fastapi.concurrency import run_in_threadpool
from pydantic import BaseModel
from typing import List
import asyncio
from datetime import datetime

from develop import web_scrape
from card_validation import validate_card_name
from models.mockResponse import ErrorResponse
from fastapi.responses import JSONResponse
import json


from fastapi import FastAPI, HTTPException, status
from fastapi.responses import FileResponse
from fastapi.staticfiles import StaticFiles
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel
from typing import List
import json, os

from develop import web_scrape
from card_validation import validate_card_name

app = FastAPI()

# Serve static frontend files
# app.mount("/static", StaticFiles(directory="static"), name="static")

# Set CORS (optional if frontend served from same server)
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_methods=["*"],
    allow_headers=["*"],
)

class ScrapeRequest(BaseModel):
    name: str

# @app.get("/")
# def serve_index():
#     return FileResponse("static/index.html")

MAX_CONCURRENT = 40
semaphore = asyncio.Semaphore(MAX_CONCURRENT)


def web_scrape_wrapper(search_term: str):
    try:
        return web_scrape(search_term)
    except Exception as e:
        # print(f"[ERROR] Scraping failed for {search_term}: {e}")
        return {"error": str(e), "card": search_term}


async def limited_scrape(name: str):
    # print(f"[START] {name}")
    async with semaphore:
        result = await run_in_threadpool(web_scrape_wrapper, name)
    # print(f"[DONE] {name}")
    return result


@app.post("/scrape")
async def scrape_data(scrape_request: List[ScrapeRequest]):
    start = datetime.now()
    tasks = [limited_scrape(req.name) for req in scrape_request]
    results = await asyncio.gather(*tasks, return_exceptions=True)

    final_results = []
    for res in results:
        if isinstance(res, Exception):
            final_results.append({"error": str(res)})
        else:
            final_results.append(res)

    end = datetime.now()
    print(f"[INFO] Completed scraping in {end - start}")
    return final_results


@app.post("/mockScrape")
def get_mock_cards(scrape_request: List[ScrapeRequest]):
    errorCase = []
    
    for s in scrape_request:
        returnVal = validate_card_name(s) 
        if returnVal[0] != "Valid Card Name":
            errorCase.append(f"Card not found for {returnVal[1]}: Did you mean {returnVal[0]}?")
        else:
            continue
    if errorCase:
        error_response = ErrorResponse(
            detail="Cards not found",
            errors=errorCase
        )
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail=error_response.model_dump()
        )
    with open("mock_cards.json", "r") as f:
        data = json.load(f)
    return JSONResponse(content=data)

@app.get("/errorTest/{item_id}")
async def read_item(item_id: str):
    val = 1
    if val == 1:
        raise HTTPException(status_code=404, detail="hehe test number")
    return {"val": val}


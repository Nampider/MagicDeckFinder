
from fastapi import FastAPI
from fastapi.concurrency import run_in_threadpool
from pydantic import BaseModel
from typing import List
import asyncio
from datetime import datetime

from develop import web_scrape


class ScrapeRequest(BaseModel):
    name: str


app = FastAPI()

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

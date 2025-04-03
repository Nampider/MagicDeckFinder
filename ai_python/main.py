#This is the main file.
#Use selenium

#Use selenium to filter through a website and pass those values (data) to an LLM

import streamlit as st
from scrape import scrape_website
#Title for the website
st.title("MTG Card List Reader")
url = st.text_input("Enter a Card Name ")

if st.button("Find Cards"):
    st.write("Milking the cats....")
    result = scrape_website(url)
    print(result)


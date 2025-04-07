#Actual AI code

#OLLAMA allows you to run open source LLMS on your computer
from langchain_ollama import OllamaLLM  #ollamallm for ollama llm can use chatgpt
from langchain_core.prompts import ChatPromptTemplate
#langchain allows us to connect LLMs to our python code

model = OllamaLLM(model="llama3.1")
#we have to install ollama for this to work

#Use the template for the prompts
#specify the variables to be injected into the prompt
template = (
    "You are tasked with extracting specific information from the following text content: {dom_content}. "
    "Please follow these instructions carefully: \n\n"
    "1. **Extract Information:** Only extract the information that directly matches the provided description: {parse_description}. "
    "2. **No Extra Content:** Do not include any additional text, comments, or explanations in your response. "
    "3. **Empty Response:** If no information matches the description, return an empty string ('')."
    "4. **Direct Data Only:** Your output should contain only the data that is explicitly requested, with no other text."
)

def parse_with_ollama(dom_chunks, parse_description):
    prompt = ChatPromptTemplate.from_template(template)
    chain = prompt | model  #this is a chain that says first we will go to the prompt and go to the model after

    parsed_results = []

    for i, chunk in enumerate(dom_chunks, start=1):
        response = chain.invoke({"dom_content": chunk, "parse_description": parse_description})
        print(f"Parsed batch {i} of {len(dom_chunks)}")
        parsed_results.append(response)
    return "\n".join(parsed_results)
    #Get the prompt from the user input

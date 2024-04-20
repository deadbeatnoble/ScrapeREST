# ScrapeREST

## Introduction
ScrapeREST is a RESTful API designed for web scraping purposes. It provides a simple and efficient way to extract data from websites using HTTP requests and parsing the HTML responses.

## Project Features
- Fetch web pages using HTTP requests
- Extract data by iterating and searching for specific selectors or tags
- Support for various scraping techniques (XPath, CSS selectors, regular expressions)
- Customizable scraping configurations
- Error handling

## API Base URL
The base URL for the ScrapeREST API is `https://scrape-v1-0.onrender.com`.

## API Endpoints
| HTTP Verbs | Endpoints | Action |
| --- | --- | --- |
| GET | `/feed?type=latest&page={page?}` | Retrieves the latest feed |
| GET | `/feed?type=topview&page={page?}` | Retrieves the popular feed |
| GET | `/feed?type=newest&page={page?}` | Retrieves the new feed |
| GET | `/collection/genre?genre_id={genre_id?}&page={page?}` | Performs a genre-based search |
| GET | `/collection/author?author_id={author_id?}&page={page?}` | Performs an author-based search |
| GET | `/search?title={title?}&page={page?}` | Performs a simple search |
| GET | `/advanced_search?type={type?}&title={title?}&s={s?}&g_i={g_i?}&g_e={g_e?}&stat={stat?}&orby={orby?}&page={page?}` | Performs an advanced search |
| GET | `/manga?manga_id={manga_id?}` | Retrieves manga details |
| GET | `/chapter?manga_id={manga_id?}&chapter_id={chapter_id?}` | Retrieves chapter pages |

## Technologies Used
- Ktor (Kotlin): A framework for building asynchronous servers and clients in connected systems.
- Kotlin coroutines: A powerful tool for writing asynchronous code in a sequential style.
- HTML parsing libraries: Utilized to extract data from the HTML responses.
- Docker: A containerization platform used for packaging the application and its dependencies into a standardized unit for easy deployment and scalability.

## Author
- Deadbeatnoble (https://github.com/deadbeatnoble)

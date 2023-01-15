# Argus Private Search Proxy

Argus is a privacy focused web search engine proxy and knowledge
engine that tries to minimise reliance on external search engines 
(such as Google) and single function websites (e.g. time zone
converters). Its purpose is to maximise the number of queries that can be
performed locally, and for those queries that cannot be performed
locally to maximise the number that can be performed directly on the
websites that can supply answers to those queries. The remaining
queries are passed to DuckDuckGo. Note that queries that are forwarded to
DuckDuckGo are no more private than those run directly on their website.

Argus provides three major benefits over using a search engine
directly:

* Enhanced privacy.
* Increased performance for queries that can be performed locally.
* Reduced reliance on network access. Locally performed queries do not require network access.

![Screenshot!](/screenshot.jpg)

## Features

Currently Argus only supports a very limited set of queries:

### Bang Queries

Argus supports a subset of [DuckDuckGo's bang commands](https://duckduckgo.com/bang) to route queries directly to the desired website, e.g. typing the following:

    !a wuthering heights

Would search for Wuthering Heights directly on Amazon's website removing the need to visit DuckDuckGo which would then re-direct to Amazon.

Other supported bang commands are:

| bang command | action |
| --- | --- |
| `!gh` | Run the query directly on GitHub |
| `!so` | Run the query directly on StackOverflow |
| `!w` | Run the query direclty on Wikipedia |
| `!yt` | Run the query direclty on YouTube |

To run the query directly on a specific search engine:

| bang command | search engine |
| --- | --- |
| `!b` | Run the query directly on Bing |
| `!brave` | Run the query directly on Brave |
| `!ddg` | Run the query directly on DuckDuckGo |
| `!g` | Run the query directly on Google |

Queries can be prefixed with "@" for Firefox style at queries, e.g.

    @amazon Wuthering heights

### Math Engine

Argus has an [integrated math engine](https://bitbucket.org/axelclk/symja_android_library/src/master/).

| example query | result |
| sin(30°) | 0.5 |

### ISO 8601 / UTC Dates

Argus supports converting dates in the ISO 8601 / UTC format
(e.g. 2022-12-24T01:08:48Z) to the local time.

### Random Number Generation

Argus supports generating a limited set of random numbers:

| example query | result |
| --- | --- |
| random hex | 946B2D46764836F904A0B4B842571469C83F6AF2AF619CAC0973B989A34C0C9C | 
| random uuid | 3c68645a-ed8b-4b28-81cc-8faa0bbb5bbe |

### Base64 & JWT Decoding

Argus supports decoding UTF-8 base64 encoded strings and JWTs (JSON
Web Tokens):

| example query | result |
| --- | --- |
| dXNlcjpwYXNzd29yZAo= | user:password |
| ey...J9.ey...fQ.Sf...5c | Header { "typ: "JWT", ... } |

### Colour Decoding

Argus supports decoding an encoded colour string and displaying that colour.

| example query | result |
| --- | --- |
| #FF0000 | Background colour changes to red |

### Word Pattern Matching

Argus supports matching words based on a pattern.

| example query | result |
| --- | --- |
| h?ve | have, hive, hove | 

## How to Run

### Docker

The simplest way to run is using Docker. Install [Docker](https://www.docker.com/) and run the following command:

    docker run --rm -p 3000:8080 -it aleppard/argus:0.1

or

    sudo docker run --rm -p 3000:8080 -it aleppard/argus:0.1

You can then visit <http://localhost:3000> in your browser.

The container is available in DockerHub [here](https://hub.docker.com/r/aleppard/argus).

You can also build and run the Docker image yourself, or build the source and deploy to a local [Apache Tomcat](https://tomcat.apache.org/) server.

## Setting as default search

### Firefox

You can set Argus to be Firefox' default search engine (including running queries directly in the address bar if enabled) by following these steps:

1. Visit the web page of your local Argus server in Firefox.
2. Right-click on the address bar and select "Add Argus".
2. Go to Firefox -> Preferences -> Search then select "Argus" as the Default Search Engine.

## Future Improvements

Future improvements could include:

* Set up site running Argus (possibly an Onion site).
* Add "copy to clipboard" button for locally returned results.
* Add ability to hit return twice to forward locally returned results
  directly to DuckDuckGo (e.g. if a locally returned result is not
  sufficient).
* Add ability to specify a different default search engine than DuckDuckGo.
* Custom bang commands.
* Support more bang commands.
* Improved math support.
  * Better support for internal math library (e.g. expose symbolic functionality).
  * A REPL style console to perform multiple math operations.
  * Display equations in MathML.
  * Plot equations.
* A computation engine to handle unit conversion.
* Support search through local text, HTML or other documents.
* Support search of local images using ML.
* A ML model to translate questions into queries (e.g. BERT).
* A question and answer engine perhaps trained on Wikipedia/Wikidata/[DBpedia](https://www.dbpedia.org).
* Support a local copy of Wikipedia/Wikidata/DBpedia.
* Support parsing and querying the semantic web (i.e. owl, json+ld, rdf etc).
* Support a local copy of OpenStreetMap.
* An integrated dictionary to handle spelling, definitions, anagrams, pronunciation etc.
* An integrated thesaurus to handle synonyms, antonyms etc.
* Ability to translate words and phrases.
* Time conversion to other time zones including finding the day of the
  week that a date falls on, and finding the number of days until a given date.
* A "local only" search mode.
* Cache recent queries and provide a "private" search mode to disable caching.
* Direct links to websites or calls to external APIs for weather, currency conversion, crypto & stock quotes etc.
* Ability to search local databases / uploaded spreadsheets / uploaded
  csv files.
* Provide a plug-in architecture to support custom queries.
* A REST API that can be used to build private virtual assistants
  (e.g. replacements for Alexa, Siri, etc).
* Calculate hashes of UTF-8 text strings.
* Additional colour decoding, naming of colour and coverting to other
  colour spaces.
* Ability to search and return books that are no longer under
  copyright.
* Identity (and continue) numeric sequences.
* Chemistry:
  * Enter chemical formula, e.g. C2H5OH and return the chemical name and
    structure.
  * Enter chemical name and show formula and structure.
  * Solve or query chemistry equations (e.g. H2O + Li returns 
    2H2O + 2Li -> LiOH (aq) + H2(g) + heat).
* More random number/string generation:
  * Integers (e.g. "random int", "randon long").
  * Specify number of bits.
  * Specify UUID version.
* Detect and decode encoded data without context, including:
  * Unicode code point
  * Basic ciphers (e.g. Caeser, Vignere, etc)
  * Any number and return as much data about that number (e.g. is it
    prime, factors, atomic element with given number if any, etc).
  * Bible verse
  * Post/zip codes
  * Abbreviations + acronyms including country codes, file extensions,
    etc.

## Building From Source

Install maven and jdk11+.

    mvn package 

### Deploy to Tomcat

Install and configure [Apache Tomcat](https://tomcat.apache.org/).

Create `~/.m2/settings.xml` using `example_settings.xml` as a guide. Set Tomcat credentials.

The first build and deployment can be made by running:

    mvn package
    mvn cargo:deploy 
    
Then to re-deploy:
    
    mvn package
    mvn cargo:redeploy 

Pass `-P `argument to the deploy command to deploy to a production instance, otherwise it will deploy to the development instance. See `example_settings.xml` for more information.

### Docker

You can build the Docker image yourself and run it locally:

    mvn package
    docker build -t aleppard/argus:0.1 .
    docker run -p 3000:8080 -it aleppard/argus:0.1

Pass `-d` argument to `docker run` to run detached (i.e. in the background).
    
You may need to prefix the `docker` commands with `sudo`.

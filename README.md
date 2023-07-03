# Argus Private Search Proxy

Argus is a privacy focused web search engine proxy and knowledge
engine that tries to minimise reliance on external search engines 
(such as Google) and single function websites (e.g. time zone
converters). Its purpose is to maximise the number of queries that can be
performed locally, and for those queries that cannot be performed
locally to maximise the number that can be performed directly on the
websites that can supply answers to those queries. The remaining
queries are passed to DuckDuckGo (or other search engine as
configured). Note that queries that are forwarded to DuckDuckGo (or
other) are no more private than those run directly on their website.

Argus provides three major benefits over using a search engine
directly:

* Enhanced privacy.
* Increased performance for queries that can be performed locally.
* Reduced reliance on network access. Locally performed queries do not
  require network access.

![Screenshot!](/screenshot.jpg)

## Features

Currently Argus only supports a very limited set of queries:

### Bang Queries

Argus supports a subset of [DuckDuckGo's bang commands](https://duckduckgo.com/bang) to route queries directly to the desired website, e.g. typing the following:

    !a wuthering heights

Would search for Wuthering Heights directly on Amazon's website removing the need to visit DuckDuckGo which would then re-direct to Amazon.

Other supported bang commands include:

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
| `!ma` | Run the query directly on Marginalia |

Queries can be prefixed with "@" for Firefox style at queries, e.g.

    @amazon Wuthering heights

See [the full list of supported commands](src/main/resources/default_settings.yaml).

### Math Engine

Argus has an [integrated math engine](https://bitbucket.org/axelclk/symja_android_library/src/master/).

| example query | result |
| --- | --- |
| sin(30°) | 0.5 |

### Conversion Library

Argus supports numeric conversions, and conversions from one base
(radix) to another:

| example query | result |
| --- | --- |
| mi to km | 1mi = 1.609344km |
| 300F in celsius | 148.8886705555555893186492°C = 300°F |
| 10 in hex | 0xA |
| 10 in binary | 0b1010 |
| 1010 binary to hex | 0xA |
| convert FE to dec | 254 |

### Random Number Generation

Argus supports generating a limited set of random numbers:

| example query | result |
| --- | --- |
| random hex | 946B2D46764836F904A0B4B842571469C83F6AF2AF619CAC0973B989A34C0C9C | 
| random password | 4n$Kg4l1@M$C |
| random uuid | 3c68645a-ed8b-4b28-81cc-8faa0bbb5bbe |

### Base64 & JWT Decoding

Argus supports decoding UTF-8 base64 encoded strings and JWTs (JSON
Web Tokens):

| example query | result |
| --- | --- |
| dXNlcjpwYXNzd29yZAo= | user:password |
| ey...J9.ey...fQ.Sf...5c | Header { "typ: "JWT", ... } |
| ! safasfsd | No results found. |

Passing "! " before the query will stop the query being forwarded to
an external web browser if the token couldn't be decoded locally.

### Colour Decoding

Argus supports decoding an encoded colour string and displaying that colour.

| example query | result |
| --- | --- |
| #FF0000 | Background colour changes to red |
| rgb(255, 0, 0) | Background colour changes to red |

### Character information

Argus supports taking a single character (ASCII or Unicode) and
returning information (e.g. Unicode code point) for that character.
It also supports searching the unicode characters for specific
characters based on their name.

| example query | result |
| --- | --- |
| ✔ | Information about U+2714 HEAVY CHECK MARK (✔) |
| a | Information about letter a |
| unicode greek | List all Greek unicode characters |
| U+2714 | Information about U+2714 HEAVY CHECK MARK (✔) |

### Word Pattern Matching

Argus supports matching words based on a pattern.

| example query | result |
| --- | --- |
| h?ve | have, hive, hove | 

### Time

Argus supports returning the local time in human readable format, ISO
8601 format and Unix epoch. It can also convert an ISO 8601 formatted
date to the local time and to Unix epoch and back-again.

| example query | result |
| --- | --- |
| now, time, current time | Various formats for the current time | 
| 2020-01-01T12:00:00Z | Various formats for the time |
| 1675050793 epoch | Various formats for the time |

## How to Run

### Docker

The simplest way to try Argus is using Docker. Install [Docker](https://www.docker.com/) and run the following command:

    docker run --rm -p 3000:3000 -it aleppard/argus:0.1

or

    docker run --rm -p 3000:3000 -it aleppard/argus:0.1 -d
    docker stop <container id>

You may need to prefix the `docker` commands with `sudo`.

You can then visit <http://localhost:3000> in your browser.

The container is available in DockerHub [here](https://hub.docker.com/r/aleppard/argus).

You can also build and run the Docker image yourself.

If you want to be able to configure Argus (and later store data) you'll need 
to create a volume and set a password to access settings:

    docker volume create argus
    
Then

    docker run -e ARGUS_ADMIN_PASSWORD='mysecretpassword' -p 3000:3000 -it --mount source=argus,target=/argus aleppard/argus:0.1

## Setting as default search

### Firefox

You can set Argus to be Firefox' default search engine (including running queries directly in the address bar if enabled) by following these steps:

1. Visit the web page of your local Argus server in Firefox.
2. Right-click on the address bar and select "Add Argus".
2. Go to Firefox -> Preferences -> Search then select "Argus" as the Default Search Engine.

## Future Improvements

Future improvements could include:

* Set up a public site running Argus (possibly an Onion site).
* Add "copy to clipboard" button for locally returned results.
* Add ability to hit return twice to forward locally returned results
  directly to DuckDuckGo (e.g. if a locally returned result is not
  sufficient).
* Custom bang commands.
* Support more bang commands.
* Improved math support.
  * Better support for integrated math library (e.g. expose symbolic functionality).
  * A REPL style console to perform multiple math operations.
  * Display equations in MathML.
  * Plot equations.
  * Factorisation / prime test.
* Ability to solve simple physical math problems with units, e.g.
  * What is the volume of a cylinder 1m high by 0.5m diameter?
  * How many acres in a 2km circle?
  * What is the weight of a 1m iron cube?
  * 5mins + 10mins + 2hrs
* Add support for imperial numbers in the form feet'inches", e.g. 5'7".
* Support search through local text, HTML or other documents.
* Add ability to upload HTML archives and have Argus store, index and
  search those pages.
* Add ability to index (and optionally download) specific websites and
  perhaps share those index results with other Argus users,
  e.g. peer-to-peer web indexing.
* Support search of local images using ML.
* A model to translate questions into standardised queries
  (e.g. BERT).
* A LLM to answer questions.
  * Perhaps trained on
    Wikipedia/Wikidata/[DBpedia](https://www.dbpedia.org).
  * Or use an OpenSource LLM.
* Support a local copy of Wikipedia/Wikidata/DBpedia.
* Support parsing and querying the semantic web (i.e. owl, json+ld, rdf etc).
* Support a local copy of OpenStreetMap.
* An integrated dictionary to handle spelling, definitions, anagrams, pronunciation etc.
* An integrated thesaurus to handle synonyms, antonyms etc.
* Ability to translate words and phrases. Perhaps we can integrate
  [FreeDict](https://freedict.org/).
* Time conversion to other time zones including finding the day of the
  week that a date falls on, and finding the number of days or weeks until a
  given date, and finding a date plus or minus a number of days,
  weeks.
* Cache recent queries and provide a "private" search mode to disable caching.
* Direct links to websites or calls to external APIs for weather, currency conversion, crypto & stock quotes etc.
* Ability to search local databases / uploaded spreadsheets / uploaded
  csv files.
* Provide a plug-in architecture to support custom queries.
* A REST API that can be used to build private virtual assistants
  (e.g. replacements for Alexa, Siri, etc).
* Calculate hashes of UTF-8 text strings.
* Additional colour decoding, naming of colour (perhaps using ML) and
  also HTML colour names and coverting to other colour spaces.
* Ability to search and return books, stories, plays, poetry, proverbs,
  fables, librettos, lyrics etc that are no longer under copyright.
  Perhaps integrate https://github.com/Lyrics/lyrics
* Ability to search (by name, description and using ML) and return
  scans of popular art works that are no longer under copyright.
* Identify (and continue) numeric sequences.
* Chemistry:
  * Enter chemical formula, e.g. C2H5OH and return the chemical name and
    structure.
  * Enter chemical name and show formula and structure.
  * Solve or query chemistry equations (e.g. H2O + Li returns 
    2H2O + 2Li -> LiOH (aq) + H2(g) + heat).
* More random number/string generation:
  * Integers (e.g. "random int", "randon long").
  * Specify or generate different UUID versions.
  * Generate random passphrases.
  * Generate randon numbers in specified range, eg. "random [0, 12)".
  * Add entropy on client-side to improve security.
* Detect and decode encoded data without context, including:
  * Unix epoch
  * Basic ciphers (e.g. Caeser, Vignere, etc)
  * Any number and return as much data about that number (e.g. is it
    prime, factors, atomic element with given number if any, etc).
  * Bible verse
  * Post/zip codes
  * Abbreviations + acronyms including country codes, file extensions,
    etc.
* Sounds:
  * Identify wavelength/frequency. 
  * Convert wavelength to/from frequency.
  * Generate sound from frequency (Hz), from wavelength, from note 
    (e.g. "middle-c"), chord name, text or phonetics.

## Building and running from Source

Argus is written using a Java SpringBoot back-end with a React +
tailwindcss front-end.

Install maven and jdk17+ then run:

    mvn spring-boot:run

Pass `-Pproduction` argument to build a production version without
frontend debug information.

You can then visit <http://localhost:3000> in your browser.

### Deploying

#### Jar

You can deploy the jar by first building it:

    mvn package
    
Pass `-Pproduction` argument to `mvn package` for production.

You can then copy the `jar` to the desired place:

    cp target/argus.jar argus.jar
    
Then you can run the jar with:

    java -jar argus.jar

#### Docker

You can build the Docker image yourself:

    mvn package
    docker build -t aleppard/argus:0.1 .

Pass `-Pproduction` argument to `mvn package` for production.

Then you can run the Docker container as described above.

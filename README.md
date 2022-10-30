# Argus Private Search Proxy

Argus is a privacy focused web search engine proxy that tries to minimise reliance on external search engines such as Bing and Google. Its purpose is to maximise the number of queries that can be performed locally, and for those queries that cannot be performed locally to maximise the number that can be performed directly on the websites that can supply answers to those queries. The remaining queries are passed to DuckDuckGo.

![Screenshot!](/screenshot.jpg)

## Features

Currently the only feature Argus supports is implementing a subset of [DuckDuckGo's bang commands](https://duckduckgo.com/bang) to route queries directly to the desired website, e.g. typing the following:

    !a wuthering heights

Would search for Wuthering Heights directly on Amazon's website removing the need to visit DuckDuckGo which would then re-direct to Amazon.

Other supported bang commands are:

| bang command | action |
| --- | --- |
| `!so` | Run the query directly on StackOverflow |
| `!w` | Run the query direclty on Wikipedia |

To run the query directly on a specific search engine:

| bang command | search engine |
| --- | --- |
| `!b` | Run the query directly on Bing |
| `!ddg` | Run the query directly on DuckDuckGo |
| `!g` | Run the query directly on Google |

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

* Custom bang commands.
* Support more bang commands.
* A computation engine to handle math, logic and unit conversion queries.
* Supporting search through local text, HTML or other documents.
* Supporting search of local images using ML.
* A ML model to translate questions into queries (e.g. BERT).
* A question and answer engine perhaps trained on Wikipedia. 
* Supporting a local copy of Wikipedia.
* Supporting a local copy of OpenStreetMap.
* An integrated dictionary to handle spelling, definitions, anagrams, pronunciation etc.
* An integrated thesaurus to handle synonyms, antonyms etc.
* Time conversion.
* A "local only" search mode.
* Cache recent queries and provide a "private" search mode to disable caching.
* Direct links to websites or calls to external APIs for weather, currency conversion, crypto & stock quotes etc.
* Ability to search local databases.
* A REST API that can be used to build private virtual assistants (e.g. replacements for Alexa, Siri, etc).

## Building From Source

Install maven and jdk.

    mvn package 

### Deploy to Tomcat

Install and configure [Apache Tomcat](https://tomcat.apache.org/).

Create ~/.m2/settings.xml and set Tomcat credentials. See example_settings.xml for an example file.

The first build and deployment can be made by running:

    mvn package
    mvn carg:deploy 
    
Then to re-deploy:
    
    mvn package
    mvn carg:redeploy 

Pass -P argument to the deploy command to deploy to a production instance, otherwise it will deploy to the development instance. See example_settings.xml for more information.

### Docker

You can build the Docker image yourself and run it locally:

    mvn package
    docker build -t aleppard/argus:0.1 .
    docker run -p 3000:8080 -it aleppard/argus:0.1

You may need to prefix the `docker` commands with sudo.

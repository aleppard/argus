////////////////////////////////////////////////////////////////////////////////
package com.argus.resolver;

import com.argus.DictionaryDefinitionQueryResult;
import com.argus.Query;
import com.argus.QueryResult;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.logging.Logger;

import org.apache.commons.text.similarity.LevenshteinDistance;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * Returns dictionary definition of word or phrase.
 *
 * This also supportrs looking up mispelt words if the user includes the word "spell", 
 * "spelling" or even just "sp", e.g. "sp thoroug" should return the dictionary definition 
 * for "thorough".
 */
public class DictionaryResolver implements Resolver
{
   private static final Logger LOGGER =
       Logger.getLogger(DictionaryResolver.class.getName());

    // This file is downloaded from:
    // https://kaikki.org/dictionary/rawdata.html
    // It would be good getting the data directly but my god the format
    // is bad.
    // @todo Rather than reading this file from disk we should have a page that
    // lets the user select and download third party data.
    private static final String RAW_FILE_NAME = "raw-wiktextract-data.jsonl";

    // @todo We should store the processed data into a database not a file.
    // Perhaps HSQLDB to make things easy for now?
    private static final String PROCESSED_FILE_NAME =
        "processed-wiktextract-data.jsonl";     
    
    // A dictionary can have multiple entries for the same word, e.g.
    // if it has different meanings.
    private Map<String, List<DictionaryEntry>> dictionary = new HashMap<>();
    
    public DictionaryResolver() {
        if (new File(PROCESSED_FILE_NAME).exists()) {
            readProcessedFile();
        }
        else if (new File(RAW_FILE_NAME).exists()) {
            if (readRawFile()) {
                writeProcessedFile();
            }
        }
        else {
            LOGGER.info("Can't find file " + RAW_FILE_NAME + ". Dictionary queries are disabled. You can download the file at https://kaikki.org/dictionary/rawdata.html");
        }
    }

    private void writeProcessedFile() {
        // @todo Should add a header with a version so we know if we need to regenerate.
        if (dictionary.isEmpty()) return;

        File file = new File(PROCESSED_FILE_NAME);

        try (BufferedWriter bufferedWriter =
             new BufferedWriter(new FileWriter(file))) {
            
            for (Map.Entry<String, List<DictionaryEntry>> entity :
                     dictionary.entrySet()) {
                for (final DictionaryEntry entry : entity.getValue()) {
                    JSONObject object = new JSONObject();
                    object.put("word", entity.getKey());
                    object.put("part_of_speech", entry.partOfSpeech);
                    object.put("definition", entry.definition);
                    bufferedWriter.write(object.toString());
                    bufferedWriter.newLine();
                }
            }
        }
        catch (IOException exception) {
            // @todo Better error handling.
            LOGGER.severe("Error writing file " + PROCESSED_FILE_NAME + ".");
        }
    }

    private boolean readRawFile() {
        File file = new File(RAW_FILE_NAME);
        
        try {
            Reader reader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(reader);

            LOGGER.info("Reading " + RAW_FILE_NAME + "...");

            // @todo We should store this data into a database rather
            // than writing it out to a JSON file that we have to read
            // completely at start-up.
            String line;

            // @todo We could parallelise some of this processing. However,
            // it's a once-off and only takes a few minutes so probably not
            // worth it.
            while ((line = bufferedReader.readLine()) != null) {
                JSONObject object = new JSONObject(new JSONTokener(line));
                if (object.has("word")) {
                    final String languageCode = object.getString("lang_code");

                    // @todo Include foreign language words and support word
                    // translation.
                    // @todo Include pronouciation, quotes, etymology etc.
                    // @todo Use language code symbol here.
                    if (languageCode.equals("en")) {
                        // @todo Abstract functionality
                        final String word = object.getString("word");

                        if (object.has("pos")) {
                            // @todo Investigate entries that don't have this.
                            final String partOfSpeech = object.getString("pos");
                            final JSONArray senses = object.getJSONArray("senses");
                            for (int i = 0; i < senses.length(); i++) {
                                final JSONObject sense = senses.getJSONObject(i);
                                
                                if (sense.has("glosses")) {
                                    final JSONArray glosses = sense.getJSONArray("glosses");
                                    // Definition will be in last position.
                                    // Any previous relate to groups, with
                                    // the first being the parent group, and
                                    // then the child group etc.
                                    String definition =
                                        glosses.getString(glosses.length() - 1);
                                    DictionaryEntry entry = new DictionaryEntry();
                                    entry.partOfSpeech = partOfSpeech;
                                    entry.definition = definition;
                                    dictionary.computeIfAbsent
                                        (word,
                                         k -> new ArrayList<>()).add(entry);                                    
                                }
                            }
                        }
                    }
                }
            }

            LOGGER.info("Done.");
            return true;
        }
        catch (IOException exception) {
            // @todo Better error handling.
            LOGGER.severe("Error reading file " + RAW_FILE_NAME + ".");
        }

        return false;
    }

    private void readProcessedFile() {
        File file = new File(PROCESSED_FILE_NAME);
        
        try {
            Reader reader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(reader);

            LOGGER.info("Reading " + PROCESSED_FILE_NAME + "...");

            String line;

            while ((line = bufferedReader.readLine()) != null) {
                final JSONObject object = new JSONObject(new JSONTokener(line));
                final String word = object.getString("word");
                DictionaryEntry entry = new DictionaryEntry();
                entry.partOfSpeech = object.getString("part_of_speech");
                entry.definition = object.getString("definition");
                dictionary.computeIfAbsent(word,
                                           k -> new ArrayList<>()).add(entry);
            }

            LOGGER.info("Done.");
        }
        catch (IOException exception) {
            // @todo Better error handling.
            LOGGER.info("Error reading file " + PROCESSED_FILE_NAME + ".");
        }
    }

    private static boolean isSpellingWordOrAbbreviation(final String word) {
        // @todo Also mispelling, mispelt etc.
        return (word.equals("spell") ||
                word.equals("spelling") ||
                word.equals("speling") ||
                word.equals("sp"));
    }

    private QueryResult findMispeltWord(final Query query,
                                        final String mispeltWord) {
        // @todo We could use a BK-Tree for faster spell check but I don't think we
        // need it.
        int bestScore = Integer.MAX_VALUE;
        String bestWord = null;
        List<DictionaryEntry> bestEntries = null;

        // There is no need to calculate the exact distance between vastly different
        // words, e.g. pet and elephant.
        LevenshteinDistance levenshteinDistance = new LevenshteinDistance(4);

        for (final String dictionaryWord : dictionary.keySet()) {
            Integer distance = levenshteinDistance.apply(mispeltWord, dictionaryWord);
            if (distance == -1) continue;
            if (distance <= bestScore) {
                // @todo For equal scores, compare by word frequency to return the most
                // likely match.
                // @todo For equal scores, preference words that start with the same
                // letter.
                // @todo If a word is small don't suggest another word that has all
                // the letters (or most) changed.
                // @todo Get smarter than using L. distance as it doesn't take into
                // account how humans are likely to mispel words.
                bestScore = distance;
                bestWord = dictionaryWord;
                bestEntries = dictionary.get(dictionaryWord);
            }
        }

        if (bestWord == null) return null;
        
        return new DictionaryDefinitionQueryResult(query,
                                                   bestWord,
                                                   bestEntries);
    }
    
    public @Override QueryResult tryResolve(final Query query) {
        if (dictionary.isEmpty()) return null;

        // Do we have an exact match?
        final String queryString = query.getNormalisedString();
        final List<DictionaryEntry> entries = dictionary.get(queryString);
        if (entries != null) {
            return new DictionaryDefinitionQueryResult(query, queryString,
                                                       entries);
        }

        // If not see if the user is looking up the spelling of a word.
        
        // We say "word" here but the dictionary does have multi-word terms
        // too, e.g. "mens rea".
        final List<String> normalisedWordList = query.getNormalisedWordList();
        if (normalisedWordList.size() >= 2) {
            if (isSpellingWordOrAbbreviation(normalisedWordList.get(0))) {
                return findMispeltWord(query,
                                       String.join
                                       (" ",
                                        normalisedWordList.subList
                                        (1,
                                         normalisedWordList.size())));
            }
            else if (isSpellingWordOrAbbreviation(normalisedWordList.get
                                                  (normalisedWordList.size() - 1))) {
                return findMispeltWord(query,
                                       String.join
                                       (" ",
                                        normalisedWordList.subList
                                        (0,
                                         normalisedWordList.size() - 1)));
            }
        }
        
        return null;
    }
}

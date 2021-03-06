/*
 * Copyright (c) 2016 Carmen Alvarez
 *
 * This file is part of Rhymer.
 *
 * Rhymer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Rhymer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Rhymer.  If not, see <http://www.gnu.org/licenses/>.
 */

package ca.rmen.rhymer.cmu;


import ca.rmen.rhymer.PhoneType;
import ca.rmen.rhymer.WordVariant;
import org.junit.Assert;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class TestCmuDictionaryReader {

    private static final String WORDS_FILE = "src/main/resources/dictionary_files/cmudict-0.7b";
    private static final String PHONES_FILE = "src/main/resources/dictionary_files/cmudict-0.7b.phones";

    public static Map<String, PhoneType> readPhones() throws IOException {
        return CmuDictionaryReader.readPhones(new FileInputStream(PHONES_FILE));
    }

    public static Map<String, List<WordVariant>> readWords() throws IOException {
        SyllableParser syllableParser = new SyllableParser(readPhones());
        return CmuDictionaryReader.readWords(syllableParser, new FileInputStream(WORDS_FILE));
    }

    /**
     * Test that we load all the phone symbols, and spot check the phone type for a couple of them.
     */
    @Test
    public void testLoadPhones() throws IOException {
        Map<String, PhoneType> phones = readPhones();
        Assert.assertNotNull(phones);
        Assert.assertEquals(39, phones.size());
        PhoneType phoneType = phones.get("IY");
        Assert.assertEquals(PhoneType.VOWEL, phoneType);
        phoneType = phones.get("G");
        Assert.assertEquals(PhoneType.STOP, phoneType);
    }

    /**
     * Test that we correctly load the symbols for some words.
     */
    @Test
    public void testLoadWords() throws IOException {
        Map<String, List<WordVariant>> words = readWords();
        Assert.assertNotNull(words);
        Assert.assertEquals(125074, words.size());
        // ZYNDA  Z IH1 N D AH0
        testWordSyllables("zynda", "AH", "IHNDAH", null, words);
        // ZYMAN  Z AY1 M AH0 N
        testWordSyllables("zyman", "AHN", "AYMAHN", null, words);
        // CAT  K AE1 T
        testWordSyllables("cat", "AET", null, null, words);
        //CELEBRATE  S EH1 L AH0 B R EY2 T
        testWordSyllables("celebrate", "EYT", "AHBREYT", "EHLAHBREYT", words);
    }

    private void testWordSyllables(String word, String expectedLastSyllable, String expectedLastTwoSyllables, String expectedLastThreeSyllables, Map<String, List<WordVariant>> dict) {
        List<WordVariant> wordVariants = dict.get(word);
        Assert.assertNotNull(wordVariants);
        Assert.assertTrue(wordVariants.size() == 1);
        WordVariant wordVariant = wordVariants.get(0);
        Assert.assertEquals("Last syllable differs " + word, expectedLastSyllable, wordVariant.lastRhymingSyllable);
        Assert.assertEquals("Last two syllables differ " + word, expectedLastTwoSyllables, wordVariant.lastTwoRhymingSyllables);
        Assert.assertEquals("Last three syllables differ " + word, expectedLastThreeSyllables, wordVariant.lastThreeRhymingSyllables);
    }

}

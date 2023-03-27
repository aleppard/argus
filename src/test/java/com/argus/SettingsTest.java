////////////////////////////////////////////////////////////////////////////////
package com.argus;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Test Settings class.
 */
public class SettingsTest {

    @Test public void test() {
        // @todo Note this would not work correctly if there happens to be
        // a /argus/settings.xml file.
        final Settings settings = Settings.getInstance();
        
        assertEquals("ddg", settings.getDefaultSearchEngine());
    }

}

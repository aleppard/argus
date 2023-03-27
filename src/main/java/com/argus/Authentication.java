////////////////////////////////////////////////////////////////////////////////
package com.argus;

import java.nio.charset.StandardCharsets;

import java.util.Base64;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Provides authentication for admin access.
 */
public class Authentication
{
    /** Check that the user has authorisation for admin access. */
    public static boolean isAuthorisedForAdminAccess
        (HttpServletRequest request) {
        final String authorisationHeader = request.getHeader("Authorization");
        if (authorisationHeader != null &&
            authorisationHeader.toUpperCase().startsWith("BASIC ")) {
            final String base64Encoded = authorisationHeader.substring(6);

            try {
                final String userPassword =
                    new String(Base64.getDecoder().decode(base64Encoded),
                               StandardCharsets.UTF_8);
                final int offsetOfColon = userPassword.indexOf(':');
                
                if (offsetOfColon != -1) {
                    final String user =
                        userPassword.substring(0, offsetOfColon);
                    final String password =
                        userPassword.substring(offsetOfColon + 1);

                    final String expectedPassword =
                        System.getenv("ARGUS_ADMIN_PASSWORD");
                    if (expectedPassword != null &&
                        expectedPassword.length() > 1 &&
                        user.equals("admin") &&
                        password.equals(expectedPassword)) {
                        return true;
                    }
                }
            }
            catch (IllegalArgumentException exception) {}
        }

        return false;
    }
}

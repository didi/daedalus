package com.didichuxing.daedalus.util.curl;

public enum Mechanism {
        /**
         * @deprecated (use {@link Mechanism#BASIC})
         */
        @Deprecated
        BASIC_DIGEST,
        /**
         * Basic Auth
         */
        BASIC,
        /**
         * Digest Auth
         */
        DIGEST,
        /**
         * Kerberos Auth
         */
        KERBEROS
    }
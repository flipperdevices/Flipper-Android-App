package com.flipperdevices.faphub.dao.network.ktorfit.model

enum class KtorfitExceptionCode(val code: Int) {
    UNDEFINED(code = 0),

    UNKNOWN_SDK(code = 1001),
    UNKNOWN_ASSET(code = 1002),
    UNKNOWN_BUNDLE(code = 1003),
    UNKNOWN_CATEGORY(code = 1004),
    UNKNOWN_APPLICATION(code = 1005),
    UNKNOWN_APPLICATION_VERSION(code = 1006),
    UNKNOWN_APPLICATION_VERSION_BUILD(code = 1007),
    UNKNOWN_COMPATIBLE_APPLICATION_VERSION_BUILD(code = 1008),

    EXISTING_APPLICATION_VERSION(code = 2001),
    EXISTING_APPLICATION_VERSION_BUILD(code = 2002),
    EXISTING_CATEGORY(code = 2003),
    EXISTING_SDK(code = 2004),

    EMPTY_VERSIONS(code = 3001),
    EMPTY_LOGS(code = 3002),
    EMPTY_BUILDS(code = 3003),

    OLDEST_SDK(code = 4000),
    RELEASED_SDK(code = 4001),
    INVALID_FILE(code = 4002),
    APPLICATION_NAMING(code = 4003);

    companion object {
        fun fromCode(code: Int): KtorfitExceptionCode {
            return KtorfitExceptionCode.values().find { it.code == code } ?: UNDEFINED
        }
    }
}

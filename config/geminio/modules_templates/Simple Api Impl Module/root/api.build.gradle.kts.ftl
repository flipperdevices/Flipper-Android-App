plugins {
<#if needCompose>
    id("flipper.android-compose")
<#else>
    id("flipper.android-lib")
</#if>
}

android.namespace = "${packageName}.api"

dependencies {
}

package ${packageName}.api

import ${packageName?keep_before(".impl")}.api.${__formattedModuleName}Api
import ${packageName}.fragments.${__formattedModuleName}Fragment
<#if shouldGenerateDI>
import com.flipperdevices.core.di.AppGraph
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject
</#if>

<#if shouldGenerateDI>
@ContributesBinding(AppGraph::class)
class ${__formattedModuleName}ApiImpl @Inject constructor() : ${__formattedModuleName}Api {
<#else>
class ${__formattedModuleName}ApiImpl : ${__formattedModuleName}Api {
</#if>
}

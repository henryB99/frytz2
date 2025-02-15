package dev.fritz2.headlessdemo

import dev.fritz2.core.RenderContext
import dev.fritz2.core.classes
import dev.fritz2.core.storeOf
import dev.fritz2.headless.components.inputField
import kotlinx.coroutines.flow.map

fun RenderContext.inputFieldDemo() {

    val name = storeOf("", id = "inputField")

    div("max-w-sm") {

        inputField("mb-8") {
            value(name)
            placeholder("The name is...")
            inputLabel("block mb-1.5 ml-1 text-sm font-medium text-primary-800") {
                +"Enter the framework's name"
            }
            div("mt-2") {
                inputTextfield(
                    """w-full py-2.5 px-2.5
                        | bg-white rounded
                        | font-sans text-sm 
                        | disabled:opacity-50""".trimMargin()
                ) {
                    className(value.hasError.map {
                        if (it) classes(
                            """border border-error-600 
                                | text-error-800 placeholder:text-error-400
                                | hover:border-error-800  
                                | focus:outline-none focus:ring-4 focus:ring-error-600 focus:border-error-800""".trimMargin()
                        )
                        else classes(
                            """border border-primary-600 
                                | text-primary-800 placeholder:text-slate-400
                                | hover:border-primary-800  
                                | focus:outline-none focus:ring-4 focus:ring-primary-600 focus:border-primary-800""".trimMargin()
                        )
                    })
                }
            }
            inputDescription("ml-1 mt-2 text-xs text-primary-700") {
                +"The name should reflect the concept of the whole framework."
            }
        }

        div(
            """mt-4 p-2.5
            | bg-primary-100 rounded shadow-sm
            | ring-2 ring-primary-500 
            | text-sm text-primary-800
            | focus:outline-none focus:ring-4 focus:ring-primary-600 focus:border-primary-800""".trimMargin(),
            id = "result"
        ) {
            attr("tabindex", "0")
            span("font-medium") { +"Name: " }
            span { name.data.renderText() }
        }
    }

}

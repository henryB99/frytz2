@file:Suppress("unused")

package dev.fritz2.core

import kotlinx.browser.window
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.shareIn
import org.w3c.dom.*
import org.w3c.dom.Window
import org.w3c.dom.clipboard.ClipboardEvent
import org.w3c.dom.events.*
import org.w3c.xhr.ProgressEvent

/**
 * Contains all javascript event types.
 * Take a look [here](https://www.w3schools.com/jsref/dom_obj_event.asp).
 *
 * Sometimes it is necessary to use a more generic type (like [Event])
 * because the type that is offered to the listener is not always consistent
 * (on different browsers, different actions, etc.)
 */
interface WithEvents<out T: EventTarget> {

    /**
     * Creates an [Listener] for the given event [name].
     *
     * @param name of the [Event] to listen for
     */
    fun <X : Event> subscribe(name: String): Listener<X, T>

    /**
     * occurs when the loading of a media is aborted
     */
    val aborts get() = subscribe<Event>("abort")

    /**
     * occurs when a page has started printing, or if the print dialogue box has been closed
     */
    val afterprints get() = subscribe<Event>("afterprint")

    /**
     * occurs when a page is about to be printed
     */
    val beforeprints get() = subscribe<Event>("beforeprint")

    /**
     * occurs before the document is about to be unloaded
     */
    val beforeunloads get() = subscribe<Event>("beforeunload")

    /**
     * occurs when an element loses focus
     */
    val blurs get() = subscribe<FocusEvent>("blur")

    /**
     * occurs when the browser can start playing the media (when it has buffered enough to begin)
     */
    val canplays get() = subscribe<Event>("canplay")

    /**
     * occurs when the browser can play through the media without stopping for buffering
     */
    val canplaythroughs get() = subscribe<Event>("canplaythrough")

    /**
     * occurs when the content of a form element, the selection, or the checked state have changed
     * (for `<input>`, `<select>`, and `<textarea>`)
     */
    val changes get() = subscribe<Event>("change")

    /**
     * occurs when the user clicks on an element
     */
    val clicks get() = subscribe<MouseEvent>("click")

    /**
     * occurs when the user right-clicks on an element to open a context menu
     */
    val contextmenus get() = subscribe<MouseEvent>("contextmenu")

    /**
     * occurs when the user copies the content of an element
     */
    val copys get() = subscribe<ClipboardEvent>("copy")

    /**
     * occurs when the user cuts the content of an element
     */
    val cuts get() = subscribe<ClipboardEvent>("cut")

    /**
     * occurs when the user double-clicks on an element
     */
    val dblclicks get() = subscribe<MouseEvent>("dblclick")

    /**
     * occurs when an element is being dragged
     */
    val drags get() = subscribe<DragEvent>("drag")

    /**
     * occurs when the user has finished dragging an element
     */
    val dragends get() = subscribe<DragEvent>("dragend")

    /**
     * occurs when the dragged element enters the drop target
     */
    val dragenters get() = subscribe<DragEvent>("dragenter")

    /**
     * occurs when the dragged element leaves the drop target
     */
    val dragleaves get() = subscribe<DragEvent>("dragleave")

    /**
     * occurs when the dragged element is over the drop target
     */
    val dragovers get() = subscribe<DragEvent>("dragover")

    /**
     * occurs when the user starts to drag an element
     */
    val dragstarts get() = subscribe<DragEvent>("dragstart")

    /**
     * occurs when the dragged element is dropped on the drop target
     */
    val drops get() = subscribe<DragEvent>("drop")

    /**
     * occurs when the duration of the media is changed
     */
    val durationchanges get() = subscribe<Event>("durationchange")

    /**
     * occurs when the media has reach the end (useful for messages like "thanks for listening")
     */
    val endeds get() = subscribe<Event>("ended")

    /**
     * occurs when an element gets focus
     */
    val focuss get() = subscribe<FocusEvent>("focus")

    /**
     * occurs when an element is about to get focus
     */
    val focusins get() = subscribe<FocusEvent>("focusin")

    /**
     * occurs when an element is about to lose focus
     */
    val focusouts get() = subscribe<FocusEvent>("focusout")

    /**
     * occurs when an element is displayed in fullscreen mode
     */
    val fullscreenchanges get() = subscribe<Event>("fullscreenchange")

    /**
     * occurs when an element can not be displayed in fullscreen mode
     */
    val fullscreenerrors get() = subscribe<Event>("fullscreenerror")

    /**
     * occurs when there has been changes to the anchor part of a URL
     */
    val hashchanges get() = subscribe<HashChangeEvent>("hashchange")

    /**
     * occurs when an element gets user input has to use Event as type because Chrome and Safari offer Events instead
     * of InputEvents when selecting from a datalist
     */
    val inputs get() = subscribe<Event>("input")

    /**
     * occurs when an element is invalid
     */
    val invalids get() = subscribe<Event>("invalid")

    /**
     * occurs when the user is pressing a key
     */
    val keydowns get() = subscribe<KeyboardEvent>("keydown")

    /**
     * occurs when the user presses a key
     */
    val keypresss get() = subscribe<KeyboardEvent>("keypress")

    /**
     * occurs when the user releases a key
     */
    val keyups get() = subscribe<KeyboardEvent>("keyup")

    /**
     * occurs when an object has loaded
     */
    val loads get() = subscribe<Event>("load")

    /**
     * occurs when media data is loaded
     */
    val loadeddatas get() = subscribe<Event>("loadeddata")

    /**
     * occurs when metadata (like dimensions and duration) are loaded
     */
    val loadedmetadatas get() = subscribe<Event>("loadedmetadata")

    /**
     * occurs when the pointer is moved onto an element
     */
    val mouseenters get() = subscribe<MouseEvent>("mouseenter")

    /**
     * occurs when the pointer is moved out of an element
     */
    val mouseleaves get() = subscribe<MouseEvent>("mouseleave")

    /**
     * occurs when the pointer is moving while it is over an element
     */
    val mousemoves get() = subscribe<MouseEvent>("mousemove")

    /**
     * occurs when the pointer is moved onto an element, or onto one of its children
     */
    val mouseovers get() = subscribe<MouseEvent>("mouseover")

    /**
     * occurs when a user moves the mouse pointer out of an element, or out of one of its children
     */
    val mouseouts get() = subscribe<MouseEvent>("mouseout")

    /**
     * occurs when a user releases a mouse button over an element
     */
    val mouseups get() = subscribe<MouseEvent>("mouseup")

    /**
     * occurs when the browser starts to work offline
     */
    val offlines get() = subscribe<Event>("offline")

    /**
     * occurs when the browser starts to work online
     */
    val onlines get() = subscribe<Event>("online")

    /**
     * occurs when a connection with the event source is opened
     */
    val opens get() = subscribe<Event>("open")

    /**
     * occurs when the user navigates away from a webpage
     */
    val pagehides get() = subscribe<PageTransitionEvent>("pagehide")

    /**
     * occurs when the user navigates to a webpage
     */
    val pageshows get() = subscribe<PageTransitionEvent>("pageshow")

    /**
     * occurs when the user pastes some content in an element
     */
    val pastes get() = subscribe<ClipboardEvent>("paste")

    /**
     * occurs when the browser starts looking for the specified media
     */
    val loadstarts get() = subscribe<ProgressEvent>("loadstart")

    /**
     * occurs when a message is received through the event source
     */
    val messages get() = subscribe<Event>("message")

    /**
     * occurs when the user presses a mouse button over an element
     */
    val mousedowns get() = subscribe<MouseEvent>("mousedown")

    /**
     * occurs when the media is paused either by the user or programmatically
     */
    val pauses get() = subscribe<Event>("pause")

    /**
     * occurs when the media has been started or is no longer paused
     */
    val plays get() = subscribe<Event>("play")

    /**
     * occurs when the media is playing after having been paused or stopped for buffering
     */
    val playings get() = subscribe<Event>("playing")

    /**
     * occurs when the window's history changes
     */
    val popstates get() = subscribe<PopStateEvent>("popstate")

    /**
     * occurs when the browser is in the process of getting the media data (downloading the media)
     */
    val progresss get() = subscribe<Event>("progress")

    /**
     * occurs when the playing speed of the media is changed
     */
    val ratechanges get() = subscribe<Event>("ratechange")

    /**
     * occurs when the document view is resized
     */
    val resizes get() = subscribe<Event>("resize")

    /**
     * occurs when a form is reset
     */
    val resets get() = subscribe<Event>("reset")

    /**
     * occurs when an element's scrollbar is being scrolled
     */
    val scrolls get() = subscribe<Event>("scroll")

    /**
     * occurs when the user writes something in a search field (for <input="search">)
     */
    val searchs get() = subscribe<Event>("search")

    /**
     * occurs when the user is finished moving/skipping to a new position in the media
     */
    val seekeds get() = subscribe<Event>("seeked")

    /**
     * occurs when the user starts moving/skipping to a new position in the media
     */
    val seekings get() = subscribe<Event>("seeking")

    /**
     * occurs after the user selects some text (for <input> and <textarea>)
     */
    val selects get() = subscribe<Event>("select")

    /**
     * occurs when a <menu> element is shown as a context menu
     */
    val shows get() = subscribe<Event>("show")

    /**
     * occurs when the browser is trying to get media data, but data is not available
     */
    val stalleds get() = subscribe<Event>("stalled")

    /**
     * occurs when a Web Storage area is updated
     */
    val storages get() = subscribe<StorageEvent>("storage")

    /**
     * occurs when a form is submitted
     */
    val submits get() = subscribe<Event>("submit")

    /**
     * occurs when the browser is intentionally not getting media data
     */
    val suspends get() = subscribe<Event>("suspend")

    /**
     * occurs when the playing position has changed (like when the user fast forwards to a different point in the media)
     */
    val timeupdates get() = subscribe<Event>("timeupdate")

    /**
     * occurs when the user opens or closes the <details> element
     */
    val toggles get() = subscribe<Event>("toggle")

    /**
     * occurs when the touch is interrupted
     */
    val touchcancels get() = subscribe<TouchEvent>("touchcancel")

    /**
     * occurs when a finger is removed from a touch screen
     */
    val touchends get() = subscribe<TouchEvent>("touchend")

    /**
     * occurs when a finger is dragged across the screen
     */
    val touchmoves get() = subscribe<TouchEvent>("touchmove")

    /**
     * occurs when a finger is placed on a touch screen
     */
    val touchstarts get() = subscribe<TouchEvent>("touchstart")

    /**
     * occurs once a page has unloaded (for <body>)
     */
    val unloads get() = subscribe<Event>("unload")

    /**
     * occurs when the volume of the media has changed (includes setting the volume to "mute")
     */
    val volumechanges get() = subscribe<Event>("volumechange")

    /**
     * occurs when the media has paused but is expected to resume (like when the media pauses to buffer more data)
     */
    val waitings get() = subscribe<Event>("waiting")

    /**
     * occurs when the mouse wheel rolls up or down over an element
     */
    val wheels get() = subscribe<WheelEvent>("wheel")
}

/**
 * Represents all [Event]s of the browser [window] object as [Event]-flows
 */
object Window : WithEvents<Window> {

    private val scope = MainScope()

    override fun <X : Event> subscribe(name: String): Listener<X, Window> = Listener(callbackFlow {
        val listener: (Event) -> Unit = {
            try {
                trySend(it.unsafeCast<X>())
            } catch (e: Exception) {
                console.error("Unexpected type while listening for `$name` events in Window object", e)
            }
        }
        window.addEventListener(name, listener)

        awaitClose { window.removeEventListener(name, listener) }
    }.shareIn(scope, SharingStarted.Lazily))

    override val aborts by lazy { super.aborts }
    override val afterprints by lazy { super.afterprints }
    override val beforeprints by lazy { super.beforeprints }
    override val beforeunloads by lazy { super.beforeunloads }
    override val blurs by lazy { super.blurs }
    override val canplays by lazy { super.canplays }
    override val canplaythroughs by lazy { super.canplaythroughs }
    override val changes by lazy { super.changes }
    override val clicks by lazy { super.clicks }
    override val contextmenus by lazy { super.contextmenus }
    override val copys by lazy { super.copys }
    override val cuts by lazy { super.cuts }
    override val dblclicks by lazy { super.dblclicks }
    override val drags by lazy { super.drags }
    override val dragends by lazy { super.dragends }
    override val dragenters by lazy { super.dragenters }
    override val dragleaves by lazy { super.dragleaves }
    override val dragovers by lazy { super.dragovers }
    override val dragstarts by lazy { super.dragstarts }
    override val drops by lazy { super.drops }
    override val durationchanges by lazy { super.durationchanges }
    override val endeds by lazy { super.endeds }
    override val focuss by lazy { super.focuss }
    override val focusins by lazy { super.focusins }
    override val focusouts by lazy { super.focusouts }
    override val fullscreenchanges by lazy { super.fullscreenchanges }
    override val fullscreenerrors by lazy { super.fullscreenerrors }
    override val hashchanges by lazy { super.hashchanges }
    override val inputs by lazy { super.inputs }
    override val invalids by lazy { super.invalids }
    override val keydowns by lazy { super.keydowns }
    override val keypresss by lazy { super.keypresss }
    override val keyups by lazy { super.keyups }
    override val loads by lazy { super.loads }
    override val loadeddatas by lazy { super.loadeddatas }
    override val loadedmetadatas by lazy { super.loadedmetadatas }
    override val loadstarts by lazy { super.loadstarts }
    override val messages by lazy { super.messages }
    override val mousedowns by lazy { super.mousedowns }
    override val mouseenters by lazy { super.mouseenters }
    override val mouseleaves by lazy { super.mouseleaves }
    override val mousemoves by lazy { super.mousemoves }
    override val mouseovers by lazy { super.mouseovers }
    override val mouseouts by lazy { super.mouseouts }
    override val mouseups by lazy { super.mouseups }
    override val offlines by lazy { super.offlines }
    override val onlines by lazy { super.onlines }
    override val opens by lazy { super.opens }
    override val pagehides by lazy { super.pagehides }
    override val pageshows by lazy { super.pageshows }
    override val pastes by lazy { super.pastes }
    override val pauses by lazy { super.pauses }
    override val plays by lazy { super.plays }
    override val playings by lazy { super.playings }
    override val popstates by lazy { super.popstates }
    override val progresss by lazy { super.progresss }
    override val ratechanges by lazy { super.ratechanges }
    override val resizes by lazy { super.resizes }
    override val resets by lazy { super.resets }
    override val scrolls by lazy { super.scrolls }
    override val searchs by lazy { super.searchs }
    override val seekeds by lazy { super.seekeds }
    override val seekings by lazy { super.seekings }
    override val selects by lazy { super.selects }
    override val shows by lazy { super.shows }
    override val stalleds by lazy { super.stalleds }
    override val storages by lazy { super.storages }
    override val submits by lazy { super.submits }
    override val suspends by lazy { super.suspends }
    override val timeupdates by lazy { super.timeupdates }
    override val toggles by lazy { super.toggles }
    override val touchcancels by lazy { super.touchcancels }
    override val touchends by lazy { super.touchends }
    override val touchmoves by lazy { super.touchmoves }
    override val touchstarts by lazy { super.touchstarts }
    override val unloads by lazy { super.unloads }
    override val volumechanges by lazy { super.volumechanges }
    override val waitings by lazy { super.waitings }
    override val wheels by lazy { super.wheels }
}
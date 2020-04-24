/*
 * Copyright (C) 2020 Sam Lu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 
@file:Suppress("unused")

package <change_to_your_package_name>.ViewScope

import android.util.Log
import android.view.View
import androidx.core.view.ViewCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import java.io.Closeable
import kotlin.coroutines.CoroutineContext

private const val KEY_VIEW_SCOPE = R.id.view_scope
private const val LOG_TAG = "ViewScope"

/**
 * [CoroutineScope] tied to this [View].
 * This scope will be canceled when the view is detached from a window.
 *
 * This scope is bound to [Dispatchers.Main.immediate]
 *
 * An example:
 * ```
 * val view: View = ...
 * view.viewScope.launch {
 *   // this statement will be run in the UI thread
 *   withContext(Dispatchers.Default) {
 *     // statements of this block are running in a worker thread and will be canceled when
 *     // this view is detached from a window (I.e. after Activity.onDestroy(), 
 *     // Fragment.onDestroyView() called or you manually removed this view from the view tree).
 *   }
 *   // this statement will be run in the UI thread
 * }
 * ```
 */
val View.viewScope: CoroutineScope
    get() {
        getTag(KEY_VIEW_SCOPE)?.let {
            if (it is CoroutineScope)
                return it
            else
                Log.e(LOG_TAG, "check why the value of KEY_VIEW_SCOPE is ${it.javaClass.name}")
        }

        //////////////////////
        val scope = CloseableCoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
        setTag(KEY_VIEW_SCOPE, scope)

        //////////////////////
        if (!ViewCompat.isAttachedToWindow(this)) {
            Log.w(LOG_TAG,
                "Creating a CoroutineScope before ${javaClass.name} attaches to a window. " +
                "Coroutine jobs won't be canceled if the view has never been attached to a window.")
        }

        //////////////////////
        addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(view: View) {}

            override fun onViewDetachedFromWindow(view: View) {
                removeOnAttachStateChangeListener(this)
                setTag(KEY_VIEW_SCOPE, null)
                scope.close()
            }
        })

        return scope
    }

internal class CloseableCoroutineScope(context: CoroutineContext) : Closeable, CoroutineScope {
    override val coroutineContext: CoroutineContext = context

    override fun close() {
        coroutineContext.cancel()
    }
}

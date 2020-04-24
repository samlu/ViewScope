# Android Kotlin ViewScope
`View.viewScope` is a `CoroutineScope` tied to a `View`. This scope will be canceled when the view is detached from a window.

## Getting started
1. Copy the `ViewScope.kt` file to your source set and change the package name
2. Add a `ids.xml` file to the [project]/app/src/main/res/values directory

The content of ids.xml is shown below:
```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
  <!-- used by ViewScope (View.viewScope) to keep an instance of CoroutineScope -->
  <item type="id" name="view_scope" />
</resources>
```

## Usage
A `ViewScope` is created when you first call `View.viewScope` for a view. Any coroutine launched in this scope is automatically canceled if the view is detached from the window. Usually, a `View` is detached from a widnow after `Activity.onDestroy()`, `Fragment.onDestroyView()` called or you manually removed this view from the view tree.

You can access the `CoroutineScope` of a `View` through the `viewScope` property of the View, as shown in the following example:
```kotlin
val view: View = ...
view.viewScope.launch {
  // this statement will be run in the UI thread
  withContext(Dispatchers.Default) {
    // statements of this block are running in a worker thread and will be canceled when
    // this view is detached from a window (I.e. after Activity.onDestroy(), 
    // Fragment.onDestroyView() called or you manually removed this view from the view tree).
  }
  // this statement will be run in the UI thread
}
```  

## License
    Copyright (C) 2019 Sam Lu

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

package com.mustafakocer.movieappfeaturebasedclean

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * The custom `Application` class for the application.
 *
 * Architectural Decision: Annotating the `Application` class with `@HiltAndroidApp` is the
 * mandatory first step to enable Dagger Hilt for dependency injection. This annotation triggers
 * Hilt's code generation, which creates a top-level dependency container attached to the
 * application's lifecycle. This container serves as the parent for all other dependency
 * containers in the app, allowing Hilt to manage dependencies on an application-wide scale.
 *
 * While this class is currently empty, it serves as a central entry point and can be used in the
 * future for initializing application-wide libraries, setting up logging, or performing other
 * tasks that need to run once when the application starts.
 */
@HiltAndroidApp
class MovieApplication : Application()
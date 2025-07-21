package com.mustafakocer.navigation_contracts.actions


/**
 * :feature-movies modülünün tüm navigasyon eylemlerini tek bir yerde toplayan
 * birleşik sözleşme.
 */
interface FeatureSplashNavActions : CommonNavActions {

    fun navigateToHome()
    fun navigateToWelcome()

}
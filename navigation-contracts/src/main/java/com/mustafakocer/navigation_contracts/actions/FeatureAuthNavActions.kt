package com.mustafakocer.navigation_contracts.actions


/**
 * :feature-movies modülünün tüm navigasyon eylemlerini tek bir yerde toplayan
 * birleşik sözleşme.
 */
interface FeatureAuthNavActions : CommonNavActions {

    fun navigateToHome()
    fun navigateToWelcome()

}
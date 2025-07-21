package com.mustafakocer.core_domain.util

/**
 * Bir Resource<T> nesnesinin içindeki başarılı veriyi (T) başka bir tipe (R)
 * dönüştürmek için kullanılır. Hata ve Yükleme durumlarını korur.
 *
 * Bu, Repository katmanlarında DTO'dan Domain modeline dönüşüm yaparken
 * 'when' bloğu tekrarını önler.
 *
 * @param T Orijinal veri tipi.
 * @param R Dönüştürülecek veri tipi.
 * @param transform Başarılı veri üzerinde uygulanacak dönüşüm fonksiyonu.
 * @return Dönüştürülmüş veriyi içeren yeni bir Resource<R> nesnesi.
 */
fun <T, R> Resource<T>.mapSuccess(transform: (T) -> R): Resource<R> {
    return when (this) {
        is Resource.Success -> Resource.Success(transform(data))
        is Resource.Error -> this
        is Resource.Loading -> Resource.Loading
    }
}
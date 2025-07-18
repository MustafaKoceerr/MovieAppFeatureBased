package com.mustafakocer.core_network.util

import com.mustafakocer.core_common.exception.AppException
import com.mustafakocer.core_common.util.Resource
import com.mustafakocer.core_network.error.ErrorMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response

/**
 * Retrofit API çağrılarını sarmalayan ve sonucu Flow<Resource<T>> olarak döndüren merkezi yardımcı fonksiyon.
 *
 * @param T API'den beklenen DTO tipidir.
 * @param apiCall Gerçekleştirilecek olan suspend Retrofit çağrısı.
 * @return API isteğinin durumunu (Loading, Success, Error) yayan bir Flow.
 */
fun <T> safeApiCall(
    apiCall: suspend () -> Response<T>,
): Flow<Resource<T>> = flow {
    // 1. Akış başladığında, UI'a yüklemenin başladığını bildir.
    emit(Resource.Loading)

    // 2. Asıl API çağrısını yap
    val response = apiCall()

    // 3. Yanıtı kontrol et.
    if (response.isSuccessful) {
        val body = response.body()
        if (body != null) {
            // Başarılı ve body dolu ise, Success durumunu yayınla.
            emit(Resource.Success(body))
        } else {
            // Başarılı ama body boş ise, bu da bir veri hatasıdır.
            emit(Resource.Error(AppException.Data.EmptyResponse))
        }
    } else {
        // Sunucudan 4xx veya 5xx gibi bir hata kodu geldiyse,
        // ErrorMapper'ı kullanarak bunu bizim hata modelimize dönüştür.
        emit(Resource.Error(ErrorMapper.mapHttpErrorResponseToAppException(response)))
    }
}.catch { e ->
    // Çağrı sırasında HttpException, IOException gibi bir istisna fırlatıldıysa,
    // ErrorMapper'ı kullanarak bunu yakala ve bizim hata modelimize dönüştür.
    emit(Resource.Error(ErrorMapper.mapThrowableToAppException(e)))
}.flowOn(Dispatchers.IO) // Tüm bu işlemleri IO thred'inde yap.
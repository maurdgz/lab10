package com.example.lab10.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Objeto Singleton que configura y proporciona la instancia de Retrofit.
 */
object ApiClient {
    // ⚠️ ATENCIÓN: Esta URL debe coincidir con la dirección donde corre tu backend de Django.
    // Si estás usando un emulador de Android (como AVD), "10.0.2.2" es la dirección
    // especial para referirse al localhost de tu máquina.
    // Asegúrate de que tu servidor Django esté corriendo en http://127.0.0.1:8000/
    private const val BASE_URL = "http://10.0.2.2:8000/"

    /**
     * Instancia de Retrofit, inicializada de forma perezosa (lazy).
     */
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    /**
     * Proporciona la instancia del servicio SerieApiService configurada.
     */
    val apiService: SerieApiService by lazy {
        retrofit.create(SerieApiService::class.java)
    }
}
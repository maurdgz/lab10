package com.example.lab10.data

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

// Interfaz para la definición de los servicios web de la API de Series
interface SerieApiService {

    // 1. Obtener todas las series (GET - READ)
    // Retorna una lista de SerieModel
    @GET("serie/")
    suspend fun selectSeries(): Response<ArrayList<SerieModel>>

    // 2. Obtener una sola serie por ID (GET - READ)
    // Retorna un objeto SerieModel
    @GET("serie/{id}")
    suspend fun selectSerie(@Path("id") id: Int): Response<SerieModel>

    // 3. Insertar una nueva serie (POST - CREATE)
    // Se envía el objeto SerieModel en el cuerpo de la solicitud
    @Headers("Content-Type: application/json")
    @POST("serie/")
    suspend fun insertSerie(@Body serie: SerieModel): Response<SerieModel>

    // 4. Actualizar una serie existente (PUT - UPDATE)
    // Se envía el ID en la ruta y el objeto actualizado en el cuerpo
    @PUT("serie/{id}")
    suspend fun updateSerie(@Path("id") id: Int, @Body serie: SerieModel): Response<SerieModel>

    // 5. Eliminar una serie (DELETE - DELETE)
    // Se envía el ID en la ruta
    @DELETE("serie/{id}")
    suspend fun deleteSerie(@Path("id") id: Int): Response<Unit>
}

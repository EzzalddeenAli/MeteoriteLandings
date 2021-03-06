package com.antonio.samir.meteoritelandingsspots.data.repository

import android.util.Log
import androidx.paging.DataSource
import com.antonio.samir.meteoritelandingsspots.data.Result
import com.antonio.samir.meteoritelandingsspots.data.Result.*
import com.antonio.samir.meteoritelandingsspots.data.local.MeteoriteLocalRepository
import com.antonio.samir.meteoritelandingsspots.data.remote.MeteoriteRemoteRepository
import com.antonio.samir.meteoritelandingsspots.data.repository.model.Meteorite
import com.antonio.samir.meteoritelandingsspots.util.DispatcherProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.IOException


@ExperimentalCoroutinesApi
class MeteoriteRepositoryImpl(
        private val meteoriteLocalRepository: MeteoriteLocalRepository,
        private val meteoriteRemoteRepository: MeteoriteRemoteRepository,
        private val dispatchers: DispatcherProvider
) : MeteoriteRepository {

    companion object {

        private const val OLDDATABASE_COUNT = 1000

        private val TAG = MeteoriteRepository::class.java.simpleName
    }

    override val pageSize: Int
        get() = 5000

    override suspend fun loadMeteorites(filter: String?, longitude: Double?, latitude: Double?): DataSource.Factory<Int, Meteorite> {
        return meteoriteLocalRepository.meteoriteOrdered(filter, latitude, longitude)
    }

    override fun getMeteoriteById(id: String): Flow<Result<Meteorite>> = flow {
        emit(InProgress())
        try {
            emitAll(meteoriteLocalRepository.getMeteoriteById(id).map { Success(it) })
        } catch (e: IOException) {
            Log.e(TAG, e.message, e)
            emit(Error(MeteoriteLocalException(e)))
        }
    }

    override suspend fun update(meteorite: Meteorite) {
        meteoriteLocalRepository.update(meteorite)
    }

    override suspend fun update(list: List<Meteorite>) {
        meteoriteLocalRepository.updateAll(list)
    }

    override fun loadDatabase(): Flow<Result<Nothing>> = flow {
        val meteoritesCount = meteoriteLocalRepository.getMeteoritesCount()
        emit(InProgress())
        try {
            recoverFromNetwork(if (meteoritesCount <= OLDDATABASE_COUNT) {
                0 //Download from beginner
            } else {
                meteoritesCount
            })
        } catch (e: Exception) {
            Log.e(TAG, e.message, e)
            emit(Error(MeteoriteServerException(e)))
        }
        emit(Success())
    }

    private suspend fun recoverFromNetwork(offset: Int) = withContext(dispatchers.io()) {

        var currentPage = 0

        do {

            val serviceOffset = (pageSize * currentPage) + offset
            val meteorites = meteoriteRemoteRepository.getMeteorites(
                    offset = serviceOffset,
                    limit = pageSize
            )

            meteoriteLocalRepository.insertAll(meteorites)

            currentPage++
        } while (meteorites.size == pageSize)

    }

}

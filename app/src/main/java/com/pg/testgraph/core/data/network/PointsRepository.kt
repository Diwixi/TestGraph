package com.pg.testgraph.core.data.network

import com.pg.testgraph.core.domain.Point
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PointsRepository(private val apiService: ApiService) {
    var points: List<Point> = emptyList()

    suspend fun fetchPoints(count: Int): Result<List<Point>> = withContext(Dispatchers.IO) {
        safeCall { Result.success(apiService.getPoints(count)) }
    }.mapCatching { pointsListResponse ->
        points = pointsListResponse.toPoints()
        points
    }

    fun saveGeneratedPoints(points: List<Point>) {
        this.points = points
    }
}

inline fun <T> safeCall(action: () -> Result<T>): Result<T> {
    return try {
        action.invoke()
    } catch (e: Exception) {
        Result.failure(e)
    }
}

fun PointListResponse.toPoints() = points.map { it.toPoint() }
fun PointResponse.toPoint() = Point(x = x, y = y)
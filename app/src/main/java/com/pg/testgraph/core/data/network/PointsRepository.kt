package com.pg.testgraph.core.data.network

import android.content.Context
import com.pg.testgraph.core.domain.Point
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.io.File

class PointsRepository(
    private val apiService: ApiService,
    private val dispatcher: CoroutineDispatcher
) {
    var points: List<Point> = emptyList()

    suspend fun fetchPoints(count: Int): Result<List<Point>> = withContext(dispatcher) {
        safeCall { Result.success(apiService.getPoints(count)) }
    }.mapCatching { pointsListResponse ->
        points = pointsListResponse.toPoints().sortedBy { it.x }
        points
    }

    fun rememberGeneratedPoints(points: List<Point>) {
        this.points = points.sortedBy { it.x }
    }

    fun savePointsToFile(context: Context, resultAction: (SaveFileResult) -> Unit) {
        try {
            val file = File(context.filesDir, "points.txt")
            file.printWriter().use { out ->
                points.forEach { out.println("${it.x}, ${it.y}") }
            }
            resultAction(SaveFileResult.Success)
        } catch (e: Exception) {
            resultAction(SaveFileResult.Failure)
        }
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
package com.pg.testgraph.core.data.network

data class PointListResponse(val points: List<PointResponse>)
data class PointResponse(val x: Double, val y: Double)
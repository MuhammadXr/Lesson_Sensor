package uz.xsoft.lessonsensor_xr

interface Repository {
    fun loadMap()

    fun getMapByLevel(level: Int): Array<Array<Int>>
}
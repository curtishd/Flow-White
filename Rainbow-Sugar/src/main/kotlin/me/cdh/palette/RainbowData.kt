package me.cdh.palette

import java.awt.Color

class RainbowData : RainbowPaletteData {
    private val listColors = ArrayList<Color?>()

    init {
        init()
    }

    private fun init() {
        listColors.add(Color(239, 68, 68)) // red-500
        listColors.add(Color(249, 115, 22)) // orange-500
        listColors.add(Color(234, 179, 8)) // yellow-500
        listColors.add(Color(132, 204, 22)) // lime-500
        listColors.add(Color(34, 197, 94)) // green-500
        listColors.add(Color(16, 185, 129)) // emerald-500
        listColors.add(Color(20, 184, 166)) // teal-500
        listColors.add(Color(6, 182, 212)) // cyan-500
        listColors.add(Color(14, 165, 233)) // sky-500
        listColors.add(Color(59, 130, 246)) // blue-500
        listColors.add(Color(99, 102, 241)) // indigo-500
        listColors.add(Color(139, 92, 246)) // violet-500
        listColors.add(Color(168, 85, 247)) // purple-500
        listColors.add(Color(217, 70, 239)) // fuchsia-500
        listColors.add(Color(236, 72, 153)) // pink-500
        listColors.add(Color(244, 63, 94)) // rose-500
        listColors.add(Color(251, 146, 60)) // amber-500
        listColors.add(Color(202, 138, 4)) // yellow-600
    }

    override fun size(): Int {
        return listColors.size
    }

    override fun get(index: Int): Color? {
        return listColors[index]
    }
}

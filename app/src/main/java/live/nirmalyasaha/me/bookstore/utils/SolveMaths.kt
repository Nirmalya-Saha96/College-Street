package live.nirmalyasaha.me.bookstore.utils

import android.util.Log

object SolveMaths {

    fun solveMathBot(equation: String) : Int{

        val updatedEquation = equation.replace(" ", "")

        return when {
            updatedEquation.contains("+") -> {
                val split = updatedEquation.split("+")
                val resultSum = split[0].toInt() + split[1].toInt()
                resultSum
            }
            updatedEquation.contains("-") -> {
                val split = updatedEquation.split("-")
                val resultDiff = split[0].toInt() - split[1].toInt()
                resultDiff
            }
            updatedEquation.contains("*") -> {
                val split = updatedEquation.split("*")
                val resultMul = split[0].toInt() * split[1].toInt()
                resultMul
            }
            updatedEquation.contains("/") -> {
                val split = updatedEquation.split("/")
                val resultDiv = split[0].toInt() / split[1].toInt()
                resultDiv
            }
            else -> {
                0
            }
        }
    }
}
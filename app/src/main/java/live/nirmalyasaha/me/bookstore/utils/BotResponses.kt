package live.nirmalyasaha.me.bookstore.utils

import java.sql.Date
import java.sql.Timestamp
import java.text.SimpleDateFormat

object BotResponse {

    fun basicResponses(_message: String): String {

        val random = (0..2).random()
        val message =_message.toLowerCase()

        return when {

            //Flips a coin
            message.contains("flip") && message.contains("coin") -> {
                val r = (0..1).random()
                val result = if (r == 0) "heads" else "tails"

                "$result is the call"
            }

            //Math calculations
            message.contains("solve") -> {
                val equation: String? = message.substringAfterLast("solve")
                return try {
                    val answer = SolveMaths.solveMathBot(equation ?: "0")
                    "$answer"

                } catch (e: Exception) {
                    "Sorry, I can't solve that. \n trained to solve only basic problems"
                }
            }

            //Hello
            message.contains("hello") -> {
                when (random) {
                    0 -> "Hello!"
                    1 -> "Hii"
                    2 -> "Buongiorno!"
                    else -> "error" }
            }

            //How are you?
            message.contains("how are you") -> {
                when (random) {
                    0 -> "Great!!"
                    1 -> "At your service"
                    2 -> "Fine"
                    else -> "error"
                }
            }

            message.contains("whatsup")-> {
                when (random) {
                    0 -> "Great!!"
                    1 -> "At your service"
                    2 -> "Fine"
                    else -> "error"
                }
            }

            message.contains("hi")-> {
                when (random) {
                    0 -> "Great!!"
                    1 -> "At your service"
                    2 -> "Fine"
                    else -> "error"
                }
            }

            message.contains("good")->{
                val equation: String? = message.substringAfterLast("good")
                "Good ${equation}"
            }

            message.contains("bye")->{
                when (random){
                    0-> "GoodBye!!"
                    1 -> "See you later!!"
                    2 -> "Will be eager to see you in some time!!"
                    else -> "error"
                }
            }

            message.contains("author") && message.contains("app")->{
                when (random){
                    0 -> Constants.LINKEDIN
                    1 -> Constants.LINKEDIN
                    2 -> Constants.PORTFOLIO
                    else -> "error"
                }
            }

            message.contains("creator") && message.contains("app")->{
                when (random){
                    0 -> Constants.LINKEDIN
                    1 -> Constants.PORTFOLIO
                    2 -> Constants.PORTFOLIO
                    else -> "error"
                }
            }

            message.contains("owner") && message.contains("app")->{
                when (random){
                    0 -> Constants.LINKEDIN
                    1 -> Constants.PORTFOLIO
                    2 -> Constants.PORTFOLIO
                    else -> "error"
                }
            }

            message.contains("what you can do")->{
                when (random){
                    0 -> "I am trained to communicate to you on behalf of the creator"
                    1 -> "I can tell you time, joke, quote"
                    2 -> "I can search anything, open google, calculate your amount, and quit"
                    else -> "error"
                }
            }

            message.contains("features") && message.contains("yours")->{
                when (random){
                    0 -> "I am trained to communicate to you on behalf of the creator"
                    1 -> "I can tell you time, joke, quote"
                    2 -> "I can search anything, open google, calculate your amount, and quit"
                    else -> "error"
                }
            }

            message.contains("tell") && message.contains("joke")->{
                when (random){
                    0 -> "Q. How did the programmer die in the shower? \n" +
                            "A. He read the shampoo bottle instructions: Lather. Rinse. Repeat."
                    1 -> "Q: Do you know why Facebook went public? \n" +
                            "A: They couldn’t figure out the privacy settings!"
                    2 -> " Database Admins walked into a NoSQL bar. \n A little while later they walked out because they couldn’t find a table"
                    else -> "error"
                }
            }

            message.contains("tell") && message.contains("quote")->{
                when (random){
                    0 -> "You are the master of your own destiny \n You influence direct and control your own environment \n You can make your own life what you want to be"
                    1 -> "We are what we are because of the vibrations of thought what we pick up & register through the stimuli of daily environment"
                    2 -> "Success requires no appologies \n Failure permits no alibis"
                    else -> "error"
                }
            }

            message.contains("app") && message.contains("about")->{
                when (random){
                    0 -> "B-2-B market place for 2nd hand books & \n review/blog story of books, with secure environment \n supproted by Cryptography and Machine Learning"
                    1 -> "B-2-B market place for 2nd hand books & \n" +
                            " review/blog story of books, with secure environment \n supported by Cryptography and Machine Learning"
                    2 -> "B-2-B market place for 2nd hand books & \n" +
                            " review/blog story of books, with secure environment \n supproted by Cryptography and Machine learning"
                    else -> "error"
                }
            }

            message.contains("features") && message.contains("app")->{
                when (random){
                    0 -> "AES End-To-End Encrypted chat messaging and \n Google Maps to show encrypted address in maps"
                    1 -> "Add, Update, Delete any books, Review/Blogs \n with wishlist, cart, review/comments likes/unlike & share features"
                    2 -> "Searching features with ML Image-Text Processing, \n settings for managing encrypted address \n cancel order & status update of delivery"
                    else -> "error"
                }
            }

            message.contains("technologies") && message.contains("used")->{
                when (random){
                    0 -> "Kotlin, XML, Firebase"
                    1 -> "AES Cryptography technology \n ML Image-Text Processing"
                    2 -> "Google Maps Api & SQLite Database"
                    else -> "error"
                }
            }

            message.contains("great") && message.contains("app")->{
                when (random){
                    0 -> "Thankyou!!"
                    1 -> "Thanks for your reviews"
                    2 -> "May you have a great user experience"
                    else -> "error"
                }
            }

            message.contains("nice") && message.contains("app")->{
                when (random){
                    0 -> "Thankyou!!"
                    1 -> "Thanks for your reviews"
                    2 -> "May you have a great user experience"
                    else -> "error"
                }
            }

            message.contains("my name is")->{
                val equation: String? = message.substringAfterLast("is")
                "Charm to meet you ${equation}"
            }

            message.contains("i am")->{
                val equation: String? = message.substringAfterLast("am")
                "Charm to meet you ${equation}"
            }

            message.contains("quit")->{
                Constants.QUIT
            }

            //What time is it?
            message.contains("time") && message.contains("?")-> {
                val timeStamp = Timestamp(System.currentTimeMillis())
                val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm")
                val date = sdf.format(Date(timeStamp.time))

                date.toString()
            }

            //Open Google
            message.contains("open") && message.contains("google")-> {
                Constants.OPEN_GOOGLE
            }

            //Search on the internet
            message.contains("search")-> {
                Constants.OPEN_SEARCH
            }

            //When the programme doesn't understand...
            else -> {
                when (random) {
                    0 -> "I don't understand..."
                    1 -> "Sorry!! I am not trained for that"
                    2 -> "Anything you want to know about the app/creator"
                    else -> "error"
                }
            }
        }
    }
}
package com.joseandres.tinyprice

import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class OffersRepository {
    fun searchOffers(query: String, anonKey: String): List<Offer> {
        val url = URL("https://zqxiqljfklhwjenwqgyt.supabase.co/functions/v1/search-offers")
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "POST"
        connection.setRequestProperty("Content-Type", "application/json")
        connection.setRequestProperty("Authorization", "Bearer $anonKey")
        connection.doOutput = true

        val jsonInputString = """{"query": "$query"}"""
        connection.outputStream.use { os ->
            val input = jsonInputString.toByteArray(Charsets.UTF_8)
            os.write(input, 0, input.size)
        }

        if (connection.responseCode == HttpURLConnection.HTTP_OK) {
            val response = connection.inputStream.bufferedReader().use { it.readText() }
            val jsonObject = JSONObject(response)
            val offersArray = jsonObject.getJSONArray("offers")

            val result = mutableListOf<Offer>()
            for (i in 0 until offersArray.length()) {
                val item = offersArray.getJSONObject(i)

                // Extraer tags si existen
                val tagsArray = item.optJSONArray("tags")
                val tagsList = mutableListOf<String>()
                if (tagsArray != null) {
                    for (j in 0 until tagsArray.length()) {
                        tagsList.add(tagsArray.getString(j))
                    }
                }

                result.add(
                    Offer(
                        id = item.getString("id"),
                        store = item.getString("store"),
                        productName = item.getString("productName"),
                        price = item.getString("price"),
                        sourceUrl = item.getString("sourceUrl"),
                        summary = item.getString("summary"),
                        tags = tagsList
                    )
                )
            }
            return result
        } else {
            throw Exception("Error de red: ${connection.responseCode}")
        }
    }
}
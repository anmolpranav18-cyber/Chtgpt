package com.example.data.local

import androidx.room.TypeConverter
import com.example.data.model.Citation
import com.example.data.model.CanvasComment
import com.example.data.model.Collaborator
import com.example.data.model.DotRunLog
import com.example.data.model.SpaceActivity
import org.json.JSONArray
import org.json.JSONObject

class ChatGptTypeConverters {

    @TypeConverter
    fun fromStringList(list: List<String>?): String {
        if (list == null) return "[]"
        val arr = JSONArray()
        list.forEach { arr.put(it) }
        return arr.toString()
    }

    @TypeConverter
    fun toStringList(json: String?): List<String> {
        if (json.isNullOrEmpty()) return emptyList()
        val result = mutableListOf<String>()
        try {
            val arr = JSONArray(json)
            for (i in 0 until arr.length()) {
                result.add(arr.getString(i))
            }
        } catch (_: Exception) {}
        return result
    }

    @TypeConverter
    fun fromCitationList(list: List<Citation>?): String {
        if (list == null) return "[]"
        val arr = JSONArray()
        list.forEach {
            val obj = JSONObject()
            obj.put("title", it.title)
            obj.put("url", it.url)
            obj.put("snippet", it.snippet)
            arr.put(obj)
        }
        return arr.toString()
    }

    @TypeConverter
    fun toCitationList(json: String?): List<Citation> {
        if (json.isNullOrEmpty()) return emptyList()
        val result = mutableListOf<Citation>()
        try {
            val arr = JSONArray(json)
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                result.add(
                    Citation(
                        title = obj.optString("title"),
                        url = obj.optString("url"),
                        snippet = obj.optString("snippet")
                    )
                )
            }
        } catch (_: Exception) {}
        return result
    }

    @TypeConverter
    fun fromCanvasCommentList(list: List<CanvasComment>?): String {
        if (list == null) return "[]"
        val arr = JSONArray()
        list.forEach {
            val obj = JSONObject()
            obj.put("id", it.id)
            obj.put("lineNumber", it.lineNumber)
            obj.put("author", it.author)
            obj.put("text", it.text)
            obj.put("timestamp", it.timestamp)
            arr.put(obj)
        }
        return arr.toString()
    }

    @TypeConverter
    fun toCanvasCommentList(json: String?): List<CanvasComment> {
        if (json.isNullOrEmpty()) return emptyList()
        val result = mutableListOf<CanvasComment>()
        try {
            val arr = JSONArray(json)
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                result.add(
                    CanvasComment(
                        id = obj.optString("id"),
                        lineNumber = obj.optInt("lineNumber"),
                        author = obj.optString("author"),
                        text = obj.optString("text"),
                        timestamp = obj.optLong("timestamp")
                    )
                )
            }
        } catch (_: Exception) {}
        return result
    }

    @TypeConverter
    fun fromCollaboratorList(list: List<Collaborator>?): String {
        if (list == null) return "[]"
        val arr = JSONArray()
        list.forEach {
            val obj = JSONObject()
            obj.put("id", it.id)
            obj.put("name", it.name)
            obj.put("role", it.role)
            obj.put("isAi", it.isAi)
            arr.put(obj)
        }
        return arr.toString()
    }

    @TypeConverter
    fun toCollaboratorList(json: String?): List<Collaborator> {
        if (json.isNullOrEmpty()) return emptyList()
        val result = mutableListOf<Collaborator>()
        try {
            val arr = JSONArray(json)
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                result.add(
                    Collaborator(
                        id = obj.optString("id"),
                        name = obj.optString("name"),
                        role = obj.optString("role"),
                        isAi = obj.optBoolean("isAi")
                    )
                )
            }
        } catch (_: Exception) {}
        return result
    }

    @TypeConverter
    fun fromSpaceActivityList(list: List<SpaceActivity>?): String {
        if (list == null) return "[]"
        val arr = JSONArray()
        list.forEach {
            val obj = JSONObject()
            obj.put("id", it.id)
            obj.put("authorName", it.authorName)
            obj.put("isAi", it.isAi)
            obj.put("actionText", it.actionText)
            obj.put("timestamp", it.timestamp)
            arr.put(obj)
        }
        return arr.toString()
    }

    @TypeConverter
    fun toSpaceActivityList(json: String?): List<SpaceActivity> {
        if (json.isNullOrEmpty()) return emptyList()
        val result = mutableListOf<SpaceActivity>()
        try {
            val arr = JSONArray(json)
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                result.add(
                    SpaceActivity(
                        id = obj.optString("id"),
                        authorName = obj.optString("authorName"),
                        isAi = obj.optBoolean("isAi"),
                        actionText = obj.optString("actionText"),
                        timestamp = obj.optLong("timestamp")
                    )
                )
            }
        } catch (_: Exception) {}
        return result
    }

    @TypeConverter
    fun fromDotRunLogList(list: List<DotRunLog>?): String {
        if (list == null) return "[]"
        val arr = JSONArray()
        list.forEach {
            val obj = JSONObject()
            obj.put("id", it.id)
            obj.put("executedAt", it.executedAt)
            obj.put("status", it.status)
            obj.put("summary", it.summary)
            val stepArr = JSONArray()
            it.stepsCompleted.forEach { step -> stepArr.put(step) }
            obj.put("stepsCompleted", stepArr)
            arr.put(obj)
        }
        return arr.toString()
    }

    @TypeConverter
    fun toDotRunLogList(json: String?): List<DotRunLog> {
        if (json.isNullOrEmpty()) return emptyList()
        val result = mutableListOf<DotRunLog>()
        try {
            val arr = JSONArray(json)
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                val steps = mutableListOf<String>()
                val stepArr = obj.optJSONArray("stepsCompleted")
                if (stepArr != null) {
                    for (j in 0 until stepArr.length()) {
                        steps.add(stepArr.getString(j))
                    }
                }
                result.add(
                    DotRunLog(
                        id = obj.optString("id"),
                        executedAt = obj.optLong("executedAt"),
                        status = obj.optString("status"),
                        summary = obj.optString("summary"),
                        stepsCompleted = steps
                    )
                )
            }
        } catch (_: Exception) {}
        return result
    }
}

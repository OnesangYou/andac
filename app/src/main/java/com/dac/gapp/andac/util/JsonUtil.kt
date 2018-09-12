package com.dac.gapp.andac.util

import org.json.JSONArray
import org.json.JSONObject

class JsonUtil {
    companion object {
        fun getInt(jsonObject: JSONObject, name: String): Int {
            return if (jsonObject.has(name)) jsonObject.getInt(name) else 0
        }

        fun getDouble(jsonObject: JSONObject, name: String): Double {
            return if (jsonObject.has(name)) jsonObject.getDouble(name) else 0.0
        }

        fun getString(jsonObject: JSONObject, name: String): String {
            return if (jsonObject.has(name)) jsonObject.getString(name) else ""
        }

        fun getBoolean(jsonObject: JSONObject, name: String): Boolean {
            return if (jsonObject.has(name)) jsonObject.getBoolean(name) else false
        }

        fun getObject(jsonObject: JSONObject, name: String): JSONObject {
            return if (jsonObject.has(name)) jsonObject.getJSONObject(name) else JSONObject()
        }

        fun getArray(jsonObject: JSONObject, name: String): JSONArray {
            return if (jsonObject.has(name)) jsonObject.getJSONArray(name) else JSONArray()
        }
    }
}
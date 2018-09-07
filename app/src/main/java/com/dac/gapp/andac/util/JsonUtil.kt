package com.dac.gapp.andac.util

import org.json.JSONObject

class JsonUtil {
    companion object {
        fun <T> getData(jsonObject: JSONObject, name: String): T {
            return if (jsonObject.has(name)) jsonObject.get(name) as T else Any() as T
        }

        fun getObject(jsonObject: JSONObject, name: String): JSONObject {
            return if (jsonObject.has(name)) jsonObject.getJSONObject(name) else JSONObject()
        }
    }
}
package com.codermert.yapayzeka

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.RetryPolicy
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.textfield.TextInputEditText
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    lateinit var responseTV: TextView
    lateinit var questionTV: TextView
    lateinit var queryEdt: TextInputEditText

    var url = "https://api.openai.com/v1/completions"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        responseTV = findViewById(R.id.idTVResponse)
        questionTV = findViewById(R.id.idTVQuestion)
        queryEdt = findViewById(R.id.idEdtQuery)



        queryEdt.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {

                responseTV.text = "Lütfen bekleyin sorguluyorum...\n\n\n\n\n\n\nTelegram @codermert"

                if (queryEdt.text.toString().length > 0) {
                    getResponse(queryEdt.text.toString())
                } else {
                    Toast.makeText(this, "Lütfen sorgunuzu girin...", Toast.LENGTH_SHORT).show()
                    responseTV.text = ""
                }
                return@OnEditorActionListener true
            }
            false
        })
    }

    private fun getResponse(query: String) {

        questionTV.text = query
        queryEdt.setText("")


        val queue: RequestQueue = Volley.newRequestQueue(applicationContext)

        val jsonObject: JSONObject? = JSONObject()

        jsonObject?.put("model", "text-davinci-003")
        jsonObject?.put("prompt", query)
        jsonObject?.put("temperature", 0)
        jsonObject?.put("max_tokens", 100)
        jsonObject?.put("top_p", 1)
        jsonObject?.put("frequency_penalty", 0.0)
        jsonObject?.put("presence_penalty", 0.0)


        val postRequest: JsonObjectRequest =

            object : JsonObjectRequest(Method.POST, url, jsonObject,
                Response.Listener { response ->

                    val responseMsg: String =
                        response.getJSONArray("choices").getJSONObject(0).getString("text")
                    responseTV.text = responseMsg
                },

                Response.ErrorListener { error ->
                    Log.e("TAGAPI", "Error is : " + error.message + "\n" + error)
                }) {
                override fun getHeaders(): kotlin.collections.MutableMap<kotlin.String, kotlin.String> {
                    val params: MutableMap<String, String> = HashMap()

                    params["Content-Type"] = "application/json"
                    params["Authorization"] =
                        "Bearer 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"
                    return params;
                }
            }


        postRequest.setRetryPolicy(object : RetryPolicy {
            override fun getCurrentTimeout(): Int {
                return 50000
            }

            override fun getCurrentRetryCount(): Int {
                return 50000
            }

            @Throws(VolleyError::class)
            override fun retry(error: VolleyError) {
            }
        })

        queue.add(postRequest)
    }
}

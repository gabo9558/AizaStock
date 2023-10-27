package com.quantum.aizastock.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.quantum.aizastock.R
import com.quantum.aizastock.common.PreferenceUtils
import com.quantum.aizastock.databinding.DescriptionFragmentBinding
import com.quantum.aizastock.remote.Urls
import com.quantum.aizastock.remote.response.DataConsultResponse
import com.quantum.aizastock.remote.response.ErrorResponse
import org.json.JSONObject

class DescriptionAddFragment: Fragment() {

    private var _binding: DescriptionFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var bundle: Bundle

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DescriptionFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Functions
        arguments?.let { bundle = it }

        setListeners()
        initData()
    }

    private fun initData(){
        val map: HashMap<String, String> = HashMap()
        map["IMEI"] = bundle.getString("IMEI", "")
        consultItem(map)
    }

    private fun setListeners(){

        binding.btnFinish.setOnClickListener {

            val map: HashMap<String, String> = HashMap()
            map["IMEI"] = bundle.getString("IMEI", "")
            map["description"] = binding.etDescription.text.toString()
            map["user_id"] = PreferenceUtils.getUserID(requireContext()) ?: ""
            map["group_id"] = PreferenceUtils.getCurrentID(requireContext()).toString()

            addItem(map)

        }
    }

    private fun addItem(map: HashMap<String, String>){
        val queue = Volley.newRequestQueue(requireContext())
        val url = Urls.URL_BASE + Urls.CHECK_ITEM

        val request = JsonObjectRequest(
            Request.Method.POST, url,
            (map as Map<*, *>?)?.let { JSONObject(it) },
            { response ->

                try {
                    if (response.get("status").equals("ok")){

                        var currentAmount = PreferenceUtils.getCurrentAmount(requireContext())
                        currentAmount += 1
                        PreferenceUtils.saveCurrentAmount(requireContext(), currentAmount)

                        view?.let {
                            Navigation.findNavController(it).navigate(R.id.description_add_to_work)
                        }

                    }else if (response.get("status") == "error"){
                        val error = Gson().fromJson(response.toString(), ErrorResponse::class.java)
                        println("Error: ${error.message}")
                        Toast.makeText(requireContext(), "Error: ${error.message}", Toast.LENGTH_LONG).show()

                        if (error.message == "IMEI ya fue validado"){
                            view?.let { Navigation.findNavController(it).navigate(R.id.description_add_to_work) }
                        }
                    }
                }catch (e: Exception){
                    println("exception: $e")
                }
            },
            { error ->
                println("Error listener ADD ITEM: $error")
            }
        )

        queue.add(request)
    }

    private fun consultItem(map: HashMap<String, String>){
        val queue = Volley.newRequestQueue(requireContext())
        val url = Urls.URL_BASE + Urls.CONSULT_IMEI

        val request = JsonObjectRequest(
            Request.Method.POST, url,
            (map as Map<*, *>?)?.let { JSONObject(it) },
            { response ->

                try {
                    if (response.get("status").equals("ok")){

                        val gItem = Gson().fromJson(response.toString(), DataConsultResponse::class.java)

                        with(binding){
                            etDescription.setText(gItem.data.description)
                        }

                    }else if (response.get("status") == "error"){
                        val error = Gson().fromJson(response.toString(), ErrorResponse::class.java)
                        println("Error: ${error.message}")
                        Toast.makeText(requireContext(), "Error: ${error.message}", Toast.LENGTH_LONG).show()

                        if (error.message == "El item no existe"){
                            with(binding){
                                tvSubtitleDescription.setText(R.string.subtitle_new_item)
                                tvBodyDescription.setText(R.string.body_new_item)
                            }
                        }
                    }
                }catch (e: Exception){
                    println("exception: $e")
                }
            },
            { error ->
                println("Error listener CONSULT ITEM: $error")
            }
        )

        queue.add(request)
    }
}
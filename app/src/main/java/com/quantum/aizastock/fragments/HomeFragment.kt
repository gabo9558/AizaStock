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
import com.quantum.aizastock.databinding.HomeFragmentBinding
import com.quantum.aizastock.remote.Urls
import com.quantum.aizastock.remote.response.ErrorResponse
import com.quantum.aizastock.remote.response.StockResponse
import org.json.JSONObject

class HomeFragment: Fragment() {

    private var _binding: HomeFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = HomeFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Functions
        setStockService()
        setListeners()
        validateUserForDelete(binding)

        binding.btnRegreshStats.visibility = View.VISIBLE
        binding.pbHome.visibility = View.GONE
    }

    private fun setListeners(){
        with(binding){

            view?.let { xview ->

                btnLogout.setOnClickListener {
                    val map: HashMap<String, String> = HashMap()
                    map["email"] = PreferenceUtils.getEmail(requireContext()).toString()
                    logout(map)
                }

                btnAddItem.setOnClickListener {
                    Navigation.findNavController(xview).navigate(R.id.home_to_create_work)
                }

                btnRegreshStats.setOnClickListener {
                    setStockService()
                }

            }
        }
    }

    private fun setStockService(){

        binding.btnRegreshStats.visibility = View.GONE
        binding.pbHome.visibility = View.VISIBLE

        val queue = Volley.newRequestQueue(requireContext())
        val url = Urls.URL_BASE + Urls.GET_STOCK

        val request = JsonObjectRequest(
            Request.Method.GET, url,
            null,
            { response ->

                try {
                    if (response.get("status").equals("ok")){
                        val stock = Gson().fromJson(response.toString(), StockResponse::class.java)

                        val totalStock = "${resources.getString(R.string.total_stock_home)} ${stock.data.total_stock}"
                        val totalCheck = "${resources.getString(R.string.total_check)} ${stock.data.total_check}"
                        val totalFaltan = "${resources.getString(R.string.total_faltantes)} ${stock.data.total_faltantes}"
                        val totalNews = "${resources.getString(R.string.total_new)} ${stock.data.total_news}"

                        binding.tvTotalStock.text = totalStock
                        binding.tvTotalCheckin.text = totalCheck
                        binding.tvTotalFaltantes.text = totalFaltan
                        binding.tvTotalNews.text = totalNews

                        binding.btnRegreshStats.visibility = View.VISIBLE
                        binding.pbHome.visibility = View.GONE

                    }else if (response.get("status") == "error"){

                        binding.btnRegreshStats.visibility = View.VISIBLE
                        binding.pbHome.visibility = View.GONE

                        val error = Gson().fromJson(response.toString(), ErrorResponse::class.java)
                        println("Error: ${error.message}")
                        Toast.makeText(requireContext(), "Error: ${error.message}", Toast.LENGTH_LONG).show()
                    }
                }catch (e: Exception){
                    println("exception: $e")
                }
            },
            { error ->
                println("Error listener get stock: $error")
            }
        )

        queue.add(request)
    }

    private fun logout(map: HashMap<String, String>){
        val queue = Volley.newRequestQueue(requireContext())
        val url = Urls.URL_BASE + Urls.LOGOUT

        val request = JsonObjectRequest(
            Request.Method.POST, url,
            (map as Map<*, *>?)?.let { JSONObject(it) },
            { response ->

                try {
                    if (response.get("status").equals("ok")){
                        PreferenceUtils.deleteAll(requireContext())
                        PreferenceUtils.saveSession(requireContext(), "0")
                        view?.let {
                            Navigation.findNavController(it).navigate(R.id.home_to_login)
                        }
                    }else if (response.get("status") == "error"){
                        val error = Gson().fromJson(response.toString(), ErrorResponse::class.java)
                        println("Error: ${error.message}")
                        Toast.makeText(requireContext(), "Error: ${error.message}", Toast.LENGTH_LONG).show()
                    }
                }catch (e: Exception){
                    println("exception: $e")
                }
            },
            { error ->
                println("Error listener logout: $error")
            }
        )

        queue.add(request)
    }

    private fun validateUserForDelete(binding: HomeFragmentBinding){
        if (PreferenceUtils.getEmail(requireContext()).equals("ernesto@correo.com")){
            binding.btnDeleteTable.visibility = View.VISIBLE
        }
    }
}
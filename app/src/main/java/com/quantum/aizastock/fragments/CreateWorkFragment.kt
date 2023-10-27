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
import com.quantum.aizastock.databinding.CreateWorkFragmentBinding
import com.quantum.aizastock.remote.Urls
import com.quantum.aizastock.remote.response.ErrorResponse
import com.quantum.aizastock.remote.response.GroupResponse
import org.json.JSONObject

class CreateWorkFragment: Fragment() {

    private var _binding: CreateWorkFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = CreateWorkFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Functions
        setListeners()
    }

    private fun setListeners(){
        with(binding){
            view?.let {
                btnCreateGroup.setOnClickListener {

                    val map: HashMap<String, String> = HashMap()
                    map["name"] = tietGroupName.text.toString()
                    map["amount"] = tietGroupAmount.text.toString()
                    map["user_id"] = PreferenceUtils.getUserID(requireContext()) ?: ""
                    createGroup(map)
                }
            }
        }
    }

    private fun createGroup(map: HashMap<String, String>){
        val queue = Volley.newRequestQueue(requireContext())
        val url = Urls.URL_BASE + Urls.CREATE_GROUP

        val request = JsonObjectRequest(
            Request.Method.POST, url,
            (map as Map<*, *>?)?.let { JSONObject(it) },
            { response ->

                try {
                    if (response.get("status").equals("ok")){

                        val gGroup = Gson().fromJson(response.toString(), GroupResponse::class.java)

                        val bundle = Bundle()
                        bundle.apply {
                            putInt("id_group", gGroup.data.id)
                            putString("id_user", gGroup.data.id_user)
                            putString("group_name", gGroup.data.name)
                            putInt("group_amount", gGroup.data.amount)
                        }

                        view?.let {
                            Navigation.findNavController(it).navigate(R.id.create_work_to_work, bundle)
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
                println("Error listener create group: $error")
            }
        )

        queue.add(request)
    }
}
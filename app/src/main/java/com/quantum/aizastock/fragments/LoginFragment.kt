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
import com.quantum.aizastock.databinding.LoginFragmentBinding
import com.quantum.aizastock.remote.Urls
import com.quantum.aizastock.remote.response.ErrorResponse
import com.quantum.aizastock.remote.response.LoginResponse
import org.json.JSONObject

class LoginFragment: Fragment() {

    private var _binding: LoginFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = LoginFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Functions
        validateSession()
        setListeners()
    }

    private fun validateSession(){
        if (PreferenceUtils.getSession(requireContext()) == "1"){
            view?.let { Navigation.findNavController(it).navigate(R.id.login_to_home) }
        }
    }

    private fun setListeners(){
        with(binding){
            btnLogin.setOnClickListener {

                val map: HashMap<String, String> = HashMap<String, String>()
                map["email"] = tietEmail.text.toString()
                map["password"] = tietPassword.text.toString()

                loginService(map)
            }
        }
    }

    private fun loginService(map: HashMap<String, String>){
        val queue = Volley.newRequestQueue(requireContext())
        val url = Urls.URL_BASE + Urls.LOGIN

        val request = JsonObjectRequest(
            Request.Method.POST, url,
            (map as Map<*, *>?)?.let { JSONObject(it) },
            { response ->

                try {
                    if (response.get("status").equals("ok")){
                        val gUser = Gson().fromJson(response.toString(), LoginResponse::class.java)
                        PreferenceUtils.saveUserID(requireContext(), gUser.user.id.toString())
                        PreferenceUtils.saveEmail(requireContext(), gUser.user.email)
                        PreferenceUtils.saveSession(requireContext(), "1")
                        PreferenceUtils.saveUserRole(requireContext(), gUser.user.role)
                        view?.let {
                            Navigation.findNavController(it).navigate(R.id.login_to_home)
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
                println("Error listener login: $error")
            }
        )

        queue.add(request)
    }
}